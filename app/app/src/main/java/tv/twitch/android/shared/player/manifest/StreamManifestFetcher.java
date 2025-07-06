package tv.twitch.android.shared.player.manifest;

import android.net.Uri;

import io.reactivex.Single;
import tv.purple.monolith.core.LoggerImpl;
import tv.purple.monolith.features.proxy.bridge.ProxyHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.models.AccessTokenResponse;
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

    public final Uri buildManifestURI$shared_player_release(final String streamName, AccessTokenResponse accessTokenResponse, final ManifestProperties manifestProperties, boolean z) {
        Uri res = null;

        res = ProxyHook.tryHookManifestURI(res); // TODO: __INJECT_CODE

        return res;
    }
}