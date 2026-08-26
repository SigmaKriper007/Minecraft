# Task 31 — Paradise Island worldgen foundation

## Scope

- Register the first Paradise block palette and creative section.
- Generate one self-contained floating island and Parthenon NBT piece.
- Register locator-compatible Overworld structure/worldgen resources.
- Add deterministic structure loot and all block assets/tags/loot/locales.
- Sample fresh chunks and verify the island remains airborne, complete and within build height.

## Initial palette

- Celestial Stone: pale load-bearing underside stone.
- Paradise Soil / Paradise Grass: living surface strata.
- Paradise Log / Leaves: original sanctuary tree material.
- Parthenon Marble / Gilded Marble: arena structure and readable gold pathing.

## Non-goals for this slice

Angel Boy, wind barriers, fruit growth, wyvern and equipment remain subsequent tasks. Their exact anchors and progression are reserved in `concepts/paradise/MASTER_PLAN.md`; temporary entities or fake boss logic are not introduced.

## Acceptance

- [x] Structure is placed at a fixed sky band with no terrain projection/adaptation.
- [x] NBT and source resource copies match and pass structure-toolkit QA.
- [x] Worldgen JSON and biome/structure tags load without errors.
- [x] Fresh-world sampling finds a structure and confirms air below the rim plus a complete Parthenon.
- [x] Original textures are tileable/readable, item/block models resolve, and all five locales are covered.
- [x] Full build and dedicated startup pass.

## Delivered

- Seven registered Paradise blocks and items: Celestial Stone, Paradise Soil/Grass/Log/Leaves, Parthenon Marble and Gilded Marble.
- A dedicated `Sky Paradise` creative tab, block/item models, original 32px textures, mining/ecology tags, block loot and EN/RU/UK/PL/DE localization.
- A deterministic 97x72x97 single-piece island: 66,272 placed blocks, 5,261 grass surface blocks, 12 sanctuary trees, hanging stone keels, water mirrors and four cardinal approaches.
- A complete open-court Parthenon with a three-tier stylobate, 28-column peristyle, gold navigation inlays, roof galleries, stepped pediments, a lodestone dais and four loot reliquaries.
- A rare locator-compatible Overworld jigsaw structure at absolute sky height 148, restricted to seven temperate/open biomes and registered in `#opusvsexe:opus_structures`.
- A Paradise cache loot table with sky/alchemy materials and no premature final equipment.

## QA evidence

- Toolkit/NBT: `[97, 72, 97]`, palette 15, blocks 66,272; pack/resource SHA-256 `f5c0b86a2f47cbd640a32a132f9b43d410c4c6d1aa878c91636357277ee19375`.
- Dedicated datapack startup loaded 1,276 advancements and reached `Done` without Paradise/worldgen errors.
- Fresh QA world `/locate` found `opusvsexe:paradise_island`; its saved start contained one jigsaw child at `PosY=147`, bounding box `9056,147,10784 -> 9152,218,10880`.
- Runtime block probes confirmed the lodestone center, marble column, gilded pediment, reliquary chest and its `opusvsexe:chests/paradise_island_cache` reference, plus an air gap below the island rim.
- Runtime loot invocation produced two item entities; both were removed. Force-load state was cleared, the ordinary `run/world` remained selected, and the disposable QA world was deleted.
