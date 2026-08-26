# Task 43 — Japanese settlement and warriors

Status: complete.

## Locked contract

- Add a real `japanese_settlement` Overworld structure with a layered castle, dojo, homes, torii, cherry landscape, river and arched bridge.
- Guarantee eight Black Ninja and four Samurai while reserving the central court for Task 44's Young Samurai.
- Black Ninja use a player-scale hitbox, 20 health, high speed, ordinary katana and a telegraphed smoke-step.
- Samurai render and collide at `1.3x` player scale, have 36 health, heavier damage, long katana and a telegraphed lunge.
- Add survival recipes, dedicated item models/textures and player right-click techniques for ordinary and long katana.
- Add original humanoid models/textures with item-in-hand rendering and authored idle/walk/attack/action poses.
- Add common settlement loot, two trophies, a Japanese archive advancement and five-locales.
- Focused Task 43 QA covers attributes/hitboxes, equipment, action state/damage, item techniques, recipes, trophies and structure/entity registries.

## Verification

- Focused dedicated QA passed structure registration, exact stat/hitbox contracts, fixed weapons, smoke-step/lunge state and damage, both recipes and trophy bindings.
- Live placement exposed embedded façade spawns; all twelve anchors were moved to audited floor/air columns.
- Final live placement retained exactly eight Black Ninja and four Samurai after the settlement had ticked.
- Targeted resource audit passed the `97x36x97` NBT, warrior/item art, 25-entry trophy archive and all new JSON.
