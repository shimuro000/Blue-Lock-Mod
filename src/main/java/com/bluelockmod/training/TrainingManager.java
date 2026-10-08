package com.bluelockmod.training;

import com.bluelockmod.football.FootballEntity;
import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.player.ProfileManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TrainingManager {
    private static final int DURATION_TICKS = 20 * 30;
    private static final int COOLDOWN_TICKS = 20 * 45;
    private final Map<UUID, TrainingSession> sessions = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private long tick;

    public void tick(MinecraftServer server) {
        tick++;
        sessions.entrySet().removeIf(entry -> {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            TrainingSession session = entry.getValue();
            if (player == null) return true;
            update(player, session);
            if (tick >= session.endTick()) { finish(player, session); return true; }
            return false;
        });
        cooldowns.entrySet().removeIf(e -> e.getValue() <= tick);
    }

    public boolean start(ServerPlayer player, TrainingType type) {
        if (type == null || sessions.containsKey(player.getUUID()) || isOnCooldown(player)) return false;
        sessions.put(player.getUUID(), new TrainingSession(player.getUUID(), type, tick, DURATION_TICKS));
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Training started: " + type.displayName() + " (30 seconds)"));
        return true;
    }

    public boolean stop(ServerPlayer player) {
        TrainingSession session = sessions.remove(player.getUUID());
        if (session == null) return false;
        finish(player, session);
        return true;
    }

    public TrainingSession session(ServerPlayer player) { return sessions.get(player.getUUID()); }
    public boolean isOnCooldown(ServerPlayer player) { return cooldowns.getOrDefault(player.getUUID(), 0L) > tick; }

    public void recordAction(ServerPlayer player, boolean success) {
        TrainingSession session = sessions.get(player.getUUID());
        if (session != null) session.addAction(success);
    }

    private void update(ServerPlayer player, TrainingSession session) {
        if (player.isSprinting()) session.addActiveTick();
        Vec3 delta = player.getDeltaMovement();
        if (delta.horizontalDistanceSqr() > 0.0001) session.addDistance(Math.sqrt(delta.horizontalDistanceSqr()));
        if (session.type() == TrainingType.DRIBBLING) {
            FootballEntity ball = nearestBall(player);
            if (ball != null && ball.isControlled() && ball.owner().filter(player.getUUID()::equals).isPresent()) session.addAction(true);
        }
        if (session.type() == TrainingType.DEFENSE) {
            if (nearestBall(player) != null) session.addActiveTick();
        }
    }

    private FootballEntity nearestBall(ServerPlayer player) {
        return player.level().getEntitiesOfClass(FootballEntity.class, player.getBoundingBox().inflate(3.0D))
                .stream().min((a,b) -> Double.compare(player.distanceToSqr(a), player.distanceToSqr(b))).orElse(null);
    }

    private void finish(ServerPlayer player, TrainingSession session) {
        PlayerProfile profile = ProfileManager.get(player);
        int score = calculateScore(session);
        String grade = grade(score);
        int xp = 10 + score / 4;
        int development = grade.equals("S") ? 2 : (score >= 55 ? 1 : 0);
        profile.addXp(xp);
        profile.addDevelopmentPoint(development);
        profile.addEgo(score >= 70 ? 2 : 1);
        profile.setConfidence(Math.min(100, profile.confidence() + (score >= 60 ? 2 : 1)));
        if (score >= 45 && profile.stats().get(session.type().primaryStat()) < 100) profile.stats().add(session.type().primaryStat(), 1);
        profile.recordTrainingSession();
        cooldowns.put(player.getUUID(), tick + COOLDOWN_TICKS);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "Training complete: " + grade + " | Score " + score + "/100 | XP +" + xp + (development > 0 ? " | Development +" + development : "")));
    }

    private int calculateScore(TrainingSession s) {
        return switch (s.type()) {
            case SPEED -> clamp((int)(s.distance() * 2.2) + s.activeTicks() / 8);
            case DRIBBLING -> clamp(s.successfulActions() * 3 + (int)(s.distance() * 1.1));
            case PASSING, SHOOTING -> clamp(s.successfulActions() * 12 + s.actions() * 3);
            case DEFENSE -> clamp(s.activeTicks() / 3 + s.successfulActions() * 4);
            case STAMINA -> clamp(s.activeTicks() / 2 + (int)(s.distance() * 1.5));
        };
    }
    private static int clamp(int value) { return Math.max(0, Math.min(100, value)); }
    private static String grade(int score) {
        if (score >= 90) return "S";
        if (score >= 75) return "A";
        if (score >= 60) return "B";
        if (score >= 45) return "C";
        if (score >= 25) return "D";
        if (score >= 10) return "F";
        return "F";
    }
}
