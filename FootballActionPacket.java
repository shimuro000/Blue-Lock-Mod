package com.bluelockmod.network;

import com.bluelockmod.football.FootballGameplay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class FootballActionPacket {
    public enum Action { CONTROL, PASS, SHOOT, DRIBBLE }

    private final Action action;
    private final float power;

    public FootballActionPacket(Action action, float power) {
        this.action = action;
        this.power = Math.max(0.0F, Math.min(1.0F, power));
    }

    public static void encode(FootballActionPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.action);
        buf.writeFloat(msg.power);
    }

    public static FootballActionPacket decode(FriendlyByteBuf buf) {
        return new FootballActionPacket(buf.readEnum(Action.class), buf.readFloat());
    }

    public static void handle(FootballActionPacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) FootballGameplay.handleAction(player, msg.action, msg.power);
        });
        context.setPacketHandled(true);
    }
}
