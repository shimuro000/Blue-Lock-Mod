package com.bluelockmod.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Phase 19: short-term, match-scoped learning for named First Selection players. */
public final class DynamicCanonAIManager {
    private static final Map<UUID, Map<String, Memory>> MATCHES = new HashMap<>();
    private DynamicCanonAIManager() {}

    public static void start(UUID matchId) { MATCHES.put(matchId, new HashMap<>()); }
    public static void clear(UUID matchId) { MATCHES.remove(matchId); }

    private static Memory memory(UUID matchId, String player) {
        if (matchId == null || player == null) return new Memory();
        return MATCHES.computeIfAbsent(matchId, k -> new HashMap<>())
                .computeIfAbsent(player, k -> new Memory());
    }

    public static void observe(UUID matchId, String player, Action action, boolean success) {
        if (matchId == null || player == null || action == null) return;
        Memory m = memory(matchId, player);
        m.attempts.merge(action, 1, Integer::sum);
        if (success) m.successes.merge(action, 1, Integer::sum);
        m.recent = Math.min(100, m.recent + (success ? 2 : 1));
    }

    public static double successRate(UUID matchId, String player, Action action) {
        Memory m = memory(matchId, player);
        int a = m.attempts.getOrDefault(action, 0);
        if (a == 0) return 0.5D;
        return m.successes.getOrDefault(action, 0) / (double) a;
    }

    public static double preference(UUID matchId, String player, Action action) {
        Memory m = memory(matchId, player);
        int a = m.attempts.getOrDefault(action, 0);
        if (a == 0) return 0.0D;
        double rate = successRate(matchId, player, action);
        return Math.max(-0.18D, Math.min(0.18D, (rate - 0.5D) * 0.36D));
    }

    public static boolean learned(UUID matchId, String player, Action action) {
        return memory(matchId, player).attempts.getOrDefault(action, 0) >= 2;
    }

    public static void decay() {
        for (Map<String, Memory> map : MATCHES.values()) for (Memory m : map.values()) {
            if (m.recent > 0) m.recent--;
            if (m.recent == 0) {
                for (Action a : Action.values()) {
                    int attempts = m.attempts.getOrDefault(a, 0);
                    if (attempts > 1) m.attempts.put(a, attempts - 1);
                    int success = m.successes.getOrDefault(a, 0);
                    if (success > attempts - 1) m.successes.put(a, Math.max(0, attempts - 1));
                }
                m.recent = 20;
            }
        }
    }

    public enum Action { PASS, SHOOT, DRIBBLE, PRESS, SUPPORT, INTERCEPT }
    private static final class Memory {
        final Map<Action,Integer> attempts = new HashMap<>();
        final Map<Action,Integer> successes = new HashMap<>();
        int recent;
    }
}
