package com.bluelockmod.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public final class PlayerProfileProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PlayerProfile> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation ID = new ResourceLocation("bluelockmod", "player_profile");
    private final PlayerProfile profile = new PlayerProfile();
    private final LazyOptional<PlayerProfile> optional = LazyOptional.of(() -> profile);

    @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    public CompoundTag serialize() { return profile.save(); }
    public void deserialize(CompoundTag tag) { profile.load(tag); }
    @Override public CompoundTag serializeNBT() { return profile.save(); }
    @Override public void deserializeNBT(CompoundTag tag) { profile.load(tag); }
}
