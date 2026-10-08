package com.bluelockmod.game;

import com.bluelockmod.ai.AIDifficulty;
import com.bluelockmod.ai.AIRole;
import com.bluelockmod.ai.FootballAIEntity;
import com.bluelockmod.ai.FootballAIManager;
import com.bluelockmod.football.FootballEntity;
import com.bluelockmod.player.ProgressionService;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.story.FirstSelectionCampaign;
import com.bluelockmod.story.StoryState;
import com.bluelockmod.ego.EgoFlowManager;
import com.bluelockmod.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.bluelockmod.effects.FootballEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MatchManager {
    public static final int DEFAULT_DURATION_TICKS = 3600;
    private final Map<UUID, Match> matches = new ConcurrentHashMap<>();
    private final Map<UUID, MatchContext> contexts = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerMatches = new ConcurrentHashMap<>();

    public Match create(Team home, Team away, int durationTicks) {
        Match match = new Match(home, away, durationTicks);
        matches.put(match.id(), match);
        return match;
    }

    public Match get(UUID id) { return matches.get(id); }
    public Match forPlayer(UUID playerId) {
        UUID matchId = playerMatches.get(playerId);
        return matchId == null ? null : matches.get(matchId);
    }

    public boolean sameTeam(Match match, UUID first, UUID second) {
        if (match == null || first == null || second == null) return false;
        MatchContext ctx = contexts.get(match.id());
        if (ctx == null) return false;
        boolean firstHome = first.equals(ctx.captain) || ctx.homeAI.contains(first);
        boolean secondHome = second.equals(ctx.captain) || ctx.homeAI.contains(second);
        return firstHome == secondHome;
    }

    public Match createCampaignMatch(ServerPlayer captain) {
        PlayerProfile profile = ProfileManager.get(captain);
        FirstSelectionCampaign.Fixture fixture = FirstSelectionCampaign.fixture(profile);
        return createConfiguredMatch(captain, fixture.opponent(), fixture.rating(), fixture.chemistry(), fixture.style(), true);
    }

    private Match createConfiguredMatch(ServerPlayer captain, String awayName, int awayRating, int awayChemistry, String awayStyle, boolean campaign) {
        Match existing = forPlayer(captain.getUUID());
        if (existing != null && existing.state() != MatchState.FINISHED && existing.state() != MatchState.CANCELLED) return existing;

        ServerLevel level = captain.serverLevel();
        BlockPos origin = captain.blockPosition().above();
        MatchArena.build(level, origin);

        Team home = new Team("Team Z", TeamSide.HOME, Formation.F_433);
        Team away = new Team(awayName, TeamSide.AWAY, Formation.F_433);
        home.color(0x1E5BFF); away.color(0xD92B2B);
        home.rating(72); away.rating(awayRating);
        home.chemistry(68); away.chemistry(awayChemistry);
        home.playstyle(TeamPlaystyle.BALANCED); away.playstyle(parseStyle(awayStyle));
        home.addPlayer(captain.getUUID());

        Match match = create(home, away, DEFAULT_DURATION_TICKS);
        MatchContext context = new MatchContext(level, origin, captain.getUUID(), AIDifficulty.NORMAL);
        context.campaign = campaign;
        contexts.put(match.id(), context);
        playerMatches.put(captain.getUUID(), match.id());
        MatchPerformanceManager.start(captain.getUUID());
        ScoutingManager.start(captain.getUUID());
        DynamicCanonAIManager.start(captain.getUUID());

        spawnTeam(level, match, home, true, true);
        spawnTeam(level, match, away, false, false);
        spawnBall(level, context);
        positionHuman(captain, origin, true, home.formation());
        match.state(MatchState.WARMUP);
        captain.sendSystemMessage(Component.literal("Match created: Team Z vs " + awayName + " | 5v5 | First Selection roster"));
        captain.sendSystemMessage(Component.literal("Rival key player: " + FirstSelectionRosters.roster(awayName).get(0).name() + " — " + FirstSelectionRosters.roster(awayName).get(0).weapon()));
        captain.sendSystemMessage(Component.literal("Opponent scouting: Rating " + awayRating + " | Chemistry " + awayChemistry + " | Style: " + awayStyle));
        if (campaign) {
            captain.sendSystemMessage(Component.literal(FirstSelectionCampaign.objectiveText(FirstSelectionCampaign.fixture(ProfileManager.get(captain)).objective())));
        }
        return match;
    }

    private TeamPlaystyle parseStyle(String style) {
        if (style == null) return TeamPlaystyle.BALANCED;
        return switch (style.toUpperCase(Locale.ROOT)) {
            case "PRESSING" -> TeamPlaystyle.PRESSING;
            case "POSSESSION" -> TeamPlaystyle.POSSESSION;
            case "COUNTER" -> TeamPlaystyle.COUNTER;
            case "DIRECT" -> TeamPlaystyle.DIRECT;
            default -> TeamPlaystyle.BALANCED;
        };
    }

    public Match createQuickMatch(ServerPlayer captain) {
        return createConfiguredMatch(captain, "Rival XI", 74, 76, "PRESSING", false);
    }

    public boolean setDifficulty(ServerPlayer player, AIDifficulty difficulty) {
        Match match = forPlayer(player.getUUID());
        MatchContext ctx = match == null ? null : contexts.get(match.id());
        if (ctx == null || difficulty == null) return false;
        ctx.difficulty = difficulty;
        respawnDifficulty(ctx);
        player.sendSystemMessage(Component.literal("AI difficulty set to " + difficulty.name()));
        return true;
    }

    private void respawnDifficulty(MatchContext ctx) {
        for (UUID id : ctx.homeAI) configureDifficulty(ctx.level, id, ctx.difficulty);
        for (UUID id : ctx.awayAI) configureDifficulty(ctx.level, id, ctx.difficulty);
    }

    private void configureDifficulty(ServerLevel level, UUID id, AIDifficulty difficulty) {
        Entity entity = level.getEntity(id);
        if (entity instanceof FootballAIEntity ai) ai.difficulty(difficulty);
    }

    public boolean start(ServerPlayer captain) {
        Match match = forPlayer(captain.getUUID());
        if (match == null || match.state() == MatchState.FINISHED) return false;
        MatchContext ctx = contexts.get(match.id());
        if (ctx == null) return false;
        resetPositions(match, ctx);
        match.state(MatchState.KICKOFF);
        ctx.kickoffTicks = 20;
        captain.sendSystemMessage(Component.literal("Kickoff!"));
        return true;
    }

    public boolean stop(ServerPlayer captain) {
        Match match = forPlayer(captain.getUUID());
        if (match == null) return false;
        match.state(MatchState.CANCELLED);
        finishMatch(match, contexts.get(match.id()));
        return true;
    }

    public void tick() {
        for (Match match : new ArrayList<>(matches.values())) {
            MatchContext ctx = contexts.get(match.id());
            if (ctx != null) tickMatch(match, ctx);
        }
    }

    private void tickMatch(Match match, MatchContext ctx) {
        if (match.state() == MatchState.KICKOFF) {
            if (--ctx.kickoffTicks <= 0) match.state(MatchState.PLAYING);
        }
        if (match.state() == MatchState.GOAL && match.stateTicks() == 1) {
            broadcast(ctx, "GOAL! " + match.home().score() + " - " + match.away().score());
            resetPositions(match, ctx);
        }
        if (match.state() == MatchState.FINISHED || match.state() == MatchState.CANCELLED) {
            finishMatch(match, ctx);
            return;
        }
        if (match.state() == MatchState.PLAYING) {
            enforcePitch(match, ctx);
            checkGoal(match, ctx);
            if (match.elapsedTicks() % 2 == 0) tickTactics(match, ctx);
        }
        match.tick();
    }

    private void tickTactics(Match match, MatchContext ctx) {
        FootballEntity ball = findBall(ctx);
        if (ball == null) return;
        if (ctx.aiCache.isEmpty() || match.elapsedTicks() % 10 == 0) {
            ctx.aiCache.clear();
            for (UUID id : ctx.homeAI) addAI(ctx.level, id, ctx.aiCache);
            for (UUID id : ctx.awayAI) addAI(ctx.level, id, ctx.aiCache);
        }
        List<FootballAIEntity> players = ctx.aiCache;
        AITacticalContext tactical = new AITacticalContext(ctx.origin, ball,
                ctx.homeAI, ctx.awayAI, players,
                match.home().formation(), match.away().formation(),
                match.home().playstyle(), match.away().playstyle(), ctx.captain);
        FootballAIManager.tickMatchAI(ctx.level, tactical);
    }

    private void addAI(ServerLevel level, UUID id, List<FootballAIEntity> list) {
        Entity e = level.getEntity(id);
        if (e instanceof FootballAIEntity ai && ai.isAlive()) list.add(ai);
    }

    private void checkGoal(Match match, MatchContext ctx) {
        FootballEntity ball = findBall(ctx);
        if (ball == null) return;
        double dx = ball.getX() - ctx.origin.getX();
        double dz = Math.abs(ball.getZ() - ctx.origin.getZ());
        if (dz > MatchArena.GOAL_WIDTH + 0.75D) return;
        if (dx > MatchArena.HALF_LENGTH + 0.7D) score(match, ctx, TeamSide.HOME, ball);
        else if (dx < -MatchArena.HALF_LENGTH - 0.7D) score(match, ctx, TeamSide.AWAY, ball);
    }

    private void score(Match match, MatchContext ctx, TeamSide side, FootballEntity ball) {
        UUID scorer = ball.owner().orElse(null);
        match.goal(side, scorer);
        if (scorer != null) {
            ServerPlayer scorerPlayer = ctx.level.getServer().getPlayerList().getPlayer(scorer);
            if (scorerPlayer != null && isInMatch(scorerPlayer, match)) {
                EgoFlowManager.onSuccessfulPlay(scorerPlayer, 6, 10);
                MatchPerformance performance = MatchPerformanceManager.get(scorerPlayer.getUUID());
                if (performance != null) performance.goal();
            }
        }
        ball.clearControl();
        ball.setDeltaMovement(Vec3.ZERO);
        ball.setPos(ctx.origin.getX(), ctx.origin.getY() + 1.2D, ctx.origin.getZ());
        ctx.level.players().stream().filter(p -> isInMatch(p, match))
                .forEach(FootballEffects::goal);
    }

    private void enforcePitch(Match match, MatchContext ctx) {
        FootballEntity ball = findBall(ctx);
        if (ball == null) { spawnBall(ctx.level, ctx); return; }
        double x = Math.max(ctx.origin.getX() - MatchArena.HALF_LENGTH - 2, Math.min(ctx.origin.getX() + MatchArena.HALF_LENGTH + 2, ball.getX()));
        double z = Math.max(ctx.origin.getZ() - MatchArena.HALF_WIDTH - 1, Math.min(ctx.origin.getZ() + MatchArena.HALF_WIDTH + 1, ball.getZ()));
        if (x != ball.getX() || z != ball.getZ()) {
            ball.setPos(x, Math.max(ctx.origin.getY() + 1.05D, ball.getY()), z);
            ball.setDeltaMovement(Vec3.ZERO);
            ball.clearControl();
        }
    }

    private void resetPositions(Match match, MatchContext ctx) {
        for (UUID id : ctx.homeAI) positionAI(ctx.level, id, ctx.origin, true, match.home().formation());
        for (UUID id : ctx.awayAI) positionAI(ctx.level, id, ctx.origin, false, match.away().formation());
        ServerPlayer captain = ctx.level.getServer().getPlayerList().getPlayer(ctx.captain);
        if (captain != null) positionHuman(captain, ctx.origin, true, match.home().formation());
        FootballEntity ball = findBall(ctx);
        if (ball == null) spawnBall(ctx.level, ctx);
        else { ball.clearControl(); ball.setPos(ctx.origin.getX(), ctx.origin.getY() + 1.2D, ctx.origin.getZ()); ball.setDeltaMovement(Vec3.ZERO); }
    }

    private void finishMatch(Match match, MatchContext ctx) {
        if (ctx == null || ctx.finished) return;
        ctx.finished = true;
        ServerPlayer captain = ctx.level.getServer().getPlayerList().getPlayer(ctx.captain);
        if (captain != null) {
            int personalGoals = match.goalsByPlayer(captain.getUUID());
            ProgressionService.awardMatch(captain, personalGoals, 0);
            ScoutingProfile scouting = ScoutingManager.remove(captain.getUUID());
            if (scouting != null) captain.sendSystemMessage(Component.literal(scouting.summary(captain)));
            MatchPerformance performance = MatchPerformanceManager.remove(captain.getUUID());
            if (performance != null) {
                int home = match.home().score();
                int away = match.away().score();
                int teamGoals = home;
                int opponentGoals = away;
                performance.finish(captain, teamGoals, opponentGoals, match.ticksRemaining(), DEFAULT_DURATION_TICKS);
            }
            captain.sendSystemMessage(Component.literal("Match finished: " + match.home().score() + " - " + match.away().score()
                    + " | Your goals: " + personalGoals));
            if (ctx.campaign) {
                boolean objectiveComplete = campaignObjectiveComplete(captain, match, performance);
                int fixtureIndex = Math.max(0, ProfileManager.get(captain).story().firstSelectionMatch());
                boolean homeWin = match.home().score() > match.away().score();
                CanonDevelopmentManager.recordFixtureDevelopment(captain, fixtureIndex, homeWin, performance == null ? 0 : performance.rating(match.home().score(), match.away().score(), match.ticksRemaining(), DEFAULT_DURATION_TICKS));
                FirstSelectionCampaign.completeFixture(captain, ProfileManager.get(captain), match.home().score(), match.away().score(), objectiveComplete);
                captain.sendSystemMessage(Component.literal("First Selection record: " + ProfileManager.get(captain).story().firstSelectionPoints() + " points | Objective " + (objectiveComplete ? "complete" : "incomplete")));
            }
            playerMatches.remove(captain.getUUID());
        }
        cleanup(ctx);
        DynamicCanonAIManager.clear(ctx.captain);
        contexts.remove(match.id());
        matches.remove(match.id());
    }

    private boolean campaignObjectiveComplete(ServerPlayer player, Match match, MatchPerformance performance) {
        if (performance == null) return false;
        String objective = ProfileManager.get(player).story().currentObjective();
        return switch (objective) {
            case "CREATE_3_CHANCES" -> performance.successfulShots() + performance.successfulPasses() >= 3;
            case "COMPLETE_2_SUCCESSFUL_DRIBBLES" -> performance.successfulDribbles() >= 2;
            case "COMPLETE_4_PASSES" -> performance.successfulPasses() >= 4;
            case "SCORE_OR_CREATE" -> performance.goals() > 0 || performance.successfulPasses() >= 4;
            default -> performance.rating(match.home().score(), match.away().score(), match.ticksRemaining(), DEFAULT_DURATION_TICKS) >= 65;
        };
    }

    private void spawnTeam(ServerLevel level, Match match, Team team, boolean home, boolean captainUsesStriker) {
        MatchContext ctx = contexts.get(match.id());
        List<CanonPlayer> roster = FirstSelectionRosters.roster(team.name());
        int spawned = 0;
        PlayerProfile captainProfile = ProfileManager.get(ctx.level.getServer().getPlayerList().getPlayer(ctx.captain));
        for (CanonPlayer basePlayer : roster) {
            if (home && spawned >= 4) break; // captain occupies Team Z's striker slot
            CanonPlayer player = captainProfile == null ? basePlayer : CanonDevelopmentManager.developed(captainProfile, basePlayer);
            AIRole role = player.role();
            Vec3 pos = team.formation().position(role, home, ctx.origin);
            FootballAIEntity ai = FootballAIManager.spawn(level, pos, role, home, ctx.difficulty, match.id(), player);
            ai.setMatchOrigin(ctx.origin);
            team.addPlayer(ai.getUUID());
            (home ? ctx.homeAI : ctx.awayAI).add(ai.getUUID());
            spawned++;
        }
        team.color(home ? 0x1E5BFF : 0xD92B2B);
    }

    private void spawnBall(ServerLevel level, MatchContext ctx) {
        FootballEntity ball = ModEntities.FOOTBALL.get().create(level);
        if (ball == null) return;
        ball.setPos(ctx.origin.getX(), ctx.origin.getY() + 1.2D, ctx.origin.getZ());
        level.addFreshEntity(ball);
        ctx.ball = ball.getUUID();
    }

    private FootballEntity findBall(MatchContext ctx) {
        if (ctx.ball != null) {
            Entity entity = ctx.level.getEntity(ctx.ball);
            if (entity instanceof FootballEntity ball && ball.isAlive()) return ball;
        }
        return ctx.level.getEntitiesOfClass(FootballEntity.class, new AABB(ctx.origin).inflate(24)).stream().findFirst().orElse(null);
    }

    private void cleanup(MatchContext ctx) {
        if (ctx.ball != null) discard(ctx.level, ctx.ball);
        for (UUID id : ctx.homeAI) discard(ctx.level, id);
        for (UUID id : ctx.awayAI) discard(ctx.level, id);
    }

    private void discard(ServerLevel level, UUID id) { Entity entity = level.getEntity(id); if (entity != null) entity.discard(); }

    private void positionHuman(ServerPlayer player, BlockPos origin, boolean home, Formation formation) {
        Vec3 pos = formation.position(AIRole.STRIKER, home, origin);
        player.teleportTo(pos.x, pos.y, pos.z);
        player.setYRot(home ? 0 : 180);
    }

    private void positionAI(ServerLevel level, UUID id, BlockPos origin, boolean home, Formation formation) {
        Entity entity = level.getEntity(id);
        if (!(entity instanceof FootballAIEntity ai)) return;
        Vec3 pos = formation.position(ai.role(), home, origin);
        entity.teleportTo(pos.x, pos.y, pos.z);
        ai.anchor(pos);
    }

    private boolean isInMatch(ServerPlayer player, Match match) { return match.id().equals(playerMatches.get(player.getUUID())); }
    private void broadcast(MatchContext ctx, String text) {
        ctx.level.players().stream().filter(p -> p.distanceToSqr(Vec3.atCenterOf(ctx.origin)) < 80 * 80)
                .forEach(p -> p.sendSystemMessage(Component.literal(text)));
    }

    public static final class MatchContext {
        final ServerLevel level;
        final BlockPos origin;
        final UUID captain;
        final Set<UUID> homeAI = new HashSet<>();
        final Set<UUID> awayAI = new HashSet<>();
        final List<FootballAIEntity> aiCache = new ArrayList<>();
        UUID ball;
        int kickoffTicks;
        boolean finished;
        boolean campaign;
        AIDifficulty difficulty;
        MatchContext(ServerLevel level, BlockPos origin, UUID captain, AIDifficulty difficulty) {
            this.level = level; this.origin = origin; this.captain = captain; this.difficulty = difficulty;
        }
    }
}
