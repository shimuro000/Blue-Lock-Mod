# Phase 22 — Optimization & Performance

## Goals
Reduce unnecessary server work while preserving server-authoritative football, AI behavior, canon development, scouting, and effects.

## Implemented
- Central `PerformanceScheduler` for deterministic tick budgets.
- Background/debug AI scanning reduced to every 3 server ticks.
- Match tactical AI updates reduced to every 2 match ticks; decision reaction intervals remain difficulty-based.
- Match AI entity lists are cached and refreshed every 10 match ticks instead of rebuilding them every tactical tick.
- Vision scans remain responsive during active matches (5 ticks) and are reduced to 10 ticks outside active matches.
- Scouting aging runs every 5 ticks through the existing manager tick path.
- Flow/Awakening ambient effects are throttled to 6 ticks to reduce particle traffic.
- Logout cleanup now clears scouting and match-performance runtime state in addition to skills/vision/Flow.

## Behavior Guarantees
- No canon character ability is changed or unlocked earlier.
- No client-side authority is introduced.
- Match scoring, ball ownership, skill validation, progression, and development remain server-authoritative.
- Difficulty continues to affect AI reaction/decision behavior rather than granting hidden omniscience.

## Validation
- Java brace audit
- JSON parse validation
- source reference checks
- ZIP integrity
- Forge compilation is not claimed unless Gradle successfully completes it.
