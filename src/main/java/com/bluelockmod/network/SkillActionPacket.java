package com.bluelockmod.network;

import com.bluelockmod.skill.SkillManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client sends only an intent; server validates loadout, ownership, cost and cooldown. */
public final class SkillActionPacket {
    private final int slot;
    public SkillActionPacket(int slot) { this.slot = Math.max(0, Math.min(3, slot)); }
    public static void encode(SkillActionPacket msg, FriendlyByteBuf buf) { buf.writeByte(msg.slot); }
    public static SkillActionPacket decode(FriendlyByteBuf buf) { return new SkillActionPacket(buf.readUnsignedByte()); }
    public static void handle(SkillActionPacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) SkillManager.activate(player, msg.slot);
        });
        context.setPacketHandled(true);
    }
}
