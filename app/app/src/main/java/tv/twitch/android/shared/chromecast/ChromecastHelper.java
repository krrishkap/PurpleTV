package tv.twitch.android.shared.chromecast;

import tv.purple.monolith.core.bridge.CoreHook;
import tv.purple.monolith.models.exception.VirtualImpl;

public class ChromecastHelper {
    /* ... */

    public boolean showChromecastUi() {
        if (CoreHook.disableChromecast()) { // TODO: __INJECT_CODE
            return false;
        }

        /* ... */

        throw new VirtualImpl();
    }

    /* ... */
}
