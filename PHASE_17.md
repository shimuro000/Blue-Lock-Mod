# Phase 17 — Canon First Selection Rivals & Player Identity

## Goal
Replace generic Rival XI players in First Selection campaign matches with the named First Selection rosters from the manga/anime and give each relevant player a distinct weapon profile.

## Canon roster basis
The named Team X, Y, W and V rosters are based on First Selection team listings from Blue Lock reference material. Team V is the Nagi/Reo/Zantetsu core; Team W is built around the Wanima brothers' coordination; Team X funnels play toward Barou; Team Y centers on Niko and Okawa. These team identities are also reflected in documented First Selection match results. The official anime site supports the defining weapons of major characters such as Isagi, Bachira, Kunigami, Nagi, Reo and Barou.

## Important stats note
The mod's 25-stat system needs numerical values, but a complete authoritative 25-stat table for every First Selection character is not consistently published. The Phase 17 numeric values are therefore **gameplay ratings calibrated from the characters' demonstrated First Selection abilities**, not claims that every number is an official databook value. Canon names, team membership, and signature weapons are kept distinct from these gameplay numbers.

## Added
- `CanonPlayer`
- `FirstSelectionRosters`
- Named rosters for Teams Z, X, Y, W and V.
- Character names shown above AI players in-world.
- Signature weapon stored on AI actors.
- Character-specific stat profile used by AI movement/decision quality.
- Faster players receive higher movement speed from their profile.
- Better decision/shooting players make better tactical choices without global stat multipliers.
- Rival key-player message at match creation.
- Team W campaign style changed to DIRECT to reflect its pass-and-go identity rather than generic possession.

## Major First Selection identities
- Team Z: Isagi spatial awareness, Bachira dribbling, Kunigami left-foot power, Chigiri speed, Raichi marking/stamina, etc.
- Team X: Barou as the dominant scoring focal point.
- Team Y: Niko's field reading / counter interception structure.
- Team W: Wanima brothers' coordinated pass-and-go play.
- Team V: Nagi trapping, Reo versatility, Zantetsu acceleration.

## AI behavior
The existing server-authoritative tactical AI now consumes the character profile. This affects movement speed and decision/shooting quality, while retaining difficulty, formation, pressure, scouting, and team playstyle systems.

## Validation
- Java brace sanity check passed.
- Resource JSON parsing passed.
- Roster sizes checked for 11 players per team.
- ZIP integrity checked after packaging.
- Forge compilation was not performed because the environment does not contain a Gradle executable/wrapper.
