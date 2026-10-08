# Phase 11 — Skills & Abilities

Target: Minecraft Java Edition 1.20.1 + Forge 47.4.26 + Java 17.

## Implemented
- Persistent SkillState inside PlayerProfile, schema v4.
- Four loadout slots.
- Skill Points, awarded on level-up.
- Unlock requirements: level, prerequisites, Skill Points.
- Persistent mastery from 0–100%.
- Server-side cooldowns; cooldowns are intentionally runtime state and do not survive a server restart.
- Client-to-server SkillActionPacket containing only a slot intent.
- Server validation for ownership, loadout, stamina, Ego, cooldown, ball distance/ownership and action-specific state.
- Six skills:
  - direct_shot
  - burst_dribble
  - spatial_read
  - killer_pass
  - body_feint
  - interception_burst
- Default new/legacy profiles receive the first three skills and equip them in slots 1–3.
- Keys 1–4 activate the equipped slots.
- `/bluelock skill list`
- `/bluelock skill unlock <id>`
- `/bluelock skill equip <1-4> <id>`
- `/bluelock skill status`

## Skill behavior
- Direct Shot: stronger, more accurate server-authoritative shot.
- Burst Dribble: controlled-ball acceleration burst.
- Spatial Read: temporary spatial-reading state that improves subsequent pass/shot accuracy.
- Killer Pass: stronger forward pass using the player's look direction when no safe target is available.
- Body Feint: controlled lateral burst while retaining possession.
- Interception Burst: rapid movement toward an uncontrolled/opponent-controlled ball and possible immediate control at close range.

## Persistence
Profile schema 3 data remains readable. Missing Skill Points default safely, and missing SkillState receives the three starter skills. Existing saved progress is not deleted.

## Simplicity
The player only needs keys 1–4. Unlocking and equipping can also be done through commands until the polished Skills UI phase.

## Known boundary
The skill system is now real gameplay, but the full polished Skills menu, visual ability effects, audio, Flow/Ultimate slot, and advanced AI skill usage are intentionally handled by later phases.

## Validation
- Source files checked for duplicate registrations and missing referenced skill classes.
- Localization JSON parsed successfully.
- Archive integrity checked after packaging.
- Forge/Minecraft compilation is not claimed because this environment does not have a usable Forge dependency cache/build setup.
