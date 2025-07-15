package tv.purple.monolith.component.api.data.source

import io.reactivex.Scheduler
import io.reactivex.Single
import tv.purple.monolith.component.api.data.api.TwitchGQLApi
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.models.retrofit.gql.logs.DataResponse
import tv.purple.monolith.models.retrofit.gql.logs.ModLogsData
import tv.purple.monolith.models.retrofit.gql.logs.ModLogsRequest
import tv.purple.monolith.models.retrofit.gql.logs.UserInfoRequest
import javax.inject.Inject

class TwitchApiSource @Inject constructor(
    private val twitchGQLApi: TwitchGQLApi,
    private val scheduler: Scheduler
) {
    fun getModLogs(username: String, channelId: String): Single<DataResponse<ModLogsData>> {
        return twitchGQLApi.requestUserInfo(
            UserInfoRequest(
                username = username
            )
        ).flatMap {
            LoggerImpl.debug { "userInfo --> ${it.data?.user}" }

            val userInfo = it.data?.user
                ?: return@flatMap Single.error(NullPointerException("userInfo is null"))
            val userId =
                userInfo.id ?: return@flatMap Single.error(NullPointerException("userId is null"))

            twitchGQLApi.requestModLogs(
                ModLogsRequest(
                    channelID = channelId,
                    senderID = userId
                )
            )
        }.subscribeOn(scheduler)
    }
}