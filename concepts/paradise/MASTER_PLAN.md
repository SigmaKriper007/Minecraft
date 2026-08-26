# Paradise Island / Parthenon — master design

Status: complete and runtime-tested across worldgen, ecology, wyvern, Angel Boy, gear, resurrection and progression integration. This is a main-world progression branch and does not inherit the Fire Realm palette or story.

## 1. World identity

Paradise Island is a rare, enormous floating sanctuary generated high above temperate Overworld biomes. It is readable from the ground as a pale stone mass with hanging roots and a gold-white crown; from above it is a living circular garden organized around a monumental Parthenon.

Visual language:

- primary: warm ivory marble, honey-gold inlays, pale celestial stone;
- living accent: saturated jade grass, turquoise water, white/gold fruit blossoms;
- energy accent: sky cyan -> warm gold -> white hotspot;
- silhouettes: broad terraces, clean colonnades, sweeping wings and curved wind paths;
- no Opus machinery, black industrial plating, Fire Realm basalt, or random rainbow magic.

The island is not a separate dimension. Its rarity, height and locator-compatible structure ID make discovery meaningful while keeping it integrated with the existing world.

## 2. Island composition

- Overall generated footprint: approximately 97x97 blocks at Y 150–221.
- Main landmass: radius 44, 4–23 blocks thick, irregular underside tapering into stalactites and hanging roots.
- Surface: a shallow crowned plateau around Y 179 with raised temple platform.
- Four garden quarters: Paradise trees, flower clearings, small water mirrors and paths.
- Southern processional stair: the safe arrival route from the island rim into the temple.
- Four small shelves/satellite outcrops break the circular silhouette and provide wyvern perches later.
- Falling is a real hazard; low parapets protect only the ceremonial route, not the wild rim.

Worldgen rules:

- Overworld temperate/open biomes only; no oceans, Nether, End or Fire Realm.
- Fixed sky band rather than terrain projection, so the island never becomes a hill.
- `terrain_adaptation: none`; the structure supplies its own complete underside.
- Random-spread spacing targets roughly 1.5–2 km between candidates.
- One deterministic NBT piece, locator-compatible through its structure set.

## 3. Parthenon arena

Footprint: 45x33 blocks on a three-tier marble stylobate.

- Peristyle of 28 columns with gold collars and bright capitals.
- Open central cella/arena, approximately 25x21 clear blocks.
- North and south triangular pediments; shallow stepped roof preserves the classical silhouette.
- Central `angel_dais` position reserved for the sleeping Angel Boy encounter.
- Four side reliquaries carry island loot and later resurrection ingredients.
- Arena boundary is architecture, not an invisible wall. During combat, wind barriers will close column gaps and release after victory/reset.

## 4. Angel Boy reference translation

The supplied concept shows a youthful red-haired humanoid with a gold halo, white draped trousers, gold arm/waist ornaments, a red chest gem and three wing pairs. The model must adapt that identity rather than copy image noise.

Scale and silhouette:

- 1.35x player height; combat hull about 0.9x2.45 blocks.
- Six complete feathered wings: upper pair for lift, broad middle pair for combat silhouette, lower pair for braking/guarding.
- Every pair has left/right root, forewing and feather-fan bones; roots pivot from distinct back heights, so no pair deforms into another.
- Halo is a segmented octagonal ring above/behind the head, not a flat texture.
- White asymmetrical drapery, warm skin, restrained gold bands and a ruby wind-heart at the sternum.

Required rig:

`root -> body -> head/halo`, articulated arms and legs, and six independent wing chains (`upper/middle/lower` x `left/right`) with named VFX anchors at both hands, sternum, halo and wing tips.

Core animations:

- dormant: kneeling/standing still, wings mantled and halo dim;
- awaken_rebuff: zero-damage first-hit guard, all wings flare, attacker launched ~25 blocks;
- idle_ground / idle_flight; walk / flight; hurt; phase transition; death/defeat;
- attacks each own anticipation, strike, impact and recovery animation;
- wing flap cycles offset by pair: broad middle stroke, upper stabilization, lower braking.

## 5. Boss rules and phases

Angel Boy is neutral and damage-immune before awakening. The first attempted hit deals zero damage, names the attacker as the challenger, launches them approximately 25 blocks away with a safe upward arc, seals the arena and begins combat.

Target baseline:

- 420 HP, armor 14, movement 0.34 ground / controlled flight, attack 12;
- boss leash follows the Parthenon, not arbitrary player distance;
- no block griefing and no unavoidable off-island knockback;
- persistence and duplicate guard; resurrection restores the dais only if no living boss exists.

Phases:

1. **The Unmoved Witness (100–70%)** — measured ground/hover duel, long readable pauses.
2. **Sixfold Gale (70–35%)** — permanent flight, faster wing patterns, arena wind lanes.
3. **Judgement of the Open Sky (35–0%)** — halo/ruby overcharge, combined patterns and shorter recovery, but every lethal area remains telegraphed.

Ability kit:

- **Mercy Rebuff**: encounter opener only; gold-white wing flash, radial wind ring, zero boss damage.
- **Halo Lances**: 3/5/7 delayed sky markers followed by narrow vertical light-wind spears.
- **Seraphic Crosswind**: two perpendicular gust walls sweep across the arena with visible safe quadrants.
- **Feather Verdict**: fan of physical feather projectiles that curve once; gaps are deterministic.
- **Wingbeat Cataclysm**: three expanding rings; jump/position timing test, not raw unavoidable damage.
- **Ascension Grip**: localized hurricane captures targets briefly, lifts and throws inward rather than off the island.
- **Ruby Descent**: phase-three dive with a growing ground target, impact core, shockwave and recovery opening.

VFX timing uses telegraph -> impact -> fade. Paradise wind is cyan/gold/white with feather motes; red is reserved for the sternum core and phase-three danger.

## 6. Paradise Wyvern

Role: rare neutral flying mount, visually inspired by Terraria-style wyverns but built as an original long segmented sky serpent with two forewings, four small stabilizer fins and a horned head.

- Tamed only with mature Paradise Fruit from island trees.
- Ownership, sit/follow, persistent saddle state and server-authoritative rider control.
- Flight has capped ascent/descent and stamina-free cruising; it cannot hover perfectly still.
- Right-click/ability input fires one Wind Core while mounted.
- 15-second (300 tick) per-wyvern cooldown is saved and synchronized.

Wind Core impact creates a 6-second hurricane:

- 1 s telegraph ring, 4 s active suction/orbit/lift, 1 s outward release;
- pulls valid living entities toward the center, rotates them, raises them gradually, then scatters them;
- excludes owner, rider, allies and other tamed mounts; no block damage;
- server owns physics/hit membership, client owns wind ribbons/leaf/feather VFX.

## 7. Flora and ambient mobs

- Paradise Tree: pale-gold directional bark, jade/ivory crown and stable `paradise_fruit`; fruit is reserved as the wyvern taming resource.
- Trees now regrow from guarded saplings using random ticks or normal bonemeal; they never perform Fire Realm terrain conversion.
- Implemented fauna set: Sunfinch (small true-flying bird), Cloud Grazer (peaceful highland herbivore) and the tameable/rideable Paradise Wyvern.
- Every registered mob adds its unique trophy to the central archive before release.

## 8. Equipment progression

Intermediate set: **Aerie Bronze**.

- iron-level protection, feather-fall identity and mild knockback resistance;
- crafted from island copper/gold components and common creature drops;
- gates safe exploration, not flight.

Final set: **Parthenon Regalia**.

- diamond-equivalent base defense with intrinsic Protection III behavior;
- full set adds +10 max health (half a row);
- chestplate supplies creative-style flight only while equipped, with deterministic cleanup on removal/death/dimension change;
- full set V creates an aimed hurricane at the looked-at block/entity intercept, not at the player;
- hurricane cooldown is 15 seconds and shares the same server physics implementation as the wyvern.

Tools:

- sword/pickaxe/axe/shovel/hoe use diamond tier and intrinsic Efficiency IV;
- repair material comes from Angel Boy progression/reliquaries;
- all items require survival recipes through the planned Parthenon crafting station.

## 9. Resurrection and loot

- Angel Boy drops a guaranteed boss trophy, Seraphic Pinions and a Ruby Halo Shard.
- A dormant center dais records defeat.
- Resurrection ritual: four Seraphic Pinions on cardinal reliquaries plus one Ruby Halo Shard at the dais; consumes only after duplicate/arena checks pass.
- Island caches provide fruit, Aerie Bronze ingredients and breadcrumbs toward the ritual, never final gear directly.

## 10. Delivery order

1. [x] Island blocks, visuals, structure NBT, worldgen registration and generation QA.
2. [x] Paradise trees/fruit/sapling and ambient spawn foundation.
3. [x] Wyvern model/AI/taming/riding/Wind Core/hurricane.
4. [x] Angel Boy entity/model/six-wing rig/action entities/boss encounter.
5. [x] Aerie Bronze and Parthenon gear, shared hurricane input and flight cleanup.
6. [x] Loot, resurrection, trophies, advancement expansion, locales and integration regression.

Paradise is complete: worldgen, live encounter, mount control, all six wings, gear effects, resurrection and trophy progression now have build/resource/dedicated-runtime evidence. Live multiplayer input and subjective visual/audio feel remain manual acceptance checks, not missing systems.
