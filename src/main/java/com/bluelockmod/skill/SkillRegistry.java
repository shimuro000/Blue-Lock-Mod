package com.bluelockmod.skill;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SkillRegistry {
    private final Map<String, SkillDefinition> skills = new ConcurrentHashMap<>();
    public SkillRegistry() {
        register(new SkillDefinition("direct_shot", "Direct Shot", SkillType.SHOOTING, Set.of(), 10, 5, 40, 1, 1));
        register(new SkillDefinition("burst_dribble", "Burst Dribble", SkillType.DRIBBLING, Set.of(), 12, 4, 50, 1, 1));
        register(new SkillDefinition("spatial_read", "Spatial Read", SkillType.VISION, Set.of(), 8, 8, 100, 2, 1));
        register(new SkillDefinition("killer_pass", "Killer Pass", SkillType.PASSING, Set.of("spatial_read"), 9, 5, 65, 3, 1));
        register(new SkillDefinition("body_feint", "Body Feint", SkillType.DRIBBLING, Set.of("burst_dribble"), 7, 3, 55, 2, 1));
        register(new SkillDefinition("interception_burst", "Interception Burst", SkillType.DEFENSE, Set.of(), 8, 4, 70, 2, 1));
    }
    public void register(SkillDefinition skill) { skills.putIfAbsent(skill.id(), skill); }
    public SkillDefinition get(String id) { return skills.get(id); }
    public Map<String, SkillDefinition> all() { return Map.copyOf(skills); }
}
