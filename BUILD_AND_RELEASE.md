# Building the Blue Lock Mod JAR

Target: Minecraft Java Edition 1.20.1, Forge 47.4.26, Java 17.

## What you need
- Java 17 JDK (not only a JRE).
- Internet access for the first dependency download.
- This project directory.

## Build
From this folder run the Gradle wrapper if present:

`./gradlew build`

On Windows PowerShell:

`./gradlew.bat build`

The release JAR will be in:

`build/libs/blue-lock-mod-0.2.0-alpha.jar`

Copy that JAR into the Minecraft 1.20.1 Forge `mods` folder.

## If the wrapper is missing
Install Gradle 8.x, then run:

`gradle build`

The Gradle configuration downloads Forge 1.20.1-47.4.26 automatically from Forge Maven.

## Runtime
Use Minecraft 1.20.1 with Forge 47.4.26 and Java 17.

Do not rename the JAR. Keep only one copy of the mod in the `mods` directory.

## Phase 5 smoke test
1. Launch a world.
2. Give yourself a football with `/give @s bluelockmod:football`.
3. Use `/bluelock ai spawn 6` to create AI players.
4. Use the Phase 3 controls to control/pass/shoot/dribble.


## Final release requirement
The finished project must produce a real compiled `blue-lock-mod-<version>.jar` for Minecraft 1.20.1 Forge 47.4.26. The JAR is the player-facing deliverable; source ZIPs are development artifacts only.
