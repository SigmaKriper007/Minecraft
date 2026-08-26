# Task 42 — Survivor settlement

Status: complete.

## Locked contract

- Add one real `survivor_settlement` Overworld structure with detailed civic, market, forge, farm, watch and housing spaces.
- Guarantee twelve persistent survivor residents using twelve original 64x64 skin variants.
- Give survivors exactly 20 base health, player-sized collision, neutral persistent anger, melee combat, equipment pickup and visible held/armor rendering.
- Start retreat below 6 health and stop deliberate attacks while retreating.
- Avoid Haiku without an Opus-tagged weapon; acquire and attack Haiku when holding one.
- Recognize complete Opus, Fire/Ember, Parthenon and Dark Forest armor sets and apply their relevant passive/AI-usable abilities.
- Sell four distinct self-calibrating expedition compasses for Opus ruins, Paradise Island, Dark Forest and Moon Fountain destinations.
- Add settlement caches, discovery/archive advancements, a survivor trophy, five-locale names/tooltips and synchronized pack resources.
- Focused Task 42 QA covers attributes, anger, retreat, equipment, Haiku relation, armor ability, trades, trophy binding and registries.

## Verification

- Focused dedicated QA passed player-scale stats, 12-variant persistence, anger, the exact `<6 HP` retreat threshold, equipment acceptance, conditional Haiku damage, Dark Forest set abilities, four locator trades and trophy binding.
- A disposable server placed the complete `91x31x91` template and loaded exactly twelve resident entities without errors.
- Targeted audit passed 12 original skins, 128 animated compass frames, 23 trophy criteria/tag entries, all new JSON and synchronized structure NBT.
- Final build passed; the disposable world was removed from the workspace and `server.properties` was restored.
