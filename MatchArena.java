package com.bluelockmod.game;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Builds a compact football pitch around an explicitly chosen origin. */
public final class MatchArena {
    public static final int HALF_LENGTH = 18;
    public static final int HALF_WIDTH = 10;
    public static final int GOAL_WIDTH = 3;
    public static final int FLOOR_Y = 0;

    private MatchArena() {}

    public static void build(ServerLevel level, BlockPos origin) {
        int minX = origin.getX() - HALF_LENGTH - 2;
        int maxX = origin.getX() + HALF_LENGTH + 2;
        int minZ = origin.getZ() - HALF_WIDTH - 2;
        int maxZ = origin.getZ() + HALF_WIDTH + 2;
        int y = origin.getY();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                level.setBlock(new BlockPos(x, y, z), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            }
        }

        BlockState line = Blocks.WHITE_WOOL.defaultBlockState();
        for (int x = origin.getX() - HALF_LENGTH; x <= origin.getX() + HALF_LENGTH; x++) {
            level.setBlock(new BlockPos(x, y + 1, origin.getZ() - HALF_WIDTH), line, 3);
            level.setBlock(new BlockPos(x, y + 1, origin.getZ() + HALF_WIDTH), line, 3);
        }
        for (int z = origin.getZ() - HALF_WIDTH; z <= origin.getZ() + HALF_WIDTH; z++) {
            level.setBlock(new BlockPos(origin.getX() - HALF_LENGTH, y + 1, z), line, 3);
            level.setBlock(new BlockPos(origin.getX() + HALF_LENGTH, y + 1, z), line, 3);
        }
        for (int x = origin.getX() - HALF_LENGTH; x <= origin.getX() + HALF_LENGTH; x++) {
            level.setBlock(new BlockPos(x, y + 1, origin.getZ()), line, 3);
        }

        int circle = 3;
        for (int x = -circle; x <= circle; x++) {
            for (int z = -circle; z <= circle; z++) {
                if (Math.abs(x * x + z * z - circle * circle) <= 2) {
                    level.setBlock(new BlockPos(origin.getX() + x, y + 1, origin.getZ() + z), line, 3);
                }
            }
        }

        buildGoal(level, origin, false);
        buildGoal(level, origin, true);
    }

    private static void buildGoal(ServerLevel level, BlockPos origin, boolean positiveX) {
        int x = origin.getX() + (positiveX ? HALF_LENGTH + 1 : -HALF_LENGTH - 1);
        int back = x + (positiveX ? 2 : -2);
        int y = origin.getY() + 1;
        int z0 = origin.getZ() - GOAL_WIDTH;
        int z1 = origin.getZ() + GOAL_WIDTH;
        BlockState frame = Blocks.IRON_BARS.defaultBlockState();
        BlockState net = Blocks.WHITE_WOOL.defaultBlockState();
        for (int z = z0; z <= z1; z++) {
            level.setBlock(new BlockPos(x, y, z), frame, 3);
            level.setBlock(new BlockPos(back, y, z), net, 3);
        }
        for (int yy = 0; yy <= 2; yy++) {
            level.setBlock(new BlockPos(x, y + yy, z0), frame, 3);
            level.setBlock(new BlockPos(x, y + yy, z1), frame, 3);
        }
        level.setBlock(new BlockPos(x, y + 2, z0), frame, 3);
        level.setBlock(new BlockPos(x, y + 2, z1), frame, 3);
    }
}
