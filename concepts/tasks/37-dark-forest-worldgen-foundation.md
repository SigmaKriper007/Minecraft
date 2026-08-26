# Task 37 — Dark Forest worldgen foundation

Status: complete

## Scope

- Register a real rare Overworld `dark_forest` biome through climate-point insertion, retaining ordinary caves, ores and springs.
- Add the first survival block palette: Moonlit Soil/Grass, Gloomwood Log/Leaves, Fountain Stone and an immutable Moon Fountain Core.
- Add a chunk-safe biome feature that converts only eligible surface columns and builds dense, clearance-checked Gloomwood trees without fluids/block entities replacement.
- Add one large original surface-projected Moon Fountain jigsaw structure, restricted to Dark Forest and built as the stable future boss/resurrection arena.
- Blend client sky/fog and celestial angle toward readable midnight only while the local player is inside the biome; never change server day time.
- Provide block/item models, hard-edged 32px textures, loot/tags, a dedicated creative tab and EN/RU/UK/PL/DE localization.

## Validation contract

- Java 17 main/client compilation and full build.
- Parse all JSON; validate PNG dimensions/palette and byte-identical structure NBT copies.
- Dedicated startup must load the biome, placed feature and Moon Fountain without registry/datapack errors.
- Fresh-world locate/sampling must find a natural Dark Forest climate region and an eligible Moon Fountain; sampled biome chunks must contain the custom surface and Gloomwood blocks.
- Client initialization must reach the renderer/mixin layer; the known host Wayland GLFW limitation is acceptable only after mixin application succeeds.
- Remove disposable QA worlds/state and stop every test process.

## Recorded evidence

- Full Java 17 Gradle build completed successfully on 2026-08-26.
- All 721 resource JSON files parsed; all eight foundation PNGs are hard-edged RGBA 32x32 images with bounded palettes, and all five locale files contain the eight new names.
- Both Moon Fountain NBT copies are byte-identical (`09df49ebdd6a94deca25b7aa8bb6964b1338ebcf9eafeb1fba0a253b65ee884f`): size `49x22x49`, 3,917 blocks, exactly one core, 2,467 fountain-stone blocks, 48 logs and 900 leaves.
- Fresh-world dedicated QA found a natural Dark Forest at `[-2704, 63, -368]`; its sampled chunk contained `154` Moonlit Grass, `33` Gloomwood Logs and `395` Gloomwood Leaves. The same startup resolved the biome feature, structure, structure set and template pool.
- `/locate structure opusvsexe:moon_fountain` found an eligible natural start at `[-1088, ~, 4000]` in the disposable world.
- Client loading reached Indigo/render initialization with the Dark Forest mixins applied. Window creation then stopped only at the host's known Wayland GLFW 65548 focus limitation, with no mixin failure.
- Dedicated and client processes stopped cleanly; disposable Task 37 QA worlds were removed after verification.
