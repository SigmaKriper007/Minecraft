# Task 35 — Aerie Bronze and Parthenon gear

Status: completed and runtime-tested

## Progression contract

- `parthenon_forge` is a dedicated 3x3 station with its own `parthenon_forging` recipe type. Equipment recipes cannot resolve in a vanilla crafting table.
- Sunfinches provide `sunfeather`; Cloud Grazers provide `cloud_fleece`. Together with copper, gold and Paradise Fruit they form renewable Aerie Bronze.
- Aerie Bronze is the safe-island intermediate set: iron defense/durability, mild per-piece knockback resistance and intrinsic Feather Falling II on the boots. It grants no flight.
- Parthenon Regalia upgrades corresponding Aerie pieces with diamond, gilded marble and a Ruby Halo Shard catalyst. The shard is returned after forging so it remains available for repair and task-36 resurrection.
- Parthenon tools upgrade their diamond equivalents in the same forge. Sword/pickaxe/axe/shovel/hoe use diamond tier; mining tools carry intrinsic Efficiency IV.
- All equipment is repairable with the boss-gated Ruby Halo Shard.

## Regalia mechanics

- Each Parthenon piece has diamond-equivalent base defense/toughness and intrinsic Protection III.
- A complete four-piece set adds exactly `+10` maximum health through one transient attribute modifier, never a rounded potion approximation.
- The chestplate grants creative-style flight. The server records only flight it granted and deterministically revokes it on removal, death or loss of the chestplate without disabling legitimate creative/spectator flight.
- The V key reuses the established armor-ability binding. The client sends only an edge-triggered intent packet.
- The server requires a living non-spectator player in a complete Regalia set, checks a persisted per-player ready tick, raycasts from the authoritative eye/look vector up to 48 blocks and creates the shared Paradise Hurricane at the first block/entity intercept.
- Cooldown is 300 ticks/15 seconds. Vanilla item cooldown rendering plus localized action-bar feedback provides compact state information without another permanent HUD panel.

## Visual system

- Aerie Bronze: weathered copper-bronze plates, narrow honey-gold edges, ivory feather stitch. Functional, grounded and visibly below the final set.
- Parthenon Regalia: warm ivory plate, stepped honey-gold borders, jade shadow recesses, restrained ruby catalyst marks and cyan airflow channels.
- Final geometry uses a segmented circlet/halo, layered cuirass, pauldrons, vambraces, belt/hip plates, knees, greaves and one complete pair of articulated feather wings anchored to the chestplate.
- Wings use root -> forewing -> fan/tip hierarchy and animate as one mirrored pair: folded on ground, broad stable glide while falling and a readable symmetric beat during creative flight.
- Aimed Hurricane VFX is the existing cyan/gold/white funnel: 20-tick readable telegraph, 80-tick suction/orbit/lift and 20-tick release/fade. Gameplay remains server-owned; particles remain local and distance-capped.
- Forge UI extends vanilla slot language with a compact centered dark-jade panel, ivory slots, gold border and a directional arrow; it does not obscure the player inventory or rely on color alone.

## Validation

- [x] Java 17 main/client compilation and full build pass.
- [x] All 684 asset/data JSON files parse. The 16 new item IDs have item models, 32px textures and EN/RU/UK/DE/PL names; all 14 equipment recipes resolve only as `opusvsexe:parthenon_forging`.
- [x] Visual asset QA inspected the deterministic 1024x256 contact sheet. The final rig contains independent humanoid anchors, one mirrored feather-wing pair and named halo/wing pivots; the 128px atlas preserves the ivory/gold/cyan/ruby hierarchy.
- [x] Dedicated startup/reload loaded eight recipe types (the seven vanilla types plus `parthenon_forging`) and 1,277 advancements without recipe, registry or mixin failure.
- [x] The development-only dedicated contract probe found all 14 forge recipes and the separately vanilla-craftable station; proved the Ruby Halo Shard remainder, iron/diamond-equivalent stats, Protection III, Efficiency IV, exact `20 -> 30 -> 20` max-health transition, chest flight grant/revoke, first aimed Hurricane spawn and second-cast cooldown rejection.
- [x] Dedicated mob-loot QA killed isolated Sunfinches/Cloud Grazers and observed `sunfeather` and `cloud_fleece` item entities. Every temporary entity/item and force-load was removed.
- [x] The final full build passed. In-game renderer/window inspection remains manual on this host because the existing Wayland GLFW boundary prevents client-window creation; client Java and renderer registration compile successfully.
