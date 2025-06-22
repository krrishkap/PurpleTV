package tv.twitch.android.shared.emotes.emotepicker;

import java.util.List;

import io.reactivex.Flowable;
import kotlin.Pair;
import tv.purple.monolith.features.chat.bridge.ChatHook;
import tv.purple.monolith.features.chat.bridge.IEmotePickerPresenter;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.core.mvp.presenter.PresenterAction;
import tv.twitch.android.core.mvp.presenter.PresenterState;
import tv.twitch.android.core.mvp.presenter.StateMachine;
import tv.twitch.android.core.mvp.presenter.StateUpdateEvent;
import tv.twitch.android.core.mvp.viewdelegate.ViewDelegateState;
import tv.twitch.android.shared.emotes.emotepicker.models.EmoteUiSet;

public class EmotePickerPresenter implements IEmotePickerPresenter { // TODO: __IMPLEMENT
    private StateMachine<EmotePickerState, UpdateEvent, PresenterAction> stateMachine;

    @Override
    public void notifyFavEmotesChanged() { // TODO: __INJECT_METHOD
        stateMachine.pushEvent(UpdateEvent.AnimatedEmotesSettingsChanged.INSTANCE);
    }
    /* ... */

    public static final class EmotePickerState implements PresenterState, ViewDelegateState {
        /* ... */
    }

    /* ... */

    private Flowable<Pair<EmoteUiSet, List<EmoteUiSet>>> createAllEmoteSetsFlowable(final Integer num, final EmotePickerSource emotePickerSource) {
        /* ... */

        Flowable<Pair<EmoteUiSet, List<EmoteUiSet>>> map = null;

        /* ... */

        map = ChatHook.hookEmoteSetsFlowable(map, num); // TODO: __INJECT_CODE

        return map;
    }

    public final void updateOnNewEmoteSets(List<EmoteUiSet> list, boolean z) {
        list = ChatHook.sortEmotePickerUiSets(list); // TODO: __INJECT_CODE

        throw new VirtualImpl();
    }


    public static abstract class ClickEvent {
    }

    public static abstract class UpdateEvent implements StateUpdateEvent {

        /* compiled from: EmotePickerPresenter.kt */
        public static final class AnimatedEmotesSettingsChanged extends UpdateEvent {
            public static final AnimatedEmotesSettingsChanged INSTANCE = new AnimatedEmotesSettingsChanged();

        }
    }

    /* ... */
}
