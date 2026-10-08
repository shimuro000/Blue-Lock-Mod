# Phase 23A — Advanced Football Animations

Built on Phase 23.

## Goal
Expand the original action-trigger animation layer into a blended football locomotion system while preserving the vanilla player renderer, custom skin, 3D hair, body appearance, and Phase 22 performance strategy.

## Advanced animation layers
- Idle breathing
- Walk / jog cycle
- Sprint posture and stride
- Strafe movement
- Acceleration / braking lean
- Direction-change counter rotation
- Continuous ball-carry/dribble gait
- Blended control/pass/shoot/skill/Flow/Awakening action animations
- Multi-stage pass and shot wind-up/strike/follow-through curves

## Architecture
The server remains authoritative for discrete football actions and broadcasts compact animation triggers. Continuous locomotion is derived client-side from replicated entity movement, avoiding per-frame network packets.

Both player entities and FootballAIEntity use the same client animation layer. Runtime state is bounded and cleared when the client level disappears or the cache exceeds its safety limit.

## GeckoLib decision
GeckoLib 4.7.x supports Forge 1.20.1, but it is intentionally not introduced in this sub-phase. Replacing Minecraft's PlayerModel with a GeoRenderer would require a larger renderer/skin integration and would risk breaking the existing custom hair, eyes, body-build visuals, and vanilla compatibility. The current procedural layer provides advanced locomotion without adding a hard dependency.

GeckoLib remains a valid future choice for dedicated custom animated models (for example a fully custom character/entity model) if the project later moves beyond the vanilla PlayerModel.

## Validation
- Java source brace audit
- JSON resource validation
- source reference audit
- archive integrity
- no Forge compilation claimed unless Gradle tooling is available and compilation actually succeeds
