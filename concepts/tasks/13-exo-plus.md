# Задача 13 — EXO+ (Exo-6 «Exo+») — экспериментальный экзоскелет

Новый оседлываемый экзоскелет фракции EXO. Спроектирован с нуля: механики, способности, анимации, 3D-модель, звуки, управление, интерфейс.

> Редакция 2 — переработка способностей + новый визуальный концепт (красно-чёрно-золотой).
> Редакция 3 (2026-08-23) — МОДЕЛЬ И АНИМАЦИИ ПО РЕФЕРЕНСУ. §1–§3, §6–§10 сохраняем; §4 «3D-модель», §5 «Анимации», §11 «Файлы» переписаны (секция «РЕДАКЦИЯ 3» ниже приоритетна для геометрии/анимаций).

---

## РЕДАКЦИЯ 3 — МОДЕЛЬ ПО РЕФЕРЕНСУ (2026-08-23)

### 0. Постановка
Пересобрать `exo_plus.geo.json` + `exo_plus.png` + `exo_plus.animation.json` так, чтобы мех выглядел как референс (T-поза, красный/чёрный мех с золотом):
- **Стеклянный кокпит-канапа** с видимым пилотом (шлем + тёмный костюм);
- **Лицо-«злоба»** под канапой: чёрная панель, два красных косых светящихся глаза;
- **Две наплечные пушки** (чёрные, длинные, угол вверх-вперёд, золотой обод);
- **Две катаны за спиной**: золотая головка рукояти НаД плечом, золотая кромка-клинок к бедру (диагональ);
- **Два чёрных выхлопа-трубы** за канапой;
- **Клешни-кисти** (пальцы); корпус с красной чешуйчатой бронёй (рыбий узор), золотые канты; тяжёлые ноги (красные колени/голени, чёрные стопы с золотым мыском), золотые ромбы на плечах;
- Габарит: 4.5 блока (72px, как `ExoPlusRenderer.MODEL_HEIGHT`).

### 1. Решения
| Факт | Решение |
|---|---|
| старый geo: 26 костей/97 кубов, нет кокпита/пилота/катан/клешней/лица | полная пересборка геометрии с нуля под референс |
| БАГ старой модели: правые пивоты = левым (`right_arm_joint` pivot −20 вместо +20) | новый генератор сам зеркалит X: `left_*`→`right_*` с корректными пивотами |
| в анимациях НЕТ hurt/death/block → мех не реагирует | расширяем до 10 анимаций (idle/walk/attack/hurt/death/block/ability×4) |
| стекло канапы: GeckoLib cutout (alpha<0.5 невидимо) | канапа = рамка + диагональные блики `glass` (alpha0 в центре); пилот виден в проём |
| косая катана требует повёрнутых кубов | каждая катана = 1 кость, клинок — куб с `rotation`+`pivot` (GeckoLib 4.4.9 поддерживает, подтверждено байткодом BakedModelFactory) |
| знаки анимаций | переиспользуем проверенные значения старого файла (left_arm Z −55 / right +55 и т.п., конвенция §2 задачи 14) |

### 2. Скелет (29 костей)
```
the_omen (root)
├── left_hip_joint(-4,45) → left_thigh_joint(-10,45) → left_calf_joint(-10,26)
│     └── left_foot_joint(-10,9)      (правые IDENTICAL через зеркало, x>0)
└── hips(0,45)
    ├── torso(0,56)
    │   ├── head(0,70)             — канапа: рамка/стекло/крыша
    │   │   └── pilot(0,68,−2)     — пилот внутри (виден через проём)
    │   ├── face(0,60,−7)          — чёрная панель + красные глаза (эмиссив)
    │   ├── left_exhaust(−5,72,+4) / right_exhaust — выхлопы
    │   ├── left_katana(−11,68,−4) / right_katana — куб-клинок с rotation
    │   ├── left_pauldron(−17,63) → left_cannon(−14,66,+1)
    │   ├── right_pauldron → right_cannon
    │   ├── left_arm_joint(−20,62) → left_arm_joint2(−20,46) → left_hand(−20,30) — клешня
    │   └── right_* (зеркало)
    └── (пояс-лоялти в hips)
```
Имена старых костей (`left_arm_joint` и т.д.) сохранены — знаковые значения анимаций (old walk/slam/chest_shot) переносятся без изменений.

### 3. Палитра + новые маляры
D `#4A0E0E` / R `#8B1A1A` / r `#B32D2D`; B `#141414` / b `#262626` / e `#3D3D3D`; g `#6E6E6E` / l `#9A9A9A`; G `#8A6D1F` / A `#D4A017` / Y `#FFC93C` / W `#FFF5C8`.

Новые маляры (процедурные, pixel-art, свет сверху-слева): `black` (панели-швы), `red` (чешуя на груди), `red_plate`, `red_dark`, `gold` (вертикальный градиент G→A→Y), `gold_bright` (эмиссив W), `metal`, `metal_dark`, `eye` (2 косых красных глаза), `glass` (диагональные блики, центр alpha=0), `pilot` (шлем+визор+костюм), `claw` (тёмная сталь + светлые кончики), `katana` (золотой клинок + красно-чёрная рукоять), `stack` (чёрные трубы с тёмно-красными кольцами), `decal` (белые царапины «WRZ06» на бедре).

### 4. Анимации (10 шт., exo_plus.animation.json)
| Имя | Тип | Длина | Суть |
|---|---|---|---|
| idle | loop | 2.0с | дыхание торса, пульс ядра, глаза/стекло-микродвижение |
| walk | loop | 1.4с | поступь: бедро ±28, голень, стопа, руки §22, таз ±4 |
| attack | play | 1.0с | удар КЛЕШНЕЙ справа: замах −55 → семец 45, торс-твист |
| hurt | play | 0.6с | отшатывание (раньше отсутствовало!) |
| death | play | 2.0с | падение вперёд (таз 45, торс 70, колени −75) |
| block | loop | 1.0с | руки в хватке перед собой (X 60/70) |
| ability_extra | play | 0.7с | руки враскат (Z ∓55), пушки-отдача, ядро 1.4× |
| ability_laser | play | 0.6с | грудь +18, руки враскат, вспышка ядра |
| ability_slam | play | 1.0с | руки −130 → +40, присед (бёдра+торс+колени), вспышка |
| ability_ultra | play | 2.0с | заряд (0–0.8): ядро 1→1.6, глаза-вспышка → выстрел (0.8–1.2): пушки вверх, торс −5 → рассеивание (1.2–2.0) |

Длительности = `Exo6Plus.abilityAnimDuration`: extra 14т=0.7с, laser 12=0.6с, slam 20=1.0с, ultra 40=2.0с.

### 5. Инструмент
`generate_exo_plus_entity.py` (по образцу haiku_model_kit): 1) абсолютные кубы в финальной системе авторинга (левая X<0, фронт −Z, конвенция старого geo, без kit-finalize); 2) процедурный атлас 256×256 (first-fit-decreasing, 1px отступ); 3) зеркало left→right с общими UV; 4) anims JSON 1.8.0; 5) QA: миссы=0, DANGER=0, UV в границах, FK-маркеры (подошвы y≈0, катана: остриё у бедра/рукоять над плечом, кисти у бёдер); 6) превью-рендерер (реплика GeckoLib-конвейера: load-мироринг x в constructCube, negation X/Y в анимациях) → PNG front/side/back для сверки с референсом.

### 6. Целевые файлы
- `src/main/resources/assets/opusvsexe/geo/entity/exo_plus.geo.json` (пересборка)
- `.../textures/entity/exo_plus.png` (256×256)
- `.../animations/entity/exo_plus.animation.json` (10 анимаций)
- `generate_exo_plus_model.py` (генератор)
- Java и рендерер НЕ меняются (MODEL_HEIGHT 4.5 сохранён).

### 7. СТАТУС: ВЫПОЛНЕНО (2026-08-23)
- **geo:** 28 костей/95 кубов/256×256/1.12.0, `geometry.exo_plus`, visible_bounds 6.0×6.0 offset [0,2.6,0].
- **скелет:** the_omen → hips → torso → {head, pilot, face, back_reactor, chest_core, паулдроны→пушки, катаны, рука (arm_joint→arm_joint2→hand-клешня)}; ноги hip→thigh→calf→foot. left на x<0, right — зеркало генератора (баг старых пивотов устранён).
- **текстура:** 17 процедурных маляров; FFD-атлас; стекло = рамка+блики (центр alpha 0, пилот виден); глаза косые красные (эмиссив), ядро gold_glow.
- **анимации:** 10 шт. (idle 2.0, walk 1.4, attack 1.0, hurt 0.6, death 2.0, block 1.0, extra 0.7, laser 0.6, slam 1.0, ultra 2.0с); длительности = abilityAnimDuration (14/12/20/40 т).
- **QA:** миссов 0, DANGER 0, UV в границах 256², подошвы y=0, пивоты L/R зеркальны. Gradle BUILD SUCCESSFUL; jar содержит 3 файла.
- **превью:** `preview_exo_plus_render.py` (реплика BakedModelFactory + AnimationProcessor + RenderUtils, матрицы 4×4, painter-сортировка) → `build/preview_exo_plus/*.png` (9 кадров); сверено с референсом (канапа+пилот, глаза, пушки, катаны, клешни).
- TODO: золотые «полосы» в чёрной текстуре местами читаются вертикально (крайние UV-полосы атласа) — при желании даунскейл и контрастная окантовка паулдронов.

---

## 1. Концепт и лор-позиция
- **EXO-1..5** — линейка Кодди; EXO-5 «Возмездие» — его незавершённый последний проект (лор.md).
- **EXO+ (EXO-6)** — пост-человеческая реконструкция: машина, собранная ПОСЛЕ гибели Кодди из обломков всех пяти рамок и остатков Опуса. Не серийная рамка, а единственный гибрид-«плюс»: самое мощное шасси, но нестабильное.
- Фирменная черта: **4 слота способностей** (первая в линейке), включая ульту **«Катаклизм»** (небесный луч) и **«Экстра-лазер»** (широкий луч, взрывает местность).
- Визуально — **красно-чёрно-золотой**: красная броня, чёрный силовой каркас, золотая отделка и светящееся золотое ядро.

Идентификаторы: entity id `exo_6_plus`, класс `Exo6Plus`, tier `ExoTier.EXO_6`, имя «EXO-6 Exo+», яйцо `exo_6_plus_spawn_egg`.

## 2. Характеристики (ExoTier.EXO_6)
HP 400 | Энергия 2500 | Скорость 0.28 | Спринт 1.45 | Урон 24 | Дальность 10.0 | Кулдаун атаки 18 | Прыжок 1.05 | Высота шага 2.0 | Реген энергии 14 | Дренаж 4 | Сопр. отбросу 1.0 | Броня 24 | Воздушный рывок да. Hitbox `3.0 × 9.0`.

## 3. Способности (4 слота, F/G/H/J)
- **Слот 0 «Экстра-лазер»**: цена 150, кулдаун 200. Новая `ExtraLaserBeamEntity` (наследует `BlasterBeamEntity`): как heavy_blaster_beam, но **в 2.5× толще** (Ø1.25). Каждый блок на пути взрывается как TNT (`Level.ExplosionInteraction.TNT`, сила 2.0, до 12 взрывов). Спавн из груди по прицелу (clip по блокам). Рендер `ExoExtraLaserRenderer`: та же геометрия, золотая текстура `extra_laser_beam.png`, scale X/Y ×2.5. Анимация `ability_extra`. Звук `exo_extra`.
- **Слот 1 «Фотонный пик»**: цена 200, кулдаун 80. Реюзает `heavy_blaster_beam` (`HeavyLaserBeamEntity`). Луч из груди вдоль взгляда. Анимация `ability_laser` (руки в стороны, ядро вспыхивает). Звук `exo_laser`.
- **Слот 2 «Сейсмический удар»**: цена 250, кулдаун 120. `CombatEffects.shockwave` (радиус 6.5, урон ×0.9, отброс 3.2, подброс). Анимация `ability_slam`. Звуки `exo_slam` + `shockwave`.
- **Слот 3 «Катаклизм» (УЛЬТА)**: цена 500, кулдаун 400 (20с). Реюзает `sky_laser` (`SkyLaserEntity`), спавн в точке прицела (clip). Звуки `exo_ultra` + `super_laser` (в точке). Анимация `ability_ultra` (заряд→выстрел→рассеивание).

## 4. 3D-МОДЕЛЬ (переработана: красно-чёрно-золотая)
### 4.1 Силуэт и палитра
Высокий тяжёлый гуманоидный мех (~4.5 блока). Широкие паулдроны, слоистая красная броня поверх чёрного каркаса, золотая отделка (канты, наколенники, эмблемы, клёпки) и золотое ядро.
Палитра 256×256: D `#4A0E0E` темно-красный, R `#8B1A1A`, r `#B32D2D`, B `#141414` чёрный, b `#262626`, e `#3D3D3D`, g `#6E6E6E`, l `#9A9A9A`, G `#8A6D1F` тёмное золото, A `#D4A017`, Y `#FFC93C`, W `#FFF5C8` горячее ядро.
UV-зоны: чёрные узлы [0..48, 0..48]; золотой металл [0..48, 48..96]; красная броня [48..208, 0..208]; золотое эмиссивное свечение [208..256, 208..256].

### 4.2 Костный каркас (26 костей, 97 кубов)
```
root (the_omen) ─ hips ─ torso ─ { head (золотой визор, красный гребень),
  chest_core (золотое ядро-реактор, emissive), back_reactor (горб + выхлопы),
  left/right_pauldron, left/right_cannon, left_arm_joint → arm_joint2 → hand
  (→ left_wrist_emitter), right_arm_joint → arm_joint2 → hand (→ right_wrist_emitter) },
  hips: left/right_hip_joint → thigh_joint → calf_joint → foot_joint
```
Кости `hips/torso/head/arm_joint/arm_joint2/foot_joint/calf_joint` сохранены — idle/walk/attack работают без правок базы.

### 4.3 Детализация
Голова: золотой визор-щель, красный гребень, нащёчники. Грудь: вдавленное золотое ядро в чёрном кольце. Спина: горб-реактор с выхлопами. Плечи: паулдроны + пушки с золотыми дулами. Руки: красные пластины на чёрных валах, золотые кольца у локтя/запястья, запястный излучатель. Ноги: слоистые пластины, золотые наколенники, тяжёлые ступни с золотым кантом. Износ: царапины, тёмные швы, предупреждения.

### 4.4 Промт генерации (полный)
EN-промт (сокращённо): Create a detailed 3D model of a large humanoid combat mech in dark military sci-fi, Minecraft Java (Blockbench/GeckoLib), RED+BLACK+GOLD palette. OVERALL SILHOUETTE — tall heavily armored humanoid, broad chest, angular pauldrons, narrow waist, thick legs, long arms, recessed gold chest reactor, shoulder laser cannons, narrow head with glowing gold visor. STYLE — layered crimson-red armor over black frame, gold trim/rivets/knee-wrist bands + glowing gold core, worn metal, exposed joints/pistons/cables/vents/bolts/seams/warnings/scratches/dirt, functional engineering. COLOR — armor crimson (#8B1A1A,#B32D2D,#4A0E0E), structure black/charcoal (#141414,#262626,#3D3D3D), details gunmetal (#6E6E6E)/light metal (#9A9A9A), glow gold (#D4A017→#FFC93C→#FFF5C8, sparingly). TORSO — layered red plates over black spine, recessed gold reactor in black ring, gold emblem below, gold buckle. HEAD — no face, gold visor slit, red crest fin, red cheek guards. SHOULDERS — angular pauldrons (red/gold trim) carrying laser cannons (black housing, gold lens). ARMS — red upper plates, black elbow joints + gold bands, thick forearms, black hands with gold knuckles, small gold wrist emitters. BACK — rear reactor hump (gold exhausts) cabled to cannons. LEGS — layered red thighs, black knees + gold caps, red shins, large feet with gold toe trim. REQUIREMENTS — hard-surface, strong mechanical segmentation, symmetrical (build left mirror right), correct pivots for hips/torso/head/chest_core/back_reactor/pauldrons/cannons/arm joints/hands/wrist emitters/legs; separate movable bones, no organic anatomy/superhero/excessive neon/fantasy ornament. FINAL — heavy military combat mech: crimson armor + black frame + gold trim + glowing core + shoulder cannons + wrist emitters + heavy legs.

## 5. Анимации (exo_plus.animation.json)
| Имя | Тип | Описание |
|---|---|---|
| `idle` | loop | «дыхание» торса, пульс ядра, лёгкий дрейф рук |
| `walk` | loop | тяжёлая поступь, рука↔нога |
| `attack` | play | удар кулаком |
| `ability_extra` | play | руки раздвигаются, ядро/пушки разгораются (0.7с) |
| `ability_laser` | play | выстрел из груди, руки в стороны (0.6с) |
| `ability_slam` | play | руки вверх→вниз, присед (1.0с) |
| `ability_ultra` | play | заряд→выстрел→рассеивание (2.0с) |

Триггер — хук `abilityAnimName(slot)` (synced-поле, сервер ставит, клиент играет). Перезапуск одноразовых — forceAnimationReset (методичка §9.1).

## 6. Управление
WASD/мышь — движение/поворот; ПКМ — сесть, Shift+ПКМ — инвентарь; ЛКМ — ближняя атака; Пробел — прыжок/воздушный рывок; Ctrl — спринт; **F** слот 0, **G** слот 1, **H** слот 2, **J** слот 3 (ульта); R — отсеки костюма.

## 7. HUD
Имя рамки, шкала корпуса, шкала энергии, 4 строки способностей (F/G/H/J) с кулдаунами; ульта готова — золотая. Панель расширена под 4 строки.

## 8. Звуки
`exo_laser`=лазерный залп, `exo_slam`=низкий удар, `exo_ultra`=протяжный луч, `exo_extra`=мощный луч+взрыв. Переиспользуются: `super_laser`, `shockwave`, `radio_explosion`, `exo_thrust`. Текстура `extra_laser_beam.png` (золотая, hue-shift из heavy_blaster_beam). Звуки синтезированы ffmpeg.

## 9. Сущности лучей
`heavy_blaster_beam` (HeavyLaserBeamEntity), `sky_laser` (SkyLaserEntity), `extra_laser_beam` (**ExtraLaserBeamEntity, новая**) — BlasterBeamEntity с tick(): широкий луч (1.25) + TNT-взрывы. Рендер ExoExtraLaserRenderer.

## 10. Изменения базы
`ExosuitEntity`: `ABILITY_SLOTS` 3→4, `DATA_COOLDOWNS` INT→LONG, хук `abilityAnimName/abilityAnimDuration/startAbilityAnim` + synced `DATA_ABILITY_ANIM`. Обратно совместимо.

## 11. Файлы (результат)
Код: `entity/custom/Exo6Plus.java`, `entity/ExtraLaserBeamEntity.java` (new), `entity/BlasterBeamEntity.java` (+getShooterUUID), `registry/ModEntities.java` (+EXTRA_LASER_BEAM), `sound/ModSounds.java` (+EXO_EXTRA), `client/model/ExoExtraLaserModel.java` + `client/renderer/ExoExtraLaserRenderer.java` (new), `client/OpusVsExeClient.java`.
Ассеты: `geo/entity/exo_plus.geo.json`, `textures/entity/exo_plus.png`, `textures/entity/extra_laser_beam.png`, `animations/entity/exo_plus.animation.json`, `sounds/exo_extra.ogg`, `sounds.json`.

## 12. TODO
- [ ] Баланс Экстра-лазера (TNT разрушает чужую местность в мультиплеере; нет защиты территорий).
- [ ] Отдельный гул-луп для Экстра-лазера (сейчас одноразовый).
- [ ] Дроп с EXO+ при гибели.