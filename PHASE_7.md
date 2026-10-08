# Phase 7 — Match Engine & Simple Playable Match

Implemented for Forge 1.20.1:
- compact football pitch generation;
- visible midfield and boundary lines;
- simple goals;
- 5v5 quick-match setup;
- Team Z vs Rival XI;
- kickoff, playing, goal, restart, and finished states;
- match clock;
- goal-line detection;
- automatic kickoff/reset positions;
- match cleanup;
- simple commands: `/bluelock match create`, `start`, `stop`, `status`;
- player remains the captain and controls the football directly;
- existing simple controls remain the intended default.

Design note: this phase deliberately avoids adding complex tactical menus. Formations and tactics exist as data foundations, but the player should be able to start a match with one simple command/UI flow and immediately play.

The edited design document's future 3D hair customization is retained as a Character Creation requirement and is not replaced by a generic skin-only solution.
