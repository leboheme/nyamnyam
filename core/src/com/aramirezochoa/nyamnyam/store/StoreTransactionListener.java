package com.aramirezochoa.nyamnyam.store;

/**
 * Created by leboheme on 20/02/15.
 */
public interface StoreTransactionListener {

    void transactionFinished(int responseCode, String token);

}
