package tv.purple.monolith.features.settings.bridge.settings

import androidx.fragment.app.FragmentActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import tv.purple.monolith.bridge.RES_STRINGS
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.ResManager.fromResToString
import tv.purple.monolith.features.settings.bridge.factory.TwitchItemsFactory
import tv.purple.monolith.features.settings.bridge.model.PurpleTVSubMenu
import tv.purple.monolith.features.settings.component.PurpleTVSettingsController
import tv.purple.monolith.features.tracking.NopAnalyticsTracker
import tv.twitch.android.shared.settings.SettingsTracker
import tv.twitch.android.shared.ui.menus.core.MenuAdapterBinder

class PurpleTVInfoSettingsPresenter(
    activity: FragmentActivity,
    adapterBinder: MenuAdapterBinder,
    settingsTracker: SettingsTracker,
    controller: PurpleTVSettingsController,
    factory: TwitchItemsFactory
) : BaseSettingsPresenter(
    activity,
    adapterBinder,
    settingsTracker,
    controller,
    PurpleTVSubMenu.Info,
    factory
) {
    private val disposables = CompositeDisposable()

    override fun updateSettingModels() {
        super.updateSettingModels()
        disposables.add(
            NopAnalyticsTracker.get().getPinnyInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LoggerImpl.debug { "Pinny: $it" }
                    if (it.build > 1) {
                        addInfoMenu(
                            title = RES_STRINGS.purpletv_pinny_active_users.fromResToString(it.build),
                            desc = RES_STRINGS.purpletv_pinny_total_users.fromResToString(it.total),
                            func = {}
                        )
                        this.bindSettings()
                    }
                }, Throwable::printStackTrace)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}