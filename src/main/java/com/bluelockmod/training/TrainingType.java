package com.bluelockmod.training;

import com.bluelockmod.player.PlayerStats.Stat;

public enum TrainingType {
    SPEED(Stat.TOP_SPEED, "Speed Sprint"),
    DRIBBLING(Stat.DRIBBLING, "Dribbling Course"),
    PASSING(Stat.PASSING, "Passing Reps"),
    SHOOTING(Stat.SHOOTING, "Shooting Reps"),
    DEFENSE(Stat.INTERCEPTION, "Defensive Reaction"),
    STAMINA(Stat.STAMINA, "Repeated Sprint");

    private final Stat primaryStat;
    private final String displayName;
    TrainingType(Stat primaryStat, String displayName) { this.primaryStat = primaryStat; this.displayName = displayName; }
    public Stat primaryStat() { return primaryStat; }
    public String displayName() { return displayName; }
    public static TrainingType parse(String raw) {
        try { return valueOf(raw.toUpperCase()); } catch (Exception ignored) { return null; }
    }
}
