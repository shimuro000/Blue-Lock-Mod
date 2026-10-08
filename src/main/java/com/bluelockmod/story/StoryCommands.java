package com.bluelockmod.story;

import com.bluelockmod.BlueLockMod;
import com.bluelockmod.player.CharacterArchetype;
import com.bluelockmod.player.PlayerProfileProvider;
import com.bluelockmod.player.PlayerStats;
import com.bluelockmod.player.ProgressionService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StoryCommands {
    @SubscribeEvent public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("bluelock")
            .then(Commands.literal("profile")
                .then(Commands.literal("create").then(Commands.argument("archetype", StringArgumentType.word()).executes(c -> create(c.getSource().getPlayerOrException(), StringArgumentType.getString(c, "archetype")))))
                .then(Commands.literal("info").executes(c -> info(c.getSource().getPlayerOrException())))
                .then(Commands.literal("customize").executes(c -> customize(c.getSource().getPlayerOrException())))
                .then(Commands.literal("upgrade").then(Commands.argument("stat", StringArgumentType.word()).executes(c -> upgrade(c.getSource().getPlayerOrException(), StringArgumentType.getString(c, "stat"))))))
            .then(Commands.literal("story")
                .then(Commands.literal("next").executes(c -> next(c.getSource().getPlayerOrException())))
                .then(Commands.literal("state").executes(c -> state(c.getSource().getPlayerOrException())))));
    }
    private static int create(ServerPlayer p, String raw) {
        CharacterArchetype a = CharacterArchetype.parse(raw);
        p.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(profile -> { profile.applyArchetype(a); profile.story().state(StoryState.ARRIVAL); p.sendSystemMessage(Component.literal("Player created as " + a.name() + ". Story: ARRIVAL")); });
        return 1;
    }
    private static int info(ServerPlayer p) {
        p.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(x -> p.sendSystemMessage(Component.literal("Level " + x.level() + " | OVR " + x.stats().overall() + " | Ego " + x.ego() + " | Flow " + x.flow() + " | Rank " + x.rank() + " | " + x.archetype() + " | Matches " + x.matchesPlayed() + " | Goals " + x.goals())));
        return 1;
    }
    private static int customize(ServerPlayer p) {
        p.sendSystemMessage(Component.literal("Open the Character Creation screen with the Profile key or client command."));
        return 1;
    }
    private static int upgrade(ServerPlayer p, String raw) {
        try { PlayerStats.Stat stat = PlayerStats.Stat.valueOf(raw.toUpperCase()); if (ProgressionService.spendPoint(p, stat)) { p.sendSystemMessage(Component.literal("Upgraded " + stat.name() + " by +1.")); return 1; } }
        catch (IllegalArgumentException ignored) {}
        p.sendSystemMessage(Component.literal("Invalid stat or no Development Points.")); return 0;
    }
    private static int next(ServerPlayer p) { StoryManager.advance(p); return state(p); }
    private static int state(ServerPlayer p) { p.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(x -> p.sendSystemMessage(Component.literal("Story state: " + x.story().state().name()))); return 1; }
}
