package tv.purple.monolith.features.logs.component.data.repository

import io.reactivex.Single
import tv.purple.monolith.features.logs.component.data.model.MessageInfo
import tv.purple.monolith.features.logs.component.data.model.MessageItem
import tv.purple.monolith.features.logs.component.data.source.LocalLogsDataSource
import tv.purple.monolith.features.logs.component.data.source.TwitchLogsDataSource
import tv.twitch.android.shared.chat.messages.data.ChatMessageV2Parser
import tv.twitch.android.shared.chat.pub.messages.ui.ChatHistoryMessage
import tv.twitch.chat.library.model.ChatMessageInfo
import javax.inject.Inject

class LogsRepository @Inject constructor(
    private val twitchLogsDataSource: TwitchLogsDataSource,
    private val localLogsDataSource: LocalLogsDataSource
) {
//    fun getLocalLogs(userId: Int, channelId: Int): Single<List<MessageItem>> {
//        return localLogsDataSource.getMessages(userId = userId, channelId = channelId)
//    }
//
//    fun getLocalLogs(userName: String, channelId: Int): Single<List<MessageItem>> {
//        return localLogsDataSource.getMessages(userName = userName, channelId = channelId)
//    }

    fun getTwitchLogs(
        parser: ChatMessageV2Parser,
        username: String,
        channelId: String
    ): Single<List<MessageItem>> {
        return twitchLogsDataSource.getMessages(
            parser = parser,
            username = username,
            channelId = channelId
        )
    }

    fun addLocalMessage(
        channelId: Int,
        userInfo: tv.twitch.chat.library.model.ChatUserInfo,
        timestamp: Long,
        msg: ChatHistoryMessage
    ) {
        localLogsDataSource.addMessage(
            MessageInfo(
                userId = userInfo.userId,
                userName = userInfo.userName,
                timestamp = timestamp,
                msg = msg,
                channelId = channelId
            )
        )
    }
}