package com.bluelockmod.story;

import com.bluelockmod.player.PlayerProfile;
import net.minecraft.server.level.ServerPlayer;

public final class StoryManager {
    private StoryManager() {}

    public static StoryProgress get(PlayerProfile profile) {
        return profile.story();
    }

    public static void advance(ServerPlayer player) {
        PlayerProfile profile = player.getCapability(com.bluelockmod.player.PlayerProfileProvider.CAPABILITY).orElseThrow(IllegalStateException::new);
        StoryProgress progress = profile.story();
        StoryState[] states = StoryState.values();
        int index = progress.state().ordinal();
        if (index < states.length - 1) progress.state(states[index + 1]);
    }

    public static void setState(PlayerProfile profile, StoryState state) { profile.story().state(state); }
}
