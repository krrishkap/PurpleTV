package tv.purple.monolith.features.proxy.bridge

import io.reactivex.Single
import okhttp3.OkHttpClient
import tv.purple.monolith.features.proxy.Proxy
import tv.twitch.android.models.manifest.extm3u
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestResponse

object ProxyHook {
    @JvmStatic
    fun tryHookStreamManifestResponse(
        streamName: String,
        orgStreamManifest: Single<ManifestResponse>
    ): Single<ManifestResponse> {
        return Proxy.get().maybeHookStreamManifestResponse(streamName, orgStreamManifest)
    }

    @JvmStatic
    fun tryHookPlaylistUrl(requestUrl: String, model: extm3u): String {
        if (model.ProxyUrl.isNullOrBlank()) {
            return requestUrl
        }

        return model.ProxyUrl
    }

    @JvmStatic
    fun maybeAddInterceptor(builder: OkHttpClient.Builder) {
        builder.addNetworkInterceptor(LolTvApiInterceptor())
        builder.addNetworkInterceptor(GayPlusApiInterceptor())
        builder.addNetworkInterceptor(GayPlus2ApiInterceptor())
    }
}