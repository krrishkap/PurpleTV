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
                   "d", "-f", "-b", "--keep-broken-res",
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

    # Структурированные константы для различных типов файлов
    class SmaliFiles:
        ACTION = [
            "tv/twitch/android/shared/pip/NativePipParamsUpdater.smali",
            "tv/twitch/android/shared/pip/NativePipParamsUpdater$Companion$pipActionsFlow$1.smali",
            "tv/twitch/android/shared/pip/NativePipParamsUpdater$Companion$pipActionsFlow$1$receiver$1.smali",
            "tv/twitch/android/shared/pip/NativePictureInPicturePresenter.smali",
            "tv/twitch/android/shared/pip/NativePictureInPicturePresenter$commandReceiver$1.smali",
            "tv/twitch/android/shared/audio/ads/AudioAdsPresenter.smali",
            "tv/twitch/android/feature/foreground/audio/ads/foreground/AudioAdsForegroundPresenter.smali",
            "tv/twitch/android/feature/theatre/ads/LastCommercialIdProvider.smali"
        ]

        BROADCAST = [
            "tv/twitch/android/broadcast/gamebroadcast/GameBroadcastIntentHelper$GameBroadcastAction.smali",
            "tv/twitch/android/broadcast/routers/GameBroadcastingRouter.smali",
            "tv/twitch/android/broadcast/gamebroadcast/GameBroadcastPresenter.smali"
        ]

        ACTION_HASH_FIX = [
            "tv/twitch/android/shared/pip/NativePictureInPicturePresenter$commandReceiver$1.smali",
            "tv/twitch/android/shared/pip/NativePipParamsUpdater$Companion$pipActionsFlow$1$receiver$1.smali"
        ]

        PUSH = ["tv/twitch/android/shared/notifications/pub/NotificationIntentAction.smali"]

    class ManifestFiles:
        ANDROID_MANIFEST = ["AndroidManifest.xml"]

    # Константы для замен
    class OriginalPackages:
        TWITCH_APP = "tv.twitch.android.app"
        TWITCH_PUSH = "tv.twitch.android.push."
        TWITCH_MEDIA_ACTION = "tv.twitch.android.media.action."
        TWITCH_BROADCAST = "tv.twitch.android.broadcast"

    # Хеши для исправления действий
    class ActionHashes:
        PAUSE_HASH = b"-0xf2323c"
        RESUME_HASH = b"-0x69bc8593"

    def __init__(self, context, package_name, verbose=False):
        super().__init__(context)
        self._verbose = verbose
        self._package_name = self._validate_and_normalize_package_name(package_name)

    def _validate_and_normalize_package_name(self, package_name: str) -> str:
        """Валидирует и нормализует имя пакета"""
        if not package_name:
            raise ValueError("Package name cannot be empty")

        # Базовая валидация формата пакета
        normalized = package_name.lower().strip()
        if not normalized.replace('.', '').replace('_', '').isalnum():
            logger.warning(f"Package name '{normalized}' may contain invalid characters")

        return normalized

    def _replace_with_validation(self, smali_map: dict, files: list, replace_pairs: list, operation_name: str = ""):
        """Улучшенная версия метода _replace с валидацией и логированием"""
        if not smali_map:
            logger.error(f"Empty smali_map for operation: {operation_name}")
            return False

        if not files:
            logger.warning(f"No files specified for operation: {operation_name}")
            return False

        if not replace_pairs:
            logger.warning(f"No replacement pairs specified for operation: {operation_name}")
            return False

        success_count = 0
        total_files = len(files)

        for rel_path in files:
            full_path = smali_map.get(rel_path)
            if not full_path:
                logger.error(f"File '{rel_path}' not found in smali map for operation: {operation_name}")
                continue

            try:
                for search, replace in replace_pairs:
                    replace_in_binary_file(rel_path, full_path, search, replace)
                success_count += 1
            except Exception as e:
                logger.error(f"Failed to process file '{rel_path}' in operation '{operation_name}': {e}")

        logger.info(f"Operation '{operation_name}' completed: {success_count}/{total_files} files processed successfully")
        return success_count == total_files

    def _generate_action_strings(self):
        """Генерирует строки действий для текущего пакета"""
        return {
            'pause': f"{self._package_name}.media.action.pause_playback",
            'resume': f"{self._package_name}.media.action.resume_playback"
        }

    def _calculate_action_hashes(self):
        """Вычисляет хеши для строк действий"""
        actions = self._generate_action_strings()
        return {
            'pause': str(calc_java_string_hash(actions['pause'])).encode("utf-8"),
            'resume': str(calc_java_string_hash(actions['resume'])).encode("utf-8")
        }

    def fix_action_hash(self, smali_map: dict) -> bool:
        """Исправляет хеши действий в smali файлах"""
        hashes = self._calculate_action_hashes()

        replace_pairs = [
            (self.ActionHashes.PAUSE_HASH, hashes['pause']),
            (self.ActionHashes.RESUME_HASH, hashes['resume'])
        ]

        return self._replace_with_validation(
            smali_map=smali_map,
            files=self.SmaliFiles.ACTION_HASH_FIX,
            replace_pairs=replace_pairs,
            operation_name="fix_action_hash"
        )

    def replace_push_notifications(self, smali_map: dict) -> bool:
        """Заменяет ссылки на push уведомления"""
        original = self.OriginalPackages.TWITCH_PUSH.encode("utf-8")
        replacement = f"{self._package_name}.push.".encode("utf-8")

        return self._replace_with_validation(
            smali_map=smali_map,
            files=self.SmaliFiles.PUSH,
            replace_pairs=[(original, replacement)],
            operation_name="replace_push_notifications"
        )

    def replace_media_actions(self, smali_map: dict) -> bool:
        """Заменяет ссылки на медиа действия"""
        original = self.OriginalPackages.TWITCH_MEDIA_ACTION.encode("utf-8")
        replacement = f"{self._package_name}.media.action.".encode("utf-8")

        return self._replace_with_validation(
            smali_map=smali_map,
            files=self.SmaliFiles.ACTION,
            replace_pairs=[(original, replacement)],
            operation_name="replace_media_actions"
        )

    def replace_broadcast_actions(self, smali_map: dict) -> bool:
        """Заменяет ссылки на broadcast действия"""
        original = self.OriginalPackages.TWITCH_BROADCAST.encode("utf-8")
        replacement = f"{self._package_name}.broadcast".encode("utf-8")

        return self._replace_with_validation(
            smali_map=smali_map,
            files=self.SmaliFiles.BROADCAST,
            replace_pairs=[(original, replacement)],
            operation_name="replace_broadcast_actions"
        )

    def _generate_manifest_replacements(self):
        """Генерирует пары замен для AndroidManifest.xml"""
        return [
            (
                f'package="{self.OriginalPackages.TWITCH_APP}"'.encode("utf-8"),
                f'package="{self._package_name}"'.encode("utf-8")
            ),
            (
                f'android:name="{self.OriginalPackages.TWITCH_PUSH}'.encode("utf-8"),
                f'android:name="{self._package_name}.push.'.encode("utf-8")
            ),
            (
                f'android:name="{self.OriginalPackages.TWITCH_APP}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"'.encode("utf-8"),
                f'android:name="{self._package_name}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"'.encode("utf-8")
            ),
            (
                f'android:authorities="{self.OriginalPackages.TWITCH_APP}.'.encode("utf-8"),
                f'android:authorities="{self._package_name}.'.encode("utf-8")
            ),
            (
                f'android:authorities="com.amazon.identity.auth.device.MapInfoProvider.{self.OriginalPackages.TWITCH_APP}"'.encode("utf-8"),
                f'android:authorities="com.amazon.identity.auth.device.MapInfoProvider.{self._package_name}"'.encode("utf-8")
            ),
            (
                f'<data android:host="{self.OriginalPackages.TWITCH_APP}" android:scheme="amzn"/>'.encode("utf-8"),
                f'<data android:host="{self._package_name}" android:scheme="amzn"/>'.encode("utf-8")
            )
        ]

    def replace_android_manifest(self, ctx: Context) -> bool:
        """Заменяет ссылки на пакет в AndroidManifest.xml"""
        manifest_path = ctx.apk_dir.joinpath("AndroidManifest.xml")

        if not manifest_path.exists():
            logger.error(f"AndroidManifest.xml not found at {manifest_path}")
            return False

        manifest_map = {"AndroidManifest.xml": manifest_path}
        replace_pairs = self._generate_manifest_replacements()

        return self._replace_with_validation(
            smali_map=manifest_map,
            files=self.ManifestFiles.ANDROID_MANIFEST,
            replace_pairs=replace_pairs,
            operation_name="replace_android_manifest"
        )

    def run(self, env: Env):
        """Выполняет клонирование APK с новым именем пакета"""
        ctx = self.ctx()
        logger.info(f"Starting APK cloning with package name: {self._package_name}")

        try:
            # Генерация карты smali файлов
            logger.info("Generating smali map...")
            smali_map = {rel.as_posix(): file.as_posix() for rel, file in get_smali_classes(ctx)}
            logger.info(f"Generated smali map with {len(smali_map)} files")

            # Выполнение всех замен с отслеживанием результатов
            operations = [
                ("Media Actions", lambda: self.replace_media_actions(smali_map)),
                ("Broadcast Actions", lambda: self.replace_broadcast_actions(smali_map)),
                ("Action Hashes", lambda: self.fix_action_hash(smali_map)),
                ("Push Notifications", lambda: self.replace_push_notifications(smali_map)),
                ("Android Manifest", lambda: self.replace_android_manifest(ctx))
            ]

            failed_operations = []
            for operation_name, operation_func in operations:
                logger.info(f"Executing operation: {operation_name}")
                try:
                    if not operation_func():
                        failed_operations.append(operation_name)
                        logger.warning(f"Operation '{operation_name}' completed with warnings")
                except Exception as e:
                    failed_operations.append(operation_name)
                    logger.error(f"Operation '{operation_name}' failed: {e}")

            # Итоговый отчет
            if failed_operations:
                logger.warning(f"APK cloning completed with issues in operations: {', '.join(failed_operations)}")
            else:
                logger.info("APK cloning completed successfully")

        except Exception as e:
            logger.error(f"APK cloning failed: {e}")
            raise


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
        return ["org/joda/time", "coil", "androidx/cardview", "tv/twitch/gql/type/adapter", "tv/twitch/android/shared/dj"]

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
