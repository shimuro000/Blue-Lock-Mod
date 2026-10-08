# Phase 18 — Canon First Selection Behavior & Character Development

## Goals
- Give named First Selection players distinct decision tendencies based on their established weapons.
- Make every NPC improve through matches and between-match training.
- Keep development persistent in the player's StoryProgress save.
- Constrain major development to the First Selection story order so later-arc transformations are not unlocked early.

## Canon-grounded development rules
The official BLUE LOCK anime site describes:
- Isagi's goal-scent/spatial awareness blossoming during First Selection and his later adaptability.
- Bachira growing mentally and technically through the Blue Lock trials.
- Chigiri regaining his speed weapon and ego during First Selection.
- Nagi awakening to a stronger personal drive during the Team V confrontation.
The implementation uses these as directional milestones rather than inventing official numeric databook ratings.

## NPC growth
`CanonDevelopmentManager` stores, per canon character:
- matches played
- training blocks
- per-stat growth deltas

Growth is saved inside `StoryProgress -> CanonDevelopment`, so the campaign survives reloads.

Every First Selection fixture:
1. Records a match for every named player in Teams Z/X/Y/W/V.
2. Gives role-focused match growth.
3. Runs a focused between-match training block for every named player.
4. Applies story-gated milestone growth.

The growth is capped and narrow. It cannot arbitrarily turn a defender into a striker or grant unlimited stats.

## Story-gated milestones
- Chigiri: speed/acceleration/agility breakthrough is reinforced during First Selection.
- Isagi: spatial awareness/vision/positioning emphasis increases late in First Selection.
- Bachira: dribbling/ball-control emphasis increases late in First Selection.
- Nagi: first-touch/control/decision growth is unlocked at the Team V stage.
- Barou, Niko, Reo, Wanima brothers, and other players receive role/weapon-aligned training growth, but later-arc transformations remain locked for later phases.

## AI behavior
- Barou shoots more aggressively and from a longer range.
- Bachira favors dribbling and carries the ball more often.
- Nagi has strong shooting/trapping-oriented decisions.
- Reo and Niko favor passing/decision-making.
- Wanima brothers favor combination passing.

## Data integrity
The canonical names/weapons are retained from Phase 17. Numeric values remain gameplay ratings and are not presented as official numeric canon stats.

## Validation
- Java brace balance checked.
- Resource JSON checked.
- ZIP integrity checked.
- Forge compilation is not claimed unless Gradle/Forge is actually executed.
