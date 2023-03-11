package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.pet;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarStateFactory;

/**
 * Created by leboheme on 2/02/15.
 */
public class PetGlutyAvatar extends PetAvatar {

    public void init(float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        super.init(PetType.GLUTY, x, y, lookingAt, gameWrapper);
    }

    @Override
    protected EntityState getInitialState() {
        return AvatarStateFactory.loop(AvatarStateFactory.sequence(AvatarStateFactory.bounce(this, 5), AvatarStateFactory.think(this, 2)));
    }

    @Override
    public void dampVelocity() {
        // do nothing
    }

    @Override
    protected void checkCollisions() {
        for (int i = 0; i < getParent().enemies.size; i++) {
            if (box.overlaps(getParent().enemies.get(i).box)) {
                getParent().enemies.get(i).hit(false);
            }
        }
    }

    @Override
    protected boolean isLoopAnimation() {
        return true;
    }

    @Override
    public void remove() {
        super.remove();
        parent.pets.removeValue(this, true);
    }

    @Override
    public float getVelocity() {
        return Constant.GLUTY_VELOCITY;
    }

    @Override
    public void notifyWall() {
        super.notifyWall();
        lookingAt = lookingAt.flip();
    }
}
