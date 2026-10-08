package com.bluelockmod.registry;

import com.bluelockmod.BlueLockMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(ForgeRegistries.CREATIVE_MODE_TABS, BlueLockMod.MOD_ID);
    public static final RegistryObject<CreativeModeTab> TABS_MAIN = TABS.register("main", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.bluelockmod.main")).icon(() -> new ItemStack(ModItems.FOOTBALL.get())).displayItems((params, output) -> { output.accept(ModItems.FOOTBALL.get()); output.accept(ModBlocks.MATCH_TERMINAL.get()); output.accept(ModBlocks.TRAINING_STATION.get()); }).build());
    private ModCreativeTabs() {}
}
