package com.aramirezochoa.nyamnyam.store;

import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.aramirezochoa.nyamnyam.screen.store.StoreItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leboheme on 20/02/15.
 */
public enum StoreManager {
    INSTANCE;

    private StoreEngine storeEngine;

    private Map<StoreItem, String> extraItems = new HashMap<StoreItem, String>();

    public void setStoreEngine(StoreEngine storeEngine) {
        this.storeEngine = storeEngine;
    }

    public void init() {
        storeEngine.create();
    }

    public void dispose() {
        storeEngine.destroy();
    }

    public void purchaseItem(StoreItem storeItem, StoreTransactionListener storeTransactionListener) {
        storeEngine.purchaseItem(storeItem, storeTransactionListener);
    }

    public void consumeItem(StoreItem item, String token, StoreTransactionListener storeTransactionListener) {
        storeEngine.consumeItem(item, token, storeTransactionListener);
    }

    public void addExtraItem(StoreItem storeItem, String token) {
        extraItems.put(storeItem, token);
    }

    public Map<StoreItem, String> getExtraItems() {
        return extraItems;
    }

    public void checkExtraItems() {
        Map<StoreItem, String> items = StoreManager.INSTANCE.getExtraItems();
        final List<StoreItem> consumed = new ArrayList<StoreItem>();
        for (final StoreItem item : items.keySet()) {
            StoreManager.INSTANCE.consumeItem(item, items.get(item),
                    new StoreTransactionListener() {
                        @Override
                        public void transactionFinished(int responseCode, String token) {
                            if (responseCode == 0) {
                                checkItem(item);
                                consumed.add(item);
                            }
                        }
                    });
        }
        for (StoreItem item : consumed) {
            items.remove(item);
        }
    }

    public void checkItem(StoreItem item) {
        GameStatus gameStatus = DataManager.INSTANCE.getGameStatus();
        switch (item) {
            case THREE_LIVES:
                gameStatus.increaseLive();
                gameStatus.increaseLive();
                gameStatus.increaseLive();
                break;
            case PIZZA:
                gameStatus.enableMovementBoost();
                ScreenManager.INSTANCE.getCurrentScreen().getMediaManager().playSound(MediaManager.SOUND_NYAM_BOOST);
                break;
            case BURGER:
                gameStatus.enableFireLapseBoost();
                ScreenManager.INSTANCE.getCurrentScreen().getMediaManager().playSound(MediaManager.SOUND_NYAM_BOOST);
                break;
            case HOT_DOG:
                gameStatus.enableFireVelocityBoost();
                ScreenManager.INSTANCE.getCurrentScreen().getMediaManager().playSound(MediaManager.SOUND_NYAM_BOOST);
                break;
            case MEAT:
                gameStatus.enableFireDistanceBoost();
                ScreenManager.INSTANCE.getCurrentScreen().getMediaManager().playSound(MediaManager.SOUND_NYAM_BOOST);
                break;
            case PRO_SHOOTER:
                gameStatus.enableFireLapseBoost();
                gameStatus.enableFireVelocityBoost();
                gameStatus.enableFireDistanceBoost();
                ScreenManager.INSTANCE.getCurrentScreen().getMediaManager().playSound(MediaManager.SOUND_NYAM_BOOST);
                break;
        }
        DataManager.INSTANCE.saveGameStatus(false);
    }
}
