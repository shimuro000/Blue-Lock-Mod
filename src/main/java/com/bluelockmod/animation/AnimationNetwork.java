package com.bluelockmod.animation;

import com.bluelockmod.network.AnimationPacket;
import com.bluelockmod.network.NetworkHandler;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

/** Small server-side bridge for broadcasting presentation-only animation events. */
public final class AnimationNetwork {
    private AnimationNetwork() {}

    public static void play(Entity entity, FootballAnimation animation) {
        NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                AnimationPacket.forEntity(entity, animation));
    }
}
