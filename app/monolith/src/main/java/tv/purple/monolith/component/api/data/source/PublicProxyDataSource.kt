package tv.purple.monolith.component.api.data.source

import io.reactivex.Scheduler
import io.reactivex.Single
import retrofit2.Response
import tv.purple.monolith.component.api.data.api.proxy.PublicProxyApi
import javax.inject.Inject

class PublicProxyDataSource @Inject constructor(
    private val publicProxyApi: PublicProxyApi,
    private val scheduler: Scheduler
) {
    fun getPublicProxyPlaylist(channelName: String, domain: String): Single<Response<String>> {
        return publicProxyApi.getPlaylist(buildPPUrl(channelName, domain))
            .subscribeOn(scheduler)
    }

    companion object {
        private fun buildPPUrl(channelName: String, domain: String): String {
            return "$domain/live/$channelName?allow_source=true&allow_audio_only=true&fast_bread=true"
        }
    }
}