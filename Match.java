package com.bluelockmod.game;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public final class Match {
    private final UUID id = UUID.randomUUID();
    private final Team home;
    private final Team away;
    private MatchState state = MatchState.WAITING;
    private int ticksRemaining;
    private int stateTicks;
    private int elapsedTicks;
    private UUID lastScorer;
    private final Map<UUID, Integer> goalsByPlayer = new HashMap<>();

    public Match(Team home, Team away, int durationTicks) {
        this.home = home;
        this.away = away;
        this.ticksRemaining = Math.max(1, durationTicks);
    }

    public UUID id() { return id; }
    public Team home() { return home; }
    public Team away() { return away; }
    public MatchState state() { return state; }
    public void state(MatchState state) { this.state = state; this.stateTicks = 0; }
    public int ticksRemaining() { return ticksRemaining; }
    public UUID lastScorer() { return lastScorer; }
    public int stateTicks() { return stateTicks; }
    public int elapsedTicks() { return elapsedTicks; }
    public int goalsByPlayer(UUID player) { return goalsByPlayer.getOrDefault(player, 0); }

    public void tick() {
        stateTicks++;
        elapsedTicks++;
        if (state == MatchState.GOAL) {
            if (stateTicks >= 40) state = MatchState.RESTART;
            return;
        }
        if (state == MatchState.RESTART) {
            if (stateTicks >= 20) state = MatchState.PLAYING;
            return;
        }
        if (state != MatchState.PLAYING && state != MatchState.EXTRA_TIME) return;
        if (ticksRemaining > 0) ticksRemaining--;
        if (ticksRemaining == 0) state = MatchState.FINISHED;
    }

    public void goal(TeamSide side, UUID scorer) {
        if (state != MatchState.PLAYING && state != MatchState.EXTRA_TIME) return;
        if (side == TeamSide.HOME) home.scoreGoal(); else away.scoreGoal();
        lastScorer = scorer;
        if (scorer != null) goalsByPlayer.merge(scorer, 1, Integer::sum);
        state(MatchState.GOAL);
    }

    public void restart() {
        if (state == MatchState.GOAL || state == MatchState.RESTART) state(MatchState.PLAYING);
    }
}
