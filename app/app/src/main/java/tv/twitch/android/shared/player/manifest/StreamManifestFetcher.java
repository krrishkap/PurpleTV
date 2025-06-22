package tv.twitch.android.shared.player.manifest;

import io.reactivex.Single;
import tv.purple.monolith.core.LoggerImpl;
import tv.purple.monolith.features.proxy.bridge.ProxyHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.models.analytics.FmpTrackingId;
import tv.twitch.android.models.player.ManifestProperties;
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestResponse;

public class StreamManifestFetcher {
    public final Single<ManifestResponse> fetchSourceManifestFromFactory(final String channelName, final ManifestProperties manifestProperties, Integer num, final FmpTrackingId fmpTrackingId) {
        LoggerImpl.debugObject(channelName);
        LoggerImpl.debugObject(manifestProperties);

        Single<ManifestResponse> singleDoOnSuccess = null;
        singleDoOnSuccess = ProxyHook.tryHookStreamManifestResponse(channelName, singleDoOnSuccess); // TODO: __INJECT_CODE

        throw new VirtualImpl();
    }
}