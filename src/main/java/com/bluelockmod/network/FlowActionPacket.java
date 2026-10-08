package com.bluelockmod.network;

import com.bluelockmod.ego.EgoFlowManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client sends only the request; the server decides whether Flow can activate. */
public final class FlowActionPacket {
    public FlowActionPacket() {}
    public static void encode(FlowActionPacket msg, FriendlyByteBuf buf) {}
    public static FlowActionPacket decode(FriendlyByteBuf buf) { return new FlowActionPacket(); }
    public static void handle(FlowActionPacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) EgoFlowManager.activateFlow(player);
        });
        context.setPacketHandled(true);
    }
}
