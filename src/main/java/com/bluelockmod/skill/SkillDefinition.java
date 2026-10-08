package com.bluelockmod.skill;

import java.util.Set;

public record SkillDefinition(
        String id,
        String name,
        SkillType type,
        Set<String> prerequisites,
        int staminaCost,
        int egoCost,
        int cooldownTicks,
        int unlockLevel,
        int skillPointCost) {
    public SkillDefinition {
        prerequisites = Set.copyOf(prerequisites);
        if (staminaCost < 0 || egoCost < 0 || cooldownTicks < 0 || unlockLevel < 1 || skillPointCost < 0)
            throw new IllegalArgumentException("Invalid skill definition");
    }
    public SkillDefinition(String id, String name, SkillType type, Set<String> prerequisites,
                           int staminaCost, int egoCost, int cooldownTicks) {
        this(id, name, type, prerequisites, staminaCost, egoCost, cooldownTicks, 1, 1);
    }
}
