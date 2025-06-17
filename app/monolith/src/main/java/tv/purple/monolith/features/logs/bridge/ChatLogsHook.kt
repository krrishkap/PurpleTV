package tv.purple.monolith.features.logs.bridge

import tv.purple.monolith.core.compat.ClassCompat.getPrivateField
import tv.twitch.android.shared.chat.command.BlockAndUnblockCommandInterceptor
import tv.twitch.android.shared.chat.command.ChatCommandInterceptor
import tv.twitch.android.shared.chat.command.VoteCommandInterceptor

object ChatLogsHook {
    @JvmStatic
    fun createLogsCommandInterceptor(
        vci: VoteCommandInterceptor?,
        bau: BlockAndUnblockCommandInterceptor?
    ): ChatCommandInterceptor? {
        vci ?: return null
        bau ?: return null

        return LogsCommandInterceptor(
            //activity = bau.getPrivateField("activity"),
            chatSource = vci.getPrivateField("chatMessagesDispatcher")
        )
    }
}