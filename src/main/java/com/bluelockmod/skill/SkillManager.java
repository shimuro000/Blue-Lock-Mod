package com.bluelockmod.skill;

import com.bluelockmod.football.FootballEntity;
import com.bluelockmod.ego.EgoFlowManager;
import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.player.PlayerStats.Stat;
import com.bluelockmod.animation.AnimationNetwork;
import com.bluelockmod.animation.FootballAnimation;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.system.ServerTickHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/** Server-authoritative skill validation, activation, cooldowns and runtime effects. */
public final class SkillManager {
    private static final SkillRegistry REGISTRY = new SkillRegistry();
    private static final Map<UUID, Map<String, Long>> COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Integer> SPATIAL_READ_TICKS = new HashMap<>();

    private SkillManager() {}
    public static SkillRegistry registry() { return REGISTRY; }

    public static boolean unlock(ServerPlayer player, String id) {
        PlayerProfile profile = ProfileManager.get(player);
        SkillDefinition skill = REGISTRY.get(id);
        if (skill == null || profile.skillState().isUnlocked(id)) return false;
        if (profile.level() < skill.unlockLevel()) return false;
        for (String prerequisite : skill.prerequisites()) if (!profile.skillState().isUnlocked(prerequisite)) return false;
        if (profile.skillPoints() < skill.skillPointCost()) return false;
        profile.spendSkillPoints(skill.skillPointCost());
        profile.skillState().unlock(id);
        player.sendSystemMessage(Component.literal("Skill unlocked: " + skill.name()));
        return true;
    }

    public static boolean equip(ServerPlayer player, int slot, String id) {
        PlayerProfile profile = ProfileManager.get(player);
        SkillDefinition skill = REGISTRY.get(id);
        if (skill == null || !profile.skillState().isUnlocked(id)) return false;
        if (slot < 0 || slot >= 4) return false;
        profile.skillState().loadout().equip(slot, id);
        player.sendSystemMessage(Component.literal("Equipped " + skill.name() + " in Skill " + (slot + 1)));
        return true;
    }

    public static boolean activate(ServerPlayer player, int slot) {
        PlayerProfile profile = ProfileManager.get(player);
        String id = profile.skillState().loadout().get(slot);
        if (id == null) return false;
        SkillDefinition skill = REGISTRY.get(id);
        if (skill == null || !profile.skillState().isUnlocked(id)) return false;
        long now = ServerTickHandler.instance().tick();
        if (cooldown(player.getUUID(), id) > now) return false;
        if (profile.stamina() < skill.staminaCost() || profile.ego() < skill.egoCost()) return false;

        boolean success = switch (id) {
            case "direct_shot" -> directShot(player, profile);
            case "burst_dribble" -> burstDribble(player, profile);
            case "spatial_read" -> spatialRead(player, profile);
            case "killer_pass" -> killerPass(player, profile);
            case "body_feint" -> bodyFeint(player, profile);
            case "interception_burst" -> interceptionBurst(player, profile);
            default -> false;
        };
        if (!success) return false;

        profile.setStamina(profile.stamina() - skill.staminaCost());
        profile.addEgo(-skill.egoCost());
        int mastery = profile.skillState().addMastery(id, 4);
        setCooldown(player.getUUID(), id, now + Math.max(5, skill.cooldownTicks() - mastery / 10));
        ServerTickHandler.instance().trainingManager().recordAction(player, true);
        EgoFlowManager.onSuccessfulPlay(player, 1, 2);
        player.sendSystemMessage(Component.literal(skill.name() + " activated | Mastery " + mastery + "%"));
        AnimationNetwork.play(player, FootballAnimation.SKILL);
        return true;
    }

    private static boolean directShot(ServerPlayer player, PlayerProfile profile) {
        FootballEntity ball = nearestBall(player, 2.5D);
        if (ball == null || !owns(player, ball)) return false;
        Vec3 direction = player.getLookAngle().normalize();
        double accuracy = 0.86D + profile.stats().get(Stat.FINISHING) / 650.0D + EgoFlowManager.accuracyBonus(player);
        direction = new Vec3(direction.x * accuracy, direction.y + 0.05D, direction.z * accuracy).normalize();
        double power = 1.35D + profile.stats().get(Stat.SHOT_POWER) / 130.0D + profile.skillState().mastery("direct_shot") / 400.0D;
        ball.kick(direction.scale(power).add(0, 0.14D, 0));
        return true;
    }

    private static boolean burstDribble(ServerPlayer player, PlayerProfile profile) {
        FootballEntity ball = nearestBall(player, 2.5D);
        if (ball == null || !owns(player, ball)) return false;
        ball.control(player.getUUID());
        Vec3 forward = player.getLookAngle().normalize();
        player.setDeltaMovement(player.getDeltaMovement().add(forward.scale(0.22D + profile.stats().get(Stat.ACCELERATION) / 1200.0D)));
        return true;
    }

    private static boolean spatialRead(ServerPlayer player, PlayerProfile profile) {
        SPATIAL_READ_TICKS.put(player.getUUID(), 20 * 6);
        return true;
    }

    private static boolean killerPass(ServerPlayer player, PlayerProfile profile) {
        FootballEntity ball = nearestBall(player, 2.5D);
        if (ball == null || !owns(player, ball)) return false;
        ServerPlayer target = nearestOpenTeammate(player, 13.0D);
        Vec3 direction = target != null ? target.position().add(0, 0.3D, 0).subtract(player.position()).normalize() : player.getLookAngle().normalize();
        double power = 1.05D + profile.stats().get(Stat.PASSING) / 260.0D + profile.stats().get(Stat.LONG_PASSING) / 500.0D;
        ball.kick(direction.scale(power).add(0, 0.05D, 0));
        return true;
    }

    private static boolean bodyFeint(ServerPlayer player, PlayerProfile profile) {
        FootballEntity ball = nearestBall(player, 2.5D);
        if (ball == null || !owns(player, ball)) return false;
        ball.control(player.getUUID());
        Vec3 right = player.getLookAngle().cross(new Vec3(0, 1, 0)).normalize();
        player.setDeltaMovement(player.getDeltaMovement().add(right.scale(0.16D + profile.stats().get(Stat.AGILITY) / 1600.0D)));
        return true;
    }

    private static boolean interceptionBurst(ServerPlayer player, PlayerProfile profile) {
        FootballEntity ball = nearestBall(player, 4.0D);
        if (ball == null) return false;
        if (ball.isControlled() && ball.owner().filter(player.getUUID()::equals).isPresent()) return false;
        Vec3 toBall = ball.position().subtract(player.position()).normalize();
        player.setDeltaMovement(player.getDeltaMovement().add(toBall.scale(0.28D + profile.stats().get(Stat.REACTION) / 1400.0D)));
        if (player.distanceTo(ball) < 1.8D) ball.control(player.getUUID());
        return true;
    }

    private static ServerPlayer nearestOpenTeammate(ServerPlayer player, double range) {
        return null;
    }

    private static FootballEntity nearestBall(ServerPlayer player, double range) {
        return player.level().getEntitiesOfClass(FootballEntity.class, player.getBoundingBox().inflate(range)).stream()
                .min(Comparator.comparingDouble(player::distanceToSqr)).orElse(null);
    }

    private static boolean owns(ServerPlayer player, FootballEntity ball) {
        return player.distanceTo(ball) <= 2.5D && ball.owner().filter(player.getUUID()::equals).isPresent();
    }

    private static long cooldown(UUID player, String id) { return COOLDOWNS.getOrDefault(player, Map.of()).getOrDefault(id, 0L); }
    private static void setCooldown(UUID player, String id, long tick) { COOLDOWNS.computeIfAbsent(player, k -> new HashMap<>()).put(id, tick); }

    public static int cooldownRemaining(ServerPlayer player, String id) {
        long remaining = cooldown(player.getUUID(), id) - ServerTickHandler.instance().tick();
        return (int)Math.max(0, remaining);
    }

    public static boolean spatialReadActive(ServerPlayer player) { return SPATIAL_READ_TICKS.getOrDefault(player.getUUID(), 0) > 0; }
    public static double spatialReadBonus(ServerPlayer player) { return spatialReadActive(player) ? 0.10D : 0.0D; }

    public static void tick() {
        SPATIAL_READ_TICKS.entrySet().removeIf(e -> {
            int left = e.getValue() - 1;
            if (left <= 0) return true;
            e.setValue(left);
            return false;
        });
    }

    public static void clear(UUID player) {
        COOLDOWNS.remove(player);
        SPATIAL_READ_TICKS.remove(player);
    }
}
