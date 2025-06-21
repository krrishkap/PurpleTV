package tv.purple.monolith.features.logs

import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import tv.purple.monolith.bridge.PurpleTVAppContainer.Companion.getInstance
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.variants.LocalLogs
import tv.purple.monolith.core.util.ViewUtil.changeVisibility
import tv.purple.monolith.features.logs.component.data.repository.LogsRepository
import tv.purple.monolith.features.logs.view.ViewFactory
import tv.twitch.android.core.mvp.presenter.BasePresenter
import tv.twitch.android.core.mvp.rxutil.DisposeOn
import tv.twitch.android.models.chat.MessageBadge
import tv.twitch.android.shared.chat.moderation.ModerationBottomSheetViewState
import tv.twitch.android.shared.chat.pub.events.ChatChannelEvent
import tv.twitch.android.shared.chat.pub.events.MessagesReceivedEvent
import tv.twitch.android.shared.chat.pub.messages.data.ChatMessage
import tv.twitch.android.shared.chat.pub.messages.ui.ChatHistoryMessage
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageInterface
import tv.twitch.android.shared.ui.elements.bottomsheet.BottomSheetListItemModel
import tv.twitch.chat.library.model.ChatLiveMessage
import tv.twitch.chat.library.model.ChatMessageInfo
import tv.twitch.chat.library.model.ChatUserInfo
import tv.twitch.chat.library.model.ChatUserMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatLogs @Inject constructor(
    private val viewFactory: ViewFactory,
    private val logsRepository: LogsRepository
) {
    companion object {
        @JvmStatic
        fun get(): ChatLogs {
            return getInstance().provideChatLogs()
        }

        @JvmStatic
        fun maybeAddToLocalLogs(event: ChatChannelEvent?) {
            event ?: return
//
//            if (Flag.LOCAL_LOGS.asVariant<LocalLogs>() == LocalLogs.L0) {
//                return
//            }

            if (event is ChatChannelEvent.ChannelMessagesReceived) {
//                maybeAddMessagesToLocalStore(
//                    event.channelId,
//                    event.component2() ?: emptyList()
//                )
            }
        }

        @JvmStatic
        fun maybeAddToLocalLogs(messages: MessagesReceivedEvent?) {
            messages ?: return
//
//            if (Flag.LOCAL_LOGS.asVariant<LocalLogs>() == LocalLogs.L0) {
//                return
//            }

            // val chatLogs = get()

            messages.messages?.let { msg ->
//                chatLogs.maybeAddMessagesToLocalStore(
//                    messages.channelId,
//                    messages.messages
//                )
            }
        }
    }

//    fun setupLocalLogsClickHandler(
//        activity: FragmentActivity,
//        localLogs: TextView,
//        info: ChatUserDialogInfo,
//        onDismissListener: () -> Unit,
//        chatUser: ChatUser
//    ) {
//        localLogs.setOnClickListener {
//            val fragment = viewFactory.createLogsFragment()
//            fragment.bind(activity)
//            fragment.show(activity.supportFragmentManager, "orange_logs")
//            //fragment.loadLocalLogs(chatUser.userId, info.channelId)
//            onDismissListener.invoke()
//        }
//    }
//
//    fun showLocalLogs(activity: FragmentActivity, channelId: Int, userName: String) {
//        val fragment = viewFactory.createLogsFragment()
//        fragment.bind(activity)
//        fragment.show(activity.supportFragmentManager, "purpletv_logs")
//        //fragment.loadLocalLogs(channelId = channelId, userName = userName)
//    }

    fun showModLogs(activity: FragmentActivity, channelId: String, userName: String) {
        val fragment = viewFactory.createLogsFragment()
        fragment.bind(activity, channelId.toIntOrNull() ?: 0)
        fragment.show(activity.supportFragmentManager, "purpletv_logs")
        fragment.loadTwitchLogs(userName, channelId)
    }

//    fun injectLocalLogsButton(view: View): TextView {
////        return viewFactory.createLocalLogsButton(view).apply {
////            this.changeVisibility(Flag.LOCAL_LOGS.asVariant<LocalLogs>() != LocalLogs.L0)
////        }
//    }

    fun injectModLogsButton(
        list: List<BottomSheetListItemModel<*>>,
        state: ModerationBottomSheetViewState
    ): List<BottomSheetListItemModel<*>> {
        return list.toMutableList().apply {
            add(viewFactory.createModLogsButton(state = state))
        }
    }

    private fun maybeAddMessagesToLocalStore(
        channelId: Int,
        chatLiveMessageArr: List<ChatLiveMessage>
    ) {
//        if (Flag.LOCAL_LOGS.asVariant<LocalLogs>() == LocalLogs.L0) {
//            return
//        }

//        val parser = ChatMessageParser()
//        chatLiveMessageArr.forEach { msg ->
//            logsRepository.addLocalMessage(
//                channelId,
//                msg.messageInfo.userInfo,
//                msg.messageInfo.timestamp ?: 0,
//                ChatHistoryMessage(
//                    msg.messageInfo.userInfo.userId,
//                    msg.messageInfo.userInfo.userName,
//                    msg.messageInfo.userInfo.displayName,
//                    msg.messageInfo.userInfo.`nameColorARGB-0hXNFcg`.toString(),
//                    msg.messageInfo.badges.map { chatMessageBadge ->
//                        MessageBadge(chatMessageBadge.name, chatMessageBadge.version)
//                    },
//                    parser.tokens(msg.messageInfo.tokens)
//                )
//            )
//        }
    }

    fun subscribeToMessages(
        presenter: BasePresenter,
        messagesSubject: PublishSubject<MessagesReceivedEvent>
    ) {
        presenter.asyncSubscribe(
            messagesSubject.toFlowable(BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()), {
                maybeAddMessagesToLocalStore(it.channelId, it.messages)
            }, {
                it.printStackTrace()
            }, DisposeOn.DESTROY
        )
    }

    fun maybeAddToLocalLogs(msg: ChatMessageInfo, channelId: Int, channelName: String) {
//        if (Flag.LOCAL_LOGS.asVariant<LocalLogs>() == LocalLogs.L0) {
//            return
//        }

//        val parser = ChatMessageParserSdk()
//        logsRepository.addLocalMessage(
//            channelId,
//            ChatUserInfo(
//                msg.userName,
//                msg.displayName,
//                ChatUserMode(),
//                msg.nameColorARGB.toUInt(),
//                msg.userId,
//                null
//            ),
//            msg.timestamp.toLong(),
//            ChatHistoryMessage(
//                msg.userId,
//                msg.userName,
//                msg.displayName,
//                msg.nameColorARGB.toString(),
//                msg.badges.map { chatMessageBadge ->
//                    MessageBadge(chatMessageBadge.name, chatMessageBadge.version)
//                },
//                parser.tokens(msg.tokens)
//            )
//        ) // fixme: new_version
    }

    fun maybeAddToLocalLogs(msg: ChatMessageInterface, channelId: Int) {
        if (msg.isAction) {
            return
        }

//        if (Flag.LOCAL_LOGS.asVariant<LocalLogs>() == LocalLogs.L0) {
//            return
//        }

        logsRepository.addLocalMessage(
            channelId,
            ChatUserInfo(
                msg.userName,
                msg.displayName,
                ChatUserMode(),
                msg.timestampSeconds.toUInt(),
                msg.userId,
                null
            ),
            msg.timestampSeconds,
            ChatHistoryMessage(
                msg.userId,
                msg.userName,
                msg.displayName,
                "", // FIXME: fix color
                msg.badges.map { chatMessageBadge ->
                    MessageBadge(chatMessageBadge.name, chatMessageBadge.version)
                },
                msg.tokens
            )
        )
    }

    fun maybeAddToLocalLogs(msg: ChatMessage.LiveChatMessage, channelId: Int) {
        // fixme: fix
    }
}