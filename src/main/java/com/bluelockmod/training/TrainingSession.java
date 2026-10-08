package com.bluelockmod.training;

import java.util.UUID;

public final class TrainingSession {
    private final UUID playerId;
    private final TrainingType type;
    private final long startTick;
    private final long endTick;
    private double distance;
    private int actions;
    private int successfulActions;
    private int activeTicks;

    public TrainingSession(UUID playerId, TrainingType type, long startTick, int durationTicks) {
        this.playerId = playerId;
        this.type = type;
        this.startTick = startTick;
        this.endTick = startTick + durationTicks;
    }
    public UUID playerId() { return playerId; }
    public TrainingType type() { return type; }
    public long endTick() { return endTick; }
    public double distance() { return distance; }
    public int actions() { return actions; }
    public int successfulActions() { return successfulActions; }
    public int activeTicks() { return activeTicks; }
    public void addDistance(double value) { distance += Math.max(0, value); }
    public void addAction(boolean success) { actions++; if (success) successfulActions++; }
    public void addActiveTick() { activeTicks++; }
}
