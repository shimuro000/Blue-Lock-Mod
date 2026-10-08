package com.bluelockmod.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

public final class ProfileManager {
    private ProfileManager() {}
    public static PlayerProfile get(Player player) { return player.getCapability(PlayerProfileProvider.CAPABILITY).orElseThrow(() -> new IllegalStateException("Player profile capability missing")); }
    public static LazyOptional<PlayerProfile> optional(Player player) { return player.getCapability(PlayerProfileProvider.CAPABILITY); }
    public static void copy(PlayerProfile from, PlayerProfile to) { CompoundTag data = from.save(); to.load(data); }
}
