package com.bluelockmod.ego;

import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.effects.FootballEffects;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.animation.AnimationNetwork;
import com.bluelockmod.animation.FootballAnimation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Server-authoritative Ego, Flow and Awakening runtime. */
public final class EgoFlowManager {
    public static final int FLOW_ACTIVATION_THRESHOLD = 60;
    public static final int FLOW_DURATION_TICKS = 240;
    public static final int FLOW_COOLDOWN_TICKS = 600;
    public static final int AWAKENING_DURATION_TICKS = 200;
    public static final int AWAKENING_COOLDOWN_TICKS = 1200;

    private static final Map<UUID, Integer> FLOW_TICKS = new HashMap<>();
    private static final Map<UUID, Integer> AWAKENING_TICKS = new HashMap<>();
    private static final Map<UUID, Long> FLOW_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Long> AWAKENING_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Integer> PERFORMANCE_STREAK = new HashMap<>();
    private static long tick;

    private EgoFlowManager() {}

    public static boolean activateFlow(ServerPlayer player) {
        PlayerProfile profile = ProfileManager.get(player);
        UUID id = player.getUUID();
        if (isFlowActive(player) || isAwakeningActive(player)) return false;
        if (flowCooldownRemaining(player) > 0 || awakeningCooldownRemaining(player) > 0) return false;
        if (profile.flow() < FLOW_ACTIVATION_THRESHOLD || profile.ego() < 20) return false;

        profile.addFlow(-FLOW_ACTIVATION_THRESHOLD);
        profile.addEgo(-20);
        profile.recordFlowActivation();
        FLOW_TICKS.put(id, FLOW_DURATION_TICKS);
        FLOW_COOLDOWNS.put(id, tick + FLOW_COOLDOWN_TICKS);
        player.sendSystemMessage(Component.translatable("bluelockmod.ego.flow_activated"));
        FootballEffects.flowActivated(player);
        AnimationNetwork.play(player, FootballAnimation.FLOW);

        if (profile.ego() >= 70 && PERFORMANCE_STREAK.getOrDefault(id, 0) >= 3 && profile.flowMastery() >= 20) awaken(player, profile);
        return true;
    }

    public static void onSuccessfulPlay(ServerPlayer player, int egoGain, int flowGain) {
        PlayerProfile profile = ProfileManager.get(player);
        UUID id = player.getUUID();
        profile.addEgo(Math.max(0, egoGain));
        profile.addFlow(Math.max(0, flowGain));
        if (isFlowActive(player)) profile.addFlowMastery(1);
        PERFORMANCE_STREAK.put(id, Math.min(20, PERFORMANCE_STREAK.getOrDefault(id, 0) + 1));

        if (isFlowActive(player) && !isAwakeningActive(player)
                && profile.ego() >= 70
                && PERFORMANCE_STREAK.getOrDefault(id, 0) >= 3
                && profile.flowMastery() >= 20
                && awakeningCooldownRemaining(player) <= 0) awaken(player, profile);
    }

    public static void onFailedPlay(ServerPlayer player) {
        UUID id = player.getUUID();
        PERFORMANCE_STREAK.put(id, 0);
        PlayerProfile profile = ProfileManager.get(player);
        if (!isFlowActive(player)) profile.addFlow(-1);
    }

    private static void awaken(ServerPlayer player, PlayerProfile profile) {
        UUID id = player.getUUID();
        if (isAwakeningActive(player) || awakeningCooldownRemaining(player) > 0) return;
        AWAKENING_TICKS.put(id, AWAKENING_DURATION_TICKS);
        AWAKENING_COOLDOWNS.put(id, tick + AWAKENING_COOLDOWN_TICKS);
        profile.recordAwakening();
        PERFORMANCE_STREAK.put(id, 0);
        FootballEffects.awakening(player);
        AnimationNetwork.play(player, FootballAnimation.AWAKENING);
        player.sendSystemMessage(Component.translatable("bluelockmod.ego.awakening"));
    }

    public static boolean isFlowActive(ServerPlayer player) { return FLOW_TICKS.getOrDefault(player.getUUID(), 0) > 0; }
    public static boolean isAwakeningActive(ServerPlayer player) { return AWAKENING_TICKS.getOrDefault(player.getUUID(), 0) > 0; }
    public static int flowTicksRemaining(ServerPlayer player) { return Math.max(0, FLOW_TICKS.getOrDefault(player.getUUID(), 0)); }
    public static int awakeningTicksRemaining(ServerPlayer player) { return Math.max(0, AWAKENING_TICKS.getOrDefault(player.getUUID(), 0)); }
    public static int flowCooldownRemaining(ServerPlayer player) { return (int) Math.max(0, FLOW_COOLDOWNS.getOrDefault(player.getUUID(), 0L) - tick); }
    public static int awakeningCooldownRemaining(ServerPlayer player) { return (int) Math.max(0, AWAKENING_COOLDOWNS.getOrDefault(player.getUUID(), 0L) - tick); }

    public static double speedMultiplier(ServerPlayer player) {
        if (isAwakeningActive(player)) return 1.16D;
        if (isFlowActive(player)) return 1.08D;
        return 1.0D;
    }

    public static double accuracyBonus(ServerPlayer player) {
        if (isAwakeningActive(player)) return 0.08D;
        if (isFlowActive(player)) return 0.04D;
        return 0.0D;
    }

    public static double staminaRegenBonus(ServerPlayer player) {
        if (isAwakeningActive(player)) return 0.35D;
        if (isFlowActive(player)) return 0.20D;
        return 0.0D;
    }

    public static int performanceStreak(ServerPlayer player) { return PERFORMANCE_STREAK.getOrDefault(player.getUUID(), 0); }

    public static void tick() {
        tick++;
        decrement(FLOW_TICKS);
        decrement(AWAKENING_TICKS);
    }

    private static void decrement(Map<UUID, Integer> states) {
        states.entrySet().removeIf(entry -> {
            int left = entry.getValue() - 1;
            if (left <= 0) return true;
            entry.setValue(left);
            return false;
        });
    }

    public static void clear(UUID player) {
        FLOW_TICKS.remove(player);
        AWAKENING_TICKS.remove(player);
        FLOW_COOLDOWNS.remove(player);
        AWAKENING_COOLDOWNS.remove(player);
        PERFORMANCE_STREAK.remove(player);
    }
}
