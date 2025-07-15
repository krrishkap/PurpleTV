package tv.twitch.android.feature.theatre.vertical.chat;

import kotlin.jvm.internal.Intrinsics;
import tv.purple.monolith.features.chat.bridge.ChatHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.feature.theatre.vertical.VerticalTheatreFragment;

public class VerticalTheatreChat {
    /* ... */

    private static void handleChatMessageAction(final VerticalTheatreFragment verticalTheatreFragment, final VerticalTheatreChatMessagesComponent.ViewAction viewAction) {
        if (Intrinsics.areEqual(viewAction, VerticalTheatreChatMessagesComponent.ViewAction.ClearAllMessages.INSTANCE)) {
            if (!ChatHook.preventModClear()) { // TODO: __INJECT_CODE
                verticalTheatreFragment.getChatMessagesAdapterBinder$feature_theatre_vertical_release().clearMessages();
            }
            return;
        }

        throw new VirtualImpl();
    }

    /* ... */
}
