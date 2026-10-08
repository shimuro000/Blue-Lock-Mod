package com.bluelockmod.vision;

import java.util.UUID;

public record VisionTarget(UUID id, String name, double distance, double bearing, boolean teammate, boolean predicted, boolean threat, double projectedDistance, double projectedBearing) {}
