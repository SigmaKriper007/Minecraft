# Task 26 — Four Veins armor migration

## Interpretation

- The redesigned Ember armor is the retained high-tier fire set and moves into The Four Veins creative/progression line.
- The former Fire/Vein armor is replaced by a new Vein Crust baseline set rather than kept as a parallel duplicate.
- The Ember Depths creative section and every Ember-only item form stop being obtainable/registered; the four retained Ember armor IDs remain stable for existing saves. Block/entity IDs remain registered without item forms so old worlds and the separately requested Cinder Slime animation stay intact.
- Vein Crust defense is calibrated to iron armor plus Protection I, and a full set grants Fire Resistance.
- Retained Ember armor recipes become upgrades from the Vein Crust baseline, satisfying the basalt/Vein-Crust upgrade requirement.

## Implementation plan

- Audit common/client registration edges before disabling Ember Depths content.
- Register the retained Ember armor from the Four Veins line without forcing initialization of discarded Ember registries.
- Replace old Fire armor exposure with Vein Crust armor, including original models/textures, recipes and translations.
- Migrate Ember armor recipes to Vein Crust upgrades and remove old direct Fire armor recipes from the active data pack.
- Validate startup registry safety, recipes, JSON/PNG resources and full build.

## Compatibility rule

Keep the four `opusvsexe:ember_*` armor IDs stable. Removed item content is disabled at registration/progression boundaries; orphaned compatibility assets may remain until a later pack-size cleanup.

## Result

- Completed. The Ember Depths creative tab class was removed and `EmberItems` now registers only the four retained armor IDs. Dedicated commands confirmed retired Essence, Trident and Cinder block-item IDs are unknown while both retained armor tiers resolve.
- Existing `fire_*` armor IDs now represent the Vein Crust baseline: iron defense/durability, zero toughness, intrinsic Protection I, Magma Crust repair/crafting, no wings/flight/attack, and full-set Fire Resistance only.
- The retained detailed Ember set is exposed in The Four Veins tab and each piece upgrades from its matching Vein Crust piece plus four Vein Essences.
- The old Flame Demon loot references were redirected to active Four Veins items. Blocks/entities remain registered without item forms for old-world safety and Cinder Slime continuity.
- Original Vein Crust inventory palettes and revised hornless/ridged 3D helmet geometry were generated and visually inspected. All 540 JSON resources parsed, clean build passed, stale tab bytecode was absent, and the existing dedicated world reached `Done`.
- Remaining manual QA: rendered armor alignment, survival equip/fire-resistance transition and confirmation of intrinsic Protection I after entering a player inventory.
