# Task 39 — Mossbound Enderman encounter

Status: complete

## Scope

- Turn the Moon Fountain Core into a persistent arena controller. On first nearby player load it creates exactly one dormant Mossbound Enderman beside the fountain; after victory it records defeat and does not silently respawn the boss.
- Add a fountain-leashed, non-griefing GeckoLib boss with an original blocky rig, scale-matched collision, readable three-phase combat, spawn egg, progression loot and archive trophy.
- Reserve resurrection interaction for a later slice, but expose the exact Moonflower Heart and four Rootbound Eyes required by that ritual.

## Boss contract

- Physical scale: `2.35 x 5.25`; 480 HP, 12 armor, 14 base attack, 0.32 movement, 64 follow range and full knockback resistance.
- Dormant until damaged. The waking hit deals zero damage, starts a 32-tick root/void awakening and targets the challenger.
- Arena anchor persists in NBT. The boss stays within 22 blocks of its fountain, never edits terrain and returns through a marked safe teleport if displaced.
- Phase thresholds are exact: Sleeping Grove above 65%, Horned Eclipse at or below 65%, Last Bloom at or below 30%.
- Projectile avoidance uses visible departure/arrival traces and a bounded cooldown rather than invisible immunity.
- Phase three periodically opens the single back flower for a readable weak-point window; damage is multiplied by `1.5` only while open.

## Actions

- Antler Sweep: 22-tick frontal telegraph, one 12-damage arc and knockback.
- Root Snare: ground marker, 24-tick warning, one 8-damage root burst with strong temporary Slowness.
- Marked Step: visible destination marker, then safe teleport and one 10-damage arrival pulse.
- Moonwell Orbs: aimed finite projectiles, 7 damage once per target.
- Bloomfall: three warning circles in phase two and five in phase three; 10 damage once when each blooms.
- Echo Double: two false silhouettes warn before independent 9-damage pulses.
- Eclipse Rush: phase-three line telegraph followed by a fast finite 14-damage pass.

## Visual contract

- Reference sheet: `concepts/dark_forest/task39-mossbound-enderman-concept.png` (reference only, generated with built-in image generation).
- Silhouette: extreme hunch, long segmented limbs and fingers, broad branching horns, root wraps, moss shoulder mass and exactly one independently animated back flower.
- Palette: near-black violet bark, plum shadow, deep moss, pale ivory petals and restricted lunar cyan eyes/flower core. Shipping textures remain hard-edged pixel art with no antialiasing.
- Animation set: dormant, awaken, idle, walk, antler sweep, root cast, marked step, orb cast, bloomfall, echo double, eclipse rush, phase shift, flower open and death.

## Focused validation

- Compile main/client source and run one final build.
- One development dedicated-server contract run: core single-spawn/defeat state, dormant zero-hit awakening, exact stats/hitbox/phase thresholds, flower weak point, representative finite hit-once attack damage, safe arena return, progression loot and trophy mapping.
- Parse only the new/changed Task 39 resource files and verify the boss animation bones exist in its geometry.

## Recorded evidence

- Java 17 main/client compilation and the final Gradle build pass.
- Focused `-Dopus.qa.only=39` dedicated QA passed: one fountain-owned dormant spawn, persisted defeat suppression, zero-damage waking hit, exact hitbox/attributes/phases, `1.5x` flower weak point, finite hit-once Root Snare, safe arena teleport, exact `1+4` progression loot, trophy mapping and `doMobLoot` suppression.
- The changed-resource audit passed `21` JSON files and `10` RGBA PNGs. The boss has `46` named bones and `14` animations with no missing animation references; all six attack geometries have valid roots. The central archive now has `22` criteria.
- The disposable Task 39 world was removed after clean shutdown; the normal server world and server properties were restored.
