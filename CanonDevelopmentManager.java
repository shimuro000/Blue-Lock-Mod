package com.bluelockmod.game;

import com.bluelockmod.ai.AIRole;
import com.bluelockmod.player.PlayerStats.Stat;
import com.bluelockmod.player.PlayerProfile;
import net.minecraft.server.level.ServerPlayer;

import java.util.EnumMap;
import java.util.Map;

/**
 * Phase 18: persistent NPC growth for First Selection.
 * Numeric changes are gameplay values, while milestone directions are constrained by the official story's development order.
 */
public final class CanonDevelopmentManager {
    private CanonDevelopmentManager() {}

    public static CanonPlayer developed(PlayerProfile profile, CanonPlayer base) {
        EnumMap<Stat,Integer> values = new EnumMap<>(Stat.class);
        for (Stat stat : Stat.values()) values.put(stat, Math.min(100, base.stat(stat) + profile.story().canonGrowth(base.name(), stat.name())));
        return new CanonPlayer(base.name(), base.number(), base.role(), base.weapon(), values);
    }

    public static void recordFixtureDevelopment(ServerPlayer captain, int fixtureIndex, boolean homeWin, int playerRating) {
        PlayerProfile profile = com.bluelockmod.player.ProfileManager.get(captain);
        for (String team : new String[]{"Team Z", "Team X", "Team Y", "Team W", "Team V"}) {
            for (CanonPlayer player : FirstSelectionRosters.roster(team)) {
                profile.story().recordCanonMatch(player.name());
                genericMatchGrowth(profile, player, homeWin, team.equals("Team Z"), playerRating);
                scriptedMilestones(profile, player, fixtureIndex);
            }
        }
        runBetweenMatchTraining(profile, fixtureIndex);
    }

    private static void genericMatchGrowth(PlayerProfile profile, CanonPlayer player, boolean homeWin, boolean teamZ, int humanRating) {
        int impact = homeWin == teamZ ? 1 : 0;
        if (player.role() == AIRole.STRIKER || player.role() == AIRole.WINGER) {
            add(profile, player, Stat.POSITIONING, impact);
            add(profile, player, Stat.DECISION_MAKING, impact);
            add(profile, player, Stat.FINISHING, impact);
        } else if (player.role() == AIRole.MIDFIELDER) {
            add(profile, player, Stat.PASSING, impact);
            add(profile, player, Stat.VISION, impact);
            add(profile, player, Stat.DECISION_MAKING, impact);
        } else if (player.role() == AIRole.DEFENDER) {
            add(profile, player, Stat.MARKING, impact);
            add(profile, player, Stat.INTERCEPTION, impact);
            add(profile, player, Stat.POSITIONING, impact);
        } else {
            add(profile, player, Stat.REACTION, impact);
            add(profile, player, Stat.POSITIONING, impact);
        }
        if (player.name().equals("Yoichi Isagi") && humanRating >= 70) add(profile, player, Stat.SPATIAL_AWARENESS, 1);
    }

    private static void runBetweenMatchTraining(PlayerProfile profile, int fixtureIndex) {
        // One focused training block between fixtures. Everyone improves, but only in a narrow role/weapon direction.
        for (String team : new String[]{"Team Z", "Team X", "Team Y", "Team W", "Team V"}) {
            for (CanonPlayer player : FirstSelectionRosters.roster(team)) {
                profile.story().recordCanonTraining(player.name());
                switch (player.name()) {
                    case "Yoichi Isagi" -> { add(profile, player, Stat.SPATIAL_AWARENESS, 1); add(profile, player, Stat.DECISION_MAKING, 1); }
                    case "Meguru Bachira" -> { add(profile, player, Stat.DRIBBLING, 1); add(profile, player, Stat.BALL_CONTROL, 1); }
                    case "Rensuke Kunigami" -> { add(profile, player, Stat.SHOT_POWER, 1); add(profile, player, Stat.SHOOTING, 1); }
                    case "Hyoma Chigiri" -> { add(profile, player, Stat.ACCELERATION, 1); }
                    case "Seishiro Nagi" -> { add(profile, player, Stat.FIRST_TOUCH, 1); }
                    case "Reo Mikage" -> { add(profile, player, Stat.VISION, 1); add(profile, player, Stat.PASSING, 1); }
                    case "Zantetsu Tsurugi" -> { add(profile, player, Stat.ACCELERATION, 1); add(profile, player, Stat.TOP_SPEED, 1); }
                    case "Shoei Barou" -> { add(profile, player, Stat.SHOOTING, 1); add(profile, player, Stat.SHOT_POWER, 1); }
                    case "Ikki Niko" -> { add(profile, player, Stat.SPATIAL_AWARENESS, 1); add(profile, player, Stat.ANTICIPATION, 1); }
                    case "Junichi Wanima", "Keisuke Wanima" -> { add(profile, player, Stat.PASSING, 1); add(profile, player, Stat.DECISION_MAKING, 1); }
                    default -> genericTraining(profile, player);
                }
            }
        }
    }

    private static void genericTraining(PlayerProfile profile, CanonPlayer player) {
        switch (player.role()) {
            case STRIKER -> add(profile, player, Stat.FINISHING, 1);
            case WINGER -> add(profile, player, Stat.ACCELERATION, 1);
            case MIDFIELDER -> add(profile, player, Stat.PASSING, 1);
            case DEFENDER -> add(profile, player, Stat.MARKING, 1);
            case GOALKEEPER -> add(profile, player, Stat.REACTION, 1);
        }
    }

    private static void scriptedMilestones(PlayerProfile profile, CanonPlayer player, int fixtureIndex) {
        // First Selection sequence only; later arcs are deliberately locked for later phases.
        if (fixtureIndex >= 0 && player.name().equals("Hyoma Chigiri")) {
            // His First Selection breakthrough restores use of his speed weapon.
            add(profile, player, Stat.TOP_SPEED, 2); add(profile, player, Stat.ACCELERATION, 2); add(profile, player, Stat.AGILITY, 1);
        }
        if (fixtureIndex >= 2 && player.name().equals("Yoichi Isagi")) {
            add(profile, player, Stat.SPATIAL_AWARENESS, 2); add(profile, player, Stat.VISION, 1); add(profile, player, Stat.POSITIONING, 1);
        }
        if (fixtureIndex >= 2 && player.name().equals("Meguru Bachira")) {
            add(profile, player, Stat.DRIBBLING, 2); add(profile, player, Stat.BALL_CONTROL, 1);
        }
        if (fixtureIndex >= 3 && player.name().equals("Seishiro Nagi")) {
            add(profile, player, Stat.FIRST_TOUCH, 3); add(profile, player, Stat.BALL_CONTROL, 2); add(profile, player, Stat.DECISION_MAKING, 1);
        }
    }

    private static void add(PlayerProfile profile, CanonPlayer player, Stat stat, int amount) {
        if (amount <= 0) return;
        int current = profile.story().canonGrowth(player.name(), stat.name());
        int cap = switch (stat) {
            case TOP_SPEED, ACCELERATION, AGILITY, DRIBBLING, BALL_CONTROL, FIRST_TOUCH, SHOOTING, FINISHING, SHOT_POWER, VISION, SPATIAL_AWARENESS, POSITIONING, DECISION_MAKING -> 12;
            default -> 8;
        };
        profile.story().addCanonGrowth(player.name(), stat.name(), Math.min(amount, Math.max(0, cap - current)));
    }

    public static String summary(PlayerProfile profile, String name) {
        CanonPlayer base = null;
        for (String team : new String[]{"Team Z","Team X","Team Y","Team W","Team V"}) for (CanonPlayer p : FirstSelectionRosters.roster(team)) if (p.name().equals(name)) base = p;
        if (base == null) return name + ": unknown player";
        int matches = profile.story().canonMatches(name);
        int trainings = profile.story().canonTrainings(name);
        CanonPlayer now = developed(profile, base);
        return name + " | matches " + matches + " | training blocks " + trainings + " | overall " + base.stats().values().stream().mapToInt(Integer::intValue).average().orElse(0) + " -> " + now.stats().values().stream().mapToInt(Integer::intValue).average().orElse(0);
    }
}
