package com.bluelockmod.animation;

public enum FootballAnimation {
    CONTROL(8, 1.0F),
    PASS(12, 1.0F),
    SHOOT(16, 1.0F),
    DRIBBLE(14, 1.0F),
    SKILL(16, 1.0F),
    FLOW(28, 1.0F),
    AWAKENING(36, 1.0F);

    private final int defaultDuration;
    private final float defaultIntensity;

    FootballAnimation(int defaultDuration, float defaultIntensity) {
        this.defaultDuration = defaultDuration;
        this.defaultIntensity = defaultIntensity;
    }

    public int defaultDuration() { return defaultDuration; }
    public float defaultIntensity() { return defaultIntensity; }
}
