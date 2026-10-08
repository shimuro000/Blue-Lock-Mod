package com.bluelockmod.ai;

import net.minecraft.world.phys.Vec3;

/** Small deterministic tactical helpers used by future match/formation logic. */
public final class AITactics {
    private AITactics() {}

    public static Vec3 pressTarget(Vec3 ball, Vec3 ownGoal, Vec3 opponentGoal, double aggression) {
        double a = Math.max(0.0D, Math.min(1.0D, aggression));
        return ball.lerp(opponentGoal, 0.18D + 0.22D * a).lerp(ownGoal, 0.08D * (1.0D - a));
    }

    public static boolean shouldShoot(double distanceToGoal, double finishing, double composure) {
        double quality = (finishing + composure) / 200.0D;
        return distanceToGoal < 22.0D || quality > 0.78D;
    }

    public static boolean shouldPass(double pressure, double vision, double passing) {
        return pressure > 0.72D && (vision + passing) / 200.0D > 0.58D;
    }
}
