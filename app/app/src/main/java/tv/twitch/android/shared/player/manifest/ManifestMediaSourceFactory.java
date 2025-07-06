package tv.twitch.android.shared.player.manifest;

import android.net.Uri;

import kotlin.coroutines.Continuation;
import tv.purple.monolith.features.proxy.bridge.ProxyHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.models.AccessTokenResponse;
import tv.twitch.android.shared.manifest.fetcher.pub.ManifestResponse;

public class ManifestMediaSourceFactory {
    public final Object getMediaSource(Uri var1, Object var2) {
        var1 = ProxyHook.tryHookMediaSourceURI(var1);

        throw new VirtualImpl();
    }

    public final Object getSourceObject(Uri var1, AccessTokenResponse var2, Continuation<? super ManifestResponse> var3) {
        var1 = ProxyHook.tryHookSourceObjectURI(var1);


        throw new VirtualImpl();
    }

}
