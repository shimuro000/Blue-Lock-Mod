# Phase 6 — Character, Progression & Story Core

Target: Minecraft 1.20.1 / Forge 47.4.26 / Java 17.

## Added
- Character archetypes: STRIKER, PLAYMAKER, DRIBBLER, SPEEDSTER, DEFENDER, GOALKEEPER.
- Archetype stat specialization applied at character creation.
- Persistent profile statistics: matches, goals, assists.
- XP rewards from match performance.
- Level progression and Development Points.
- Development Point stat upgrades.
- Persistent StoryProgress attached to the player profile and saved in NBT.
- Story progression state machine covering the planned Season 1 path.
- Commands for creating a profile, inspecting profile data, upgrading stats, and advancing/checking story state.

## Commands
`/bluelock profile create <archetype>`
`/bluelock profile info`
`/bluelock profile upgrade <stat>`
`/bluelock story next`
`/bluelock story state`

## Example
`/bluelock profile create STRIKER`
`/bluelock profile info`
`/bluelock profile upgrade FINISHING`
`/bluelock story next`

## Design direction
The commands are a development/testing interface. They are not intended to be the final player-facing character creation UI. The final UI will be added later with client screens, navigation, previews, confirmation, and story presentation.
