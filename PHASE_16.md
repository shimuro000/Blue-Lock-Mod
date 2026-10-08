# Phase 16 — First Selection Campaign

## Goal
Turn the existing football/match systems into a playable original First Selection-style campaign with persistent progress, fixtures, objectives, points, and qualification.

## Campaign structure
The campaign uses four original Team Z fixtures against:
- Team X — Counter
- Team Y — Balanced
- Team W — Possession
- Team V — Pressing

Each opponent has a different rating, chemistry, playstyle, and therefore different tactical pressure through the existing AI system.

## Player objectives
Each fixture assigns one simple objective:
- Create 3 dangerous attacking actions
- Complete 2 successful dribbles
- Complete 4 successful passes
- Score or create a decisive attacking contribution

Objectives are evaluated server-side from successful match actions rather than client claims.

## Qualification
A player clears First Selection by either:
- reaching 7 campaign points, or
- completing at least 3 personal objectives.

Win = 3 points, draw = 1 point, loss = 0 points.

On qualification, the story advances to `SECOND_SELECTION_INTRO`. If the threshold is not reached after the four fixtures, the player remains in `FIRST_SELECTION_PROGRESS` and can retry the campaign.

## Persistent data
`StoryProgress` now stores:
- campaign match index
- points
- wins/draws/losses
- campaign goals
- completed objectives
- current objective
- campaign flags

No separate save system was introduced.

## Commands
- `/bluelock campaign start`
- `/bluelock campaign next`
- `/bluelock campaign status`
- `/bluelock campaign objective`

`campaign start` initializes the campaign. `campaign next` creates the current fixture as a real 5v5 match using the existing match, scouting, performance, and tactical AI systems.

## Integration
Campaign matches use:
- MatchManager
- Team/Formations
- FootballAIManager
- ScoutingManager
- MatchPerformance
- ProgressionService
- existing profile/story persistence

The campaign does not create omniscient AI or hidden stat multipliers. Opponent difficulty comes from the existing tactical systems and opponent configuration.

## Validation
- Java brace sanity check passed.
- Resource JSON parsing passed.
- ZIP integrity passed.
- Forge compilation was not performed in this environment because a Gradle executable/wrapper is not present.
