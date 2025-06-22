package tv.twitch.android.shared.player.manifest;

import io.reactivex.Single;
import tv.purple.monolith.core.LoggerImpl;
import tv.purple.monolith.features.vodhunter.bridge.VodHunterHook;
import tv.twitch.android.models.player.ManifestProperties;
import tv.twitch.android.models.videos.VodModel;
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestResponse;

public class VodManifestProvider {
    public final void fetchVodManifest(VodModel model, ManifestProperties manifestProperties, Integer num) {
        LoggerImpl.debugObject(model);
        LoggerImpl.debugObject(manifestProperties);

        Single<ManifestResponse> singleDoOnSuccess = null;
        singleDoOnSuccess = VodHunterHook.maybeHookVodManifestResponse(model.getId(), singleDoOnSuccess); // TODO: __INJECT_CODE
    }
}
