# Task 25 — Fire gear foundation

## Scope

- Fix Ember/Fire bonus-health clamping by applying long-lived set effects only on transitions/expiry, not replacing Health Boost every ten ticks.
- Fill the newly granted +4 health once when a full set is equipped and cleanly clamp only when the set is removed.
- Mark Fire Realm and Ember-line items as fire resistant so dropped gear survives fire and lava.
- Add a complete Vein Fire tool set. Sword permanently restores Fire Aspect II; pickaxe converts fresh ore drops through the vanilla smelting recipe manager unless Silk Touch is present; axe/shovel/hoe complete the mining family.
- Add original 32×32 item textures, handheld models, survival recipes, creative-tab placement, item tags and EN/RU/UK/PL/DE names/tooltips.

## QA

- Health Boost is not removed/re-added every ten ticks and bonus health remains healable.
- Sword cannot permanently lose intrinsic Fire Aspect; pickaxe uses recipe outputs rather than hardcoded ore mappings.
- Every registered fire/ember item reports fire-resistant properties.
- Build, JSON/PNG/resource audit and dedicated registry/recipe smoke test.

## Result

- Completed. Both armor bonus managers use stable 24,000-tick effects and transition tracking, removing the repeated Health Boost replacement that clamped healing.
- All Fire/Ember registry paths now use `fireResistant`; a dedicated lava test kept Vein Essence alive while the dirt control burned.
- The full five-tool set is registered with recipes, creative/tag integration, original 32×32 textures and five-language names. Furnace Touch is queued until end-of-world-tick because Fabric's block-break `AFTER` hook precedes vanilla drop creation.
- `#c:ores` matched iron ore and the new recipe IDs parsed on a current-source dedicated server. All 536 resource JSON files parsed, PNG dimensions/modes passed and `./gradlew build` succeeded.
- Remaining manual QA: heal a survival player through the bonus hearts and mine several Fortune/Silk Touch ores in a rendered client.
