package tv.purple.monolith.component.api.data.api.proxy

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface PublicProxyApi {
    @GET
    fun getPlaylist(@Url fullUrl: String): Single<Response<String>>
}