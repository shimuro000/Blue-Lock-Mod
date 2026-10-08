package com.bluelockmod.player;

import java.util.Locale;

public enum HairStyle {
    SHORT("Short"),
    SPIKY("Spiky"),
    LONG("Long"),
    MOHAWK("Mohawk"),
    UNDERCUT("Undercut"),
    WAVE("Wave"),
    AFRO("Afro");

    private final String display;
    HairStyle(String display) { this.display = display; }
    public String display() { return display; }
    public static HairStyle parse(String value) {
        try { return valueOf(value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException | NullPointerException e) { return SHORT; }
    }
}
