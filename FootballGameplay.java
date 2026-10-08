package com.bluelockmod.football;

import com.bluelockmod.network.FootballActionPacket.Action;
import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.ego.EgoFlowManager;
import com.bluelockmod.skill.SkillManager;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.player.PlayerStats.Stat;
import com.bluelockmod.animation.AnimationNetwork;
import com.bluelockmod.animation.FootballAnimation;
import com.bluelockmod.system.ServerTickHandler;
import com.bluelockmod.game.MatchPerformance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.bluelockmod.registry.ModSounds;
import net.minecraft.sounds.SoundSource;

import java.util.Comparator;
import java.util.List;

public final class FootballGameplay {
    private static final double CONTROL_RANGE = 2.15D;
    private static final double ACTION_RANGE = 2.45D;
    private FootballGameplay() {}

    public static void handleAction(ServerPlayer player, Action action, float power) {
        FootballEntity ball = nearestBall(player, ACTION_RANGE);
        if (ball == null) return;
        PlayerProfile profile = ProfileManager.get(player);
        switch (action) {
            case CONTROL -> control(player, ball, profile);
            case PASS -> pass(player, ball, profile, power);
            case SHOOT -> shoot(player, ball, profile, power);
            case DRIBBLE -> dribble(player, ball, profile);
        }
    }

    private static void control(ServerPlayer player, FootballEntity ball, PlayerProfile profile) {
        if (player.distanceTo(ball) > CONTROL_RANGE) { EgoFlowManager.onFailedPlay(player); ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.CONTROL, false); return; }
        if (!ball.isControlled() || ball.owner().filter(player.getUUID()::equals).isEmpty()) {
            ball.control(player.getUUID());
            EgoFlowManager.onSuccessfulPlay(player, 1, 2);
            ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.CONTROL, true);
            AnimationNetwork.play(player, FootballAnimation.CONTROL);
        }
    }

    private static void pass(ServerPlayer player, FootballEntity ball, PlayerProfile profile, float power) {
        if (!canUse(profile, 3)) { EgoFlowManager.onFailedPlay(player); ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.PASS, false); return; }
        if (!ownsOrControl(player, ball)) { EgoFlowManager.onFailedPlay(player); ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.PASS, false); return; }
        double passPower = 0.55D + power * 1.25D + stat(profile, Stat.PASSING) / 250.0D + SkillManager.spatialReadBonus(player) * 0.35D + EgoFlowManager.accuracyBonus(player) * 0.20D;
        ball.kick(player.getLookAngle().normalize().scale(passPower).add(0, 0.06D, 0));
        player.playNotifySound(ModSounds.PASS.get(), SoundSource.PLAYERS, 0.55F, 1.0F);
        spend(profile, 3);
        profile.addXp(2);
        ServerTickHandler.instance().trainingManager().recordAction(player, true);
        EgoFlowManager.onSuccessfulPlay(player, 1, 3);
        ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.PASS, true);
        AnimationNetwork.play(player, FootballAnimation.PASS);
    }

    private static void shoot(ServerPlayer player, FootballEntity ball, PlayerProfile profile, float power) {
        if (!canUse(profile, 8)) { EgoFlowManager.onFailedPlay(player); ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.SHOT, false); return; }
        if (!ownsOrControl(player, ball)) { EgoFlowManager.onFailedPlay(player); ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.SHOT, false); return; }
        double accuracy = 0.78D + stat(profile, Stat.FINISHING) / 500.0D + SkillManager.spatialReadBonus(player) + EgoFlowManager.accuracyBonus(player);
        Vec3 direction = player.getLookAngle().normalize();
        double shotPower = 0.85D + power * 1.55D + stat(profile, Stat.SHOT_POWER) / 180.0D;
        direction = new Vec3(direction.x * accuracy, direction.y, direction.z * accuracy).normalize();
        ball.kick(direction.scale(shotPower).add(0, 0.12D + stat(profile, Stat.CURVE) / 1000.0D, 0));
        player.playNotifySound(ModSounds.SHOOT.get(), SoundSource.PLAYERS, 0.75F, 1.0F);
        spend(profile, 8);
        profile.addXp(5);
        ServerTickHandler.instance().trainingManager().recordAction(player, true);
        EgoFlowManager.onSuccessfulPlay(player, 2, 4);
        ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.SHOT, true);
        AnimationNetwork.play(player, FootballAnimation.SHOOT);
    }

    private static void dribble(ServerPlayer player, FootballEntity ball, PlayerProfile profile) {
        if (!canUse(profile, 5)) { EgoFlowManager.onFailedPlay(player); ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.DRIBBLE, false); return; }
        if (!ownsOrControl(player, ball)) { EgoFlowManager.onFailedPlay(player); ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.DRIBBLE, false); return; }
        ball.control(player.getUUID());
        player.playNotifySound(ModSounds.KICK.get(), SoundSource.PLAYERS, 0.45F, 1.05F);
        spend(profile, 5);
        profile.addXp(1);
        ServerTickHandler.instance().trainingManager().recordAction(player, true);
        EgoFlowManager.onSuccessfulPlay(player, 1, 2);
        ServerTickHandler.instance().recordMatchAction(player, MatchPerformance.Action.DRIBBLE, true);
        AnimationNetwork.play(player, FootballAnimation.DRIBBLE);
    }

    private static boolean ownsOrControl(ServerPlayer player, FootballEntity ball) {
        if (player.distanceTo(ball) > CONTROL_RANGE) return false;
        if (!ball.isControlled()) { ball.control(player.getUUID()); return true; }
        return ball.owner().filter(player.getUUID()::equals).isPresent();
    }

    private static FootballEntity nearestBall(ServerPlayer player, double range) {
        AABB box = player.getBoundingBox().inflate(range);
        List<FootballEntity> balls = player.level().getEntitiesOfClass(FootballEntity.class, box);
        return balls.stream().min(Comparator.comparingDouble(player::distanceToSqr)).orElse(null);
    }

    private static boolean canUse(PlayerProfile profile, int cost) { return profile.stamina() >= cost; }
    private static void spend(PlayerProfile profile, int amount) { profile.setStamina(profile.stamina() - amount); }
    private static int stat(PlayerProfile profile, Stat stat) { return profile.stats().get(stat); }

    public static void tickPlayer(ServerPlayer player) {
        PlayerProfile profile = ProfileManager.get(player);
        int regen = player.isSprinting() ? -1 : 1;
        if (regen > 0) profile.setStamina(Math.min(100, (int)Math.min(100, profile.stamina() + regen + EgoFlowManager.staminaRegenBonus(player))));
        else profile.setStamina(Math.max(0, profile.stamina() - 1));

        if (player.isSprinting()) {
            float factor = (float)((1.0F + profile.stats().get(Stat.TOP_SPEED) / 1000.0F) * EgoFlowManager.speedMultiplier(player));
            player.setSprinting(player.getFoodData().getFoodLevel() > 0 && profile.stamina() > 0);
            Vec3 movement = player.getDeltaMovement();
            if (movement.horizontalDistanceSqr() > 0.01D) player.setDeltaMovement(movement.x * factor, movement.y, movement.z * factor);
        }
    }
}
