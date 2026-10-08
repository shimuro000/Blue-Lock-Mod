package com.bluelockmod.player;

import net.minecraft.nbt.CompoundTag;
import java.util.EnumMap;
import java.util.Map;

public final class PlayerStats {
    public enum Stat { TOP_SPEED, ACCELERATION, STAMINA, STRENGTH, BALANCE, AGILITY, REACTION, BALL_CONTROL, DRIBBLING, PASSING, LONG_PASSING, SHOOTING, FINISHING, SHOT_POWER, CURVE, FIRST_TOUCH, VISION, SPATIAL_AWARENESS, POSITIONING, COMPOSURE, DECISION_MAKING, ANTICIPATION, TACKLING, INTERCEPTION, MARKING }
    private final Map<Stat, Integer> values = new EnumMap<>(Stat.class);
    public PlayerStats() { for (Stat stat : Stat.values()) values.put(stat, 40); }
    public int get(Stat stat) { return values.get(stat); }
    public void resetToBase() { for (Stat stat : Stat.values()) values.put(stat, 40); }
    public void set(Stat stat, int value) { values.put(stat, Math.max(0, Math.min(100, value))); }
    public void add(Stat stat, int amount) { set(stat, get(stat) + amount); }
    public int overall() { return (int)Math.round(values.values().stream().mapToInt(Integer::intValue).average().orElse(0)); }
    public void save(CompoundTag tag) { CompoundTag stats = new CompoundTag(); values.forEach((s,v) -> stats.putInt(s.name(), v)); tag.put("Stats", stats); }
    public void load(CompoundTag tag) { if (!tag.contains("Stats")) return; CompoundTag stats = tag.getCompound("Stats"); for (Stat s : Stat.values()) if (stats.contains(s.name())) set(s, stats.getInt(s.name())); }
}
