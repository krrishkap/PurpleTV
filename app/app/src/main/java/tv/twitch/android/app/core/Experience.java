package tv.twitch.android.app.core;

import android.content.Context;

import tv.purple.monolith.features.ui.UI;
import tv.purple.monolith.models.exception.VirtualImpl;

public class Experience {

    public enum SupportedExperiences {
        Phone,
        Tablet,
        Chromebook
    }

    public final boolean shouldShowTabletUI(Context context) {
        boolean res = false;

        res = UI.hookIsTablet(res); // TODO: __INJECT_CODE

        throw new VirtualImpl();
    }
}
