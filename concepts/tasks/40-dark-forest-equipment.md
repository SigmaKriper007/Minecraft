# Task 40 — Dark Forest equipment

Status: complete and runtime-tested

## Progression contract

- Add `Briarweave` as a renewable composite of Shade Silk, Moonwing Membrane, Gloomwood and vanilla iron. Its four-piece armor uses exact iron defense/durability and grants poison resistance only as a complete set.
- Dark Forest Vestments upgrade the corresponding Briarweave pieces. The four recipes consume the boss bundle exactly once across the set: one Rootbound Eye per piece and the Moonflower Heart on the chestplate.
- Vestments use diamond defense/durability with 2 toughness. A full set grants Night Vision, Haste I, Speed II, Strength I and Dolphin's Grace II; partial sets retain no granted effects.
- Add sword, pickaxe, axe, shovel and hoe using gold-equivalent mining level/speed, diamond durability and intrinsic Unbreaking I. Any one actively held Dark Forest tool grants Haste III and reverts cleanly to armor Haste I or no Haste when released.

## Teleport contract

- The existing V binding sends a dedicated edge-triggered intent packet when a complete Vestments set is equipped. No new permanent HUD is added.
- The server validates the set, player state and persisted exact `50`-tick cooldown, then raycasts the authoritative look vector up to 32 blocks.
- Destination search requires a sturdy floor, two-block player clearance, empty fluids, world-border containment and collision-free placement. Search steps backward from the aim point and never clips through the hit surface.
- Success uses restrained violet/cyan compression and arrival particles, Enderman sounds, zero fall distance, vanilla chestplate cooldown rendering and localized action-bar feedback. Failure consumes no cooldown.

## Visual contract

- Briarweave: matte near-black woven cloth, moss binding, plum thorn plates and small cyan stitches. Geometry uses a fitted hood/mask, layered chest wrap, thorn pauldrons, bracers, hip panels, knees and wrapped boots.
- Vestments: heavier violet-black bark plate over moss cloth, short antler crown, moonflower clasp, segmented shoulders/gauntlets, root belt, thigh panels, knees and greaves. It remains organic and contains no Opus machinery, Paradise gold or Fire materials.
- Both sets use the existing custom player armor-layer architecture with separate `128x128` hard-edged atlases. Fourteen inventory/tool icons use `32x32` transparent pixel art with one shared dark/plum/moss/cyan palette.

## Focused validation

- Compile after integration and run one final full build.
- One `-Dopus.qa.only=40` dedicated probe: all 14 recipes, exact armor/tool tiers, intrinsic Unbreaking I, full/partial effect cleanup, Haste III hand transition, first safe aimed teleport, second-cast cooldown rejection and persisted cooldown NBT.
- Parse only Task 40 resources and validate PNG dimensions/palette plus both armor model part contracts.

## Recorded evidence

- Java 17 main/client compilation and the final Gradle build pass.
- Focused `-Dopus.qa.only=40` dedicated QA passed: all 14 crafting recipes, exact iron/diamond armor and tool tier contracts, intrinsic Unbreaking I, Briarweave poison removal, all five Vestments effects and partial-set cleanup, held-tool Haste III transition, safe 32-block aimed teleport, second-cast rejection and exact persisted 50-tick cooldown.
- The targeted resource audit passed 14 item models, 14 recipe JSONs, five locales, all 32px icons, both 128px armor atlases and the custom armor-part contract. The deterministic contact sheet was inspected at nearest-neighbor scale.
- The disposable Task 40 server world was removed after clean shutdown; the normal world name was restored and no user world was touched.
