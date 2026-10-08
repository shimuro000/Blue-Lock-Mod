package com.bluelockmod.game;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Owns match-scoped opponent memory. Nothing here is client-authoritative. */
public final class ScoutingManager {
    private static final Map<UUID, ScoutingProfile> ACTIVE = new ConcurrentHashMap<>();
    private ScoutingManager() {}

    public static void start(UUID player) { ACTIVE.put(player, new ScoutingProfile()); }
    public static ScoutingProfile get(UUID player) { return ACTIVE.get(player); }
    public static void observe(ServerPlayer player, MatchPerformance.Action action, boolean successful) {
        ScoutingProfile profile = ACTIVE.get(player.getUUID());
        if (profile != null) profile.observe(player, action, successful);
    }
    public static void tick() { ACTIVE.values().forEach(ScoutingProfile::tick); }
    public static ScoutingProfile remove(UUID player) { return ACTIVE.remove(player); }
    public static void clear(UUID player) { ACTIVE.remove(player); }
}
