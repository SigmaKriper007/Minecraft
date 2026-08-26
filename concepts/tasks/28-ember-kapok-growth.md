# Task 28 — Ember Kapok propagation

## Scope

- Add a dedicated Ember Kapok Sapling item/block dropped by Ember Kapok Crown leaves.
- Grow it only with Blaze Powder, with server-owned consumption and failure-safe space checks.
- On successful growth, convert a radius around the roots into native Ember Loam, Vein Crust and Cinder Block terrain.
- Create a readable hand-authored sapling texture/model and repair Ember Kapok Pod inventory rendering by using its dedicated item icon instead of the oriented block model.
- Add creative/tag integration and EN/RU/UK/PL/DE localization.

## QA

- Leaf loot JSON yields saplings naturally and preserves leaf harvesting with shears/Silk Touch.
- Failed growth does not consume powder; successful growth consumes one outside creative mode.
- JSON/PNG/build and dedicated registry/loot/startup checks.

## Result

- Completed. `ember_sapling` is a fire-resistant cross-model block/item in the Four Veins tab and both vanilla sapling tags.
- A successful Blaze Powder interaction grows a 10–14 block server-owned branching Kapok, consumes one powder outside creative, adds particles/sound and converts a five-block root radius to Ember Loam/Cinder over Vein Crust. Clearance failure returns before consumption or terrain edits.
- Ember Crown loot follows vanilla tool behavior: shears/Silk Touch return the leaf; ordinary destruction has a 10% sapling branch. Dedicated QA produced saplings from 100 ordinary breaks and exactly one leaf from a shears loot context.
- The first `alternative` condition spelling failed dedicated parsing and was replaced with the verified vanilla 1.20.1 `any_of`/`alternatives` schema; the clean reload had no loot errors.
- Kapok Pod inventory rendering now uses a dedicated generated-item model and a redesigned 32×32 three-chamber pixel icon instead of the orientation-dependent block model.
- Sapling/Pod textures were visually inspected; 548 resource JSON files parse and build/dedicated startup pass. Live-player Blaze Powder interaction remains manual QA.
