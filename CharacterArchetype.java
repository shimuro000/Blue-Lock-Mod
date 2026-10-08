package com.bluelockmod.player;

import java.util.Locale;

public enum CharacterArchetype {
    STRIKER(2, 0, 2, 1, 1, 0, 2),
    PLAYMAKER(0, 2, 1, 2, 2, 1, 0),
    DRIBBLER(2, 2, 0, 2, 2, 0, 0),
    SPEEDSTER(3, 1, 1, 1, 1, 0, 0),
    DEFENDER(0, 0, 2, 0, 1, 3, 2),
    GOALKEEPER(0, 0, 3, 0, 1, 3, 2);

    private final int shooting, passing, stamina, dribbling, vision, defending, physical;
    CharacterArchetype(int shooting, int passing, int stamina, int dribbling, int vision, int defending, int physical) {
        this.shooting = shooting; this.passing = passing; this.stamina = stamina; this.dribbling = dribbling;
        this.vision = vision; this.defending = defending; this.physical = physical;
    }
    public static CharacterArchetype parse(String value) {
        try { return valueOf(value.toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException e) { return STRIKER; }
    }
    public void apply(PlayerStats s) {
        s.add(PlayerStats.Stat.SHOOTING, shooting); s.add(PlayerStats.Stat.FINISHING, shooting);
        s.add(PlayerStats.Stat.PASSING, passing); s.add(PlayerStats.Stat.LONG_PASSING, passing / 2);
        s.add(PlayerStats.Stat.STAMINA, stamina); s.add(PlayerStats.Stat.DRIBBLING, dribbling);
        s.add(PlayerStats.Stat.BALL_CONTROL, dribbling); s.add(PlayerStats.Stat.VISION, vision);
        s.add(PlayerStats.Stat.TACKLING, defending); s.add(PlayerStats.Stat.INTERCEPTION, defending);
        s.add(PlayerStats.Stat.STRENGTH, physical); s.add(PlayerStats.Stat.ACCELERATION, physical / 2);
    }
}
