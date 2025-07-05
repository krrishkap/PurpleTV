package tv.twitch.android.feature.viewer.main;

import android.os.Bundle;
import android.view.ViewGroup;

import tv.purple.monolith.features.ui.UI;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.core.activities.TwitchDaggerActivity;

public class MainActivity extends TwitchDaggerActivity {
    private ViewGroup mWrapper;

    /* ... */

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        /* ... */

        this.mWrapper = null;
        UI.attachToMainActivityWrapper(this.mWrapper); // TODO: __INJECT_CODE

        /* ... */

        throw new VirtualImpl();
    }

    /* ... */
}
