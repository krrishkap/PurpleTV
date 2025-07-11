package tv.purple.monolith.features.chapters.bridge

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import tv.purple.monolith.bridge.PurpleTVAppContainer
import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.bridge.ex.ISeekbarOverlayPresenter
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.util.ViewUtil.changeVisibility
import tv.purple.monolith.features.chapters.VodChapters.Companion.fixVodId
import tv.twitch.android.models.videos.VodModel
import tv.twitch.android.shared.player.overlay.PlayerOverlayViewDelegate

object ChaptersHook {
    private val chapters by lazy {
        PurpleTVAppContainer.getInstance().provideChapters()
    }

    @JvmStatic
    fun getChaptersButton(view: View): ImageView? {
        return chapters.createChaptersButton(view = view).also {
            LoggerImpl.debug("ChaptersHook->getChaptersButton: $it")
        }
    }

    @JvmStatic
    fun bindChaptersButton(
        delegate: PlayerOverlayViewDelegate,
        vod: VodModel,
        presenter: ISeekbarOverlayPresenter
    ) {
        val button = chapters.getChaptersButton(delegate)
        button?.changeVisibility(isVisible = true)
        button?.setOnClickListener {
            val fragmentManager = (button.context as FragmentActivity).supportFragmentManager
            val fragment = chapters.getChaptersFragment()
            fragment.bindSeekPresenter(presenter)
            fragment.show(fragmentManager, RES_STRINGS.purpletv_chapters_tag)
            fragment.load(fixVodId(vod.id))
        }
    }

    @JvmStatic
    fun hideChaptersButton(delegate: PlayerOverlayViewDelegate) {
        chapters.getChaptersButton(delegate)?.changeVisibility(false)
    }
}