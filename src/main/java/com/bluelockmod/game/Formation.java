package com.bluelockmod.game;

import com.bluelockmod.ai.AIRole;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Formation data is deliberately compact: the quick match uses one player-controlled slot
 * plus four AI slots on the home side, while the away side uses five AI slots.
 */
public enum Formation {
    F_433("4-3-3", new double[][]{{0.08, 0.00}, {0.30, 0.00}, {0.52, 0.00}, {0.62, -0.48}, {0.86, 0.00}}),
    F_442("4-4-2", new double[][]{{0.08, 0.00}, {0.30, -0.22}, {0.52, 0.22}, {0.65, -0.34}, {0.86, 0.34}}),
    F_4231("4-2-3-1", new double[][]{{0.08, 0.00}, {0.30, -0.28}, {0.30, 0.28}, {0.58, -0.22}, {0.88, 0.00}}),
    F_352("3-5-2", new double[][]{{0.10, 0.00}, {0.36, -0.42}, {0.48, 0.00}, {0.36, 0.42}, {0.82, 0.00}}),
    F_532("5-3-2", new double[][]{{0.08, 0.00}, {0.28, -0.38}, {0.45, 0.00}, {0.28, 0.38}, {0.82, 0.00}}),
    CUSTOM("Custom", new double[][]{{0.08, 0.00}, {0.30, 0.00}, {0.52, 0.00}, {0.64, -0.38}, {0.86, 0.00}});

    private final String display;
    private final double[][] slots;

    Formation(String display, double[][] slots) { this.display = display; this.slots = slots; }
    public String display() { return display; }

    /** Returns a world position for the five tactical lanes used by the current match engine. */
    public Vec3 position(AIRole role, boolean home, BlockPos origin) {
        int index = switch (role) {
            case GOALKEEPER -> 0;
            case DEFENDER -> 1;
            case MIDFIELDER -> 2;
            case WINGER -> 3;
            case STRIKER -> 4;
        };
        double forward = 17.0D * slots[index][0];
        double lateral = 8.0D * slots[index][1];
        double x = home ? origin.getX() - 17.0D + forward : origin.getX() + 17.0D - forward;
        return new Vec3(x, origin.getY() + 1.0D, origin.getZ() + lateral);
    }
}
