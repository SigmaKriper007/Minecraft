# Task 34 — Angel Boy encounter

Status: completed and runtime-tested

## Reference translation

- Preserve the supplied concept's readable identity: youthful red-haired humanoid, white asymmetric drapery, restrained gold bands, ruby sternum gem, segmented halo and three complete wing pairs.
- Gameplay scale is 1.35 players; physical hull is approximately `0.9 x 2.45` blocks. Six decorative wings widen the silhouette without widening collision.
- Each upper/middle/lower wing has an independent left/right root, forewing and feather fan. Pair pivots occupy distinct back heights and never share/deform each other's chains.
- Paradise palette only: warm skin, ivory cloth, honey gold, cool feather shadows, sky-cyan wind and a ruby reserved for phase-three danger.

## Encounter contract

- The center lodestone becomes an `angel_dais` block entity. It summons one persistent dormant Angel Boy when a player enters the central court and no living boss is nearby.
- Dormant Angel Boy is neutral and damage-immune. The first attempted non-bypass hit deals zero damage, binds that living attacker as challenger, applies a safe approximately 25-block launch, flares all six wings and begins combat.
- Arena anchor is persisted. During combat a visible wind boundary pushes players back toward the architectural court instead of placing/destructively removing blocks.
- Baseline: 420 HP, armor 14, attack 12, ground speed 0.34, follow range 48 and full knockback resistance.
- Boss bar names the current phase. Phase thresholds are 70% and 35%; phase two+ uses controlled flight and phase three overcharges the ruby/halo.
- Leashing never chases indefinitely: outside the court the boss returns toward its dais and regenerates rather than griefing terrain.

## Attack entities and timing

All hit membership, damage and movement are server-owned. Client entities render local range-capped VFX.

- `halo_lance`: 25-tick growing gold/cyan ground marker, then a four-tick narrow vertical spear. Phase count 3/5/7; 10 damage.
- `seraphic_crosswind`: two perpendicular 22-block wind walls sweep through the court for 48 ticks. Their stable line silhouettes leave readable quadrants; each target is hit once for 8 damage and bounded inward knockback.
- `seraphic_feather`: deterministic fan of 7/9/11 physical feathers. Each curves once after 15 ticks, deals 7 damage once and expires without block damage.
- `wingbeat_ring`: three separate expanding annuli. A target is vulnerable only near the low ring plane, making jumping/vertical positioning the counterplay; 9 damage once per ring.
- `angel_ascension`: 20-tick circle telegraph, 30-tick suction/orbit/lift, then captured targets are thrown inward/up rather than off-island; 6 damage on release.
- `ruby_descent`: phase-three-only growing red/gold target for 35 ticks, then the boss dives to the center and creates a six-block impact plus recoverable shockwave opening; 15 damage with bounded knockback.

## VFX specification

- Halo Lance — purpose danger/position test; concentric octagonal marker -> thin vertical core; cyan/gold/white; build 25t, impact 4t, fade 7t; client-local dust/end-rod particles.
- Crosswind — purpose moving-lane danger; long translucent feather-edged wall; cyan body, gold boundary, white hotspot; linear sweep; low particle density.
- Feather Verdict — purpose directional dodge; readable gold-white feather silhouettes with sampled cyan trail; one deterministic mid-flight curve.
- Wingbeat — purpose jump timing; expanding low ring with white leading edge and cyan wake; one entity per pulse avoids per-particle networking.
- Ascension — purpose capture warning/control; circular glyph -> spiral funnel -> inward release; reuses Paradise wind language but has boss-safe inward release.
- Ruby Descent — purpose phase-three lethal warning; red reserved target core, gold outer timer and white impact; large but short impact flash, no block damage.

## Progression boundaries

- Boss death marks its nearby dais defeated and drops one central archive trophy plus guaranteed `seraphic_pinions` and `ruby_halo_shard` through an entity loot table.
- Task 34 establishes the defeat state and drops. The four-pinions-plus-shard resurrection interaction remains task 36 with final progression integration.
- Global archive expands from 17 to 18 current mobs; Paradise archive expands from 3 to 4.

## Validation

- [x] Java 17 main/client compilation and full build pass.
- [x] All 633 asset/data JSON files parse. The 42-bone boss rig has six independent wing roots; every boss and shared effect animation bone resolves in all seven rigs.
- [x] Both 66,272-block structure NBT copies contain exactly one `angel_dais` at `[48, 33, 48]` and share SHA-256 `2c1b06f530cc797b67cc753b12ba99d4b3b78f09e6f6cbfe7dfbd852ea5b8bce`.
- [x] Dedicated QA proved first-hit health `420 -> 420`, `Awakened: 1b` and challenger motion `[2.25, 0.82, 0]`; phase thresholds produced Phase 2 at 280 HP and Phase 3 plus flight at 140 HP.
- [x] Isolated 100-HP targets ended at 90/92/93/91/94/85 HP for Lance/Crosswind/Feather/Ring/Ascension/Descent. Ascension also changed target motion, every effect expired and the control gold block survived all six.
- [x] Boss death changed dais `Defeated: 0b -> 1b`, then retained `1b` across a full save/stop/start. It dropped exact stacks of four pinions, one shard and one boss trophy.
- [x] All QA entities/items/platforms, both temporary dais blocks and the temporary force-load were removed before final shutdown.
