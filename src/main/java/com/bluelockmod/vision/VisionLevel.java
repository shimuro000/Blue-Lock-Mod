package com.bluelockmod.vision;

public enum VisionLevel {
    BASIC(1, 18.0D),
    INTERMEDIATE(2, 24.0D),
    ADVANCED(3, 30.0D),
    ELITE(4, 36.0D);

    private final int tier;
    private final double radius;
    VisionLevel(int tier, double radius) { this.tier = tier; this.radius = radius; }
    public int tier() { return tier; }
    public double radius() { return radius; }
    public static VisionLevel fromStats(int vision, int spatial) {
        int score = (vision + spatial) / 2;
        if (score >= 80) return ELITE;
        if (score >= 60) return ADVANCED;
        if (score >= 40) return INTERMEDIATE;
        return BASIC;
    }
}
