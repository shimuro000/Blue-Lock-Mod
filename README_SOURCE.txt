Blue Lock Mod - Forge 1.20.1 - Phase 3

Implemented in this phase:
- Server-authoritative football action packet.
- Ball ownership/control synchronized through entity data.
- Control, pass, shoot and dribble gameplay actions.
- Profile stamina resource with regeneration/drain.
- Basic stat scaling for passing, finishing, shot power and top speed.
- Client keybinds: F Control, G Pass, R Shoot, V Dribble.
- Client football renderer using the football item model.
- Basic in-game stamina / Ego / Flow HUD.
- Server tick integration for stamina and match ticking.
- Pixel-art football item texture and generated item model.

Known limitations:
- No goalkeeper AI or full opponent AI yet.
- No formal pitch boundaries / goal detection yet.
- No pass target selection; pass follows player look direction.
- No charge-to-shoot UI; current key sends fixed high power.
- No tackle/duel resolution yet.
- MatchManager is still not backed by a persistent world match registry.
- Build was not verified in this environment because Forge Maven may be unreachable.
