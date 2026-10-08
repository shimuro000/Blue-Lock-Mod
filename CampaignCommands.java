package com.bluelockmod.story;

import com.bluelockmod.BlueLockMod;
import com.bluelockmod.game.MatchManager;
import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.player.PlayerProfileProvider;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CampaignCommands {
    @SubscribeEvent public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("bluelock").then(Commands.literal("campaign")
            .then(Commands.literal("start").executes(c -> start(c.getSource().getPlayerOrException())))
            .then(Commands.literal("next").executes(c -> next(c.getSource().getPlayerOrException())))
            .then(Commands.literal("status").executes(c -> status(c.getSource().getPlayerOrException())))
            .then(Commands.literal("objective").executes(c -> objective(c.getSource().getPlayerOrException())))));
    }
    private static int start(ServerPlayer p) {
        return p.getCapability(PlayerProfileProvider.CAPABILITY).map(profile -> {
            if (FirstSelectionCampaign.active(profile) && profile.story().firstSelectionMatch() < FirstSelectionCampaign.fixtureCount()) {
                p.sendSystemMessage(Component.literal("First Selection is already active. Use /bluelock campaign next.")); return 0;
            }
            FirstSelectionCampaign.start(p, profile);
            return 1;
        }).orElse(0);
    }
    private static int next(ServerPlayer p) {
        return p.getCapability(PlayerProfileProvider.CAPABILITY).map(profile -> {
            if (profile.story().state() == StoryState.SECOND_SELECTION_INTRO || profile.story().state() == StoryState.SEASON_1_COMPLETE) {
                p.sendSystemMessage(Component.literal("The First Selection campaign is complete.")); return 0;
            }
            FirstSelectionCampaign.prepareFixture(profile);
            MatchManager manager = com.bluelockmod.event.ServerTickHandler.matchManager();
            if (manager.forPlayer(p.getUUID()) != null) { p.sendSystemMessage(Component.literal("You are already in a match.")); return 0; }
            manager.createCampaignMatch(p);
            return 1;
        }).orElse(0);
    }
    private static int status(ServerPlayer p) {
        return p.getCapability(PlayerProfileProvider.CAPABILITY).map(profile -> {
            var s = profile.story();
            p.sendSystemMessage(Component.literal("First Selection | Match " + Math.min(s.firstSelectionMatch() + 1, FirstSelectionCampaign.fixtureCount()) + "/" + FirstSelectionCampaign.fixtureCount() + " | Points " + s.firstSelectionPoints() + " | W " + s.firstSelectionWins() + " D " + s.firstSelectionDraws() + " L " + s.firstSelectionLosses() + " | Goals " + s.firstSelectionGoals() + " | Objectives " + s.firstSelectionObjectives()));
            return 1;
        }).orElse(0);
    }
    private static int objective(ServerPlayer p) {
        return p.getCapability(PlayerProfileProvider.CAPABILITY).map(profile -> { p.sendSystemMessage(Component.literal(FirstSelectionCampaign.objectiveText(profile.story().currentObjective()))); return 1; }).orElse(0);
    }
}
