package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by boheme on 28/01/15.
 */
public class HorizontalProjectileState extends ProjectileState {

    @Override
    public void init() {
        super.init();
        getProjectile().velocity.x = DirectionType.RIGHT.equals(getProjectile().getLookingAt()) ? getProjectile().getVelocity() : -getProjectile().getVelocity();
        getProjectile().acceleration.y = -Constant.GRAVITY.y;
    }

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateProjectile(getProjectile(), deltaTime, getProjectile().getType().isTileCollision());
        TiledMapTileLayer ground = (TiledMapTileLayer) getProjectile().getParent().getMap().getLayers().get("ground");
        float mapWidth = ground.getWidth() * ground.getTileWidth();
        if (getProjectile().position.y < -getProjectile().dimension.y
                || getProjectile().position.x < Constant.TILE_SIZE
                || getProjectile().position.x + getProjectile().dimension.x > mapWidth - Constant.TILE_SIZE
                || getProjectile().velocity.x == 0) {
            return true;
        }
        getProjectile().velocity.x = DirectionType.RIGHT.equals(getProjectile().getLookingAt()) ? getProjectile().getVelocity() : -getProjectile().getVelocity();

        return false;
    }

    @Override
    public String getCode() {
        return getProjectile().getType().name();
    }

    @Override
    public void reset() {
        getProjectile().acceleration.y = 0;
        getProjectile().velocity.x = 0;
    }
}
