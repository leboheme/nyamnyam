package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy;

import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/**
 * Created by leboheme on 3/03/15.
 */
public class FollowEnemyState extends EnemyState {

    public static final String CODE = "FLY";
    private float startX, startY, endX, endY, percent, partialTime, totalTime, stopTimer;

    @Override
    public void init() {
        super.init();
        startX = getEnemy().position.x;
        startY = getEnemy().position.y;
        endX = getEnemy().getParent().mainAvatar.position.x;
        endY = getEnemy().getParent().mainAvatar.position.y;
        partialTime = 0;
        totalTime = Vector2.len(endX - startX, endY - startY) / 150;
        percent = 0;
        stopTimer = 1;
    }

    @Override
    public boolean update(float deltaTime) {
        if (totalTime != 0) {
            percent = partialTime / totalTime;
        } else {
            percent = 1;
        }

        getEnemy().position.x = startX + (endX - startX) * percent;
        getEnemy().position.y = startY + (endY - startY) * percent;

        if (getEnemy().getParent().mainAvatar.position.x < getEnemy().position.x) {
            getEnemy().setLookingAt(DirectionType.LEFT);
        } else {
            getEnemy().setLookingAt(DirectionType.RIGHT);
        }
        if (percent >= 1) {
            if (stopTimer < 0) {
                return true;
            }
            stopTimer -= deltaTime;
            return false;
        } else {
            partialTime += deltaTime;
            return false;
        }
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public void reset() {

    }
}
