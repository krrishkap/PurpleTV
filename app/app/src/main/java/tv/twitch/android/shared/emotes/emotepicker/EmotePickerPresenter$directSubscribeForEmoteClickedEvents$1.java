package tv.twitch.android.shared.emotes.emotepicker;

import tv.purple.monolith.features.chat.bridge.ChatHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.shared.emotes.emotepicker.models.EmoteClickedEvent;

public class EmotePickerPresenter$directSubscribeForEmoteClickedEvents$1 {
    EmotePickerPresenter thos;

    /* ... */

    public final void invoke(EmoteClickedEvent clickEvent) {
        if (ChatHook.hookEmotePickerPresenterLongEmoteClick(clickEvent)) { // TODO: __INJECT_CODE
            return;
        }

        /* ... */

        throw new VirtualImpl();
    }

    /* ... */
}