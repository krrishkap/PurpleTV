package tv.twitch.android.shared.ui.menus.togglemenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.view.View;

import kotlin.NotImplementedError;
import tv.twitch.android.core.mvp.lifecycle.VisibilityProvider;
import tv.twitch.android.shared.ui.menus.SettingActionListener;
import tv.twitch.android.shared.ui.menus.SettingsPreferencesController;
import tv.twitch.android.shared.ui.menus.core.MenuModel;


public class ToggleMenuModel extends MenuModel.SingleItemModel { // TODO: __REMOVE_FINAL
    private String orangeKey = null; // TODO: __INJECT_FIELD

    public ToggleMenuModel(String str, CharSequence charSequence, String str2, Spannable spannable, boolean z, boolean z2, Drawable drawable, String str3, boolean z3, String str4, Integer num, Integer num2, SettingsPreferencesController.SettingsPreference settingsPreference, View.OnClickListener onClickListener, Integer num3) {
        super(null, null, null, null, null, null);

        /* ... */
    }

    public final String getEventName() {
        throw new NotImplementedError();
    }

    @Override
    protected ToggleMenuRecyclerItem toRecyclerAdapterItem(Context context, SettingActionListener settingActionListener, VisibilityProvider visibilityProvider) {
        throw new NotImplementedError();
    }

    public boolean getToggleState() { // TODO: __REMOVE_FINAL
        throw new NotImplementedError();
    }

    public void setOrangeKey(String key) { // TODO: __INJECT_METHOD
        orangeKey = key;
    }

    public String getOrangeKey() { // TODO: __INJECT_METHOD
        return orangeKey;
    }
}
