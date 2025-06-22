package tv.twitch.android.core.mvp.presenter;

import java.util.List;

import tv.purple.monolith.core.LoggerImpl;
import tv.purple.monolith.models.exception.VirtualImpl;

public class StateMachine<State extends PresenterState, Event extends StateUpdateEvent, Action extends PresenterAction> {
    /* ... */

    private final void debugStateUpdate(String str, Event event, State state, State state2, List<? extends Action> list) {// TODO: __REPLACE_METHOD
        List<PresenterAction> actions = null;
        if (list != null) {
            actions = (List<PresenterAction>) list;
        }

        LoggerImpl.debugStateUpdate(str, event, state, state2, actions);
    }

    public final void pushEvent(Event event) {
        throw new VirtualImpl();
    }

    /* ... */
}
