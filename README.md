# Redstone Engineering 0.2.0 — Freezer prototype

Minecraft Java 1.21.11 · Fabric · Java 21

## Implemented in this update
- The Freezer now reacts to redstone power.
- When powered, it affects non-player entities in the entire 16×16 chunk containing the Freezer, across the chunk's build height.
- Mob AI is disabled while frozen; entity velocity is repeatedly cleared for other non-player entities.
- When power is removed, mobs frozen by this Freezer have their AI restored.
- If the Freezer block is removed, it attempts to restore tagged mobs in its chunk.
- The chunk itself stays loaded; normal blocks and redstone continue to function.
- All existing machine registrations, models, textures, item models, language entries, and GitHub Actions workflow are preserved.

## Not implemented yet
- Teleporter functionality (fixed-coordinate destination selected as the first mode).
- Compresser functionality.
- Remote/Receiver linking and Engineering Goggles functionality.

## Build with GitHub Actions
1. Extract this ZIP.
2. Copy the contents of `RedstoneEngineering2-main` into the root of your existing GitHub repository, replacing matching files.
3. Commit and push to `main`.
4. Open Actions → Build Redstone Engineering.
5. Download the `RedstoneEngineering-JAR` artifact after the run succeeds.
6. Install `redstoneengineering-0.2.0.jar` in the Fabric 1.21.11 mods folder.

## Test the Freezer
1. Place the Freezer in a chunk containing a few mobs.
2. Apply a lever/redstone signal to the Freezer.
3. Non-player entities in that chunk should stop moving / mobs should stop their AI.
4. Players and redstone machinery should remain unaffected.
5. Turn the signal off; mobs frozen by the Freezer should resume AI.

This source package has not been compiled in this environment. Use the included GitHub Actions build as the compile check before installing it. If the build fails, share the red error log and do not install the JAR.
