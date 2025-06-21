package tv.twitch.android.shared.chat.pub.messages.data;

import kotlin.NotImplementedError;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import tv.twitch.android.core.commerce.models.bits.Cheermote;

public abstract class MessageTokenV2 {
    public static final class BitsToken extends MessageTokenV2 {
        private final Cheermote cheermote;
        private final int numBits;

        public BitsToken(Cheermote cheermote, int i) {
            throw new NotImplementedError();
        }

        public final Cheermote getCheermote() {
            return this.cheermote;
        }

        public final int getNumBits() {
            return this.numBits;
        }
    }

    public static final class CensoredTextToken extends MessageTokenV2 {
        private final String text;
        private final CensoredMessageTrackingInfo trackingInfo;

        public CensoredTextToken(String text, CensoredMessageTrackingInfo trackingInfo) {
            throw new NotImplementedError();
        }

        public final String getText() {
            return this.text;
        }

        public final CensoredMessageTrackingInfo getTrackingInfo() {
            return this.trackingInfo;
        }
    }

    public static class EmoteToken extends MessageTokenV2 { // TODO: __REMOVE_FINAL
        private final String id;
        private final String text;

        public EmoteToken(String id, String text) {
            throw new NotImplementedError();
        }

        public final String getId() {
            return this.id;
        }

        public final String getText() {
            return this.text;
        }
    }

    public static final class MentionToken extends MessageTokenV2 {
        private final boolean isLocalUser;
        private final String text;
        private final String userName;

        public MentionToken(String text, String userName, boolean z) {
            throw new NotImplementedError();
        }

        public final String getText() {
            return this.text;
        }

        public final String getUserName() {
            return this.userName;
        }
    }

    public static final class TextToken extends MessageTokenV2 {
        private final String text;

        public TextToken(String text) {
            throw new NotImplementedError();
        }

        public final String getText() {
            return this.text;
        }
    }

    public static final class UrlToken extends MessageTokenV2 {
        private final boolean hidden;
        private final String url;

        public UrlToken(String url, boolean z) {
            throw new NotImplementedError();
        }

        public final boolean getHidden() {
            return this.hidden;
        }

        public final String getUrl() {
            return this.url;
        }
    }

    public MessageTokenV2(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    private MessageTokenV2() {
    }
}
