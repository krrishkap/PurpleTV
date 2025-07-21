package tv.purple.monolith.core.models.flag

import android.graphics.Color
import tv.purple.monolith.core.PrefManager
import tv.purple.monolith.core.models.flag.core.BooleanValue
import tv.purple.monolith.core.models.flag.core.IntRangeValue
import tv.purple.monolith.core.models.flag.core.IntValue
import tv.purple.monolith.core.models.flag.core.ListValue
import tv.purple.monolith.core.models.flag.core.ObjectValue
import tv.purple.monolith.core.models.flag.core.StringValue
import tv.purple.monolith.core.models.flag.core.ValueHolder
import tv.purple.monolith.core.models.flag.core.Variant
import tv.purple.monolith.core.models.flag.variants.BottomNavbarPosition
import tv.purple.monolith.core.models.flag.variants.DeletedMessages
import tv.purple.monolith.core.models.flag.variants.DisplayName
import tv.purple.monolith.core.models.flag.variants.EmoteQuality
import tv.purple.monolith.core.models.flag.variants.MeStyle
import tv.purple.monolith.core.models.flag.variants.PinnedMessageStrategy
import tv.purple.monolith.core.models.flag.variants.PlayerImpl
import tv.purple.monolith.core.models.flag.variants.ProxyImpl
import tv.purple.monolith.core.models.flag.variants.Timestamps
import tv.purple.monolith.core.models.flag.variants.UpdateChannel
import java.util.EnumSet

enum class Flag(
    val preferenceKey: String,
    val titleResName: String,
    val summaryResName: String?,
    val defaultHolder: ValueHolder,
    var valueHolder: ValueHolder
) {
    /**
     * ---------------------------- Fixed stuff ----------------------------
     */
    GLIDE_CACHE_SIZE(
        prefKey = "glide_cache_size",
        titleResName = "purpletv_settings_glide_cache_size",
        value = IntRangeValue(
            minValue = 64,
            maxValue = 1024,
            currentValue = 512,
            step = 32
        )
    ),
    HIDE_ONE_CHAT_POPUP(
        prefKey = "hide_one_chat_popup",
        titleResName = "purpletv_settings_hide_one_chat_popup",
        value = BooleanValue(false)
    ),
    SHOW_STATS_BUTTON(
        prefKey = "show_stats_button",
        titleResName = "purpletv_settings_show_stats_button",
        value = BooleanValue(true)
    ),
    DELETED_MESSAGES_STRATEGY(
        prefKey = "deleted_messages",
        titleResName = "purpletv_settings_deleted_messages",
        value = ListValue(DeletedMessages::class)
    ),
    SHOW_TIMER_BUTTON(
        prefKey = "show_timer_button",
        titleResName = "purpletv_settings_show_timer_button",
        value = BooleanValue(true)
    ),
    PLAYER_IMPL(
        prefKey = "player_impl_broken",
        titleResName = "purpletv_settings_player_impl",
        value = ListValue(PlayerImpl::class)
    ),
    TIMESTAMPS(
        prefKey = "timestamps_v2",
        titleResName = "purpletv_settings_timestamps",
        value = ListValue(Timestamps::class)
    ),
    BUGSNAG(
        prefKey = "bugsnag_m",
        titleResName = "purpletv_settings_bugsnag",
        value = BooleanValue(true)
    ),
    DISABLE_STICKY_HEADERS_EP(
        prefKey = "disable_sticky_headers_ep",
        titleResName = "purpletv_settings_disable_sticky_headers_ep",
        value = BooleanValue(false)
    ),
    CHAT_FONT_SIZE(
        prefKey = "chat_font_size_v2",
        titleResName = "purpletv_settings_chat_font_size",
        value = IntRangeValue(
            minValue = 8,
            maxValue = 24,
            currentValue = 13,
            step = 1
        )
    ),
    VIBRATION_DURATION(
        prefKey = "vibration_duration",
        titleResName = "purpletv_settings_vibration_duration",
        value = IntRangeValue(
            minValue = 10,
            maxValue = 1000,
            currentValue = 100,
            step = 1
        )
    ),
    ALTERNATING_BACKGROUND(
        prefKey = "alternate_background",
        titleResName = "purpletv_settings_alternate_background",
        value = BooleanValue(false)
    ),
    ME_STYLE(
        prefKey = "me_style",
        titleResName = "purpletv_settings_me_style",
        value = ListValue(MeStyle::class)
    ),
    PINNED_MESSAGE(
        prefKey = "pinned_message",
        titleResName = "purpletv_settings_pinned_message",
        value = ListValue(PinnedMessageStrategy::class)
    ),
    LANDSCAPE_CHAT_SIZE(
        prefKey = "landscape_chat_size_v3",
        titleResName = "purpletv_settings_landscape_chat_size",
        value = IntRangeValue(
            minValue = 10,
            maxValue = 50,
            currentValue = 30
        )
    ),
    LANDSCAPE_SPLIT_CHAT_SIZE(
        prefKey = "landscape_split_chat_size_v3",
        titleResName = "purpletv_settings_landscape_split_chat_size",
        value = IntRangeValue(
            minValue = 10,
            maxValue = 70,
            currentValue = 50
        )
    ),
    FORWARD_SEEK(
        prefKey = "forward_seek",
        titleResName = "purpletv_settings_forward_seek",
        value = IntRangeValue(
            minValue = 5,
            maxValue = 120,
            currentValue = 30,
            step = 5
        )
    ),
    REWIND_SEEK(
        prefKey = "backward_seek",
        titleResName = "purpletv_settings_rewind_seek",
        value = IntRangeValue(
            minValue = 5,
            maxValue = 120,
            currentValue = 10,
            step = 5
        )
    ),
    HIDE_LEADERBOARDS(
        prefKey = "hide_leaderboards",
        titleResName = "purpletv_settings_hide_leaderboards",
        value = BooleanValue(false)
    ),
    DISABLE_CAST_BUTTON(
        prefKey = "disable_cast",
        titleResName = "purpletv_settings_disable_cast",
        value = BooleanValue(false)
    ),
    ONE_CHAT_LURKER(
        prefKey = "one_chat_lurker",
        titleResName = "purpletv_settings_one_chat_lurker",
        value = BooleanValue(false)
    ),
    ALPHA_BUILD_SHOWN(
        prefKey = "alpha_build_28032025",
        titleResName = "",
        value = BooleanValue(false)
    ),
    HOMIES_BADGES(
        prefKey = "homies_badges",
        titleResName = "purpletv_settings_homies_badges",
        value = BooleanValue(true)
    ),
    DISABLE_HYPETRAIN(
        prefKey = "disable_hype_train",
        titleResName = "purpletv_settings_disable_hype_train",
        value = BooleanValue(false)
    ),
    OKHTTP_LOGGING(
        prefKey = "okhttp_logging",
        titleResName = "purpletv_settings_okhttp_logging",
        value = BooleanValue(false)
    ),
    HIDE_RESUME_WATCHING_SECTION(
        prefKey = "hide_resume_watching_section",
        titleResName = "purpletv_settings_hide_resume_watching_section",
        value = BooleanValue(false)
    ),
    HIDE_OFFLINE_CHANNEL_SECTION(
        prefKey = "hide_offline_channel_section",
        titleResName = "purpletv_settings_hide_offline_channel_section",
        value = BooleanValue(false)
    ),
    BTTV_EMOTES(
        prefKey = "bttv_emotes",
        titleResName = "purpletv_settings_bttv_emotes",
        value = BooleanValue(true)
    ),
    FFZ_EMOTES(
        prefKey = "ffz_emotes",
        titleResName = "purpletv_settings_ffz_emotes",
        value = BooleanValue(true)
    ),
    STV_EMOTES(
        prefKey = "stv_emotes",
        titleResName = "purpletv_settings_stv_emotes",
        value = BooleanValue(true)
    ),
    STV_GLOBAL_EMOTES(
        prefKey = "stv_global_emotes",
        titleResName = "purpletv_settings_stv_global_emotes",
        value = BooleanValue(true)
    ),
    HOMIES_EMOTES(
        prefKey = "homies_emotes",
        titleResName = "purpletv_settings_homies_emotes",
        value = BooleanValue(false)
    ),
    FFZ_BADGES(
        prefKey = "ffz_badges",
        titleResName = "purpletv_settings_ffz_badges",
        value = BooleanValue(true)
    ),
    CHA_BADGES(
        prefKey = "cha_badges",
        titleResName = "purpletv_settings_cha_badges",
        value = BooleanValue(true)
    ),
    PTV_BADGES(
        prefKey = "ptv_badges",
        titleResName = "purpletv_settings_ptv_badges",
        value = BooleanValue(true)
    ),
    BTTV_BADGES(
        prefKey = "bttv_badges",
        titleResName = "purpletv_settings_bttv_badges",
        value = BooleanValue(true)
    ),
    DANKCHAT_BADGES(
        prefKey = "dankchat_badges",
        titleResName = "purpletv_settings_dankchat_badges",
        value = BooleanValue(true)
    ),
    CHATSEN_BADGES(
        prefKey = "chatsen_badges",
        titleResName = "purpletv_settings_chatsen_badges",
        value = BooleanValue(true)
    ),
    REYOHOHO_BADGES(
        prefKey = "reyohoho_badges",
        titleResName = "purpletv_settings_reyohoho_badges",
        value = BooleanValue(false)
    ),
    BTTV_WEBP(
        prefKey = "bttv_webp",
        titleResName = "purpletv_settings_bttv_webp",
        value = BooleanValue(true)
    ),
    CHAT_SETTINGS(
        prefKey = "chat_settings",
        titleResName = "purpletv_settings_chat_settings",
        value = BooleanValue(true)
    ),
    USER_MENTION_COLOR(
        prefKey = "user_mention_color",
        titleResName = "purpletv_settings_user_mention_color",
        value = IntValue(
            Color.argb(100, 255, 0, 0)
        )
    ),
    DISABLE_FAST_BREAD(
        prefKey = "disable_fast_bread",
        titleResName = "purpletv_settings_disable_fast_bread",
        value = BooleanValue(false)
    ),
    DISABLE_LINK_DISCLAIMER(
        prefKey = "disable_link_disclaimer",
        titleResName = "purpletv_settings_disable_link_disclaimer",
        value = BooleanValue(false)
    ),
    DEV_MODE(
        prefKey = "dev_mode",
        titleResName = "purpletv_settings_dev_mode",
        value = BooleanValue(false)
    ),
    BRIGHTNESS_GESTURE(
        prefKey = "brightness_gesture_v2",
        titleResName = "purpletv_settings_brightness_gesture",
        value = BooleanValue(false)
    ),
    VOLUME_GESTURE(
        prefKey = "volume_gesture_v2",
        titleResName = "purpletv_settings_volume_gesture",
        value = BooleanValue(false)
    ),
    HIDE_UNFOLLOW_BUTTON(
        prefKey = "hide_unfollow_button",
        titleResName = "purpletv_settings_hide_unfollow_button",
        value = BooleanValue(false)
    ),
    VIBRATE_ON_MENTION(
        prefKey = "vibrate_on_mention",
        titleResName = "purpletv_settings_vibrate_on_mention",
        value = BooleanValue(false)
    ),
    HIDE_PLAYER_CREATE_CLIP_BUTTON(
        prefKey = "hide_player_create_clip_button",
        titleResName = "purpletv_settings_hide_player_create_clip_button",
        value = BooleanValue(false)
    ),
    HIDE_PLAYER_LIVE_SHARE_BUTTON(
        prefKey = "hide_player_live_share_button",
        titleResName = "purpletv_settings_hide_player_live_share_button",
        value = BooleanValue(false)
    ),
    DISABLE_MATURE_CONTENT(
        prefKey = "disable_mature_content",
        titleResName = "purpletv_settings_disable_mature_content",
        value = BooleanValue(false)
    ),
    HIDE_CHAT_HEADER(
        prefKey = "hide_chat_header",
        titleResName = "purpletv_settings_hide_chat_header",
        value = BooleanValue(false)
    ),
    AUTO_HIDE_MESSAGE_INPUT(
        prefKey = "auto_hide_message_input",
        titleResName = "purpletv_settings_auto_hide_message_input",
        value = BooleanValue(false)
    ),
    HIDE_MESSAGE_INPUT(
        prefKey = "hide_message_input",
        titleResName = "purpletv_settings_hide_message_input",
        value = BooleanValue(false)
    ),
    HIDE_BITS_BUTTON(
        prefKey = "hide_bits_button",
        titleResName = "purpletv_settings_hide_bits_button",
        value = BooleanValue(false)
    ),
    UPDATE_CHANNEL(
        prefKey = "update_channel",
        titleResName = "purpletv_settings_updater_channel",
        value = ListValue(UpdateChannel::class)
    ),
    PROXY_LIST(
        prefKey = "proxy_v3",
        titleResName = "purpletv_settings_proxy",
        value = ListValue(ProxyImpl::class)
    ),
    VODHUNTER(
        prefKey = "vodhunter",
        titleResName = "purpletv_settings_vodhunter",
        value = BooleanValue()
    ),

    /**
     * ThirdParty stuff
     */
    PRONOUNS("pronouns", "purpletv_settings_pronouns", BooleanValue()),

    SHOW_REFRESH_BUTTON(
        "show_refresh_button_v2",
        "purpletv_settings_show_refresh_button",
        BooleanValue(false)
    ),
    MOD_LOG_NOTICES("mod_logs_notices", "purpletv_settings_mod_logs_notices", BooleanValue(true)),
    FOLLOWED_FULL_CARDS(
        "followed_full_cards",
        "purpletv_settings_followed_full_cards",
        BooleanValue()
    ),
    HIDE_DISCOVER_TAB("hide_discover_tab", "purpletv_settings_hide_discover_tab", BooleanValue()),
    HIDE_TOP_CHAT_PANEL_VODS(
        "hide_top_chat_panel_vods_v2",
        "purpletv_settings_hide_top_chat_panel_vods",
        BooleanValue()
    ),
    COMPACT_PLAYER_FOLLOW_VIEW(
        "compact_player_follow_view",
        "purpletv_settings_compact_player_follow_view",
        BooleanValue()
    ),
    DISABLE_THEATRE_AUTOPLAY(
        "disable_theatre_autoplay",
        "purpletv_settings_disable_theatre_autoplay",
        BooleanValue()
    ),
    ANON_CHAT_CONNECTION(
        "anon_chat",
        "purpletv_settings_anon_chat_connection",
        BooleanValue(false)
    ),
    HIDE_GAME_SECTION("hide_game_section", "purpletv_settings_hide_game_section", BooleanValue()),

    HIDE_FEATURED_CLIPS_SECTION(
        "hide_featured_clips_section",
        "purpletv_settings_hide_featured_clips_section",
        BooleanValue()
    ),
    FORCE_OTA("force_ota", "purpletv_settings_force_ota", BooleanValue()),
    HIDE_CREATE_BUTTON(
        "hide_create_button",
        "purpletv_settings_hide_create_button",
        BooleanValue()
    ),
    HIDE_FSB(
        "hide_fsb",
        "purpletv_settings_hide_fsb",
        BooleanValue(false)
    ),
    FORCE_EXOPLAYER_FOR_VODS(
        "force_exoplayer_for_vods",
        "purpletv_settings_force_exoplayer_for_vods",
        BooleanValue()
    ),
    PREVENT_MOD_CLEAR("prevent_mod_clear", "purpletv_settings_prevent_mod_clear", BooleanValue()),
    FORCE_TOOLBAR_SEARCH_BUTTON(
        "force_toolbar_search_button",
        "purpletv_settings_force_toolbar_search_button",
        BooleanValue()
    ),
    STETHO_INTERCEPTOR(
        "stetho_interceptor",
        "purpletv_settings_stetho_interceptor",
        BooleanValue()
    ),
    FORCE_TABLET_MODE("force_tablet_mode", "purpletv_settings_force_tablet_mode", BooleanValue()),
    DISCOVERY_FEED("discovery_feed", "purpletv_settings_discovery_feed", BooleanValue(true)),
    DISCOVERY_FEED_CLIPS(
        "discovery_feed_clips",
        "purpletv_settings_discovery_feed_clips",
        BooleanValue(true)
    ),
    STORIES("stories", "purpletv_settings_stories", BooleanValue(true)),
    DEBUG_STATE_MACHINE(
        "shil_state_machine_on",
        "purpletv_settings_debug_state_machine",
        BooleanValue(true)
    ),
    DISABLE_COMSCORE("disable_comscore", "purpletv_settings_disable_comscore", BooleanValue(true)),
    DISABLE_GOOGLE_PLAY_SERVICES(
        "disable_google_play_services",
        "purpletv_settings_disable_google_play_services",
        BooleanValue(false)
    ),
    HIDE_DISCOVER_FEED(
        "hide_discover_feed",
        "purpletv_settings_hide_discover_feed",
        BooleanValue()
    ),
    PINNED_CHEER("pinned_cheer", "purpletv_settings_pinned_cheer", BooleanValue(true)),

    // LIST
    BOTTOM_NAVBAR_POSITION(
        "bottom_navbar_position",
        "purpletv_settings_bottom_navbar_position",
        ListValue(BottomNavbarPosition::class)
    ),

    EMOTE_QUALITY(
        "emote_quality",
        "purpletv_settings_emote_quality",
        ListValue(EmoteQuality::class)
    ),
    DISPLAY_NAME("display_name", "purpletv_settings_display_name", ListValue(DisplayName::class)),


    // INT
    LANDSCAPE_CHAT_OPACITY(
        "landscape_chat_opacity",
        "purpletv_settings_landscape_chat_opacity",
        IntRangeValue(0, 100, 30)
    ),
    CUSTOM_PROXY_URL(
        prefKey = "custom_proxy_url_v2",
        titleResName = "orange_custom_proxy_url",
        value = StringValue("")
    ),
    FAST_EMOTE_AUTOCOMPLETE(
        prefKey = "fast_emote_autocomplete",
        titleResName = "purpletv_fast_emote_autocomplete",
        value = BooleanValue(true)
    );

    constructor(prefKey: String, titleResName: String, value: ValueHolder) : this(
        preferenceKey = prefKey,
        titleResName = titleResName,
        summaryResName = titleResName + "_desc",
        defaultHolder = value,
        valueHolder = value
    )

    fun asBoolean(): Boolean {
        return getBoolean(holder = this.valueHolder)
    }

    fun asIntRange(): IntRangeValue {
        return getIntRange(holder = this.valueHolder)
    }

    fun <T : Variant> isVariant(variant: T): Boolean {
        return asVariant<T>() == variant
    }

    fun <T : Variant> asVariant(): T {
        return getVariant(holder = this.valueHolder)
    }

    fun asInt(): Int {
        return getInt(holder = this.valueHolder)
    }

    fun asString(): String {
        return getString(holder = this.valueHolder)
    }

    fun <T : Any> asObject(): T? {
        return getObject(holder = this.valueHolder)
    }

    fun setValue(value: Any?) {
        valueHolder.setValue(value)
        save()
    }

    private fun save() {
        PrefManager.saveToPreferences(this)
    }

    companion object {
        fun getInt(holder: ValueHolder): Int {
            if (holder is IntValue) {
                return holder.getValue()
            }
            if (holder is IntRangeValue) {
                return holder.getValue()
            }

            throw IllegalStateException("$holder")
        }

        fun getBoolean(holder: ValueHolder): Boolean {
            if (holder is BooleanValue) {
                return holder.getValue()
            }

            throw IllegalStateException("$holder")
        }

        fun getString(holder: ValueHolder): String {
            if (holder is StringValue) {
                return holder.getValue()
            }
            if (holder is ListValue<*>) {
                return holder.getValue()
            }
            if (holder is ObjectValue<*>) {
                return holder.getValue()
            }

            throw IllegalStateException("$holder")
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Variant> getVariant(holder: ValueHolder): T {
            if (holder is ListValue<*>) {
                return holder.getVariant() as T
            }

            throw IllegalStateException("$holder")
        }

        fun getIntRange(holder: ValueHolder): IntRangeValue {
            if (holder is IntRangeValue) {
                return holder
            }

            throw IllegalStateException("$holder")
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> getObject(holder: ValueHolder): T? {
            if (holder is ObjectValue<*>) {
                return holder.getObject() as T?
            }

            throw IllegalStateException("$holder")
        }

        fun findByKey(prefKey: String): Flag? {
            return EnumSet.allOf(Flag::class.java).firstOrNull {
                it.preferenceKey == prefKey
            }
        }
    }
}