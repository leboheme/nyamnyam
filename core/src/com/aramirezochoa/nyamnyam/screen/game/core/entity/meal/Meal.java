package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/**
 * Created by boheme on 15/01/15.
 */
public abstract class Meal extends Entity {

    public Rectangle leftBox = new Rectangle();
    public Rectangle rightBox = new Rectangle();
    public Rectangle topBox = new Rectangle();
    public Rectangle bottomBox = new Rectangle();

    @Override
    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        super.init(type, x, y, lookingAt, gameWrapper);
        acceleration.y = -Constant.GRAVITY.y;
    }

    @Override
    protected void checkCollisions() {
        // nothing
    }

    @Override
    protected void initBoxes() {
        super.initBoxes();
        bottomBox.width = topBox.width = box.width / 2;
        bottomBox.height = topBox.height = box.height / 2;
        leftBox.width = rightBox.width = box.width / 4;
        leftBox.height = rightBox.height = box.height;
    }

    @Override
    protected void updateBoxes() {
        super.updateBoxes();
        leftBox.x = box.x;
        leftBox.y = box.y;
        bottomBox.x = leftBox.x + leftBox.width;
        bottomBox.y = box.y;
        topBox.x = leftBox.x + leftBox.width;
        topBox.y = box.y + box.height / 2;
        rightBox.x = box.x + box.width - rightBox.width;
        rightBox.y = box.y;
    }

    @Override
    protected void drawBoxes() {
        GameWrapper.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        GameWrapper.debugRenderer.setColor(Color.RED);
        GameWrapper.debugRenderer.rect(topBox.x, topBox.y, topBox.width, topBox.height);
        GameWrapper.debugRenderer.rect(bottomBox.x, bottomBox.y, bottomBox.width, bottomBox.height);
        GameWrapper.debugRenderer.rect(leftBox.x, leftBox.y, leftBox.width, leftBox.height);
        GameWrapper.debugRenderer.rect(rightBox.x, rightBox.y, rightBox.width, rightBox.height);
        GameWrapper.debugRenderer.end();
    }

    public void dampVelocity() {
        // nothing
    }

    @Override
    protected boolean isLoopAnimation() {
        return true;
    }

    @Override
    public MealType getType() {
        return (MealType) type;
    }

    @Override
    public void remove() {
        super.remove();
        parent.food.removeValue(this, true);
    }

}
