# PROJECT WORKFLOW INSTRUCTIONS

## Communication
- Respond concisely. No filler/repetition/unrelated info. Prioritize actionable info. Optimize for token efficiency.

## Concepts / Plans / TODOs / Ideas / Prompts
- ALL project concepts, plans, TODOs, ideas, prompts MUST be saved in `concepts/` (source of truth for ideas/plans).
- Before creating new: check relevant files in `concepts/`, reuse, avoid duplicates.
- When creating/modifying a concept: update the file, preserve existing info unless obsolete, keep related ideas together, record decisions/changes, keep TODOs and plans up to date.
- Never rely only on chat history for important project info.

## Skills
- Before any task: check `skills/`, identify all relevant skills, follow them EXACTLY, apply throughout the task. Do not ignore relevant skill files.

## Lore
- `lore/lore.md` is authoritative. Before creating/modifying anything related to items, blocks, entities, weapons, armor, structures, dimensions, factions, characters, environments, mechanics, visual design, descriptions, names, narrative elements — read and follow `lore/lore.md`.
- All generated content MUST be consistent with lore. Do not invent lore contradicting it. New lore → record in `concepts/`, not silently into lore.
- **Parallel line «Fire Biom»**: authoritative docs in `lore/Fire Biom/` (LOR.md, DESIGN.md, PLAN.md). Self-contained storyline: does NOT follow main Haiku/EXO lore or visual standards; own lore+palette; code isolated under `com.opus.fire.*`.

## General workflow (every task)
1. Read relevant `skills/` files → 2. read `lore.md` when applicable → 3. check `concepts/` for related info → 4. plan → 5. execute → 6. update `concepts/` with decisions/TODOs/plans/ideas/prompts → 7. verify.

## Information preservation
Important project info must not live only in chat. Save to `concepts/`: design decisions, implementation plans, unfinished ideas, TODOs, prompts, technical approaches, rejected approaches (when relevant), constraints, future improvements. Keep knowledge organized, consistent, recoverable.

## Final output
- Be direct; no unnecessary commentary; don't repeat obvious info; only info relevant to current task.
- If a task requires modifying project files, modify them instead of only describing what to do.