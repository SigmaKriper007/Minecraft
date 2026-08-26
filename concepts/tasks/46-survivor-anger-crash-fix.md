# Task 46 — Survivor anger crash fix

Status: complete.

## Cause

`SurvivorEntity.canAttack(Player)` called `NeutralMob.isAngryAt(Player)`. Vanilla `isAngryAt` validates through the entity's overridden `canAttack`, creating an unbounded `canAttack → isAngryAt → canAttack` recursion whenever a Survivor evaluated a player target. Both integrated server and client terminated with `StackOverflowError`.

## Fix

- Player attack eligibility now reads the persisted anger UUID directly.
- Universal anger remains supported when the anger timer is active and the target UUID is intentionally empty.
- Haiku targeting, exact retreat behavior and vanilla base attack validation remain unchanged.
- Focused Survivor QA now calls both `canAttack` and vanilla `isAngryAt` for the angry player and rejects an unrelated player, providing a direct recursion regression test.

## Verification

- Focused Task 42 dedicated QA passed the player-anger regression and all existing Survivor contracts.
- Final Gradle build passed.
