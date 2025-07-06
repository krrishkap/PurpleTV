package tv.purple.monolith.features.proxy

import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.upstream.DataSpec
import io.reactivex.Single
import retrofit2.Response
import tv.purple.monolith.bridge.PurpleTVAppContainer.Companion.getInstance
import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.component.api.repository.ProxyRepository
import tv.purple.monolith.core.CoreUtil
import tv.purple.monolith.core.LoggerWithTag
import tv.purple.monolith.core.ResManager.fromResToString
import tv.purple.monolith.core.compat.ClassCompat.getPrivateField
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.variants.ProxyImpl
import tv.purple.monolith.features.proxy.view.CustomProxyUrlFragment
import tv.twitch.android.models.AccessTokenResponse
import tv.twitch.android.models.TokenForbiddenReason
import tv.twitch.android.models.manifest.ManifestModel
import tv.twitch.android.models.manifest.extm3u
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestResponse
import tv.twitch.android.shared.manifest.fetcher.pub.extm3uParser
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Proxy @Inject constructor(
    private val repository: ProxyRepository
) {
    private val logger = LoggerWithTag("Proxy")

    fun maybeHookStreamManifestResponse(
        channelName: String,
        manifest: Single<ManifestResponse>
    ): Single<ManifestResponse> {
        val variant = Flag.PROXY_LIST.asVariant<ProxyImpl>()
        if (variant == ProxyImpl.Disabled) {
            return manifest
        }

        return when (variant) {
            ProxyImpl.CUSTOM -> trySwapPlaylist(
                twitchResponse = manifest,
                proxyResponse = repository.getCustomProxyResponse(channelName),
                proxyName = variant.desc
            )
            ProxyImpl.ReYohohoTwitchProxy -> injectProxyName(manifest, ProxyImpl.ReYohohoTwitchProxy.desc)

            else -> variant.domain?.let { domain ->
                trySwapPlaylist(
                    twitchResponse = manifest,
                    proxyResponse = repository.getPublicProxyPlaylist(channelName, domain),
                    proxyName = variant.desc
                )
            } ?: manifest
        }
    }

    private fun injectProxyName(
        twitchResponse: Single<ManifestResponse>,
        proxyName: String
    ): Single<ManifestResponse> {
        return twitchResponse.map { response ->
            if (response is ManifestResponse.Success.ManifestSuccess) {
                response.model.getPrivateField<extm3u>("mManifest").ProxyServer = proxyName
            }

            response
        }
    }
    
    private fun trySwapPlaylist(
        twitchResponse: Single<ManifestResponse>,
        proxyResponse: Single<Response<String>>,
        proxyName: String
    ): Single<ManifestResponse> {
        logger.debug("[$proxyName] Start request...")
        return proxyResponse.flatMap { proxyPlaylist ->
            if (!proxyPlaylist.isSuccessful) {
                CoreUtil.showToast(
                    RES_STRINGS.purpletv_generic_error_d.fromResToString(
                        "Proxy",
                        RES_STRINGS.purpletv_proxy_error_ur.fromResToString()
                    )
                )
                logger.debug("[$proxyName] Unsuccessful")
                return@flatMap twitchResponse
            }
            val time = getRequestTime(proxyPlaylist.raw())
            logger.debug("[$proxyName] Time: $time")
            var body = proxyPlaylist.body() ?: run {
                CoreUtil.showToast(
                    RES_STRINGS.purpletv_generic_error_d.fromResToString(
                        "Proxy",
                        RES_STRINGS.purpletv_proxy_error_cpr.fromResToString()
                    )
                )
                logger.debug("[$proxyName] Body is null")
                return@flatMap Single.error(Exception("proxy_error"))
            }
            val proxyUrl = proxyPlaylist.raw().request.url
            body = body.replace(
                "#EXT-X-TWITCH-INFO:",
                "#EXT-X-TWITCH-INFO:PROXY-SERVER=\"$proxyName ($time ms)\",PROXY-URL=\"$proxyUrl\","
            )
            logger.debug("[$proxyName] Result: $body")
            createPlaylistResponse(body, proxyPlaylist)
        }.onErrorResumeNext { th: Throwable ->
            CoreUtil.showToast(
                RES_STRINGS.purpletv_generic_error_d.fromResToString(
                    "Proxy",
                    th.localizedMessage ?: "<empty>"
                )
            )
            logger.debug("[$proxyName] Ex: ${th.localizedMessage}")
            th.printStackTrace()
            twitchResponse
        }
    }

    fun createCustomUrlFragment(): Fragment {
        return CustomProxyUrlFragment()
    }

    companion object {
        @JvmStatic
        fun get(): Proxy {
            return getInstance().provideProxy()
        }

        private fun buildManifestModel(url: String, manifest: String): ManifestModel {
            return ManifestModel(
                url,
                extm3uParser.parse(manifest),
                AccessTokenResponse("", "", TokenForbiddenReason.UNKNOWN, 0),
                manifest
            )
        }

        private fun createPlaylistResponse(
            playlist: String, proxyResponse: Response<String>
        ): Single<ManifestResponse> {
            return Single.just(
                ManifestResponse.Success.ManifestSuccess(
                    buildManifestModel(
                        proxyResponse.raw().request.url.toString(),
                        playlist
                    )
                )
            )
        }

        private fun getRequestTime(response: okhttp3.Response): Int = try {
            val tx = response.sentRequestAtMillis
            val rx = response.receivedResponseAtMillis
            (rx - tx).toInt()
        } catch (th: Throwable) {
            0
        }

        @JvmStatic
        fun patchExoPlayerDataSpec(dataSpec: DataSpec?): DataSpec? {
            dataSpec ?: run {
                return null
            }

            val url = dataSpec.uri.toString()
            val headers = when {
                url.contains("https://api.ttv.lol/") -> Collections.unmodifiableMap(
                    dataSpec.httpRequestHeaders.toMutableMap().apply {
                        put("x-donate-to", "https://ttv.lol/donate")
                    }
                )

                url.contains(".ttvnw.net/v1/playlist/") -> mapOf(
                    "Accept" to "application/x-mpegURL, application/vnd.apple.mpegurl, application/json, text/plain"
                )

                else -> null
            } ?: return dataSpec

            return DataSpec(
                dataSpec.uri,
                dataSpec.httpMethod,
                dataSpec.httpBody,
                dataSpec.absoluteStreamPosition,
                dataSpec.position,
                dataSpec.length,
                dataSpec.key,
                dataSpec.flags,
                headers
            )
        }
    }
}