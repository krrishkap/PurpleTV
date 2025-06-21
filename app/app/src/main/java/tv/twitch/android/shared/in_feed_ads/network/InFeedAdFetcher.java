package tv.twitch.android.shared.in_feed_ads.network;

import io.reactivex.Single;
import tv.twitch.android.shared.ads.models.InFeedAd;
import tv.twitch.android.shared.ads.models.InFeedAdSlot;
import tv.twitch.android.shared.ads.models.display.DisplayAdInfo;
import tv.twitch.android.shared.ads.models.display.DisplayAdType;
import tv.twitch.android.shared.in_feed_ads.AdjacentFeedItems;
import tv.twitch.android.util.Optional;

public class InFeedAdFetcher {
    public final Single<Optional<DisplayAdInfo>> requestInFeedAd(final String adSessionId, AdjacentFeedItems adjacentFeedItems, final DisplayAdType displayAdType) { // TODO: __REPLACE_METHOD
        return Single.just(Optional.empty());
    }

    public final Single<Optional<InFeedAd>> requestMultiFormatInFeedAd(final String adSessionId, AdjacentFeedItems adjacentFeedItems, final InFeedAdSlot inFeedAdSlot, String str) { // TODO: __REPLACE_METHOD
        return Single.just(Optional.empty());
    }
}