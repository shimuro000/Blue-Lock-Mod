package com.bluelockmod.ai;

import com.bluelockmod.BlueLockMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AICommands {
    private AICommands() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("bluelock")
                .then(Commands.literal("ai")
                        .then(Commands.literal("spawn")
                                .executes(ctx -> spawn(ctx.getSource(), 6))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 16))
                                        .executes(ctx -> spawn(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "count")))))));
    }

    private static int spawn(CommandSourceStack source, int count) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        Vec3 origin = player.position();
        AIRole[] roles = AIRole.values();
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2.0D * i) / count;
            Vec3 pos = origin.add(Math.cos(angle) * 6.0D, 0, Math.sin(angle) * 6.0D);
            FootballAIManager.spawn(level, pos, roles[i % roles.length], i % 2 == 0);
        }
        source.sendSuccess(() -> net.minecraft.network.chat.Component.literal("Spawned " + count + " football AI players."), true);
        return count;
    }
}
