package tv.purple.monolith.features.tracking.bridge

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Configuration
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.util.PurpleBuildConfigUtil
import tv.twitch.android.core.crashreporter.CrashReporter
import tv.twitch.android.util.LogTag
import java.util.concurrent.atomic.AtomicBoolean

object BugsnagUtil {
    private val isInitialized = AtomicBoolean(false)

    @JvmStatic
    fun init(application: Application) {
        LoggerImpl.debug("[Bugsnag] Init...")
        PurpleBuildConfigUtil.buildConfigVariant.apiKey?.let { apiKey ->
            val build = PurpleBuildConfigUtil.buildConfigVariant
            Bugsnag.start(application, Configuration(apiKey).apply {
                appVersion = "${build.version}/{${build.number}}"
            })
            isInitialized.set(true)
            LoggerImpl.info("[Bugsnag] Done!")
            return
        }
        LoggerImpl.error("[Bugsnag] API key not found")
    }

    @JvmStatic
    fun logException(th: Throwable?, context: String, type: CrashReporter.ExceptionType) {
        if (!isInitialized.get()) {
            return
        }

        th ?: run {
            LoggerImpl.warning("th is null")
            return
        }

        LoggerImpl.error("context: `$context`, type: `$type`, value: `${th.localizedMessage}`")
        Bugsnag.notify(th)
        th.printStackTrace()
    }

    @JvmStatic
    fun setTag(key: String?, value: String?) {
        if (!isInitialized.get()) {
            return
        }

        key ?: return

        Bugsnag.addMetadata("tag", key, value)
    }

    @JvmStatic
    fun setBool(key: String?, z: Boolean) {
        if (!isInitialized.get()) {
            return
        }

        key ?: return

        Bugsnag.addMetadata("bool", key, z)
        LoggerImpl.debug("key: $key, value: $z")
    }

    @JvmStatic
    fun setUser(username: String?) {
        if (!isInitialized.get()) {
            return
        }

        username ?: return

        Bugsnag.setUser(null, null, username)
    }

    @JvmStatic
    fun setInteger(key: String?, i: Int) {
        if (!isInitialized.get()) {
            return
        }

        key ?: return

        Bugsnag.addMetadata("integer", key, i)
        LoggerImpl.debug("key: $key, value: $i")
    }

    @JvmStatic
    fun setLong(key: String?, j: Long) {
        if (!isInitialized.get()) {
            return
        }

        key ?: return

        Bugsnag.addMetadata("long", key, j)
        LoggerImpl.debug("key: $key, value: $j")
    }

    @JvmStatic
    fun logEvent(level: String, msg: String) {
        if (!isInitialized.get()) {
            return
        }

        LoggerImpl.debug("level: $level, msg: $msg")
    }

    @JvmStatic
    fun log(logLevel: CrashReporter.LogLevel, tag: LogTag, safeLogMessage: String) {
        // TODO: impl
    }
}
