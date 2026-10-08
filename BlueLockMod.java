package com.bluelockmod;

import com.bluelockmod.registry.ModBlocks;
import com.bluelockmod.registry.ModCreativeTabs;
import com.bluelockmod.registry.ModEntities;
import com.bluelockmod.registry.ModItems;
import com.bluelockmod.registry.ModMenus;
import com.bluelockmod.registry.ModSounds;
import com.bluelockmod.system.PlayerDataEvents;
import com.bluelockmod.system.ServerTickHandler;
import com.bluelockmod.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BlueLockMod.MOD_ID)
public final class BlueLockMod {
    public static final String MOD_ID = "bluelockmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BlueLockMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        NetworkHandler.register();

        MinecraftForge.EVENT_BUS.register(new PlayerDataEvents());
        MinecraftForge.EVENT_BUS.register(new ServerTickHandler());
        LOGGER.info("Blue Lock Mod initialized on Forge 1.20.1-47.4.26");
    }
}
