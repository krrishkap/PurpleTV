package tv.twitch.android.shared.ads.vaes;

import io.reactivex.Single;
import tv.purple.monolith.core.LoggerImpl;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.shared.ads.models.AdRequestInfo;
import tv.twitch.android.shared.video.ads.sdk.RequestBuilder;

public class VaesAdTagUrlBuilder {
    public final Single<RequestBuilder> buildAdTagUrlUrl(AdRequestInfo adRequestInfo) {
        LoggerImpl.debugObject(adRequestInfo); // TODO: __INJECT_CODE

        throw new VirtualImpl();
    }
}