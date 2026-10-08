package com.bluelockmod.game;

import com.bluelockmod.BlueLockMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.bluelockmod.ai.AIDifficulty;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MatchCommands {
    private MatchCommands() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("bluelock")
                .then(Commands.literal("match")
                        .then(Commands.literal("create").executes(ctx -> create(ctx.getSource().getPlayerOrException())))
                        .then(Commands.literal("start").executes(ctx -> start(ctx.getSource().getPlayerOrException())))
                        .then(Commands.literal("stop").executes(ctx -> stop(ctx.getSource().getPlayerOrException())))
                        .then(Commands.literal("status").executes(ctx -> status(ctx.getSource().getPlayerOrException())))
                        .then(Commands.literal("difficulty")
                                .then(Commands.argument("level", StringArgumentType.word())
                                        .executes(ctx -> difficulty(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "level")))))));
    }

    private static int create(ServerPlayer player) {
        Match match = com.bluelockmod.system.ServerTickHandler.instance().matchManager().createQuickMatch(player);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Match " + match.id() + " ready. Use /bluelock match start."));
        return 1;
    }

    private static int start(ServerPlayer player) {
        boolean ok = com.bluelockmod.system.ServerTickHandler.instance().matchManager().start(player);
        if (!ok) player.sendSystemMessage(net.minecraft.network.chat.Component.literal("No ready match found."));
        return ok ? 1 : 0;
    }

    private static int stop(ServerPlayer player) {
        boolean ok = com.bluelockmod.system.ServerTickHandler.instance().matchManager().stop(player);
        if (!ok) player.sendSystemMessage(net.minecraft.network.chat.Component.literal("No active match found."));
        return ok ? 1 : 0;
    }

    private static int difficulty(ServerPlayer player, String value) {
        AIDifficulty difficulty;
        try { difficulty = AIDifficulty.valueOf(value.toUpperCase(java.util.Locale.ROOT)); }
        catch (IllegalArgumentException ex) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Use: easy, normal, hard, or elite."));
            return 0;
        }
        boolean ok = com.bluelockmod.system.ServerTickHandler.instance().matchManager().setDifficulty(player, difficulty);
        if (!ok) player.sendSystemMessage(net.minecraft.network.chat.Component.literal("No active match found."));
        return ok ? 1 : 0;
    }

    private static int status(ServerPlayer player) {
        Match match = com.bluelockmod.system.ServerTickHandler.instance().matchManager().forPlayer(player.getUUID());
        if (match == null) player.sendSystemMessage(net.minecraft.network.chat.Component.literal("No active match."));
        else player.sendSystemMessage(net.minecraft.network.chat.Component.literal(match.home().name() + " " + match.home().score() + " - " + match.away().score() + " " + match.away().name() + " | " + match.state()));
        return 1;
    }
}
