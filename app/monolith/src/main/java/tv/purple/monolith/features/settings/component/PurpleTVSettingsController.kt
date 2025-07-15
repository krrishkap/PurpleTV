package tv.purple.monolith.features.settings.component

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.component.blacklist.Blacklist
import tv.purple.monolith.component.highlighter.Highlighter
import tv.purple.monolith.core.CoreUtil
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.ResManager.fromResToString
import tv.purple.monolith.core.ResManager.fromResToStringId
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.core.Variant
import tv.purple.monolith.features.proxy.Proxy
import tv.purple.monolith.features.settings.bridge.factory.TwitchItemsFactory
import tv.purple.monolith.features.settings.bridge.model.PurpleTVSubMenu
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVChatSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVDevSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVGestureSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVInfoSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVPlayerSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.PurpleTVViewSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartyBadgesSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartyEmotesSettingsFragment
import tv.purple.monolith.features.settings.bridge.settings.submenu.PurpleTVThirdPartySettingsFragment
import tv.purple.monolith.features.settings.bridge.slider.SliderModel
import tv.twitch.android.models.settings.SettingsDestination
import tv.twitch.android.routing.routers.IFragmentRouter
import tv.twitch.android.routing.routers.WebViewRouter
import tv.twitch.android.shared.settings.SettingsNavigationController
import tv.twitch.android.shared.ui.menus.SettingsPreferencesController
import tv.twitch.android.shared.ui.menus.core.MenuModel
import tv.twitch.android.shared.ui.menus.togglemenu.ToggleMenuModel
import javax.inject.Inject

class PurpleTVSettingsController @Inject constructor(
    private val activity: FragmentActivity,
    private val fragmentRouter: IFragmentRouter,
    private val webViewRouter: WebViewRouter,
    private val highlighter: Highlighter,
    private val blacklist: Blacklist,
    private val twitchItemsFactory: TwitchItemsFactory,
    private val proxy: Proxy
) : SettingsPreferencesController, SliderModel.SliderListener, SettingsNavigationController {
    override fun updatePreferenceBooleanState(toggleMenuModel: ToggleMenuModel, state: Boolean) {
        val key = toggleMenuModel.orangeKey
        if (key.isNullOrBlank()) {
            return
        }

        val flag = Flag.findByKey(key) ?: run {
            LoggerImpl.error("Flag not found: $key")
            return
        }

        flag.setValue(state)
        checkIfNeedRestart(flag = flag)
    }

    private fun checkIfNeedRestart(flag: Flag) {
        if (!RESTART_FLAGS.contains(flag)) {
            return
        }

        val alertDialog = activity.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(RES_STRINGS.purpletv_restart_app_title.fromResToString())
                setPositiveButton(RES_STRINGS.purpletv_restart_app.fromResToString()) { _, _ ->
                    CoreUtil.restart(activity = activity)
                }
                setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ ->
                    dialog.dismiss()
                }
            }

            builder.create()
        }

        alertDialog.show()
    }

    fun getMainSettingModels(): Collection<MenuModel> {
        return PurpleTVSubMenu.values().filter {
            (it.items.isNotEmpty() || ALWAYS_SHOW_MENUS.contains(it)) && !SUB_SETTINGS.contains(it)
        }.map {
            twitchItemsFactory.createSubMenuModel(
                it.title.fromResToString(),
                it.desc?.fromResToString(),
                it.destination
            )
        }
    }

    fun onDropDownMenuItemSelection(flag: Flag, variant: Variant) {
        if (flag.asString() == variant.toString()) {
            return
        }

        flag.setValue(variant)
        checkIfNeedRestart(flag)
    }

    override fun onSliderValueChanged(flag: Flag, value: Int) {
        if (flag.asInt() == value) {
            return
        }

        flag.setValue(value)
        checkIfNeedRestart(flag = flag)
    }

    override fun navigateToSettingFragment(settingsDestination: SettingsDestination, p1: Bundle?) {
        if (settingsDestination == SettingsDestination.PurpleWiki) {
            WebViewRouter.DefaultImpls.`showWebViewActivity$default`(
                webViewRouter,
                activity,
                RES_STRINGS.purpletv_settings_menu_wiki_url.fromResToStringId(),
                RES_STRINGS.purpletv_settings_menu_wiki.fromResToStringId(),
                false,
                8,
                null
            )
            return
        }

        when (settingsDestination) {
            SettingsDestination.PurpleThirdParty -> PurpleTVThirdPartySettingsFragment()
            SettingsDestination.PurpleThirdPartyEmotes -> PurpleTVThirdPartyEmotesSettingsFragment()
            SettingsDestination.PurpleThirdPartyBadges -> PurpleTVThirdPartyBadgesSettingsFragment()
            SettingsDestination.PurpleChat -> PurpleTVChatSettingsFragment()
            SettingsDestination.PurplePlayer -> PurpleTVPlayerSettingsFragment()
            SettingsDestination.PurpleView -> PurpleTVViewSettingsFragment()
            SettingsDestination.PurpleDev -> PurpleTVDevSettingsFragment()
            SettingsDestination.PurpleInfo -> PurpleTVInfoSettingsFragment()
            SettingsDestination.PurpleGestures -> PurpleTVGestureSettingsFragment()
            SettingsDestination.PurpleHighlighter -> highlighter.createHighlighterFragment()
            SettingsDestination.PurpleBlacklist -> blacklist.createBlacklistFragment()
            SettingsDestination.PurpleCustomProxy -> proxy.createCustomUrlFragment()

            else -> null
        }?.let { fragment ->
            fragmentRouter.addOrRecreateFragmentWithDefaultTransitions(
                activity, fragment, settingsDestination.toString(), Bundle()
            )
            return
        }

        when (settingsDestination) {
            SettingsDestination.PurpleHighlightColor -> {
                highlighter.showChangeMentionHighlightColorDialog(activity = activity)
                return
            }

            else -> {}
        }
    }

    fun getContext(): Context {
        return activity
    }

    companion object {
        private val SUB_SETTINGS = setOf(
            PurpleTVSubMenu.ThirdPartyEmotes,
            PurpleTVSubMenu.ThirdPartyBadges,
        )

        private val ALWAYS_SHOW_MENUS = setOf(
            PurpleTVSubMenu.Wiki,
            PurpleTVSubMenu.Info,
        )

        private val RESTART_FLAGS = setOf(
            Flag.BOTTOM_NAVBAR_POSITION,
            Flag.DEV_MODE,
            Flag.STETHO_INTERCEPTOR,
            Flag.OKHTTP_LOGGING,
            Flag.PROXY_LIST,
            Flag.REYOHOHO_BADGES
        )
    }
}
