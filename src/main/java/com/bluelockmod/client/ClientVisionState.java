package com.bluelockmod.client;

import com.bluelockmod.vision.VisionLevel;
import com.bluelockmod.vision.VisionTarget;

import java.util.List;

public final class ClientVisionState {
    private static VisionLevel level = VisionLevel.BASIC;
    private static boolean spatialRead;
    private static List<VisionTarget> targets = List.of();
    private static long lastUpdate;
    private ClientVisionState() {}
    public static void set(VisionLevel newLevel, boolean newSpatialRead, List<VisionTarget> newTargets) { level = newLevel; spatialRead = newSpatialRead; targets = List.copyOf(newTargets); lastUpdate = System.currentTimeMillis(); }
    public static VisionLevel level() { return level; }
    public static boolean spatialRead() { return spatialRead; }
    public static List<VisionTarget> targets() { return targets; }
    public static boolean fresh() { return System.currentTimeMillis() - lastUpdate < 1500; }
    public static void clear() { targets = List.of(); spatialRead = false; level = VisionLevel.BASIC; }
}
