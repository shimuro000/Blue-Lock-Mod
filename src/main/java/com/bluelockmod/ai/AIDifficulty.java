package com.bluelockmod.ai;

public enum AIDifficulty {
    EASY(0.72D, 0.70D, 0.55D),
    NORMAL(0.88D, 0.85D, 0.75D),
    HARD(1.00D, 1.00D, 0.90D),
    ELITE(1.08D, 1.10D, 1.00D);

    private final double reaction;
    private final double positioning;
    private final double decision;

    AIDifficulty(double reaction, double positioning, double decision) {
        this.reaction = reaction;
        this.positioning = positioning;
        this.decision = decision;
    }

    public double reaction() { return reaction; }
    public double positioning() { return positioning; }
    public double decision() { return decision; }
}
