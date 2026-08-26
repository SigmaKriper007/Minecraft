# Task 20 — Haiku material rules and Titan miniboss progression

## Design

All Haiku chassis are Opus-memory machines: fire/lava cannot damage them, and non-Opus impacts disperse across their memory lattice. The shared rule belongs in `HaikuMob`; Omega retains only its phase-specific multiplier and boss feedback.

### Haiku-4: Resonance Guard

- Trigger: target within 12 blocks, 9 s cooldown.
- Timeline: 18-tick amber convergence telegraph → radial repulse at impact → 32-tick guard window.
- Gameplay: modest damage/knockback; incoming valid Opus damage is reduced during the guard window.
- Readability: stable ring telegraph, amber/cyan sparks, impact sound; no per-particle network packets.

### Haiku-5 miniboss

- Persistent boss bar while tracked by players.
- **Titan Quake:** 26-tick planted wind-up, impact on tick 18; large ground shockwave, vertical launch, strongest near the Titan.
- **Amber Rush:** 16-tick line telegraph, then a controlled forward burst; entities in the swept corridor take damage/knockback once.
- Scheduler is server authoritative, mutually exclusive, distance-aware and cooldown bounded. Synced action id/ticks drive GeckoLib presentation.
- Drops 4–7 Titan Plates (Looting adds up to 2) plus existing Core Opus. Plates are the required structural component for Opus armor.

### Progression and resurrection

- Titan placements: Haiku Citadel remains guaranteed; Frontier Fortress gains a rarer/guarded Titan placement.
- Opus armor recipes use Titan Plates with Core/Resonant Opus so the miniboss is part of progression.
- Using an Altar Heart item on an activated altar restores it only if no living Omega is within the arena; item is consumed outside creative mode.

## QA

- Non-Opus melee/projectile/environment damage is rejected for all Haiku; bypass/admin sources remain usable for maintenance.
- Opus weapon tag damage succeeds; fire/lava never damages Haiku.
- Ability damage excludes allied Haiku/Omega entities.
- Cooldowns and active action survive normal entity save/load.
- Build, JSON parse, NBT validation and dedicated-server startup smoke test.

## Result — 2026-08-25

- Shared material rules implemented in `HaikuMob`; Omega now reuses the shared gate and keeps only phase-specific damage behavior.
- Resonance Guard, Titan Quake and Amber Rush implemented with persistent cooldown/action state.
- Titan Plate progression, four armor recipes and four Opus tools completed with original pixel assets.
- Frontier Fortress regenerated and validated with its courtyard Titan; Citadel placement and Omega summon remain intact.
- Activated altar restoration implemented with an alive-Omega safety check.
- QA passed: build, all JSON parse, NBT toolkit/sync, dedicated server startup, damage gate and loot drop. Client visual/animation, live ability targeting and multiplayer player-held item tests remain.
- Follow-on bug fix: all 15 memory fragments now play the canonical history rather than returning corrupted data after fragment 10; localized in EN/RU/UK/PL/DE.
