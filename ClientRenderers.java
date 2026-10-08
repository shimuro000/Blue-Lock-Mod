package com.bluelockmod.client;

import com.bluelockmod.BlueLockMod;
import com.bluelockmod.registry.ModEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientRenderers {
    private ClientRenderers() {}

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FOOTBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.FOOTBALL_AI.get(), ZombieRenderer::new);
    }
}
