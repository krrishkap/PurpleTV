@file:Suppress("KotlinJniMissingFunction")

package tv.purple.monolith.features.vodhunter

import tv.purple.monolith.features.tracking.bridge.BugsnagUtil
import tv.twitch.android.core.crashreporter.CrashReporter

object LibWrapper {
    external fun getVodHunterPayload(stream: String): String

    init {
        try {
            System.loadLibrary("monolith")
        } catch (e: UnsatisfiedLinkError) {
            BugsnagUtil.logException(e, "VodHunter", CrashReporter.ExceptionType.NON_FATAL)
        }
    }
}