package tv.purple.monolith.component.api.data.api

import io.reactivex.Single
import retrofit2.http.GET
import tv.purple.monolith.models.retrofit.ReyohohoBadgeData

interface ReyohohoApi {
    @GET("/api/badge-users")
    fun badges(): Single<List<ReyohohoBadgeData>>
}