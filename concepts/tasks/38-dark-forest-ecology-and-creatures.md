# Task 38 — Dark Forest ecology and creatures

Status: complete

## Scope

- Make Gloomwood renewable through an ordinary random-tick/bonemeal sapling with complete clearance/fluid/block-entity rejection and no terrain conversion outside its planting column.
- Replace placeholder vanilla forest-floor flora with biome-owned Moonflower and Thorn Fern patches. Both survive only on Moonlit Grass/Soil; Moonflower is retained as a common future ritual/crafting ingredient, not a boss drop.
- Add three naturally spawning Dark Forest hostiles with original GeckoLib models, animation sets, textures, renderers and scale-matched hitboxes.
- Add spawn eggs, conservative common-material loot and guaranteed archive trophies. No Briarweave recipes or final equipment are exposed in this slice.

## Creature contract

### Shade Spiderling

- `0.68 x 0.38` hitbox, 8 HP, 3 attack, 0.42 movement, 20-block follow range.
- Uses Spider climbing and leap/melee behavior; it is quick pressure with low reach and no hidden ranged attack.
- Low wedge body, articulated eight-leg gait, cyan eyes and restrained moss seams; animations: idle, scuttle, bite.

### Gloom Broodmother

- `2.20 x 1.55` hitbox, 56 HP, 4 armor, 8 attack, 0.27 movement and 0.5 knockback resistance.
- Retains wall climbing. A readable combat cycle chooses a 20-tick web-lane wind-up at medium range or a 16-tick body-slam wind-up at close range; both have recovery and cannot overlap.
- Web Lane creates a bounded row of temporary no-block-damage Gloom Web nodes. Each node hits a target once for 3 damage and Slowness II, then expires.
- Body Slam leaps only after its telegraph and applies one bounded 9-damage impact with knockback on landing/expiry.
- Server death releases exactly six Shade Spiderlings once. A persisted release flag prevents re-entry/reload duplication; child type cannot chain the mechanic.
- Raised armored abdomen and visible spinnerets; animations: idle, walk, bite, web cast, slam wind-up/impact, death release.

### Moonwing Bat

- `1.60 x 0.85` body hitbox, 24 HP, 6 dive damage, 0.42 flying speed and 28-block follow range.
- True flying navigation. It uses explicit acquire/orbit → 12-tick dive telegraph → one-hit dive → 24-tick retreat/recovery instead of contact damage.
- Every third attack cycle emits an expanding Moonwing Pulse entity. The ring is visible, non-griefing and marks each living target once with 2 damage plus 60 ticks of Glowing before expiring.
- Broad crescent wings, large sonar ears and compact body; animations: hover, fly, dive, sonar.

## Spawning and progression

- Natural spawning is restricted to `opusvsexe:dark_forest` and Moonlit Grass/Soil columns, independent of global daytime because the biome's midnight is intentionally local rather than a server-time mutation.
- Weights/groups: Spiderling `70 / 2–4`, Moonwing `24 / 1–2`, Broodmother `7 / 1`.
- Common drops: Shade Silk from spiders and Moonwing Membrane from bats. These are reserved for the later Briarweave slice.
- The central trophy archive expands from 18 to 21 released custom mobs and gains a Dark Forest archive branch.

## Visual source

- Internal silhouette/color concept: `concepts/dark_forest/task38-creature-concept.png`.
- The generated sheet is reference-only. Shipping textures must remain hard-edged Minecraft pixel art with inspected UVs, a restrained Dark Forest palette and no antialiasing.

## Validation contract

- Java 17 main/client compilation and full build.
- Parse every resource JSON; validate model bone/animation-name correspondence and all new PNG dimensions/palette bounds.
- Development dedicated QA must prove exact attributes/hitboxes, valid/invalid spawn floors, sapling clearance and successful growth, temporary effect expiry, web/pulse hit-once behavior, Moonwing aerial movement, and exactly six Spiderlings from one Broodmother death.
- Loot/trophy QA must prove one expected common drop path, one trophy per released species and `doMobLoot=false` suppression without leaving entities/items/blocks/forced chunks behind.
- Client initialization must register all renderers/models before the known host Wayland GLFW boundary.

## Recorded evidence

- Java 17 main/client compilation and the full Gradle build pass with the finished source set.
- All `755` resource JSON files parse. The five new GeckoLib resources resolve as `22/3` Shade Spiderling bones/animations, `25/5` Gloom Broodmother bones/animations, `12/4` Moonwing Bat bones/animations, plus the Web and Pulse effect models. All `13` new gameplay PNGs are RGBA, use the intended `32/64/128px` grids and stay within the locked palette bounds; all five locales contain the new names and descriptions.
- Fresh dedicated QA found natural Dark Forest terrain at `[48, 69, 368]` and sampled `255/18/217` Moonlit Grass/Gloomwood Log/Gloomwood Leaves. Task 38 QA then passed sapling clearance/growth, all three exact stat/hitbox contracts, biome spawn injection, Web/Pulse hit-once and expiry, Moonwing flight, exactly six successful Broodmother children, deterministic common loot, all three trophies and `doMobLoot` suppression.
- The central archive now covers `21` released custom mobs and has a localized Dark Forest branch. The disposable QA world and intermediate crash reports were removed after clean server shutdown.
- Client loading reached registry, renderer and Indigo initialization and LWJGL backend creation with no model, texture, GeckoLib or mixin failure. Window creation cannot be completed unattended in the current IDE/Wayland session, so the smoke client was stopped after that boundary.
