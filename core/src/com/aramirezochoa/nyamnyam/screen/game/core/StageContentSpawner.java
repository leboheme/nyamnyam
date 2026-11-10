package com.aramirezochoa.nyamnyam.screen.game.core;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;
import com.badlogic.gdx.utils.Array;

/** Handles timed food and guided-air spawns for the current stage. */
public class StageContentSpawner {

    private final GameWrapper gameWrapper;
    private final Array<Constant.Coordinates> leftGuide = new Array<Constant.Coordinates>(true, 6);
    private final Array<Constant.Coordinates> rightGuide = new Array<Constant.Coordinates>(true, 6);
    private TmxStage stage;
    private float mealTimer;
    private float airTimer;
    private boolean mealsFinished;
    private boolean guidedAir;
    private boolean specialAir;
    private int mealSpawnIndex;
    private int automaticAirMode;

    public StageContentSpawner(GameWrapper gameWrapper) {
        this.gameWrapper = gameWrapper;
    }

    public void initialize(TmxStage stage, MainAvatar mainAvatar) {
        this.stage = stage;
        mealTimer = Constant.MEAL_SPAWN_DELAY;
        mealsFinished = false;
        mealSpawnIndex = 0;
        specialAir = true;
        leftGuide.clear();
        rightGuide.clear();

        TmxStage.AirSettings airSettings = stage.getAirSettings();
        guidedAir = airSettings.isGuided() || (airSettings.hasExtraAir() && !mainAvatar.isAirSkilled());
        if (guidedAir) {
            leftGuide.addAll(airSettings.getLeftGuide());
            rightGuide.addAll(airSettings.getRightGuide());
            airTimer = Constant.AIR_SPAWN_RATE;
        }
        automaticAirMode = airSettings.getAutomaticMode();
    }

    public void update(float deltaTime) {
        updateMeals(deltaTime);
        updateAir(deltaTime);
    }

    private void updateMeals(float deltaTime) {
        if (!mealsFinished && mealTimer < 0) {
            if (mealSpawnIndex < stage.getMealSpawns().size) {
                TmxStage.MealSpawn spawn = stage.getMealSpawns().get(mealSpawnIndex);
                MealType mealType = spawn.getType() == null ? MealType.randomFruit() : spawn.getType();
                if (mealType.isBoost()) {
                    gameWrapper.addMeal(EntityFactory.boost(spawn.getX(), spawn.getY(), mealType, gameWrapper, true));
                } else {
                    gameWrapper.addMeal(EntityFactory.fruit(spawn.getX(), spawn.getY(), mealType, gameWrapper));
                }
                mealSpawnIndex++;
                mealTimer = Constant.MEAL_SPAWN_DELAY;
                if (mealSpawnIndex == stage.getMealSpawns().size) {
                    mealsFinished = true;
                }
            }
        } else {
            mealTimer -= deltaTime;
        }
    }

    private void updateAir(float deltaTime) {
        if (!guidedAir) {
            return;
        }
        if (airTimer < 0) {
            MealType mealType = specialAir ? stage.getAirSettings().getSpecialAir() : MealType.AIR;
            spawnGuidedAir(mealType, RaiseType.LEFT_GUIDED, leftGuide, DirectionType.LEFT);
            spawnGuidedAir(mealType, RaiseType.RIGHT_GUIDED, rightGuide, DirectionType.RIGHT);
            airTimer = Constant.AIR_SPAWN_RATE;
            specialAir = !specialAir;
        }
        airTimer -= deltaTime;
    }

    private void spawnGuidedAir(MealType type, RaiseType raiseType, Array<Constant.Coordinates> guide,
                               DirectionType direction) {
        if (guide.size == 0) {
            return;
        }
        Constant.Coordinates point = guide.first();
        float x = point.x - type.getDimension().x / 2;
        float y = point.y - type.getDimension().y / 2;
        switch (type) {
            case AIR:
                gameWrapper.addMeal(EntityFactory.air(type, raiseType, x, y, direction, gameWrapper));
                break;
            case AIR_TORNADO:
                gameWrapper.addMeal(EntityFactory.tornado(type, raiseType, x, y, direction, gameWrapper));
                break;
            case AIR_SPIKES:
                gameWrapper.addMeal(EntityFactory.spikes(type, raiseType, x, y, direction, gameWrapper));
                break;
        }
    }

    public Array<Constant.Coordinates> getLeftGuide() {
        return leftGuide;
    }

    public Array<Constant.Coordinates> getRightGuide() {
        return rightGuide;
    }

    public int getAutomaticAirMode() {
        return automaticAirMode;
    }
}
