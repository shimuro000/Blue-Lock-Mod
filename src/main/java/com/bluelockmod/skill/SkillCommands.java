package com.bluelockmod.skill;

import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.player.ProfileManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bluelockmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SkillCommands {
    private SkillCommands() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bluelock")
                .then(Commands.literal("skill")
                        .then(Commands.literal("list").executes(ctx -> list(ctx.getSource().getPlayerOrException())))
                        .then(Commands.literal("unlock").then(Commands.argument("id", StringArgumentType.word())
                                .executes(ctx -> unlock(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "id")))))
                        .then(Commands.literal("equip").then(Commands.argument("slot", IntegerArgumentType.integer(1, 4))
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(ctx -> equip(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "slot"), StringArgumentType.getString(ctx, "id"))))))
                        .then(Commands.literal("status").executes(ctx -> status(ctx.getSource().getPlayerOrException())))));
    }

    private static int list(ServerPlayer player) {
        PlayerProfile p = ProfileManager.get(player);
        player.sendSystemMessage(Component.literal("Skill Points: " + p.skillPoints() + " | Loadout: " + String.join(", ", safeLoadout(p))));
        for (SkillDefinition skill : SkillManager.registry().all().values()) {
            String state = p.skillState().isUnlocked(skill.id()) ? "UNLOCKED" : "LOCKED";
            player.sendSystemMessage(Component.literal(skill.id() + " — " + skill.name() + " | " + state + " | Level " + skill.unlockLevel() + " | Cost " + skill.skillPointCost() + " | Mastery " + p.skillState().mastery(skill.id()) + "%"));
        }
        return 1;
    }

    private static int unlock(ServerPlayer player, String id) {
        if (SkillManager.unlock(player, id)) return 1;
        player.sendSystemMessage(Component.literal("Cannot unlock skill: check level, prerequisites, skill points, or skill id."));
        return 0;
    }

    private static int equip(ServerPlayer player, int slot, String id) {
        if (SkillManager.equip(player, slot - 1, id)) return 1;
        player.sendSystemMessage(Component.literal("Cannot equip skill: the slot, skill, or unlock state is invalid."));
        return 0;
    }

    private static int status(ServerPlayer player) {
        PlayerProfile p = ProfileManager.get(player);
        player.sendSystemMessage(Component.literal("Skill Points: " + p.skillPoints() + " | Slots: " + String.join(" / ", safeLoadout(p))));
        return 1;
    }

    private static String[] safeLoadout(PlayerProfile p) {
        String[] slots = p.skillState().loadout().snapshot();
        for (int i = 0; i < slots.length; i++) if (slots[i] == null) slots[i] = "empty";
        return slots;
    }
}
