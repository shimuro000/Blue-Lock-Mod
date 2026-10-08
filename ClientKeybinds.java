package com.bluelockmod.client;

import com.bluelockmod.network.FootballActionPacket;
import com.bluelockmod.network.NetworkHandler;
import com.bluelockmod.network.SkillActionPacket;
import com.bluelockmod.network.FlowActionPacket;
import com.bluelockmod.network.VisionTogglePacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "bluelockmod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientKeybinds {
    public static final KeyMapping CONTROL = new KeyMapping("key.bluelockmod.control", GLFW.GLFW_KEY_F, "key.categories.bluelockmod");
    public static final KeyMapping PASS = new KeyMapping("key.bluelockmod.pass", GLFW.GLFW_KEY_G, "key.categories.bluelockmod");
    public static final KeyMapping SHOOT = new KeyMapping("key.bluelockmod.shoot", GLFW.GLFW_KEY_R, "key.categories.bluelockmod");
    public static final KeyMapping DRIBBLE = new KeyMapping("key.bluelockmod.dribble", GLFW.GLFW_KEY_V, "key.categories.bluelockmod");
    public static final KeyMapping SKILL_1 = new KeyMapping("key.bluelockmod.skill_1", GLFW.GLFW_KEY_1, "key.categories.bluelockmod");
    public static final KeyMapping SKILL_2 = new KeyMapping("key.bluelockmod.skill_2", GLFW.GLFW_KEY_2, "key.categories.bluelockmod");
    public static final KeyMapping SKILL_3 = new KeyMapping("key.bluelockmod.skill_3", GLFW.GLFW_KEY_3, "key.categories.bluelockmod");
    public static final KeyMapping SKILL_4 = new KeyMapping("key.bluelockmod.skill_4", GLFW.GLFW_KEY_4, "key.categories.bluelockmod");
    public static final KeyMapping FLOW = new KeyMapping("key.bluelockmod.flow", GLFW.GLFW_KEY_H, "key.categories.bluelockmod");
    public static final KeyMapping VISION = new KeyMapping("key.bluelockmod.vision", GLFW.GLFW_KEY_B, "key.categories.bluelockmod");
    public static final KeyMapping PROFILE = new KeyMapping("key.bluelockmod.profile", GLFW.GLFW_KEY_P, "key.categories.bluelockmod");

    private ClientKeybinds() {}

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(CONTROL);
        event.register(PASS);
        event.register(SHOOT);
        event.register(DRIBBLE);
        event.register(FLOW);
        event.register(VISION);
        event.register(PROFILE);
        event.register(SKILL_1);
        event.register(SKILL_2);
        event.register(SKILL_3);
        event.register(SKILL_4);
    }

    @Mod.EventBusSubscriber(modid = "bluelockmod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class InputHandler {
        @SubscribeEvent
        public static void input(InputEvent.Key event) {
            if (event.getAction() != GLFW.GLFW_PRESS) return;
            if (Minecraft.getInstance().player == null) return;
            if (FLOW.matches(event.getKey(), event.getScanCode())) { NetworkHandler.CHANNEL.sendToServer(new FlowActionPacket()); return; }
            if (VISION.matches(event.getKey(), event.getScanCode())) { NetworkHandler.CHANNEL.sendToServer(new VisionTogglePacket()); return; }
            if (PROFILE.matches(event.getKey(), event.getScanCode())) { CharacterCreationScreen.open(); return; }
            if (SKILL_1.matches(event.getKey(), event.getScanCode())) sendSkill(0);
            else if (SKILL_2.matches(event.getKey(), event.getScanCode())) sendSkill(1);
            else if (SKILL_3.matches(event.getKey(), event.getScanCode())) sendSkill(2);
            else if (SKILL_4.matches(event.getKey(), event.getScanCode())) sendSkill(3);
            else if (CONTROL.matches(event.getKey(), event.getScanCode())) send(FootballActionPacket.Action.CONTROL, 0.0F);
            else if (PASS.matches(event.getKey(), event.getScanCode())) send(FootballActionPacket.Action.PASS, 0.65F);
            else if (SHOOT.matches(event.getKey(), event.getScanCode())) send(FootballActionPacket.Action.SHOOT, 1.0F);
            else if (DRIBBLE.matches(event.getKey(), event.getScanCode())) send(FootballActionPacket.Action.DRIBBLE, 0.0F);
        }

        private static void sendSkill(int slot) { NetworkHandler.CHANNEL.sendToServer(new SkillActionPacket(slot)); }

        private static void send(FootballActionPacket.Action action, float power) {
            NetworkHandler.CHANNEL.sendToServer(new FootballActionPacket(action, power));
        }
    }
}
