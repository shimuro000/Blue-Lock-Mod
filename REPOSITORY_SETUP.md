# Blue Lock Mod — Phase 23 Repository Setup

Source of truth: **Phase 23 Advanced Animations**.

Target:
- Minecraft Java Edition 1.20.1
- Forge 47.4.26
- Java 17
- Project version 0.2.0-alpha

## GitHub Actions

`.github/workflows/build.yml` builds the project on pushes and pull requests to `main`, and also supports manual runs from the Actions tab.

The workflow does **not** compile in the development sandbox. GitHub Actions performs the Gradle build and uploads the resulting `build/libs/*.jar` as the artifact `blue-lock-mod-1.20.1-phase23`.

## Important

This repository package was prepared from the Phase 23 Advanced Animations archive exactly as supplied. No Phase 22 source was used as the implementation base.
