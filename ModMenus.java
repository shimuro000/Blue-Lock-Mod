package com.bluelockmod.registry;

import com.bluelockmod.BlueLockMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BlueLockMod.MOD_ID);
    public static final net.minecraftforge.registries.RegistryObject<MenuType<net.minecraft.world.inventory.AbstractContainerMenu>> PROFILE = MENUS.register("profile", () -> IForgeMenuType.create((windowId, inv, data) -> new com.bluelockmod.player.ProfileMenu(windowId, inv)));
    private ModMenus() {}
}
