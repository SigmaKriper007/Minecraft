# Task 44 — Young Samurai

Status: complete.

## Locked contract

- Place exactly one persistent Young Samurai in the open court of every generated Japanese Settlement.
- Follow `young_samurai.jpg` with a black floral kimono, red edging, purple bindings, elaborate hair ornaments, two scabbards and a blood-red long-katana silhouette.
- Use a `0.66 x 1.98` hitbox, 360 health, 12 armor, 14 attack damage, high movement speed and a fixed Long Katana.
- Phase one uses a 48-tick recovery. At half health, phase two gains a continuous red/violet aura and an exact 24-tick recovery, doubling ability frequency.
- Implement Crimson Draw, Crescent Sweep, Rising Knee, Lotus Barrage and Flash Step as telegraphed sword/taijutsu actions with authored poses and bounded non-griefing effects.
- Every projectile hit is evaded; all other incoming damage has an exact `30%` teleport-dodge roll.
- Add boss loot, the unique Bloodflower Crest trophy, archive progression, five locales, original texture/model art and a creative spawn egg.

## Verification

- Main and client compilation passed.
- Focused dedicated QA passed attributes, hitbox, fixed weapon, two-phase aura/cadence, action state and damage, exact dodge boundary, projectile guarantee, trophy binding and `doMobLoot` suppression.
- Live `/place structure` verification retained one 360-health phase-one Young Samurai with the Long Katana.
- The generated NBT contains 13 persistent settlement entities: `8` Black Ninja, `4` Samurai and `1` Young Samurai.
- Final resource audit and Gradle build passed.
