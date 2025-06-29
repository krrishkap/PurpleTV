package tv.purple.monolith.features.chat.bridge

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tv.purple.monolith.bridge.ex.IMessageRecyclerItem
import tv.twitch.android.core.adapters.RecyclerAdapterItem
import tv.twitch.android.shared.chat.chomments.ChommentRecyclerItem
import tv.twitch.android.shared.chat.commerce.notices.SubGoalUserNoticeRecyclerItem
import tv.twitch.android.shared.chat.messages.ui.ChatMessageViewHolder
import tv.twitch.android.shared.chat.messages.ui.item.PrivateCalloutsMessageRecyclerItem
import tv.twitch.android.shared.chat.messages.ui.item.RaidMessageRecyclerItem

interface OnBindCallback {
    fun maybeChangeMessageFontSize(textView: TextView?)
    fun bindHighlightMessage(viewHolder: RecyclerView.ViewHolder?, highlightColor: Int?, abColor: Int?)
}

object BindBridge {
    fun onBindToViewHolder(
        viewHolder: RecyclerView.ViewHolder?,
        item: RecyclerAdapterItem?,
        callback: OnBindCallback
    ) {
        when (viewHolder) {
            is ChatMessageViewHolder -> {
                callback.maybeChangeMessageFontSize(textView = viewHolder.messageTextView)
            }

            is ChommentRecyclerItem.ChommentItemViewHolder -> {
                callback.maybeChangeMessageFontSize(textView = viewHolder.chommentTextView)
            }

            is SubGoalUserNoticeRecyclerItem.SubGoalUserNoticeViewHolder -> {
                callback.maybeChangeMessageFontSize(textView = viewHolder.chatMessage)
                callback.maybeChangeMessageFontSize(textView = viewHolder.goalProgressText)
                callback.maybeChangeMessageFontSize(textView = viewHolder.systemMessage)
            }

            // FIXME: broken 24.9
//            is RaidMessageRecyclerItem.RaidMessageViewHolder -> {
//                callback.maybeChangeMessageFontSize(textView = viewHolder.bind)
//            }

            is PrivateCalloutsMessageRecyclerItem.CalloutMessageViewHolder -> {
                callback.maybeChangeMessageFontSize(textView = viewHolder.body)
            }
        }
//        when (item) {
//            is IMessageRecyclerItem -> {
//                callback.bindHighlightMessage(
//                    viewHolder = viewHolder,
//                    highlightColor = item.getHighlightColor(),
//                    abColor = item.getAlternatingBackground()
//                )
//            }
//        }
    }
}