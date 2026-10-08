package com.bluelockmod.ai;

import com.bluelockmod.football.FootballEntity;
import com.bluelockmod.game.AITacticalContext;
import com.bluelockmod.game.CanonPlayer;
import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.player.PlayerStats.Stat;
import com.bluelockmod.animation.AnimationNetwork;
import com.bluelockmod.animation.FootballAnimation;
import com.bluelockmod.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class FootballAIManager {
    private FootballAIManager() {}

    public static FootballEntity nearestBall(Entity source, double range) {
        AABB box = source.getBoundingBox().inflate(range);
        return source.level().getEntitiesOfClass(FootballEntity.class, box).stream()
                .min(Comparator.comparingDouble(source::distanceToSqr)).orElse(null);
    }

    public static void tryAIControl(FootballAIEntity ai, FootballEntity ball) {
        if (!ball.isControlled() && ai.distanceTo(ball) <= 1.8D) {
            ball.control(ai.getUUID());
            AnimationNetwork.play(ai, FootballAnimation.CONTROL);
        }
    }

    /** Legacy/global ticking for debug-spawned AI. Match AI is driven by MatchManager with tactical context. */
    public static void tickAI(ServerLevel level) {
        for (FootballAIEntity ai : level.getEntitiesOfClass(FootballAIEntity.class, new AABB(level.getSharedSpawnPos()).inflate(4096))) {
            if (!ai.isAlive() || ai.matchId() != null) continue;
            FootballEntity ball = nearestBall(ai, 4.0D);
            if (ball != null && ball.isControlled() && ball.owner().filter(ai.getUUID()::equals).isPresent()) executeDecision(ai, ball, null);
        }
    }

    public static void tickMatchAI(ServerLevel level, AITacticalContext context) {
        List<FootballAIEntity> all = level.getEntitiesOfClass(FootballAIEntity.class,
                new AABB(context.origin()).inflate(24.0D));
        FootballEntity ball = context.ball();
        if (ball == null) return;
        if (level.getGameTime() % 20L == 0L) com.bluelockmod.game.DynamicCanonAIManager.decay();

        for (FootballAIEntity ai : all) {
            if (!ai.isAlive() || !context.contains(ai.getUUID())) continue;
            ai.reduceDecisionCooldown();
            if (!ball.isControlled() && ai.distanceTo(ball) <= 1.65D) tryAIControl(ai, ball);
            if (ai.tickCount % reactionInterval(ai.difficulty()) != 0) continue;
            updateStateAndPosition(ai, ball, all, context);
            if (ball.isControlled() && ball.owner().filter(ai.getUUID()::equals).isPresent()) {
                executeDecision(ai, ball, context);
            }
        }
    }

    private static int reactionInterval(AIDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 12;
            case NORMAL -> 9;
            case HARD -> 6;
            case ELITE -> 4;
        };
    }

    private static void updateStateAndPosition(FootballAIEntity ai, FootballEntity ball, List<FootballAIEntity> all, AITacticalContext context) {
        Vec3 ownGoal = context.goal(ai.homeTeam());
        Vec3 opponentGoal = context.goal(!ai.homeTeam());
        boolean possession = ball.isControlled();
        boolean teammatePossession = possession && isTeammateOwner(ai, ball, all);
        boolean opponentPossession = possession && !teammatePossession;
        double ballDistance = ai.distanceTo(ball);

        if (opponentPossession) {
            FootballAIEntity owner = ownerAI(ball, all);
            double ownerDistance = owner == null ? ballDistance : ai.distanceTo(owner);
            double stylePressBonus = context.playstyle(ai.homeTeam()) == com.bluelockmod.game.TeamPlaystyle.PRESSING ? 1.5D :
                    (context.playstyle(ai.homeTeam()) == com.bluelockmod.game.TeamPlaystyle.COUNTER ? -0.5D : 0.0D);
            com.bluelockmod.game.ScoutingProfile scouting = com.bluelockmod.game.ScoutingManager.get(context.humanCaptain());
            boolean humanPossession = ball.owner().filter(context.humanCaptain()::equals).isPresent();
            if (humanPossession && scouting != null) {
                if (scouting.repeatedDribble()) stylePressBonus += 0.9D;
                if (scouting.repeatedShot() && ai.role() == AIRole.DEFENDER) stylePressBonus += 0.7D;
                if (scouting.repeatedPass() && ai.role() == AIRole.MIDFIELDER) stylePressBonus += 0.8D;
            }
            if (ai.role() == AIRole.GOALKEEPER) ai.state(AIState.POSITION);
            else if (ownerDistance < 8.0D && ballDistance < pressRange(ai) + stylePressBonus) ai.state(AIState.PRESS);
            else if (owner != null && ai.role() == AIRole.DEFENDER && ownerDistance < 12.0D) ai.state(AIState.MARK);
            else ai.state(AIState.RETREAT);
        } else if (teammatePossession) {
            ai.state(ai.role() == AIRole.STRIKER || ai.role() == AIRole.WINGER ? AIState.SUPPORT : AIState.POSITION);
        } else if (!possession) {
            ai.state(ballDistance < 6.0D ? AIState.INTERCEPT : AIState.SCAN);
        } else {
            ai.state(AIState.POSITION);
        }

        Vec3 target = context.formationPosition(ai);
        switch (ai.state()) {
            case PRESS, INTERCEPT -> target = ball.position();
            case MARK -> {
                FootballAIEntity owner = ownerAI(ball, all);
                if (owner != null) target = owner.position().lerp(ownGoal, 0.20D);
            }
            case SUPPORT -> target = supportTarget(ai, ball, all, context);
            case RETREAT -> target = context.formationPosition(ai).lerp(ownGoal, 0.38D);
            default -> {
                if (teammatePossession) target = context.formationPosition(ai).lerp(ball.position(), 0.18D);
            }
        }
        com.bluelockmod.game.ScoutingProfile scouting = com.bluelockmod.game.ScoutingManager.get(context.humanCaptain());
        if (!ai.homeTeam() && scouting != null && humanPossession(context, ball)) {
            if (scouting.repeatedShot() && ai.role() == AIRole.DEFENDER) {
                Vec3 goalLine = context.goal(false);
                target = target.lerp(goalLine.add(0, 0, scouting.shotLane() >= 4 ? 2.5D : -2.5D), 0.18D);
            }
            if (scouting.repeatedPass() && ai.role() == AIRole.MIDFIELDER) {
                target = target.lerp(ball.position(), 0.14D);
            }
            if (scouting.repeatedDribble() && (ai.role() == AIRole.DEFENDER || ai.role() == AIRole.MIDFIELDER)) {
                target = target.lerp(ball.position(), 0.16D);
            }
        }
        ai.anchor(target);
        ai.getNavigation().moveTo(target.x, target.y, target.z, 0.96D + 0.16D * ai.difficulty().reaction());
        ai.getLookControl().setLookAt(ball, 30F, 30F);
    }

    private static Vec3 supportTarget(FootballAIEntity ai, FootballEntity ball, List<FootballAIEntity> all, AITacticalContext context) {
        Vec3 base = context.formationPosition(ai);
        Vec3 forward = context.goal(!ai.homeTeam()).subtract(context.goal(ai.homeTeam())).normalize();
        double side = ai.role() == AIRole.WINGER ? (ai.homeTeam() ? 1.0D : -1.0D) : 0.0D;
        return base.add(forward.scale(2.4D)).add(0, 0, side * 1.5D);
    }


    private static boolean humanPossession(AITacticalContext context, FootballEntity ball) {
        return ball.owner().filter(context.humanCaptain()::equals).isPresent();
    }

    private static double pressRange(FootballAIEntity ai) {
        return switch (ai.difficulty()) {
            case EASY -> 3.2D;
            case NORMAL -> 4.0D;
            case HARD -> 4.8D;
            case ELITE -> 5.4D;
        };
    }

    private static boolean isTeammateOwner(FootballAIEntity ai, FootballEntity ball, List<FootballAIEntity> all) {
        FootballAIEntity owner = ownerAI(ball, all);
        return owner != null && owner.homeTeam() == ai.homeTeam();
    }

    private static FootballAIEntity ownerAI(FootballEntity ball, List<FootballAIEntity> all) {
        if (ball.owner().isEmpty()) return null;
        for (FootballAIEntity ai : all) if (ai.getUUID().equals(ball.owner().get())) return ai;
        return null;
    }

    private static void executeDecision(FootballAIEntity ai, FootballEntity ball, AITacticalContext context) {
        if (ai.decisionCooldown() > 0) return;
        Vec3 goal = context == null ? ai.anchor() : context.goal(!ai.homeTeam());
        Vec3 toGoal = goal.subtract(ai.position());
        double distance = toGoal.length();
        double decisionRoll = ThreadLocalRandom.current().nextDouble();
        double decisionQuality = ai.difficulty().decision();
        decisionQuality *= (0.88D + ai.characterStat(Stat.DECISION_MAKING) / 500.0D);

        FootballAIEntity passTarget = context == null ? null : bestPassTarget(ai, context, ball);
        double pressure = nearestOpponentDistance(ai, context);
        com.bluelockmod.game.TeamPlaystyle style = context == null ? com.bluelockmod.game.TeamPlaystyle.BALANCED : context.playstyle(ai.homeTeam());
        double passChance = style == com.bluelockmod.game.TeamPlaystyle.POSSESSION ? 0.76D :
                style == com.bluelockmod.game.TeamPlaystyle.DIRECT ? 0.48D : 0.62D;
        boolean isBarou = ai.characterName().equals("Shoei Barou");
        boolean isBachira = ai.characterName().equals("Meguru Bachira");
        boolean isNagi = ai.characterName().equals("Seishiro Nagi");
        boolean isReo = ai.characterName().equals("Reo Mikage");
        boolean isNiko = ai.characterName().equals("Ikki Niko");
        boolean isWanima = ai.characterName().equals("Junichi Wanima") || ai.characterName().equals("Keisuke Wanima");

        if (isBachira) passChance -= 0.12D;
        if (isNiko || isReo || isWanima) passChance += 0.10D;
        java.util.UUID learningKey = context == null ? null : context.humanCaptain();
        double learnedPass = context == null ? 0.0D : com.bluelockmod.game.DynamicCanonAIManager.preference(learningKey, ai.characterName(), com.bluelockmod.game.DynamicCanonAIManager.Action.PASS);
        double learnedShot = context == null ? 0.0D : com.bluelockmod.game.DynamicCanonAIManager.preference(learningKey, ai.characterName(), com.bluelockmod.game.DynamicCanonAIManager.Action.SHOOT);
        double learnedDribble = context == null ? 0.0D : com.bluelockmod.game.DynamicCanonAIManager.preference(learningKey, ai.characterName(), com.bluelockmod.game.DynamicCanonAIManager.Action.DRIBBLE);
        passChance += learnedPass;
        boolean shouldPass = passTarget != null && (pressure < 4.5D || ai.role() != AIRole.STRIKER) && decisionRoll < Math.min(0.92D, passChance * decisionQuality);
        boolean shouldShoot = context != null && (ai.role() == AIRole.STRIKER || ai.role() == AIRole.WINGER)
                && distance < (isBarou ? 16.0D : (isNagi ? 14.0D : (ai.difficulty() == AIDifficulty.EASY ? 10.0D : 13.0D)))
                && decisionRoll > Math.max(0.02D, (isBarou ? 0.08D : (isNagi ? 0.18D : 0.25D)) - learnedShot);

        double shootingQuality = ai.characterStat(Stat.SHOOTING) / 100.0D;
        shouldShoot = shouldShoot && ThreadLocalRandom.current().nextDouble() < (0.72D + shootingQuality * 0.28D);
        if (shouldShoot) {
            kickToward(ball, ai.position(), goal, 1.15D + 0.18D * decisionQuality, 0.10D);
            ai.state(AIState.SHOOT);
            AnimationNetwork.play(ai, FootballAnimation.SHOOT);
            ai.decisionCooldown(10);
            if (learningKey != null) com.bluelockmod.game.DynamicCanonAIManager.observe(learningKey, ai.characterName(), com.bluelockmod.game.DynamicCanonAIManager.Action.SHOOT, distance < (isBarou ? 14.0D : 11.0D));
            return;
        }
        if (shouldPass) {
            kickToward(ball, ai.position(), passTarget.position().add(0, 0.25D, 0), 0.78D + 0.16D * decisionQuality, 0.06D);
            ai.state(AIState.PASS);
            AnimationNetwork.play(ai, FootballAnimation.PASS);
            ai.decisionCooldown(8);
            if (learningKey != null) com.bluelockmod.game.DynamicCanonAIManager.observe(learningKey, ai.characterName(), com.bluelockmod.game.DynamicCanonAIManager.Action.PASS, passTarget.distanceTo(ai) > 2.0D && passTarget.distanceTo(ai) < 14.0D);
            return;
        }

        Vec3 dribbleTarget = toGoal.lengthSqr() > 0.04D ? toGoal.normalize() : ai.getLookAngle().normalize();
        double dribblePower = 0.48D + 0.22D * decisionQuality + learnedDribble;
        if (isBachira) dribblePower += 0.14D;
        if (isNagi) dribblePower += 0.04D;
        ball.kick(dribbleTarget.scale(dribblePower).add(0, 0.05D, 0));
        ai.state(AIState.DRIBBLE);
        AnimationNetwork.play(ai, FootballAnimation.DRIBBLE);
        ai.decisionCooldown(6);
        if (learningKey != null) com.bluelockmod.game.DynamicCanonAIManager.observe(learningKey, ai.characterName(), com.bluelockmod.game.DynamicCanonAIManager.Action.DRIBBLE, pressure > 3.0D);
    }

    private static FootballAIEntity bestPassTarget(FootballAIEntity ai, AITacticalContext context, FootballEntity ball) {
        return context.aiPlayers().stream()
                .filter(other -> other.getUUID() != null && other.isAlive() && other.homeTeam() == ai.homeTeam() && !other.getUUID().equals(ai.getUUID()))
                .filter(other -> ai.distanceTo(other) < 18.0D)
                .max(Comparator.comparingDouble(other -> passScore(ai, other, context)))
                .orElse(null);
    }

    private static double passScore(FootballAIEntity ai, FootballAIEntity target, AITacticalContext context) {
        Vec3 opponentGoal = context.goal(!ai.homeTeam());
        double forward = 1.0D - Math.min(1.0D, target.distanceToSqr(opponentGoal) / 900.0D);
        double separation = Math.min(1.0D, ai.distanceTo(target) / 12.0D);
        double central = 1.0D - Math.min(1.0D, Math.abs(target.getZ() - context.origin().getZ()) / 10.0D);
        return forward * 0.48D + separation * 0.30D + central * 0.22D;
    }

    private static double nearestOpponentDistance(FootballAIEntity ai, AITacticalContext context) {
        if (context == null) return 20.0D;
        return context.aiPlayers().stream()
                .filter(other -> other.isAlive() && other.homeTeam() != ai.homeTeam())
                .mapToDouble(ai::distanceTo).min().orElse(20.0D);
    }

    private static void kickToward(FootballEntity ball, Vec3 from, Vec3 target, double power, double lift) {
        Vec3 direction = target.subtract(from);
        if (direction.lengthSqr() < 0.01D) direction = new Vec3(1, 0, 0);
        ball.kick(direction.normalize().scale(power).add(0, lift, 0));
    }

    public static FootballAIEntity spawn(ServerLevel level, Vec3 pos, AIRole role, boolean home) {
        return spawn(level, pos, role, home, AIDifficulty.NORMAL, null);
    }

    public static FootballAIEntity spawn(ServerLevel level, Vec3 pos, AIRole role, boolean home, AIDifficulty difficulty, java.util.UUID matchId) {
        return spawn(level, pos, role, home, difficulty, matchId, null);
    }

    public static FootballAIEntity spawn(ServerLevel level, Vec3 pos, AIRole role, boolean home, AIDifficulty difficulty, java.util.UUID matchId, CanonPlayer character) {
        FootballAIEntity ai = ModEntities.FOOTBALL_AI.get().create(level);
        if (ai == null) throw new IllegalStateException("Unable to create Blue Lock AI entity");
        ai.moveTo(pos.x, pos.y, pos.z, home ? 0 : 180, 0);
        ai.configure(role, home, pos, difficulty, matchId);
        double speedRating = character == null ? 70.0D : character.stat(Stat.TOP_SPEED);
        ai.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.27D + speedRating / 500.0D + 0.01D * difficulty.reaction());
        ai.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20D);
        ai.setHealth(ai.getMaxHealth());
        if (character != null) ai.character(character);
        level.addFreshEntity(ai);
        return ai;
    }
}
