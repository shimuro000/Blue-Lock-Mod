package com.bluelockmod.skill;

import java.util.Arrays;

public final class SkillLoadout {
    private final String[] slots = new String[4];
    public boolean equip(int slot, String skillId) {
        if (slot < 0 || slot >= slots.length) return false;
        slots[slot] = skillId;
        return true;
    }
    public void clear() { Arrays.fill(slots, null); }
    public String get(int slot) { return slot >= 0 && slot < slots.length ? slots[slot] : null; }
    public String[] snapshot() { return Arrays.copyOf(slots, slots.length); }
}
