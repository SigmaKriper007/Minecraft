# Task 23 — hidden EXO punch shockwave

## Design

Every pilotable EXO gains a separate **B-key Resonance Punch**. It is deliberately absent from the four-slot ability HUD and does not replace normal left-click melee or any frame-specific ability.

- Server validates the pilot, 60-tick cooldown and tier-scaled energy cost.
- A dedicated `punch_shockwave` entity expands in front of the fist for 12 ticks, damages each target once and attributes damage to the EXO chassis.
- Radius and damage scale with EXO tier while remaining bounded; the wave never breaks blocks or hits its pilot/other EXO frames.
- Synced punch state drives a dedicated full-body punch animation on both standard EXO and EXO+ rigs.
- Visual design follows `concepts/punch_shockwave.jpg`: saturated blue pressure shell, broken white radial arcs and a compact bright impact core, translated into original pixel/GeckoLib assets.

## QA

- B is independent from displayed F/G/H/J abilities and ignored outside a piloted living EXO.
- Repeated packets cannot bypass energy/cooldown checks.
- Effect entity registration, save/sync, hit-once damage, EXO owner attribution and expiry.
- Build, resource validation, dedicated-server entity/damage smoke test and client bootstrap.

## Result — 2026-08-25

- B-key mapping, hidden packet receiver and shared `tryResonancePunch` path implemented for EXO-1 through EXO-6.
- Punch state is synced into a new 0.7-second animation in both standard and EXO+ animation sets.
- Dedicated tier-scaled wave entity, original radial model/texture/animation and fullbright renderer added.
- EN/RU/UK/PL/DE input and cooldown strings added. All JSON, full build and dedicated startup passed.
- Runtime entity QA passed: a configured 7-damage wave reduced a 10-health cow to 3 exactly once and discarded after its 12-tick lifetime. Final live-pilot B input remains manual because client window creation is blocked by the host Wayland focus limitation.
