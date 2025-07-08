package tv.twitch.android.shared.player.overlay;

import tv.purple.monolith.features.ui.bridge.UIHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.shared.clip.editor.network.data.ClipCreationPermissionEligibilityResponse;

public class CreateClipButtonViewModel {
    /* ... */

    private final boolean resolveIsClipOptionVisible(ClipCreationPermissionEligibilityResponse clipCreationPermissionEligibilityResponse, boolean z) {
        if (UIHook.hideCreateClipButton()) { // TODO: __INJECT_CODE
            return false;
        }

        throw new VirtualImpl();
    }

    /* ... */
}
