package com.bluelockmod.client;

public final class ClientEgoFlowState {
    private static int ego, flow, stamina, flowTicks, awakeningTicks, flowCooldown, awakeningCooldown, streak;
    private ClientEgoFlowState() {}
    public static void update(int newEgo, int newFlow, int newStamina, int newFlowTicks, int newAwakeningTicks, int newFlowCooldown, int newAwakeningCooldown, int newStreak) {
        ego = newEgo; flow = newFlow; stamina = newStamina; flowTicks = newFlowTicks; awakeningTicks = newAwakeningTicks;
        flowCooldown = newFlowCooldown; awakeningCooldown = newAwakeningCooldown; streak = newStreak;
    }
    public static int ego() { return ego; }
    public static int flow() { return flow; }
    public static int stamina() { return stamina; }
    public static int flowTicks() { return flowTicks; }
    public static int awakeningTicks() { return awakeningTicks; }
    public static int flowCooldown() { return flowCooldown; }
    public static int awakeningCooldown() { return awakeningCooldown; }
    public static int streak() { return streak; }
}
