package com.bluelockmod.game;

import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.player.ProgressionService;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Server-side per-match individual performance accounting. */
public final class MatchPerformance {
    public enum Action { PASS, SHOT, DRIBBLE, TACKLE, INTERCEPTION, CONTROL }

    private final UUID playerId;
    private int passes, shots, dribbles, tackles, interceptions, controls, goals;
    private int clutchPlays;
    private int failedActions;
    private int successfulPasses, successfulDribbles, successfulShots, successfulTackles, successfulInterceptions;
    private int pressureExposure;

    public MatchPerformance(UUID playerId) { this.playerId = playerId; }
    public void record(Action action, boolean successful, boolean clutch) {
        switch (action) {
            case PASS -> passes++;
            case SHOT -> shots++;
            case DRIBBLE -> dribbles++;
            case TACKLE -> tackles++;
            case INTERCEPTION -> interceptions++;
            case CONTROL -> controls++;
        }
        if (successful) switch (action) {
            case PASS -> successfulPasses++;
            case SHOT -> successfulShots++;
            case DRIBBLE -> successfulDribbles++;
            case TACKLE -> successfulTackles++;
            case INTERCEPTION -> successfulInterceptions++;
            case CONTROL -> { }
        }
        if (!successful) failedActions++;
        if (clutch && successful) clutchPlays++;
    }
    public void goal() { goals++; clutchPlays++; }
    public void pressure() { pressureExposure++; }
    public int goals() { return goals; }

    public int rating(int teamGoals, int opponentGoals, int ticksRemaining, int duration) {
        double value = 50.0;
        value += goals * 18.0;
        value += Math.min(8, passes * 0.8);
        value += Math.min(10, dribbles * 1.2);
        value += Math.min(8, tackles * 1.5);
        value += Math.min(8, interceptions * 2.0);
        value += Math.min(5, controls * 0.5);
        value += clutchPlays * 4.0;
        value -= Math.min(15, failedActions * 1.0);
        if (teamGoals > opponentGoals) value += 8;
        else if (teamGoals < opponentGoals) value -= 5;
        if (pressureExposure > 0) value += Math.min(6, pressureExposure * 0.75);
        return Math.max(0, Math.min(100, (int)Math.round(value)));
    }

    public boolean mvp(int rating) { return rating >= 85 || (goals >= 2 && rating >= 75); }
    public boolean hasContribution() { return goals > 0 || passes > 0 || dribbles > 0 || tackles > 0 || interceptions > 0; }
    public int passes() { return passes; } public int shots() { return shots; } public int dribbles() { return dribbles; }
    public int successfulPasses() { return successfulPasses; } public int successfulDribbles() { return successfulDribbles; }
    public int successfulShots() { return successfulShots; } public int successfulTackles() { return successfulTackles; }
    public int successfulInterceptions() { return successfulInterceptions; }
    public int tackles() { return tackles; } public int interceptions() { return interceptions; }

    public void finish(ServerPlayer player, int teamGoals, int opponentGoals, int ticksRemaining, int duration) {
        PlayerProfile profile = com.bluelockmod.player.ProfileManager.get(player);
        int rating = rating(teamGoals, opponentGoals, ticksRemaining, duration);
        boolean win = teamGoals > opponentGoals;
        boolean draw = teamGoals == opponentGoals;
        boolean mvp = mvp(rating);
        profile.recordMatchPerformance(rating, win, draw, mvp, shots, passes, dribbles, tackles, interceptions);
        int xp = 25 + rating + goals * 50 + clutchPlays * 8 + (win ? 35 : draw ? 15 : 0);
        profile.addXp(xp);
        int rankDelta = win ? Math.max(1, (rating - 55) / 5 + 1) : draw ? Math.max(0, (rating - 65) / 8) : -Math.max(0, (65 - rating) / 8);
        profile.setRank(Math.max(1, profile.rank() - rankDelta));
        player.sendSystemMessage(Component.translatable("bluelockmod.match.performance", rating, xp, profile.confidence()));
        player.sendSystemMessage(Component.translatable("bluelockmod.match.performance_breakdown", goals, shots, passes, dribbles, tackles, interceptions));
        if (mvp) player.sendSystemMessage(Component.translatable("bluelockmod.match.mvp"));
    }
}
