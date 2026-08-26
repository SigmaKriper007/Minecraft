# Task 32 — Paradise ecology foundation

## Scope

- Make the sanctuary tree renewable with a normal-bonemeal sapling and a survival fruit drop.
- Add two original peaceful Paradise species: the airborne Sunfinch and terrestrial Cloud Grazer.
- Restrict natural spawning to Paradise Grass inside eligible island biomes.
- Give both species complete GeckoLib rigs, animations, textures, spawn eggs, localization and archive trophies.

## Design

- **Paradise Fruit:** gold-white hanging fruit with a cyan seed-heart. It is edible now and becomes the wyvern taming item in the mount slice; its stable ID must not change.
- **Sunfinch:** a small jade-and-gold flock bird. It uses true three-dimensional navigation, independent wing bones and calm fruit breeding.
- **Cloud Grazer:** a broad ivory highland herbivore with layered cloud fleece, short gold horns and sturdy legs. Fruit attracts/breeds it.
- Neither mob is hostile or drops progression combat materials. Their trophies extend the central archive as required for every released custom mob.

## Acceptance

- [x] Sapling grows only with sufficient clearance and never replaces fluids/block entities; random ticks and ordinary bonemeal both work.
- [x] Leaves can drop saplings and Paradise Fruit while preserving shears/Silk Touch behavior.
- [x] Both entities summon, move, animate, breed from fruit and serialize normally.
- [x] Natural spawn registration is biome-limited and the spawn predicate requires Paradise Grass.
- [x] Trophy registry, tag and all-current-trophies advancement expand from 14 to 16 entries.
- [x] Models/textures/locales/resources compile; dedicated runtime QA covers growth and both entities.

## Delivered / QA

- `paradise_sapling` uses ordinary bonemeal and guarded 7–9-block crown generation. A stone obstruction kept it intact under accelerated ticks; a dispenser bone-meal pulse grew a log trunk and multi-layer crown after the obstruction was removed.
- Paradise Crown loot retained the leaf under shears. One hundred ordinary runtime samples produced both `paradise_sapling` and `paradise_fruit` (plus the intended low-chance sticks).
- Sunfinch has a 9-bone/13-cube rig, independent wings, true flying navigation/no-gravity control, 6 HP and fruit breeding. Dedicated QA observed its launch from Y=242 to Y=244.68 and subsequent autonomous 3D movement.
- Cloud Grazer has a 10-bone/16-cube rig, four independently animated legs, 18 HP, ground roaming and fruit breeding. Dedicated QA observed autonomous ground movement.
- Both entity types registered in the seven eligible island biomes, but their spawn predicates accept only Paradise Grass. Spawn eggs and five locales resolve.
- Death QA produced exactly one `trophy_sunfinch` and one `trophy_cloud_grazer`; the archive now has 16 bound entity types and a localized Paradise branch.
- QA items/blocks/entities were removed, `randomTickSpeed=3`, saved daytime `10634` and force-load state were restored before shutdown.
