package com.bluelockmod.player;

import net.minecraft.nbt.CompoundTag;
import com.bluelockmod.story.StoryProgress;
import com.bluelockmod.skill.SkillState;

public final class PlayerProfile {
    private int schemaVersion = 7;
    private String name = "Player";
    private String nickname = "";
    private int jerseyNumber = 9;
    private HairStyle hairStyle = HairStyle.SHORT;
    private int hairColor = 0x202020;
    private int secondaryHairColor = 0x101010;
    private int eyeColor = 0x4A6A8A;
    private BodyBuild bodyBuild = BodyBuild.BALANCED;
    private Personality personality = Personality.COMPETITIVE;
    private int xp;
    private int level = 1;
    private int developmentPoints;
    private int skillPoints = 3;
    private int ego;
    private int flow;
    private int flowMastery;
    private int flowActivations;
    private int awakeningCount;
    private int stamina = 100;
    private int rank = 300;
    private int matchesPlayed;
    private int goals;
    private int assists;
    private int confidence = 50;
    private int lastMatchRating;
    private int bestMatchRating;
    private int shots;
    private int passes;
    private int dribbles;
    private int tackles;
    private int interceptions;
    private int wins;
    private int draws;
    private int losses;
    private int mvps;
    private int trainingSessions;
    private String archetype = "STRIKER";
    private String dominantFoot = "RIGHT";
    private final PlayerStats stats = new PlayerStats();
    private final SkillState skillState = new SkillState();
    private final StoryProgress story = new StoryProgress();

    public PlayerProfile() {
        skillState.unlock("direct_shot");
        skillState.unlock("burst_dribble");
        skillState.unlock("spatial_read");
        skillState.loadout().equip(0, "direct_shot");
        skillState.loadout().equip(1, "burst_dribble");
        skillState.loadout().equip(2, "spatial_read");
    }

    public int xp() { return xp; }
    public int level() { return level; }
    public int developmentPoints() { return developmentPoints; }
    public int skillPoints() { return skillPoints; }
    public int ego() { return ego; }
    public int flow() { return flow; }
    public int flowMastery() { return flowMastery; }
    public int flowActivations() { return flowActivations; }
    public int awakeningCount() { return awakeningCount; }
    public int stamina() { return stamina; }
    public int rank() { return rank; }
    public String archetype() { return archetype; }
    public String dominantFoot() { return dominantFoot; }
    public PlayerStats stats() { return stats; }
    public SkillState skillState() { return skillState; }
    public StoryProgress story() { return story; }
    public int matchesPlayed() { return matchesPlayed; }
    public int goals() { return goals; }
    public int assists() { return assists; }
    public int confidence() { return confidence; }
    public int lastMatchRating() { return lastMatchRating; }
    public int bestMatchRating() { return bestMatchRating; }
    public int shots() { return shots; }
    public int passes() { return passes; }
    public int dribbles() { return dribbles; }
    public int tackles() { return tackles; }
    public int interceptions() { return interceptions; }
    public int wins() { return wins; }
    public int draws() { return draws; }
    public int losses() { return losses; }
    public int mvps() { return mvps; }
    public int trainingSessions() { return trainingSessions; }
    public String name() { return name; }
    public String nickname() { return nickname; }
    public int jerseyNumber() { return jerseyNumber; }
    public HairStyle hairStyle() { return hairStyle; }
    public int hairColor() { return hairColor; }
    public int secondaryHairColor() { return secondaryHairColor; }
    public int eyeColor() { return eyeColor; }
    public BodyBuild bodyBuild() { return bodyBuild; }
    public Personality personality() { return personality; }
    public void setName(String value) { name = clean(value, "Player", 24); }
    public void setNickname(String value) { nickname = clean(value, "", 20); }
    public void setJerseyNumber(int value) { jerseyNumber = Math.max(1, Math.min(99, value)); }
    public void setHairStyle(HairStyle value) { hairStyle = value == null ? HairStyle.SHORT : value; }
    public void setHairColor(int value) { hairColor = value & 0xFFFFFF; }
    public void setSecondaryHairColor(int value) { secondaryHairColor = value & 0xFFFFFF; }
    public void setEyeColor(int value) { eyeColor = value & 0xFFFFFF; }
    public void setBodyBuild(BodyBuild value) { bodyBuild = value == null ? BodyBuild.BALANCED : value; }
    public void setPersonality(Personality value) { personality = value == null ? Personality.COMPETITIVE : value; }
    private static String clean(String value, String fallback, int max) {
        if (value == null) return fallback;
        String cleaned = value.replaceAll("[\r\n\t]", "").trim();
        if (cleaned.isEmpty()) return fallback;
        return cleaned.substring(0, Math.min(max, cleaned.length()));
    }

    public void addDevelopmentPoint(int amount) { developmentPoints = Math.max(0, developmentPoints + amount); }
    public void addSkillPoints(int amount) { skillPoints = Math.max(0, skillPoints + amount); }
    public boolean spendSkillPoints(int amount) { if (amount < 0 || skillPoints < amount) return false; skillPoints -= amount; return true; }
    public void addXp(int amount) { xp = Math.max(0, xp + amount); while (xp >= xpForNextLevel()) { xp -= xpForNextLevel(); level++; developmentPoints++; skillPoints++; } }
    private int xpForNextLevel() { return 100 + (level - 1) * 50; }
    public void setArchetype(String value) { archetype = value; }
    public void setDominantFoot(String value) { dominantFoot = value; }
    public void addEgo(int amount) { ego = Math.max(0, Math.min(100, ego + amount)); }
    public void addFlow(int amount) { flow = Math.max(0, Math.min(100, flow + amount)); }
    public void addFlowMastery(int amount) { flowMastery = Math.max(0, Math.min(100, flowMastery + amount)); }
    public void recordFlowActivation() { flowActivations++; }
    public void recordAwakening() { awakeningCount++; }
    public void setStamina(int value) { stamina = Math.max(0, Math.min(100, value)); }
    public void setConfidence(int value) { confidence = Math.max(0, Math.min(100, value)); }
    public void recordMatchPerformance(int rating, boolean win, boolean draw, boolean mvp, int shotCount, int passCount, int dribbleCount, int tackleCount, int interceptionCount) {
        lastMatchRating = Math.max(0, Math.min(100, rating));
        bestMatchRating = Math.max(bestMatchRating, lastMatchRating);
        shots += Math.max(0, shotCount);
        passes += Math.max(0, passCount);
        dribbles += Math.max(0, dribbleCount);
        tackles += Math.max(0, tackleCount);
        interceptions += Math.max(0, interceptionCount);
        if (win) wins++; else if (draw) draws++; else losses++;
        if (mvp) mvps++;
        int confidenceDelta = lastMatchRating >= 80 ? 5 : lastMatchRating >= 65 ? 2 : lastMatchRating < 45 ? -4 : -1;
        if (win) confidenceDelta += 2; else if (!draw) confidenceDelta -= 1;
        setConfidence(confidence + confidenceDelta);
    }
    public void recordTrainingSession() { trainingSessions++; }
    public void setRank(int value) { rank = Math.max(1, value); }
    public void recordMatch(int goalCount, int assistCount) { matchesPlayed++; goals += Math.max(0, goalCount); assists += Math.max(0, assistCount); addXp(50 + goalCount * 100 + assistCount * 50); addEgo(goalCount * 4 + assistCount * 2); }
    public void applyArchetype(CharacterArchetype archetype) { stats.resetToBase(); this.archetype = archetype.name(); archetype.apply(stats); }

    public CompoundTag save() { CompoundTag tag = new CompoundTag(); tag.putInt("SchemaVersion", schemaVersion); tag.putString("Name", name); tag.putString("Nickname", nickname); tag.putInt("JerseyNumber", jerseyNumber); tag.putString("HairStyle", hairStyle.name()); tag.putInt("HairColor", hairColor); tag.putInt("SecondaryHairColor", secondaryHairColor); tag.putInt("EyeColor", eyeColor); tag.putString("BodyBuild", bodyBuild.name()); tag.putString("Personality", personality.name()); tag.putInt("XP", xp); tag.putInt("Level", level); tag.putInt("DevelopmentPoints", developmentPoints); tag.putInt("SkillPoints", skillPoints); tag.putInt("Ego", ego); tag.putInt("Flow", flow); tag.putInt("FlowMastery", flowMastery); tag.putInt("FlowActivations", flowActivations); tag.putInt("AwakeningCount", awakeningCount); tag.putInt("Stamina", stamina); tag.putInt("Rank", rank); tag.putInt("MatchesPlayed", matchesPlayed); tag.putInt("Goals", goals); tag.putInt("Assists", assists); tag.putInt("Confidence", confidence); tag.putInt("LastMatchRating", lastMatchRating); tag.putInt("BestMatchRating", bestMatchRating); tag.putInt("Shots", shots); tag.putInt("Passes", passes); tag.putInt("Dribbles", dribbles); tag.putInt("Tackles", tackles); tag.putInt("Interceptions", interceptions); tag.putInt("Wins", wins); tag.putInt("Draws", draws); tag.putInt("Losses", losses); tag.putInt("MVPs", mvps); tag.putInt("TrainingSessions", trainingSessions); tag.putString("Archetype", archetype); tag.putString("DominantFoot", dominantFoot); stats.save(tag); CompoundTag skills = new CompoundTag(); for (String id : skillState.unlocked()) skills.putBoolean("U_" + id, true); for (String id : skillState.unlocked()) skills.putInt("M_" + id, skillState.mastery(id)); String[] loadout = skillState.loadout().snapshot(); for (int i = 0; i < loadout.length; i++) if (loadout[i] != null) skills.putString("L_" + i, loadout[i]); tag.put("Skills", skills); tag.put("Story", story.save()); return tag; }
    public void load(CompoundTag tag) { name = tag.contains("Name") ? clean(tag.getString("Name"), "Player", 24) : "Player"; nickname = tag.contains("Nickname") ? clean(tag.getString("Nickname"), "", 20) : ""; jerseyNumber = Math.max(1, Math.min(99, tag.contains("JerseyNumber") ? tag.getInt("JerseyNumber") : 9)); hairStyle = HairStyle.parse(tag.contains("HairStyle") ? tag.getString("HairStyle") : "SHORT"); hairColor = tag.contains("HairColor") ? tag.getInt("HairColor") & 0xFFFFFF : 0x202020; secondaryHairColor = tag.contains("SecondaryHairColor") ? tag.getInt("SecondaryHairColor") & 0xFFFFFF : 0x101010; eyeColor = tag.contains("EyeColor") ? tag.getInt("EyeColor") & 0xFFFFFF : 0x4A6A8A; bodyBuild = BodyBuild.parse(tag.contains("BodyBuild") ? tag.getString("BodyBuild") : "BALANCED"); personality = Personality.parse(tag.contains("Personality") ? tag.getString("Personality") : "COMPETITIVE"); xp = tag.getInt("XP"); level = Math.max(1, tag.getInt("Level")); developmentPoints = Math.max(0, tag.getInt("DevelopmentPoints")); skillPoints = Math.max(0, tag.contains("SkillPoints") ? tag.getInt("SkillPoints") : 3); ego = Math.max(0, Math.min(100, tag.getInt("Ego"))); flow = Math.max(0, Math.min(100, tag.getInt("Flow"))); flowMastery = Math.max(0, Math.min(100, tag.contains("FlowMastery") ? tag.getInt("FlowMastery") : 0)); flowActivations = Math.max(0, tag.contains("FlowActivations") ? tag.getInt("FlowActivations") : 0); awakeningCount = Math.max(0, tag.contains("AwakeningCount") ? tag.getInt("AwakeningCount") : 0); stamina = Math.max(0, Math.min(100, tag.contains("Stamina") ? tag.getInt("Stamina") : 100)); rank = Math.max(1, tag.getInt("Rank")); matchesPlayed = Math.max(0, tag.getInt("MatchesPlayed")); goals = Math.max(0, tag.getInt("Goals")); assists = Math.max(0, tag.getInt("Assists")); confidence = Math.max(0, Math.min(100, tag.contains("Confidence") ? tag.getInt("Confidence") : 50)); lastMatchRating = Math.max(0, Math.min(100, tag.contains("LastMatchRating") ? tag.getInt("LastMatchRating") : 0)); bestMatchRating = Math.max(lastMatchRating, Math.min(100, tag.contains("BestMatchRating") ? tag.getInt("BestMatchRating") : 0)); shots = Math.max(0, tag.getInt("Shots")); passes = Math.max(0, tag.getInt("Passes")); dribbles = Math.max(0, tag.getInt("Dribbles")); tackles = Math.max(0, tag.getInt("Tackles")); interceptions = Math.max(0, tag.getInt("Interceptions")); wins = Math.max(0, tag.getInt("Wins")); draws = Math.max(0, tag.getInt("Draws")); losses = Math.max(0, tag.getInt("Losses")); mvps = Math.max(0, tag.getInt("MVPs")); trainingSessions = Math.max(0, tag.getInt("TrainingSessions")); if (tag.contains("Archetype")) archetype = tag.getString("Archetype"); if (tag.contains("DominantFoot")) dominantFoot = tag.getString("DominantFoot"); stats.load(tag); skillState.clear(); if (tag.contains("Skills")) { CompoundTag skills = tag.getCompound("Skills"); for (String key : skills.getAllKeys()) if (key.startsWith("U_") && skills.getBoolean(key)) skillState.unlock(key.substring(2)); for (String key : skills.getAllKeys()) if (key.startsWith("M_")) skillState.addMastery(key.substring(2), skills.getInt(key)); for (int i = 0; i < 4; i++) if (skills.contains("L_" + i)) skillState.loadout().equip(i, skills.getString("L_" + i)); } else { skillState.unlock("direct_shot"); skillState.unlock("burst_dribble"); skillState.unlock("spatial_read"); skillState.loadout().equip(0, "direct_shot"); skillState.loadout().equip(1, "burst_dribble"); skillState.loadout().equip(2, "spatial_read"); } if (tag.contains("Story")) story.load(tag.getCompound("Story")); }
}
