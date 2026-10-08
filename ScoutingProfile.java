package com.bluelockmod.game;

import com.bluelockmod.player.PlayerProfile;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.player.PlayerStats.Stat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

/** Server-side, match-scoped scouting memory. It observes actions rather than hidden client state. */
public final class ScoutingProfile {
    private int passes;
    private int shots;
    private int dribbles;
    private int tackles;
    private int interceptions;
    private int repeatedShotLane;
    private int repeatedPassLane;
    private int repeatedDribbleDirection;
    private int ageTicks;

    public void observe(ServerPlayer player, MatchPerformance.Action action, boolean successful) {
        ageTicks = 0;
        if (!successful) return;
        switch (action) {
            case PASS -> { passes++; repeatedPassLane = lane(player.getLookAngle()); }
            case SHOT -> { shots++; repeatedShotLane = lane(player.getLookAngle()); }
            case DRIBBLE -> { dribbles++; repeatedDribbleDirection = lane(player.getLookAngle()); }
            case TACKLE -> tackles++;
            case INTERCEPTION -> interceptions++;
            case CONTROL -> { }
        }
    }

    public void tick() {
        ageTicks++;
        if (ageTicks >= 100) {
            ageTicks = 0;
            if (passes > 0) passes--;
            if (shots > 0) shots--;
            if (dribbles > 0) dribbles--;
            if (tackles > 0) tackles--;
            if (interceptions > 0) interceptions--;
        }
    }

    public int passes() { return passes; }
    public int shots() { return shots; }
    public int dribbles() { return dribbles; }
    public int tackles() { return tackles; }
    public int interceptions() { return interceptions; }
    public boolean repeatedDribble() { return dribbles >= 3; }
    public boolean repeatedShot() { return shots >= 3; }
    public boolean repeatedPass() { return passes >= 3; }
    public int shotLane() { return repeatedShotLane; }
    public int passLane() { return repeatedPassLane; }
    public int dribbleLane() { return repeatedDribbleDirection; }

    public String summary(ServerPlayer player) {
        PlayerProfile profile = ProfileManager.get(player);
        String strength = strongest(profile);
        String weakness = weakest(profile);
        String learned = repeatedShot() ? "Repeated shooting lane" : repeatedDribble() ? "Repeated dribble direction" : repeatedPass() ? "Repeated passing lane" : "No strong tendency yet";
        return "Scouting | Strength: " + strength + " | Weakness: " + weakness + " | Learned: " + learned;
    }

    private static String strongest(PlayerProfile p) {
        Stat[] stats = {Stat.SHOOTING, Stat.DRIBBLING, Stat.PASSING, Stat.INTERCEPTION, Stat.VISION};
        Stat best = stats[0];
        for (Stat stat : stats) if (p.stats().get(stat) > p.stats().get(best)) best = stat;
        return label(best);
    }

    private static String weakest(PlayerProfile p) {
        Stat[] stats = {Stat.SHOOTING, Stat.DRIBBLING, Stat.PASSING, Stat.INTERCEPTION, Stat.VISION};
        Stat worst = stats[0];
        for (Stat stat : stats) if (p.stats().get(stat) < p.stats().get(worst)) worst = stat;
        return label(worst);
    }

    private static String label(Stat stat) { return stat.name().toLowerCase(Locale.ROOT).replace('_', ' '); }
    private static int lane(Vec3 look) {
        double angle = Math.atan2(look.z, look.x) + Math.PI;
        return (int)Math.floor(angle / (Math.PI * 2.0D) * 8.0D) & 7;
    }
}
