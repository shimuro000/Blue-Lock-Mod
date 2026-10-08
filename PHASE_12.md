# Phase 12 — Ego, Flow & Awakening

Target: Minecraft Java Edition 1.20.1 / Forge 47.4.26 / Java 17.

## Implemented

### Ego
- Ego remains persistent on `PlayerProfile` and is still server-authoritative.
- Successful football actions now feed the psychological progression loop.
- Goals provide a larger immediate Ego/Flow gain.
- Skill activation contributes to the same loop.

### Flow
- Added `EgoFlowManager` as the single runtime owner for Flow/Awakening state.
- Flow activation is server-authoritative through a dedicated packet.
- Default activation requirements:
  - Flow >= 60
  - Ego >= 20
  - no active Flow/Awakening
  - no Flow/Awakening cooldown
- Activation consumes 60 Flow and 20 Ego.
- Duration: 240 ticks (12 seconds).
- Cooldown: 600 ticks (30 seconds).
- Flow effects:
  - +8% movement multiplier
  - +4% football accuracy contribution
  - +0.20 stamina regeneration contribution when not sprinting
- Flow mastery is persistent and grows while successfully playing during Flow.
- Flow activations are persisted for career statistics.

### Awakening
- Awakening is deliberately not another required keybind.
- It can trigger automatically from strong performance while Flow is active.
- Default conditions include:
  - Ego >= 70 after Flow activation cost
  - 3 successful plays in the current performance streak
  - Flow mastery >= 20
  - Awakening cooldown ready
- Duration: 200 ticks (10 seconds).
- Cooldown: 1200 ticks (60 seconds).
- Awakening effects:
  - +16% movement multiplier
  - +8% football accuracy contribution
  - +0.35 stamina regeneration contribution when not sprinting
- Awakening count is persistent.

## Gameplay loop

Successful football action -> Ego/Flow gain -> Flow activation -> stronger play -> sustained performance -> Awakening -> temporary evolution -> cooldown/recovery.

The system intentionally rewards actual football actions instead of passive time or menu interaction.

## Networking

Added:
- `FlowActionPacket` — client sends only an activation request.
- `EgoFlowSyncPacket` — server sends display state to the client HUD.

The server remains authoritative over:
- activation eligibility;
- costs;
- durations;
- cooldowns;
- performance streak;
- all gameplay effects.

## Persistence / migration

`PlayerProfile` schema is now version 5.
New persistent fields:
- `FlowMastery`
- `FlowActivations`
- `AwakeningCount`

Old saves without these fields safely default them to zero.
The player profile capability now implements `INBTSerializable<CompoundTag>` so the profile is actually eligible for Forge capability persistence rather than relying only on clone handling.

Runtime-only state is intentionally not saved:
- active Flow ticks;
- active Awakening ticks;
- cooldown timers;
- current performance streak.

This prevents reconnects/restarts from preserving temporary combat-like buffs.

## Controls

- `H` — Flow
- Existing football and Skill 1–4 controls remain unchanged.
- Awakening has no extra button.

## HUD

The HUD now receives server-synchronized values and displays:
- stamina;
- Ego;
- Flow;
- Flow active state;
- Awakening active state;
- Flow cooldown.

All new user-visible text is localized in English and Arabic.

## Validation

Performed:
- Java source brace-balance check: passed.
- Java source count: 68.
- English localization JSON parse: passed.
- Arabic localization JSON parse: passed.
- Source-reference audit for Phase 12 classes: passed.
- No Gradle/Forge dependency cache was available in the current environment.
- No real Forge/Minecraft compilation is claimed.
