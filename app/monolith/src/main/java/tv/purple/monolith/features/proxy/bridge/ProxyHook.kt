package tv.purple.monolith.features.proxy.bridge

import android.net.Uri
import io.reactivex.Single
import okhttp3.OkHttpClient
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.variants.ProxyImpl
import tv.purple.monolith.features.proxy.Proxy
import tv.twitch.android.models.manifest.extm3u
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestResponse
import androidx.core.net.toUri

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
        builder.addNetworkInterceptor(ReYohohoTwitchProxy())
    }

    @JvmStatic
    fun tryHookManifestURI(res: Uri?): Uri? {
        res ?: return null

        return res.also {
            LoggerImpl.debug("Manifest URI: $it")
        }
    }

    @JvmStatic
    fun tryHookMediaSourceURI(res: Uri?): Uri? {
        res ?: return null

        return res.also {
            LoggerImpl.debug("Media source URI: $it")
        }
    }

    @JvmStatic
    fun tryHookSourceObjectURI(res: Uri?): Uri? {
        res ?: return null

        if (Flag.PROXY_LIST.asVariant<ProxyImpl>() != ProxyImpl.ReYohohoTwitchProxy) {
            return res
        }

        return "${ProxyImpl.ReYohohoTwitchProxy.domain}/$res".toUri().also {
            LoggerImpl.debug("Source object URI: $it")
        }
    }
}