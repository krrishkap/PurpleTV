package tv.purple.monolith.features.settings.bridge

import tv.purple.monolith.features.settings.bridge.settings.PurpleTVChatSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVChatSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVDevSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVDevSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVGestureSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVGestureSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVInfoSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVInfoSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVPlayerSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVPlayerSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVViewSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVViewSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartyBadgesSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartyBadgesSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartyEmotesSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartyEmotesSettingsPresenter
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartySettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartySettingsPresenter
import tv.twitch.android.shared.settings.BaseSettingsPresenter

enum class DaggerInjectionList(
    val fragment: Class<*>,
    val presenter: Class<out BaseSettingsPresenter>
) {
    MainSettings(
        fragment = PurpleTVSettingsFragment::class.java,
        presenter = PurpleTVSettingsPresenter::class.java
    ),
    ThirdPartyEmotes(
        fragment = PurpleTVThirdPartyEmotesSettingsFragment::class.java,
        presenter = PurpleTVThirdPartyEmotesSettingsPresenter::class.java
    ),
    ThirdPartyBadges(
        fragment = PurpleTVThirdPartyBadgesSettingsFragment::class.java,
        presenter = PurpleTVThirdPartyBadgesSettingsPresenter::class.java
    ),
    ThirdParty(
        fragment = PurpleTVThirdPartySettingsFragment::class.java,
        presenter = PurpleTVThirdPartySettingsPresenter::class.java
    ),
    Chat(
        fragment = PurpleTVChatSettingsFragment::class.java,
        presenter = PurpleTVChatSettingsPresenter::class.java
    ),
    Player(
        fragment = PurpleTVPlayerSettingsFragment::class.java,
        presenter = PurpleTVPlayerSettingsPresenter::class.java
    ),
    View(
        fragment = PurpleTVViewSettingsFragment::class.java,
        presenter = PurpleTVViewSettingsPresenter::class.java
    ),
    Dev(
        fragment = PurpleTVDevSettingsFragment::class.java,
        presenter = PurpleTVDevSettingsPresenter::class.java
    ),
    Info(
        fragment = PurpleTVInfoSettingsFragment::class.java,
        presenter = PurpleTVInfoSettingsPresenter::class.java
    ),
    Gestures(
        fragment = PurpleTVGestureSettingsFragment::class.java,
        presenter = PurpleTVGestureSettingsPresenter::class.java
    )
}