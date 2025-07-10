package tv.purple.monolith.features.chat

import android.content.Context
import android.text.Spanned
import android.util.TypedValue
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import tv.purple.monolith.bridge.ex.IMessageRecyclerItem
import tv.purple.monolith.component.badges.BadgeProvider
import tv.purple.monolith.component.blacklist.Blacklist
import tv.purple.monolith.component.emotes.EmoteProvider
import tv.purple.monolith.component.emotes.model.Configuration
import tv.purple.monolith.component.highlighter.Highlighter
import tv.purple.monolith.component.pronouns.PronounSetter
import tv.purple.monolith.component.pronouns.component.PronounProvider
import tv.purple.monolith.core.CoreUtil
import tv.purple.monolith.core.LifecycleCore
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.LoggerWithTag
import tv.purple.monolith.core.ResManager.fromResToStringId
import tv.purple.monolith.core.compat.ClassCompat.getPrivateField
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.FlagListener
import tv.purple.monolith.core.models.flag.core.Variant.Companion.isDefault
import tv.purple.monolith.core.models.flag.variants.DeletedMessages
import tv.purple.monolith.core.models.flag.variants.DisplayName
import tv.purple.monolith.core.models.flag.variants.EmoteQuality
import tv.purple.monolith.core.models.flag.variants.PinnedMessageStrategy
import tv.purple.monolith.core.models.flag.variants.Timestamps
import tv.purple.monolith.core.models.lifecycle.LifecycleAware
import tv.purple.monolith.core.util.ViewUtil.spToPx
import tv.purple.monolith.features.chat.bridge.BindBridge
import tv.purple.monolith.features.chat.bridge.ChatFactory
import tv.purple.monolith.features.chat.bridge.ChatMessageInterfaceWrapper
import tv.purple.monolith.features.chat.bridge.IEmotePickerPresenter
import tv.purple.monolith.features.chat.bridge.OnBindCallback
import tv.purple.monolith.features.chat.bridge.PurpleTVEmoteModel
import tv.purple.monolith.features.chat.bridge.PurpleTVEmotePickerEmoteModel
import tv.purple.monolith.features.chat.bridge.PurpleTVEmoteSet
import tv.purple.monolith.features.chat.bridge.PurpleTVEmoteToken
import tv.purple.monolith.features.chat.data.model.FavoriteEmote
import tv.purple.monolith.features.chat.data.repository.FavEmotesRepository
import tv.purple.monolith.features.chat.util.ChatUtil.isUserMentioned
import tv.purple.monolith.models.data.emotes.Emote
import tv.purple.monolith.models.wrapper.EmotePackageSet
import tv.twitch.android.core.adapters.RecyclerAdapterItem
import tv.twitch.android.core.mvp.viewdelegate.EventDispatcher
import tv.twitch.android.core.user.TwitchAccountManager
import tv.twitch.android.models.chat.ChatModNoticeEvents
import tv.twitch.android.models.chat.MessageBadgeViewModel
import tv.twitch.android.models.emotes.EmoteModelAssetType
import tv.twitch.android.models.emotes.EmoteSet
import tv.twitch.android.shared.chat.messages.data.ChatNoticeExtensionsKt
import tv.twitch.android.shared.chat.messages.ui.ChatMessageViewHolder
import tv.twitch.android.shared.chat.messages.ui.MessageRecyclerItem
import tv.twitch.android.shared.chat.pub.events.MessagesReceivedEvent
import tv.twitch.android.shared.chat.pub.messages.data.ChatMessage
import tv.twitch.android.shared.chat.pub.messages.data.MessageTokenV2
import tv.twitch.android.shared.chat.pub.messages.data.MessageTokenV2.EmoteToken
import tv.twitch.android.shared.chat.pub.messages.data.MessageTokenV2.MentionToken
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageClickedEvents
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageInterface
import tv.twitch.android.shared.chat.pub.pinnedchat.CreatorPinnedChatTime
import tv.twitch.android.shared.chat.pub.pinnedchat.CreatorPinnedChatUiModel
import tv.twitch.android.shared.chat.pub.pinnedchat.PinnedChatMessageState
import tv.twitch.android.shared.chat.settings.preferences.ChatSettingsPreferencesFile
import tv.twitch.android.shared.emotes.emotepicker.models.ClickedEmote
import tv.twitch.android.shared.emotes.emotepicker.models.EmoteClickedEvent
import tv.twitch.android.shared.emotes.emotepicker.models.EmoteHeaderUiModel
import tv.twitch.android.shared.emotes.emotepicker.models.EmotePickerSection
import tv.twitch.android.shared.emotes.emotepicker.models.EmoteUiModel
import tv.twitch.android.shared.emotes.emotepicker.models.EmoteUiSet
import tv.twitch.android.shared.raids.RaidsConfiguration
import tv.twitch.android.util.CoreDateUtil
import tv.twitch.chat.library.ChannelEvent
import tv.twitch.chat.library.IrcEventComponents
import tv.twitch.chat.library.model.ChatMessageInfo
import tv.twitch.chat.library.model.IrcCommand
import java.util.StringTokenizer
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatHookProvider @Inject constructor(
    private val context: Context,
    private val emoteProvider: EmoteProvider,
    private val badgeProvider: BadgeProvider,
    private val pronounProvider: PronounProvider,
    private val twitchAccountManager: TwitchAccountManager,
    private val lifecycleCore: LifecycleCore,
    private val favEmotesRepository: FavEmotesRepository,
    private val highlighter: Highlighter,
    private val blacklist: Blacklist,
    private val scheduler: Scheduler,
    private val chatSettings: ChatSettingsPreferencesFile
) : LifecycleAware, FlagListener, OnBindCallback {
    private val currentChannelSubject = BehaviorSubject.create<Int>()

    private var currentEmoteSize: Emote.Size = Emote.Size.MEDIUM
    private var usePronouns: Boolean = false

    private val logger = LoggerWithTag("ChatHookProvider")

    override fun onAllComponentStopped() {}
    override fun onAccountLogout() {}
    override fun onFirstActivityStarted() {}
    override fun onConnectedToChannel(channelId: Int) {}

    private fun injectBadges(
        badges: List<MessageBadgeViewModel>,
        userId: Int,
        channelId: Int
    ): List<MessageBadgeViewModel> {
        if (!badgeProvider.hasBadges(userId = userId)) {
            return badges
        }

        val newBadges = badgeProvider.getBadges(userId = userId).toMutableList()
        if (newBadges.isEmpty()) {
            return badges
        }

        val stack = mutableListOf<MessageBadgeViewModel>()
        badges.forEach { badge ->
            val replaces = newBadges.firstOrNull { it.getReplaces() == badge.name }

            if (replaces != null) {
                stack.add(
                    MessageBadgeViewModel(
                        replaces.getCode(),
                        replaces.getUrl()
                    )
                )
                newBadges.remove(replaces)
            } else {
                stack.add(badge)
            }
        }
        stack.addAll(newBadges.map {
            MessageBadgeViewModel(
                it.getCode(),
                it.getUrl()
            )
        })

        return stack
    }

    private fun injectEmotes(
        tokens: List<MessageTokenV2>,
        userId: Int,
        channelId: Int
    ): List<MessageTokenV2> {
        if (tokens.isEmpty() || !tokens.any { it is MessageTokenV2.TextToken }) {
            return tokens
        }

        val stack = ArrayList<MessageTokenV2>(tokens.size * 2)
        val emoteCache = HashMap<String, Emote?>()

        tokens.forEach { token ->
            when (token) {
                is MessageTokenV2.TextToken -> {
                    val st = StringTokenizer(token.text, " \n\r\t")
                    var isStartFromSpace = token.text.startsWith(" ")
                    while (st.hasMoreTokens()) {
                        if (isStartFromSpace) {
                            stack.add(MessageTokenV2.TextToken(" "))
                            isStartFromSpace = false
                        }
                        val word = st.nextToken()
                        val emote = emoteCache.getOrPut(word) {
                            emoteProvider.getEmote(code = word, channelId = channelId)
                        }

                        if (emote != null) {
                            val spaceToken = MessageTokenV2.TextToken(" ")
                            stack.add(
                                PurpleTVEmoteToken.fromEmote(emote, currentEmoteSize)
                            )
                            stack.addAll(listOf(spaceToken))
                        } else {
                            stack.add(MessageTokenV2.TextToken("$word "))
                        }
                    }
                }

                is MentionToken, is EmoteToken -> {
                    stack.add(token)
                    stack.add(MessageTokenV2.TextToken(" "))
                }

                else -> {
                    stack.add(token)
                }
            }
        }

        return stack
    }

    private fun combineZwEmoteTokens(tokens: Collection<MessageTokenV2>): MutableList<MessageTokenV2> {
        val newTokens = mutableListOf<MessageTokenV2>()

        var stack: PurpleTVEmoteToken? = null
        for (token in tokens) {
            when (token) {
                is MessageTokenV2.TextToken -> {
                    if (!token.text.isNullOrBlank()) {
                        stack = null
                    }
                    newTokens.add(token)
                }

                is PurpleTVEmoteToken -> {
                    if (!token.isZW) {
                        stack = PurpleTVEmoteToken(token.id, token.text)
                        newTokens.add(stack)
                    } else {
                        stack?.subEmotes?.add(token) ?: newTokens.add(token)
                    }
                }

                is EmoteToken -> {
                    stack = PurpleTVEmoteToken(token.id, token.text, isTwitchEmote = true)
                    newTokens.add(stack)
                }

                else -> {
                    stack = null
                    newTokens.add(token)
                }
            }
        }

        LoggerImpl.debugObject(newTokens)

        return newTokens
    }

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    private fun injectThirdPartyUISets(
        map: Flowable<Pair<EmoteUiSet, MutableList<EmoteUiSet>>>,
        channelId: Integer?
    ): Flowable<Pair<EmoteUiSet, MutableList<EmoteUiSet>>> {
        return map.map { pair ->
            emoteProvider.getEmotesMap(channelId = channelId?.toInt() ?: 0)
                .filter { it.second.isNotEmpty() }
                .forEach { emotePair ->
                    pair.second.add(
                        EmoteUiSet(
                            EmoteHeaderUiModel.EmoteHeaderStringResUiModel(
                                (emotePair.first.resTitleName
                                    ?: "purpletv_unknown_emotes").fromResToStringId(),
                                true,
                                EmotePickerSection.PURPLETV,
                                false
                            ), emotePair.second.map { emote ->
                                ChatFactory.createEmoteUiModel(
                                    emote = emote,
                                    channelId = channelId?.toInt() ?: 0,
                                    isAnimated = emote.isAnimated(),
                                    packageSet = emote.getPackageSet()
                                )
                            })
                    )
                }

            return@map pair
        }
    }

    private fun mapDBEmotesToUiSet(
        entities: List<FavoriteEmote>,
        channelId: Int
    ): List<EmoteUiModel> {
        return entities.filter {
            it.channelId == channelId
        }.mapNotNull {
            if (it.isValidTwitchEmote()) {
                return@mapNotNull ChatFactory.createFavEmoteUiModel(
                    emoteId = it.id ?: "0",
                    emoteToken = it.code,
                    isAnimated = it.isAnimated,
                    channelId = channelId,
                    packageSet = it.packageSet
                )
            } else {
                val emote = emoteProvider.getEmoteFromPackageSet(
                    code = it.code,
                    channelId = it.channelId,
                    emotePackageSet = it.packageSet
                ) ?: return@mapNotNull null

                return@mapNotNull ChatFactory.createPurpleTVFavEmoteUiModel(
                    emote.getUrl(Emote.Size.LARGE),
                    emoteCode = it.code,
                    emoteId = "0",
                    channelId = it.channelId,
                    isAnimated = it.isAnimated,
                    packageSet = it.packageSet
                )
            }
        }
    }


    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    fun hookEmoteSetsFlowable(
        map: Flowable<Pair<EmoteUiSet, MutableList<EmoteUiSet>>>,
        channelId: Integer?
    ): Flowable<Pair<EmoteUiSet, MutableList<EmoteUiSet>>> {
        return injectThirdPartyUISets(map, channelId)
            .doOnError {
                logger.debug { "error -> $it" }
            }.flatMap { pair ->
                favEmotesRepository.getEmotesForChannel(channelId = channelId?.toInt() ?: 0)
                    .observeOn(
                        scheduler
                    ).map {
                        mapDBEmotesToUiSet(it, channelId?.toInt() ?: 0)
                    }.map {
                        ChatFactory.createFavEmoteUiSet(it)
                    }.observeOn(AndroidSchedulers.mainThread()).map { set: EmoteUiSet ->
                        if (set.emotes.isNotEmpty()) {
                            pair.second.find {
                                it.header.emotePickerSection.equals(
                                    EmotePickerSection.PURPLETV_FAV
                                )
                            }
                                ?.let {
                                    pair.second.remove(it)
                                }
                            if (set.emotes.isNotEmpty()) {
                                pair.second.add(0, set)
                            }
                        }
                        pair
                    }.toFlowable()
            }
    }

    fun hookAutoCompleteMapProvider(emotesFlow: Flowable<List<EmoteSet>>): Flowable<List<EmoteSet>> {
        return emotesFlow.flatMap { orgList ->
            if (orgList.isEmpty()) {
                return@flatMap Flowable.empty()
            }

            currentChannelSubject.flatMap {
                Observable.just(it)
                    .delay(DELAY_BEFORE_INJECT, TimeUnit.SECONDS, Schedulers.single())
            }.toFlowable(BackpressureStrategy.LATEST).flatMap { channelId ->
                val newSets = emoteProvider.getEmotesMap(channelId = channelId).map { pair ->
                    PurpleTVEmoteSet(emotes = pair.second.map {
                        PurpleTVEmoteModel(
                            emoteToken = it.getCode(),
                            emoteUrl = it.getUrl(Emote.Size.LARGE)
                        )
                    })
                }
                Flowable.just((orgList + newSets))
            }
        }
    }

    fun hookMediaSpanSizeDp(sizeDp: Float): Float {
        if (fontSizeScaleFactory != 0F && fontSizeScaleFactory != 1F) {
            return sizeDp * fontSizeScaleFactory
        }

        return sizeDp
    }

    companion object {
        private const val DELAY_BEFORE_INJECT = 2L

        var fontSizeSp: Int = 0
        var fontSizePx: Float = 0F
        var fontSizeScaleFactory: Float = 0F

        var useCustomTimestamps: Boolean = false
        var alternatingBackground: Boolean = false

        private var killChat = false

        @JvmStatic
        fun bypassChatBan(): Boolean {
            return Flag.BYPASS_CHAT_BAN.asBoolean()
        }

        @JvmStatic
        fun hook(
            messageId: String?,
            message: Spanned?,
            context: Context?,
            messageClickEventDispatcher: EventDispatcher<ChatMessageClickedEvents>,
            hasModAccess: Boolean
        ): Spanned? {
            return message
        }

        private fun calcFontSizeScale(fontSizeSp: Int): Float {
            return fontSizeSp.div(Flag.CHAT_FONT_SIZE.asIntRange().getDefaultValue().toFloat())
        }

//        @JvmStatic
//        fun fixUsernameSpanColor(usernameColor: (Context) -> Int): (Context) -> Int {
//            return {
//                ChatUtil.fixUsernameColor(
//                    color = usernameColor.,
//                    isDarkThemeEnabled = PrefManager.isDarkThemeEnabled
//                )
//            }
//        }

        @JvmStatic
        fun fixDeletedMessage(
            ret: MessageRecyclerItem?
        ) {
            ret ?: return

            if (ret.getPrivateField("hasBeenDeleted")) {
                LoggerImpl.debug("Fix deleted message: $ret")
                ret.markAsDeleted()
            }
        }

        @JvmStatic
        val isChatKilled
            get() = killChat

        @JvmStatic
        fun hookRaidsConfiguration(org: RaidsConfiguration?): RaidsConfiguration? {
            return org;

//            if (Flag.RAIDS.asBoolean()) {
//                return org
//            }
//
//            return RaidsConfiguration(false, false)
        }

        @JvmStatic
        fun hookCreatorPinnedChatMessageModel(model: CreatorPinnedChatUiModel?): CreatorPinnedChatUiModel? {
            model ?: return null // fix crash when model is null

            val delay = 30 * 1000
            val chatTime = model.time
            val hookedUnpinnedMs = CoreDateUtil().currentTimeInMillis + delay

            val hookedChatTime = CreatorPinnedChatTime(
                chatTime.timeMessageSentMS,
                chatTime.timeMessagePinnedMS,
                model.time.timeMessageUnpinnedMS?.let { unpinnedMS ->
                    if (unpinnedMS - hookedUnpinnedMs - delay <= 0) {
                        unpinnedMS
                    } else {
                        hookedUnpinnedMs
                    }
                } ?: hookedUnpinnedMs
            )

            return when (Flag.PINNED_MESSAGE.asVariant<PinnedMessageStrategy>()) {
                PinnedMessageStrategy.Default -> model
                PinnedMessageStrategy.Disabled -> CreatorPinnedChatUiModel(
                    model.common,
                    model.time,
                    model.pinnedByUser,
                    PinnedChatMessageState.Terminated
                )

                PinnedMessageStrategy.SEC30 -> CreatorPinnedChatUiModel(
                    model.common,
                    hookedChatTime,
                    model.pinnedByUser,
                    model.state
                )
            }
        }

        @JvmStatic
        fun hookDisplayName(cmi: ChatMessageInterface?): ChatMessageInterface? {
            cmi ?: return null

            return when (Flag.DISPLAY_NAME.asVariant<DisplayName>()) {
                DisplayName.InternationalUsername -> cmi
                DisplayName.International -> ChatMessageInterfaceWrapper(
                    displayName = getInternationalDisplayName(
                        cmi.displayName
                    ),
                    cmi = cmi
                )

                DisplayName.Username -> ChatMessageInterfaceWrapper(
                    displayName = cmi.userName,
                    cmi = cmi
                )
            }
        }

        private fun getInternationalDisplayName(displayName: String?): String? {
            displayName ?: return null

            return if (displayName.contains(" ")) {
                displayName.split(" ")[0]
            } else {
                displayName
            }
        }

        @JvmStatic
        @Deprecated("remove")
        fun parseClearChatChatEvent(components: IrcEventComponents): ChannelEvent {
            return ChannelEvent.ChatChannelMessagesCleared.INSTANCE
        }

        private fun isBanMessage(components: IrcEventComponents?): Boolean {
            components ?: return false

            return components.command == IrcCommand.CLEARCHAT && components.content.isNotBlank() && !components.messageTags.containsKey(
                "ban-duration"
            )
        }

        private fun isTimeoutMessage(components: IrcEventComponents?): Boolean {
            components ?: return false

            return components.messageTags.containsKey("ban-duration")
        }

        @JvmStatic
        fun hideUrl(): Boolean {
            return Flag.DELETED_MESSAGES_STRATEGY.asVariant<DeletedMessages>().isDefault()
        }

        @JvmStatic
        private fun getLocalizedTimeoutMessage(
            context: Context,
            userName: String,
            timeout: Int
        ): String {
            return ChatNoticeExtensionsKt.getSystemMessageText(
                ChatModNoticeEvents.UserTimedOutEvent(
                    userName,
                    timeout
                )
            ).getString(context)
        }

        @JvmStatic
        private fun getLocalizedBanMessage(
            context: Context,
            userName: String
        ): String {
            return ChatNoticeExtensionsKt.getSystemMessageText(
                ChatModNoticeEvents.UserBannedEvent(
                    userName
                )
            ).getString(context)
        }

        @JvmStatic
        fun recyclePronounSetter(pronounSetter: PronounSetter?) {
            pronounSetter?.destroy()
        }

        @JvmStatic
        fun getNameColorARGB(messageInfo: ChatMessageInfo): Int {
            return messageInfo.userInfo.`nameColorARGB-0hXNFcg`.toInt()
        }
    }

    private fun updateFontSize() {
        fontSizeSp = Flag.CHAT_FONT_SIZE.asIntRange().getValue()
        fontSizePx = context.spToPx(fontSizeSp)
        fontSizeScaleFactory = calcFontSizeScale(fontSizeSp = fontSizeSp)
        logger.debug { "fontSizeSp=$fontSizeSp, fontSizePx=$fontSizePx, fontSizeScaleFactory=$fontSizeScaleFactory" }
    }

    override fun onAllComponentDestroyed() {
        pronounProvider.destroy()
    }

    override fun onFirstActivityCreated() {
        badgeProvider.fetch()
        emoteProvider.fetch()
        pronounProvider.updatePronounsMap()
    }

    override fun onConnectingToChannel(channelId: Int) {
        emoteProvider.refreshEmotes(channelId)
        currentChannelSubject.onNext(channelId)
    }

    override fun onAccountLogin(tam: TwitchAccountManager) {}

    override fun onFlagValueChanged(flag: Flag) {
        when (flag) {
            Flag.BTTV_EMOTES -> {
                emoteProvider.setConfiguration(
                    emoteProvider.getConfiguration().copy(bttv = flag.asBoolean())
                )
            }

            Flag.FFZ_EMOTES -> {
                emoteProvider.setConfiguration(
                    emoteProvider.getConfiguration().copy(ffz = flag.asBoolean())
                )
            }

            Flag.STV_EMOTES -> {
                emoteProvider.setConfiguration(
                    emoteProvider.getConfiguration().copy(stv = flag.asBoolean())
                )
            }

            Flag.STV_GLOBAL_EMOTES -> {
                emoteProvider.setConfiguration(
                    emoteProvider.getConfiguration().copy(stvGlobal = flag.asBoolean())
                )
            }

            Flag.HOMIES_EMOTES -> {
                emoteProvider.setConfiguration(
                    emoteProvider.getConfiguration().copy(homies = flag.asBoolean())
                )
            }

            Flag.HOMIES_BADGES,
            Flag.FFZ_BADGES,
            Flag.CHA_BADGES,
            Flag.PTV_BADGES -> badgeProvider.rebuild()

            Flag.CHAT_FONT_SIZE -> updateFontSize()
            Flag.EMOTE_QUALITY -> currentEmoteSize = flag.asVariant<EmoteQuality>().toSize()
            Flag.TIMESTAMPS -> useCustomTimestamps =
                flag.asVariant<Timestamps>() != Timestamps.DEFAULT

            Flag.ALTERNATING_BACKGROUND -> alternatingBackground = flag.asBoolean()
            Flag.PRONOUNS -> usePronouns = flag.asBoolean()

            else -> {}
        }
    }

    fun setShouldHighlightBackground(
        message: IMessageRecyclerItem,
        liveChatMessage: ChatMessage.LiveChatMessage
    ) {
        if (highlighter.isEnabled()) {
            highlighter.getHighlightDesc(liveChatMessage)?.let { desc ->
                message.setHighlightColor(desc.color)
                if (desc.vibrate) {
                    vibrate()
                }
                return
            }
        }

        if (isUserMentioned(
                liveChatMessage = liveChatMessage,
                username = twitchAccountManager.username
            )
        ) {
            message.setHighlightColor(Flag.USER_MENTION_COLOR.asInt())
            if (Flag.VIBRATE_ON_MENTION.asBoolean()) {
                vibrate()
            }
        }
    }

    private fun vibrate() {
        CoreUtil.vibrate(
            context = context,
            delay = 200,
            duration = Flag.VIBRATION_DURATION.asInt()
        )
    }

    override fun maybeChangeMessageFontSize(textView: TextView?) {
        textView ?: return

        if (fontSizeScaleFactory == 1F) {
            return
        }

        if (fontSizeSp == 0 || fontSizeScaleFactory == 0F) {
            updateFontSize()
        }

        if (textView.textSize == fontSizePx) {
            return
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizePx)
    }

    override fun bindHighlightMessage(
        viewHolder: ViewHolder?,
        highlightColor: Int?,
        abColor: Int?
    ) {
        viewHolder ?: return

        highlightColor?.let { color ->
            viewHolder.itemView.setBackgroundColor(color)
        } ?: run {
            abColor?.let { color ->
                viewHolder.itemView.setBackgroundColor(color)
            } ?: run {
                viewHolder.itemView.background = null
            }
        }
    }

    fun onBindToViewHolder(
        viewHolder: ViewHolder?,
        item: RecyclerAdapterItem?
    ) {
        BindBridge.onBindToViewHolder(viewHolder, item, this)
    }

    fun bindPronoun(
        holder: ChatMessageViewHolder?,
        item: RecyclerAdapterItem?
    ): PronounSetter? {
        holder ?: return null
        item ?: return null

        if (!usePronouns) {
            return null
        }

        if (item !is MessageRecyclerItem) {
            return null
        }

        val userName = item.getPrivateField<String?>("username") ?: return null
        val setter = PronounSetter(view = holder)

        pronounProvider.fetchPronounText(userName = userName) { pronounText: String ->
            setter.setText(pronounText)
        }

        return setter
    }

    fun hookEmotePickerPresenterLongEmoteClick(
        clickEvent: EmoteClickedEvent,
        emotePickerPresenter: IEmotePickerPresenter
    ): Boolean {
        if (clickEvent !is EmoteClickedEvent.LongClick) {
            return false
        }

        val clickedEmote = clickEvent.getClickedEmote()
        if (clickedEmote !is ClickedEmote.Unlocked) {
            return false
        }

        val model = clickedEmote.emote ?: return false

        if (model is PurpleTVEmotePickerEmoteModel && model.isFavoriteEmote) {
            favEmotesRepository.deleteEmote(
                emote = model
            )
            Toast.makeText(context, "Removed: ${model.token}", Toast.LENGTH_SHORT).show()
        } else {
            val channelId =
                clickedEmote.trackingMetadata?.chatChannelId ?: currentChannelSubject.value ?: run {
                    Toast.makeText(context, "Can't add emote: channelId is null", Toast.LENGTH_LONG)
                        .show()
                    return true
                }

            val favEmote = FavoriteEmote(
                code = model.token,
                channelId = channelId,
                packageSet = if (model is PurpleTVEmotePickerEmoteModel) {
                    model.packageSet
                } else {
                    EmotePackageSet.TwitchChannel
                },
                id = model.id,
                isAnimated = model.assetType == EmoteModelAssetType.ANIMATED
            )
            logger.debug { "favEmote: $favEmote" }
            favEmotesRepository.addEmote(favEmote)
            if (emotePickerPresenter is IEmotePickerPresenter) {
                LoggerImpl.debug("Try update emotes")
                emotePickerPresenter.notifyFavEmotesChanged();
            }
            Toast.makeText(context, "Added: ${model.token}", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    fun switchKillChatState() {
        killChat = !killChat
    }

    fun filterMessages(flowable: Flowable<MessagesReceivedEvent>): Flowable<MessagesReceivedEvent> {
        if (!blacklist.isEnabled()) {
            return flowable
        }

        return flowable.map { mre ->
            MessagesReceivedEvent(
                mre.channelId,
                mre.messages.filter { message -> !blacklist.isBlacklisted(message) },
                mre.getPrivateField("fromHistory")
            )
        }.filter { it.messages.isNotEmpty() }
    }

    fun hookLiveChatMessage(
        msg: ChatMessage.LiveChatMessage,
        channelId: Int
    ): ChatMessage.LiveChatMessage {
        val badges = injectBadges(
            badges = msg.senderBadges,
            userId = msg.sender.userId,
            channelId = channelId
        )
        val tokens = combineZwEmoteTokens(
            injectEmotes(
                tokens = msg.messageTokens,
                userId = msg.sender.userId,
                channelId = channelId
            )
        )

        return ChatMessage.LiveChatMessage(
            msg.messageId,
            msg.messageType,
            msg.getPrivateField("messageTags"),
            badges,
            msg.senderColor,
            msg.sender,
            tokens,
            msg.gigantifiedEmoteToken,
            msg.timestampSeconds,
            msg.isDeleted,
            msg.isDeletedMessageClickable,
            msg.isAction,
            msg.isMessageEffectAnimationsEnabled,
            msg.clipSlug,
            msg.replyInfo,
            msg.sourceInfo
        )
    }

    fun init() {
        updateFontSize()
        currentEmoteSize = Flag.EMOTE_QUALITY.asVariant<EmoteQuality>().toSize()
        usePronouns = Flag.PRONOUNS.asBoolean()
        useCustomTimestamps = Flag.TIMESTAMPS.asVariant<Timestamps>() != Timestamps.DEFAULT
        alternatingBackground = Flag.ALTERNATING_BACKGROUND.asBoolean()
        highlighter.pull()
        blacklist.pull()
        emoteProvider.setConfiguration(
            Configuration(
                bttv = Flag.BTTV_EMOTES.asBoolean(),
                ffz = Flag.FFZ_EMOTES.asBoolean(),
                stv = Flag.STV_EMOTES.asBoolean(),
                stvGlobal = Flag.STV_GLOBAL_EMOTES.asBoolean(),
                homies = Flag.HOMIES_EMOTES.asBoolean()
            )
        )
    }

    fun forceUpdateEmotesAndBadges() {
        emoteProvider.rebuild()
        badgeProvider.rebuild()
    }

    fun isMessageInBlacklist(merge: ChatMessage): Boolean {
        if (merge !is ChatMessage.LiveChatMessage) {
            return false
        }

        return blacklist.isBlacklisted(merge)
    }
}
