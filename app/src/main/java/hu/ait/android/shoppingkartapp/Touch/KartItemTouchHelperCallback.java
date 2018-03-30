package hu.ait.android.shoppingkartapp.Touch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by johnc on 11/7/2017.
 */

public class KartItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private KartItemTouchHelperAdapter kartItemTouchHelperAdapter;

    public KartItemTouchHelperCallback(KartItemTouchHelperAdapter kartItemTouchHelperAdapter) {
        this.kartItemTouchHelperAdapter = kartItemTouchHelperAdapter;

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        kartItemTouchHelperAdapter.onItemMove(
                viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        kartItemTouchHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
