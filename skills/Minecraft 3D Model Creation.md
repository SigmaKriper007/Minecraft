# SKILL: PROFESSIONAL MINECRAFT 3D MODEL CREATION

> Target env: Minecraft, Blockbench, GeckoLib, Java Edition modding. Objects: custom entities, armor, weapons, tools, blocks, animated machines, creatures, bosses, vehicles, mechanical/decorative objects.

## 0. Purpose
Strict professional workflow for creating/editing/optimizing/organizing/texturing/rigging/preparing 3D models. The model must be: visually coherent, correctly proportioned, Minecraft-compatible, efficiently constructed, properly structured/parented/pivoted, animation-ready, texture-ready, GeckoLib-compatible, easy to modify/animate later, optimized for rendering, consistent with project art direction, technically clean.

**Never sacrifice structural correctness for appearance; never sacrifice readability for technical complexity.** Balance: DESIGN + SILHOUETTE + PROPORTIONS + STRUCTURE + ANIMATION + TEXTURE + PERFORMANCE + ENGINE COMPATIBILITY.

## 1. Core principles
1. **Understand before modeling** — determine what/why/static?/block-item-entity-armor-weapon-machine-creature?/GeckoLib?/anims?/fits existing character?/player proportions?/visual style?/references.
2. **Design before detail** — CONCEPT → SILHOUETTE → PRIMARY FORMS → PROPORTIONS → SECONDARY FORMS → TERTIARY DETAILS → MATERIAL → UV → TEXTURE → ANIMATION PREP → OPTIMIZATION → QA. Never start with tiny decorations; bad proportions = bad model.
3. **Every geometric element must have a purpose** (silhouette, structure, animation, functionality, material separation, identity, readability, construction, decoration). Avoid random/"more detailed" geometry.
4. **Prefer readable geometry** at gameplay distance: SILHOUETTE > COLOR/MATERIAL GROUPS > MAJOR FORMS > SECONDARY > MICRO. Micro-details never overpower silhouette.
5. **Use geometry & texture appropriately** — geometry: major shape/armor plates/weapons/limbs/horns/wings/large components/recesses/protrusions; texture: scratches/dirt/tiny bolts/seams/wear/symbols/subtle material variation/tiny patterns. Don't model what texture can show.

## 2. Step 1 — Requirement analysis
Before modeling define mental spec: identity (what), function (what does it do), scale (vs Minecraft), orientation (front/back/left/right/top/bottom — never arbitrary), animation needs (idle/walk/run/attack/hurt/death/jump/fall/open/close/activate/deactivate/reload/charge/shoot/recoil/inspect/special), technical (Blockbench format, Java vs GeckoLib, entity vs item, texture? animation? preserve hierarchy? mod-compatible?).

## 3. Step 2 — Reference analysis
Extract: primary shape (height/width/depth/masses/COG), secondary (armor/weapons/straps/plates/joints/mechanical components/accessories), tertiary (bolts/seams/scratches/vents/decorations/engravings), visual language (sharp/rounded/industrial/medieval/futuristic/organic/military/magical/mechanical) → translate to Minecraft-compatible geometry, not high-poly copy.

## 4. Step 3 — Coordinate system
X=left/right, Y=vertical, Z=front/back. One consistent "front". Humanoid: +Y up. Define front before modeling; avoid accidental mirroring.

## 5. Step 4 — Scale
Minecraft block ≈ 16×16×16 texels. Determine gameplay scale: creature<player, humanoid≈player, boss=×multiple player, weapon∝hand, machine∝surrounding blocks. Verify in actual environment.

## 6. Step 5 — Blockout
Only primitives (cube/cuboid/prism/segmented cylinder). No bolts/scratches/tiny details/patterns. Must pass: correct shape/proportions/scale/silhouette from front/side/back? If NO, fix blockout first.

## 7. Step 6 — Silhouette
Check from front/back/left/right/top/bottom/45°/gameplay. Recognizable without texture. Weak silhouette → increase shape differences, fix proportions, separate overlapping forms, enlarge important features, simplify insignificant. Don't fix silhouette with texture.

## 8. Step 7 — Primary forms
torso/head/limbs/main weapon/armor/wings/tail/machine body/major components. Each independently understandable; avoid excessive intersections.

## 9. Step 8 — Proportion control
Continuous. Humanoid: head→neck→shoulders→torso→arms→forearms→hands→hips→thighs→lower legs→feet. Creature: head/neck/torso/legs/tail/wings/feet. Weapon: handle/grip/guard/body/blade-head-barrel. Proportions BEFORE details.

## 10. Step 9 — Model decomposition
Break into logical components. Humanoid: root→body→(head→jaw, left_arm→left_forearm→left_hand, right_arm→…, left_leg→left_foot, right_leg→right_foot). Mechanical: root→chassis→(cockpit, engines, arms→weapon). Weapon: root→(handle, guard, main_body, blade, details). Hierarchy reflects behavior.

## 11. Step 10 — Hierarchy design
Parent controls everything that moves with it (body→left_arm→left_forearm→left_hand). No unrelated parent-child.

## 12. Step 11 — Pivot design
Each animated part needs correct pivot = physical rotation point: arm=shoulder, forearm=elbow, head=neck, jaw=hinge, wing=attachment, tail segment=joint, door=hinge, sword=hand/grip. Never drop pivot at geometric center if rotation is elsewhere.

## 13. Step 12 — Test pivots before detailing
Rotate ±45°/±90°: correct location? incorrect intersections? child follows? physically correct? Fix now, not after texturing.

## 14. Step 13 — Secondary forms
After primary structure correct: armor plates, pauldrons, knee guards, chest plates, backpack, belt, pouches, weapon components, joints, vents, large decorations. Reinforce primary; don't destroy silhouette.

## 15. Step 14 — Joint design
Joints must communicate movement (shoulder/elbow/wrist/hip/knee/ankle/neck/jaw). Avoid two rigid cubes touching if articulated. Use joint plates/connectors/segmented forms/recessed gaps/armor overlaps.

## 16. Step 15 — Mechanical models
Functional logic: power source, main body, frame, moving parts, joints, weapons/tools, cooling, access panels, controls. Fictional tech should look mechanically intentional: cables connect meaningful points, armor attached, moving parts have clearance.

## 17. Step 16 — Organic models
Start from masses (torso/head/limbs/tail/wings), then refine. Not random cubes. Controlled segmentation for muscle/joints/claws/horns/teeth/wings/tails. Anatomy-inspired structure even if fictional.

## 18. Step 17 — Symmetry
Build one side, duplicate/mirror, verify alignment/dimensions/pivots/hierarchy. Check X/Y/Z, rotation, dimensions, pivot, parent. Mirroring is more accurate than manual recreation.

## 19. Step 18 — Intentional asymmetry
Only when it contributes: damaged armor, missing plate, holster, backpack, cybernetic limb, cape, asymmetric ornament, unique pauldron. Must look intentional.

## 20. Step 19 — Tertiary details
After major geometry: bolts/small plates/seams/vents/engraving/small cables/mech parts/decorations. Before each: can this be texture? visible? improves design? affects silhouette? animates? If no — texture.

## 21. Step 20 — Geometry optimization
Remove invisible/duplicate geometry, internal faces, redundant cubes, microscopic details, fully-hidden geometry. Avoid extreme cube counts. Goal: max visual quality per element.

## 22. Step 21 — Geometry priority
Preserve in order: 1 Silhouette, 2 Primary forms, 3 Animated forms, 4 Major secondary, 5 Recognizable details, 6 Minor decoration, 7 Invisible details. Remove bottom-up.

## 23. Step 22 — Texture planning
Determine material groups first: metal/dark_metal/light_metal/armor/cloth/leather/wood/stone/glass/energy/organic_skin/bone/glowing. Each material = understandable properties.

## 24. Step 23 — UV mapping
Intentional. Prioritize UV space for face/weapons/logos/symbols/important armor/large visible surfaces. No wasted space on invisible faces, no unnecessary fragmentation, keep related surfaces organized.

## 25. Step 24 — Texture resolution
16/32/64/128/256 by complexity. Don't auto-use highest; for Minecraft-style, lower often more authentic. Enough to show important detail, no more.

## 26. Step 25 — Texture structure
BASE COLOR + SHADOW + HIGHLIGHTS + MATERIAL DIFFERENTIATION + optional WEAR. No random noise for material.

## 27. Step 26 — Material language
Metal: hard highlights, controlled contrast, sharp transitions. Cloth: low contrast, matte, subtle folds. Leather: moderate variation, restrained highlights. Stone: irregular variation, rough. Glass: transparency when appropriate, controlled highlights, strong edges. Energy: strong contrast, bright center, darker surround, optional emissive.

## 28. Step 27 — Color palette
Controlled: primary/secondary/accent/dark variant/light variant/optional emissive. Accent identifies important functional areas. No dozens of unrelated colors.

## 29. Step 28 — Visual hierarchy
Viewer immediately understands: what is it? what's important? what moves? dangerous? interactive? decorative? Use silhouette/contrast/color/geometry/positioning. Not everything equally dominant.

## 30. Step 29 — Animation planning
Map all movable bones, then define movement of each. Attack: prepare→wind-up→strike→impact→recovery.

## 31. Step 30 — Animation-ready structure
Never combine independently animated components (arm ≠ upper_arm+forearm+hand). Required animation determines hierarchy.

## 32. Step 31 — Idle design
Subtle: breathing, slight head movement, small mechanical motion, cape/tail movement, energy fluctuation. Communicates life without distraction.

## 33. Step 32 — Walk design
Humanoid: left_arm↔right_leg, right_arm↔left_leg. Coordinated, not independent.

## 34. Step 33 — Attack design
Communicates intent: anticipation→acceleration→impact→follow-through→recovery. Impact visually clear; not simple linear rotation.

## 35. Step 34 — Recoil
aim→fire→recoil→return. Short and readable. Consider weapon movement, muzzle point, slide/bolt, energy chamber, smoke/flash location (firearms/energy).

## 36. Step 35 — Attachment points
Logical named points for effects: muzzle, mouth, eyes, weapon_tip, hand, chest_core, engine, exhaust.

## 37. Step 36 — GeckoLib compatibility
Logical hierarchy, meaningful names, correct pivots, separated animated parts, stable hierarchy after animation starts, test rotations first. No unnecessary depth.

## 38. Step 37 — Naming standard
root/body/head/jaw/neck/left_arm/left_forearm/left_hand/right_arm…/legs/feet/weapon/blade/handle/muzzle/barrel/core/wing_left/wing_right/tail. Avoid: Cube/Cube.001/Object/Group/Thing/Part/New/Test.

## 39. Step 38 — Organization
root→(body, head, limbs, equipment, accessories, effects). Deeper for complex; no random hierarchy.

## 40. Step 39 — Special effect geometry
Keep effect geometry separate (muzzle, energy_core, eye_glow, engine_exhaust, magic_core, projectile_origin) to allow animation/emissive/particles/procedural effects.

## 41. Step 40 — Final inspection
From all directions + gameplay distance: accidental asymmetry, broken silhouette, floating geometry, intersections, impossible construction, inconsistent scale, wrong pivots, bad hierarchy.

## 42. Step 41 — Animation stress test
Simulate major rotations ±45°/90° per animated part: clipping, children correct, detachment, wrong pivots, armor intersects, weapon through body. Fix before animation.

## 43. Step 42 — Performance review
Can geometry be removed? texture? two parts→one? invisible geometry? unnecessary segments? Optimize without damaging silhouette.

## 44. Step 43 — Final technical QA
Geometry (no accidents/redundancy/broken shapes/needless internal), Hierarchy (root, parents, names, animated parts), Pivots (correct rotation/orientation/child behavior), Scale (Minecraft scale/proportions/relation to player-world), UV (coverage/logic/space), Texture (resolution/palette/material readability/no accidental transparency), Animation (bones exist/moves possible/no hierarchy limits), Performance.

## 45. Step 44 — Final quality check
Visual? Recognition immediate? Minecraft-compatible? Hierarchy correct? All parts move independently? Unnecessary geometry removed? Another dev understands without guessing?

## 46. Common failures — never
start with details; ignore silhouette; random pivots; meaningless names; excessive geometry; combine animated parts; use texture to hide bad geometry; geometry for invisible details; random asymmetry; impossible mechanics; destroy animation hierarchy without permission; random proportion changes; excessive UV fragmentation; random colors; detail without purpose.

## 47. Modeling priority system
1 Silhouette, 2 Proportions, 3 Scale, 4 Hierarchy, 5 Pivots, 6 Animation structure, 7 Major secondary geometry, 8 Texture readability, 9 Minor details, 10 Micro-details. Never sacrifice higher priority for lower.

## 48. Editing an existing model
Inspect full hierarchy → identify root/body/animated/pivots/textures/UV/animation deps → determine exactly what changes → modify only required areas → preserve unrelated → retest proportions/hierarchy/pivots/animation/texture/scale. Never rebuild when local modification suffices.

## 49. Armor
Looks like an additional protective layer. Separate: helmet/chestplate/shoulders/arms/gloves/belt/legs/boots/accessories. Follow underlying body with clearance at joints/elbows/knees/shoulders/hips. Don't make movement visually impossible unless requested.

## 50. Weapons
Determine grip point/center of mass/attack direction/orientation/attachment point; sit naturally in hand. Melee: handle/guard/blade-head. Firearms: grip/receiver/barrel/magazine/sight/muzzle. Futuristic: power source/body/energy chamber/emitter/grip/controls. Fictional weapons need understandable construction.

## 51. Creatures
Define body mass/head/neck/limbs/joints/feet/tail/wings/special anatomy. Locomotion before legs. Gait-supporting geometry; wing pivots at attachment; independent jaw if jaw attacks.

## 52. Bosses
Stronger visual hierarchy: silhouette/size/unique features/weapon/head/core/special effects/armor. Recognizable immediately; gameplay-critical elements prominent.

## 53. Machines
Clear chassis, functional components, articulated parts, attachment points, visible power/energy systems, maintenance/access areas, coherent structural logic. No scattered random pieces.

## 54. Large models
Divide into logical systems: root→{main_body, left/right/front/rear_system, weapons, engines, effects, accessories}. Easier animation/maintenance.

## 55. Do not overmodel
More geometry ≠ better. More meaningFUL geometry = better. Simple + excellent proportions/silhouette beats detailed + poor structure.

## 56. Professional workflow summary
README requirements → identify type → analyze references → scale → orientation → primary silhouette → blockout → verify proportions → primary forms → split logical parts → hierarchy → pivots → test rotations → secondary forms → joints → animation-critical parts → tertiary details → optimize geometry → plan materials → UV → texture → verify material readability → prepare animation structure → attachment points → test animated movement → performance review → technical QA → visual QA → verify Minecraft scale → finalize.

## 57. Final principle
Never think "how can I add more detail?" Think instead: "how can the model communicate identity, function, movement, materials, visual hierarchy as clearly and efficiently as possible?"

The best model is not the most geometric. It's one where every form has a purpose, every part a name, every moving part a correct pivot, every animated part a correct parent, every detail supports the design, every texture supports geometry, and every piece of geometry justifies its cost — visually strong, technically clean, animation-ready, optimized, maintainable, and appropriate for Minecraft.