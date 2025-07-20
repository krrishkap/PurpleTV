package tv.twitch.android.shared.chat;

import androidx.fragment.app.FragmentActivity;

import tv.purple.monolith.features.chat.bridge.ChatHook;
import tv.purple.monolith.features.logs.bridge.ChatLocalLogsCommand;
import tv.purple.monolith.features.logs.bridge.ChatTwitchLogsCommand;
import tv.purple.monolith.features.ui.UI;
import tv.purple.monolith.features.ui.bridge.UIHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.core.mvp.presenter.BasePresenter;
import tv.twitch.android.core.mvp.rxutil.DisposeOn;
import tv.twitch.android.models.channel.ChannelInfo;
import tv.twitch.android.models.streams.StreamType;
import tv.twitch.android.shared.chat.chatheader.ChatHeaderPresenter;
import tv.twitch.android.shared.chat.command.ChatCommandAction;
import tv.twitch.android.shared.chat.messages.ChatMessagesPresenter;
import tv.twitch.android.shared.chat.observables.ChatConnectionController;
import tv.twitch.android.shared.chat.pub.events.ChatConnectionEvents;
import tv.twitch.android.shared.messageinput.pub.ChatSendAction;

public class ChatViewPresenter extends BasePresenter {
    private ChatMessagesPresenter chatMessagesPresenter;
    private ChannelInfo channel;
    private ChatViewDelegate chatViewDelegate;
    private ChatConnectionController chatConnectionController;
    private ChatHeaderPresenter chatHeaderPresenter;
    private int chatUserId;
    private String playbackSessionID;
    private StreamType streamType;

    private FragmentActivity activity;

    /* ... */

    public final void onUserBanStateUpdated(boolean z) {
        if (z && ChatHook.bypassChatBan()) { // TODO: __INJECT_CODE
            z = false;
        }

        /* ... */

        throw new VirtualImpl();
    }

    public final void setChannel(ChannelInfo channel, String playbackSessionId, StreamType streamType) {
        throw new VirtualImpl();
    }

    public final void onChannelStateChanged(ChatConnectionEvents chatConnectionEvents) {
//        ChatHistory.get().requestChatHistory(chatConnectionEvents, (IChatMessagesPresenter) chatMessagesPresenter, channel); // TODO: __INJECT_CODE

        /* ... */

        throw new VirtualImpl();
    }

    public final boolean maybeSubmitMessage(String str, ChatSendAction chatSendAction) {
        ChatCommandAction processChatCommand = null;

        /* ... */

        if (processChatCommand instanceof ChatLocalLogsCommand) { // TODO: __INJECT_CODE
            return true;
        }

        if (processChatCommand instanceof ChatTwitchLogsCommand) { // TODO: __INJECT_CODE
            return true;
        }

        /* ... */

        throw new VirtualImpl();
    }

    public void onConfigurationChanged() {
        /* ... */

        UIHook.onChatViewPresenterConfigurationChanged(chatViewDelegate); // TODO: __INJECT_CODE

        throw new VirtualImpl();
    }

    public final void connect() {
        /* ... */

        ChannelInfo channelInfo = this.channel;
//        if (channelInfo != null) {
//            Cerberus.get().check(activity, channelInfo); // TODO: __INJECT_CODE
//        }
    }

    public final void attachViewDelegate(final ChatViewDelegate chatViewDelegate) {
        setupOrangeStuff(chatViewDelegate); // TODO: __INJECT_CODE

        /* ... */
    }

    private void setupOrangeStuff(ChatViewDelegate delegate) { // TODO: __INJECT_METHOD
        directSubscribe(chatHeaderPresenter.observeHideInputButtonClickEvents(), DisposeOn.DESTROY, UI.onHideInputClicked(delegate));
    }

    /* ... */
}