package tv.twitch.android.shared.player.overlay;

import android.view.View;

import tv.purple.monolith.features.timer.bridge.TimerHook;
import tv.purple.monolith.features.ui.bridge.UIHook;
import tv.twitch.android.shared.player.overlay.databinding.PlayerControlOverlayBinding;

public class RxPlayerOverlayHeaderViewDelegate {
    private final PlayerControlOverlayBinding viewBinding = null;


    /* ... */

    private final void showPlayerOverlayHeader(PlayerOverlayHeaderViewModel playerOverlayHeaderViewModel) {
        /* ... */

        UIHook.maybeHideOverlayHeaderButtons(viewBinding.createClipButton, viewBinding.shareButton); // TODO: __INJECT_CODE
        TimerHook.maybeShowTimerButton(viewBinding.timerButton); // TODO: __INJECT_CODE
    }

    private final void hidePlayerOverlayOptions() {
        /* ... */

        viewBinding.timerButton.setVisibility(View.GONE); // TODO: __INJECT_CODE
    }

    private final void resolveClipButton(PlayerOverlayHeaderViewModel.PlayerOverlayOptions playerOverlayOptions) {
        /* ... */

        UIHook.maybeHideCreateClipButton(viewBinding.createClipTextButton); // TODO: __INJECT_CODE
    }

    /* ... */
}