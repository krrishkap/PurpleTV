package tv.purple.monolith.features.ui

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.core.PrefManager
import tv.purple.monolith.core.ResManager.fromResToLayoutId
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.variants.BottomNavbarPosition
import tv.purple.monolith.core.util.ViewUtil.changeVisibility
import tv.purple.monolith.core.util.ViewUtil.getView
import tv.purple.monolith.features.ui.bridge.HideKeyboardActionButtonClicked
import tv.purple.monolith.models.IPurpleTVAppContainer
import tv.twitch.android.app.core.ApplicationContext
import tv.twitch.android.app.core.Experience
import tv.twitch.android.core.mvp.viewdelegate.EventDispatcher
import tv.twitch.android.shared.chat.ChatViewDelegate
import tv.twitch.android.shared.chat.chatheader.ChatHeaderPresenter
import tv.twitch.android.shared.chat.chatheader.ChatHeaderViewDelegate
import tv.twitch.android.shared.ui.elements.AnimationUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UI @Inject constructor(

) {
    companion object {
        @JvmStatic
        fun get(): UI {
            return (ApplicationContext.getInstance()
                .getContext() as IPurpleTVAppContainer).provideUI()
        }

        @JvmStatic
        val isCreatorButtonVisible get() = !Flag.HIDE_CREATE_BUTTON.asBoolean()

        @JvmStatic
        fun hookPlayerMetadataViewId(org: Int): Int {
            if (!Flag.COMPACT_PLAYER_FOLLOW_VIEW.asBoolean()) {
                return org
            }

            return RES_STRINGS.purpletv_player_metadata_view_extended.fromResToLayoutId()
        }

        @JvmStatic
        fun isBottomBarVisible(org: Boolean): Boolean {
            if (!org) {
                return false
            }

            return Flag.BOTTOM_NAVBAR_POSITION.asVariant<BottomNavbarPosition>() != BottomNavbarPosition.Hidden
        }

        @JvmStatic
        fun attachToMainActivityWrapper(wrapper: ViewGroup?) {
            wrapper ?: return

            val frame = wrapper.getView<FrameLayout>(resName = "main_activity__main_wrapper")
            val bottomNavigation = wrapper.getView<FrameLayout>(resName = "bottom_navigation")

            when (Flag.BOTTOM_NAVBAR_POSITION.asVariant<BottomNavbarPosition>()) {
                BottomNavbarPosition.Top -> {
                    wrapper.removeAllViews()
                    wrapper.addView(bottomNavigation)
                    wrapper.addView(frame)
                }

                BottomNavbarPosition.Hidden -> {
                    bottomNavigation.changeVisibility(false)
                }

                else -> {}
            }
        }

        @JvmStatic
        fun changeLandscapeChatContainerOpacity(viewGroup: ViewGroup?) {
            if (PrefManager.isDarkThemeEnabled) {
                viewGroup?.setBackgroundColor(
                    Color.argb(
                        (255 * (Flag.LANDSCAPE_CHAT_OPACITY.asInt() / 100F)).toInt(),
                        0,
                        0,
                        0
                    )
                )
            }
        }

        @JvmStatic
        fun getHideKeyboardButton(content: ChatHeaderViewDelegate): ImageView {
            return content.contentView.getView<ImageView>("chat_header__input_action").apply {
                changeVisibility(true)
                setOnClickListener {
                    content.pushEvent(HideKeyboardActionButtonClicked)
                }
            }
        }

        @JvmStatic
        fun dispatchHideKeyboard(hideInputEvents: EventDispatcher<Unit>): Function1<ChatHeaderPresenter.Event.ViewEvent, Unit> {
            return {
                hideInputEvents.pushEvent(Unit)
            }
        }

        @JvmStatic
        fun onHideInputClicked(delegate: ChatViewDelegate): Function1<Unit, Unit> {
            return {
                delegate.messageInputViewDelegate?.isVisible?.let {
                    if (it) {
                        AnimationUtil.INSTANCE.setViewVisibilityWithAlphaAnimation(
                            delegate.messageInputViewDelegate.contentView,
                            false,
                            0,
                            0,
                            null
                        )
                    } else {
                        AnimationUtil.INSTANCE.setViewVisibilityWithAlphaAnimation(
                            delegate.messageInputViewDelegate.contentView,
                            true,
                            0,
                            0,
                            null
                        )
                    }
                }
            }
        }

        @JvmStatic
        fun maybeChangeBackgroundColor(view: View?) {
            if (PrefManager.isDarkThemeEnabled) {
                listOf(view).forEach {
                    it?.setBackgroundColor(
                        Color.argb(
                            (255 * (Flag.LANDSCAPE_CHAT_OPACITY.asInt() / 100F)).toInt(),
                            0,
                            0,
                            0
                        )
                    )
                }
            }
        }

        @JvmStatic
        fun hookIsTablet(res: Experience.SupportedExperiences): Experience.SupportedExperiences {
            if (Flag.FORCE_TABLET_MODE.asBoolean()) {
                return Experience.SupportedExperiences.Tablet
            }

            return res
        }

        @JvmStatic
        fun hookIsTablet(res: Boolean): Boolean {
            if (Flag.FORCE_TABLET_MODE.asBoolean()) {
                return true
            }

            return res
        }
    }
}