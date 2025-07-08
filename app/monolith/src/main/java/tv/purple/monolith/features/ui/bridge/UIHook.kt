package tv.purple.monolith.features.ui.bridge

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.compose.ui.platform.ComposeView
import androidx.mediarouter.app.MediaRouteButton
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.util.ViewUtil.changeVisibility
import tv.purple.monolith.core.util.ViewUtil.isLandscapeOrientation
import tv.purple.monolith.core.util.ViewUtil.isVisible
import tv.purple.monolith.features.ui.misc.AlphaBuildDialog
import tv.twitch.android.feature.followed.model.FollowingContentItemCollection
import tv.twitch.android.shared.chat.ChatViewDelegate
import tv.twitch.android.shared.chat.emotecard.FollowButtonUiModel
import tv.twitch.android.shared.messageinput.impl.ChatMessageInputViewDelegate

object UIHook {
    @JvmStatic
    val shouldShowVideoDebugPanel get() = Flag.SHOW_STATS_BUTTON.asBoolean()

    @JvmStatic
    val forwardSeek get() = Flag.FORWARD_SEEK.asInt()

    @JvmStatic
    val rewindSeek get() = -Flag.REWIND_SEEK.asInt()

    @JvmStatic
    val hideLeaderboards get() = Flag.HIDE_LEADERBOARDS.asBoolean()

    @JvmStatic
    val showFullCards get() = Flag.FOLLOWED_FULL_CARDS.asBoolean()

    @JvmStatic
    fun maybeHideCastButton(var1: Boolean): Boolean {
        if (Flag.DISABLE_CAST_BUTTON.asBoolean()) {
            return false;
        }

        return var1;
    }

    @JvmStatic
    fun maybeHideCastButton(castButton: MediaRouteButton) {
        if (Flag.DISABLE_CAST_BUTTON.asBoolean()) {
            castButton.changeVisibility(false)
        }
    }

    @JvmStatic
    fun maybeShowAlphaBuildDialog(context: Context) {
        if (!Flag.ALPHA_BUILD_SHOWN.asBoolean()) {
            AlphaBuildDialog(context).show()
        }
    }

    @JvmStatic
    fun filterFollowingContentItemCollection(
        itemCollections: MutableList<out FollowingContentItemCollection>
    ): MutableList<out FollowingContentItemCollection> {
        return itemCollections.filter {
            when (it) {
                is FollowingContentItemCollection.Categories -> !Flag.HIDE_GAME_SECTION.asBoolean()
                is FollowingContentItemCollection.ResumeWatching -> !Flag.HIDE_RESUME_WATCHING_SECTION.asBoolean()
                is FollowingContentItemCollection.OfflineChannels -> !Flag.HIDE_OFFLINE_CHANNEL_SECTION.asBoolean()
                else -> true
            }
        }.toMutableList()
    }

    @JvmStatic
    fun maybeHideChatSettingsButton(openChatSettingsButton: ImageView) {
        if (!Flag.CHAT_SETTINGS.asBoolean()) {
            openChatSettingsButton.changeVisibility(false)
        }
    }

    @JvmStatic
    val disableLinkDisclaimer get() = Flag.DISABLE_LINK_DISCLAIMER.asBoolean()

    @JvmStatic
    fun maybeHideFollowButton(
        view: View?,
        model: FollowButtonUiModel?
    ) {
        view ?: return

        if (Flag.HIDE_UNFOLLOW_BUTTON.asBoolean()) {
            model?.let {
                if (it.isFollowing) {
                    view.changeVisibility(false)
                }
            }
        }
    }

    @JvmStatic
    val showFollowButtonExtended get() = !Flag.HIDE_UNFOLLOW_BUTTON.asBoolean()

    @JvmStatic
    fun maybeHideCreateClipButton(
        createClipButtonComposeView: ComposeView?
    ) {
        if (Flag.HIDE_PLAYER_CREATE_CLIP_BUTTON.asBoolean()) {
            createClipButtonComposeView?.changeVisibility(false)
        }
    }

    @JvmStatic
    fun maybeHideOverlayHeaderButtons(createClipButton: ImageView?, shareButton: ImageView?) {
        if (Flag.HIDE_PLAYER_CREATE_CLIP_BUTTON.asBoolean()) {
            createClipButton?.changeVisibility(false)
        }
        if (Flag.HIDE_PLAYER_LIVE_SHARE_BUTTON.asBoolean()) {
            shareButton?.changeVisibility(false)
        }
    }

    @JvmStatic
    val disableMatureContent get() = Flag.DISABLE_MATURE_CONTENT.asBoolean()

    @JvmStatic
    fun shouldHideMessageInput(delegate: ChatMessageInputViewDelegate): Boolean =
        Flag.AUTO_HIDE_MESSAGE_INPUT.asBoolean() && delegate.context.isLandscapeOrientation()
                || Flag.HIDE_MESSAGE_INPUT.asBoolean()

    @JvmStatic
    fun onChatViewPresenterConfigurationChanged(delegate: ChatViewDelegate) {
        if (!delegate.contentView.isVisible()) {
            delegate.contentView.changeVisibility(false)
            return
        }

        if (!Flag.AUTO_HIDE_MESSAGE_INPUT.asBoolean() && !Flag.HIDE_MESSAGE_INPUT.asBoolean()) {
            return
        }

        delegate.messageInputViewDelegate.apply {
            if (Flag.HIDE_MESSAGE_INPUT.asBoolean() || context.isLandscapeOrientation()) {
                hide()
            } else {
                show()
            }
        }
    }

    @JvmStatic
    fun hideCreateClipButton(): Boolean {
        return Flag.HIDE_PLAYER_CREATE_CLIP_BUTTON.asBoolean()
    }

    @JvmStatic
    fun hideShareOption(): Boolean {
        return Flag.HIDE_PLAYER_LIVE_SHARE_BUTTON.asBoolean()
    }
}