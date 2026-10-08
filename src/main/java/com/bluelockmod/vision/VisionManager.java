package com.bluelockmod.vision;

import com.bluelockmod.ai.FootballAIEntity;
import com.bluelockmod.football.FootballEntity;
import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.player.PlayerStats.Stat;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.skill.SkillManager;
import com.bluelockmod.game.Match;
import com.bluelockmod.game.MatchManager;
import com.bluelockmod.game.MatchState;
import com.bluelockmod.network.NetworkHandler;
import com.bluelockmod.network.VisionSnapshotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Server-authoritative field perception. It only reports targets the player could reasonably perceive. */
public final class VisionManager {
    private static final Set<UUID> ACTIVE = ConcurrentHashMap.newKeySet();
    private static long tick;
    private VisionManager() {}

    public static boolean toggle(ServerPlayer player) {
        UUID id = player.getUUID();
        if (!ACTIVE.add(id)) { ACTIVE.remove(id); sendEmpty(player); player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("bluelockmod.vision.off")); return false; }
        sendSnapshot(player);
        player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("bluelockmod.vision.on"));
        return true;
    }
    public static boolean active(ServerPlayer player) { return ACTIVE.contains(player.getUUID()); }
    public static void clear(UUID id) { ACTIVE.remove(id); }

    public static void tick(MinecraftServer server) {
        tick++;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!active(player)) continue;
            Match match = com.bluelockmod.system.ServerTickHandler.instance().matchManager().forPlayer(player.getUUID());
            boolean inMatch = match != null && match.state() == MatchState.PLAYING;
            int interval = inMatch ? 5 : 10;
            if (tick % interval == 0) sendSnapshot(player);
        }
    }

    private static void sendSnapshot(ServerPlayer player) {
        PlayerProfile profile = ProfileManager.get(player);
        VisionLevel level = VisionLevel.fromStats(profile.stats().get(Stat.VISION), profile.stats().get(Stat.SPATIAL_AWARENESS));
        if (SkillManager.spatialReadActive(player)) level = upgraded(level);
        List<VisionTarget> targets = new ArrayList<>();
        Match match = com.bluelockmod.system.ServerTickHandler.instance().matchManager().forPlayer(player.getUUID());
        UUID ballId = null;
        if (match != null && match.state() == MatchState.PLAYING) {
            // Nearby football is always perceptible when it is in normal visual range.
            for (FootballEntity ball : player.level().getEntitiesOfClass(FootballEntity.class, player.getBoundingBox().inflate(level.radius()))) {
                if (player.hasLineOfSight(ball)) { ballId = ball.getUUID(); break; }
            }
        }
        AABB box = player.getBoundingBox().inflate(level.radius());
        List<Entity> candidates = new ArrayList<>();
        candidates.addAll(player.level().getEntitiesOfClass(ServerPlayer.class, box, e -> e != player));
        candidates.addAll(player.level().getEntitiesOfClass(FootballAIEntity.class, box, Entity::isAlive));
        for (Entity e : candidates) {
            if (!player.hasLineOfSight(e)) continue;
            double distance = player.distanceTo(e);
            if (distance > level.radius()) continue;
            boolean teammate = com.bluelockmod.system.ServerTickHandler.instance().matchManager().sameTeam(match, player.getUUID(), e.getUUID());
            if (!teammate && level.tier() < 2 && distance > 12) continue;
            double bearing = bearing(player, e);
            boolean predicted = level.tier() >= 3;
            boolean threat = !teammate && distance <= 16 && level.tier() >= 2;
            Vec3 projected = e.position().add(e.getDeltaMovement().scale(10.0D));
            double projectedDistance = player.position().distanceTo(projected);
            double projectedBearing = bearingTo(player, projected);
            targets.add(new VisionTarget(e.getUUID(), displayName(e), distance, bearing, teammate, predicted, threat, projectedDistance, projectedBearing));
        }
        if (ballId != null) {
            Entity ballEntity = player.level().getEntity(ballId);
            if (ballEntity != null) targets.add(new VisionTarget(ballId, "Football", player.distanceTo(ballEntity), bearing(player, ballEntity), true, level.tier() >= 2, false, player.distanceTo(ballEntity), bearing(player, ballEntity)));
        }
        targets.sort(Comparator.comparingDouble(VisionTarget::distance));
        int max = level.tier() == 1 ? 4 : level.tier() == 2 ? 7 : level.tier() == 3 ? 10 : 14;
        if (targets.size() > max) targets = new ArrayList<>(targets.subList(0, max));
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new VisionSnapshotPacket(level, SkillManager.spatialReadActive(player), targets));
    }

    private static void sendEmpty(ServerPlayer player) { NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new VisionSnapshotPacket(VisionLevel.BASIC, false, List.of())); }
    private static VisionLevel upgraded(VisionLevel level) { return switch (level) { case BASIC -> VisionLevel.INTERMEDIATE; case INTERMEDIATE -> VisionLevel.ADVANCED; case ADVANCED, ELITE -> VisionLevel.ELITE; }; }
    private static String displayName(Entity e) { return e instanceof ServerPlayer p ? p.getGameProfile().getName() : e instanceof FootballAIEntity ai ? ai.getDisplayName().getString() : e.getName().getString(); }
    private static double bearing(ServerPlayer player, Entity entity) { return bearingTo(player, entity.position()); }
    private static double bearingTo(ServerPlayer player, Vec3 position) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 to = position.subtract(player.position()).normalize();
        return Math.toDegrees(Math.atan2(look.cross(new Vec3(0, 1, 0)).dot(to), look.dot(to)));
    }
}
