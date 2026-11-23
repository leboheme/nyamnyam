package com.aramirezochoa.nyamnyam.screen.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** Temporary square icon shared by the HUD and world pickups, drawn with filled triangles. */
public final class ToothShape {
    private static final Color EMPTY = Color.valueOf("17151f");
    private static final Color CARIES = Color.valueOf("b5ba83");
    private ToothShape() { }

    public static void draw(ShapeRenderer shapes, float x, float y, float size, int halves, boolean caries, float angle) {
        float centerX = x + size / 2, centerY = y + size / 2;
        float border = size / 10;
        shapes.setColor(Color.WHITE);
        rect(shapes, centerX, centerY, -size / 2, -size / 2, size, size, angle);
        float inner = size - border * 2;
        shapes.setColor(EMPTY);
        rect(shapes, centerX, centerY, -inner / 2, -inner / 2, inner, inner, angle);
        if (halves > 0) {
            shapes.setColor(caries ? CARIES : Color.WHITE);
            rect(shapes, centerX, centerY, -inner / 2, -inner / 2, inner * Math.min(2, halves) / 2, inner, angle);
        }
    }

    private static void rect(ShapeRenderer shapes, float cx, float cy, float x, float y, float w, float h, float angle) {
        float c = MathUtils.cosDeg(angle), s = MathUtils.sinDeg(angle);
        float ax = cx + x * c - y * s, ay = cy + x * s + y * c;
        float bx = cx + (x + w) * c - y * s, by = cy + (x + w) * s + y * c;
        float dx = cx + x * c - (y + h) * s, dy = cy + x * s + (y + h) * c;
        float ex = cx + (x + w) * c - (y + h) * s, ey = cy + (x + w) * s + (y + h) * c;
        shapes.triangle(ax, ay, bx, by, ex, ey);
        shapes.triangle(ax, ay, ex, ey, dx, dy);
    }
}
