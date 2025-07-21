package tv.purple.monolith.features.settings.bridge.model

import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.core.models.flag.Flag
import tv.twitch.android.models.settings.SettingsDestination

sealed interface Item {
    data class FlagItem(val flag: Flag) : Item
    data class InfoItem(
        val title: String,
        val desc: String?,
        val behavior: InfoItemBehavior = InfoItemBehavior.None
    ) : Item

    data class SubMenuItem(
        val title: String,
        val desc: String?,
        val dest: SettingsDestination
    ) : Item
}

enum class PurpleTVSubMenu(
    val destination: SettingsDestination,
    val title: String,
    val desc: String? = null,
    val items: List<Item> = emptyList()
) {
    ThirdPartyEmotes(
        destination = SettingsDestination.PurpleThirdPartyEmotes,
        title = RES_STRINGS.purpletv_settings_emotes,
        items = listOf(
            Flag.BTTV_EMOTES,
            Flag.BTTV_WEBP,
            Flag.FFZ_EMOTES,
            Flag.STV_EMOTES,
            Flag.STV_GLOBAL_EMOTES,
            Flag.HOMIES_EMOTES,
            Flag.EMOTE_QUALITY,
        ).map { Item.FlagItem(it) }
    ),
    ThirdPartyBadges(
        destination = SettingsDestination.PurpleThirdPartyEmotes,
        title = RES_STRINGS.purpletv_settings_badges,
        items = listOf(
            Flag.FFZ_BADGES,
            // Flag.STV_BADGES,
            Flag.CHA_BADGES,
            Flag.HOMIES_BADGES,
            Flag.PTV_BADGES,
            Flag.BTTV_BADGES,
            Flag.DANKCHAT_BADGES,
            Flag.CHATSEN_BADGES,
            Flag.REYOHOHO_BADGES,
        ).map { Item.FlagItem(it) }
    ),
    ThirdParty(
        destination = SettingsDestination.PurpleThirdParty,
        title = RES_STRINGS.purpletv_settings_menu_third_party,
        items = listOf(
            Item.SubMenuItem(
                title = RES_STRINGS.purpletv_settings_emotes,
                desc = null,
                dest = SettingsDestination.PurpleThirdPartyEmotes
            ),
            Item.SubMenuItem(
                title = RES_STRINGS.purpletv_settings_badges,
                desc = null,
                dest = SettingsDestination.PurpleThirdPartyBadges
            ),
            Item.FlagItem(Flag.PRONOUNS)
        )
    ),
    Chat(
        destination = SettingsDestination.PurpleChat,
        title = RES_STRINGS.purpletv_settings_menu_chat,
        items = listOf(
            Flag.ONE_CHAT_LURKER,
            Flag.HIDE_ONE_CHAT_POPUP,
            Flag.PINNED_CHEER,
            Flag.ALTERNATING_BACKGROUND,
            Flag.VIBRATE_ON_MENTION,
            Flag.ANON_CHAT_CONNECTION,
            Flag.CHAT_SETTINGS,
            Flag.PREVENT_MOD_CLEAR,
            Flag.MOD_LOG_NOTICES,
            Flag.DISABLE_LINK_DISCLAIMER,
            Flag.HIDE_CHAT_HEADER,
            Flag.FAST_EMOTE_AUTOCOMPLETE,
            Flag.HIDE_MESSAGE_INPUT,
            Flag.AUTO_HIDE_MESSAGE_INPUT,
            Flag.HIDE_BITS_BUTTON,
            Flag.DISABLE_STICKY_HEADERS_EP,
            Flag.HIDE_LEADERBOARDS,
            Flag.DISABLE_HYPETRAIN,
            Flag.TIMESTAMPS,
            Flag.DISPLAY_NAME,
            Flag.ME_STYLE,
            Flag.DELETED_MESSAGES_STRATEGY,
            Flag.CHAT_FONT_SIZE,
            Flag.PINNED_MESSAGE,
            Flag.VIBRATION_DURATION,
            Flag.LANDSCAPE_CHAT_SIZE,
            Flag.LANDSCAPE_SPLIT_CHAT_SIZE,
            Flag.LANDSCAPE_CHAT_OPACITY,
            Flag.GLIDE_CACHE_SIZE,
        ).map { Item.FlagItem(it) } + listOf(
            Item.SubMenuItem(
                title = RES_STRINGS.purpletv_settings_menu_highlighter,
                desc = null,
                dest = SettingsDestination.PurpleHighlighter
            ),
            Item.SubMenuItem(
                title = RES_STRINGS.purpletv_settings_menu_blacklist,
                desc = null,
                dest = SettingsDestination.PurpleBlacklist
            ),
            Item.SubMenuItem(
                title = RES_STRINGS.purpletv_settings_user_mention_color,
                desc = null,
                dest = SettingsDestination.PurpleHighlightColor
            )
        ),
    ),
    Player(
        destination = SettingsDestination.PurplePlayer,
        title = RES_STRINGS.purpletv_settings_menu_player,
        items = listOf(
            Flag.PLAYER_IMPL,
            Flag.PROXY_LIST
        ).map { Item.FlagItem(it) } + listOf(
            Item.SubMenuItem(
                title = RES_STRINGS.purpletv_custom_proxy_url,
                desc = RES_STRINGS.purpletv_custom_proxy_url_desc,
                dest = SettingsDestination.PurpleCustomProxy
            )
        ) + listOf(
            Flag.DISABLE_MATURE_CONTENT,
            Flag.DISABLE_FAST_BREAD,
            Flag.VODHUNTER,
            Flag.DISABLE_THEATRE_AUTOPLAY,
            Flag.FORCE_EXOPLAYER_FOR_VODS,
            Flag.COMPACT_PLAYER_FOLLOW_VIEW,
            Flag.SHOW_STATS_BUTTON,
            Flag.SHOW_REFRESH_BUTTON,
            Flag.HIDE_UNFOLLOW_BUTTON,
            Flag.HIDE_FSB,
            Flag.HIDE_PLAYER_CREATE_CLIP_BUTTON,
            Flag.HIDE_PLAYER_LIVE_SHARE_BUTTON,
            Flag.DISABLE_CAST_BUTTON,
            Flag.FORWARD_SEEK,
            Flag.REWIND_SEEK,
        ).map { Item.FlagItem(it) }
    ),
    Gestures(
        destination = SettingsDestination.PurpleGestures,
        title = RES_STRINGS.purpletv_settings_menu_gestures,
        items = listOf(
            Item.FlagItem(Flag.VOLUME_GESTURE),
            Item.FlagItem(Flag.BRIGHTNESS_GESTURE)
        )
    ),
    View(
        destination = SettingsDestination.PurpleView,
        title = RES_STRINGS.purpletv_settings_menu_view,
        items = listOf(
            Flag.STORIES,
            Flag.BOTTOM_NAVBAR_POSITION,
            Flag.FOLLOWED_FULL_CARDS,
            Flag.HIDE_DISCOVER_TAB,
            Flag.HIDE_DISCOVER_FEED,
            Flag.HIDE_GAME_SECTION,
            Flag.HIDE_RESUME_WATCHING_SECTION,
            Flag.HIDE_OFFLINE_CHANNEL_SECTION,
            Flag.HIDE_FEATURED_CLIPS_SECTION,
            Flag.SHOW_TIMER_BUTTON,
            Flag.HIDE_CREATE_BUTTON,
            Flag.FORCE_TOOLBAR_SEARCH_BUTTON,
            Flag.FORCE_TABLET_MODE,
        ).map { Item.FlagItem(it) }
    ),
    Patches(
        destination = SettingsDestination.PurplePatches,
        title = RES_STRINGS.purpletv_settings_menu_patches
    ),
    Dev(
        destination = SettingsDestination.PurpleDev,
        title = RES_STRINGS.purpletv_settings_menu_dev,
        items = listOf(
            Flag.DEV_MODE,
            Flag.OKHTTP_LOGGING,
            Flag.FORCE_OTA,
            Flag.STETHO_INTERCEPTOR,
            Flag.DEBUG_STATE_MACHINE,
            Flag.DISABLE_COMSCORE,
            Flag.DISABLE_GOOGLE_PLAY_SERVICES,
            Flag.BUGSNAG
        ).map { Item.FlagItem(it) }
    ),
    Support(
        destination = SettingsDestination.PurpleSupport,
        title = RES_STRINGS.purpletv_settings_menu_support
    ),
    Wiki(
        destination = SettingsDestination.PurpleWiki,
        title = RES_STRINGS.purpletv_settings_menu_wiki
    ),
    Info(
        destination = SettingsDestination.PurpleInfo,
        title = RES_STRINGS.purpletv_settings_menu_info,
        items = listOf(
            Item.InfoItem(
                title = RES_STRINGS.purpletv_settings_info_dev,
                desc = RES_STRINGS.purpletv_settings_info_dev_desc,
                behavior = InfoItemBehavior.ClickableUrl("https://t.me/nopbreak")
            ),
            Item.InfoItem(
                title = RES_STRINGS.purpletv_settings_info_lanzzz,
                desc = RES_STRINGS.purpletv_settings_info_lanzzz_desc,
                behavior = InfoItemBehavior.ClickableUrl("https://www.twitch.tv/lanzzopoulos")
            ),
            Item.InfoItem(
                title = RES_STRINGS.purpletv_settings_info_tg,
                desc = RES_STRINGS.purpletv_settings_info_tg_desc,
                behavior = InfoItemBehavior.ClickableUrl("https://t.me/pubTwChat")
            ),
            Item.InfoItem(
                title = RES_STRINGS.purpletv_settings_crowdin,
                desc = RES_STRINGS.purpletv_settings_crowdin_desc,
                behavior = InfoItemBehavior.ClickableUrl("https://crowdin.com/project/orangetv")
            )
        )
    ),
}