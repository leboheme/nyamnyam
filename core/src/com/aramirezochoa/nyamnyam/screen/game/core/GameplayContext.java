package com.aramirezochoa.nyamnyam.screen.game.core;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;
import com.aramirezochoa.nyamnyam.screen.game.run.BubbleAttractionService;
import com.aramirezochoa.nyamnyam.screen.game.run.RunCombatService;
import com.aramirezochoa.nyamnyam.screen.game.run.RunDropService;
import com.aramirezochoa.nyamnyam.screen.game.run.SalivationReserve;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

/** Runtime services used by entities, independent from the legacy GameWrapper. */
public interface GameplayContext {
    TextureAtlas getTextureAtlas();
    MediaManager getMediaManager();
    GameStatus getGameStatus();
    EntityWorld getEntityWorld();
    default RunDropService getDropService() { return null; }
    default RunCombatService getCombatService() { return null; }
    default SalivationReserve getSalivationReserve() { return null; }
    CollisionWorld getCollisionWorld();
    void addEnemy(Enemy enemy);
    void addMeal(Meal meal);
    void addProjectile(Projectile projectile);
    void addPet(Avatar avatar);
    void mainAvatarDead();
    void notifyMessage(String message, float x, float y);
    int getAutomaticAirMode();
    RaiseType getBubbleRaiseType();
    BubbleAttractionService getBubbleAttractionService();
    long nextEntityId();
    Array<Constant.Coordinates> getLeftGuide();
    Array<Constant.Coordinates> getRightGuide();
}
