package com.bluelockmod.network;

import com.bluelockmod.vision.VisionManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class VisionTogglePacket {
    public VisionTogglePacket() {}
    public static void encode(VisionTogglePacket packet, FriendlyByteBuf buf) {}
    public static VisionTogglePacket decode(FriendlyByteBuf buf) { return new VisionTogglePacket(); }
    public static void handle(VisionTogglePacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) VisionManager.toggle(player);
        });
        ctx.setPacketHandled(true);
    }
}
