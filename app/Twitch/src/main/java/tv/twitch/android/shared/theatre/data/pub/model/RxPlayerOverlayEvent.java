package tv.twitch.android.shared.theatre.data.pub.model;

import kotlin.jvm.internal.DefaultConstructorMarker;

public class RxPlayerOverlayEvent {
    public RxPlayerOverlayEvent(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    private RxPlayerOverlayEvent() {
    }

    /* ... */

    public static final class RefreshClicked extends RxPlayerOverlayEvent { // TODO: __INJECT_CLASS
        public static final RefreshClicked INSTANCE = new RefreshClicked();

        private RefreshClicked() {
            super(null);
        }
    }

    /* ... */
}

