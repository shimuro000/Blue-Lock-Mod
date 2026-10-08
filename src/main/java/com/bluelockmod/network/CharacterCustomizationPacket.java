package com.bluelockmod.network;

import com.bluelockmod.player.BodyBuild;
import com.bluelockmod.player.CharacterArchetype;
import com.bluelockmod.player.HairStyle;
import com.bluelockmod.player.Personality;
import com.bluelockmod.player.PlayerProfileProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class CharacterCustomizationPacket {
    private final String name;
    private final String nickname;
    private final int jerseyNumber;
    private final HairStyle hairStyle;
    private final int hairColor;
    private final int secondaryHairColor;
    private final int eyeColor;
    private final BodyBuild bodyBuild;
    private final Personality personality;
    private final CharacterArchetype archetype;
    private final String dominantFoot;

    public CharacterCustomizationPacket(String name, String nickname, int jerseyNumber, HairStyle hairStyle,
                                        int hairColor, int secondaryHairColor, int eyeColor,
                                        BodyBuild bodyBuild, Personality personality, CharacterArchetype archetype, String dominantFoot) {
        this.name = clean(name, "Player", 24);
        this.nickname = clean(nickname, "", 20);
        this.jerseyNumber = Math.max(1, Math.min(99, jerseyNumber));
        this.hairStyle = hairStyle == null ? HairStyle.SHORT : hairStyle;
        this.hairColor = hairColor & 0xFFFFFF;
        this.secondaryHairColor = secondaryHairColor & 0xFFFFFF;
        this.eyeColor = eyeColor & 0xFFFFFF;
        this.bodyBuild = bodyBuild == null ? BodyBuild.BALANCED : bodyBuild;
        this.personality = personality == null ? Personality.COMPETITIVE : personality;
        this.archetype = archetype == null ? CharacterArchetype.STRIKER : archetype;
        this.dominantFoot = "LEFT".equalsIgnoreCase(dominantFoot) ? "LEFT" : "RIGHT";
    }

    private static String clean(String value, String fallback, int max) {
        if (value == null) return fallback;
        String v = value.replaceAll("[\\r\\n\\t]", "").trim();
        return v.isEmpty() ? fallback : v.substring(0, Math.min(max, v.length()));
    }

    public static void encode(CharacterCustomizationPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.name, 24); buf.writeUtf(msg.nickname, 20); buf.writeInt(msg.jerseyNumber);
        buf.writeEnum(msg.hairStyle); buf.writeInt(msg.hairColor); buf.writeInt(msg.secondaryHairColor);
        buf.writeInt(msg.eyeColor); buf.writeEnum(msg.bodyBuild); buf.writeEnum(msg.personality); buf.writeEnum(msg.archetype); buf.writeUtf(msg.dominantFoot, 5);
    }

    public static CharacterCustomizationPacket decode(FriendlyByteBuf buf) {
        return new CharacterCustomizationPacket(buf.readUtf(24), buf.readUtf(20), buf.readInt(),
                buf.readEnum(HairStyle.class), buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readEnum(BodyBuild.class), buf.readEnum(Personality.class), buf.readEnum(CharacterArchetype.class), buf.readUtf(5));
    }

    public static void handle(CharacterCustomizationPacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            player.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(profile -> {
                profile.setName(msg.name); profile.setNickname(msg.nickname); profile.setJerseyNumber(msg.jerseyNumber);
                profile.setHairStyle(msg.hairStyle); profile.setHairColor(msg.hairColor);
                profile.setSecondaryHairColor(msg.secondaryHairColor); profile.setEyeColor(msg.eyeColor);
                profile.setBodyBuild(msg.bodyBuild); profile.setPersonality(msg.personality); profile.setDominantFoot(msg.dominantFoot);
                if (profile.matchesPlayed() == 0 && profile.level() == 1) profile.applyArchetype(msg.archetype);
            });
        });
        context.setPacketHandled(true);
    }
}
