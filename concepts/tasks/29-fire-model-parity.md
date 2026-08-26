# Task 29 — Fire model parity

## Goal

Close the two remaining Fire Realm model/physics discrepancies without introducing duplicate visual assets.

## Demonic Trident

- Render the inventory, GUI, dropped and held item through GeckoLib.
- Reuse `geo/fire/trident.geo.json`, `textures/fire/entity/trident.png` and the matching animation resource used by the thrown projectile.
- Keep item display transforms in the `builtin/entity` item model; do not maintain a second cuboid approximation.
- Give the held/inventory core a restrained pulse while the projectile alone owns its flight/return animation state.

## Diablo hitbox

- Treat the body, arms and horns as the combat silhouette; wings remain decorative.
- Source geometry audit: arm span is 20.6 model units (`1.2875` blocks), horn height is 44 units (`2.75` blocks).
- Use an entity size of `1.30 x 2.75` blocks.
- Do not use the full wing span (`63.6 / 16 = 3.975` blocks) as collision width.

## Acceptance

- Item and projectile model adapters resolve the exact same geo and texture constants.
- Item model uses `builtin/entity` and has explicit left/right hand, GUI, ground and fixed transforms.
- Diablo registry dimensions match the audited combat silhouette.
- Client and main sources compile, resource JSON parses, full build and dedicated startup pass.
