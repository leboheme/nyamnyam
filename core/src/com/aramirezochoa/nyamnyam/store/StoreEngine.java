package com.aramirezochoa.nyamnyam.store;

import com.aramirezochoa.nyamnyam.screen.store.StoreItem;

/**
 * Created by leboheme on 20/02/15.
 */
public interface StoreEngine {

    void create();

    void destroy();

    void purchaseItem(StoreItem storeItem, StoreTransactionListener storeTransactionListener);

    void consumeItem(StoreItem item, String token, StoreTransactionListener storeTransactionListener);
}
