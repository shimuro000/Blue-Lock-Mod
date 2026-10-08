package com.bluelockmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

/** Server-to-client HUD snapshot. It contains display data only; gameplay remains server-authoritative. */
public final class EgoFlowSyncPacket {
    private final int ego;
    private final int flow;
    private final int stamina;
    private final int flowTicks;
    private final int awakeningTicks;
    private final int flowCooldown;
    private final int awakeningCooldown;
    private final int streak;

    public EgoFlowSyncPacket(int ego, int flow, int stamina, int flowTicks, int awakeningTicks, int flowCooldown, int awakeningCooldown, int streak) {
        this.ego = ego;
        this.flow = flow;
        this.stamina = stamina;
        this.flowTicks = flowTicks;
        this.awakeningTicks = awakeningTicks;
        this.flowCooldown = flowCooldown;
        this.awakeningCooldown = awakeningCooldown;
        this.streak = streak;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(ego); buf.writeVarInt(flow); buf.writeVarInt(stamina);
        buf.writeVarInt(flowTicks); buf.writeVarInt(awakeningTicks);
        buf.writeVarInt(flowCooldown); buf.writeVarInt(awakeningCooldown); buf.writeVarInt(streak);
    }

    public static EgoFlowSyncPacket decode(FriendlyByteBuf buf) {
        return new EgoFlowSyncPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> com.bluelockmod.client.ClientEgoFlowState.update(ego, flow, stamina, flowTicks, awakeningTicks, flowCooldown, awakeningCooldown, streak)));
        context.setPacketHandled(true);
    }
}
