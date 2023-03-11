package com.aramirezochoa.nyamnyam.screen.store;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by boheme on 19/02/15.
 */
public enum StoreItem {
    THREE_LIVES("three_lives", "0.50€"),
    PIZZA("pizza", "0.50€"),
    BURGER("burger", "0.50€"),
    HOT_DOG("hot_dog", "0.50€"),
    MEAT("meat", "0.50€"),
    PRO_SHOOTER("pro_shooter", "1.00€");

    private final String itemId;
    private String price;

    StoreItem(String itemId, String defaultPrice) {
        this.itemId = itemId;
        this.price = defaultPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price.replaceAll("\\s", "");
    }

    public static StoreItem randomGift() {
        switch (MathUtils.random(4)) {
            case 0:
                return StoreItem.PIZZA;
            case 1:
                return StoreItem.BURGER;
            case 2:
                return StoreItem.HOT_DOG;
            case 3:
                return StoreItem.MEAT;
        }
        return StoreItem.PIZZA;
    }
}
