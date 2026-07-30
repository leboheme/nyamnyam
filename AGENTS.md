# Nyam Nyam — Agent Guidance

## Project Overview

Nyam Nyam is a LibGDX Java game with desktop and HTML targets. It is a food-themed, fixed-screen platform-action game in which the player moves, attacks enemies, transforms some of them into candy, and eats the resulting food to clear each level.

The project is an older, mostly complete game codebase. Treat existing behavior as valuable design history. When changing it, preserve the game’s identity and avoid introducing modern architecture merely for its own sake.

The player-facing design reference is [`docs/GAME_DESIGN.md`](docs/GAME_DESIGN.md).

## User Preferences

- Work on one explicitly requested task at a time.
- Prefer understanding and preserving existing behavior before refactoring.
- Follow existing Java, LibGDX, and project naming conventions.
- Keep code and identifiers in English.
- Do not create, delete, rename, or rewrite tests unless explicitly asked.
- Do not commit changes without explicit authorization.
- Do not run the application just to inspect logs; ask the user to run it and provide logs when manual runtime verification is needed.
- Do not assume a library, method, asset, or map object exists. Verify it first.
- Avoid speculative abstractions and unused helper methods.
- When adding player-facing text, use the existing i18n property bundles instead of hardcoded strings.
- The user values the build/learn process; explain important design or architectural choices without overloading the task.

## Build and Verification

Useful commands:

```bash
./gradlew core:compileJava
./gradlew build
```

The project uses Java source compatibility 1.7 and LibGDX. The desktop launcher is under `desktop/`; the shared game logic is under `core/`; the browser target is under `html/`.

Prefer the narrowest relevant compile check after a change. Run the full build when a change crosses modules, affects assets, or changes build configuration.

## High-Level Architecture

### Application and screens

- `CoreGame` is the LibGDX application entry point.
- `ScreenManager` changes between splash, loading, intro, menu, help, store, game, and end screens.
- `AbstractScreen` provides shared screen and asset-lifecycle behavior.
- `MediaManager` owns per-screen asset loading, unloading, music, sounds, fonts, texture atlases, and Tiled maps.
- `ActivityEngine` and `ActivityManager` isolate platform services such as ads, analytics, rewarded video, and rating prompts.

### Gameplay runtime

- `GameScreen` coordinates the gameplay screen, input, UI, pause state, win/lose transitions, and level changes.
- `GameWrapper` is the central gameplay orchestrator. It owns the current map, camera, renderer, main avatar, enemies, food, projectiles, pets, timers, and level-loading behavior.
- `GameStatus` owns campaign state: level, lives, score, temporary upgrades, and game state notifications.
- `GameStatusListener` connects gameplay state changes to the UI and runtime reactions.

Do not move gameplay rules casually into UI classes or platform launchers. Keep platform-independent gameplay in `core`.

### Entities and behavior

- `Entity` is the common runtime base for world objects.
- `Avatar` covers characters and enemies.
- `MainAvatar`, `Enemy`, `Boss`, and `PetAvatar` provide behavior families.
- `Meal` covers ordinary food, fruit, vegetables, candy, air, and boosts.
- `Projectile` and `ProjectileType` define attacks and hostile projectiles.
- `EntityFactory` is the creation boundary and uses LibGDX pools for frequently created entities.
- Entity behavior is represented by state objects and state factories under `entity/state/`.
- `GameWrapperAssistant` handles shared movement, tile collision, and avatar collision assistance.

The existing design uses inheritance, factories, object pooling, and state objects together. Preserve those boundaries unless the requested work specifically calls for a refactor.

## Gameplay Invariants

- The central player loop is `move/jump -> attack -> transform or defeat -> eat -> clear`.
- A transformed enemy becomes candy and must be eaten before the level can finish.
- Bosses have lives and cannot be eaten.
- Normal levels can become more dangerous over time through the hurry state and Dark enemy.
- Air objects are both traversal tools and edible/special-attack objects.
- Boost durations are tracked in lives, generally lasting three lives.
- Losing a life also reduces active temporary upgrade duration.
- Level completion advances persistent progress through `GameStatus` and `DataManager`.
- Progress is stored separately for Nyam, Gluty, and Tfly.

If a requested change conflicts with one of these rules, call out the design impact before implementing it.

## Level and Asset Authoring

Levels are Tiled `.tmx` maps under `assets/data/stages/`. The standard map structure is:

- `ground`: visible tile layer;
- `collision`: hidden collision tile layer;
- `enemies`: enemy object layer;
- `meals`: food and boost object layer;
- `path`: optional left/right polyline paths for guided air.

Map properties such as `bossLevel`, `bossBoost`, `guidedAir`, `specialAir`, `extraAir`, and `automaticAirMode` affect runtime behavior. Object names are parsed into `EnemyType` and `MealType` values, so spelling and enum names are part of the content contract.

When adding or changing a level:

- verify the object layer names and object names against the parser;
- preserve the 16-pixel tile and 32-pixel entity conventions unless intentionally changing the coordinate model;
- check that collision tiles and visible ground agree;
- check boss properties and special-air properties together;
- update localization if the level introduces player-facing text.

Texture atlases, fonts, and sounds are grouped by screen under `assets/data/`. Add new assets to the relevant `MediaManager` group and ensure they are unloaded with that screen.

## Input and State Changes

Keyboard input is processed in both `GameWrapper` and the input/UI layer. Touch controls are implemented through `GamePad`. Preserve the distinction between held actions—movement, jump, attack—and one-shot menu actions such as pause, continue, next, and exit.

When changing gameplay state:

- use `GameStatus.setGameState()` for win, lose, pause, and in-game transitions;
- allow its listeners to update UI and related runtime behavior;
- avoid directly changing screens from individual entities;
- clean pooled entity collections when starting or restarting a level.

## Change Protocol

1. Read the relevant screen, gameplay wrapper, entity/state, and asset/map code before editing.
2. Identify whether the requested behavior belongs in `GameScreen`, `GameWrapper`, `GameStatus`, an entity, a state, or level data.
3. Prefer a data-driven level or enum change when the behavior already has a corresponding content hook.
4. Keep unrelated legacy behavior unchanged.
5. Run `git diff --check` and the narrowest relevant Gradle compile check.
6. Report runtime verification separately when the application was not launched.

## Done Criteria

- The requested behavior is implemented in the existing architectural boundary.
- Existing player-facing rules remain intact unless the task explicitly changes them.
- New user-facing text is localized.
- New assets are loaded by the appropriate `MediaManager` group.
- Tiled map names and properties match the code-side contracts.
- Pooled entities are removed and freed consistently.
- `git diff --check` passes.
- Relevant compilation passes, or any environment/build limitation is clearly reported.
