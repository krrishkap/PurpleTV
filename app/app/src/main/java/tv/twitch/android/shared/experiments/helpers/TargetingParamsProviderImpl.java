package tv.twitch.android.shared.experiments.helpers;

import tv.purple.monolith.core.LoggerImpl;
import tv.purple.monolith.core.bridge.CoreHook;
import tv.purple.monolith.models.exception.VirtualImpl;
import tv.twitch.android.models.player.ManifestProperties;

public class TargetingParamsProviderImpl {
    public ManifestProperties applyTargetingParameters(ManifestProperties manifestProperties) {
        LoggerImpl.debugObject(manifestProperties); // TODO: __INJECT_CODE
        manifestProperties = CoreHook.testHook(manifestProperties); // TODO: __INJECT_CODE
        throw new VirtualImpl();
    }
}
