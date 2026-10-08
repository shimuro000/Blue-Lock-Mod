package com.bluelockmod.player;

import net.minecraft.server.level.ServerPlayer;

public final class ProgressionService {
    private ProgressionService() {}
    public static void awardMatch(ServerPlayer player, int goals, int assists) {
        player.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(profile -> {
            int before = profile.level();
            profile.recordMatch(goals, assists);
            if (profile.level() > before) player.sendSystemMessage(net.minecraft.network.chat.Component.literal("LEVEL UP! Level " + profile.level() + " — Development Point gained."));
        });
    }
    public static boolean spendPoint(ServerPlayer player, PlayerStats.Stat stat) {
        return player.getCapability(PlayerProfileProvider.CAPABILITY).map(profile -> {
            if (profile.developmentPoints() <= 0 || profile.stats().get(stat) >= 100) return false;
            profile.addDevelopmentPoint(-1); profile.stats().add(stat, 1); return true;
        }).orElse(false);
    }
}
