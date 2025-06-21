package tv.twitch.android.shared.player.overlay;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.compose.ui.platform.ComposeView;
import androidx.mediarouter.app.MediaRouteButton;

import io.reactivex.subjects.PublishSubject;
import tv.purple.monolith.features.chapters.bridge.ChaptersHook;
import tv.purple.monolith.features.timer.bridge.TimerHook;
import tv.purple.monolith.features.ui.bridge.UIHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.core.mvp.viewdelegate.BaseViewDelegate;
import tv.twitch.android.models.player.PlayerMode;
import tv.twitch.android.models.videos.VodModel;
import tv.twitch.android.shared.ads.pub.TheatreAdsState;
import tv.twitch.android.shared.player.overlay.seekable.SeekbarOverlayPresenter;

public class PlayerOverlayViewDelegate extends BaseViewDelegate implements IPlayerOverlay {
    private ImageView createClipButton;
    private ComposeView createClipButtonComposeView;

    private final MediaRouteButton castButton;
    private final PublishSubject<PlayerOverlayEvents> playerOverlayEventsSubject = null;

    /* ... */

    public PlayerOverlayViewDelegate(View view) {
        super(view);

        /* ... */

//        orangeChaptersButton = VodChapters.get().getChaptersButton(this); // TODO: __INJECT_CODE
//        orangeVodSpeedButton = VodSpeed.get().getVodSpeedButton(this); // TODO: __INJECT_CODE
        TimerHook.bindTimerButton(this); // TODO: __INJECT_CODE

        /* ... */

        Object r2 = new BottomPlayerControlOverlayViewDelegate.BottomPlayerControlListener() {


            @Override
            public void onChangeOrientationButtonClicked() {
                /* ... */
            }

            @Override
            public void onExpandVideoButtonClicked() {
                /* ... */
            }

            @Override
            public void onMuteButtonClicked() {

            }

            @Override
            public void onShowChatButtonClicked() {
                /* ... */
            }

            @Override
            public void onVideoDebugInfoButtonClicked() {
                /* ... */
            }

            @Override
            public void onViewCountClicked() {
                /* ... */
            }

            @Override
            public void onRefreshClicked() { // TODO: __INJECT_METHOD
                PlayerOverlayViewDelegate.this.getOverlayLayoutController().hideOverlay();
                PlayerOverlayViewDelegate.this.getPlayerOverlayEventsSubject().onNext(PlayerOverlayEvents.Refresh.INSTANCE); // FIXME: wtf?
            }
        };

        /* ... */

        UIHook.maybeHideCreateClipButton(createClipButtonComposeView); // TODO: __INJECT_CODE

        throw new VirtualImpl();
    }

    @Override
    public void layoutOverlayForState(boolean b, PlayerMode playerMode, ViewGroup viewGroup, ViewGroup viewGroup1, boolean b1, TheatreAdsState theatreAdsState) {
        /* ... */

        throw new VirtualImpl();
    }

    @Override
    public void setChatButtonState(boolean b) {
        /* ... */

        throw new VirtualImpl();
    }

    @Override
    public void setChatButtonVisible(boolean b) {
        /* ... */

        throw new VirtualImpl();
    }

    public final OverlayLayoutController getOverlayLayoutController() {
        /* ... */

        throw new VirtualImpl();
    }

    public final PublishSubject<PlayerOverlayEvents> getPlayerOverlayEventsSubject() {
        /* ... */

        throw new VirtualImpl();
    }

    private BottomPlayerControlOverlayViewDelegate getBottomPlayerControlOverlayViewDelegate() {
        /* ... */

        throw new VirtualImpl();
    }

    public void onBindVodModel(@NonNull VodModel vod, @NonNull SeekbarOverlayPresenter presenter) { // TODO: __INJECT_METHOD
        ChaptersHook.bindChaptersButton(this, vod, presenter);
    }

    public final void setClipButtonState(boolean var1) {
        /* ... */

        UIHook.maybeHideCreateClipButton(createClipButtonComposeView); // TODO: __INJECT_CODE

        throw new VirtualImpl();
    }

    public final ImageView getShareButton() {
        throw new VirtualImpl();
    }

    public final void setShouldShowChromecast(boolean var1) {
        var1 = UIHook.maybeHideCastButton(var1); // TODO: __INJECT_CODE
    }

    public final void setupChromecast() {
        /* ... */

        UIHook.maybeHideCastButton(castButton); // TODO: __INJECT_CODE
    }

    public void onInflateViewDelegate() { // TODO: __INJECT_METHOD
        ChaptersHook.hideChaptersButton(this);
    }

    public void onBindClip() { // TODO: __INJECT_METHOD
        ChaptersHook.hideChaptersButton(this);
    }
}
