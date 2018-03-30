package hu.ait.android.shoppingkartapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import hu.ait.android.shoppingkartapp.Data.KartItem;
import hu.ait.android.shoppingkartapp.MainActivity;
import hu.ait.android.shoppingkartapp.R;
import hu.ait.android.shoppingkartapp.Touch.KartItemTouchHelperAdapter;
import io.realm.Realm;

/**
 * Created by johnc on 11/7/2017.
 */
public class KartRecyclerAdapter extends RecyclerView.Adapter<KartRecyclerAdapter.ViewHolder> implements KartItemTouchHelperAdapter {

    public static final int POPUP_SCALE = 1100;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDescription;
        private TextView tvItemName;
        private TextView tvItemCost;
        private CheckBox cbPurchased;
        private ImageView ivItemImage;
        private Button btnDescription;
        private Button btnEdit;
        private Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvDescription = itemView.findViewById(R.id.etItemDesc);
            tvItemCost = itemView.findViewById(R.id.tvItemCost);
            cbPurchased = itemView.findViewById(R.id.cbPurchased);
            ivItemImage = itemView.findViewById(R.id.itemImage);
            btnDescription = itemView.findViewById(R.id.btnItemDetails);
            btnEdit = itemView.findViewById(R.id.btnItemEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private List<KartItem> itemList;
    private Context context;
    private Realm realmKart;
    private PopupWindow pw;
    private int lastPosition = -1;

    public KartRecyclerAdapter(List<KartItem> itemList, Context context, Realm realmKart) {
        this.itemList = itemList;
        this.context = context;
        this.realmKart = realmKart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_row, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.tvItemName.setText(itemList.get(position).getItemTitle());
        viewHolder.tvItemCost.setText(itemList.get(position).getItemCost().toString());
        viewHolder.ivItemImage.setImageResource(R.drawable.electronics);
        viewHolder.cbPurchased.setChecked(itemList.get(position).isDone());
        viewHolder.ivItemImage.setImageResource(itemList.get(position).getItemType().getIconId());

        viewHolder.cbPurchased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                realmKart.beginTransaction();
                itemList.get(position).setDone(isChecked);
                realmKart.commitTransaction();
            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDismiss(viewHolder.getAdapterPosition());
            }
        });
        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).showEditItemActivity(
                        itemList.get(viewHolder.getAdapterPosition()).getItemID(),
                        viewHolder.getAdapterPosition());
            }
        });

        viewHolder.btnDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatePopupWindow(view, itemList.get(position).getItemDescription());
            }
        });

        setAnimation(viewHolder.itemView, position);
    }

    private void initiatePopupWindow(View view, String message) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_window, (ViewGroup) view.findViewById(R.id.popup_element));
            pw = new PopupWindow(layout, POPUP_SCALE, POPUP_SCALE, true);
            ((TextView) pw.getContentView().findViewById(R.id.popup_message)).setText(message);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);


            Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
            cancelButton.setOnClickListener(cancel_button_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addItem(KartItem item) {
        itemList.add(item);
        notifyDataSetChanged();
    }

    public void updateItem(int index, KartItem item) {
        itemList.set(index, item);
        notifyItemChanged(index);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onItemDismiss(int position) {
        KartItem itemToDelete = itemList.get(position);
        ((MainActivity) context).deleteItem(itemToDelete);
        itemList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(itemList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(itemList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void clearList() {
        if (itemList.size() > 0) {
            for (int i = 0; i < itemList.size(); i++) {
                ((MainActivity) context).deleteItem(itemList.get(i));
            }
            itemList.clear();
            notifyDataSetChanged();
        }
    }
}