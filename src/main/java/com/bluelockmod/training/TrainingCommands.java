package com.bluelockmod.training;

import com.bluelockmod.BlueLockMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TrainingCommands {
    private TrainingCommands() {}
    @SubscribeEvent public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("bluelock").then(Commands.literal("training")
                .then(Commands.literal("start").then(Commands.argument("type", StringArgumentType.word())
                        .executes(c -> start(c.getSource().getPlayerOrException(), StringArgumentType.getString(c, "type")))))
                .then(Commands.literal("stop").executes(c -> stop(c.getSource().getPlayerOrException())))
                .then(Commands.literal("status").executes(c -> status(c.getSource().getPlayerOrException())))));
    }
    private static int start(ServerPlayer p, String raw) {
        TrainingType type = TrainingType.parse(raw);
        if (type == null) { p.sendSystemMessage(Component.literal("Types: speed, dribbling, passing, shooting, defense, stamina")); return 0; }
        boolean ok = com.bluelockmod.system.ServerTickHandler.instance().trainingManager().start(p, type);
        if (!ok) p.sendSystemMessage(Component.literal("Cannot start training: already training or on cooldown."));
        return ok ? 1 : 0;
    }
    private static int stop(ServerPlayer p) {
        boolean ok = com.bluelockmod.system.ServerTickHandler.instance().trainingManager().stop(p);
        if (!ok) p.sendSystemMessage(Component.literal("No active training session."));
        return ok ? 1 : 0;
    }
    private static int status(ServerPlayer p) {
        TrainingSession s = com.bluelockmod.system.ServerTickHandler.instance().trainingManager().session(p);
        if (s == null) { p.sendSystemMessage(Component.literal("No active training session.")); return 0; }
        p.sendSystemMessage(Component.literal("Training: " + s.type().displayName() + " | actions " + s.actions() + " | distance " + String.format("%.1f", s.distance())));
        return 1;
    }
}
