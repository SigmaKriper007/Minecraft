# Task 36 — Angel Boy resurrection and Paradise integration

Status: complete — build/resource/NBT/dedicated-runtime tested 2026-08-25

## Ritual contract

- The Parthenon center gains four unbreakable `seraphic_reliquary` blocks at the exact cardinal offsets four blocks from the existing `angel_dais`.
- Right-clicking an empty reliquary with Seraphic Pinions inserts exactly one. Sneak-right-clicking a charged reliquary with an empty hand returns its pinion. The `charged` block state changes silhouette/light as a non-color-only status cue.
- Once the original Angel Boy is defeated, right-clicking the dais with one Ruby Halo Shard validates the defeated state, all four exact reliquaries, the absence of a living Angel Boy within the full arena and the absence of an active ritual.
- Validation is atomic: no item is consumed on a missing pinion, undefeated arena, duplicate boss or active ritual. After success, four pinions and one shard are consumed together.
- Already-generated islands backfill missing cardinal reliquaries only when their reserved sockets remain air; player-built blocks are never overwritten.
- The ritual persists its timer and initiating player UUID. At its peak it creates one dormant, persistent Angel Boy at the existing arena anchor and resets the dais defeat state. A spawn failure or late duplicate abort restores all four pinions to their reliquaries and returns the shard as one item entity.

## Interaction and VFX language

- Reliquary feedback uses vanilla interaction only: hand animation, localized action-bar state, amethyst/chime sounds and a raised feather-shaped bowl. No separate screen is needed for a one-slot altar.
- Ritual duration is 80 ticks: 0–39 convergence from four cyan/gold cardinal streams, 40–59 tightening halo rings, 60 white/ruby judgement flash and boss creation, 61–79 short fade.
- Gameplay and consumption remain server-authoritative. Server particle bursts are bounded to the arena and occur at low/medium density; red appears only at the ruby peak.
- Failure feedback is compact localized action-bar text plus a low beacon/amethyst sound. Success grants the initiating player the `chronicles/reopen_heavens` advancement.

## Progression integration

- Paradise cache loot replaces generic feathers with common `sunfeather`/`cloud_fleece`/Paradise Fruit breadcrumbs and Aerie Bronze support materials, but never contains Seraphic Pinions, Ruby Halo Shards, final armor or final tools.
- The existing guaranteed boss drop remains four pinions plus one shard, so one complete victory funds exactly one resurrection while the shard's forge-catalyst behavior remains non-consuming.
- The Paradise archive remains the four-species trophy challenge; the new resurrection advancement is its child and proves repeatable boss progression rather than first discovery.

## Validation

- Java 17 main/client compilation and full build.
- Parse all JSON; verify both synchronized structure NBT copies contain one dais and four exact uncharged reliquaries.
- Dedicated QA must prove all failure paths preserve ingredients, success consumes `4+1`, exactly one dormant boss appears, advancement state can be awarded, duplicate activation is rejected and the dais returns to undefeated state.
- Save/restart QA must prove an active ritual resumes or a completed ritual remains coherent without duplicated boss/items.
- Remove all temporary QA entities/items/blocks/force-loads and stop the server.

## Recorded evidence

- Java 17 main/client compilation and the full Gradle build passed.
- All 688 resource JSON files parsed; EN/RU/UK/PL/DE contain the complete ritual key set.
- Both Paradise structure copies are byte-identical (`SHA-256 8a7199343bdbf780a99f3daff18402d337784083853ad26a9c73501b89082966`) and contain exactly four uncharged cardinal reliquaries at `(±4, 0)` / `(0, ±4)` around the dais.
- Dedicated Task 36 QA passed atomic missing-offering and living-boss rejection, exact `4+1` consumption, active-ritual NBT round-trip, one dormant boss, advancement completion state, timer settlement and exact late-conflict refund.
- QA removed its temporary boss, shard item, dais, four reliquaries and their supports; the server stopped normally after saving every dimension.
