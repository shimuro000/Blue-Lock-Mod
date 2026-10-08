package com.bluelockmod.vision;

import java.util.List;

public record VisionSnapshot(VisionLevel level, boolean spatialRead, List<VisionTarget> targets) {}
