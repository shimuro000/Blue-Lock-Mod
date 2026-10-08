# Phase 13 — Match Performance, Confidence, Pressure & Clutch

## Implemented
- Server-side per-match performance tracker.
- Tracks shots, passes, dribbles, tackles, interceptions, controls, goals and failed actions.
- Late-match close-score situations are treated as pressure/clutch windows.
- Individual Match Rating is calculated from contribution, result, clutch actions and mistakes.
- Confidence is updated after the match with bounded changes to avoid runaway snowballing.
- XP reward scales with rating and match impact.
- Rank moves based on result and performance rating.
- MVP recognition for elite individual performances.
- Career counters persisted in PlayerProfile: ratings, best rating, shots, passes, dribbles, tackles, interceptions, wins/draws/losses and MVPs.
- Profile schema migrated from v5 to v6 with safe defaults.
- Existing Ego/Flow/Awakening system remains server-authoritative and now receives richer match-performance context.
- English and Arabic localization added.

## Simple player experience
No extra controls are required. Players simply play football; the match engine evaluates their actions and produces the result at the end.

## Important boundary
The current quick-match captain is always the home Team Z player, so the performance result uses Team Z as the player's team. Multi-human team support can later extend the same tracker per participant.

## Validation
- Java source consistency/brace audit performed after implementation.
- Localization JSON parsed.
- No compiled Forge JAR is claimed unless Gradle compilation is actually available and succeeds.
