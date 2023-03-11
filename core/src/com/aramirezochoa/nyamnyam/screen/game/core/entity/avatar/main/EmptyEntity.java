package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/**
 * Created by boheme on 15/01/15.
 */
public class EmptyEntity extends MainAvatar {

    @Override
    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {

    }

    @Override
    public void left() {

    }

    @Override
    public void right() {

    }

    @Override
    public void fire() {

    }

    @Override
    protected void createNormalFire() {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    protected boolean isLoopAnimation() {
        return false;
    }

    @Override
    public void render(Batch batch) {

    }

    @Override
    protected boolean canPerformAction() {
        return false;
    }

    @Override
    public boolean isAirSkilled() {
        return false;
    }
}
