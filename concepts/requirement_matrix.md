# Master specification requirement matrix

Updated: 2026-08-26. Status reflects repository evidence, not intent. `TESTED` means an automated/build/runtime check was actually run.

## Opus / Haiku line

| Requirement | Status | Evidence / next action |
|---|---|---|
| Haiku-5 exists as independent miniboss and Omega reinforcement | TESTED | Boss bar/entity exist; guaranteed Citadel and Frontier Fortress spawners; Omega still summons it. Dedicated server summon passed. |
| Haiku-5 has multiple designed abilities | IMPLEMENTED | Server-authoritative Titan Quake and Amber Rush with synced action animation/telegraphs; live target-trigger QA pending. |
| Haiku-5 plates and Opus armor crafting progression | TESTED | Titan Memory Plate, 4 armor recipes and entity loot table exist; runtime kill dropped 6 plates. |
| Haiku-4 has one unique ability | IMPLEMENTED | Resonance Guard telegraph/repulse/defensive window and dedicated animation added; live target-trigger QA pending. |
| Every Haiku is fire/lava immune | TESTED | Shared `HaikuMob.fireImmune`; dedicated-server Titan fire damage left 200 HP unchanged. |
| Every Haiku takes damage only from Opus tools/weapons | TESTED | Shared gate; generic damage rejected at 200 HP, EXO damage reduced Titan to 193.6 HP. Player-held Opus item path still needs multiplayer QA. |
| Opus mining tool set | IMPLEMENTED | Pickaxe/axe/shovel/hoe, 32px textures, handheld models, recipes and damage tag added; in-hand/mining QA pending. |
| Haiku Core has original active-heart visual | IMPLEMENTED | Custom item texture and animated Altar Heart block renderer exist; visual runtime QA remains. |
| Altar Heart restores the crystal for repeat Omega summons | IMPLEMENTED | Activated altar consumes Altar Heart and resets only when no living Omega is in the arena; runtime interaction QA pending. |
| All Opus structures generate more often | IMPLEMENTED | Structure sets use increased spacing values; worldgen runtime sampling remains. |
| Memory fragments contain correct complete lore | TESTED | All 15 now follow authoritative lore through translation keys; EN/RU/UK/PL/DE key coverage and JSON parsing passed. |
| Katana-OP forward purple Sword Slay, 2.5 s cooldown | TESTED | Dedicated forward slash entity travels 32+ blocks at 14 damage; exact 50-tick cooldown, build and server entity/damage QA passed. |
| Refined Katana area air-slash barrage | TESTED | Twelve server-owned radial cyan slash entities, 8 damage and hit-once tracking; registration/damage QA and build passed. |
| Golden Katana forward ground/sky golden rupture | TESTED | Gold variant tracks terrain for ~18 blocks, damages a corridor, then creates a rising launch blast; registration/damage QA and build passed. |
| Opus warhammer shockwave | IMPLEMENTED | Server-side radial damage/knockback exists; dedicated effect-entity polish remains. |
| Full Opus armor shockwave + rage | TESTED | V triggers a server-validated shockwave plus bounded Resonant Rage (Strength IV and +35% attack speed); non-stacking cooldown/cleanup, build and startup passed. Live-player input QA remains. |
| Opus armor balance: two extra health bars, Strength reduced one level | TESTED | Health Boost X adds exactly +40 health (two bars) and baseline Strength is III; constants/build/startup audit passed. |
| Kimi laboratory, ruined city and data spire rebuilt from references | IMPLEMENTED | Single-template NBT rebuilds and generators exist; visual/runtime inspection remains. |
| Hidden B-key EXO punch shockwave ability for every EXO | TESTED | Separate B packet/action works outside HUD slots for the shared EXO base; tier-scaled hit-once entity, both rig animations and original assets added. Build/startup passed and configured 7 damage reduced a cow from 10 to 3. Live piloting input remains manual QA. |
| Haiku Omega is a full boss | IMPLEMENTED | Boss bar, phases, attacks, leash, summons, music and Opus gate exist; runtime combat QA remains. |

## Fire Realm / Four Veins line

| Requirement | Status | Evidence / next action |
|---|---|---|
| Diablo theme and distance-independent combat voice/music | IMPLEMENTED | Original supplied `diablo_theme` is a 152 s streamed OGG; server target state drives a relative/no-attenuation client loop, while all realm players receive notify-sound dialogue. Build/resource/startup passed; live audio listening remains manual QA. |
| Ember armor remodeled, textured and animated with complete flight wings | IMPLEMENTED | Dedicated Ember models, textures and wing states exist; visual runtime QA remains. |
| Ember armor moved to Four Veins; old Vein set removed | TESTED | The Ember tab no longer registers; retained Ember armor is listed in Four Veins. Former `fire_*` IDs were compatibility-migrated into the distinct Vein Crust baseline. Clean build and existing-world dedicated startup passed. |
| Demonic Trident inventory/hand model matches thrown model | IMPLEMENTED | Held/GUI/ground/frame rendering now uses `builtin/entity` plus a GeckoLib item adapter that shares the thrown entity's exact geo, emissive texture and animation resource. Main/client compile and initialization passed up to the host GLFW boundary; visual hand-transform QA remains. |
| Ember Depths section and items removed except Ember armor | TESTED | Creative tab class removed and `EmberItems` registers only four armor pieces. Dedicated parser reported removed Essence/Trident/Cinder item IDs as unknown while armor IDs remained valid. Compatibility block/entity IDs remain without item forms. |
| Vein Crust armor, iron+Protection I equivalence, full-set fire resistance | IMPLEMENTED | Compatibility-preserved `fire_*` IDs now use iron defense/durability, intrinsic Protection I, Magma Crust recipes/repair, hornless crust visuals and full-set Fire Resistance only. Live equip/render QA remains. |
| Ember bonus-health healing bug fixed | IMPLEMENTED | Ember/Fire managers now apply 20-minute set effects only on equip/expiry instead of replacing Health Boost every ten ticks; first equip fills the granted +4 health and unequip cleans up. Build passed; live healing-bar QA remains. |
| Breakthrough in Space Nether generation | TESTED | Custom rift feature applies to all five Nether biomes at uniform Y=6…121 with 1/24-chunk rarity and atomic lava/bedrock/block-entity rejection. Fresh-chunk QA found/restored a complete 15-block core at Y=80…84; Nether return dimension is preserved. |
| Detailed Ember inventory textures and repaired Kapok Pod | IMPLEMENTED | Ember armor inventory textures remain detailed; Kapok Pod now uses a stable generated-item model and redesigned 32px three-chamber icon instead of its oriented block model. Visual in-game QA remains. |
| Fire trees drop growable saplings; blaze-powder growth converts terrain | TESTED | Fire-resistant Kapok Sapling, leaf loot, Blaze Powder-only server growth, clearance guard and radius-5 native terrain conversion added. Dedicated ordinary/shears loot QA passed; live-player growth input remains manual. |
| Fire boss repeat summon | IMPLEMENTED | Four Vein Essences on the central Seal Cryoice rebuild the prison and spawn exactly one persistent sealed Diablo; duplicate guard/build/startup passed, live-player interaction QA remains. |
| Cinder Slime cartoon hops and creeper blast | IMPLEMENTED | Server-authoritative `ANIM_STATE` cycle (pause -> squash -> hop -> stretch -> land), 50-tick hissing charge with white `FLASH` flicker and `EXPLOSION_EMITTER`/ember/ash/smoke blast with block interaction. Build/validator passed; live feel QA remains. |
| Fire tools: auto-Fire Aspect sword, auto-smelt pickaxe, complete set | IMPLEMENTED | Sword restores intrinsic Fire Aspect II; pickaxe defers fresh ore-drop conversion until end-of-tick and uses vanilla smelting recipes with a Silk Touch guard. Axe/shovel/hoe, recipes, handheld assets and five locales are complete; live mining QA remains. |
| Diablo model/hitbox correspondence | TESTED | Geometry audit measured a 20.6-unit arm span and 44-unit horn height; registry hitbox is now `1.30 x 2.75` blocks. Decorative wings are intentionally excluded. Build and dedicated startup passed. |
| Fire items do not burn in fire/lava | TESTED | Every registered Fire and retained Ember item is built from `fireResistant` properties. Dedicated lava test destroyed a dirt control while Vein Essence survived; registry source audit and build passed. |
| Fire armor crafted by upgrading basalt armor | IMPLEMENTED | Vein Crust is crafted from Magma Crust; each retained high-tier Ember piece is upgraded from its matching Vein Crust piece plus four Vein Essences. Recipe loading/build passed; survival crafting QA remains. |

## New progression regions

| Requirement | Status | Evidence / next action |
|---|---|---|
| Paradise Island, trees/mobs, tameable rideable wyvern and hurricane | TESTED | Rare fixed-height 97x72x97 island, renewable fruit trees and all three Paradise species are implemented. Wyvern has fruit taming, persistent saddle, server-owned flight input and 300-tick Wind Core; scheduled dedicated QA proved impact conversion plus the Hurricane's 20/80/20-tick no-force/suction-orbit-lift/release phases without block damage. |
| Parthenon arena and multi-phase Angel Boy boss from concept | TESTED | The 28-column open court now contains one persistent Angel Dais and a concept-faithful 420-HP six-wing boss. Dedicated QA proved zero-damage first-hit awakening/rebuff, 70%/35% phase transitions, flight, exact damage from all six non-griefing action entities, `4+1+1` loot and saved defeat state across restart. |
| Parthenon armor/tools, flight, aimed V hurricane, intermediate set | TESTED | Dedicated contract QA proved 14 station-exclusive recipes, returned boss catalyst, exact iron/diamond tiers, intrinsic Protection III/Efficiency IV, `20 -> 30 -> 20` health cleanup, chest flight grant/revoke and 15-second aimed-Hurricane cooldown. Common-material drops, full build and 684-JSON audit passed; live renderer/input feel remains manual QA. |
| Angel Boy repeat resurrection and final Paradise integration | TESTED | Four structure-bound cardinal reliquaries plus one Ruby Halo Shard drive an atomic saved 80-tick ritual. Dedicated QA proved non-consuming failure paths, exact `4+1` consumption/refund, active NBT round-trip, duplicate guard, exactly one dormant boss, defeat-state reset and the localized child advancement; 688-JSON and synchronized-NBT audits passed. |
| Dark moon forest biome, three spider/bat mobs | TESTED | The real rare biome, local moon/sky presentation, renewable Gloomwood, owned undergrowth and Moon Fountain are implemented. Dedicated QA proved natural terrain, all three biome-only spawn entries and exact stat/hitbox contracts, Moonwing flight, bounded Web/Pulse behavior and one Broodmother death releasing exactly six Spiderlings. |
| Dark Enderman fountain boss, abilities and resurrection | TESTED | The fountain-owned 480-HP Mossbound encounter, seven attacks, weak point, leash and exact `1+4` loot are runtime-tested. Four cardinal pedestals now drive an atomic persisted resurrection; focused QA proved exact consumption/refund, one dormant boss, advancement completion and reload-safe duplicate suppression. |
| Dark Forest armor/tools and intermediate set | TESTED | Briarweave and Vestments, five tools, exact tiers/effects, held Haste III and the safe persisted 50-tick V teleport passed focused dedicated QA. Fourteen recipes/icons, two custom armor rigs/atlases and five locales passed targeted audit. |
| Survivor settlement, survivor AI, equipment, trade maps/compasses, Haiku relations | TESTED | `91x31x91` settlement placed with exactly 12 persistent residents and original project-owned skins. Focused QA passed stats, anger/retreat, pickup, armor abilities, four animated locator trades, conditional Haiku defense and trophy binding. The player-target anger path now has a dedicated recursion regression after repairing the observed integrated-server `StackOverflowError`. |
| Japanese settlement, cherry landscape, ninja/samurai mobs and katanas | TESTED | `97x36x97` reference-led keep, river bridge, torii, cherry perimeter and reserved boss court placed successfully. Focused QA passed Black Ninja/Samurai scale, stats, weapons, techniques, recipes and trophies; live NBT retained exact `8+4` guards. |
| Young Samurai two-phase boss, aura, taijutsu/sword kit and dodge rules | TESTED | A persistent 360-HP court boss now uses five telegraphed attacks, high leap and safe teleporting. Focused QA proved the half-health aura, exact `48 -> 24` doubled cadence, 30% universal dodge boundary, guaranteed projectile dodge, fixed weapon and trophy; live settlement placement retained exactly one boss. |

## Global integration

| Requirement | Status | Evidence / next action |
|---|---|---|
| New textures/models/animations/effects for all visible content | IN_PROGRESS | Task 47 completed a visual pass over all four creative sections (Sky Paradise, The Four Veins, Dark Forest, Survivor Settlement): 50+ redrawn 32px item icons, missing item models (4 settlement spawn eggs, seraphic reliquary), animated emissive 3D Haiku Core, four expedition compasses reworked with the needle copied 1:1 per frame so pointing functionality is preserved, and encounter music for Young Samurai / Mossbound Enderman / Angel Boy plus the slowed enderman death cry. Later regions remain. |
| Boss encounter audio (Japanese fight / enderman theme / angel theme) | IMPLEMENTED | Streaming OGG themes (`japanese_fight`, `enderman_theme`, `angel_boy_theme`, `young_samurai_defeated` sting) tied to aggro/death lifecycles through the shared `BossMusicHub`; slowed vanilla enderman death cry for the Mossbound Enderman. Subtitle keys in EN/RU/UK/PL/DE. |
| Animated 3D Haiku Core item | IMPLEMENTED | Amber crystal core in a counter-rotating gyro mount with emissive glowmask, idle bob/pulse animation and `BuiltinItemRendererRegistry` wiring (`geo/item/haiku_core.geo.json` + 41st validated Gecko pair). |
| Trophy from every mob and all-trophies final completion | TESTED | Extensible registry covers all 26 current custom mobs with guaranteed `doMobLoot`-aware drops, unique 32px items and simultaneous-inventory completion criteria. The Bloodflower Crest binding and final archive criterion passed focused QA. |
| Complete loot, recipes, progression, evolution, story and lore | IN_PROGRESS | Opus/Fire foundations exist; global progression incomplete. |
| Guided advancement tab | IN_PROGRESS | New localized `Chronicles of the Fallen` tab tracks first trophy, Haiku archive, Fire archive and all-current-trophy completion; Omega summon is integrated. Region/crafting guidance expands with upcoming content. |
| Crafting tables/attributes and recipes for all non-cheat content | IN_PROGRESS | Structure crafting blocks exist; recipe coverage is sparse. |
| English, Russian, Polish and German translations | IN_PROGRESS | Memory-fragment content is complete in EN/RU/PL/DE (+UK); Polish/German coverage for the rest of the mod remains. |
| Illustrated navigable HTML documentation site | TESTED | The dependency-free `docs/` field archive documents lore, five progression routes, structures, bosses, equipment, abilities, rituals and commands. Live generators expose all 56 recipes, 26 trophies and 269 localized item/block entries with bundled art; focused HTML/link/asset/catalog validation passed. |
| Build, resource, startup, gameplay and regression tests | IN_PROGRESS | Build/JSON/NBT sync/dedicated startup passed; focused command/contract QA covers Haiku/Fire, Paradise and the complete Dark Forest line through equipment and resurrection. Task selection avoids re-running unrelated legacy QA. Automated multiplayer tests remain. |
