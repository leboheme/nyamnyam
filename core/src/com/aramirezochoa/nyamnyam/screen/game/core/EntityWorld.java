package com.aramirezochoa.nyamnyam.screen.game.core;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.bubble.Bubble;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.SnapshotArray;

/** Owns the entities currently active in the gameplay world. */
public class EntityWorld {

    private final SnapshotArray<Enemy> enemies = new SnapshotArray(true, 10, Enemy.class);
    private final SnapshotArray<Meal> food = new SnapshotArray(true, 4, Meal.class);
    private final SnapshotArray<Projectile> projectiles = new SnapshotArray(true, 4, Projectile.class);
    private final SnapshotArray<Avatar> pets = new SnapshotArray(true, 4, Avatar.class);
    private final SnapshotArray<Bubble> bubbles = new SnapshotArray(true, 4, Bubble.class);
    private MainAvatar mainAvatar;

    public SnapshotArray<Bubble> getBubbles() { return bubbles; }
    public void addBubble(Bubble bubble) { bubbles.add(bubble); }
    public void removeBubble(Bubble bubble) { bubbles.removeValue(bubble, true); }
    private Enemy dark;

    public MainAvatar getMainAvatar() {
        return mainAvatar;
    }

    public void setMainAvatar(MainAvatar mainAvatar) {
        this.mainAvatar = mainAvatar;
    }

    public Enemy getDark() {
        return dark;
    }

    public void setDark(Enemy dark) {
        this.dark = dark;
    }

    public SnapshotArray<Enemy> getEnemies() {
        return enemies;
    }

    public SnapshotArray<Meal> getFood() {
        return food;
    }

    public SnapshotArray<Projectile> getProjectiles() {
        return projectiles;
    }

    public SnapshotArray<Avatar> getPets() {
        return pets;
    }

    public void collectCollisionBodies(Array<CollisionBody> output) {
        output.clear();
        if (mainAvatar != null) output.add(mainAvatar);
        for (Enemy enemy : enemies) output.add(enemy);
        for (Meal meal : food) output.add(meal);
        for (Projectile projectile : projectiles) output.add(projectile);
        for (Avatar pet : pets) output.add(pet);
        for (Bubble bubble : bubbles) output.add(bubble);
        if (dark != null && !output.contains(dark, true)) output.add(dark);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void addMeal(Meal meal) {
        food.add(meal);
    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public void addPet(Avatar pet) {
        pets.add(pet);
    }

    public void removeEnemy(Enemy enemy) {
        enemies.removeValue(enemy, true);
    }

    public void removeMeal(Meal meal) {
        food.removeValue(meal, true);
    }

    public void removeProjectile(Projectile projectile) {
        projectiles.removeValue(projectile, true);
    }

    public void removePet(Avatar pet) {
        pets.removeValue(pet, true);
    }

    public void update(float deltaTime) {
        if (mainAvatar != null) {
            mainAvatar.update(deltaTime);
        }
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).update(deltaTime);
        }
        for (int i = 0; i < food.size; i++) {
            food.get(i).update(deltaTime);
        }
        for (int i = bubbles.size - 1; i >= 0; i--) {
            bubbles.get(i).update(deltaTime);
        }
        for (int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).update(deltaTime);
        }
        for (int i = 0; i < pets.size; i++) {
            pets.get(i).update(deltaTime);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < bubbles.size; i++) bubbles.get(i).render(batch);
        for (int i = 0; i < food.size; i++) {
            food.get(i).render(batch);
        }
        for (int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).render(batch);
        }
        for (int i = 0; i < pets.size; i++) {
            pets.get(i).render(batch);
        }
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).render(batch);
        }
        if (mainAvatar != null) {
            mainAvatar.render(batch);
        }
        if (dark != null) {
            dark.render(batch);
        }
    }

    public boolean hasEnemies() {
        return enemies.size > 0;
    }

    /** JSON combat is unresolved while any enemy remains free or owned by a bubble. */
    public boolean hasUnresolvedEnemies() {
        if (hasEnemies()) {
            return true;
        }
        for (int i = 0; i < bubbles.size; i++) {
            if (bubbles.get(i).hasCapturedEnemy()) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        for (int i = 0; i < bubbles.size; i++) bubbles.get(i).free();
        bubbles.clear();
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).free();
        }
        enemies.clear();
        for (int i = 0; i < food.size; i++) {
            food.get(i).free();
        }
        food.clear();
        for (int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).free();
        }
        projectiles.clear();
        for (int i = 0; i < pets.size; i++) {
            pets.get(i).free();
        }
        pets.clear();
        if (dark != null) {
            dark.free();
            dark = null;
        }
    }
}
