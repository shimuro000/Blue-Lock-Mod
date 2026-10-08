# Phase 8 — Team, Formation & Tactical AI

Target: Minecraft Java Edition 1.20.1 / Forge 47.4.26 / Java 17.

## Implemented

- True quick-match 5v5 composition: player occupies Team Z striker slot; four Team Z AI plus five Rival XI AI.
- Formation-aware positions for 4-3-3, 4-4-2, 4-2-3-1, 3-5-2, 5-3-2 and Custom.
- Team metadata: color, rating, chemistry and playstyle.
- Team playstyles: Balanced, Possession, Pressing, Direct, Counter.
- AI states aligned with the design document: Position, Scan, Support, Receive, Pass, Dribble, Shoot, Press, Mark, Intercept, Recover, Retreat.
- Tactical positioning instead of every AI simply chasing the ball.
- Support runs when a teammate has possession.
- Defensive retreat and marking.
- Contextual pressing and interception.
- Basic goalkeeper positioning behavior.
- AI passing to an evaluated open teammate.
- Role-aware shooting/dribbling decisions.
- Difficulty levels: Easy, Normal, Hard, Elite.
- Difficulty changes reaction/positioning/decision quality rather than merely multiplying attributes.
- Team playstyle influences pressing and passing tendencies.
- AI remains server authoritative.
- AI does not receive omniscient world information; decisions are based on nearby match entities and tactical context.
- Football possession now works for AI entities as well as players.
- Goal attribution now records the actual ball owner when available.
- Quick-match rewards are no longer awarded once per goal and again at match completion; the captain is rewarded once at completion using personal goals.
- AI reset positions now use formation slots instead of collapsing to one coordinate.

## Commands

- `/bluelock match create`
- `/bluelock match start`
- `/bluelock match stop`
- `/bluelock match status`
- `/bluelock match difficulty easy|normal|hard|elite`

## Playability principle

No additional tactical controls are required from the player. The tactical system operates underneath the existing simple controls: Control, Pass, Shoot and Dribble.

## Remaining later-phase work

- Human teammate AI/player roster synchronization in multiplayer.
- Full team menu UI.
- Tactical radar and readable role indicators.
- Advanced passing lanes and off-ball prediction.
- Better goalkeeper save/clearance behavior.
- Match snapshots/network synchronization.
- Full match statistics and rating pipeline.
- Character creation and 3D hair system.
- Story campaign scenes and selection progression.
- Compile verification and final JAR build.
