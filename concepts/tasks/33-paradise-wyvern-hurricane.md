# Task 33 — Paradise Wyvern and Hurricane

Status: completed

## Player contract

- A rare Paradise Wyvern spawns only above Paradise Grass in Paradise-island biomes.
- A wild adult wyvern accepts a mature Paradise Fruit. Each fruit has a one-in-three taming chance; successful taming records the player as owner.
- The owner can heal the wyvern with fruit, install a saddle with a vanilla saddle, sneak-interact to sit/follow, and ride by interacting with an empty hand.
- A saddled wyvern supports server-authoritative aerial steering. Rider look pitch controls ascent/descent, horizontal input controls cruise, and an idle mount slowly loses height instead of hovering perfectly.
- While riding, one press of Use fires a Wind Core along the rider's look vector. The server validates ownership, saddle, pilot identity, and the per-wyvern cooldown.
- Wind Core cooldown is exactly 300 ticks (15 seconds), synchronized to clients and persisted in NBT.

## Hurricane contract

- Wind Core impact creates one stationary hurricane lasting 120 ticks (6 seconds), with no block damage.
- Ticks 0–19: readable cyan/gold/white telegraph.
- Ticks 20–99: living targets in a seven-block radius are pulled inward, orbited, and lifted by server-owned velocity changes.
- Ticks 100–119: captured targets receive one outward/upward release impulse, then the vortex dissipates.
- The caster, current rider, owner, allied entities, passengers, and tamed animals owned by the same player are excluded.
- Gameplay membership and physics exist only on the server. The client entity emits distance-capped local particles and renders the animated vortex.

## Visual contract

- The wyvern is an original long sky-serpent silhouette: segmented jade body and tail, ivory belly, gold horns/crest, two large forewings, and four stabilizer fins.
- Hard-edged authored pixel textures use Paradise jade, cyan, warm gold, ivory, and deep teal shadow ramps.
- Wind Core is a compact cyan core inside crossed gold/white rings.
- Hurricane is a translucent layered funnel with orbit rings; local feather/leaf-like particles reinforce Paradise identity without dense network particle traffic.
- GeckoLib animations cover idle flight, cruise flight, sitting, and casting.

## Technical boundaries

- A client Use-key edge sends only an intent packet; it never creates projectiles or changes cooldowns locally.
- Projectile collision, vortex creation, exclusions, target capture, and velocity changes are authoritative on the logical server.
- Entity registrations stay inside the independent `com.opus.paradise` line.
- The global trophy archive gains a seventeenth, non-boss Wyvern trophy and both Paradise/all-trophy advancement criteria.

## Validation

- [x] Java 17 main/client compilation and full build pass.
- [x] All 616 asset/data JSON files parse; the new rigs contain 36 bones and 49 cubes total, every animated bone resolves, and all expected animation clips exist.
- [x] Dedicated server registered Wyvern, Wind Core and Hurricane and reached `Done` on the existing world.
- [x] Client reached common/client initialization and renderer registration; the known host boundary then stopped GLFW window creation with Wayland focus error `65548`, not a mod/resource exception.
- [x] A scheduled runtime probe proved: ticks 0–19 apply no force; the active phase simultaneously applies inward, tangential and upward velocity; tick 100 releases outward/upward; the control gold block survived the full vortex.
- [x] A downward-moving Wind Core was consumed on collision and a Hurricane existed five ticks later.
- [x] A saddled Wyvern saved at cooldown 300 survived a full stop/start with `Saddle: 1b` and `WindCooldown: 244`, proving persistent state and continued server ticking.
- [x] Death QA found exactly one `trophy_paradise_wyvern`; the all-current archive now has 17 criteria and Paradise has 3.
- [x] All temporary entities, items, blocks, force-loads, scoreboard state and the temporary QA datapack were removed before final shutdown.

## Delivered details

- Client prediction sends bounded steering intent; a common-side server mixin rejects vanilla client vehicle-position authority for this mount, so the server integrates the accepted input and remains the movement source of truth.
- Use is edge-triggered on the client. The server rechecks controlling passenger, ownership, saddle, life state and cooldown before creating a projectile.
- The saddle bone is hidden until installed and the physical saddle is persisted and returned on death when mob loot is enabled.
- A 24-bone/32-cube Wyvern rig, 4-bone Wind Core and 8-bone Hurricane use original hard-edged jade/ivory/gold/cyan assets. Hurricane particles are client-local and range-capped.
