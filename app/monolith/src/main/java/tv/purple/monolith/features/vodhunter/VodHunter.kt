package tv.purple.monolith.features.vodhunter

import io.reactivex.Single
import retrofit2.Response
import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.component.api.repository.NopRepository
import tv.purple.monolith.core.CoreUtil
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.LoggerWithTag
import tv.purple.monolith.core.ResManager.fromResToString
import tv.purple.monolith.core.models.lifecycle.LifecycleAware
import tv.purple.monolith.features.tracking.bridge.BugsnagUtil
import tv.twitch.android.core.crashreporter.CrashReporter
import tv.twitch.android.core.user.TwitchAccountManager
import tv.twitch.android.models.AccessTokenResponse
import tv.twitch.android.models.TokenForbiddenReason
import tv.twitch.android.models.manifest.ManifestModel
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestError
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestResponse
import tv.twitch.android.shared.manifest.fetcher.pub.extm3uParser
import javax.inject.Inject

class VodHunter @Inject constructor(
    private val nopRepository: NopRepository,
    private val manager: DonationManager
) : LifecycleAware {
    private val logger = LoggerWithTag("VODHunter")

    fun canUseVodHunter(): Boolean {
        return manager.isCurrentUserDonator()
    }

    fun hookVodManifestResponse(
        vodResponse: Single<Response<String>>,
        vodId: String
    ): Single<Response<String>> {
        return vodResponse
    }

    fun hookVodManifestResponse(
        vodId: String
    ): Single<ManifestResponse> {
        if (!canUseVodHunter()) {
            return Single.just(ManifestResponse.Error(ManifestError.VOD_SUBS_ONLY))
        }

        logger.debug("Try hooking response for vodId=$vodId")

        return createSubFreePlaylist(
            vodId = vodId
        )
    }

    private fun createSubFreePlaylist(
        vodId: String
    ): Single<ManifestResponse> {
        val payload = LibWrapper.getVodHunterPayload(stream = vodId)
        logger.debug("Generated payload --> $payload for vodId=$vodId")
        return nopRepository.getVodHunterPlaylist(payload = payload)
            .map<ManifestResponse> { raw ->
                LoggerImpl.debug("mapper response: $raw")
                val fixed = raw.body() ?: return@map ManifestResponse.Error(
                    ManifestError.UNKNOWN
                )
//                val fixed = raw.body()?.replace(
//                    "#EXT-X-TWITCH-INFO:",
//                    "#EXT-X-TWITCH-INFO:PROXY-SERVER=\"VodHunter\",PROXY-URL=\"${raw.raw().request.url}\","
//                ) ?: return@map ManifestResponse.Error(
//                    ManifestError.UNKNOWN
//                )
                LoggerImpl.debug("fixed: $fixed")
                val model = buildManifestModel(
                    raw.raw().request.url.toString(),
                    fixed
                )
                LoggerImpl.debugObject(model)
                return@map ManifestResponse.Success.ManifestSuccess(
                    model
                )
            }
            .doOnSuccess {
                logger.info("OK")
                CoreUtil.showToast(
                    "[VODHunter] ${RES_STRINGS.purpletv_vodhunter_hunting.fromResToString()}"
                )
            }
            .onErrorResumeNext { th: Throwable ->
                logger.info("FAIL")
                CoreUtil.showToast(
                    RES_STRINGS.purpletv_generic_error_d.fromResToString(
                        "VODHunter",
                        th.localizedMessage ?: "<empty>"
                    )
                )
                BugsnagUtil.logException(th, "VodHunter", CrashReporter.ExceptionType.NON_FATAL)
                Single.just(ManifestResponse.Error(ManifestError.VOD_SUBS_ONLY))
            }
    }


    override fun onAllComponentDestroyed() {}
    override fun onAllComponentStopped() {}
    override fun onAccountLogout() {}
    override fun onFirstActivityCreated() {
        manager.updateDonators()
    }

    override fun onFirstActivityStarted() {}
    override fun onConnectedToChannel(channelId: Int) {}
    override fun onConnectingToChannel(channelId: Int) {}
    override fun onAccountLogin(tam: TwitchAccountManager) {}

    companion object {
        private fun buildManifestModel(url: String, manifest: String): ManifestModel {
            return ManifestModel(
                url,
                extm3uParser.parse(manifest),
                AccessTokenResponse("", "", TokenForbiddenReason.UNKNOWN, 0),
                manifest
            )
        }
    }
}