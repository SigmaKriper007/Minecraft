# Task 21 — Opus katana ability entities

## Shared visual language

Dedicated `katana_slash` effect entity, not a particle-only placeholder. Seven articulated arc segments form a tapered crescent with a bright cutting edge. Server owns movement, collision, damage and cooldown; client renders the synced variant.

- Opus: dark violet body, purple energy, cyan edge, white impact hotspot.
- Refined: narrower cyan-violet air blades; many low-cost outward slashes form a radial barrage.
- Gold: black-gold shell, saturated gold edge and ivory core; a large ground-following slash leaves a rising rupture column.

## Mechanics

- **Katana-OP / Sword Slay:** one forward slash, 32-block reach, 14 damage, exact 50-tick (2.5 s) cooldown.
- **Refined Katana / Air-Slash Hail:** 12 radial slashes around the player, 8 damage each, per-entity hit-once, 120-tick cooldown.
- **Golden Katana / Heaven-Earth Rupture:** ground-following golden slash for 18 blocks; corridor damage plus final launch shockwave, 100-tick cooldown.
- Slash entities never damage their owner, do not break blocks and are culled after their bounded lifetime.

## QA

- Entity registration and dedicated-server spawn.
- Variant sync/save, owner attribution, block collision and hit-once set.
- Client model/texture/animation resource loading.
- Exact cooldown constants, JSON/PNG validation, build and server smoke test.

## Result — 2026-08-25

- All three item abilities now spawn dedicated server-authoritative slash entities; the generic radial shockwave was removed.
- Added variant-synced GeckoLib model, looping travel animation, emissive renderer and three original 64×64 pixel palettes.
- EN/RU/UK/PL/DE tooltips describe each ability and cooldown.
- Build and resource JSON validation passed. Dedicated server registration/spawn/movement/hit-once damage passed: a configured 7-damage slash reduced a 10-health cow to 3.
- Client initialized the mod and renderer registry; final window/resource rendering could not run because the host Wayland compositor rejected GLFW input-focus setup.
