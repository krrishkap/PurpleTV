package tv.purple.monolith.component.api.repository

import io.reactivex.Single
import retrofit2.Response
import tv.purple.monolith.component.api.data.source.NopApiDataSource
import tv.purple.monolith.component.api.data.source.PublicProxyDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProxyRepository @Inject constructor(
    private val nopApiDataSource: NopApiDataSource,
    private val ppApiDataSource: PublicProxyDataSource
) {
    fun getGayPlaylist(
        channelName: String
    ): Single<Response<String>> {
        return nopApiDataSource.getGayPlaylist(channelName)
    }

    fun getPublicProxyPlaylist(channelName: String, domain: String): Single<Response<String>> {
        return ppApiDataSource.getPublicProxyPlaylist(channelName, domain)
    }

    fun getCustomProxyResponse(channelName: String): Single<Response<String>> {
        return nopApiDataSource.getCustomProxyResponse(channelName)
    }
}