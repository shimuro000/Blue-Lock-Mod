package com.bluelockmod.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class Team {
    private final UUID id = UUID.randomUUID();
    private final String name;
    private final TeamSide side;
    private Formation formation;
    private final List<UUID> players = new ArrayList<>();
    private int score;
    private int color = 0xFFFFFF;
    private int rating = 70;
    private int chemistry = 70;
    private TeamPlaystyle playstyle = TeamPlaystyle.BALANCED;

    public Team(String name, TeamSide side, Formation formation) { this.name = name; this.side = side; this.formation = formation; }
    public UUID id() { return id; }
    public String name() { return name; }
    public TeamSide side() { return side; }
    public Formation formation() { return formation; }
    public void formation(Formation formation) { this.formation = formation; }
    public List<UUID> players() { return Collections.unmodifiableList(players); }
    public void addPlayer(UUID player) { if (!players.contains(player)) players.add(player); }
    public int score() { return score; }
    public void scoreGoal() { score++; }
    public int color() { return color; }
    public void color(int color) { this.color = color; }
    public int rating() { return rating; }
    public void rating(int rating) { this.rating = Math.max(1, Math.min(99, rating)); }
    public int chemistry() { return chemistry; }
    public void chemistry(int chemistry) { this.chemistry = Math.max(0, Math.min(100, chemistry)); }
    public TeamPlaystyle playstyle() { return playstyle; }
    public void playstyle(TeamPlaystyle playstyle) { this.playstyle = playstyle == null ? TeamPlaystyle.BALANCED : playstyle; }
}
