package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.candy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.candy.Candy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealState;

/**
 * Created by boheme on 15/01/15.
 */
public class LiberateCandyState extends MealState {


    protected float timer = Constant.EXPLODE_TIME;
    public static final String CODE = "EXPLODE";

    private Entity entity;

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(meal, deltaTime, false);
        if (timer < 0) {
            Enemy enemy = EntityFactory.enemy(((Candy) meal).enemyTrappedType, meal.position.x, meal.position.y, DirectionType.random(), meal.getParent());
            enemy.anger(true);
            meal.getParent().addEnemy(enemy);
            meal.die();
            return true;
        }
        timer -= deltaTime;
        return false;
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public void reset() {
        timer = Constant.EXPLODE_TIME;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

}
