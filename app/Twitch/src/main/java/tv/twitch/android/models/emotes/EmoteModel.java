package tv.twitch.android.models.emotes;

import kotlin.NotImplementedError;
import kotlin.jvm.internal.DefaultConstructorMarker;

/**
 * Stub class for tv.twitch.android.models.emotes.EmoteModel
 */
public abstract class EmoteModel {
    public EmoteModel(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    public abstract EmoteModelAssetType getAssetType();

    public abstract String getId();

    public abstract String getToken();

    public abstract EmoteModelType getType();

    private EmoteModel() {
    }

    public static class Generic extends EmoteModel { // TODO: REMOVE_FINAL
        /* ... */

        @Override
        public EmoteModelAssetType getAssetType() {
            throw new NotImplementedError();
        }

        @Override
        public String getId() {
            throw new NotImplementedError();
        }

        @Override
        public String getToken() {
            throw new NotImplementedError();
        }

        @Override
        public EmoteModelType getType() {
            throw new NotImplementedError();
        }

        /* ... */

        public Generic(String id, String token, EmoteModelAssetType assetType, EmoteModelType type) {
            throw new NotImplementedError();
        }
    }

    /* ... */
}
