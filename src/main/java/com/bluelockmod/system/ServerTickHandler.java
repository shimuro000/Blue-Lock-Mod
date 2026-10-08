package com.bluelockmod.system;

import com.bluelockmod.football.FootballGameplay;
import com.bluelockmod.effects.FootballEffects;
import com.bluelockmod.ai.FootballAIManager;
import com.bluelockmod.game.MatchManager;
import com.bluelockmod.training.TrainingManager;
import com.bluelockmod.skill.SkillManager;
import com.bluelockmod.ego.EgoFlowManager;
import com.bluelockmod.game.MatchPerformance;
import com.bluelockmod.game.MatchPerformanceManager;
import com.bluelockmod.game.MatchState;
import com.bluelockmod.player.ProfileManager;
import com.bluelockmod.network.EgoFlowSyncPacket;
import com.bluelockmod.network.NetworkHandler;
import com.bluelockmod.vision.VisionManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public final class ServerTickHandler {
    private static ServerTickHandler INSTANCE;
    private final MatchManager matchManager = new MatchManager();
    private final TrainingManager trainingManager = new TrainingManager();
    public ServerTickHandler() { INSTANCE = this; }
    public static ServerTickHandler instance() { return INSTANCE; }
    private long tick;
    @SubscribeEvent public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tick++;
        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                FootballGameplay.tickPlayer(player);
                if (PerformanceScheduler.every(tick, PerformanceScheduler.effectInterval(player))) {
                    FootballEffects.tick(player);
                }
            }
            if (PerformanceScheduler.every(tick, 3)) {
                server.getAllLevels().forEach(level -> FootballAIManager.tickAI(level));
            }
            matchManager.tick();
            trainingManager.tick(server);
            SkillManager.tick();
            EgoFlowManager.tick();
            VisionManager.tick(server);
            if (PerformanceScheduler.every(tick, 5)) {
                com.bluelockmod.game.ScoutingManager.tick();
            }
            if (tick % 5 == 0) syncEgoFlow(server);
        }
    }
    private void syncEgoFlow(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            var profile = ProfileManager.get(player);
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new EgoFlowSyncPacket(profile.ego(), profile.flow(), profile.stamina(),
                    EgoFlowManager.flowTicksRemaining(player), EgoFlowManager.awakeningTicksRemaining(player),
                    EgoFlowManager.flowCooldownRemaining(player), EgoFlowManager.awakeningCooldownRemaining(player),
                    EgoFlowManager.performanceStreak(player)));
        }
    }
    public long tick() { return tick; }
    public MatchManager matchManager() { return matchManager; }
    public TrainingManager trainingManager() { return trainingManager; }
    public void recordMatchAction(ServerPlayer player, MatchPerformance.Action action, boolean successful) {
        var match = matchManager.forPlayer(player.getUUID());
        if (match == null || match.state() != com.bluelockmod.game.MatchState.PLAYING) return;
        boolean clutch = match.ticksRemaining() <= 900 && Math.abs(match.home().score() - match.away().score()) <= 1;
        MatchPerformanceManager.record(player, action, successful, clutch);
        com.bluelockmod.game.ScoutingManager.observe(player, action, successful);
        if (clutch) MatchPerformanceManager.pressure(player);
    }
}
