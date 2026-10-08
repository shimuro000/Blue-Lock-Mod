package com.bluelockmod.ai;

import com.bluelockmod.football.FootballEntity;
import com.bluelockmod.game.CanonPlayer;
import com.bluelockmod.player.PlayerStats.Stat;
import com.bluelockmod.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/** Server-authoritative football AI actor. Tactical decisions are handled by FootballAIManager. */
public class FootballAIEntity extends Zombie {
    private AIRole role = AIRole.MIDFIELDER;
    private AIState state = AIState.POSITION;
    private AIDifficulty difficulty = AIDifficulty.NORMAL;
    private boolean homeTeam;
    private double anchorX, anchorY, anchorZ;
    private double originX, originY, originZ;
    private UUIDHolder matchHolder = new UUIDHolder();
    private int decisionCooldown;
    private String characterName = "Rival Player";
    private String weapon = "";
    private final java.util.EnumMap<Stat,Integer> characterStats = new java.util.EnumMap<>(Stat.class);

    public FootballAIEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(false);
        this.goalSelector.addGoal(1, new FootballMovementGoal(this));
        this.goalSelector.addGoal(2, new FootballBallGoal(this));
    }

    public void configure(AIRole role, boolean homeTeam, Vec3 anchor) {
        configure(role, homeTeam, anchor, AIDifficulty.NORMAL, null);
    }

    public void configure(AIRole role, boolean homeTeam, Vec3 anchor, AIDifficulty difficulty, java.util.UUID matchId) {
        this.role = role;
        this.homeTeam = homeTeam;
        this.anchorX = anchor.x;
        this.anchorY = anchor.y;
        this.anchorZ = anchor.z;
        this.originX = anchor.x;
        this.originY = anchor.y - 1.0D;
        this.originZ = anchor.z;
        this.difficulty = difficulty == null ? AIDifficulty.NORMAL : difficulty;
        this.matchHolder.value = matchId;
    }

    public void setMatchOrigin(BlockPos origin) {
        this.originX = origin.getX(); this.originY = origin.getY(); this.originZ = origin.getZ();
    }
    public Vec3 matchOrigin() { return new Vec3(originX, originY, originZ); }
    public AIRole role() { return role; }
    public boolean homeTeam() { return homeTeam; }
    public Vec3 anchor() { return new Vec3(anchorX, anchorY, anchorZ); }
    public void anchor(Vec3 value) { anchorX = value.x; anchorY = value.y; anchorZ = value.z; }
    public AIDifficulty difficulty() { return difficulty; }
    public void difficulty(AIDifficulty value) { difficulty = value == null ? AIDifficulty.NORMAL : value; }
    public AIState state() { return state; }
    public void state(AIState state) { this.state = state; }
    public int decisionCooldown() { return decisionCooldown; }
    public void decisionCooldown(int ticks) { decisionCooldown = Math.max(0, ticks); }
    public void reduceDecisionCooldown() { if (decisionCooldown > 0) decisionCooldown--; }
    public java.util.UUID matchId() { return matchHolder.value; }
    public String characterName() { return characterName; }
    public String weapon() { return weapon; }
    public int characterStat(Stat stat) { return characterStats.getOrDefault(stat, 40); }
    public void character(CanonPlayer player) {
        this.characterName = player.name(); this.weapon = player.weapon();
        characterStats.clear(); for (Stat s : Stat.values()) characterStats.put(s, player.stat(s));
        this.setCustomName(net.minecraft.network.chat.Component.literal(player.name()));
        this.setCustomNameVisible(true);
    }

    @Override public boolean isSunSensitive() { return false; }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("BLCharacter", characterName);
        tag.putString("BLWeapon", weapon);
        tag.putString("BLRole", role.name());
        tag.putString("BLState", state.name());
        tag.putString("BLDifficulty", difficulty.name());
        tag.putBoolean("BLHome", homeTeam);
        tag.putDouble("BLAnchorX", anchorX); tag.putDouble("BLAnchorY", anchorY); tag.putDouble("BLAnchorZ", anchorZ);
        tag.putDouble("BLOriginX", originX); tag.putDouble("BLOriginY", originY); tag.putDouble("BLOriginZ", originZ);
        if (matchHolder.value != null) tag.putUUID("BLMatch", matchHolder.value);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        characterName = tag.contains("BLCharacter") ? tag.getString("BLCharacter") : "Rival Player";
        weapon = tag.contains("BLWeapon") ? tag.getString("BLWeapon") : "";
        try { role = AIRole.valueOf(tag.getString("BLRole")); } catch (Exception ignored) { role = AIRole.MIDFIELDER; }
        try { state = AIState.valueOf(tag.getString("BLState")); } catch (Exception ignored) { state = AIState.POSITION; }
        try { difficulty = AIDifficulty.valueOf(tag.getString("BLDifficulty")); } catch (Exception ignored) { difficulty = AIDifficulty.NORMAL; }
        homeTeam = tag.getBoolean("BLHome");
        anchorX = tag.getDouble("BLAnchorX"); anchorY = tag.getDouble("BLAnchorY"); anchorZ = tag.getDouble("BLAnchorZ");
        originX = tag.getDouble("BLOriginX"); originY = tag.getDouble("BLOriginY"); originZ = tag.getDouble("BLOriginZ");
        if (tag.hasUUID("BLMatch")) matchHolder.value = tag.getUUID("BLMatch");
    }

    private static final class UUIDHolder { java.util.UUID value; }

    private static final class FootballMovementGoal extends Goal {
        private final FootballAIEntity mob;
        FootballMovementGoal(FootballAIEntity mob) { this.mob = mob; setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() { return !mob.level().isClientSide && mob.matchId() == null && mob.tickCount % 5 == 0; }
        @Override public void tick() {
            Vec3 target = mob.anchor();
            FootballEntity ball = FootballAIManager.nearestBall(mob, 24.0D);
            if (ball != null && !ball.isControlled()) {
                double distance = mob.distanceTo(ball);
                double influence = switch (mob.role) {
                    case GOALKEEPER -> distance < 7 ? 0.45D : 0.12D;
                    case DEFENDER -> distance < 13 ? 0.38D : 0.10D;
                    case MIDFIELDER -> distance < 16 ? 0.58D : 0.16D;
                    case WINGER -> distance < 18 ? 0.48D : 0.13D;
                    case STRIKER -> distance < 18 ? 0.42D : 0.12D;
                } * mob.difficulty.positioning();
                target = target.lerp(ball.position(), Math.min(0.68D, influence));
            }
            mob.getNavigation().moveTo(target.x, target.y, target.z, 0.95D + 0.18D * mob.difficulty.reaction());
        }
    }

    private static final class FootballBallGoal extends Goal {
        private final FootballAIEntity mob;
        FootballBallGoal(FootballAIEntity mob) { this.mob = mob; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() {
            FootballEntity ball = FootballAIManager.nearestBall(mob, 2.0D);
            return ball != null && !ball.isControlled() && mob.matchId() == null;
        }
        @Override public void tick() {
            FootballEntity ball = FootballAIManager.nearestBall(mob, 2.0D);
            if (ball == null) return;
            mob.getLookControl().setLookAt(ball, 30F, 30F);
            if (mob.distanceTo(ball) < 1.65F) FootballAIManager.tryAIControl(mob, ball);
        }
    }
}
