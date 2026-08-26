# Task 41 — Dark Forest resurrection and integration

Status: complete and runtime-tested

## Ritual contract

- Replace the four reserved cardinal Moon Fountain sockets with unbreakable `rootbound_pedestal` blocks at exact offsets `(0,+1,±7)` and `(±7,+1,0)` from the Core. Existing fountains backfill only air or the original amethyst-cluster placeholder, never player-built blocks.
- An empty pedestal accepts exactly one Rootbound Eye. Sneak-use with an empty hand retrieves it before activation. Charged state changes both geometry/light and texture so status is not color-only.
- After the first victory, using one Moonflower Heart on the Core atomically validates the defeated state, four charged pedestals, no living Mossbound Enderman within the arena and no active ritual. Failure consumes nothing.
- Success consumes exactly four Eyes and one Heart together, then runs a persisted 100-tick ritual. Tick 72 creates exactly one dormant persistent boss at the fountain and resets defeated state.
- A late duplicate or spawn failure aborts once, recharges all four pedestals and returns one Heart as an item entity. The active timer and initiating player UUID survive save/load.

## Presentation and progression

- The ritual converges moss/violet streams from the four Eyes, tightens a cyan moonflower spiral around the Core, flashes once at spawn and fades without block damage or a new screen/HUD.
- Success grants the localized `Return of the Last Bloom` challenge beneath the Dark Forest archive.
- Fountain caches provide only common discovery support: Shade Silk, Moonwing Membrane, Moonflowers, Gloomwood and conservative vanilla materials. They never provide Rootbound Eyes, Moonflower Hearts, final armor/tools or a complete resurrection bundle.
- All interaction, validation, consumption, spawning, refund and advancement authority remains on the server. Five shipped locales cover every new block, message and advancement key.

## Focused validation

- Dedicated Task 41 QA proves non-consuming failure paths, exact `4+1` consumption, active NBT round-trip, one dormant boss, timer settlement, advancement validity and exact late-conflict refund.
- Targeted resource/NBT audit proves four exact uncharged structure pedestals, synchronized pack/resource templates, cache exclusion rules, JSON validity, model variants and five-locale coverage.
- Compile changed main/client code and run one final full build. Prior Dark Forest runtime evidence remains valid and is not repeated unnecessarily.

## Recorded evidence

- Focused `-Dopus.qa.only=41` dedicated QA passed safe legacy placeholder backfill, non-overwrite of unrelated blocks, pedestal insertion/retrieval, non-consuming missing-offering/living-boss/active-ritual paths, exact `4+1` consumption, active NBT round-trip, one dormant boss, advancement validity, timer settlement and exact late-conflict refund.
- The runtime probe exposed and fixed a reload boundary: a persisted boss UUID is now treated as authoritative while its entity chunk is temporarily unresolved, preventing a second boss from spawning during entity-index attachment.
- Targeted resources passed: seven JSON contracts, two restrained 32px pedestal states, complete five-locale keys, cache exclusion rules and four exact uncharged pedestal positions in byte-identical `49x22x49` pack/resource NBT copies (`SHA-256 b0b03aaabe029af05bc4a0f91f27e9bde6a65b71b28a4124f02f5dea92412bb2`).
- Java 17 main/client compilation and the final full build pass. The disposable Task 41 world was removed after clean shutdown and the normal world setting was restored.
