package com.bluelockmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bluelockmod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FootballHud {
    private FootballHud() {}

    @SubscribeEvent
    public static void render(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;
        GuiGraphics g = event.getGuiGraphics();
        int x = 12;
        int y = mc.getWindow().getGuiScaledHeight() - 42;
        int stamina = ClientEgoFlowState.stamina();
        int flow = ClientEgoFlowState.flow();
        int ego = ClientEgoFlowState.ego();
        g.fill(x, y, x + 120, y + 8, 0x99000000);
        g.fill(x + 1, y + 1, x + 1 + (int)(118.0F * stamina / 100.0F), y + 7, 0xFFFFFFFF);
        g.drawString(mc.font, Component.translatable("bluelockmod.hud.stamina", stamina), x, y - 12, 0xFFFFFFFF, true);
        g.drawString(mc.font, Component.translatable("bluelockmod.hud.ego_flow", ego, flow), x, y + 12, 0xFFFFFFFF, true);

        if (ClientEgoFlowState.awakeningTicks() > 0) {
            g.drawString(mc.font, Component.translatable("bluelockmod.hud.awakening"), x, y + 25, 0xFFFFFFFF, true);
        } else if (ClientEgoFlowState.flowTicks() > 0) {
            g.drawString(mc.font, Component.translatable("bluelockmod.hud.flow_active"), x, y + 25, 0xFFFFFFFF, true);
        } else if (ClientEgoFlowState.flowCooldown() > 0) {
            g.drawString(mc.font, Component.translatable("bluelockmod.hud.flow_cooldown", (ClientEgoFlowState.flowCooldown() + 19) / 20), x, y + 25, 0xFFFFFFFF, true);
        }
    }
}
