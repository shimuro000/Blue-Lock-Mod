package com.bluelockmod.registry;

import com.bluelockmod.BlueLockMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BlueLockMod.MOD_ID);

    public static final RegistryObject<SoundEvent> GOAL = register("goal");
    public static final RegistryObject<SoundEvent> KICK = register("kick");
    public static final RegistryObject<SoundEvent> PASS = register("pass");
    public static final RegistryObject<SoundEvent> SHOOT = register("shoot");
    public static final RegistryObject<SoundEvent> FLOW = register("flow");
    public static final RegistryObject<SoundEvent> AWAKENING = register("awakening");
    public static final RegistryObject<SoundEvent> UI_CONFIRM = register("ui_confirm");

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(BlueLockMod.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    private ModSounds() {}
}
