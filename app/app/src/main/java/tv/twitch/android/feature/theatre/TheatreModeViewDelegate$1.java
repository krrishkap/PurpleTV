package tv.twitch.android.feature.theatre;

import android.graphics.Rect;

import tv.purple.monolith.core.bridge.CoreHook;

public class TheatreModeViewDelegate$1 {
    public final Rect invoke() {
        Rect res = null;
        if (CoreHook.isPlayerDraggableDisabled()) { // TODO: __INJECT_CODE
            res = new Rect(0, 0, 0, 0);
        }

        return res;
    }
}