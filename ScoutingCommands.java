package com.bluelockmod.game;

import com.bluelockmod.BlueLockMod;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ScoutingCommands {
    private ScoutingCommands() {}
    @SubscribeEvent public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("bluelock").then(Commands.literal("scouting")
                .executes(ctx -> show(ctx.getSource().getPlayerOrException()))));
    }
    private static int show(ServerPlayer player) {
        ScoutingProfile profile = ScoutingManager.get(player.getUUID());
        if (profile == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("No active scouting report."));
            return 0;
        }
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(profile.summary(player)));
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "Observed: passes=" + profile.passes() + ", shots=" + profile.shots() + ", dribbles=" + profile.dribbles()
                        + ", tackles=" + profile.tackles() + ", interceptions=" + profile.interceptions()));
        return 1;
    }
}
