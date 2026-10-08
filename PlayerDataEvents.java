package com.bluelockmod.system;

import com.bluelockmod.player.PlayerProfileProvider;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.skill.SkillManager;
import com.bluelockmod.vision.VisionManager;
import com.bluelockmod.ego.EgoFlowManager;
import com.bluelockmod.game.ScoutingManager;
import com.bluelockmod.game.MatchPerformanceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class PlayerDataEvents {
    @SubscribeEvent public void attach(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (event.getObject() instanceof net.minecraft.world.entity.player.Player) event.addCapability(PlayerProfileProvider.ID, new PlayerProfileProvider());
    }
    @SubscribeEvent public void clone(PlayerEvent.Clone event) {
        event.getOriginal().revive();
        ProfileManager.optional(event.getOriginal()).ifPresent(oldProfile -> ProfileManager.optional(event.getEntity()).ifPresent(newProfile -> ProfileManager.copy(oldProfile, newProfile)));
        event.getOriginal().invalidateCaps();
    }
    @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent event) { if (event.getEntity() instanceof ServerPlayer player) player.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(p -> {}); }
    @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent event) { if (event.getEntity() instanceof ServerPlayer player) { SkillManager.clear(player.getUUID());
        VisionManager.clear(player.getUUID()); EgoFlowManager.clear(player.getUUID()); ScoutingManager.clear(player.getUUID()); MatchPerformanceManager.clear(player.getUUID()); } }
}
