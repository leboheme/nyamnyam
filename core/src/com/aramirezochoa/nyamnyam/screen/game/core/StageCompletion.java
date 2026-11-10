package com.aramirezochoa.nyamnyam.screen.game.core;

import com.badlogic.gdx.utils.SnapshotArray;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.GameState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;

/** Tracks whether the current stage is ready to finish. */
public class StageCompletion {

    private final GameWrapper gameWrapper;
    private float winCounter;
    private boolean extraMeal;
    private boolean noMoreEnemies;

    public StageCompletion(GameWrapper gameWrapper) {
        this.gameWrapper = gameWrapper;
    }

    public void reset() {
        winCounter = Constant.GAME_WIN_FREE_TIME;
        extraMeal = false;
        noMoreEnemies = false;
    }

    public void resetNoMoreEnemies() {
        noMoreEnemies = false;
    }

    public boolean hasNoMoreEnemies() {
        return noMoreEnemies;
    }

    public void update(float deltaTime, GameStatus gameStatus) {
        EntityWorld entityWorld = gameWrapper.getEntityWorld();
        if (!entityWorld.hasEnemies()) {
            boolean enemyTrapped = false;
            for (int i = 0; i < entityWorld.getFood().size; i++) {
                if (entityWorld.getFood().get(i).getType().isCandy()) {
                    enemyTrapped = true;
                }
            }
            if (!enemyTrapped) {
                noMoreEnemies = true;
                if (winCounter < 0) {
                    gameStatus.setGameState(GameState.WIN);
                }
                winCounter -= deltaTime;
                if (!extraMeal) {
                    SnapshotArray<Meal> food = entityWorld.getFood();
                    for (int i = 0; i < food.size; i++) {
                        Meal meal = food.get(i);
                        if (meal.getType().isAir()) {
                            meal.setCurrentState(EntityStateFactory.die(meal));
                            gameWrapper.addMeal(EntityFactory.randomVerdure(
                                    meal.position.x + meal.dimension.x / 2, meal.position.y, gameWrapper));
                        }
                    }
                    extraMeal = true;
                }
                Enemy dark = entityWorld.getDark();
                if (dark != null && dark.isEnabled()) {
                    dark.setCurrentState(EnemyStateFactory.explode(dark, false));
                }
            }
        }
        if (gameStatus.getLives() <= 0) {
            gameStatus.setGameState(GameState.LOSE);
        }
    }
}
