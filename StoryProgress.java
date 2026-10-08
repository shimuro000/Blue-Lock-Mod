package com.bluelockmod.story;

import net.minecraft.nbt.CompoundTag;
import java.util.HashSet;
import java.util.Set;

public final class StoryProgress {
    private StoryState state = StoryState.PROLOGUE;
    private String team = "Z";
    private int firstSelectionMatch;
    private final Set<String> flags = new HashSet<>();
    private int firstSelectionPoints;
    private int firstSelectionWins;
    private int firstSelectionDraws;
    private int firstSelectionLosses;
    private int firstSelectionGoals;
    private int firstSelectionObjectives;
    private String currentObjective = "WIN_OR_PERFORM";
    private final CompoundTag canonDevelopment = new CompoundTag();

    public StoryState state() { return state; }
    public void state(StoryState value) { state = value; }
    public String team() { return team; }
    public void team(String value) { team = value; }
    public int firstSelectionMatch() { return firstSelectionMatch; }
    public void firstSelectionMatch(int value) { firstSelectionMatch = Math.max(0, value); }
    public boolean hasFlag(String flag) { return flags.contains(flag); }
    public int firstSelectionPoints() { return firstSelectionPoints; }
    public int firstSelectionWins() { return firstSelectionWins; }
    public int firstSelectionDraws() { return firstSelectionDraws; }
    public int firstSelectionLosses() { return firstSelectionLosses; }
    public int firstSelectionGoals() { return firstSelectionGoals; }
    public int firstSelectionObjectives() { return firstSelectionObjectives; }
    public String currentObjective() { return currentObjective; }
    public void recordFirstSelectionResult(boolean win, boolean draw, int goals, boolean objectiveComplete) {
        if (win) { firstSelectionWins++; firstSelectionPoints += 3; }
        else if (draw) { firstSelectionDraws++; firstSelectionPoints += 1; }
        else firstSelectionLosses++;
        firstSelectionGoals += Math.max(0, goals);
        if (objectiveComplete) firstSelectionObjectives++;
    }
    public void currentObjective(String value) { currentObjective = value == null ? "WIN_OR_PERFORM" : value; }
    public void flag(String flag) { flags.add(flag); }
    public int canonMatches(String player) { return canonDevelopment.getCompound(safeKey(player)).getInt("Matches"); }
    public int canonTrainings(String player) { return canonDevelopment.getCompound(safeKey(player)).getInt("Trainings"); }
    public int canonGrowth(String player, String stat) { return canonDevelopment.getCompound(safeKey(player)).getInt("S_" + stat); }
    public void addCanonGrowth(String player, String stat, int amount) {
        if (player == null || stat == null || amount == 0) return;
        CompoundTag data = canonDevelopment.getCompound(safeKey(player));
        data.putInt("S_" + stat, Math.max(0, data.getInt("S_" + stat) + amount));
        canonDevelopment.put(safeKey(player), data);
    }
    public void recordCanonMatch(String player) {
        CompoundTag data = canonDevelopment.getCompound(safeKey(player));
        data.putInt("Matches", data.getInt("Matches") + 1);
        canonDevelopment.put(safeKey(player), data);
    }
    public void recordCanonTraining(String player) {
        CompoundTag data = canonDevelopment.getCompound(safeKey(player));
        data.putInt("Trainings", data.getInt("Trainings") + 1);
        canonDevelopment.put(safeKey(player), data);
    }
    public CompoundTag canonDevelopmentCopy() { return canonDevelopment.copy(); }
    private static String safeKey(String value) { return value == null ? "unknown" : value.replaceAll("[^A-Za-z0-9_-]", "_"); }
    public CompoundTag save() { CompoundTag tag = new CompoundTag(); tag.putString("State", state.name()); tag.putString("Team", team); tag.putInt("FirstSelectionMatch", firstSelectionMatch); tag.putInt("FirstSelectionPoints", firstSelectionPoints); tag.putInt("FirstSelectionWins", firstSelectionWins); tag.putInt("FirstSelectionDraws", firstSelectionDraws); tag.putInt("FirstSelectionLosses", firstSelectionLosses); tag.putInt("FirstSelectionGoals", firstSelectionGoals); tag.putInt("FirstSelectionObjectives", firstSelectionObjectives); tag.putString("CurrentObjective", currentObjective); var list = new net.minecraft.nbt.ListTag(); flags.forEach(f -> list.add(net.minecraft.nbt.StringTag.valueOf(f))); tag.put("Flags", list); tag.put("CanonDevelopment", canonDevelopment.copy()); return tag; }
    public void load(CompoundTag tag) { if (tag.contains("State")) try { state = StoryState.valueOf(tag.getString("State")); } catch (IllegalArgumentException ignored) {} if (tag.contains("Team")) team = tag.getString("Team"); firstSelectionMatch = tag.getInt("FirstSelectionMatch"); firstSelectionPoints = Math.max(0, tag.getInt("FirstSelectionPoints")); firstSelectionWins = Math.max(0, tag.getInt("FirstSelectionWins")); firstSelectionDraws = Math.max(0, tag.getInt("FirstSelectionDraws")); firstSelectionLosses = Math.max(0, tag.getInt("FirstSelectionLosses")); firstSelectionGoals = Math.max(0, tag.getInt("FirstSelectionGoals")); firstSelectionObjectives = Math.max(0, tag.getInt("FirstSelectionObjectives")); currentObjective = tag.contains("CurrentObjective") ? tag.getString("CurrentObjective") : "WIN_OR_PERFORM"; flags.clear(); canonDevelopment.getAllKeys().forEach(k -> canonDevelopment.remove(k)); if (tag.contains("CanonDevelopment")) { CompoundTag saved = tag.getCompound("CanonDevelopment"); saved.getAllKeys().forEach(k -> canonDevelopment.put(k, saved.getCompound(k).copy())); } if (tag.contains("Flags")) { for (var nbt : tag.getList("Flags", net.minecraft.nbt.Tag.TAG_STRING)) flags.add(nbt.getAsString()); } }
}
