package com.bluelockmod.effects;

import com.bluelockmod.ego.EgoFlowManager;
import com.bluelockmod.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

/** Lightweight server-authoritative visual/audio feedback. Uses vanilla particles so clients need no custom particle engine. */
public final class FootballEffects {
    private FootballEffects() {}

    public static void tick(ServerPlayer player) {
        if (player.tickCount % 6 != 0) return;
        if (EgoFlowManager.isAwakeningActive(player)) {
            player.serverLevel().sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0D, player.getZ(), 3, 0.35D, 0.55D, 0.35D, 0.015D);
        } else if (EgoFlowManager.isFlowActive(player)) {
            player.serverLevel().sendParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 1.0D, player.getZ(), 2, 0.3D, 0.5D, 0.3D, 0.01D);
        }
    }

    public static void flowActivated(ServerPlayer player) {
        player.playNotifySound(ModSounds.FLOW.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
        player.serverLevel().sendParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 1.0D, player.getZ(), 14, 0.5D, 0.8D, 0.5D, 0.03D);
    }

    public static void awakening(ServerPlayer player) {
        player.playNotifySound(ModSounds.AWAKENING.get(), SoundSource.PLAYERS, 0.95F, 0.9F);
        player.serverLevel().sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0D, player.getZ(), 24, 0.7D, 1.0D, 0.7D, 0.04D);
    }

    public static void goal(ServerPlayer player) {
        player.playNotifySound(ModSounds.GOAL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.serverLevel().sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY() + 1.2D, player.getZ(), 18, 0.8D, 0.8D, 0.8D, 0.04D);
    }
}
