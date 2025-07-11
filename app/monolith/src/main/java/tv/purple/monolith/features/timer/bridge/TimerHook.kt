package tv.purple.monolith.features.timer.bridge

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.util.ViewUtil.changeVisibility
import tv.purple.monolith.core.util.ViewUtil.getViewIfExists
import tv.purple.monolith.features.timer.TimerDialog
import tv.purple.monolith.features.tracking.bridge.BugsnagUtil
import tv.twitch.android.core.crashreporter.CrashReporter
import tv.twitch.android.shared.player.overlay.PlayerOverlayViewDelegate

object TimerHook {
    @JvmStatic
    fun maybeShowTimerButton(timerButton: ImageView?) {
        timerButton?.changeVisibility(Flag.SHOW_TIMER_BUTTON.asBoolean())
    }

    @JvmStatic
    fun maybeHideTimerButton(timerButton: ImageView?) {
        timerButton?.changeVisibility(false)
    }

    @JvmStatic
    fun createTimerButton(view: View): ImageView? {
        return view.getViewIfExists(resName = RES_STRINGS.player_control_overlay_widget__timer_button)
    }

    @JvmStatic
    fun getTimerButton(view: View): ImageView? {
        try {
            return createTimerButton(view = view)?.apply {
                setOnClickListener { button ->
                    try {
                        val fragmentActivity = button.context as FragmentActivity
                        TimerDialog.createAndShowTimerDialog(fragmentActivity)
                    } catch (th: Throwable) {
                        BugsnagUtil.logException(
                            th,
                            "TimerHook",
                            CrashReporter.ExceptionType.NON_FATAL
                        )
                    }
                }
                changeVisibility(Flag.SHOW_TIMER_BUTTON.asBoolean())
            }
        } catch (th: Throwable) {
            BugsnagUtil.logException(
                th,
                "TimerHook",
                CrashReporter.ExceptionType.NON_FATAL
            )
        }.also {
            LoggerImpl.debug("TimerHook->getTimerButton: $it")
        }

        return null
    }

    @JvmStatic
    fun bindTimerButton(playerOverlayViewDelegate: PlayerOverlayViewDelegate) {
        getTimerButton(playerOverlayViewDelegate.contentView)?.let {
            LoggerImpl.debug("Binding: $it")
        }
    }
}