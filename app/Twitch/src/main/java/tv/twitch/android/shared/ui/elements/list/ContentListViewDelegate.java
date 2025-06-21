package tv.twitch.android.shared.ui.elements.list;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContentListViewDelegate {
    private RecyclerView.Adapter<?> adapter;
    private GridLayoutManager layoutManager;
    private RecyclerView gridView;

    public final RecyclerView.Adapter<?> getAdapter() { // TODO: __INJECT_CODE
        return adapter;
    }

    public final void fastScrollToPosition(int i) { // TODO: __INJECT_CODE
        GridLayoutManager manager = layoutManager;
        if (manager == null) {
            return;
        }
        RecyclerView grid = gridView;
        if (grid == null) {
            return;
        }

        grid.stopScroll();
        manager.scrollToPositionWithOffset(i, 0);
    }
}
