package tv.twitch.android.shared.chat.messages.ui;

import android.content.Context;
import android.text.Spanned;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import kotlin.jvm.functions.Function1;
import tv.purple.monolith.bridge.ex.IMessageRecyclerItem;
import tv.purple.monolith.component.pronouns.PronounSetter;
import tv.purple.monolith.features.chat.ChatHookProvider;
import tv.purple.monolith.features.chat.bridge.ChatHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.core.adapters.RecyclerAdapterItem;
import tv.twitch.android.core.adapters.ViewHolderGenerator;
import tv.twitch.android.core.mvp.viewdelegate.EventDispatcher;
import tv.twitch.android.core.strings.StringResource;
import tv.twitch.android.shared.chat.pub.messages.ui.CensorContentChatAdapterItem;
import tv.twitch.android.shared.chat.pub.messages.ui.ChatItemClickEvent;
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageClickedEvents;
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageListStyle;
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageType;
import tv.twitch.android.shared.chat.pub.messages.ui.DeletableChatAdapterItem;
import tv.twitch.android.shared.chat.pub.messages.ui.ModActionableAdapterItem;

public class MessageRecyclerItem implements RecyclerAdapterItem, DeletableChatAdapterItem, CensorContentChatAdapterItem, ModActionableAdapterItem, IMessageRecyclerItem { // TODO: __IMPLEMENT
    private final Integer authorUserId;
    private final EventDispatcher<ChatItemClickEvent> clickEventDispatcher;
    private final String displayName;
    private boolean hasBeenDeleted;
    private boolean hasModAccess;
    private boolean isMessageEffectAnimationsEnabled;
    private boolean isUncensored;
    private float lineSpacingMultiplier;
    private final EventDispatcher<ChatMessageClickedEvents> messageClickEventDispatcher;
    private final boolean messageEffectsRenderingEnabled;
    private final StringResource messageHeader;
    private final String messageId;
    private final MessageItemLayoutConfig messageItemLayoutConfig;
    private final Function1<Context, Spanned> messageSpanCreator;
    private final Long messageTimestamp;
    private final ChatMessageType messageType;
    //private final SystemMessageType msgType;
    private int paddingPx;
    private final String rawMessage;
    private float textSizePx;
    private Spanned updatedMessage;
    private final String username;
    private final Integer viewerUserId;
    private Integer highlightColor; // TODO: __INJECT_FIELD
    private Integer abColor; // TODO: __INJECT_FIELD

    /* ... */

    public MessageRecyclerItem(String str, int i, String str2, String str3, int i2, Function1<? super Context, ? extends Spanned> messageSpanCreator/*, SystemMessageType systemMessageType */, float f, int i3, float f2, boolean z, boolean z2, String str4, EventDispatcher<ChatItemClickEvent> eventDispatcher, EventDispatcher<ChatMessageClickedEvents> eventDispatcher2) {
        /* ... */

        throw new VirtualImpl();
    }

    @Override
    public String getItemId() {
        throw new VirtualImpl();
    }

    @Override
    public Long getTimeStamp() {
        throw new VirtualImpl();
    }

    @Override
    public Integer getUserId() {
        throw new VirtualImpl();
    }

    @Override
    public void setMessageListStyle(ChatMessageListStyle chatMessageListStyle) {
        throw new VirtualImpl();
    }

    @Override
    public void updateModAccess(boolean b) {
        throw new VirtualImpl();
    }

    public void markAsDeleted() {
        this.hasBeenDeleted = true;
    }

    @Override
    public void updateDeletedMessage(CharSequence charSequence) {
        throw new VirtualImpl();
    }

    @Override
    public void setHighlightColor(@Nullable Integer highlightColor) { // TODO: __INJECT_METHOD
        this.highlightColor = highlightColor;
    }

    @Nullable
    @Override
    public Integer getHighlightColor() { // TODO: __INJECT_METHOD
        return highlightColor;
    }

    @Nullable
    @Override
    public Integer getAlternatingBackground() { // TODO: __INJECT_METHOD
        return abColor;
    }

    @Override
    public void setAlternatingBackground(@Nullable Integer color) { // TODO: __INJECT_METHOD
        abColor = color;
    }

    @Override
    public void bindToViewHolder(RecyclerView.ViewHolder viewHolder) {
        throw new VirtualImpl();
    }

    @Override
    public int getViewHolderResId() {
        throw new VirtualImpl();
    }

    @Override
    public RecyclerAdapterItem nestedItem() {
        throw new VirtualImpl();
    }

    @Override
    public ViewHolderGenerator newViewHolderGenerator() {
        throw new VirtualImpl();
    }

    @Override
    public boolean skipForAlternatingBackground() { // TODO: __INJECT_METHOD
        if (authorUserId == null) {
            return true;
        }

        return authorUserId == 0 || authorUserId == -1;
    }

    @Override
    public void revealCensoredText() {
        throw new VirtualImpl();
    }

    private static final class ViewHolder extends ChatMessageViewHolder {
        private PronounSetter pronounSetter = null;

        /* ... */

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void onBindDataItem(RecyclerAdapterItem item) {
            super.onBindDataItem(item);

            if (pronounSetter != null) { // TODO: __INJECT_CODE
                pronounSetter.destroy();
            }

            /* ... */

            MessageRecyclerItem message = (MessageRecyclerItem) item;
            Spanned spanned = message.updatedMessage;
            if (spanned == null) {
                Function1 function1 = message.messageSpanCreator;
                Context context = this.itemView.getContext();
                spanned = (Spanned) function1.invoke(context);
                spanned = ChatHook.maybeAddTimestamp(spanned, message); // TODO: __INJECT_CODE
            }

            /* ... */

            // revealCensoredMessageSpans = companion.createDeletedSpanFromChatMessageSpan(str, spanned, context3, messageRecyclerItem.messageClickEventDispatcher, messageRecyclerItem.hasModAccess);
            Spanned msg = ChatHook.hookCreateDeletedSpanFromChatMessageSpan("", null, null, null, false); // TODO: __PATCH_CODE

            // pronounSetter = TwitchRecyclerViewHolder.bindPronoun(this, item); // TODO: __INJECT_CODE
        }

        @Override
        public void onRecycled() {
            super.onRecycled();

            ChatHookProvider.recyclePronounSetter(pronounSetter); // TODO: __INJECT_CODE
            pronounSetter = null; // TODO: __INJECT_CODE

            /* ... */

            throw new VirtualImpl();
        }

        /* ... */
    }

    /* ... */

    public static abstract class MessageItemLayoutConfig {}

    /* ... */
}
