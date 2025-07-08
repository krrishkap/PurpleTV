package tv.twitch.android.shared.player.overlay;

import tv.purple.monolith.features.ui.bridge.UIHook;
import tv.purple.monolith.models.exception.VirtualImpl;

public class PlayerOverlayHeaderViewModel {
    /* ... */

    public static final class PlayerOverlayOptions extends PlayerOverlayHeaderViewModel {
        /* ... */

        public final boolean isShareOptionVisible() {
            if (UIHook.hideShareOption()) {  // TODO: __INJECT_CODE
                return false;
            }

            throw new VirtualImpl();
        }

        /* ... */
    }

    /* ... */
}