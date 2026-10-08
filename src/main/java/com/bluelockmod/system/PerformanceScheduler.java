package com.bluelockmod.system;

import net.minecraft.server.level.ServerPlayer;

/** Phase 22: central, deterministic tick budgets for non-critical systems. */
public final class PerformanceScheduler {
    private PerformanceScheduler() {}

    public static boolean every(long tick, int interval) {
        return interval <= 1 || tick % interval == 0L;
    }

    public static int aiInterval(ServerPlayer captain, int playerCount) {
        // Keep active human matches responsive while reducing background AI work.
        return playerCount <= 6 ? 2 : 3;
    }

    public static int visionInterval(ServerPlayer player, boolean inMatch) {
        return inMatch ? 5 : 10;
    }

    public static int effectInterval(ServerPlayer player) {
        return 6;
    }
}
