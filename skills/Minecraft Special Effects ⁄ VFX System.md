````markdown
---
name: minecraft-special-effects
description: Universal skill for designing, implementing and maintaining Minecraft mod visual effects, particles, energy effects, weapon trails, impacts, explosions, beams, auras, magic, animated effects, glows and screen effects.
---

# Minecraft Special Effects / VFX System

## 0. НАЗНАЧЕНИЕ

Этот skill определяет правила создания всех специальных визуальных эффектов Minecraft-мода.

Используй его для:

- particle effects;
- magic effects;
- energy effects;
- weapon trails;
- sword trails;
- slash effects;
- hit effects;
- impact effects;
- explosions;
- shockwaves;
- beams;
- lasers;
- lightning;
- auras;
- glowing objects;
- energy cores;
- charging effects;
- teleport effects;
- death effects;
- spawn effects;
- armor effects;
- entity effects;
- block effects;
- item effects;
- projectile effects;
- smoke;
- fire;
- sparks;
- dust;
- fragments;
- rings;
- circles;
- runes;
- glyphs;
- trails;
- screen shake;
- camera effects;
- screen overlays;
- post-processing;
- animated textures;
- emissive effects;
- layered VFX.

Главная цель:

**создавать эффекты, которые выглядят как часть одной визуальной системы мода, а не как случайный набор Minecraft-партиклов.**

---

# 1. ГЛАВНЫЙ ПРИНЦИП

Работай не как генератор случайных частиц.

Работай как:

**VFX artist + Minecraft rendering developer + technical artist.**

Перед созданием эффекта сначала определи:

```text
Purpose
 ↓
Shape
 ↓
Motion
 ↓
Color
 ↓
Timing
 ↓
Intensity
 ↓
Interaction
 ↓
Implementation
````

Не начинай с кода.

Сначала определи визуальное поведение эффекта.

---

# 2. ЭФФЕКТ ДОЛЖЕН ИМЕТЬ ПРИЧИНУ

Каждый VFX должен сообщать игроку что-то.

Например:

```text
Hit
→ удар произошёл

Charge
→ способность заряжается

Danger
→ рядом опасность

Energy
→ энергия активна

Success
→ действие успешно

Failure
→ действие не удалось

Teleport
→ объект переместился
```

Не добавляй эффект просто ради красивых частиц.

---

# 3. ЕДИНАЯ VFX-СИСТЕМА

Все эффекты должны использовать общий визуальный язык.

Система:

```text
VFX
├── Colors
├── Shapes
├── Particles
├── Motion
├── Timing
├── Glow
├── Trails
├── Impacts
├── Sounds
└── Screen Effects
```

Если в моде энергия имеет фиолетово-циановую цветовую систему:

все энергетические эффекты должны визуально использовать её.

---

# 4. СВЯЗЬ С ПАЛИТРОЙ МОДА

Используй существующую палитру проекта.

Например:

```text
Dark Purple
Purple
Light Purple
Pale Purple

Dark Cyan
Cyan
White
```

Для энергетических эффектов:

```text
dark
 ↓
main
 ↓
bright
 ↓
white hot core
```

Например:

```text
d → A → P → f → W
```

Не делай случайный rainbow VFX, если стиль проекта этого не предполагает.

---

# 5. ЦВЕТОВАЯ ИЕРАРХИЯ

Не весь эффект должен быть одинаково ярким.

Используй:

```text
Dark
 ↓
Mid
 ↓
Bright
 ↓
Hotspot
```

Например:

```text
████████
██PPPP██
█PffffP█
█ffWWff█
██ffff██
████████
```

Белый должен использоваться экономно.

White = максимальная энергия / hotspot.

---

# 6. VFX СТРОИТСЯ ИЗ СЛОЁВ

Сложный эффект желательно разбивать на несколько компонентов.

Например:

```text
Energy Explosion
│
├── Core
├── Glow
├── Sparks
├── Shockwave
├── Debris
└── Smoke
```

Или:

```text
Sword Slash
│
├── Blade Trail
├── Energy Trail
├── Slash Arc
├── Sparks
└── Impact
```

Не пытайся делать весь эффект одним particle.

---

# 7. CORE / ЯДРО

Большинство энергетических эффектов могут иметь центральное ядро.

Например:

```text
      glow
   █████████
  █PPPPPPPP█
 █PPffffPPPP█
 █PfffWWfffP█
 █PPffffPPPP█
  █PPPPPPPP█
   █████████
```

Core должен быть:

* маленьким;
* ярким;
* визуально самым насыщенным.

---

# 8. GLOW

Glow должен усиливать форму, а не уничтожать её.

Плохо:

```text
огромное яркое пятно
```

Хорошо:

```text
dark outer
 ↓
colored glow
 ↓
bright shape
 ↓
white core
```

Glow должен быть вторичным элементом.

---

# 9. PARTICLE TYPES

Используй разные частицы для разных материалов.

### Energy

```text
small bright particles
```

### Fire

```text
rising particles
```

### Smoke

```text
slow expanding particles
```

### Stone

```text
small fragments
```

### Metal

```text
sparks
```

### Magic

```text
floating particles
+
rings
+
glyphs
```

---

# 10. PARTICLE SIZE

Не делай все частицы одинакового размера.

Используй несколько уровней:

```text
Tiny
Small
Medium
Large
```

Например:

```text
70% tiny
20% small
8% medium
2% large
```

Это создаёт визуальную глубину.

---

# 11. PARTICLE DENSITY

Количество частиц должно зависеть от важности эффекта.

Обычный эффект:

```text
low density
```

Редкая способность:

```text
medium/high density
```

Boss ability:

```text
high density
```

Но:

**не используй огромное количество частиц просто ради впечатляющего вида.**

---

# 12. PERFORMANCE

Каждый particle имеет стоимость.

Не создавай:

```text
1000 particles / tick
```

без серьёзной необходимости.

Всегда учитывай:

* количество particles;
* lifetime;
* render distance;
* frequency;
* количество сущностей;
* количество игроков.

---

# 13. PARTICLE LIFETIME

Lifetime определяет характер эффекта.

```text
Very short
→ impact / spark

Short
→ slash / hit

Medium
→ energy / magic

Long
→ aura / ambient
```

Например:

```text
Spark:
2–5 ticks

Slash:
5–12 ticks

Energy:
10–30 ticks

Aura:
30+ ticks
```

Значения подбираются по визуальному результату.

---

# 14. MOTION

Каждая частица должна иметь понятное движение.

Типы:

```text
Linear
Radial
Orbital
Spiral
Falling
Rising
Drifting
Attraction
Explosion
Convergence
```

---

# 15. RADIAL EFFECT

Для взрыва:

```text
       ↑
       |
← ← CORE → →
       |
       ↓
```

Направление:

```text
velocity = normalize(position - center)
```

---

# 16. ORBITAL EFFECT

Для ауры:

```text
        •
     •     •
   •   CORE  •
     •     •
        •
```

Частицы вращаются вокруг объекта.

Используй:

```text
angle
radius
angularVelocity
height
```

---

# 17. SPIRAL EFFECT

Для сложной энергии:

```text
       •
      /
     •
    /
   CORE
```

Можно комбинировать:

```text
rotation
+
vertical movement
+
radius change
```

---

# 18. CONVERGENCE

Для зарядки:

частицы движутся:

```text
OUTSIDE
 ↓
 ↓
 ↓
CORE
```

Например:

```text
particles
   ↓
   ↓
   ↓
[ CORE ]
```

Это создаёт ощущение накопления энергии.

---

# 19. EXPLOSION

Взрыв не должен состоять только из увеличения particle count.

Используй несколько фаз:

```text
1. Charge
2. Flash
3. Expansion
4. Shockwave
5. Sparks
6. Debris
7. Dissipation
```

---

# 20. IMPACT

Удар должен быть очень коротким.

Например:

```text
Frame 1
flash

Frame 2
impact ring

Frame 3
particles

Frame 4–8
sparks disappear
```

Игрок должен ощущать удар почти мгновенно.

---

# 21. SHOCKWAVE

Shockwave можно создавать:

```text
ring
+
particles
+
brief brightness
```

Пример:

```text
       ███
    ██     ██
   █   CORE  █
    ██     ██
       ███
```

Ring должен быстро расширяться и исчезать.

---

# 22. SWORD TRAIL

Для оружия используй отдельную систему trail.

Trail может состоять из:

```text
Blade Trail
+
Energy Trail
+
Sparks
```

Trail должен следовать за реальным движением оружия.

Не рисуй статичный эффект рядом с рукой.

---

# 23. SWORD SLASH

Slash обычно состоит из:

```text
Start
 ↓
Arc
 ↓
Bright edge
 ↓
Fade
```

Например:

```text
       ███
     ██
   ██
 ██
█
```

Для энергетической катаны:

```text
dark purple outer
+
purple body
+
cyan edge
+
white hotspot
```

---

# 24. TRAIL SAMPLING

Для оружия желательно использовать предыдущую и текущую позиции.

```text
previous position
        ↓
        ↓
current position
```

Так можно построить trail между ними.

Не создавай trail только в текущей позиции.

Иначе при быстром движении появятся разрывы.

---

# 25. TRAIL FADE

Trail должен постепенно исчезать:

```text
100%
 ↓
75%
 ↓
50%
 ↓
25%
 ↓
0%
```

Можно изменять одновременно:

* alpha;
* width;
* brightness;
* saturation.

---

# 26. BEAM / LASER

Beam должен иметь:

```text
Outer Glow
Main Beam
Inner Core
Hotspot
```

Пример:

```text
==== purple glow ====
---- cyan beam ------
====== white core ===
```

Не делай beam одинакового цвета по всей толщине.

---

# 27. BEAM ENDPOINT

Для луча определяй:

```text
start
+
direction
+
max distance
+
collision
```

Если луч сталкивается с блоком:

его конец должен совпадать с точкой столкновения.

---

# 28. MAGIC CIRCLE

Магические круги могут состоять из:

```text
Outer Ring
Inner Ring
Glyphs
Particles
Center
Glow
```

Не перегружай круг слишком большим количеством символов.

---

# 29. RUNE / GLYPH

Glyph должен быть:

* читаемым;
* пиксельным;
* стилистически единым;
* достаточно простым.

Для маленького эффекта:

```text
8×8
16×16
```

достаточно нескольких пикселей.

---

# 30. AURA

Aura должна окружать объект.

Можно использовать:

```text
floating particles
+
rings
+
small glow
```

Aura не должна полностью закрывать модель.

Игрок должен видеть entity.

---

# 31. ENTITY EFFECTS

Для entity:

эффект должен быть привязан к ModelPart или entity position.

Например:

```text
Head
 ↓
floating particles

Chest
 ↓
energy core

Hands
 ↓
energy sparks

Feet
 ↓
dust
```

Не рисуй эффект в фиксированной мировой координате, если он должен следовать за моделью.

---

# 32. ARMOR EFFECTS

Если броня имеет VFX:

разделяй:

```text
Armor Texture
+
Armor Model
+
Armor VFX
```

Например:

```text
Helmet
 └── glowing visor

Chest
 └── energy core

Shoulders
 └── energy particles

Boots
 └── movement trail
```

Не пытайся встроить анимацию в статическую текстуру.

---

# 33. GLOWING ARMOR

Для светящихся элементов:

используй отдельный слой:

```text
Base Armor
+
Emissive Layer
```

Emissive слой должен иметь:

* alpha;
* bright colors;
* limited white hotspots.

---

# 34. EMISSIVE

Emissive не означает:

```text
всё белое
```

Правильно:

```text
dark base
→ colored emission
→ bright center
→ white hotspot
```

---

# 35. ANIMATED TEXTURES

Если эффект использует animated texture:

структура:

```text
frame 1
frame 2
frame 3
frame 4
...
```

Каждый frame должен иметь одинаковый размер.

Анимация должна быть:

* циклической;
* последовательной;
* без резких случайных скачков.

---

# 36. ANIMATION CURVES

Не все параметры должны изменяться линейно.

Используй:

```text
Ease In
Ease Out
Ease In-Out
Pulse
Ping-Pong
```

Например:

```text
Scale:
0.0
 ↓
1.2
 ↓
1.0
```

Это лучше, чем:

```text
0
0.1
0.2
0.3
...
```

без easing.

---

# 37. PULSE

Для энергии:

```text
brightness
   ↑
   │    /\
   │   /  \
   │__/    \__
```

Можно пульсировать:

* scale;
* alpha;
* brightness;
* radius.

Не пульсируй всё одновременно с одинаковой частотой.

---

# 38. RANDOMNESS

Случайность должна быть контролируемой.

Используй random для:

* particle position;
* lifetime;
* rotation;
* size;
* velocity.

Но не для:

* основной формы;
* core;
* critical visual information.

---

# 39. DETERMINISTIC EFFECTS

Если эффект должен выглядеть одинаково:

используй deterministic seed.

Например:

```text
same ability
→ same core structure
→ controlled randomness
```

Это особенно полезно для:

* boss attacks;
* synchronized multiplayer effects;
* scripted animations.

---

# 40. SOUND + VFX

Сильный эффект должен синхронизироваться со звуком.

Например:

```text
Charge
→ low sound

Flash
→ impact sound

Explosion
→ deep sound
```

Не запускай звук каждый tick.

---

# 41. SCREEN EFFECTS

Screen effects использовать осторожно.

Возможны:

```text
Screen flash
Screen shake
Vignette
Color overlay
Blur
Distortion
Chromatic effect
```

Но они должны быть короткими.

---

# 42. SCREEN SHAKE

Screen shake использовать для:

* сильного удара;
* взрыва;
* boss attack;
* тяжёлого оружия.

Не использовать для:

* обычной частицы;
* каждого попадания;
* постоянной ауры.

---

# 43. SCREEN FLASH

Flash должен быть коротким:

```text
100%
 ↓
50%
 ↓
20%
 ↓
0%
```

Не оставляй экран ярким надолго.

---

# 44. VIGNETTE

Vignette можно использовать для:

* danger;
* low health;
* boss;
* dimensional effects.

Она должна усиливать состояние, а не мешать игре.

---

# 45. POST-PROCESSING

Post-processing использовать только если он действительно улучшает эффект.

Не добавляй:

* blur;
* distortion;
* chromatic aberration;

просто потому что это выглядит "круто".

Каждый post-process должен иметь конкретную цель.

---

# 46. PARTICLE TEXTURES

Particle texture должна соответствовать стилю мода.

Для pixel-art:

* nearest filtering;
* hard edges;
* limited palette;
* no anti-aliasing.

Типичные размеры:

```text
8×8
16×16
32×32
```

---

# 47. PARTICLE ATLAS

Если используется particle atlas:

проверь:

```text
UV
frame
animation
texture filtering
padding
```

Не допускай bleeding соседних particle textures.

---

# 48. TRANSPARENCY

Используй alpha осмысленно.

Например:

```text
Core = opaque
Glow = semi-transparent
Smoke = semi-transparent
Outer particle = transparent
```

Не делай всё alpha=0.5.

---

# 49. RENDER ORDER

Сложный эффект:

```text
Background Glow
 ↓
Outer particles
 ↓
Shockwave
 ↓
Main effect
 ↓
Core
 ↓
White hotspot
```

Порядок зависит от renderer.

---

# 50. DEPTH

Определи, должен ли эффект:

```text
проходить за блоками
```

или:

```text
быть поверх блоков
```

Например:

```text
World Effect
```

обычно учитывает depth.

А:

```text
Screen Overlay
```

не зависит от world depth.

Не смешивай эти системы.

---

# 51. BLOCK EFFECTS

Для блока:

```text
Block Position
 ↓
Local Coordinates
 ↓
Effect
```

Эффект должен корректно работать на любой позиции блока.

Не используй мировые координаты вместо локальных.

---

# 52. BLOCK BREAK EFFECT

Разделяй:

```text
Dust
+
Fragments
+
Impact
```

Фрагменты должны соответствовать текстуре блока.

Если ломается фиолетовый кристалл:

используй:

```text
stone fragments
+
purple fragments
+
cyan sparks
```

---

# 53. BLOCK ACTIVATION

Для активного блока:

```text
Idle
 ↓
Activation
 ↓
Active
 ↓
Deactivation
```

Каждая стадия может иметь отдельный VFX.

---

# 54. PROJECTILE TRAIL

Projectile должен иметь:

```text
Projectile Core
+
Trail
+
Particles
```

Trail должен зависеть от:

```text
previous position
current position
```

---

# 55. TELEPORT EFFECT

Телепортация:

```text
Charge
 ↓
Compression
 ↓
Flash
 ↓
Disappear
```

На новом месте:

```text
Flash
 ↓
Expansion
 ↓
Particles
 ↓
Fade
```

---

# 56. DEATH EFFECT

Death effect не должен обязательно быть взрывом.

Варианты:

```text
Dissolve
Collapse
Energy release
Soul particles
Fragments
Smoke
Teleport
```

Выбирай эффект в соответствии с природой entity.

---

# 57. SPAWN EFFECT

Spawn:

```text
small
→ expand
→ stabilize
```

Не используй огромный взрыв для обычного моба.

---

# 58. BOSS EFFECTS

Boss может использовать:

```text
Aura
Phase transitions
Attack telegraphs
Ground effects
Shockwaves
Screen effects
Particles
```

Но визуальная иерархия должна быть понятной.

---

# 59. ATTACK TELEGRAPH

Если атака опасна:

игрок должен получить визуальное предупреждение.

Например:

```text
Target area
 ↓
Growing ring
 ↓
Bright warning
 ↓
Attack
```

Telegraph должен показывать:

* область;
* направление;
* время до атаки.

---

# 60. GAMEPLAY INFORMATION

VFX должен помогать gameplay.

Например:

```text
Purple glow
= energy

Red glow
= danger

Cyan ring
= active state

White flash
= impact
```

Сохраняй эти ассоциации во всём моде.

---

# 61. MULTIPLAYER

VFX должен правильно разделять:

```text
Gameplay event
+
Client visual effect
```

Не обязательно отправлять каждый particle через сеть.

Предпочтительно:

```text
Server
 ↓
Event / Packet
 ↓
Client
 ↓
Local VFX
```

---

# 62. NETWORK PERFORMANCE

Не синхронизируй каждую частицу через network.

Плохо:

```text
particle 1 → packet
particle 2 → packet
particle 3 → packet
...
```

Хорошо:

```text
effect event → packet
                    ↓
             client generates
                  particles
```

---

# 63. CLIENT-SIDE VFX

Большинство чисто визуальных эффектов должны выполняться на клиенте.

Например:

```text
particles
glow
screen shake
screen flash
trail rendering
```

Но gameplay event должен исходить из корректного игрового состояния.

---

# 64. DISTANCE CULLING

На большом расстоянии не обязательно рендерить полный VFX.

Можно:

```text
Near
→ full effect

Medium
→ reduced effect

Far
→ minimal effect / none
```

---

# 65. LOD

Для тяжёлых эффектов:

```text
LOD 0
Full VFX

LOD 1
Reduced particles

LOD 2
Minimal effect

LOD 3
None
```

---

# 66. VFX OBJECT POOLING

Если проект содержит очень много часто создаваемых эффектов:

рассмотри pooling вместо постоянного создания объектов.

Особенно для:

* bullets;
* sparks;
* trails;
* repeated particles.

Но не усложняй архитектуру без необходимости.

---

# 67. ЭФФЕКТ ДОЛЖЕН БЫТЬ МОДУЛЯРНЫМ

Предпочтительно:

```text
EnergyEffect
├── Core
├── Glow
├── Sparks
└── Ring
```

а не:

```text
HugeEffectClass
```

с тысячами строк.

---

# 68. ПАРАМЕТРЫ ЭФФЕКТА

По возможности эффекты должны иметь параметры:

```text
color
size
lifetime
intensity
particleCount
speed
radius
duration
```

Например:

```java
EffectConfig(
    color,
    size,
    lifetime,
    intensity
)
```

Так один эффект можно переиспользовать.

---

# 69. EFFECT PRESETS

Создавай presets:

```text
SMALL_IMPACT
LARGE_IMPACT
ENERGY_CHARGE
ENERGY_EXPLOSION
SWORD_SLASH
TELEPORT
BOSS_PHASE
```

Это уменьшает дублирование.

---

# 70. EFFECT COMPOSITION

Сложный эффект создавай композиционно:

```text
Effect
├── ParticleEffect
├── TrailEffect
├── RingEffect
├── GlowEffect
└── ScreenEffect
```

Каждый компонент отвечает за свою часть.

---

# 71. VFX STYLE ANCHOR

Если пользователь показывает существующий эффект:

используй его как:

**VFX STYLE ANCHOR.**

Извлеки:

```text
Color
Shape
Particle density
Motion
Lifetime
Glow
Contrast
Timing
```

После этого новые эффекты должны использовать тот же язык.

---

# 72. НЕ КОПИРОВАТЬ ЭФФЕКТ БУКВАЛЬНО

Если пользователь говорит:

> "сделай остальные эффекты как этот"

это означает:

сохранить:

```text
style
+
palette
+
motion language
+
intensity
+
visual hierarchy
```

но не обязательно копировать форму один-в-один.

---

# 73. VFX И ТЕКСТУРЫ

Если эффект использует PNG:

он должен соответствовать texture skill.

То есть:

```text
Pixel art
+
Limited palette
+
No anti-aliasing
+
Nearest filtering
+
Transparent background
```

---

# 74. VFX И UI

Если эффект появляется в интерфейсе:

используй UI skill.

Например:

```text
HUD
+
energy pulse
+
notification
```

Не используй world-space particle для обычного UI без необходимости.

---

# 75. VFX И АНИМАЦИЯ МОДЕЛЕЙ

Если эффект связан с моделью:

```text
ModelPart
 ↓
World position
 ↓
VFX anchor
 ↓
Particle
```

Например:

```text
Hand
 ↓
Sword
 ↓
Slash effect
```

Эффект должен следовать за реальным движением ModelPart.

---

# 76. VFX ANCHORS

Для сложных моделей создавай логические точки:

```text
head
chest
left_hand
right_hand
weapon_tip
weapon_guard
feet
core
```

Это позволяет легко привязывать VFX.

---

# 77. ЭФФЕКТЫ ОРУЖИЯ

Для оружия разделяй:

```text
Idle
Attack
Hit
Charge
Special Ability
```

Например:

```text
Katana

Idle
→ tiny energy particles

Attack
→ blade trail

Hit
→ sparks + impact

Special
→ slash wave + shockwave
```

---

# 78. НЕ ПЕРЕГРУЖАЙ IDLE

Постоянный эффект должен быть значительно слабее активной способности.

```text
Idle:
10%

Attack:
60%

Ultimate:
100%
```

Это условная визуальная шкала, а не обязательные значения.

---

# 79. EFFECT INTENSITY

Интенсивность зависит от:

```text
importance
rarity
power
danger
duration
```

Обычный action:

```text
low
```

Редкий ability:

```text
high
```

Ultimate:

```text
very high
```

---

# 80. TELEGRAPH VS IMPACT

Никогда не путай:

```text
Telegraph
```

и:

```text
Impact
```

Telegraph:

```text
медленный
читаемый
предупреждающий
```

Impact:

```text
короткий
яркий
мощный
```

---

# 81. VFX TIMELINE

Для сложного эффекта сначала создавай timeline.

Например:

```text
0 ms
│
├── charge starts
│
100 ms
│
├── particles accelerate
│
250 ms
│
├── flash
│
300 ms
│
├── impact
│
400 ms
│
├── shockwave
│
700 ms
│
└── fade
```

Это помогает синхронизировать компоненты.

---

# 82. ЭФФЕКТ ДОЛЖЕН ИМЕТЬ BEGINNING / MIDDLE / END

Хороший эффект обычно имеет:

```text
BEGIN
 ↓
BUILD
 ↓
PEAK
 ↓
FADE
```

Даже если он длится всего несколько тиков.

---

# 83. LOOPING EFFECT

Для постоянных эффектов:

```text
Aura
Energy Core
Portal
Machine
```

используй цикл.

Цикл не должен иметь очевидного резкого шва.

---

# 84. PARTICLE RANDOMNESS НЕ ДОЛЖНА ЛОМАТЬ СИЛУЭТ

Если эффект имеет:

```text
ring
beam
slash
circle
glyph
```

основная форма должна оставаться стабильной.

Random particles используются вокруг неё.

---

# 85. ЭФФЕКТЫ ДОЛЖНЫ БЫТЬ ЧИТАЕМЫ НА РАССТОЯНИИ

Проверь:

```text
close
medium
far
```

Основная форма должна сохраняться.

---

# 86. НЕ ИСПОЛЬЗОВАТЬ СЛИШКОМ МНОГО БЕЛОГО

Белый:

```text
highest brightness
```

Используй его только для:

* core;
* flash;
* hotspot;
* strongest impact.

---

# 87. ЭНЕРГЕТИЧЕСКИЙ ЯЗЫК

Если проект использует Opus energy:

```text
Purple
+
Cyan
+
White core
```

можно установить правила:

```text
Purple
= raw / stable energy

Cyan
= active / flowing energy

White
= maximum concentration
```

После этого придерживайся этих значений во всём моде.

---

# 88. VFX MATERIAL LANGUAGE

Эффекты тоже должны различать материалы.

### Metal

```text
sharp sparks
```

### Stone

```text
dust
+
fragments
```

### Crystal

```text
shards
+
glow
```

### Energy

```text
particles
+
rings
+
core
```

### Fire

```text
rising
+
flickering
```

---

# 89. DEBUG MODE

Для сложных эффектов можно добавить debug rendering:

```text
particle bounds
anchor points
effect origin
direction
radius
collision point
```

Debug mode не должен использоваться в production rendering.

---

# 90. BUILD И TEST

После реализации:

```text
Build
 ↓
Launch
 ↓
Trigger effect
 ↓
Check rendering
 ↓
Check timing
 ↓
Check performance
 ↓
Check multiplayer
```

Не считать эффект готовым только потому, что код компилируется.

---

# 91. ПРОВЕРКА VFX

Перед завершением:

```text
[ ] правильная палитра
[ ] правильный силуэт
[ ] правильный motion
[ ] правильный lifetime
[ ] правильная интенсивность
[ ] правильный glow
[ ] правильный transparency
[ ] правильный render order
[ ] нет z-fighting
[ ] нет texture bleeding
[ ] нет excessive particles
[ ] нет memory leaks
[ ] нет client crash
[ ] нет server crash
[ ] multiplayer работает
[ ] эффект читается на расстоянии
[ ] эффект соответствует стилю мода
```

---

# 92. PERFORMANCE CHECK

Если эффект запускается часто:

проверь:

```text
particles per second
active effects
render calls
texture switches
allocations
network traffic
```

Особенно внимательно проверяй эффекты:

* массовых мобов;
* оружия;
* projectiles;
* machines;
* explosions.

---

# 93. НЕ ЛОМАТЬ ИГРОВОЙ ПРОЦЕСС

VFX никогда не должен:

* изменять gameplay случайно;
* менять hitbox;
* менять damage;
* менять physics;
* ломать AI;
* блокировать input;

если это не является частью gameplay механики.

---

# 94. CLIENT / SERVER RULE

Разделяй:

```text
GAMEPLAY
+
VISUAL
```

Gameplay:

```text
Server authoritative
```

Visual:

```text
Client-side
```

---

# 95. EFFECT EVENT SYSTEM

Если в проекте много VFX:

создай централизованную систему:

```text
VFXManager
```

Например:

```java
VFXManager.spawn(
    VFXType.ENERGY_EXPLOSION,
    position
);
```

Но не создавай такую систему, если проект маленький и она не нужна.

---

# 96. VFX REGISTRY

Для большого проекта можно использовать:

```text
VFXType
```

Например:

```text
ENERGY_SPARK
ENERGY_RING
SWORD_SLASH
IMPACT
TELEPORT
EXPLOSION
AURA
```

Это упрощает управление эффектами.

---

# 97. ОПИСАНИЕ НОВОГО ЭФФЕКТА

Перед реализацией нового сложного VFX опиши:

```text
Name:
Purpose:
Trigger:
Duration:
Color:
Shape:
Motion:
Particles:
Glow:
Sound:
Screen effect:
Performance:
```

Пример:

```text
Name:
Opus Slash

Purpose:
Powerful katana attack

Duration:
~0.4 sec

Color:
Purple + Cyan + White

Shape:
Arc

Motion:
Forward

Particles:
Sparks

Glow:
Cyan edge

Impact:
Shockwave
```

---

# 98. ПРИОРИТЕТЫ

При создании эффекта:

```text
1. Gameplay readability
2. Silhouette
3. Timing
4. Motion
5. Color
6. Intensity
7. Detail
8. Decoration
```

Не жертвуй читаемостью ради количества частиц.

---

# 99. ГЛАВНЫЙ RULE

Перед завершением VFX задай себе:

### 1. Понятно ли, что произошло?

### 2. Понятно ли, где находится эффект?

### 3. Понятно ли направление движения?

### 4. Понятно ли состояние объекта?

### 5. Не мешает ли эффект игроку?

### 6. Соответствует ли эффект стилю мода?

### 7. Не слишком ли много частиц?

### 8. Работает ли он технически корректно?

Если хотя бы один ответ:

```text
NO
```

эффект не считается готовым.

---

# 100. ФИНАЛЬНАЯ ЦЕЛЬ

Все эффекты мода должны ощущаться частью одной VFX-системы:

```
                VFX SYSTEM
                     │
   ┌─────────────────┼─────────────────┐
   │                 │                 │
PARTICLES          TRAILS          IMPACTS
   │                 │                 │
Energy             Weapons          Hits
Smoke              Projectiles      Explosions
Sparks             Magic            Shockwaves
   │                 │                 │
   └─────────────────┼─────────────────┘
                     │
                WORLD EFFECTS
                     │
           ┌─────────┼─────────┐
           │         │         │
         AURA      BOSS      MAGIC
           │         │         │
           └─────────┼─────────┘
                     │
                SCREEN VFX
                     │
             Flash / Shake
             Overlay / Distortion
```

Главное правило:

**НЕ СОЗДАВАЙ ОТДЕЛЬНЫЕ ЭФФЕКТЫ. СОЗДАВАЙ ЕДИНУЮ VFX-СИСТЕМУ МОДА.**

Каждый эффект должен иметь собственные:

**форму + движение + время + интенсивность + цвет + причину существования.**

```
```
