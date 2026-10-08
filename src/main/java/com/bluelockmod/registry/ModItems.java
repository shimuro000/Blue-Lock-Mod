package com.bluelockmod.registry;

import com.bluelockmod.BlueLockMod;
import com.bluelockmod.football.FootballItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BlueLockMod.MOD_ID);
    public static final RegistryObject<Item> FOOTBALL = ITEMS.register("football", () -> new FootballItem(new Item.Properties().stacksTo(1)));
    private ModItems() {}
}
