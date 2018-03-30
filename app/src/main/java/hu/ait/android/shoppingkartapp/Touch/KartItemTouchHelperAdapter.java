package hu.ait.android.shoppingkartapp.Touch;

/**
 * Created by johnc on 11/7/2017.
 */

public interface KartItemTouchHelperAdapter {
    void onItemDismiss(int position);

    void onItemMove(int fromPosition, int toPosition);
}
