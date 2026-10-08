# Phase 15 — Scouting, Opponent Tendencies & Adaptive AI

## Implemented
- Match-scoped scouting memory for the human captain.
- Observes successful pass, shot, dribble, tackle and interception actions.
- Detects repeated shooting, passing and dribbling tendencies after multiple observations.
- Directional lane sampling from the player's actual look direction.
- Gradual memory decay so old tendencies stop dominating the AI.
- Adaptive opponent behavior:
  - tighter pressure against repeated dribbling;
  - defensive positioning against repeated shooting lanes;
  - midfield pressure against repeated passing patterns.
- AI receives the human captain UUID through `AITacticalContext`; it does not scan the world for hidden information.
- Pre-match opponent scouting message with rating, chemistry and tactical style.
- `/bluelock scouting` to inspect the live discovered tendencies.
- Runtime scouting is server-authoritative and is discarded when the match ends.
- Player save schema marker advanced to v7; no transient scouting data is persisted.
- English/Arabic localization requirements remain unchanged; scouting debug/command text is server feedback and can be localized in the later UI pass.

## Design constraints
- No omniscient AI.
- No full-world scans.
- No client authority over scouting.
- No permanent opponent memory from a single match.
- Adaptation changes decisions/positioning rather than silently multiplying AI stats.

## Validation
- Java brace audit.
- Localization JSON parse.
- Network registration consistency check.
- Source reference audit.
- ZIP integrity check.
- Forge compilation is not claimed unless actually executed.
