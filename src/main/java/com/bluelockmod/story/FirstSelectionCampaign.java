package com.bluelockmod.story;

import com.bluelockmod.player.PlayerProfile;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;

/** Original campaign layer built around a five-team First Selection structure. */
public final class FirstSelectionCampaign {
    public record Fixture(String opponent, int rating, int chemistry, String style, String objective) {}
    private static final List<Fixture> FIXTURES = List.of(
            new Fixture("Team X", 68, 64, "COUNTER", "CREATE_3_CHANCES"),
            new Fixture("Team Y", 70, 67, "BALANCED", "COMPLETE_2_SUCCESSFUL_DRIBBLES"),
            new Fixture("Team W", 73, 71, "DIRECT", "COMPLETE_4_PASSES"),
            new Fixture("Team V", 79, 82, "PRESSING", "SCORE_OR_CREATE"));

    private FirstSelectionCampaign() {}
    public static int fixtureCount() { return FIXTURES.size(); }
    public static Fixture fixture(PlayerProfile profile) {
        int i = Math.max(0, Math.min(FIXTURES.size() - 1, profile.story().firstSelectionMatch()));
        return FIXTURES.get(i);
    }
    public static boolean active(PlayerProfile profile) {
        StoryState s = profile.story().state();
        return s == StoryState.FIRST_SELECTION_INTRO || s == StoryState.FIRST_MATCH || s == StoryState.FIRST_SELECTION_PROGRESS || s == StoryState.FIRST_SELECTION_FINAL;
    }
    public static boolean qualified(PlayerProfile profile) {
        StoryProgress sp = profile.story();
        return sp.firstSelectionPoints() >= 7 || sp.firstSelectionObjectives() >= 3;
    }
    public static void start(ServerPlayer player, PlayerProfile profile) {
        StoryProgress sp = profile.story();
        sp.state(StoryState.FIRST_SELECTION_INTRO);
        sp.firstSelectionMatch(0);
        sp.currentObjective(fixture(profile).objective());
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("First Selection begins. Four matches. Build points, complete your personal objectives, and earn a place in the next stage."));
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(objectiveText(sp.currentObjective())));
    }
    public static void prepareFixture(PlayerProfile profile) {
        StoryProgress sp = profile.story();
        sp.state(sp.firstSelectionMatch() == FIXTURES.size() - 1 ? StoryState.FIRST_SELECTION_FINAL : StoryState.FIRST_MATCH);
        sp.currentObjective(fixture(profile).objective());
    }
    public static void completeFixture(ServerPlayer player, PlayerProfile profile, int homeGoals, int awayGoals, boolean objectiveComplete) {
        StoryProgress sp = profile.story();
        boolean win = homeGoals > awayGoals;
        boolean draw = homeGoals == awayGoals;
        sp.recordFirstSelectionResult(win, draw, homeGoals, objectiveComplete);
        sp.firstSelectionMatch(sp.firstSelectionMatch() + 1);
        if (sp.firstSelectionMatch() >= FIXTURES.size()) {
            if (qualified(profile)) {
                sp.state(StoryState.SECOND_SELECTION_INTRO);
                sp.flag("FIRST_SELECTION_QUALIFIED");
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("First Selection cleared. Your performance earned a place in the next stage."));
            } else {
                sp.state(StoryState.FIRST_SELECTION_PROGRESS);
                sp.flag("FIRST_SELECTION_REQUIRES_RETRY");
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("First Selection not cleared. Improve your points or personal objectives and try the campaign again."));
            }
            return;
        }
        sp.state(StoryState.FIRST_SELECTION_PROGRESS);
        sp.currentObjective(fixture(profile).objective());
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Next opponent: " + fixture(profile).opponent() + " | " + objectiveText(sp.currentObjective())));
    }
    public static String objectiveText(String id) {
        return switch (id) {
            case "CREATE_3_CHANCES" -> "Objective: create 3 dangerous attacking actions.";
            case "COMPLETE_2_SUCCESSFUL_DRIBBLES" -> "Objective: complete 2 successful dribbles.";
            case "COMPLETE_4_PASSES" -> "Objective: complete 4 successful passes.";
            case "SCORE_OR_CREATE" -> "Objective: score a goal or contribute to a decisive attacking play.";
            default -> "Objective: win or deliver a strong individual performance.";
        };
    }
}
