package tv.purple.monolith.features.chat.bridge

import android.content.Context
import android.graphics.Color
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Flowable
import tv.purple.monolith.bridge.PurpleTVAppContainer
import tv.purple.monolith.bridge.ex.IMessageRecyclerItem
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.PrefManager
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.variants.DeletedMessages
import tv.purple.monolith.core.util.ViewUtil
import tv.purple.monolith.core.util.ViewUtil.changeVisibility
import tv.purple.monolith.core.util.ViewUtil.getView
import tv.purple.monolith.features.chat.ChatHookProvider
import tv.purple.monolith.features.chat.ChatHookProvider.Companion.alternatingBackground
import tv.purple.monolith.features.chat.ChatHookProvider.Companion.useCustomTimestamps
import tv.purple.monolith.features.chat.util.ChatUtil.createDeletedGrey
import tv.purple.monolith.features.chat.util.ChatUtil.createDeletedStrikethrough
import tv.purple.monolith.features.chat.util.ChatUtil.createTimestampSpanFromChatMessageSpan
import tv.purple.monolith.features.chat.view.ViewFactory
import tv.purple.monolith.models.wrapper.EmoteCardModelWrapper
import tv.purple.monolith.models.wrapper.EmotePackageSet
import tv.twitch.android.core.adapters.RecyclerAdapterItem
import tv.twitch.android.core.mvp.presenter.StateUpdater
import tv.twitch.android.core.mvp.viewdelegate.EventDispatcher
import tv.twitch.android.feature.theatre.chomments.ChommentsViewDelegate
import tv.twitch.android.models.emotes.EmoteCardModelResponse
import tv.twitch.android.models.emotes.EmoteSet
import tv.twitch.android.models.subscriptions.SubscriptionProductTier
import tv.twitch.android.shared.chat.emotecard.EmoteCardPresenter
import tv.twitch.android.shared.chat.emotecard.EmoteCardState
import tv.twitch.android.shared.chat.emotecard.EmoteCardUiModel
import tv.twitch.android.shared.chat.messages.ui.MessageRecyclerItem
import tv.twitch.android.shared.chat.messages.util.ChatUtil
import tv.twitch.android.shared.chat.pub.messages.data.ChatMessage
import tv.twitch.android.shared.chat.pub.messages.data.MessageTokenV2
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageClickedEvents
import tv.twitch.android.shared.emotes.emotepicker.EmotePickerPresenter
import tv.twitch.android.shared.emotes.emotepicker.EmotePickerViewDelegate
import tv.twitch.android.shared.emotes.emotepicker.models.EmoteClickedEvent
import tv.twitch.android.shared.emotes.emotepicker.models.EmotePickerSection
import tv.twitch.android.shared.emotes.emotepicker.models.EmoteUiSet
import tv.twitch.android.shared.subscriptions.SubscriptionStatusForTier.NotSubscribedAtTier
import tv.twitch.android.shared.ui.elements.span.`MediaSpan$Type`
import tv.twitch.android.shared.ui.elements.span.UrlDrawable
import java.util.Date

object ChatHook {
    private val chatHookProvider by lazy { PurpleTVAppContainer.getChatHookProvider() }

    @JvmStatic
    val timestampsDisabled: Boolean = !useCustomTimestamps

    @JvmStatic
    val hypetrainDisabled: Boolean = Flag.DISABLE_HYPETRAIN.asBoolean()

    @JvmStatic
    val hideChatHeader: Boolean = Flag.HIDE_CHAT_HEADER.asBoolean()

    @JvmStatic
    fun isHideOneChatPopupEnabled(): Boolean = Flag.HIDE_ONE_CHAT_POPUP.asBoolean()

    @JvmStatic
    fun hookCreateDeletedSpanFromChatMessageSpan(
        messageId: String?,
        message: Spanned?,
        context: Context?,
        eventDispatcher: EventDispatcher<ChatMessageClickedEvents>,
        hasModAccess: Boolean
    ): Spanned? {
        return when (Flag.DELETED_MESSAGES_STRATEGY.asVariant<DeletedMessages>()) {
            DeletedMessages.Mod -> ChatUtil.Companion!!.createDeletedSpanFromChatMessageSpan(
                messageId,
                message,
                context,
                eventDispatcher,
                true
            )

            DeletedMessages.Strikethrough -> createDeletedStrikethrough(
                message = message
            )

            DeletedMessages.Grey -> createDeletedGrey(
                message = message
            )

            DeletedMessages.Default -> ChatUtil.Companion!!.createDeletedSpanFromChatMessageSpan(
                messageId,
                message,
                context,
                eventDispatcher,
                hasModAccess
            )
        }
    }

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    @JvmStatic
    fun maybeAddTimestamp(
        spanned: Spanned,
        message: MessageRecyclerItem
    ): Spanned {
        if (!useCustomTimestamps) {
            return spanned
        }
        val ts = message.timeStamp?.toLong() ?: 0
        val timestamp = if (ts != 0L) {
            ts * 1000L
        } else System.currentTimeMillis()

        val uid = message.userId ?: 0
        return if (uid > 0) {
            createTimestampSpanFromChatMessageSpan(
                message = spanned,
                date = Date(timestamp),
                variant = Flag.TIMESTAMPS.asVariant()
            )
        } else {
            spanned
        }
    }

    @JvmStatic
    fun hookLiveChatMessage(
        msg: ChatMessage.LiveChatMessage,
        channelId: Int
    ): ChatMessage.LiveChatMessage {
        return chatHookProvider.hookLiveChatMessage(msg, channelId)
    }

    @JvmStatic
    fun hookAutoCompleteMapProvider(emotesFlow: Flowable<List<EmoteSet>>): Flowable<List<EmoteSet>> {
        return chatHookProvider.hookAutoCompleteMapProvider(emotesFlow)
    }

    @JvmStatic
    fun hookEmoteSetsFlowable(
        map: Flowable<Pair<EmoteUiSet, MutableList<EmoteUiSet>>>,
        channelId: Integer?
    ): Flowable<Pair<EmoteUiSet, MutableList<EmoteUiSet>>> {
        return chatHookProvider.hookEmoteSetsFlowable(map, channelId)
    }

    @JvmStatic
    fun getPurpleTVEmotesButton(delegate: EmotePickerViewDelegate): ImageView {
        return ViewFactory.createPurpleTVEmotesButton(delegate = delegate)
    }

    @JvmStatic
    fun renderEmotePickerState(
        state: EmotePickerPresenter.EmotePickerState,
        button: ImageView?
    ) {
        button?.isSelected = state.selectedEmotePickerSection == EmotePickerSection.PURPLETV
    }

    @JvmStatic
    fun isStickyHeadersEnabled(): Boolean {
        return !Flag.DISABLE_STICKY_HEADERS_EP.asBoolean()
    }

    @JvmStatic
    fun changeBitsButtonVisibility(org: Boolean): Boolean {
        if (Flag.HIDE_BITS_BUTTON.asBoolean()) {
            return false
        }

        return org
    }

    @JvmStatic
    fun onBindToViewHolder(viewHolder: RecyclerView.ViewHolder?, item: RecyclerAdapterItem?) {
        chatHookProvider.onBindToViewHolder(viewHolder, item)
    }

    @JvmStatic
    fun hookMediaSpanSizeDp(sizeDp: Float): Float {
        return chatHookProvider.hookMediaSpanSizeDp(sizeDp)
    }

    @JvmStatic
    fun hookEmotePickerPresenterLongEmoteClick(
        clickEvent: EmoteClickedEvent,
        thos: IEmotePickerPresenter
    ): Boolean {
        return chatHookProvider.hookEmotePickerPresenterLongEmoteClick(
            clickEvent = clickEvent,
            emotePickerPresenter = thos
        )
    }

    @JvmStatic
    fun sortEmotePickerUiSets(list: MutableList<EmoteUiSet>): MutableList<EmoteUiSet> {
        list.find { it.header.emotePickerSection.equals(EmotePickerSection.PURPLETV_FAV) }
            ?.let {
                list.remove(it)
                list.add(0, it)
            }

        return list
    }

    @JvmStatic
    fun setShouldHighlightBackground(
        message: IMessageRecyclerItem?,
        chatMessage: ChatMessage.LiveChatMessage?
    ) {
        message ?: return
        chatMessage ?: return

        chatHookProvider.setShouldHighlightBackground(message, chatMessage)
    }

    @JvmStatic
    fun maybeAbItems(items: MutableList<out RecyclerAdapterItem>) {
        if (!alternatingBackground) {
            return
        }

        val alternateColor = getAlternateBackgroundColor()
        var firstItem = true
        var draw = false
        for (item in items) {
            if (item is IMessageRecyclerItem) {
                if (item.skipForAlternatingBackground()) {
                    continue
                }
                if (firstItem) {
                    draw = item.getAlternatingBackground() != null
                    firstItem = false
                }
                if (draw) {
                    item.setAlternatingBackground(alternateColor)
                    draw = false
                    continue
                }
                draw = true
            }
        }
    }

    private fun getAlternateBackgroundColor(): Int {
        return if (PrefManager.isDarkThemeEnabled) {
            Color.DKGRAY
        } else {
            Color.LTGRAY
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    @JvmStatic
    fun preloadPTVEmoteToken(
        urlDrawable: UrlDrawable?,
        emoteToken: MessageTokenV2.EmoteToken?
    ) {
        urlDrawable ?: return

        if (emoteToken is PurpleTVEmoteToken) {
            urlDrawable.setWide(true)
            urlDrawable.setAnimated(PrefManager.isAnimatedEmotesEnabled)
            if (emoteToken.subEmotes.isNotEmpty()) {
                injectZWEmotes(urlDrawable, emoteToken)
            }
        }
    }

    private fun injectZWEmotes(urlDrawable: UrlDrawable, emoteToken: PurpleTVEmoteToken) {
        for (zwToken in emoteToken.subEmotes) {
            val url = EmoteCardModelWrapper.fromString(str = zwToken.id)!!.emoteUrl
            val drawable = UrlDrawable(url, `MediaSpan$Type`.Emote)
            urlDrawable.stack?.add(drawable)
        }
    }

//    @JvmStatic
//    fun hookActionMessageStyle(span: CharSequence, style: TextStyle.Action): Spannable {
//        return when (Flag.ME_STYLE.asVariant<MeStyle>()) {
//            MeStyle.DISABLED -> SpannableString(span)
//            MeStyle.COLORED -> SpannableString(span).apply {
//                setSpan(ForegroundColorSpan(style.foregroundColor), 0, span.length, 33)
//            }
//
//            MeStyle.ITALIC -> SpannableString(span).apply {
//                setSpan(StyleSpan(2), 0, span.length, 33)
//            }
//
//            MeStyle.ITALIC_COLORED -> SpannableString(span).apply {
//                setSpan(StyleSpan(2), 0, span.length, 33)
//                setSpan(ForegroundColorSpan(style.foregroundColor), 0, span.length, 33)
//            }
//        }
//    }

    @JvmStatic
    val landscapeContainerScale get() = Flag.LANDSCAPE_CHAT_SIZE.asInt()

    @JvmStatic
    val landscapeContainerScaleFloat: Float
        get() = 1f - Flag.LANDSCAPE_CHAT_SIZE.asInt().toFloat() / 100f

    @JvmStatic
    val landscapeSplitContainerScale get() = Flag.LANDSCAPE_SPLIT_CHAT_SIZE.asInt()

    @JvmStatic
    val landscapeSplitContainerScaleFloat: Float
        get() = 1f - Flag.LANDSCAPE_SPLIT_CHAT_SIZE.asInt().toFloat() / 100f

    @JvmStatic
    fun maybeHideOneChatRightSideViews(
        defaultRightSideViews: MutableList<View>,
        bits: View
    ) {
        if (Flag.ONE_CHAT_LURKER.asBoolean()) {
            defaultRightSideViews.forEach {
                ViewUtil.hide1(it)
            }
            ViewUtil.hide1(bits)
        }
    }

    @JvmStatic
    fun hookEmoteCardModelResponse(emoteId: String?): EmoteCardModelResponse? {
        if (emoteId.isNullOrBlank()) {
            return null
        }

        val model = EmoteCardModelWrapper.fromString(str = emoteId) ?: return null

        return EmoteCardModelResponse.Success(
            PurpleTVEmoteCardModel(
                token = model.token,
                url = model.url,
                set = model.set
            )
        )
    }

    @JvmStatic
    fun pushPurpleTVEmoteCardLoadedState(
        stateUpdater: StateUpdater<*, EmoteCardPresenter.UpdateEvent>?,
        emoteCardModel: PurpleTVEmoteCardModel
    ) {
        stateUpdater?.pushStateUpdate(
            EmoteCardPresenter.UpdateEvent.Initialized(
                EmoteCardState.Loaded(
                    PurpleTVEmoteCardUiModel(
                        emoteCardModel
                    ) as EmoteCardUiModel,
                    emoteCardModel,
                    null,
                    null,
                    NotSubscribedAtTier(SubscriptionProductTier.UNKNOWN, false)
                )
            )
        )
    }

    @JvmStatic
    fun maybeHideChatHeader(viewDelegate: ChommentsViewDelegate?) {
        if (Flag.HIDE_CHAT_HEADER.asBoolean()) {
            viewDelegate?.contentView?.getView<View>(
                resName = "chomments_header_container"
            )?.changeVisibility(false)
            viewDelegate?.contentView?.getView<View>(
                resName = "chomments_header_container_divider"
            )?.changeVisibility(false)
        }
    }

    @JvmStatic
    fun filterChatMessages(merge: Flowable<ChatMessage>): Flowable<ChatMessage> {
        return merge.filter {
            !(ChatHookProvider.isChatKilled ||
                    chatHookProvider.isMessageInBlacklist(it))
        }
    }

    @JvmStatic
    fun getPurpleTVEmoteUrl(url: String?, emoteToken: MessageTokenV2.EmoteToken): String? {
        emoteToken.id?.let { tokenId ->
            if (tokenId.isBlank() || !tokenId.startsWith("{")) {
                return url
            }
        }

        val model = EmoteCardModelWrapper.fromString(str = emoteToken.id)
        if (model != null) {
            if (model.set == EmotePackageSet.TwitchGlobal || model.set == EmotePackageSet.TwitchChannel) {
                return url
            }

            return model.emoteUrl
        }
        return url
    }

    @JvmStatic
    fun maybeHighlightChatMessage(item: RecyclerAdapterItem?, messageContainer: View?) {
        item ?: return
        messageContainer ?: return

        if (item !is IMessageRecyclerItem) {
            return
        }

        LoggerImpl.debugObject(item);
        LoggerImpl.debugObject(messageContainer);

        item.getHighlightColor()?.let { color ->
            messageContainer.setBackgroundColor(color)
        } ?: run {
            item.getAlternatingBackground()?.let { color ->
                messageContainer.setBackgroundColor(color)
            } ?: run {
                messageContainer.background = null
            }
        }
    }
}