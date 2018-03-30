package hu.ait.android.shoppingkartapp.Data;

import hu.ait.android.shoppingkartapp.R;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by johnc on 11/7/2017.
 */

public class KartItem extends RealmObject {

    public enum ItemType {

        FOOD(0, R.drawable.food),
        BOOK(1, R.drawable.book),
        ELECTRONIC(2, R.drawable.electronics);

        private int value;
        private int iconId;

        private ItemType(int value, int iconId) {
            this.value = value;
            this.iconId = iconId;
        }

        public int getValue() {
            return value;
        }

        public int getIconId() {
            return iconId;
        }

        public static ItemType fromInt(int value) {
            for (ItemType p : ItemType.values()) {
                if (p.value == value) {
                    return p;
                }
            }
            return FOOD;
        }
    }

    @PrimaryKey
    private String itemID;

    private String itemTitle;
    private String createDate;
    private String itemDescription;
    private String itemCost;
    private boolean done;
    private int itemType;

    public KartItem() {
    }

    public KartItem(String itemTitle, String itemDescription, String createDate, int itemType, boolean done) {
        this.itemDescription = itemDescription;
        this.itemTitle = itemTitle;
        this.itemType = itemType;
        this.createDate = createDate;
        this.done = done;
    }

    public String getItemCost() {
        return itemCost;
    }

    public void setItemCost(String itemCost) {
        this.itemCost = itemCost;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public ItemType getItemType() {
        return ItemType.fromInt(itemType);
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getCreateDate() {
        return createDate;
    }

    public boolean isDone() {
        return done;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemTitle(String todoTitle) {
        this.itemTitle = todoTitle;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
