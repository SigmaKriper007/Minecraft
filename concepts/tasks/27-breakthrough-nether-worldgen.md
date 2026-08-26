# Task 27 — Breakthrough in Space Nether worldgen

## Scope

- Generate complete Breakthrough in Space rifts throughout the Nether at varied heights, including open caverns/sky and buried terrain.
- Keep density noticeable but exploratory rather than ubiquitous: approximately one placement attempt per 24 chunks.
- Reject every candidate whose actual rift/frame/chamber cells intersect lava, another fluid, bedrock or a block entity.
- Build a recognizable portal with a 3×5 active core, obsidian/crying-obsidian frame, basalt/magma spines and a one-block discovery pocket on both faces.
- Remember the entry dimension so a Nether-generated portal returns the player to the Nether rather than always to the Overworld.

## QA

- Configured/placed feature JSON parses and the feature is injected only into Nether biomes.
- Sample fresh Nether chunks with a deterministic test seed; find both buried and open placements and confirm no generated target overlaps lava.
- Full build and dedicated fresh-world generation smoke test.

## Result

- Completed. The configured feature is injected into all five vanilla Nether biomes and attempts placement once per 24 chunks across Y=6…121.
- The generated rift contains a 3×5 active portal core, obsidian/crying-obsidian/gilded frame, basalt/magma spines and two carved discovery faces. Either horizontal orientation is supported.
- Every target cell is preflighted; lava/other fluids, bedrock or a block entity cancel the whole placement before any block changes.
- Dedicated generation QA force-loaded 256 fresh distant Nether chunks. A sampled 8×8 quadrant contained a complete 15-core portal at x=200036…200038, z=200039, y=80…84 with both faces open to a cavern. Reversible block counting restored all 15 portal cells, then force-load and the QA scoreboard were removed.
- Portal entry now stores both position and dimension; exiting the Fire Realm returns Nether entrants to the Nether, with Overworld spawn retained only as a missing-state fallback.
- Resource JSON parsing, full build and existing-world dedicated startup passed.
