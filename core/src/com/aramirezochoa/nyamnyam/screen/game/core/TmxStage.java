package com.aramirezochoa.nyamnyam.screen.game.core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss.Boss;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;

/** Legacy TMX-backed stage adapter kept while rooms migrate to runtime data. */
public class TmxStage {

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
    private final Array<EnemySpawn> enemySpawns = new Array<EnemySpawn>();
    private final Array<MealSpawn> mealSpawns = new Array<MealSpawn>();
    private final AirSettings airSettings;
    private final boolean bossLevel;

    public TmxStage(TiledMap map) {
        this.map = map;
        renderer = new OrthogonalTiledMapRenderer(map, 1);
        bossLevel = Boolean.parseBoolean((String) map.getProperties().get("bossLevel"));
        loadEnemySpawns();
        loadMealSpawns();
        airSettings = loadAirSettings();
    }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public float getWorldWidth() {
        TiledMapTileLayer ground = getGroundLayer();
        return ground.getWidth() * ground.getTileWidth();
    }

    public float getWorldHeight() {
        TiledMapTileLayer ground = getGroundLayer();
        return ground.getHeight() * ground.getTileHeight();
    }

    public boolean isBossLevel() {
        return bossLevel;
    }

    public Array<EnemySpawn> getEnemySpawns() {
        return enemySpawns;
    }

    public Array<MealSpawn> getMealSpawns() {
        return mealSpawns;
    }

    public AirSettings getAirSettings() {
        return airSettings;
    }

    public TiledMapTileLayer getGroundLayer() {
        return (TiledMapTileLayer) map.getLayers().get("ground");
    }

    /** Temporary bridge for legacy collision code that still expects TiledMap. */
    public TiledMap getMap() {
        return map;
    }

    public void dispose() {
        renderer.dispose();
        map.dispose();
    }

    private void loadEnemySpawns() {
        for (MapObject object : map.getLayers().get("enemies").getObjects()) {
            try {
                EnemyType type = EnemyType.valueOf(object.getName());
                DirectionType facing = Boolean.parseBoolean((String) object.getProperties().get("type"))
                        ? DirectionType.LEFT : DirectionType.RIGHT;
                MealType bossBoost = null;
                if (bossLevel && Boss.class.isAssignableFrom(type.getEnemyClass())) {
                    bossBoost = MealType.valueOf((String) map.getProperties().get("bossBoost"));
                }
                enemySpawns.add(new EnemySpawn(type, facing, objectX(object), objectY(object), bossBoost));
            } catch (Exception exception) {
                com.badlogic.gdx.Gdx.app.error("Game", "Error parsing enemy value", exception);
            }
        }
    }

    private void loadMealSpawns() {
        for (MapObject object : map.getLayers().get("meals").getObjects()) {
            mealSpawns.add(new MealSpawn(findMealType(object.getName()), objectX(object), objectY(object)));
        }
    }

    private AirSettings loadAirSettings() {
        boolean guided = Boolean.parseBoolean((String) map.getProperties().get("guidedAir"));
        boolean extra = Boolean.parseBoolean((String) map.getProperties().get("extraAir"));
        int automaticMode;
        try {
            automaticMode = Integer.valueOf((String) map.getProperties().get("automaticAirMode"));
        } catch (NumberFormatException exception) {
            automaticMode = 0;
        }
        MealType specialAir = MealType.parseAir((String) map.getProperties().get("specialAir"));
        return new AirSettings(guided, extra, automaticMode, specialAir,
                loadGuide("left"), loadGuide("right"));
    }

    private Array<Constant.Coordinates> loadGuide(String name) {
        Array<Constant.Coordinates> guide = new Array<Constant.Coordinates>(true, 6);
        if (map.getLayers().get("path") == null) {
            return guide;
        }
        PolylineMapObject object = (PolylineMapObject) map.getLayers().get("path").getObjects().get(name);
        if (object != null) {
            float[] vertices = object.getPolyline().getVertices();
            float originX = object.getPolyline().getX();
            float originY = object.getPolyline().getY();
            for (int i = 0; i < vertices.length; i += 2) {
                guide.add(new Constant.Coordinates(originX + vertices[i], originY + vertices[i + 1]));
            }
        }
        return guide;
    }

    private static MealType findMealType(String name) {
        for (MealType type : MealType.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }

    private static float objectX(MapObject object) {
        if (object instanceof RectangleMapObject) {
            return ((RectangleMapObject) object).getRectangle().x;
        }
        if (object instanceof TiledMapTileMapObject) {
            return ((TiledMapTileMapObject) object).getX();
        }
        throw new IllegalArgumentException("Unsupported stage object: " + object.getClass().getName());
    }

    private static float objectY(MapObject object) {
        if (object instanceof RectangleMapObject) {
            return ((RectangleMapObject) object).getRectangle().y;
        }
        if (object instanceof TiledMapTileMapObject) {
            return ((TiledMapTileMapObject) object).getY();
        }
        throw new IllegalArgumentException("Unsupported stage object: " + object.getClass().getName());
    }

    public static class EnemySpawn {
        private final EnemyType type;
        private final DirectionType facing;
        private final float x;
        private final float y;
        private final MealType bossBoost;

        EnemySpawn(EnemyType type, DirectionType facing, float x, float y, MealType bossBoost) {
            this.type = type;
            this.facing = facing;
            this.x = x;
            this.y = y;
            this.bossBoost = bossBoost;
        }

        public EnemyType getType() { return type; }
        public DirectionType getFacing() { return facing; }
        public float getX() { return x; }
        public float getY() { return y; }
        public MealType getBossBoost() { return bossBoost; }
    }

    public static class MealSpawn {
        private final MealType type;
        private final float x;
        private final float y;

        MealSpawn(MealType type, float x, float y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }

        public MealType getType() { return type; }
        public float getX() { return x; }
        public float getY() { return y; }
    }

    public static class AirSettings {
        private final boolean guided;
        private final boolean extra;
        private final int automaticMode;
        private final MealType specialAir;
        private final Array<Constant.Coordinates> leftGuide;
        private final Array<Constant.Coordinates> rightGuide;

        AirSettings(boolean guided, boolean extra, int automaticMode, MealType specialAir,
                    Array<Constant.Coordinates> leftGuide, Array<Constant.Coordinates> rightGuide) {
            this.guided = guided;
            this.extra = extra;
            this.automaticMode = automaticMode;
            this.specialAir = specialAir;
            this.leftGuide = leftGuide;
            this.rightGuide = rightGuide;
        }

        public boolean isGuided() { return guided; }
        public boolean hasExtraAir() { return extra; }
        public int getAutomaticMode() { return automaticMode; }
        public MealType getSpecialAir() { return specialAir; }
        public Array<Constant.Coordinates> getLeftGuide() { return leftGuide; }
        public Array<Constant.Coordinates> getRightGuide() { return rightGuide; }
    }
}
