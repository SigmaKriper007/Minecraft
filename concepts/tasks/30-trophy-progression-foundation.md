# Task 30 — Trophy and progression foundation

## Goal

Create one extensible server-authoritative trophy system before adding the new-region mobs. The system covers every currently playable custom hostile mob and is expanded as each future mob is registered.

## Decisions

- A trophy is a distinct registered item, not an NBT variant. This makes recipes, translations, item predicates and the final advancement deterministic.
- Every mapped mob drops exactly one trophy on death. Collection is progression, not an extra random grind.
- Trophy drops respect `doMobLoot`; they do not depend on a mob having a JSON loot table.
- The central `EntityType -> trophy Item` map is the only runtime drop authority. Future content adds one item and one binding.
- EXO suits and projectile/effect entities are not mobs and therefore do not receive trophies.
- Retained Ember hostile entity IDs receive compatibility trophies even though their removed item/tab progression is no longer exposed.

## Current archive

- Opus: Haiku 1.5, Haiku-2, Haiku-3, Haiku-4, Haiku-5, Haiku Omega, Drone and Drone+.
- Fire Realm: Fire Slime, Lava Golem and Diablo.
- Compatibility Ember entities: Ember Slime, Obsidian Golem and Flame Demon.

## Visual system

- 32x32 transparent pixel-art medallions.
- Shared dark-metal trophy frame communicates collection membership.
- Each species has a distinct central glyph/silhouette.
- Haiku uses graphite/steel with generation-colored energy; Fire uses basalt/brass/ember; compatibility Ember uses obsidian and magenta-hot accents.
- Lighting is top-left with hard pixel clusters and no antialiasing.

## Advancement structure

- `Chronicles of the Fallen` opens on the first trophy.
- Branch challenges track the complete current Haiku and Fire archives.
- `Every Echo Preserved` requires all registered trophies simultaneously and is the expandable final completion criterion.
- As new mobs ship, their trophy criteria must be added before that content is considered complete.

## Acceptance

- Every current hostile custom mob type has exactly one mapped trophy.
- `doMobLoot=false` suppresses trophy drops.
- All trophy item models, 32px textures and five locales are complete.
- Advancement JSON parses and loads on dedicated startup.
- Runtime death QA confirms exactly one expected trophy.
- Full build and resource validation pass.
