package com.bluelockmod.game;

import com.bluelockmod.ai.FootballAIEntity;
import com.bluelockmod.football.FootballEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/** Read-only tactical context exposed to the AI layer for one live match. */
public final class AITacticalContext {
    private final BlockPos origin;
    private final FootballEntity ball;
    private final Set<UUID> home;
    private final Set<UUID> away;
    private final Collection<FootballAIEntity> aiPlayers;
    private final Formation homeFormation;
    private final Formation awayFormation;
    private final TeamPlaystyle homeStyle;
    private final TeamPlaystyle awayStyle;
    private final UUID humanCaptain;

    public AITacticalContext(BlockPos origin, FootballEntity ball, Set<UUID> home, Set<UUID> away,
                             Collection<FootballAIEntity> aiPlayers, Formation homeFormation, Formation awayFormation,
                             TeamPlaystyle homeStyle, TeamPlaystyle awayStyle, UUID humanCaptain) {
        this.origin = origin; this.ball = ball; this.home = home; this.away = away; this.aiPlayers = aiPlayers;
        this.homeFormation = homeFormation; this.awayFormation = awayFormation;
        this.homeStyle = homeStyle; this.awayStyle = awayStyle; this.humanCaptain = humanCaptain;
    }
    public BlockPos origin() { return origin; }
    public FootballEntity ball() { return ball; }
    public Collection<FootballAIEntity> aiPlayers() { return aiPlayers; }
    public boolean contains(UUID id) { return home.contains(id) || away.contains(id); }
    public Vec3 goal(boolean homeTeam) { return new Vec3(origin.getX() + (homeTeam ? -18.0D : 18.0D), origin.getY() + 1.0D, origin.getZ()); }
    public Vec3 formationPosition(FootballAIEntity ai) {
        Formation formation = ai.homeTeam() ? homeFormation : awayFormation;
        return formation.position(ai.role(), ai.homeTeam(), origin);
    }
    public TeamPlaystyle playstyle(boolean homeTeam) { return homeTeam ? homeStyle : awayStyle; }
    public UUID humanCaptain() { return humanCaptain; }
}
