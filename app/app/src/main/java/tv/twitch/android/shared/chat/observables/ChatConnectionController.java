package tv.twitch.android.shared.chat.observables;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import tv.purple.monolith.bridge.PurpleTVAppContainer;
import tv.purple.monolith.core.bridge.CoreHook;
import tv.purple.monolith.features.chat.ChatHookProvider;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.core.mvp.presenter.BasePresenter;
import tv.twitch.android.models.channel.ChannelInfo;
import tv.twitch.android.models.chat.ChannelState;
import tv.twitch.android.models.streams.StreamType;
import tv.twitch.android.shared.chat.ChatController;
import tv.twitch.android.shared.chat.pub.events.ChatNoticeEvent;
import tv.twitch.android.shared.chat.pub.events.MessagesReceivedEvent;
import tv.twitch.chat.library.model.ErrorCode;

public class ChatConnectionController extends BasePresenter {
    private PublishSubject<ChatNoticeEvent> noticeEventsSubject;
    private PublishSubject<MessagesReceivedEvent> messagesSubject;
    private int viewerId;
    private final ChatHookProvider chp = PurpleTVAppContainer.getInstance().provideChatHookProvider(); // TODO: __INJECT_FIELD

    /* ... */

    public ChatConnectionController() {
        /* ... */

        // ChatLogs.get().subscribeToMessages(this, messagesSubject); // TODO: __INJECT_CODE

        throw new VirtualImpl();
    }

    public final void handleChannelStateChange(int i, ChannelState channelState, ErrorCode errorCode) {
        if (i == 1) {
            /* ... */

            CoreHook.onConnectingToChannel(i); // TODO: __INJECT_CODE
        }
        if (i == 2) {
            /* ... */

            CoreHook.onConnectedToChannel(i); // TODO: __INJECT_CODE
        }

        throw new VirtualImpl();
    }

    public final void setViewerId(int id) { // TODO: __INJECT_METHOD
        viewerId = id;
    }

    private final void connect(int i) {
        throw new VirtualImpl();
    }

    public final void disconnect(ChannelInfo channelInfo) {
        throw new VirtualImpl();
    }

    public Flowable<MessagesReceivedEvent> observeMessagesReceived() {
        Flowable<MessagesReceivedEvent> flowable = null;

        /* ... */

        flowable = chp.filterMessages(flowable); // TODO: __INJECT_CODE

        return flowable;
    }

    public final Observable<Object> observeBroadcasterInfo() {
        throw new VirtualImpl();
    }

    private final void setBroadcaster(ChannelInfo channelInfo, StreamType streamType) {
        CoreHook.onConnectingToChannel(channelInfo); // TODO: __INJECT_CODE

        /* ... */

        throw new VirtualImpl();
    }

    private final ChatController getChatController() {
        throw new VirtualImpl();
    }

    public void reconnectAsAnon() {

    }

    /* ... */
}