package tv.purple.monolith.features.settings.bridge.model

import android.content.Context
import android.widget.ArrayAdapter
import tv.purple.monolith.core.ResManager.fromResToLayoutId
import tv.purple.monolith.core.ResManager.fromResToString
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.core.Variant
import tv.purple.monolith.core.models.flag.core.Variant.Companion.getPosition
import tv.purple.monolith.core.models.flag.core.Variant.Companion.getVariants
import tv.purple.monolith.core.models.flag.variants.ProxyImpl
import tv.purple.monolith.features.settings.component.PurpleTVSettingsController
import tv.twitch.android.core.adapters.TwitchArrayAdapter
import tv.twitch.android.core.adapters.TwitchArrayAdapterModel
import tv.twitch.android.shared.ui.menus.dropdown.DropDownMenuModel
import tv.twitch.android.shared.ui.menus.dropdown.DropDownMenuModel.DropDownMenuItemSelection

class DropDownMenuModelExt<T : Variant>(
    private val flag: Flag,
    controller: PurpleTVSettingsController,
    raw: Boolean = false
) : DropDownMenuModel<TwitchArrayAdapterModel>(
    flag.asVariant<T>().getVariants().toAdapter(controller.getContext(), flag, raw),
    flag.asVariant<T>().getVariants().getPosition(flag.asVariant()),
    flag.titleResName.fromResToString(),
    flag.summaryResName?.fromResToString(),
    null,
    null,
    DropDownMenuItemSelection { _, position ->
        controller.onDropDownMenuItemSelection(
            flag,
            flag.asVariant<T>().getVariants()[position]
        )
    }
) {
    override fun getSelectedOption(): Int {
        return flag.asVariant<T>().getVariants().getPosition(flag.asVariant())
    }

    companion object {
        private fun <E> Collection<E>.toAdapter(
            context: Context,
            flag: Flag,
            raw: Boolean
        ): ArrayAdapter<TwitchArrayAdapterModel> {
            return TwitchArrayAdapter(
                context,
                map { variant ->
                    TwitchArrayAdapterModel {
                        if (raw) {
                            variant.toString()
                        } else if (variant is ProxyImpl) {
                            variant.desc
                        } else {
                            "purpletv_${flag.preferenceKey}_$variant".fromResToString()
                        }
                    }
                },
                "twitch_spinner_dropdown_item".fromResToLayoutId()
            )
        }
    }
}