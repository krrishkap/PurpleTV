package tv.twitch.android.shared.messageinput.impl.autocomplete;

import tv.purple.monolith.features.chat.bridge.ChatHook;
import tv.purple.monolith.models.exception.VirtualImpl;

public class EmoteAutoCompleteRule {
    /* ... */

    public int findTokenStart(CharSequence charSequence) {
        char startToken = ':';
        if (ChatHook.hookStartToken()) { // TODO: __INJECT_CODE
            throw new VirtualImpl();
        }

        throw new VirtualImpl();
    }

    /* ... */
}
