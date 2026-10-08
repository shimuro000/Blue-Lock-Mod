package com.bluelockmod.network;

import com.bluelockmod.BlueLockMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BlueLockMod.MOD_ID, NetworkIds.CHANNEL),
            () -> String.valueOf(NetworkIds.PROTOCOL),
            String.valueOf(NetworkIds.PROTOCOL)::equals,
            String.valueOf(NetworkIds.PROTOCOL)::equals
    );
    private static int index;
    private static boolean registered;
    private NetworkHandler() {}

    public static void register() {
        if (registered) return;
        registered = true;
        CHANNEL.registerMessage(nextId(), FootballActionPacket.class,
                FootballActionPacket::encode, FootballActionPacket::decode, FootballActionPacket::handle);
        CHANNEL.registerMessage(nextId(), CharacterCustomizationPacket.class,
                CharacterCustomizationPacket::encode, CharacterCustomizationPacket::decode, CharacterCustomizationPacket::handle);
        CHANNEL.registerMessage(nextId(), SkillActionPacket.class,
                SkillActionPacket::encode, SkillActionPacket::decode, SkillActionPacket::handle);
        CHANNEL.registerMessage(nextId(), FlowActionPacket.class,
                FlowActionPacket::encode, FlowActionPacket::decode, FlowActionPacket::handle);
        CHANNEL.registerMessage(nextId(), EgoFlowSyncPacket.class,
                EgoFlowSyncPacket::encode, EgoFlowSyncPacket::decode, EgoFlowSyncPacket::handle);
        CHANNEL.registerMessage(nextId(), VisionTogglePacket.class,
                VisionTogglePacket::encode, VisionTogglePacket::decode, VisionTogglePacket::handle);
        CHANNEL.registerMessage(nextId(), VisionSnapshotPacket.class,
                VisionSnapshotPacket::encode, VisionSnapshotPacket::decode, VisionSnapshotPacket::handle);
        CHANNEL.registerMessage(nextId(), AnimationPacket.class,
                AnimationPacket::encode, AnimationPacket::decode, AnimationPacket::handle);
    }

    public static int nextId() { return index++; }
}
