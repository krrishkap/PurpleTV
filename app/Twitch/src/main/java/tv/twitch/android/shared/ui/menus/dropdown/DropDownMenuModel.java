package tv.twitch.android.shared.ui.menus.dropdown;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import tv.twitch.android.core.mvp.view.VisibilityProvider;

import kotlin.NotImplementedError;
import tv.twitch.android.core.mvp.lifecycle.VisibilityProvider;
import tv.twitch.android.shared.ui.menus.SettingActionListener;
import tv.twitch.android.shared.ui.menus.core.MenuModel;

public class DropDownMenuModel<T> extends MenuModel.SingleItemModel { // TODO: __REMOVE_FINAL
    public interface DropDownMenuItemSelection<T> {
        void onDropDownItemSelected(DropDownMenuModel<T> dropDownMenuModel, int i);
    }

    public DropDownMenuModel(ArrayAdapter<T> arrayAdapter, int i, String str, CharSequence charSequence, String str2, View.OnClickListener onClickListener, DropDownMenuItemSelection<T> dropDownMenuItemSelection) {
        super(null, null, null, null, null, null);

        /* ... */
    }

    public int getSelectedOption() { // TODO: __REMOVE_FINAL
        throw new NotImplementedError();
    }

    @Override
    protected DropDownMenuRecyclerItem<T> toRecyclerAdapterItem(Context context, SettingActionListener settingActionListener, VisibilityProvider visibilityProvider) {
        throw new NotImplementedError();
    }
}
