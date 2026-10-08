package com.bluelockmod.player;

import java.util.Locale;

public enum Personality {
    COMPETITIVE("Competitive"),
    ANALYTICAL("Analytical"),
    INSTINCTIVE("Instinctive"),
    OPPORTUNISTIC("Opportunistic"),
    CREATIVE("Creative"),
    RELENTLESS("Relentless");

    private final String display;
    Personality(String display) { this.display = display; }
    public String display() { return display; }
    public static Personality parse(String value) {
        try { return valueOf(value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException | NullPointerException e) { return COMPETITIVE; }
    }
}
