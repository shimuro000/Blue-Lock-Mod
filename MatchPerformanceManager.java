package com.bluelockmod.game;

import net.minecraft.server.level.ServerPlayer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MatchPerformanceManager {
    private static final Map<UUID, MatchPerformance> ACTIVE = new ConcurrentHashMap<>();
    private MatchPerformanceManager() {}
    public static void start(UUID player) { ACTIVE.put(player, new MatchPerformance(player)); }
    public static MatchPerformance get(UUID player) { return ACTIVE.get(player); }
    public static void record(ServerPlayer player, MatchPerformance.Action action, boolean successful, boolean clutch) {
        MatchPerformance p = ACTIVE.get(player.getUUID()); if (p != null) p.record(action, successful, clutch);
    }
    public static void pressure(ServerPlayer player) { MatchPerformance p = ACTIVE.get(player.getUUID()); if (p != null) p.pressure(); }
    public static MatchPerformance remove(UUID player) { return ACTIVE.remove(player); }
    public static void clear(UUID player) { ACTIVE.remove(player); }
}
