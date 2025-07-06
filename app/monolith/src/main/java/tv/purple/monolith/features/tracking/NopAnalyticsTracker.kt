package tv.purple.monolith.features.tracking

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import tv.purple.monolith.bridge.PurpleTVAppContainer.Companion.getInstance
import tv.purple.monolith.component.api.repository.NopRepository
import tv.purple.monolith.core.LoggerWithTag
import tv.purple.monolith.core.models.lifecycle.LifecycleAware
import tv.purple.monolith.core.util.PurpleBuildConfigUtil
import tv.purple.monolith.features.tracking.bridge.BugsnagUtil
import tv.purple.monolith.models.retrofit.nop.PinnyInfo
import tv.twitch.android.core.analytics.UniqueDeviceIdentifier
import tv.twitch.android.core.user.TwitchAccountManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NopAnalyticsTracker @Inject constructor(
    private val repository: NopRepository,
    private val uniqueDeviceIdentifier: UniqueDeviceIdentifier
) : LifecycleAware {
    private val logger = LoggerWithTag("Tracker")
    private val disposables = CompositeDisposable()

    private val buildNumber by lazy {
        PurpleBuildConfigUtil.buildNumber
    }

    companion object {
        @JvmStatic
        fun get(): NopAnalyticsTracker {
            return getInstance().provideTracker()
        }
    }

    private fun ping() {
        val deviceId = uniqueDeviceIdentifier.uniqueID
        disposables.add(
            repository.ping(
                build = buildNumber,
                deviceId = deviceId
            ).subscribe({
                logger.info("OK")
            }, { error ->
                BugsnagUtil.logException(error, "NopAnalyticsTracker")
            })
        )
    }

    fun getPinnyInfo(): Single<PinnyInfo> {
        return repository.pingInfo(build = buildNumber)
    }

    override fun onAllComponentDestroyed() {}
    override fun onAllComponentStopped() {}
    override fun onAccountLogout() {}
    override fun onFirstActivityCreated() {}
    override fun onConnectedToChannel(channelId: Int) {}
    override fun onConnectingToChannel(channelId: Int) {}
    override fun onAccountLogin(tam: TwitchAccountManager) {}

    override fun onFirstActivityStarted() {
        ping()
    }
}
