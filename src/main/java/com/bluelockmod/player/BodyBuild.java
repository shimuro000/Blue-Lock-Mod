package com.bluelockmod.player;

import java.util.Locale;

public enum BodyBuild {
    LEAN("Lean", 0.97F, 0.97F),
    BALANCED("Balanced", 1.00F, 1.00F),
    ATHLETIC("Athletic", 1.02F, 1.04F),
    POWERFUL("Powerful", 1.03F, 1.07F);

    private final String display;
    private final float heightScale;
    private final float widthScale;
    BodyBuild(String display, float heightScale, float widthScale) {
        this.display = display; this.heightScale = heightScale; this.widthScale = widthScale;
    }
    public String display() { return display; }
    public float heightScale() { return heightScale; }
    public float widthScale() { return widthScale; }
    public static BodyBuild parse(String value) {
        try { return valueOf(value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException | NullPointerException e) { return BALANCED; }
    }
}
