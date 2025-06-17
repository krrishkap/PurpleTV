import json
import subprocess
import zipfile
from datetime import datetime
from logging import getLogger
from pathlib import Path

from pyaxmlparser import APK

from src.model.data import Env
from src.task.base import BaseTask, BaseTaskException
from src.util import copyfile, rm, copytree

logger = getLogger(__name__)


class InternalException(BaseTaskException):
    pass


class Cleanup(BaseTask):
    __TASK_NAME__ = "PREPARE_DECOMPILE"

    def run(self, env: Env):
        apk_dir = self.ctx().apk_dir
        if not apk_dir.exists():
            return

        if apk_dir.is_file():
            raise InternalException(f"Can't decompile apk: `{apk_dir}` is file")

        rm(apk_dir)


class BuildAppDex(BaseTask):
    """
    Компилирует кастомный DEX файл из исходного кода приложения.

    Запускает Gradle задачу для компиляции Java/Kotlin кода в DEX файл,
    который будет внедрен в APK.
    """
    __TASK_NAME__ = "BUILD_DEX_APP"

    def run(self, env: Env):
        command = [env.app_dir.joinpath("gradlew.bat").as_posix(), "genDex"]
        logger.debug({'command': command})
        subprocess.run(command, cwd=env.app_dir.as_posix())


class InjectRes(BaseTask):
    """
    Внедряет кастомные ресурсы в декомпилированный APK.

    Копирует ресурсы из директории app/monolith/src/main/res в директорию
    декомпилированного APK, заменяя или добавляя новые ресурсы.
    """
    __TASK_NAME__ = "INJECT_APP_RES"

    def run(self, env: Env):
        ctx = self.ctx()
        copytree(env.app_dir.joinpath("monolith/src/main/res"), ctx.apk_dir.joinpath("res"))


class InjectDonations(BaseTask):
    """
    Внедряет файл с информацией о донатах в APK.

    Копирует файл donations.txt в директорию assets декомпилированного APK.
    """
    __TASK_NAME__ = "INJECT_DONATIONS"
    IN_APP_PATH = "assets/donations.txt"

    def run(self, env: Env):
        ctx = self.ctx()
        copyfile(env.donations_file, ctx.apk_dir.joinpath(self.IN_APP_PATH))


class X64(BaseTask):
    """
    Удаляет библиотеки для архитектур, отличных от x64.

    Удаляет директории с нативными библиотеками для архитектур armeabi-v7a, x86 и x86_64,
    оставляя только библиотеки для x64 для уменьшения размера APK.
    """
    __TASK_NAME__ = "KEEP_X64_LIB"

    @staticmethod
    def get_dirs():
        return ["lib/armeabi-v7a", "lib/x86", "lib/x86_64"]

    def run(self, env: Env):
        for d in self.get_dirs():
            rm(self.ctx().apk_dir.joinpath(d))


class CopySo(BaseTask):
    """
    Копирует нативные библиотеки (.so) в декомпилированный APK.

    Копирует нативные библиотеки из директории lib/so в соответствующие
    директории декомпилированного APK.
    """
    __TASK_NAME__ = "INJECT_EXT_SO_LIB"

    def run(self, env: Env):
        copytree(env.lib_dir.joinpath("so"), self.ctx().apk_dir)


class InjectDex(BaseTask):
    """
    Внедряет скомпилированные DEX файлы в APK.

    Добавляет кастомные DEX файлы в APK, включая app.dex и другие DEX файлы
    из директории lib.
    """
    __TASK_NAME__ = "INJECT_EXT_DEX"
    APP_DEX_PATH = "dex/app.dex"

    def run(self, env: Env):
        apk = APK(self.ctx().out_apk.as_posix())
        last_index = get_last_dex_number(apk)

        writer = zipfile.ZipFile(self.ctx().out_apk.as_posix(), mode="a", compression=zipfile.ZIP_DEFLATED)

        files = [i.as_posix() for i in env.lib_dir.iterdir() if i.name.endswith(".dex")]
        files.append(env.app_dir.joinpath(self.APP_DEX_PATH))
        for path in [*files]:
            inject_dex(path, last_index, writer)
            last_index += 1

        writer.close()


class IncreaseBuildNumber(BaseTask):
    """
    Увеличивает номер сборки мода.

    Копирует файл build.json в папку assets.
    """
    __TASK_NAME__ = "UPDATE_BUILD_FILE"
    BUILD_FILENAME = "build.json"
    FILENAME_TEMPLATE = "PurpleTV_b{build_number}_{apk_code}.apk"

    def run(self, env: Env):
        ctx = self.ctx()

        if not env.build_config.exists():
            raise InternalException(f"Can't increase build number: {self.BUILD_FILENAME} not found!")

        with open(env.build_config, "r", encoding="utf-8") as fp:
            js = json.load(fp)

        js["number"] += 1
        js['timestamp'] = int(datetime.now().timestamp())

        with open(env.build_config, "w", encoding="utf-8") as fp:
            json.dump(js, fp)

        copyfile(env.build_config, ctx.apk_dir.joinpath("assets").joinpath(self.BUILD_FILENAME))
        out_filename = self.FILENAME_TEMPLATE.format(build_number=js["number"], apk_code=ctx.apk_desc.version_code)
        ctx.out_apk = Path(ctx.out_apk.parent, out_filename)


class Install(BaseTask):
    """
    Устанавливает скомпилированный APK на подключенное устройство.
    """
    __TASK_NAME__ = "INSTALL_APK"

    def run(self, env: Env):
        subprocess.run(["adb", "install", "-r", self.ctx().out_apk])


def is_app_dex_filename(name):
    if not name.startswith("classes"):
        return False

    if not name.endswith(".dex"):
        return False

    index: str = name[7:-4]

    if not index:
        return True

    if index.isdecimal():
        return True

    return False


def get_number(name: str):
    index: str = name[7:-4]

    if not index:
        return 1

    return int(index)


def get_last_dex_number(zip: APK):
    highest = 0
    for name in zip.get_files():
        if '/' in name:
            continue

        if is_app_dex_filename(name):
            current = get_number(name)
            if current > highest:
                highest = current

    return highest


def format_dex_filename(index):
    return "classes{}.dex".format(index) if index > 1 else "classes.dex"


def inject_dex(dex_path, last_index, zip):
    app_dex_name = format_dex_filename(last_index + 1)
    if dex_path is zipfile.Path:
        dex_path = dex_path.as_posix()

    print(f"Injecting {dex_path} [{app_dex_name}]...")
    last_index += 1
    zip.write(dex_path, app_dex_name)
