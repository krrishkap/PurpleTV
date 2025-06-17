import os
import shutil
import subprocess
from logging import getLogger
from pathlib import Path

import src.util
from src.config import RUN_JAVA_COMMAND
from src.model.data import Env, Context
from src.task.base import BaseTask

logger = getLogger(__name__)


class DecompileApk(BaseTask):
    """
    Декомпилирует APK файл в smali код и ресурсы.
    """
    __TASK_NAME__ = "APKTOOL_DECOMPILE"

    def __init__(self, context, verbose=False):
        super().__init__(context)
        self._verbose = verbose

    def run(self, env: Env):
        ctx = self.ctx()
        print(f"Decompiling `{ctx.apk_desc.file.as_posix()}` to `{ctx.apk_dir.as_posix()}`...")
        command = [*RUN_JAVA_COMMAND, "-jar", env.apktool_jar.as_posix(),
                   "-p", env.bin_dir.joinpath("framework").as_posix(),
                   "d", "-f", "-b", "--keep-broken-res", "--use-aapt2",
                   ctx.apk_desc.file.as_posix(), "-o", ctx.apk_dir.as_posix()]
        if self._verbose:
            command.append("-v")

        logger.debug({"ctx": ctx, 'command': command})
        subprocess.run(command, check=True)


class CloneApk(BaseTask):
    """
    Создает клон приложения с нужным названием пакета
    """
    __TASK_NAME__ = "CLONE_APK"

    ACTION_SMALI = ["tv/twitch/android/shared/pip/NativePipParamsUpdater.smali",
                    "tv/twitch/android/shared/pip/NativePictureInPicturePresenter.smali",
                    "tv/twitch/android/shared/pip/NativePictureInPicturePresenter$commandReceiver$1.smali",
                    "tv/twitch/android/shared/audio/ads/AudioAdsPresenter.smali",
                    "tv/twitch/android/feature/foreground/audio/ads/foreground/AudioAdsForegroundPresenter.smali"]
    ACTION_SMALI_FIX = ["tv/twitch/android/shared/pip/NativePictureInPicturePresenter$commandReceiver$1.smali"]
    PUSH_SMALI = ["tv/twitch/android/shared/notifications/pub/NotificationIntentAction.smali"]
    ANDROID_MANIFEST = ["AndroidManifest.xml"]

    def __init__(self, context, package_name, verbose=False):
        super().__init__(context)
        self._verbose = verbose
        self._package_name = package_name.lower()

    @staticmethod
    def _replace(mp, files, replace_pairs):
        if mp is None:
            logger.error("Empty smali_map")
            return

        if not files:
            logger.warning("Empty files")
            return

        if not replace_pairs:
            logger.warning("Empty replace_pairs")
            return

        for rel_path in files:
            full_path = mp.get(rel_path, None)
            if not full_path:
                logger.error("File `{}` not found! Skip".format(rel_path))
                continue

            for search, replace in replace_pairs:
                replace_in_binary_file(rel_path, full_path, search, replace)

    def fix_action_hash(self, smali_map: dict):
        str1 = "{}.media.action.pause_playback".format(self._package_name)
        str2 = "{}.media.action.resume_playback".format(self._package_name)
        hash_1 = "{}".format(calc_java_string_hash(str1)).encode("utf-8")
        hash_2 = "{}".format(calc_java_string_hash(str2)).encode("utf-8")

        self._replace(
            mp=smali_map,
            files=self.ACTION_SMALI_FIX,
            replace_pairs=[(b"-0xf2323c", hash_1), (b"-0x69bc8593", hash_2)]
        )

    def replace_push(self, smali_map: dict):
        org = b"tv.twitch.android.push."
        replace = "{}.push.".format(self._package_name).encode("utf-8")
        self._replace(smali_map, files=self.PUSH_SMALI, replace_pairs=[(org, replace)])

    def replace_action(self, smali_map: dict):
        org = b"tv.twitch.android.media.action."
        replace = "{}.media.action.".format(self._package_name).encode("utf-8")
        self._replace(smali_map, files=self.ACTION_SMALI, replace_pairs=[(org, replace)])

    def run(self, env: Env):
        ctx = self.ctx()
        logger.info("Generating smali map...")
        smali_map = {rel.as_posix(): file.as_posix() for rel, file in get_smali_classes(ctx)}
        self.replace_action(smali_map)
        self.fix_action_hash(smali_map)
        self.replace_push(smali_map)
        self.replace_android_manifest(ctx)

    def replace_android_manifest(self, ctx: Context):
        mp = {"AndroidManifest.xml": ctx.apk_dir.joinpath("AndroidManifest.xml")}

        replace_pairs = [(
            'package="tv.twitch.android.app"'.encode("utf-8"),
            'package="{}"'.format(self._package_name).encode("utf-8")
        ), (
            'android:name="tv.twitch.android.push.'.encode("utf-8"),
            'android:name="{}.push.'.format(self._package_name).encode("utf-8")
        ), (
            'android:name="tv.twitch.android.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"'.encode("utf-8"),
            'android:name="{}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"'.format(self._package_name).encode("utf-8")
        ), (
            'android:authorities="tv.twitch.android.app.'.encode("utf-8"),
            'android:authorities="{}.'.format(self._package_name).encode("utf-8")
        ), (
            'android:authorities="com.amazon.identity.auth.device.MapInfoProvider.tv.twitch.android.app"'.encode("utf-8"),
            'android:authorities="com.amazon.identity.auth.device.MapInfoProvider.{}"'.format(self._package_name).encode("utf-8")
        ), (
            '<data android:host="tv.twitch.android.app" android:scheme="amzn"/>'.encode("utf-8"),
            '<data android:host="{}" android:scheme="amzn"/>'.format(self._package_name).encode("utf-8")
        )]

        self._replace(
            mp=mp,
            files=self.ANDROID_MANIFEST,
            replace_pairs=replace_pairs)


class RecompileApk(BaseTask):
    """
    Компилирует модифицированный APK из декомпилированных файлов.

    Собирает все модифицированные файлы обратно в APK файл, готовый к подписи.
    Также создает копию скомпилированного APK в кэше для ускорения будущих сборок.
    """
    __TASK_NAME__ = "APKTOOL_RECOMPILE"

    def __init__(self, context, verbose=False, debuggable=False):
        super().__init__(context)
        self._verbose = verbose
        self._debuggable = debuggable

    def run(self, env: Env):
        ctx = self.ctx()
        print(f"Recompiling `{ctx.apk_dir.as_posix()}` to `{ctx.out_apk.as_posix()}`...")
        command = [*RUN_JAVA_COMMAND, "-jar", env.apktool_jar.as_posix(),
                   "-p", env.bin_dir.joinpath("framework").as_posix(), "--use-aapt2",
                   "b", ctx.apk_dir.as_posix(), "-o", ctx.out_apk.as_posix()]
        if self._verbose:
            command.append("-v")
        if self._debuggable:
            command.append("-d")
        logger.debug({"ctx": ctx, 'command': command})

        subprocess.run(command, check=True)
        shutil.copy(ctx.out_apk.as_posix(), ctx.cached_apk.as_posix())


class CachedApk(BaseTask):
    """
    Использует кэшированную версию APK вместо повторной компиляции.

    Копирует ранее скомпилированный APK из кэша, что значительно ускоряет процесс сборки
    при отсутствии изменений в ресурсах или smali коде.
    """
    __TASK_NAME__ = "APKTOOL_CACHED_APK"

    def run(self, env: Env):
        ctx = self.ctx()
        cached_apk = ctx.cached_apk
        logger.debug({"ctx": ctx, 'cached_apk': cached_apk})
        if not cached_apk.exists():
            raise FileNotFoundError("Cannot copy cached apk: file not found")

        print(f"Copying cached apk to `{ctx.out_apk.as_posix()}`")
        shutil.copy(cached_apk.as_posix(), ctx.out_apk.as_posix())


class FixDexBuilding(BaseTask):
    """
    Перемещает определенные классы между DEX файлами для сборки.

    Решает проблемы с ограничением методов в одном DEX файле, перемещая
    указанные классы в отдельный DEX файл.
    """
    __TASK_NAME__ = "APKTOOL_MOVE_CLASSES"

    @staticmethod
    def get_classes_to_move():
        return ["org/joda/time", "coil", "androidx/cardview"]

    def run(self, env: Env):
        if not self.get_classes_to_move():
            return

        ctx = self.ctx()
        last_dex_num = get_last_dex_num(ctx.apk_dir)
        md = Path(ctx.apk_dir).joinpath(f"smali_classes{last_dex_num + 1}")
        for cls in self.get_classes_to_move():
            cls_as_path = cls.replace(".", "/")
            out_md = md.joinpath(cls_as_path)
            move_classes(Path(ctx.apk_dir).joinpath(f"smali/{cls_as_path}"), out_md)
            for i in range(2, last_dex_num):
                move_classes(Path(ctx.apk_dir).joinpath(f"smali_classes{i}/{cls_as_path}"), out_md)


class FixColors(BaseTask):
    """
    Исправляет ссылки на цвета в XML файлах ресурсов.

    Заменяет ссылки на цвета Android (@android:color/) на совместимый формат (@*android:color/)
    для предотвращения ошибок компиляции.
    """
    __TASK_NAME__ = "FIX_COLORS"
    FILES = ["res/values-v31/colors.xml", "res/values-v34/colors.xml"]

    def run(self, env: Env):
        ctx = self.ctx()
        for fp in self.FILES:
            file = ctx.apk_dir.joinpath(fp)
            if file.exists():
                fix_colors(file)


class FixAnnotations(BaseTask):
    """
    Исправляет XML аннотации в файлах ресурсов.

    Заменяет экранированные теги аннотаций (&lt;annotation) на правильный формат (<annotation)
    для корректной обработки ресурсов.
    """
    __TASK_NAME__ = "FIX_ANNOTATIONS"

    def run(self, env: Env):
        for file in src.util.get_files(self.ctx().apk_dir.joinpath("res")):
            parent_name = file.parent.name
            if parent_name == 'values' or parent_name.startswith('values-'):
                if file.name == 'plurals.xml':
                    fix_annotations(file)


class FixLauncherIcon(BaseTask):
    """
    Удаляет стандартные иконки запуска для замены на кастомные.

    Удаляет файлы иконок из ресурсов mipmap, чтобы заменить их на кастомные иконки.
    """
    __TASK_NAME__ = "FIX_LAUNCHER_ICON"
    RM_FILES = ["ic_launcher_foreground.png", "ic_launcher.png"]

    def run(self, env: Env):
        for file in src.util.get_files(self.ctx().apk_dir.joinpath("res")):
            parent_name = file.parent.name
            if parent_name == 'mipmap' or parent_name.startswith('mipmap-'):
                if file.name in self.RM_FILES:
                    print(f"Removing: {file.as_posix()}")
                    file.unlink()


class UberSignApk(BaseTask):
    """
    Подписывает скомпилированный APK файл.
co
    Использует uber-apk-signer для подписи APK с указанным ключом.
    """
    __NAME__ = "UBER_SIGN_APK"

    def run(self, env: Env):
        key_path = Path(os.environ.get("PURPLE_TV_SIGN_DIR", env.bin_dir)).joinpath("sign.jks")
        key_pass = os.environ.get("PURPLE_TV_KEY_PASS", "")
        print(f"Key path: {key_path}")

        out_apk = self.ctx().out_apk
        dig = Path(out_apk.parent, out_apk.name + ".idsig")
        command = [*RUN_JAVA_COMMAND, "-jar", env.uber_jar, "--overwrite", "--verbose", "-a", out_apk.as_posix(),
                   "--ks",
                   key_path, "--ksPass", key_pass, "--ksKeyPass", key_pass, "--ksAlias", "android"]
        subprocess.run(command, check=True)
        dig.unlink()


def fix_colors(file):
    with open(file.as_posix(), 'rb') as fp:
        content = fp.read()

    if b'>@android:color/' not in content:
        return

    print(f"Fixing colors in file: {file.as_posix()}")
    with open(file.as_posix(), 'wb') as fp:
        fp.write(content.replace(b'>@android:color/', b'>@*android:color/'))


def fix_annotations(file: Path):
    with open(file.as_posix(), 'rb') as fp:
        content = fp.read()

    if b'&lt;annotation ' not in content:
        return

    print(f"Fixing annotations in file: {file.as_posix()}")
    with open(file.as_posix(), 'wb') as fp:
        fp.write(content.replace(b'&lt;annotation ', b'<annotation ').replace(b'&lt;/annotation>', b'</annotation>'))


def get_last_dex_num(path: Path):
    last = 2
    for file in path.iterdir():
        if file.is_dir() and file.name.startswith("smali_classes"):
            num = file.name[13:]
            if num.isdecimal():
                last = max(last, int(num))

    return last


def move_classes(frm: Path, to: Path):
    if not frm.exists() or not frm.is_dir():
        return

    if not to.exists() or to.is_file():
        to.mkdir(parents=True)

    print(f"Moving classes dir `{frm.as_posix()}` to `{to.as_posix()}`")
    shutil.copytree(frm.as_posix(), to.as_posix(), dirs_exist_ok=True)
    shutil.rmtree(frm.as_posix())


def remove_classes(path: Path):
    print(f"Removing: {path.as_posix()}")
    shutil.rmtree(path.as_posix())


def iter_dir(root: Path, path: Path):
    for obj in path.iterdir():
        if obj.is_dir():
            yield from iter_dir(root=root, path=obj)
        elif obj.is_file():
            yield obj.relative_to(root), obj
        else:
            pass


def get_smali_classes(ctx: Context):
    smali_dir = ctx.apk_dir.joinpath('smali')
    yield from iter_dir(root=smali_dir, path=smali_dir)

    last_dex_number = get_last_dex_num(ctx.apk_dir)
    for dex_number in range(2, last_dex_number + 1):
        smali_dir = Path(ctx.apk_dir).joinpath(f"smali_classes{dex_number}")
        yield from iter_dir(root=smali_dir, path=smali_dir)


def calc_java_string_hash(s):
    h = 0
    for c in s:
        h = int((((31 * h + ord(c)) ^ 0x80000000) & 0xFFFFFFFF) - 0x80000000)
    return hex(h)


def replace_in_binary_file(rel_path, full_path, search, replace):
    with open(full_path, "rb") as fp:
        content = fp.read()
    if search in content:
        logger.info("[{}] Replace `{}` with `{}`".format(rel_path, search, replace))
    else:
        logger.error("[{}] `{}` not found!".format(rel_path, search))
        return

    content = content.replace(search, replace)
    with open(full_path, "wb") as fp:
        fp.write(content)
