package tv.purple.monolith.features.chat.bridge

import android.content.Context
import android.view.View
import android.view.ViewGroup
import io.reactivex.Flowable
import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.core.ResManager.fromResToString
import tv.purple.monolith.core.ResManager.fromResToStringId
import tv.purple.monolith.core.util.ViewUtil.getView
import tv.purple.monolith.core.util.ViewUtil.inflate
import tv.purple.monolith.features.chat.ChatHookProvider
import tv.twitch.android.core.mvp.viewdelegate.ViewDelegateState
import tv.twitch.android.shared.chat.settings.entry.ChatSettingsViewDelegate
import tv.twitch.android.shared.chat.settings.viewutil.SettingsViewDelegateItemsUtilKt
import tv.twitch.android.shared.ui.menus.infomenu.InfoMenuViewDelegate
import tv.twitch.android.shared.ui.menus.togglemenu.SimpleToggleRowViewDelegate

class PurpleTVChatSettingsViewDelegate(
    private val delegate: ChatSettingsViewDelegate,
    private val chatHookProvider: ChatHookProvider
) {
    private val contentView: View get() = delegate.contentView
    private val context: Context get() = delegate.context

    private val orangeContainer: ViewGroup = contentView.getView(
        resName = RES_STRINGS.chat_settings_bottom_sheet__purpletv_container
    )
    private val actionRefreshEmotesAndBadges = createInfoRowItem(
        container = orangeContainer,
        context = context,
    )

    init {
        for (v in listOf(
            actionRefreshEmotesAndBadges
        ).map { it.contentView }) {
            orangeContainer.addView(v)
        }
        renderCurrentState()
    }

    fun render(state: ViewDelegateState) {
        renderCurrentState()
    }

    private fun renderCurrentState() {
        actionRefreshEmotesAndBadges.render(createInfoState(RES_STRINGS.purpletv_chat_settings_refresh))
    }

    fun injectEvents(listOf: List<Flowable<ChatSettingsViewDelegate.ChatSettingsEvents>>): List<Flowable<ChatSettingsViewDelegate.ChatSettingsEvents>> {
        return listOf.toMutableList().apply {
            add(actionRefreshEmotesAndBadges.eventObserver().doOnNext {
                chatHookProvider.forceUpdateEmotesAndBadges()
            }.map {
                Closable()
            })
        }
    }

    companion object {
        private fun createToggleRowItem(
            container: ViewGroup,
            resName: String
        ): SimpleToggleRowViewDelegate {
            return SimpleToggleRowViewDelegate(
                container.inflate(resName = "toggle_row_item"),
                resName.fromResToStringId()
            )
        }

        private fun createInfoRowItem(
            container: ViewGroup,
            context: Context
        ): InfoMenuViewDelegate {
            return SettingsViewDelegateItemsUtilKt.getValueRowItem(
                context,
                container,
            )
        }

        private fun createInfoState(resName: String): InfoMenuViewDelegate.State {
            return InfoMenuViewDelegate.State(
                resName.fromResToString(),
                null,
                null,
                false
            )
        }
    }
}