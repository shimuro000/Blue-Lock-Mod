package com.bluelockmod.network;

import com.bluelockmod.animation.FootballAnimation;
import com.bluelockmod.client.ClientAnimationManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** Server-authoritative animation trigger. It carries presentation state only. */
public final class AnimationPacket {
    private final UUID entityId;
    private final FootballAnimation animation;
    private final int duration;
    private final float intensity;

    public AnimationPacket(UUID entityId, FootballAnimation animation, int duration, float intensity) {
        this.entityId = entityId;
        this.animation = animation;
        this.duration = Math.max(1, Math.min(80, duration));
        this.intensity = Math.max(0.0F, Math.min(1.5F, intensity));
    }

    public static AnimationPacket forEntity(Entity entity, FootballAnimation animation) {
        return new AnimationPacket(entity.getUUID(), animation, animation.defaultDuration(), animation.defaultIntensity());
    }

    public static void encode(AnimationPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.entityId);
        buf.writeEnum(msg.animation);
        buf.writeVarInt(msg.duration);
        buf.writeFloat(msg.intensity);
    }

    public static AnimationPacket decode(FriendlyByteBuf buf) {
        return new AnimationPacket(buf.readUUID(), buf.readEnum(FootballAnimation.class), buf.readVarInt(), buf.readFloat());
    }

    public static void handle(AnimationPacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientAnimationManager.play(msg.entityId, msg.animation, msg.duration, msg.intensity)));
        context.setPacketHandled(true);
    }
}
