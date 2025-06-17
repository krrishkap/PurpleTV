package tv.purple.monolith.features.logs.bridge

import androidx.fragment.app.FragmentActivity
import tv.purple.monolith.bridge.PurpleTVAppContainer
import tv.purple.monolith.bridge.ex.IChatMessagesDispatcher
import tv.purple.monolith.features.logs.ChatLogs
import tv.twitch.android.core.strings.StringResource
import tv.twitch.android.models.channel.ChannelInfo
import tv.twitch.android.shared.chat.command.ChatCommandAction
import tv.twitch.android.shared.chat.command.ChatCommandInterceptor
import tv.twitch.android.shared.chat.messages.ChatMessagesDispatcher
import tv.twitch.android.shared.one.chat.pub.ChatModeMetadata

// FIXME: broken 24.9
class LogsCommandInterceptor(
    // private val activity: FragmentActivity,
    private val chatSource: ChatMessagesDispatcher
) : ChatCommandInterceptor {
    private val chatLogs by lazy {
        ChatLogs.get()
    }

    override fun executeChatCommand(action: ChatCommandAction?, p1: ChatModeMetadata?) {
        when (action) {
//            is ChatLocalLogsCommand -> {
//                chatLogs.showLocalLogs(
//                    activity = activity,
//                    channelId = action.channelId,
//                    userName = action.userName
//                )
//            }

            is ChatTwitchLogsCommand -> {
//                chatLogs.showModLogs(
//                    activity = activity,
//                    channelId = action.channelId.toString(),
//                    userName = action.userName
//                )
            }

            is ChatUsageLogsCommand -> {
                chatSource.addSystemMessage(
                    StringResource.Companion!!.fromString("Usage: ${action.text}"),
                    false
                )
            }
        }
    }

    override fun onDestroy() {}

    override fun parseChatCommand(
        strArr: Array<out String>?,
        p1: ChannelInfo?,
        p2: Long?
    ): ChatCommandAction {
        strArr ?: return ChatCommandAction.NoOp.INSTANCE
        if (strArr.isEmpty()) {
            return ChatCommandAction.NoOp.INSTANCE
        }

        val command = strArr[0].lowercase().trim()
        if (command.isBlank()) {
            return ChatCommandAction.NoOp.INSTANCE
        }

        if (strArr.size < 2 || strArr[1].isBlank()) {
            return when (command) {
                "/llogs" -> ChatUsageLogsCommand(text = "/llogs @username")
                "/mlogs" -> ChatUsageLogsCommand(text = "/mlogs @username")
                else -> ChatCommandAction.NoOp.INSTANCE
            }
        }

        return when (command) {
            "/llogs" -> ChatLocalLogsCommand(
                channelId = p1?.id ?: -1,
                userName = getUserName(strArr[1])
            )

            "/mlogs" -> ChatTwitchLogsCommand(
                channelId = p1?.id ?: -1,
                userName = getUserName(strArr[1])
            )

            else -> ChatCommandAction.NoOp.INSTANCE
        }
    }

    companion object {
        fun getUserName(str: String): String {
            return str.lowercase().trim().let {
                if (it.startsWith("@")) {
                    return@let str.substring(1)
                } else {
                    return@let str
                }
            }
        }
    }
}