````markdown
---
name: minecraft-special-effects
description: Universal skill for designing, implementing and maintaining Minecraft mod visual effects, particles, energy effects, weapon trails, impacts, explosions, beams, auras, magic, animated effects, glows and screen effects.
---

# Minecraft Special Effects / VFX System

## 0. Назначение
Правила создания ВСЕХ спец-эффектов мода: particles, magic, energy, weapon/sword trails, slash, hit/impact, explosions, shockwaves, beams, lasers, lightning, auras, glowing objects, energy cores, charging, teleport, death, spawn, armor/entity/block/item/projectile effects, smoke, fire, sparks, dust, fragments, rings, circles, runes, glyphs, trails, screen shake, camera effects, overlays, post-processing, animated textures, emissive, layered VFX.
**Цель: эффекты как часть одной визуальной системы мода, а не случайный набор партиклов.**

## 1. Главный принцип
Роль: VFX artist + Minecraft rendering developer + technical artist. Перед эффектом определи: Purpose→Shape→Motion→Color→Timing→Intensity→Interaction→Implementation. Не начинай с кода — сначала визуальное поведение.

## 2. Эффект имеет причину
Каждый VFX сообщает игроку: Hit/Charge/Danger/Energy/Success/Failure/Teleport. Не добавляй эффект ради красивых частиц.

## 3. Единая VFX-система
Общий визуальный язык: Colors/Shapes/Particles/Motion/Timing/Glow/Trails/Impacts/Sounds/Screen Effects. Если энергия мода фиолетово-циановая — все энерго-эффекты её используют.

## 4. Связь с палитрой мода
Энерго-эффект: dark→main→bright→white hot core (напр. d→A→P→f→W). Не random rainbow вне стиля.

## 5. Цветовая иерархия
Dark→Mid→Bright→Hotspot. Белый экономно = максимум энергии/hotspot.

## 6. VFX из слоёв
Сложный эффект разбивай: Energy Explosion = Core+Glow+Sparks+Shockwave+Debris+Smoke; Sword Slash = Blade Trail+Energy Trail+Slash Arc+Sparks+Impact. Не одним particle.

## 7. Core / ядро
Маленький, яркий, насыщенный центр (glow› colored core › white-hot).

## 8. Glow
Усиливает форму, не уничтожает: dark outer→colored glow→bright shape→white core. Второстепенный элемент — не огромное пятно.

## 9. Particle types по материалу
Energy: small bright; Fire: rising; Smoke: slow expanding; Stone: small fragments; Metal: sparks; Magic: floating + rings + glyphs.

## 10. Размер частиц
Не все одинаковые: ~70% tiny, 20% small, 8% medium, 2% large — глубина.

## 11. Плотность
По важности: обычный low, редкий medium/high, boss high. Не тонны ради впечатления.

## 12. Performance
Каждый particle стоит. Не 1000/tick без нужды. Учитывать count/lifetime/render distance/frequency/сущности/игроки.

## 13. Lifetime
Very short: impact/spark (2–5t); Short: slash/hit (5–12t); Medium: energy/magic (10–30t); Long: aura/ambient (30+t). По визуалу.

## 14. Motion
Понятное движение: Linear/Radial/Orbital/Spiral/Falling/Rising/Drifting/Attraction/Explosion/Convergence.

## 15. Radial (взрыв)
`velocity = normalize(position - center)`.

## 16. Orbital (аура)
Вращение вокруг объекта: angle/radius/angularVelocity/height.

## 17. Spiral
Комбинация rotation + vertical movement + radius change.

## 18. Convergence (зарядка)
Частицы снаружи → в CORE — накопление энергии.

## 19. Explosion
Фазы: Charge→Flash→Expansion→Shockwave→Sparks→Debris→Dissipation. Не только рост count.

## 20. Impact
Очень короткий: Frame1 flash → 2 impact ring → 3 particles → 4–8 sparks уходят. Почти мгновенный.

## 21. Shockwave
ring + particles + brief brightness; быстро расширяется и исчезает.

## 22. Sword trail
Blade Trail + Energy Trail + Sparks, следует за реальным движением оружия. Не статичный у руки.

## 23. Sword slash
Start→Arc→Bright edge→Fade. Энергетическая катана: dark purple outer + purple body + cyan edge + white hotspot.

## 24. Trail sampling
Используй previous + current position оружия — trail между ними, иначе разрывы при быстром движении.

## 25. Trail fade
100→75→50→25→0%; менять alpha/width/brightness/saturation.

## 26. Beam / laser
Outer Glow + Main Beam + Inner Core + Hotspot; не одноцветный по толщине.

## 27. Beam endpoint
start + direction + max distance + collision; конец = точка столкновения.

## 28. Magic circle
Outer Ring + Inner Ring + Glyphs + Particles + Center + Glow. Не перегружать.

## 29. Rune / glyph
Читаемый, пиксельный, стилистически единый, простой (8×8/16×16 — несколько px).

## 30. Aura
floating + rings + small glow вокруг объекта; не закрывает модель.

## 31. Entity effects
Привязка к ModelPart/entity position (Head→particles, Chest→core, Hands→sparks, Feet→dust). Не фиксированная мировая точка.

## 32. Armor effects
Разделяй Armor Texture + Model + VFX (Helmet→visor glow, Chest→core, Shoulders→particles, Boots→trail). Не анимация в статичной текстуре.

## 33. Glowing armor
Отдельный слой: Base Armor + Emissive Layer (alpha, bright colors, limited white).

## 34. Emissive ≠ всё белое
dark base → colored emission → bright center → white hotspot.

## 35. Animated textures
frame1..N одинакового размера; цикличная, последовательная, без скачков.

## 36. Animation curves
Не всё линейно: Ease In / Out / In-Out / Pulse / Ping-Pong. Пример scale 0→1.2→1.0 лучше 0,0.1,0.2…

## 37. Pulse
Пульсировать scale/alpha/brightness/radius, но не всё сразу одной частотой.

## 38. Randomness
Контролируемая: random для position/lifetime/rotation/size/velocity; НЕ для основной формы/core/критичной информации.

## 39. Deterministic
Одинаковый вид → deterministic seed (boss attacks, синхронный мультиплеер, скриптованные анимации).

## 40. Sound + VFX
Синхронизация: Charge→low sound, Flash→impact, Explosion→deep. Не каждый tick.

## 41. Screen effects
Осторожно: flash/shake/vignette/color overlay/blur/distortion/chromatic. Короткие.

## 42. Screen shake
Только для: сильный удар/взрыв/boss attack/тяжёлое оружие. Не для обычной частицы/каждого попадания/постоянной ауры.

## 43. Screen flash
Короткий: 100→50→20→0%. Не оставлять экран ярким.

## 44. Vignette
danger/low health/boss/dimensional. Усиливает состояние, не мешает.

## 45. Post-processing
Только если улучшает; каждая цель конкретна. Не blur/distortion/chromatic «потому что круто».

## 46. Particle textures
Стиль мода: nearest, hard edges, limited palette, no AA. 8×8/16×16/32×32.

## 47. Particle atlas
Проверять UV/frame/animation/filtering/padding; без bleeding.

## 48. Transparency
Core=opaque, Glow=semi, Smoke=semi, outer=transparent. Не всё alpha=0.5.

## 49. Render order
Background Glow→Outer particles→Shockwave→Main effect→Core→White hotspot (зависит от renderer).

## 50. Depth
Определить: за блоками или поверх. World Effect учитывает depth; Screen Overlay — нет. Не смешивать.

## 51. Block effects
Block Position→Local Coordinates→Effect. Не мировые координаты.

## 52. Block break
Dust + Fragments + Impact; фрагменты по текстуре (фиолетовый кристалл → stone+purple fragments+cyan sparks).

## 53. Block activation
Idle→Activation→Active→Deactivation; своя стадия — свой VFX.

## 54. Projectile trail
Projectile Core + Trail + Particles; trail по previous/current position.

## 55. Teleport
Charge→Compression→Flash→Disappear; на новом месте Flash→Expansion→Particles→Fade.

## 56. Death effect
Не обязательно взрыв: Dissolve/Collapse/Energy release/Soul particles/Fragments/Smoke/Teleport — по природе entity.

## 57. Spawn
small→expand→stabilize. Не огромный взрыв для обычного моба.

## 58. Boss effects
Aura/phase transitions/telegraphs/ground effects/shockwaves/screen effects/particles — но понятная visual hierarchy.

## 59. Attack telegraph
Опасная атака — визуальное предупреждение: Target area→Growing ring→Bright warning→Attack. Показывать область, направление, время.

## 60. Gameplay information
Purple glow=energy, red=danger, cyan ring=active, white flash=impact. Сохранять ассоциации в моде.

## 61. Multiplayer
Gameplay event + Client visual effect раздельно. Не отправлять каждый particle.

## 62. Network performance
Server→Event/Packet→Client→Local VFX. Плохо: particle→packet каждый.

## 63. Client-side VFX
Визуал (particles/glow/shake/flash/trail) на клиенте; gameplay event из корректного состояния.

## 64. Distance culling
Near=full, Medium=reduced, Far=minimal/none.

## 65. LOD
LOD0 full → LOD1 reduced → LOD2 minimal → LOD3 none.

## 66. Object pooling
Часто создаваемые (bullets/sparks/trails/particles) — pooling; не усложнять без нужды.

## 67. Модульность
EnergyEffect (Core/Glow/Sparks/Ring) вместо HugeEffectClass на тысячи строк.

## 68. Параметры эффекта
color/size/lifetime/intensity/particleCount/speed/radius/duration → EffectConfig(...) — переиспользование.

## 69. Presets
SMALL_IMPACT/LARGE_IMPACT/ENERGY_CHARGE/ENERGY_EXPLOSION/SWORD_SLASH/TELEPORT/BOSS_PHASE — меньше дублирования.

## 70. Композиция
Effect = ParticleEffect + TrailEffect + RingEffect + GlowEffect + ScreenEffect — каждый своя часть.

## 71. VFX Style Anchor
Показанный эффект → извлечь Color/Shape/Density/Motion/Lifetime/Glow/Contrast/Timing; новые — тот же язык.

## 72. Не копировать буквально
«сделай остальные как этот» = сохранить style/palette/motion language/intensity/hierarchy, не форму 1:1.

## 73. VFX и текстуры
PNG-эффект — по texture skill: pixel art, limited palette, no AA, nearest, transparent bg.

## 74. VFX и UI
Эффект в интерфейсе → UI skill (HUD+energy pulse+notification); не world-space particle для UI.

## 75. VFX и модель
ModelPart→World position→VFX anchor→Particle. Следует за реальным движением ModelPart.

## 76. VFX anchors
head/chest/left_hand/right_hand/weapon_tip/weapon_guard/feet/core — логичные точки привязки.

## 77. Эффекты оружия
Idle/Attack/Hit/Charge/Special отдельно (Katana: idle tiny particles, attack blade trail, hit sparks, special slash wave+shockwave).

## 78. Не перегружай idle
Постоянный эффект слабее активного: idle ~10%, attack ~60%, ultimate ~100% (условная шкала).

## 79. Intensity
По importance/rarity/power/danger/duration: обычный low, редкий high, ultimate very high.

## 80. Telegraph vs Impact
Telegraph: медленный, читаемый, предупреждающий. Impact: короткий, яркий, мощный. Не путать.

## 81. VFX timeline
Сложный эффект — timeline (0ms charge → 100ms accelerate → 250ms flash → 300ms impact → 400ms shockwave → 700ms fade) для синхронизации.

## 82. BEGIN/MIDDLE/END
Каждый эффект: BEGIN→BUILD→PEAK→FADE, даже в несколько тиков.

## 83. Looping effects
Aura/Energy Core/Portal/Machine — цикл без резкого шва.

## 84. Randomness не ломает силуэт
ring/beam/slash/circle/glyph — основная форма стабильна; random вокруг неё.

## 85. Читаемость на расстоянии
close/medium/far — основная форма сохраняется.

## 86. Не много белого
Только core/flash/hotspot/сильнейший impact.

## 87. Энергетический язык (Opus)
Purple=raw/stable, Cyan=active/flowing, White=maximum concentration — придерживаться в моде.

## 88. Material language
Metal: sharp sparks; Stone: dust+fragments; Crystal: shards+glow; Energy: particles+rings+core; Fire: rising+flickering.

## 89. Debug mode
Для сложных: bounds/anchor points/origin/direction/radius/collision. Не в production.

## 90. Build и test
Build→Launch→Trigger→Check rendering/timing/performance/multiplayer. Компиляция ≠ готово.

## 91. Проверка VFX
Палитра, силуэт, motion, lifetime, intensity, glow, transparency, render order, нет z-fighting/bleeding/excess particles/memory leaks/crash (client/server), multiplayer, читается на расстоянии, стиль мода.

## 92. Performance check
Часто запускаемых: particles/sec, active effects, render calls, texture switches, allocations, network traffic. Массовые мобы/оружие/projectiles/machines/explosions.

## 93. Не ломать gameplay
VFX не меняет random gameplay/hitbox/damage/physics/AI/input, если это не механика.

## 94. Client/server rule
GAMEPLAY=Server authoritative; VISUAL=Client-side.

## 95. Effect event system
Много VFX → VFXManager.spawn(VFXType.ENERGY_EXPLOSION, pos). Только если нужна.

## 96. VFX registry
VFXType: ENERGY_SPARK/ENERGY_RING/SWORD_SLASH/IMPACT/TELEPORT/EXPLOSION/AURA.

## 97. Описание нового эффекта
Перед сложным VFX: Name/Purpose/Trigger/Duration/Color/Shape/Motion/Particles/Glow/Sound/Screen effect/Performance. (Пример: Opus Slash, katana attack, ~0.4s, Purple+Cyan+White, Arc, Forward, Sparks, Cyan edge, Shockwave.)

## 98. Приоритеты
1 Gameplay readability, 2 Silhouette, 3 Timing, 4 Motion, 5 Color, 6 Intensity, 7 Detail, 8 Decoration. Не жертвовать читаемостью ради частиц.

## 99. Главный rule (8 вопросов)
Что произошло? где эффект? направление? состояние объекта? не мешает игроку? стиль мода? не много частиц? технически корректно? Хоть один NO — не готово.

## 100. Финальная цель
PARTICLES + TRAILS + IMPACTS + WORLD EFFECTS + SCREEN VFX = одна VFX-система. **НЕ создавай отдельные эффекты. Создавай единую VFX-систему мода.** Каждый эффект: форма + движение + время + интенсивность + цвет + причина существования.
````