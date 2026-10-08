package com.bluelockmod.client;

import com.bluelockmod.BlueLockMod;
import com.bluelockmod.network.CharacterCustomizationPacket;
import com.bluelockmod.network.NetworkHandler;
import com.bluelockmod.player.BodyBuild;
import com.bluelockmod.player.CharacterArchetype;
import com.bluelockmod.player.HairStyle;
import com.bluelockmod.player.Personality;
import com.bluelockmod.player.PlayerProfileProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, value = Dist.CLIENT)
public final class CharacterCreationScreen extends Screen {
    private EditBox name;
    private EditBox nickname;
    private EditBox jersey;
    private int hairIndex;
    private int bodyIndex;
    private int personalityIndex;
    private int archetypeIndex;
    private boolean leftFoot;
    private int hairColor = 0x202020;
    private int eyeColor = 0x4A6A8A;

    private CharacterCreationScreen() { super(Component.literal("Character Creation")); }

    public static void open() { Minecraft.getInstance().setScreen(new CharacterCreationScreen()); }

    @Override protected void init() {
        var profile = Minecraft.getInstance().player;
        if (profile != null) profile.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(p -> {
            name = new EditBox(font, width / 2 - 100, 44, 200, 20, Component.literal("Name"));
            name.setValue(p.name()); name.setMaxLength(24);
            nickname = new EditBox(font, width / 2 - 100, 72, 200, 20, Component.literal("Nickname"));
            nickname.setValue(p.nickname()); nickname.setMaxLength(20);
            jersey = new EditBox(font, width / 2 - 100, 100, 200, 20, Component.literal("Jersey"));
            jersey.setValue(Integer.toString(p.jerseyNumber())); jersey.setMaxLength(2);
            hairIndex = HairStyle.valueOf(p.hairStyle().name()).ordinal();
            bodyIndex = p.bodyBuild().ordinal(); personalityIndex = p.personality().ordinal(); archetypeIndex = CharacterArchetype.parse(p.archetype()).ordinal(); leftFoot = "LEFT".equalsIgnoreCase(p.dominantFoot());
            hairColor = p.hairColor(); eyeColor = p.eyeColor();
            addRenderableWidget(name); addRenderableWidget(nickname); addRenderableWidget(jersey);
            addRenderableWidget(Button.builder(Component.literal("Archetype: " + CharacterArchetype.values()[archetypeIndex].name()), b -> {
                archetypeIndex = (archetypeIndex + 1) % CharacterArchetype.values().length; b.setMessage(Component.literal("Archetype: " + CharacterArchetype.values()[archetypeIndex].name()));
            }).bounds(width / 2 - 100, 124, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Dominant Foot: " + (leftFoot ? "Left" : "Right")), b -> {
                leftFoot = !leftFoot; b.setMessage(Component.literal("Dominant Foot: " + (leftFoot ? "Left" : "Right")));
            }).bounds(width / 2 - 100, 148, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Hair: " + HairStyle.values()[hairIndex].display()), b -> {
                hairIndex = (hairIndex + 1) % HairStyle.values().length; b.setMessage(Component.literal("Hair: " + HairStyle.values()[hairIndex].display()));
            }).bounds(width / 2 - 100, 172, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Build: " + BodyBuild.values()[bodyIndex].display()), b -> {
                bodyIndex = (bodyIndex + 1) % BodyBuild.values().length; b.setMessage(Component.literal("Build: " + BodyBuild.values()[bodyIndex].display()));
            }).bounds(width / 2 - 100, 196, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Personality: " + Personality.values()[personalityIndex].display()), b -> {
                personalityIndex = (personalityIndex + 1) % Personality.values().length; b.setMessage(Component.literal("Personality: " + Personality.values()[personalityIndex].display()));
            }).bounds(width / 2 - 100, 220, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Hair Color: #" + hex(hairColor)), b -> {
                hairColor = nextColor(hairColor); b.setMessage(Component.literal("Hair Color: #" + hex(hairColor)));
            }).bounds(width / 2 - 100, 244, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Eye Color: #" + hex(eyeColor)), b -> {
                eyeColor = nextEyeColor(eyeColor); b.setMessage(Component.literal("Eye Color: #" + hex(eyeColor)));
            }).bounds(width / 2 - 100, 268, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Save Character"), b -> save()).bounds(width / 2 - 100, 300, 96, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose()).bounds(width / 2 + 4, 300, 96, 20).build());
            setInitialFocus(name);
        });
    }

    private void save() {
        int jerseyValue = 9;
        try { jerseyValue = Integer.parseInt(jersey.getValue()); } catch (NumberFormatException ignored) {}
        NetworkHandler.CHANNEL.sendToServer(new CharacterCustomizationPacket(name.getValue(), nickname.getValue(), jerseyValue,
                HairStyle.values()[hairIndex], hairColor, hairColor ^ 0x101010, eyeColor,
                BodyBuild.values()[bodyIndex], Personality.values()[personalityIndex], CharacterArchetype.values()[archetypeIndex], leftFoot ? "LEFT" : "RIGHT"));
        onClose();
    }

    private static int nextColor(int color) {
        int[] palette = {0x202020, 0x5A3825, 0x8A5A30, 0xB8B8B8, 0xE6E6E6, 0x263B72, 0x6E1E4F};
        for (int i = 0; i < palette.length; i++) if (palette[i] == color) return palette[(i + 1) % palette.length];
        return palette[0];
    }
    private static int nextEyeColor(int color) {
        int[] palette = {0x4A6A8A, 0x2E6B3D, 0x6E4B2A, 0x6A3D72, 0x202020};
        for (int i = 0; i < palette.length; i++) if (palette[i] == color) return palette[(i + 1) % palette.length];
        return palette[0];
    }
    private static String hex(int color) { return String.format("%06X", color & 0xFFFFFF); }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        g.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        g.drawCenteredString(font, "Appearance is cosmetic; your football build remains server-authoritative.", width / 2, 326, 0xAAAAAA);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { onClose(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
