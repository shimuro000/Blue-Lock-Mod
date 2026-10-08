# Phase 21 — Assets, Effects & Audio

Implemented for Forge 1.20.1 / Java 17.

## Assets
- Original football, jersey, boots, Flow, Awakening and Vision UI/texture assets.
- Existing football and 3D hair assets retained.
- No ripped anime models, textures, videos, or soundtrack.

## Audio
Original generated short OGG cues:
- kick
- pass
- shoot
- goal
- flow
- awakening
- ui_confirm

Registered through Forge SoundEvent registry and `sounds.json`.

## Effects
- Flow: lightweight GLOW particle feedback.
- Awakening: stronger END_ROD feedback.
- Goal: FIREWORK burst + goal sound.
- Pass/shoot/control: distinct action cues.
- Effects are server-triggered and use low particle counts.
- No uncontrolled particle spawning.

## Performance
- Flow/Awakening ambient effects update every 4 player ticks.
- No world-wide scans are introduced.
- Existing server-authoritative gameplay remains authoritative.

## Accessibility / visual safety
- Effects are intentionally subtle compared with full-screen post-processing.
- Existing reduced-complexity approach is preserved; future reduced-effects config can disable these emissions centrally.

## Validation
- Resource paths checked.
- `sounds.json` is valid JSON.
- OGG files are present.
- Java brace audit performed.
- Forge compilation is not claimed unless a real Forge/Gradle environment is available.
