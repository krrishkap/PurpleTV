package tv.twitch.android.shared.player.overlay;

import tv.purple.monolith.features.timer.bridge.TimerHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.shared.player.overlay.databinding.PlayerControlOverlayBinding;

public class RxPlayerOverlayHeaderViewDelegate {
    private final PlayerControlOverlayBinding viewBinding = null;

    /* ... */

    private final void showPlayerOverlayHeader(PlayerOverlayHeaderViewModel playerOverlayHeaderViewModel) {
        /* ... */

        if (viewBinding != null) {
            TimerHook.maybeShowTimerButton(viewBinding.timerButton); // TODO: __INJECT_CODE
        }

        throw new VirtualImpl();
    }

    private final void hidePlayerOverlayOptions() {
        /* ... */

        if (viewBinding != null) {
            TimerHook.maybeHideTimerButton(viewBinding.timerButton); // TODO: __INJECT_CODE
        }

        throw new VirtualImpl();
    }

    /* ... */
}