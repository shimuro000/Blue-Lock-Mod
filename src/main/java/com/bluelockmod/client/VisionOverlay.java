package com.bluelockmod.client;

import com.bluelockmod.vision.VisionTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bluelockmod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VisionOverlay {
    private VisionOverlay() {}
    @SubscribeEvent
    public static void render(RenderGuiOverlayEvent.Post event) {
        if (!ClientVisionState.fresh() || Minecraft.getInstance().player == null) return;
        GuiGraphics g = event.getGuiGraphics();
        int x = 8, y = 8;
        g.drawString(Minecraft.getInstance().font, Component.literal("VISION: " + ClientVisionState.level().name()), x, y, 0xFFFFFF);
        y += 12;
        if (ClientVisionState.spatialRead()) { g.drawString(Minecraft.getInstance().font, Component.literal("SPATIAL READ"), x, y, 0xA8D8FF); y += 12; }
        int shown = 0;
        for (VisionTarget target : ClientVisionState.targets()) {
            String side = target.teammate() ? "ALLY" : (target.threat() ? "THREAT" : "RIVAL");
            String prediction = target.predicted() ? String.format(" →%+.0f°", target.projectedBearing()) : "";
            String text = String.format("%s  %s  %.0fm  %+.0f°%s", side, target.name(), target.distance(), target.bearing(), prediction);
            g.drawString(Minecraft.getInstance().font, Component.literal(text), x, y, target.threat() ? 0xFFB0B0 : 0xD0E8FF);
            y += 10;
            if (++shown >= 8) break;
        }
    }
}
