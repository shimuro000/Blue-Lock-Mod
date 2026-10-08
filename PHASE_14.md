# Phase 14 — Scouting, Vision & Advanced Spatial Awareness

## Implemented
- Server-authoritative Vision system.
- Vision tiers derived from `VISION` and `SPATIAL_AWARENESS`:
  - Basic: 18 block perception radius.
  - Intermediate: 24 blocks.
  - Advanced: 30 blocks and projected movement.
  - Elite: 36 blocks and larger target set.
- `Spatial Read` temporarily upgrades perception by one tier and exposes projected movement.
- Only line-of-sight targets are transmitted; no wallhack/omniscient information.
- Nearby teammates, rivals, AI footballers, and the football can be identified.
- Higher tiers expose more targets and limited threat classification.
- Advanced/Elite tiers include a short movement projection based on current server velocity.
- Vision snapshots are sent every 5 server ticks while the mode is active; no whole-world scan is performed.
- Client HUD overlay displays level, detected targets, distance, relative bearing, threat status, and projected bearing where available.
- New default key: `B` — Vision toggle. It remains rebindable through Minecraft controls.
- New server-authoritative packets:
  - `VisionTogglePacket`
  - `VisionSnapshotPacket`
- Logout cleanup clears active Vision state.
- English and Arabic localization added.

## Design boundaries
Vision is perception, not automatic aiming or targeting. It does not reveal entities through walls, does not decide actions for the player, and does not give the client authoritative gameplay information.

Vision state is runtime-only by design; the player's underlying Vision and Spatial Awareness stats remain persistent in the existing profile.

## Validation
- Java source count: 78.
- Java brace counts checked.
- English/Arabic localization JSON parsed successfully.
- Packet registration references checked.
- No Forge compilation claimed because the required dependency/tool cache is unavailable in the current environment.
