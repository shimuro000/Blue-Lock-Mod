package com.bluelockmod.registry;

import com.bluelockmod.BlueLockMod;
import com.bluelockmod.football.FootballEntity;
import com.bluelockmod.ai.FootballAIEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BlueLockMod.MOD_ID);
    public static final RegistryObject<EntityType<FootballEntity>> FOOTBALL = ENTITY_TYPES.register("football", () -> EntityType.Builder.<FootballEntity>of(FootballEntity::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(8).updateInterval(1).build("football"));
    public static final RegistryObject<EntityType<FootballAIEntity>> FOOTBALL_AI = ENTITY_TYPES.register("football_ai", () -> EntityType.Builder.<FootballAIEntity>of(FootballAIEntity::new, MobCategory.CREATURE).sized(0.6F, 1.95F).clientTrackingRange(32).updateInterval(2).build("football_ai"));
    private ModEntities() {}
}
