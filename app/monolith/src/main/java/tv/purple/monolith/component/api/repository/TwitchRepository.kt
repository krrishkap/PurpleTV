package tv.purple.monolith.component.api.repository

import io.reactivex.Single
import tv.purple.monolith.component.api.data.source.TwitchApiSource
import tv.purple.monolith.models.retrofit.gql.logs.DataResponse
import tv.purple.monolith.models.retrofit.gql.logs.ModLogsData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TwitchRepository @Inject constructor(
    private val twitchApiSource: TwitchApiSource
) {
    fun getModLogs(
        channelId: String,
        username: String
    ): Single<DataResponse<ModLogsData>> {
        return twitchApiSource.getModLogs(channelId = channelId, username = username)
    }
}