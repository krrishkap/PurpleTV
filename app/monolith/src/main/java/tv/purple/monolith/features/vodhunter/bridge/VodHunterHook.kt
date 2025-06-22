package tv.purple.monolith.features.vodhunter.bridge

import io.reactivex.Single
import tv.purple.monolith.bridge.PurpleTVAppContainer
import tv.purple.monolith.core.models.flag.Flag
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestError
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestResponse

object VodHunterHook {
    @JvmStatic
    fun maybeHookVodManifestResponse(
        vodId: String,
        singleDoOnSuccess: Single<ManifestResponse>?
    ): Single<ManifestResponse>? {
        return singleDoOnSuccess?.flatMap { response ->
            if (response is ManifestResponse.Success) {
                return@flatMap Single.just(response)
            }

            if (response !is ManifestResponse.Error) {
                return@flatMap Single.just(response)
            }

            if (response.error != ManifestError.VOD_SUBS_ONLY) {
                return@flatMap Single.just(response)
            }

            if (!PurpleTVAppContainer.getInstance().provideVODHunter().canUseVodHunter()) {
                return@flatMap Single.just(response)
            }

            return@flatMap PurpleTVAppContainer.getInstance().provideVODHunter()
                .hookVodManifestResponse(vodId)
        }
    }

    @JvmStatic
    val isEnabled: Boolean
        get() = Flag.VODHUNTER.asBoolean()
}