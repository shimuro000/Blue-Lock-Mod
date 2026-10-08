# Phase 19 — Dynamic Canon AI & In-Match Adaptation

## Goals
- Make named First Selection players adapt during a live match without granting future story abilities early.
- Preserve the persistent character-development system from Phase 18.
- Give each AI a short-term memory of its own pass, shot, and dribble attempts.
- Successful patterns become slightly more attractive; poor patterns become slightly less attractive.
- Memory is match-scoped and decays, so AI does not become omniscient or permanently learn from one opponent.

## Canon boundary
The official BLUE LOCK anime site describes Isagi's First Selection spatial-awareness growth and later adaptability, Bachira's mental/technical growth, Chigiri's recovery of his speed weapon during First Selection, and Nagi's awakening after developing passion for soccer. Phase 19 does not unlock later-arc transformations. It only changes moment-to-moment decisions based on events the AI has actually experienced.

## Dynamic memory
`DynamicCanonAIManager` stores runtime memory keyed by the human captain's UUID and character name:
- PASS
- SHOOT
- DRIBBLE
- PRESS
- SUPPORT
- INTERCEPT

It records attempts and approximate immediate success conditions. Preferences are capped at a small range so learning changes behavior rather than replacing canon identity.

Memory decays during the match and is cleared when the match finishes.

## AI effects
- Passing can become slightly more or less attractive after observed results.
- Shooting thresholds can shift slightly after observed results.
- Dribble drive can become slightly stronger or weaker.
- Existing canon behavior remains the base layer: Barou still prefers shooting, Bachira dribbling, Nagi trapping/shooting, Reo/Niko/Wanima passing, etc.

## Persistence
Persistent long-term growth remains in `StoryProgress -> CanonDevelopment` from Phase 18. Dynamic match memory is intentionally NOT persisted.

## Anti-omniscience
- AI only learns from its own executed decisions in the current match.
- No hidden player statistics are revealed.
- No wallhack or global future-state information is added.
- No permanent opponent counter is created by this phase.

## Validation
- Java brace balance checked.
- Resource JSON checked.
- ZIP integrity checked.
- Forge compilation is not claimed unless Gradle/Forge is actually executed.
