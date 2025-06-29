package tv.twitch.android.shared.chat.messages.ui;

import android.content.Context;
import android.text.Spanned;

import kotlin.jvm.functions.Function1;
import tv.purple.monolith.features.chat.bridge.ChatHook;
import tv.purple.monolith.models.exception.VirtualImpl;

public class DefaultMessageViewHolder {
    private final void renderMessageTextContent(MessageRecyclerItem messageRecyclerItem, boolean z) {
        /* ... */

        if (true) {
            Spanned msg = ChatHook.hookCreateDeletedSpanFromChatMessageSpan("", null, null, null, false); // TODO: __PATCH_CODE
        }

        /* ... */

        MessageRecyclerItem message = null;
        Spanned spanned = null;
        if (spanned == null) {
            Function1 function1 = null;
            Context context = null;
            spanned = (Spanned) function1.invoke(context);
            spanned = ChatHook.maybeAddTimestamp(spanned, message); // TODO: __INJECT_CODE
        }

        // this.loadedImageTargets = GlideHelper.loadImagesFromSpannedAndGetTargets(context4, updatedMessage$shared_chat_messages_release, getMessageTextView());
        // getMessageTextView().setText(updatedMessage$shared_chat_messages_release, TextView.BufferType.SPANNABLE);

        /* ... */

        throw new VirtualImpl();
    }
}
