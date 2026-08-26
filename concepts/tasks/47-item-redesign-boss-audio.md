# Task 47 — Item visual redesign, Haiku Core 3D, boss music and Cinder Slime polish

Status: `IMPLEMENTED` — build/resource/dedicated-server validated 2026-08-26.

## Scope

Four independent deliverables on top of the completed Paradise / Dark Forest / Settlement content:

1. New item textures/models for every item in the four creative sections (textures only, no registry/ID changes).
2. Animated emissive 3D Haiku Core item model.
3. Encounter music for Young Samurai, Mossbound Enderman and Angel Boy (+ slowed enderman death cry).
4. Cinder Slime cartoon-hop movement and creeper-style explosion animation work.

## 1. Item redesigns (textures only; no IDs/recipes touched)

### Sky Paradise (`paradise_tab`) — `gen_item_redesigns.py`
Ivory/honey-gold/jade/cyan/ruby language per `concepts/paradise/MASTER_PLAN.md`:
- `paradise_fruit` golden-white hanging fruit with cyan seed-heart; `sunfeather` gold-white feather; `cloud_fleece` ivory cloud tuft.
- `aerie_bronze_ingot` copper-bronze ingot with gold seams; Aerie Bronze armor set (weathered bronze plates + honey edges + ivory feather stitch).
- `seraphic_pinions` folded feather pair with ruby clasp; `ruby_halo_shard` ruby halo segment with gold filigree and white-hot core.
- Parthenon Regalia set (stepped gold borders, jade recesses, ruby catalyst marks, cyan airflow channels) and Parthenon tool set (ivory/gold heads + cyan channels).
- Missing `seraphic_reliquary` item model added (parent block model).

### The Four Veins (`fire_tab`) — `gen_item_redesigns_2.py`
Basalt/ember/sulfur language (Fire line has its own palette; no Opus violet/cyan):
- `fire_essence` obsidian-ember orb with fire veins and white-hot centre.
- Fire tool set: volcanic black iron, ember veins, fire-white edges.
- Vein Crust baseline armor (`fire_*`): basalt plating, ember seams, horned crown guard.
- Retained Ember armor: burgundy enamel, amber ribs and horns iconography.

### Dark Forest (`dark_forest_tab`) — `gen_item_redesigns_2.py`
Night/moss/teal/moon-silver language per `concepts/dark_forest/MASTER_PLAN.md`:
- `shade_silk`, `moonwing_membrane`, `moonflower_heart`, `rootbound_eye`, `briarweave` materials.
- Briarweave armor (thorny woven set with teal threads) and Dark Forest Vestments (deep-night set with moon-teal glow and silver trim).
- Dark Forest tool set: moon-black metal, teal moonlit edges.

### Survivor Settlement (`settlement_tab`) — `gen_item_redesigns_2.py`
- `katana` (black tsuka/crimson blade edge, blue ito wrap, gold tsuba) and `long_katana` (red ito, gold guard, longer blade) with existing hand transforms preserved.
- Four expedition compasses: body/dial recoloured per target identity (Unbroken-purple/cyan → Opus ruins, gold/jade → Paradise, teal/moss → Dark Forest, silver-teal → Moon Fountain). **Needle pixels are copied 1:1 from the pre-redesign 32 frames of each compass, so the angle→frame mapping and pointing functionality are preserved byte-for-byte** (verified: per-frame needle diff identical = 57 px).
- Missing item models for `survivor_spawn_egg` / `black_ninja_spawn_egg` / `samurai_spawn_egg` / `young_samurai_spawn_egg` added (`template_spawn_egg`), which previously rendered as missing textures in the tab.

## 2. Haiku Core — animated emissive 3D model

- `HaikuCoreItem` now implements `GeoAnimatable` (idle controller).
- `geo/item/haiku_core.geo.json`: amber crystal octahedron core (dark→mid→bright amber), inner glow cube, front/back white-hot hotspot, vertical + horizontal gyro rings on independent bones, 4 orbiting amber tears, pale-metal frame (9 bones).
- `textures/item/haiku_core_3d.png` (64×64 atlas, Haiku palette W/F/g/S/e + amber d→A→P→f→W) + `haiku_core_3d_glowmask.png`; GUI icon `haiku_core.png` redrawn.
- `animations/item/haiku_core.animation.json` (idle 5 s loop): hover bob, counter-rotating gyro rings, core pulse, hotspot flicker, tear orbit.
- Client: `HaikuCoreModel`/`HaikuCoreRenderer` (translucent render + `AutoGlowingGeoLayer`), registered via `BuiltinItemRendererRegistry` in `OpusVsExeClient`.
- Generator: `scripts/gen_haiku_core_3d.py`.

## 3. Boss audio

- Converted provided MP3s to streaming OGG under `sounds/music/`: `japanese_fight.ogg`, `enderman_theme.ogg`, `angel_boy_theme.ogg` (+ `young_samurai_defeated.ogg` defeat sting from `the end.mp3`).
- New sound registries: `ParadiseSounds` (`angel_boy_theme`), `DarkForestSounds` (`enderman_theme`, `mossbound_enderman_death`), `SettlementSounds` (`japanese_fight`, `young_samurai_defeated`), wired into each `*Line.init()`.
- Shared lifecycle helper `BossMusicHub` (non-positional play within radius, stop packet cleanup).
- Start/stop wiring: Angel Boy on `awaken()`, Mossbound Enderman on `awaken()`, Young Samurai on first player-boss damage; every 2 s newcomers inside the arena are invited and the track is dropped when the audience leaves; music stops on `die()` (Angel/Moss/Young) and a 3 s slowed enderman death cry (`mossbound_enderman_death.ogg`, vanilla `mob/endermen/death.ogg` at 60% tempo + echo + loudnorm) plays for the Mossbound Enderman.
- `sounds.json` + subtitles in all five locales (EN/RU/UK/PL/DE).

## 4. Cinder Slime (FireSlimeEntity)

- Server-authoritative state machine synced over `ANIM_STATE`: IDLE → COMPRESS → AIRBORNE → LAND → IDLE (jerky cartoon hops: pause 10–18 t, 5-tick squash, hop impulse by size), plus ATTACK lunge and DEATH squish (entity discarded after the 22-tick clip).
- Creeper-style detonation: 50-tick charge with hiss, shell/core swelling via `getChargeProgress`, white `FLASH` + end-rod flicker during the final 14 ticks, then `EXPLOSION_EMITTER` + explosion/flash/ember/ash/smoke burst and a block explosion (`Level.ExplosionInteraction.BLOCK`).
- New animation clips in `animations/fire/slime.animation.json` (compression/airborne/land/attack/charge/death) driven by the synced state; `FireSlimeModel` adds a flashing white bump on the shell and near-white core at the end of the charge.
- Legacy Ember line untouched (kept its own animation file).

## QA evidence

- `scripts/validate_resources.py` — **PASS**: 974 JSON, 509 PNG, 62 OGG, 41 Gecko pairs.
- `scripts/validate_docs.py` — PASS; `docs/assets/items` refreshed for the compendium.
- `./gradlew build` — BUILD SUCCESSFUL (main + client). `runServer` reached `Done (11.879s)` with only the known pre-existing "No data fixer" dev warnings and a pre-existing Task-37 worldgen QA assertion on the old seeded world.
- Compass needle preservation verified per frame (57 changed pixels between 00↔08 identical to the donors for all four compasses).

## Files

- Generators: `scripts/gen_haiku_core_3d.py`, `scripts/gen_item_redesigns.py`, `scripts/gen_item_redesigns_2.py` (all re-runnable).
- Audio: `sounds/music/{japanese_fight,enderman_theme,angel_boy_theme,young_samurai_defeated}.ogg`, `sounds/mossbound_enderman_death.ogg`.
- Java: `BossMusicHub`, `ParadiseSounds`, `DarkForestSounds`, `SettlementSounds`, boss wiring in `AngelBoyEntity`, `MossboundEndermanEntity`, `YoungSamuraiEntity`; `HaikuCoreItem` + client model/renderer; `FireSlimeEntity` + `FireSlimeModel`.
## Post-implementation crash fix (2026-08-26)

- Dev QA probes (`Tasks 31–46`, incl. Task 37 worldgen and Task 39 Mossbound contract) previously ran on **every** world load because `DevelopmentQa.enabled()` returned true whenever `-Dopus.qa.only` was unset. `MossboundEncounterQa` also asserted a stale `ATTACK_DAMAGE==14` against the shipped `16`, so an ordinary dev load crashed in the server tick loop.
- Fix: `DevelopmentQa.enabled(int)` now returns true only when an explicit selector matches — normal loads never execute the world-mutating contract probes. `MossboundEncounterQa` assertion updated to `16` to match `createAttributes()`.
- `build.gradle` runs now forward the selector (`./gradlew runServer -Dopus.qa.only=39`) via `vmArg`.
- Verified: plain `runServer` reaches `Done` with zero crash reports; focused `-Dopus.qa.only=39` run prints `Task 39 QA PASS` and no crash. Full build + resource validation pass.

## Re-verification pass (2026-08-26, follow-up session)

User re-requested the full Task 47 scope ("design from scratch") + instruction: skip anything already implemented in code. Full audit result — every requested feature is present and wired:

1. Sky Paradise tab items (non-block/non-egg: fruit/feather/fleece/ingot/Aerie Bronze set/Seraphic Pinions/Ruby Halo Shard/Parthenon tools+Regalia) → redesigned textures exist (`gen_item_redesigns.py`).
2. Four Veins tab (fire_essence, fire tools, Vein Crust `fire_*` set, retained Ember set; `fire_bean` is a BlockItem → excluded by scope; portal/trident excluded) → `gen_item_redesigns_2.py`.
3. Dark Forest tab (shade_silk/moonwing_membrane/moonflower_heart/rootbound_eye/briarweave+briarweave set/DF tools+armor; blocks, altars (moon_fountain_core/rootbound_pedestal), flowers/plants, eggs excluded per scope).
4. Haiku Core animated 3D model → geo/anim/glowmask + `HaikuCoreModel`/`HaikuCoreRenderer`, idle controller in `HaikuCoreItem`.
5. Boss audio wiring verified in code: `YoungSamuraiEntity` → `SettlementSounds.JAPANESE_FIGHT`; `MossboundEndermanEntity` → `ENDERMAN_THEME` on awaken + slowed `MOSSBOUND_DEATH` on die; `AngelBoyEntity` → `ANGEL_BOY_THEME`. OGGs present under `sounds/music/`.
6. Survivor Settlement: katana/long_katana redesigned (hand transforms preserved), 4 expedition compasses redrawn with needle functionality preserved (angle 0→frame16-up convention, 11.25°/frame clockwise), spawn-egg item models added.
7. Cinder Slime: jerky-hop state machine (IDLE→COMPRESS→AIRBORNE→LAND) + creeper-style 50-tick charge with white FLASH and block explosion in `FireSlimeEntity`.

Verification: `./gradlew build` PASS; `validate_resources.py` PASS (973 JSON / 509 PNG / 62 OGG / 41 Gecko pairs).

Known pre-existing issue (NOT part of this task, TODO): `validate_docs.py` fails on this branch — `docs/index.html` (218-line compact site from the doc-task rewrite) uses section ids `start/story/worlds/creatures/equipment/…` while the validator still requires the old 14-section scheme (`overview/installation/world/entities/bosses/armor/arsenal/controls/advancements/mechanics/status/installation`). Needs either index.html restructuring or validator contract update — separate doc task.

Note: `/home/shutniko/Рабочий стол/mod/Minecraft` is an OLD divergent copy (ends at fire-lineage tasks ~34, no paradise/dark forest/settlement packages). All current work lives here in `mod_dublicate/Minecraft`. No action taken there.
