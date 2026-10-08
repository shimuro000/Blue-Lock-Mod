# Phase 9 — Character Creation & Player Identity

Target: Minecraft Java Edition 1.20.1 + Forge 47.4.26

## Implemented

- Versioned player-profile schema raised from 1 to 2 with safe defaults for old saves.
- Character identity:
  - name;
  - nickname;
  - jersey number;
  - dominant foot (existing system retained).
- Appearance data:
  - 3D hair style;
  - primary hair color;
  - secondary hair color;
  - eye color;
  - body build.
- Personality profile:
  - Competitive;
  - Analytical;
  - Instinctive;
  - Opportunistic;
  - Creative;
  - Relentless.
- Body builds:
  - Lean;
  - Balanced;
  - Athletic;
  - Powerful.
- 3D hair geometry with multiple real model styles:
  - Short;
  - Spiky;
  - Long;
  - Mohawk;
  - Undercut;
  - Wave;
  - Afro.
- Client-side hair and eye rendering attached to the player's head.
- Body-build visual scaling kept subtle so gameplay remains readable and fair.
- Character Creation screen with simple cycle controls instead of a complicated editor.
- P key opens Character Creation.
- Server-authoritative customization packet; clients cannot directly write profile data.
- Appearance persists through player NBT and player cloning/death transitions.
- Profile command namespace now exposes `/bluelock profile customize` as a discoverability hint.

## Design alignment

This phase follows the design document's Character Creation requirements: identity, appearance, football background/archetype, personality and later visual development. The actual football archetype remains separate from cosmetic appearance, so changing hair/body/personality does not silently rewrite the player's football build.

## Deliberate limits

- No complicated color-picker GUI yet; Phase 9 uses a small curated palette.
- No live 3D preview inside the menu yet; the actual player renderer is the source of truth.
- Jersey/boots/cosmetic equipment rendering is reserved for the equipment/visual-identity phase.
- Body build is a visual presentation layer; it does not grant hidden combat advantages.
