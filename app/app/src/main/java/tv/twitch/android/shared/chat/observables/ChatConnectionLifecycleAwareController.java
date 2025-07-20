package tv.twitch.android.shared.chat.observables;

public class ChatConnectionLifecycleAwareController {
    private ChatConnectionController chatConnectionController;


    void connectAsAnon() {
        chatConnectionController.reconnectAsAnon();
    }
}
