package com.bluelockmod.skill;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Persistent skill ownership/mastery; cooldowns remain server-runtime state. */
public final class SkillState {
    private final Set<String> unlocked = new LinkedHashSet<>();
    private final Map<String, Integer> mastery = new ConcurrentHashMap<>();
    private final SkillLoadout loadout = new SkillLoadout();

    public boolean isUnlocked(String id) { return id != null && unlocked.contains(id); }
    public boolean unlock(String id) { return id != null && unlocked.add(id); }
    public Set<String> unlocked() { return Set.copyOf(unlocked); }
    public int mastery(String id) { return mastery.getOrDefault(id, 0); }
    public int addMastery(String id, int amount) {
        if (id == null || amount <= 0) return mastery(id);
        int value = Math.max(0, Math.min(100, mastery(id) + amount));
        mastery.put(id, value);
        return value;
    }
    public SkillLoadout loadout() { return loadout; }
    public void clear() { unlocked.clear(); mastery.clear(); loadout.clear(); }
}
