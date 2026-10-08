package com.bluelockmod.game;

import com.bluelockmod.ai.AIRole;
import com.bluelockmod.player.PlayerStats.Stat;

import java.util.EnumMap;
import java.util.Map;

/** First-Selection character template. Values are gameplay ratings, not claims of official numeric databook stats. */
public record CanonPlayer(String name, int number, AIRole role, String weapon, Map<Stat, Integer> stats) {
    public CanonPlayer {
        stats = new EnumMap<>(stats);
    }
    public int stat(Stat stat) { return stats.getOrDefault(stat, 40); }
}
