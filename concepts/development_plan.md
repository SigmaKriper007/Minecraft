# Development plan

Updated: 2026-08-26. Preserve registered IDs and the existing `com.opus`, `com.opus.fire`, `com.opus.ember` separation.

## Execution order

1. **Opus/Haiku correctness and progression** — shared damage/fire rules; Haiku-4/5 abilities; Titan miniboss drops/spawns; repeat Omega summon; memory lore; weapon abilities; armor rage/balance.
2. **Fire Realm completion** — audio authority, repeat summon, armor health regression, trident transforms, saplings/world conversion, fire tools, item fire immunity, Breakthrough generation, content/tab cleanup.
3. **Shared content foundations** — reusable boss action state, VFX event/preset layer, trophy registry/criteria, progression crafting infrastructure, structure locator trades.
4. **Paradise Island** — worldgen/Parthenon first, then flora/fauna, wyvern, Angel Boy, gear, resurrection and integration.
5. **Dark Forest** — biome/sky, flora, mobs, fountain/boss, gear, resurrection and progression.
6. **Settlements** — survivor AI/trades/equipment/relations, then Japanese settlement, mobs, weapons and Young Samurai.
7. **Global completion** — recipes/loot/trophies/advancements/translations, HTML documentation, resource audit, dedicated server/client gameplay regression.

## Completed slice: task 20 — Haiku material rules and Titan miniboss

- [x] Move fire/lava immunity and Opus-only damage validation into `HaikuMob`; keep Omega phase multiplier in Omega.
- [x] Add readable deflection feedback without packet/particle spam.
- [x] Add Haiku-4 Resonance Guard: telegraph, radial repulse and temporary defensive window.
- [x] Add Haiku-5 two-action kit: Titan Quake and Amber Rush, server-authoritative cooldowns and synced animation state.
- [x] Add Titan Plate item, entity loot table and Opus armor recipes using plates.
- [x] Add the missing Opus pickaxe/axe/shovel/hoe with recipes, damage tags and matching assets.
- [x] Place Haiku-5 in at least two Opus-related structures while retaining Omega reinforcement summons.
- [x] Let an Altar Heart item restore an activated altar when no Omega is nearby; add survival recipe.
- [x] Build, validate JSON/resources, run dedicated-server smoke test and update status.

## Completed slice: task 21 — unique Opus katana abilities

- [x] Replace the shared radial placeholder with a dedicated GeckoLib slash entity and three synced variants.
- [x] Implement Katana-OP Sword Slay as a 32-block forward cut with exact 50-tick cooldown.
- [x] Implement Refined Katana Air-Slash Hail as twelve radial, hit-once blades.
- [x] Implement Golden Katana Heaven-Earth Rupture as a terrain-following corridor slash with a final vertical blast.
- [x] Add original crescent geometry, animation and three hand-authored pixel palettes; register the fullbright client renderer.
- [x] Localize ability tooltips in EN/RU/UK/PL/DE and validate all language/resource JSON.
- [x] Build and dedicated-server spawn/movement/damage smoke test.

## Completed slice: task 22 — Opus armor rage and balance

- [x] Couple the existing V-key shockwave to a bounded Resonant Rage state.
- [x] Add non-stacking +35% attack speed and temporary Strength IV with deterministic expiry/removal cleanup.
- [x] Preserve baseline Strength III and Health Boost X (+40 health / two added bars).
- [x] Localize key/action feedback in EN/RU/UK/PL/DE.
- [x] Validate JSON, build and current-source dedicated-server startup.

## Completed slice: task 23 — hidden EXO resonance punch

- [x] Add an independent B key and packet without adding a fifth HUD ability slot.
- [x] Validate pilot, energy and 60-tick cooldown in the shared EXO base so all six frames inherit it.
- [x] Add a tier-scaled hit-once `punch_shockwave` effect entity with EXO damage attribution and no block damage.
- [x] Add a dedicated punch animation to standard EXO and EXO+ rigs.
- [x] Build an original blue/white radial GeckoLib model, emissive texture and renderer from the project reference.
- [x] Localize input/cooldown feedback in EN/RU/UK/PL/DE and run build/resource/server damage QA.

## Completed slice: task 24 — Diablo audio and repeat summon

- [x] Convert the supplied `diablo_theme` into a streamed 44.1 kHz Vorbis resource and register it.
- [x] Drive a client-relative, no-attenuation battle loop from server-owned Diablo target state.
- [x] Keep dialogue distance-independent for every player in the Fire Realm and stop battle audio on loss/death/exit.
- [x] Add a four-Essence central Cryoice ritual that rebuilds the seal and refuses duplicate living bosses.
- [x] Make Diablo persistent and immune to distance despawn.
- [x] Localize ritual/theme feedback in EN/RU/UK/PL/DE; validate audio/JSON/build/startup/entity persistence.

## Completed slice: task 25 — Fire gear foundation

- [x] Stop Fire/Ember armor from replacing Health Boost every ten ticks; refresh only on equip or near expiry and fill the newly granted health once.
- [x] Give all Fire and Ember registry items vanilla fire/lava immunity.
- [x] Add Vein Fire sword/pickaxe/axe/shovel/hoe with diamond harvest level, Fire Essence repair and survival recipes.
- [x] Make Fire Aspect II intrinsic to the sword and use recipe-driven ore smelting for the pickaxe, deferred until its fresh drops exist.
- [x] Add original 32×32 textures, handheld models, creative/tag integration and EN/RU/UK/PL/DE names/tooltips.
- [x] Validate all JSON/PNG resources, run a full build, dedicated recipe/tag startup and a fire-item versus dirt lava control.

## Completed slice: task 26 — Four Veins armor migration

- [x] Remove the Ember Depths tab and all Ember item registrations except the four stable armor IDs.
- [x] Move retained Ember armor into The Four Veins creative/progression line.
- [x] Compatibility-migrate former Fire armor IDs into Vein Crust with iron defense/durability and intrinsic Protection I.
- [x] Restrict the Vein Crust full-set bonus to Fire Resistance and remove its duplicate wings/flight/fireball behavior.
- [x] Craft Vein Crust from Magma Crust and upgrade matching pieces into Ember armor with Vein Essence.
- [x] Redesign Vein Crust inventory/3D helmet visuals; clean-build, JSON and existing-world dedicated startup QA.

## Completed slice: task 27 — Breakthrough Nether worldgen

- [x] Register a custom Breakthrough feature and inject its placed feature into all Nether biomes.
- [x] Sample the full Nether height with moderate 1/24-chunk rarity and both horizontal orientations.
- [x] Build a complete framed 3×5 active rift with discovery pockets for open and buried placement.
- [x] Atomically reject lava/fluids, bedrock and block entities before modifying terrain.
- [x] Preserve the entry dimension so Nether rifts return players to their source portal.
- [x] Run fresh-chunk generation/count/restoration QA, resource validation, build and dedicated startup.

## Completed slice: task 28 — Ember Kapok propagation

- [x] Register a fire-resistant Ember Kapok Sapling with cross model, original texture, tags and five locales.
- [x] Add vanilla-style Ember Crown loot: shears/Silk Touch leaf retention and natural sapling drops.
- [x] Grow a space-validated branching Kapok only from Blaze Powder and consume powder only after success.
- [x] Convert the root radius into Ember Loam/Cinder over Vein Crust without replacing fluids, bedrock or block entities.
- [x] Replace the broken orientation-dependent Kapok Pod inventory model with a detailed dedicated 32px icon.
- [x] Validate loot in ordinary/shears dedicated contexts, all JSON/PNG assets, build and startup.

## Completed slice: task 29 — Fire model parity

- [x] Replace the Demonic Trident's duplicate cuboid inventory model with a GeckoLib `builtin/entity` renderer.
- [x] Share the projectile's exact geo, emissive texture and animation resource with held, GUI, ground and frame contexts.
- [x] Keep flight/return motion projectile-owned and add only a restrained inventory core pulse.
- [x] Audit Diablo geometry in model units and correct the body hitbox from `1.30 x 3.60` to `1.30 x 2.75` blocks.
- [x] Keep the nearly four-block wing span decorative rather than creating invisible side collisions.
- [x] Validate all JSON/PNG resources, full build, dedicated startup and client initialization through the host GLFW boundary.

## Completed slice: task 30 — trophy progression foundation

- [x] Register distinct archive trophies for all 14 current custom hostile mob types; exclude EXO suits and effect/projectile entities.
- [x] Add one central `EntityType -> Item` binding map with guaranteed server death drops that respect `doMobLoot`.
- [x] Create a coherent family of 14 original 32px species medallions, generated item models and localized archive tooltips.
- [x] Add the expandable `#opusvsexe:trophies` tag and `Chronicles of the Fallen` advancement tab.
- [x] Require all current trophies simultaneously for `Every Echo Preserved`, and integrate the existing Omega summon challenge into the tab.
- [x] Validate registry/locales/resources/build/startup; runtime QA confirmed one Fire Slime trophy and suppression under `doMobLoot=false`.

## Completed slice: task 31 — Paradise Island worldgen foundation

- [x] Register a seven-block Paradise palette, dedicated creative tab and complete block/item resource surface.
- [x] Generate an original 97x72x97 floating sanctuary with deep stone underside, gardens, paths, water mirrors and 12 trees.
- [x] Build the Parthenon as a readable open-air arena with 28 columns, roof galleries, stepped pediments and a functional central lodestone.
- [x] Register rare fixed-height Overworld jigsaw generation with no terrain projection/adaptation and explicit temperate biome eligibility.
- [x] Add four reliquaries and a conservative Paradise cache loot table without exposing later boss equipment.
- [x] Validate 66,272-block NBT parity, all JSON/resources, full build, dedicated startup and natural fresh-chunk placement.

## Completed slice: task 32 — Paradise ecology foundation

- [x] Add a renewable Paradise Sapling with guarded random-tick/ordinary-bonemeal growth and a stable edible Paradise Fruit ID.
- [x] Extend Paradise Crown loot with survival sapling/fruit chances while preserving shears and Silk Touch behavior.
- [x] Add the peaceful flying Sunfinch and grounded Cloud Grazer with dedicated AI, fruit breeding, attributes and spawn eggs.
- [x] Create two original GeckoLib rigs/animation sets and hard-edged Paradise texture atlases.
- [x] Restrict biome injection to island-eligible biomes and actual spawning to Paradise Grass.
- [x] Expand the central trophy archive from 14 to 16 mob bindings and add a Paradise archive advancement.
- [x] Run growth, loot, movement, attribute, trophy, resource and dedicated startup QA; restore all QA state.

## Completed slice: task 33 — Paradise Wyvern and hurricane

- [x] Add the rare 42-HP Paradise Wyvern with Paradise-Grass-only natural spawning, fruit taming/healing/breeding and owner sit/follow behavior.
- [x] Add persistent visible saddling, owner-only riding, capped aerial cruise/ascent/descent and deliberate idle sink.
- [x] Route bounded rider input to the server and reject vanilla client vehicle-position authority for this mount.
- [x] Fire an impact-driven Wind Core from an edge-triggered Use input with a saved/synchronized 300-tick per-wyvern cooldown.
- [x] Add the 120-tick Hurricane with telegraph, server-owned suction/orbit/lift, captured-target outward release, ally/owner exclusions and no block damage.
- [x] Create original GeckoLib rigs, animations, emissive VFX atlases, spawn egg and EN/RU/UK/PL/DE localization.
- [x] Expand the central archive from 16 to 17 trophies and Paradise from 2 to 3 criteria.
- [x] Validate build/resources/startup, collision conversion, all three vortex phases, NBT across restart and exact trophy count; remove all QA state.

## Completed slice: task 34 — Paradise Angel Boy encounter

- [x] Replace the Parthenon center marker with an unbreakable persistent `angel_dais` that creates one dormant encounter and records defeat.
- [x] Add the 420-HP three-phase Angel Boy with a concept-faithful red-haired, ruby-hearted, segmented-halo silhouette and six independently rigged wings.
- [x] Make the first living-source hit deal zero damage, bind the challenger and apply the safe 25-block-class rebuff launch before combat begins.
- [x] Add six server-owned attack entities with distinct models, telegraphs, timing and mechanics: Halo Lance, Seraphic Crosswind, Verdict Feather, Wingbeat Ring, Ascension Grip and Ruby Descent.
- [x] Add a non-destructive visible court boundary, saved arena leash, phase-two flight, phase titles and guaranteed four-pinions/one-shard progression loot.
- [x] Expand the archive from 17 to 18 trophies and Paradise from 3 to 4 criteria with EN/RU/UK/PL/DE coverage.
- [x] Validate resources/build, structure NBT parity, zero-damage awakening, phase thresholds, exact damage for all six actions, block safety, loot and dais persistence across restart; remove all QA state.

## Completed slice: task 35 — Aerie Bronze and Parthenon gear

- [x] Add renewable common-creature materials, an iron-equivalent intermediate set and the dedicated Parthenon Forge.
- [x] Add diamond-equivalent Regalia with intrinsic Protection III, +10 full-set health and deterministic chest-flight cleanup.
- [x] Add the five diamond-tier Parthenon tools with intrinsic Efficiency IV and station-only survival progression.
- [x] Route full-set V through the shared aimed Hurricane with a persisted 300-tick cooldown.
- [x] Validate all 14 forge recipes, returned Ruby catalyst, exact attributes/enchantments, health/flight cleanup and cooldown on dedicated startup.

## Completed slice: task 36 — Angel Boy resurrection and Paradise integration

- [x] Add four fixed cardinal Seraphic Reliquaries with visible charged state, insertion/retrieval interaction and old-island air-socket backfill.
- [x] Add an atomic persisted 80-tick dais ritual requiring four Seraphic Pinions and one Ruby Halo Shard after the first victory.
- [x] Reject undefeated, incomplete, active and living-boss states without consumption; refund every offering on a late duplicate or spawn failure.
- [x] Spawn exactly one dormant persistent Angel Boy, reset the dais and grant the localized `Reopen the Heavens` challenge.
- [x] Refresh caches with common island ingredients only, update both structure copies, authored reliquary models/textures and five locales.
- [x] Validate JSON, synchronized NBT, main/client compilation, full build and the complete ritual/refund contract on a dedicated server; remove all QA state.

## Completed slice: task 37 — Dark Forest worldgen foundation

- [x] Register a rare real Overworld Dark Forest biome by splitting suitable vanilla Dark Forest climate cells while retaining caves, ores and springs.
- [x] Add Moonlit terrain, Gloomwood logs/leaves, Fountain Stone and one immutable luminous Moon Fountain Core with complete block/item assets.
- [x] Generate dense clearance-checked Gloomwood trees and biome-only surface conversion without replacing fluids or block entities.
- [x] Generate a large original `49x22x49` Moon Fountain arena only in Dark Forest, with conservative exploration caches and reserved ritual anchors.
- [x] Blend local sky, fog and apparent celestial angle toward readable midnight on the client without changing server time.
- [x] Add a dedicated creative tab, survival loot/tags and EN/RU/UK/PL/DE localization.
- [x] Validate resources/NBT, full build, natural fresh-world biome terrain, structure eligibility, dedicated startup and client mixin initialization; remove all disposable QA worlds.

## Completed slice: task 38 — Dark Forest ecology and creatures

- [x] Make Gloomwood renewable through guarded random-tick/bonemeal saplings and replace placeholder undergrowth with biome-owned Moonflower and Thorn Fern patches.
- [x] Add biome-only Shade Spiderling, Gloom Broodmother and Moonwing Bat spawns with exact combat scale, original GeckoLib rigs, animation sets, textures, renderers and spawn eggs.
- [x] Implement bounded non-griefing Web Lane, body slam and every-third-cycle sonar mechanics with visible telegraphs, hit-once tracking and finite effect lifetimes.
- [x] Guarantee one persisted Broodmother release of exactly six Spiderlings, with no child chaining or reload/re-entry duplication.
- [x] Add Shade Silk, Moonwing Membrane, survival loot and three localized trophies; expand the central archive from 18 to 21 entries and add its Dark Forest branch.
- [x] Validate growth, spawn lists/floors, attributes/hitboxes, flight, hazards, exact child count, loot/trophies, resources, full build, dedicated startup and client renderer initialization; remove all disposable QA state.

## Completed slice: task 39 — Mossbound Enderman encounter

- [x] Convert the Moon Fountain Core into a persisted arena controller that spawns one dormant boss near an approaching player and records victory without accidental respawn.
- [x] Add the `2.35x5.25`, 480-HP Mossbound Enderman with a 46-bone GeckoLib rig, 14 animations, zero-damage awakening, exact 65%/30% phases, fountain leash and visible projectile evasion.
- [x] Implement Antler Sweep, Root Snare, Marked Step, Moonwell Orbs, Bloomfall, Echo Double and Eclipse Rush with finite non-griefing attack entities and readable telegraphs.
- [x] Add the phase-three `1.5x` open-flower weak point, exact Moonflower Heart plus four Rootbound Eyes, spawn egg and the 22nd archive trophy.
- [x] Add focused QA selection and validate only Task 39 runtime/resource contracts plus the final build; remove the disposable world and restore server settings.

## Completed slice: task 40 — Dark Forest equipment

- [x] Add renewable Briarweave with exact iron-equivalent armor and full-set poison rejection.
- [x] Upgrade the exact boss bundle into diamond-equivalent Dark Forest Vestments with five deterministic full-set effects and cleanup.
- [x] Add five level-0/speed-12/1561-use tools with intrinsic Unbreaking I and held Haste III.
- [x] Route full-set V through a server-authoritative safe 32-block teleport with a persisted exact 50-tick cooldown.
- [x] Add 14 recipes/icons, two custom armor rigs/atlases and five-locales; pass focused runtime/resource QA and final build.

## Completed slice: task 41 — Dark Forest resurrection and integration

- [x] Activate four exact cardinal Rootbound Pedestals with safe old-fountain placeholder backfill and reversible Eye interaction.
- [x] Add an atomic persisted 100-tick Core ritual requiring four Rootbound Eyes plus one Moonflower Heart after victory.
- [x] Spawn exactly one dormant boss at tick 72, make the persisted UUID reload-safe and refund all offerings on late conflict/failure.
- [x] Refresh common-only fountain caches and add the localized `Return of the Last Bloom` advancement.
- [x] Pass focused dedicated ritual QA, synchronized-NBT/resource audit and final build; remove the disposable world.

## Completed slice: task 42 — Survivor settlement

- [x] Add a terrain-adapted `91x31x91` settlement with palisades, watchtowers, lodge, market, forge, infirmary, farms, homes and conservative caches.
- [x] Guarantee twelve persistent player-scale residents with twelve original project-owned skins, visible held equipment and armor.
- [x] Add neutral persistent anger, exact below-three-heart retreat, useful-equipment pickup and melee use.
- [x] Make unarmed residents flee Haiku while Opus-armed residents acquire and damage them through the shared lattice.
- [x] Translate full-set passives into resident AI and sell four animated self-calibrating structure/biome expedition compasses.
- [x] Add the 23rd trophy/archive criterion, discovery advancement, five locales and focused runtime/resource/placement QA.

## Completed slice: task 43 — Japanese settlement and warriors

- [x] Add a terrain-adapted `97x36x97` Japanese settlement with a layered reference-led keep, reserved boss court, dojo, homes, torii, cherry perimeter, river and arched bridge.
- [x] Guarantee eight Black Ninja and four `1.3x` Samurai with persistent structure placement and collision-audited anchors.
- [x] Add distinct humanoid rigs/textures, held-weapon rendering, action poses, smoke-step and committed long-lunge combat.
- [x] Add craftable ordinary/long katana tiers with original icons/models and 60/80-tick right-click techniques.
- [x] Add common-only caches, two trophies, discovery/archive progression and five-locales.
- [x] Pass focused dedicated behavior QA, resource/NBT audit, exact `8+4` live placement and final build.

## Completed slice: task 44 — Young Samurai boss

- [x] Place exactly one persistent 360-HP Young Samurai in every Japanese Settlement central court with a fixed Long Katana.
- [x] Build the reference-led floral-kimono model with detailed hair ornaments, sleeves, obi, skirt panels, paired scabbards and authored combat poses.
- [x] Implement five sword/taijutsu techniques, high leap, safe combat teleporting and arena return without block damage.
- [x] Guarantee projectile evasion and apply an exact 30% teleport dodge to every other incoming damage event.
- [x] Transition at half health into a visible red/violet aura and reduce recovery from 48 to 24 ticks for exact doubled ability cadence.
- [x] Add boss loot, the 26th trophy, dedicated advancement, five locales and focused runtime/placement verification.

## Completed slice: task 45 — illustrated HTML documentation

- [x] Build a dependency-free responsive field archive with warm lamplit styling, mobile navigation, active section tracking, keyboard search and accessible markup.
- [x] Document canonical lore, all five progression branches, landmarks, mobs/bosses, equipment, abilities, cooldowns, rituals, commands and troubleshooting.
- [x] Generate the complete 56-recipe book and 26-trophy archive from authoritative live resources instead of maintaining duplicate manual lists.
- [x] Add a searchable 269-entry item/block visual compendium and bundle local copies of every displayed project asset.
- [x] Link the guide from the root README and add deterministic generation/integrity scripts.
- [x] Pass focused HTML/catalog/link/asset validation and the final build.

## Completed slice: task 46 — Survivor anger crash fix

- [x] Diagnose the integrated server/client `StackOverflowError` as recursive `canAttack → isAngryAt → canAttack` evaluation.
- [x] Read persistent and universal anger state directly while preserving base attack checks, retreat and Haiku relations.
- [x] Add a focused regression covering both `canAttack(player)` and vanilla `isAngryAt(player)` plus unrelated-player rejection.
- [x] Pass focused Task 42 dedicated QA, restore all server settings, remove the disposable world and complete the final build.

## Verification baseline

- 2026-08-25: `JAVA_HOME=/home/shutniko/.jdks/jdk-17.0.20+8 ./gradlew build` — SUCCESS, no test sources.
- 2026-08-25: all resource JSON parsed with `jq`; Frontier NBT passed toolkit QA and pack/resource copies are identical.
- 2026-08-25: current-source dedicated server reached `Done`; Titan stayed at 200 HP after fire and generic damage, took valid EXO damage (193.6 HP), and dropped 6 Titan Memory Plates.
- 2026-08-25: memory fragments 1–15 moved from hardcoded partial English to authoritative localized lore; EN/RU/UK/PL/DE key coverage and build passed.
- 2026-08-25: katana slash entity spawned on a current-source dedicated server and a configured 7-damage slash reduced a 10-health cow to 3 exactly once; client reached mod/renderer initialization before the host Wayland focus limitation stopped window creation.
- 2026-08-25: Opus armor Resonant Rage added with a 120-tick duration and 240-tick shared ability cooldown; localization, build and dedicated startup passed.
- 2026-08-25: hidden EXO B-key punch completed; configured runtime wave dealt exactly 7 damage once, expired normally, and dedicated startup/build/resource validation passed.
- 2026-08-25: Diablo battle theme and repeat seal ritual completed; the 152 s theme resource, server audio state, persistent boss entity, build and dedicated startup passed.
- 2026-08-25: Fire gear foundation completed; five recipes resolved on dedicated startup, `#c:ores` recognized iron ore, Vein Essence survived lava while a dirt control burned, and full build/resource validation passed.
- 2026-08-25: Ember Depths item line retired; parser rejected removed item IDs, retained armor and Vein Crust IDs resolved, 540 JSON resources parsed, clean build passed, and the existing server world loaded successfully.
- 2026-08-25: Breakthrough feature applied to all five Nether biomes; 256 fresh chunks were generated and reversible scanning found a complete open-cavern rift core at Y=80–84. QA state was restored/removed and build/startup passed.
- 2026-08-25: Kapok propagation completed; dedicated QA yielded saplings from ordinary leaf breaks and the leaf itself with shears, corrected the 1.20.1 loot schema, and validated the repaired Pod/sapling assets and startup.
- 2026-08-25: Demonic Trident held/thrown rendering now shares one GeckoLib geo/material source; Diablo's audited 20.6x44-unit combat silhouette maps to a `1.30 x 2.75` hitbox. Full build and dedicated startup passed; client loading reached the known host Wayland window-focus limitation.
- 2026-08-25: trophy foundation registered 14 current mob trophies and four archive advancements. Dedicated death QA produced exactly one Fire Slime trophy (`Count: 1b`), suppressed it with `doMobLoot=false`, restored the gamerule and removed QA drops.
- 2026-08-25: Paradise foundation registered seven original blocks and naturally generated a 97x72x97 island at the fixed sky band. Fresh-world QA confirmed the complete lodestone/column/pediment/reliquary anchors, airborne rim, valid cache loot and one saved jigsaw child covering Y=147…218; the disposable QA world was removed after restoring server settings.
- 2026-08-25: Paradise ecology added guarded bonemeal tree growth, fruit/sapling crown loot, a true-flying 6-HP Sunfinch and 18-HP Cloud Grazer. Dedicated QA confirmed growth clearance, both loot outcomes, autonomous aerial/ground movement and one trophy per species; time, gamerules, force-loads and QA blocks/items were restored.
- 2026-08-25: Paradise Wyvern slice added fruit taming, persistent saddling, server-owned flying input, Wind Core and the six-second no-block-damage Hurricane. Scheduled dedicated QA proved telegraph/active/release vectors and impact conversion; cooldown/saddle survived restart, one trophy dropped, and every temporary entity/block/force-load/datapack was removed.
- 2026-08-25: Angel Boy completed the live Parthenon encounter with a 42-bone six-wing rig, first-hit immunity/rebuff, three boss phases and six dedicated attack entities. Dedicated QA proved exact `10/8/7/9/6/15` action damage, block safety, phase flight, exact `4+1+1` progression/trophy loot and saved dais defeat state across restart; all QA state was removed.
- 2026-08-25: Parthenon gear completed the Paradise equipment line. Dedicated contract QA proved all 14 forge recipes, the non-consuming Ruby catalyst, iron/diamond-equivalent tiers, Protection III/Efficiency IV, exact `20 -> 30 -> 20` health cleanup, chest flight revoke and aimed-Hurricane cooldown.
- 2026-08-25: Paradise resurrection completed the region. Both island NBT copies contain four exact cardinal reliquaries; dedicated QA proved atomic failures, `4+1` consumption, active NBT persistence, one dormant boss, defeat reset, advancement completion and exact late-conflict refund before clean shutdown.
- 2026-08-26: Dark Forest foundation completed. Fresh-world dedicated QA found a natural biome at `[-2704, 63, -368]` and sampled `154/33/395` Moonlit Grass/Gloomwood Log/Gloomwood Leaves; `/locate` resolved the Moon Fountain at `[-1088, ~, 4000]`. All 721 JSON files, eight 32px textures, five locales and identical 3,917-block fountain NBT copies passed audit; full build succeeded and client loading reached renderer initialization before the known Wayland GLFW boundary.
- 2026-08-26: Dark Forest ecology completed. Fresh dedicated QA re-confirmed natural terrain at `[48, 69, 368]` (`255/18/217` grass/log/leaves), then proved sapling growth/clearance, all three exact mob contracts and biome spawns, Web/Pulse hit-once expiry, true aerial movement, exactly six successful Broodmother children, deterministic common loot, three trophy mappings and `doMobLoot` suppression. All 755 JSON resources, five locales, 13 new gameplay PNGs and five GeckoLib model/animation pairs passed audit; full build succeeded, client loading reached LWJGL backend initialization, and all disposable QA state was removed.
- 2026-08-26: Mossbound Enderman encounter completed. Focused dedicated QA proved single fountain spawn and persisted defeat, zero-hit awakening, exact 480-HP/phase/weak-point contracts, finite Root Snare damage, safe arena teleport, exact `1+4` progression loot and the 22nd `doMobLoot`-aware trophy. The 21 changed JSON files, 10 PNGs, 46-bone/14-animation boss and six attack rigs passed targeted audit; final build succeeded and the disposable world was removed.
- 2026-08-26: Dark Forest equipment completed. Focused dedicated QA proved all 14 recipes, exact armor/tool tiers, poison rejection, five-effect cleanup, held Haste III and safe persisted 50-tick V teleport. The 14 icons, two armor atlases/rigs and five locales passed targeted audit; final build succeeded.
- 2026-08-26: Dark Forest resurrection completed the region. Focused dedicated QA proved safe pedestal backfill/interaction, atomic failures, exact `4+1` consumption, active NBT, one dormant boss, advancement validity, settled timer and exact late-conflict refund. Four exact uncharged pedestals were verified in synchronized fountain NBT; a reload-time unresolved-UUID duplicate window was fixed; final build succeeded and the disposable world was removed.
- Runtime setup fix: stale `run/mods/opusvsexe-1.0.0.jar` was moved to `run/mods-disabled/`; stale processed copy moved to `run/processedMods-disabled/`. Both remain recoverable.
- Working tree was already heavily modified/untracked before this slice; do not revert or overwrite unrelated work.
