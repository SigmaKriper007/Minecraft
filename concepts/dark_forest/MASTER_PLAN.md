# Dark Forest — master design

Status: complete and runtime-tested across worldgen, ecology, the Mossbound Enderman encounter, equipment, resurrection and progression integration. This is a rare Overworld progression region and is distinct from both Opus machinery and the Fire Realm.

## 1. Region identity

The Dark Forest is a broad, dense woodland occupying cool, humid inland climate pockets. Its canopy reads as an almost continuous black-violet mass from above; below it, pale fungi, moss and moonlit water keep navigation readable.

Visual language:

- primary: near-black violet bark, desaturated plum soil and charcoal fountain stone;
- living accent: deep moss, fern green and pale moonflower ivory;
- energy accent: cold lunar cyan with restrained violet shadow;
- silhouettes: tall crooked trunks, low interlocking crowns, root arches, circular fountain terraces and one central flowering vertical landmark;
- never use Opus metal/cables, Paradise gold-white marble or Fire Realm basalt/red-orange energy.

The sky is a client-rendered local illusion: entering the biome smoothly blends the visible celestial angle toward midnight, reveals the moon/stars and cools the sky/fog, while server time, crops and every other biome remain unchanged. Darkness must remain readable without mandatory Night Vision.

## 2. World composition

- A real `opusvsexe:dark_forest` Overworld biome claims rare cool-humid inland climate cells rather than merely decorating vanilla forest chunks.
- The floor uses Moonlit Grass and Moonlit Soil; dense Gloomwood trees provide the high canopy. Natural caves, ores, springs and ordinary terrain generation remain intact.
- A large `moon_fountain` jigsaw landmark projects to the surface only inside this biome. The structure contains concentric pools, four approaches, root-overgrown ruins and a stable central core reserved for the boss/resurrection state.
- Fountain spacing is sparse enough that one landmark anchors a discovered forest region without filling every chunk. No boss or final loot is added before its dedicated slice.

## 3. Flora and ambient readability

- Gloomwood: crooked directional trunk, layered purple-black crown and green moss seams. Saplings and renewal arrive with the ecology slice.
- Moonlit ground: dark but never crushed to pure black; cool edge highlights distinguish walkable ground from roots.
- Future plants: Moonflower (boss ritual ingredient), pale shelf fungi, hanging moss and thorn fern. Light comes from small controlled highlights, not blanket luminous blocks.

## 4. Four required creatures

1. **Shade Spiderling** — tiny, very fast hostile wall climber; low health, short commitment windows and pack pressure.
2. **Gloom Broodmother** — large wall-climbing spider with web lanes and body-slam telegraphs; death creates exactly six Spiderlings with anti-chain safeguards.
3. **Moonwing Bat** — large aggressive true-flying bat using dive, retreat and sonar-ring patterns rather than permanent contact damage.
4. **Mossbound Enderman** — the sleeping fountain boss: enormous horned, hunched Enderman anatomy, long limbs, moss growth and one living flower emerging from its back.

Every creature requires an original readable GeckoLib hierarchy, scale-matched hitbox, animation set, unique trophy and controlled biome-only spawning.

Task 38 locks the non-boss ecology at the following combat scale: Shade Spiderling `0.68x0.38`, 8 HP and 3 damage; Gloom Broodmother `2.20x1.55`, 56 HP and 8 base damage; Moonwing Bat `1.60x0.85`, 24 HP and 6 dive damage. Broodmother uses telegraphed temporary web lanes and a body slam, then releases exactly six Spiderlings once on death. Moonwing alternates one-hit dives with retreat and every third cycle produces a visible expanding sonar pulse. The three common creatures yield only Shade Silk/Moonwing Membrane plus trophies, reserving their progression value for Briarweave.

## 5. Mossbound Enderman encounter

The boss sleeps beside the fountain and is neutral/damage-immune until struck. The waking hit deals zero damage and triggers a root-and-void awakening before the duel.

Target baseline: 480 HP, armor 12, high teleport mobility, fountain leash and no block griefing.

Phases:

1. **The Sleeping Grove (100–65%)** — deliberate melee reaches, marked teleports and root lanes.
2. **Horned Eclipse (65–30%)** — faster paired teleports, moonwell projectiles and flowering hazards.
3. **The Last Bloom (30–0%)** — the back flower opens into a visible weak-point rhythm; combined patterns remain telegraphed and leave recovery windows.

Planned actions: Antler Sweep, Root Snare, Marked Step, Moonwell Orbs, Bloomfall circles, Echo Double and phase-three Eclipse Rush. Natural Enderman projectile avoidance remains readable: teleport departure and arrival markers replace invisible immunity.

Task 39 locks the physical boss contract at `2.35x5.25`, 480 HP, 12 armor and 14 base attack. The waking hit is always zero damage. Phase thresholds are exact at 65% and 30%; the single back flower opens during bounded phase-three weak-point windows for `1.5x` damage. All seven actions use explicit anticipation and finite hit-once damage, the arena leash is 22 blocks, and no attack changes blocks. A persisted Moon Fountain Core controller owns first spawn and defeat state so loading the fountain cannot duplicate or silently resurrect the boss.

## 6. Equipment progression

Intermediate set: **Briarweave**.

- iron-equivalent defense and durability;
- poison resistance identity plus modest movement through foliage;
- crafted from common spider/bat drops, Gloomwood material and vanilla iron; never grants the final teleport.

Final set: **Dark Forest Vestments**.

- full set grants Night Vision, Haste I, Speed II, Strength I and Dolphin's Grace II-equivalent swimming support;
- V performs a server-validated aimed Enderman teleport with an exact 50-tick / 2.5-second cooldown, safe destination search and no wall clipping;
- deterministic removal/death/dimension cleanup; no retained effects from partial equipment.

Tools use gold-equivalent mining level/speed where specified by recipes, diamond durability, intrinsic Unbreaking I and grant Haste III only while actively held. Their shared palette is purple + moss green + near-black.

Task 40 locks Briarweave to exact iron defense/durability and full-set poison resistance. Vestments use diamond defense/durability plus 2 toughness; their five requested effects are server-owned and removed immediately when the set becomes incomplete. The boss bundle upgrades one full set exactly: four Rootbound Eyes, with the single Moonflower Heart assigned to the chestplate. The V teleport is an authoritative 32-block raycast with collision/fluid/world-border validation and an exact persisted 50-tick cooldown. Dark Forest tools use tier level `0`, speed `12`, durability `1561`, intrinsic Unbreaking I and held Haste III.

## 7. Resurrection and progression

- The boss drops its trophy plus a Moonflower Heart and four Rootbound Eyes.
- After defeat, four Rootbound Eyes return to fountain pedestals and the Moonflower Heart returns to the core.
- Validation is atomic and duplicate-safe; a persisted ritual restores exactly one dormant boss and refunds offerings on late failure.
- Forest caches and mob drops support Briarweave and exploration, never final armor/tools or a complete resurrection bundle.

Task 41 replaces the four reserved fountain sockets with exact cardinal Rootbound Pedestals at `(0,+1,±7)` / `(±7,+1,0)`. After victory, four Eyes plus one Heart start a persisted 100-tick ritual; tick 72 restores exactly one dormant boss. Validation and consumption are atomic, a persisted boss UUID prevents reload-time duplication, and late conflict/spawn failure restores all `4+1` offerings. The localized `Return of the Last Bloom` challenge records success.

## 8. Delivery order

1. [x] Biome, moon-sky presentation, block palette, dense Gloomwood worldgen and Moon Fountain structure.
2. [x] Gloomwood renewal/plants plus Shade Spiderling, Broodmother and Moonwing Bat.
3. [x] Mossbound Enderman model, awakening, phases and attack entities.
4. [x] Briarweave and Dark Forest equipment, held-tool bonuses and aimed teleport.
5. [x] Resurrection, loot/trophies/advancements, five locales and integration regression.

The region is complete: natural biome/fountain generation, all four creatures, exact six-child Broodmother death, boss combat, equipment effects, teleport safety and atomic resurrection now have focused runtime evidence.
