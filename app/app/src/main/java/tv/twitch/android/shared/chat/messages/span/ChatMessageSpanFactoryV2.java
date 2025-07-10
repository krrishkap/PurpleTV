package tv.twitch.android.shared.chat.messages.span;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannedString;

import java.util.List;

import tv.purple.monolith.features.chat.bridge.ChatHook;
import tv.purple.monolith.features.chat.bridge.PurpleTVEmoteToken;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.core.mvp.viewdelegate.EventDispatcher;
import tv.twitch.android.models.chat.MessageBadgeViewModel;
import tv.twitch.android.shared.chat.pub.messages.data.ChatMessageUser;
import tv.twitch.android.shared.chat.pub.messages.data.MessageTokenV2;
import tv.twitch.android.shared.chat.pub.messages.ui.ChatItemClickEvent;
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageClickedEvents;
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageSpanGroup;
import tv.twitch.android.shared.chat.pub.messages.ui.ChatMessageUrlOptions;
import tv.twitch.android.shared.chat.pub.messages.ui.IClickableUsernameSpanListener;
import tv.twitch.android.shared.chat.pub.messages.ui.TextStyle;
import tv.twitch.android.shared.ui.elements.span.MediaSpan$Type;
import tv.twitch.android.shared.ui.elements.span.UrlDrawable;

public class ChatMessageSpanFactoryV2 {
    public final ChatMessageSpanGroup createChatMessageSpanGroup(Context context, List<MessageBadgeViewModel> badges, ChatMessageUser sender, Integer num, List<? extends MessageTokenV2> tokens, boolean z10, boolean z11, boolean z12, String str, TextStyle textStyle, boolean z13, ChatMessageUrlOptions options, Long l10, MessageTokenV2.EmoteToken emoteToken, IClickableUsernameSpanListener iClickableUsernameSpanListener, EventDispatcher<ChatItemClickEvent> eventDispatcher, EventDispatcher<ChatMessageClickedEvents> eventDispatcher2) {
        SpannableString spannableString;
        if (l10 != null && ChatHook.getTimestampsDisabled()) { // TODO: __INJECT_CODE
            throw new VirtualImpl();
        } else {
            spannableString = null;
        }

        throw new VirtualImpl();
    }

    private final SpannedString createChatMessageSpan(Context context, String str, Integer num, List<? extends MessageTokenV2> list, TextStyle textStyle, boolean z, ChatMessageUrlOptions chatMessageUrlOptions, EventDispatcher<ChatItemClickEvent> eventDispatcher) {
        throw new VirtualImpl();
    }

    private final CharSequence emoteSpannable(Context context, String str, Integer num, MessageTokenV2.EmoteToken emoteToken, boolean z10, Integer num2, EventDispatcher<ChatItemClickEvent> eventDispatcher) {
        SpannableString spannableString;
        UrlDrawable urlDrawable = new UrlDrawable("", z10 ? MediaSpan$Type.GigantifiedEmote : MediaSpan$Type.Emote);
        String url = null;
        if (emoteToken instanceof PurpleTVEmoteToken) { // TODO: __INJECT_CODE
            url = ChatHook.getPurpleTVEmoteUrl(url, emoteToken);
        }
        ChatHook.preloadPTVEmoteToken(urlDrawable, emoteToken); // TODO: __INJECT_CODE

        /* ... */

        throw new VirtualImpl();
    }

    private final Spannable textSpannable(CharSequence var1, TextStyle var2) {
//        if (var2 instanceof TextStyle.Action) { // TODO: __INJECT_CODE
//            return ChatHook.hookActionMessageStyle(var1, (TextStyle.Action) var2);
//        }

        throw new VirtualImpl();
    }

    private final Spannable textSpannable(MessageTokenV2.TextToken textToken, TextStyle textStyle) {
        throw new VirtualImpl();
    }
}