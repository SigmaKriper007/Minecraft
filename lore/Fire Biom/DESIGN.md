# DESIGN — Fire Biom: «Предел Четырёх Жил»

> Авторитетный design/implementation spec. Версия: rebuild 2026-08-24.
> Координаты GeckoLib: X вправо, Y вверх, фронт −Z; pivot в физическом суставе.

## 1. Художественная система
**Силуэт и материалы.** Идентичность: обугленная тропическая чаща, разрезанная серно-белыми жилами жара. Контраст — матовая почти чёрная оболочка против узких горячих разломов. Красный криолёд только для печати босса (не смешивать с лавой). Приоритет чтения: силуэт → крупные группы материала → активное ядро → функциональные детали → износ. Белый = пик энергии, ≤3% площади.

**Палитра:** void coal `#160B0D` (контур/тень), basalt `#2B1B20` (камень/броня/рога), plum ash `#4A2630` (листва/тень), blood glass `#8F1F32` (криолёд/акцент), vermilion `#D33A1F` (внешнее пламя), molten orange `#F06A1A` (активная лава), sulfur gold `#FFB52A` (сердечники/жилы), hot ivory `#FFF2B0` (hotspot), ash grey `#71636A` (пепел). Pixel-art: без blur/AA, свет сверху-слева, кластеры 1–4px, дизеризация только между соседними оттенками. Блоки 16×16, предметы 32×32, сущности 64×64/128×128, броня 128×128.

## 2. Пространство и генерация
**Измерение** `fire_realm`: плоское открытое, `fixed_time: 18000`, `ultrawarm: true`, без осадков. Nether sky effects (красное небо, плотный туман); client ambience — медленный пепел, редкие угли. Поверхность: 1 слой `fire_soil`, 4 слоя `magma_crust`, базальт.

**Центральная кальдера** в (0, surfaceY, 0) (идемпотентно, чтобы не дублировалась): остров Тишины r15–18 (неровный край), лавовая Корона вн.r29/вш.r37/гл.2, обожжённый берег 2–4 блока, 4 реки с N/E/S/W в Короне, длина жилы 96–120, базовая ширина 3–5, ось = seeded random walk + низкочастотная синусоида, ширина меняется каждые 9–17 блоков, изредка рукав/заводь, соседние с лавой блоки → жильная кора, прямой участок длиннее 8 блоков запрещён.

**Жарокапок:** высота 14–23; ствол 2×2 внизу/1×1–2×1 выше; 4 досковидных корня 3–6; на 60–80% высоты 3–5 ветвей; крона асимметрична r4–7 с воздушными окнами; 3–8 лиан 2–9; 1–4 жар-боба со стадией 0–2. Бобы на `AGE`; лиана — тонкий collision-free cutout, ломается водой/поршнем.

## 3. Блоки
- `fire_soil` — пепельный дёрн, тлеющие прожилки.
- `magma_crust` — базальтовая подложка, свет 3.
- `ash_block` — рыхлый пепел, глухой звук, слегка замедляет.
- `ember_log` — направленное волокно (axis), срез — серно-золотое кольцо.
- `ember_leaves` — чёрно-багровые листья с прозрачными окнами, свет 3.
- `fire_vine` — крестовая тонкая модель, cutout, без collision.
- `fire_bean` — горизонтальный стручок, 3 стадии, на зрелой свет 7.
- `crimson_ice` — полупрозрачный faceted, красные грани, свет 2.
- `fire_portal` — двухплоскостная анимированная мембрана.

## 4. Существа и модели
### 4.1 Огненный Слизень
3 размера: S 0.7×0.65, M 1.15×1.05, L 1.7×1.55; geo в размере M, масштабируется с hitbox. Скелет `root→shell→core`; shell: brow_l/r, mouth, cracks; core не делит pivot с shell. Силуэт — приплюснутая капля (юбка шире плеч). Баланс HP 12/24/42, урон 3/5/8, взрыв 2.8/4.2/6.0. Смертельный урон → CHARGING на 50 тиков (не атакует, не движется, повреждаем; доп. удар сокращает таймер максимум на 8 тиков). Анимации: idle 2.0s loop (core дрейф, shell 1.00→1.04), hop 0.7s (squash 0–.12, launch .12–.25, air .25–.52, landing .52–.65), hurt 0.25s, charge 2.5s (core сжимается, shell →1.45, crack пульсируют, дрожь ускоряется), explode 0.2s (flash→radial fragments→smoke).

### 4.2 Лавовый Голем
Hitbox 1.35×2.75. Силуэт: широкие плечи, узкая талия-щель, длинные предплечья, ступни-якоря; плиты не монолит, между ними жар. Скелет `root/body/core/head/jaw`, руки плечо→предплечье→кисть, ноги бедро→голень→ступня, пивоты в суставах. HP 72, урон 10, броня 8, KB-resist 0.85. Лава лечит 1HP/20т, вода — 2 урона/20т, удар поджигает на 5с. Анимации: idle 2.4s (плиты расходятся на вдохе), walk 1.1s, attack 0.85s (anticipation .25/impact .12/recovery .48), hurt 0.3s, death 1.4s (осыпание плит).

### 4.3 Огненный Демон
Hitbox 1.3×3.6, eye height ~3.05. Массы: рогатая голова, треугольный торс, digitigrade legs, складные крылья, трезубец. Рога 3 сегмента; крылья плечо/предплечье/два пальца + перепонки. Криолёд — отдельные bones `ice_front/back/left/right/top`, видны только в sealed. Скелет `root→pelvis→torso→chest→neck→head→jaw/horns`; руки/ноги из 3 суставов; `wing_l/r→wing_forearm→wing_finger_a/b`; `weapon_anchor→trident`; VFX anchors mouth/hand_r/chest_core/wing_tip_l/r.

Фаза SEALED: 150 HP криольда, не двигается, не получает health damage; не-fire урон трескает печать. При 0 — 40-тиковое пробуждение (трещины → втягивание света → разлёт 6 крупных+24 малых осколков → крылья → рёв) → bossbar 400 HP.

Действия кодируются синхронизированным enum/action timer: melee 18t(impact 9, 12 урона), toss 30t(impact 16, 8 урона, horiz 1.45/vert 1.05), fireball 34t(spawn 20, орб к правой ладони), aura 52t(telegraph 24, затем 3 волны через 6), trident 36t(release 17, возврат), summon 44t(ровно 5 малых слизней на кольце r3.5), hurt 8t, death 50t. Cooldown по дистанции: toss 7s, fireball 5s, aura 10s, trident 8s, summon 15s; одна action одновременно; опасные останавливают navigation на wind-up.

## 5. Снаряды и оружие
**Зародыш Короны:** hitbox 0.55; модель outer cage из 8 дуг + вращающийся core + 3 orbit shards + хвостовой anchor; разные скорости вращения для динамики. Попадание: 8 урона, взрыв r/сила 6.0 fire=true (поджог); owner исключён из collision. Взрыв: 1t ivory flash, 8t ring, 16t sparks/debris, 30t smoke. У игрока: заряд 16t, cooldown 40t.

**Демонический трезубец:** шестигранное древко, длинный центр. зуб, 2 расходящихся боковых, гарда-кольцо, рукоять. Bones root/shaft/head/prong_center/prong_l/prong_r/core/trail_anchor. Полёт по дуге, impact — взрыв 4.0+поджог, затем RETURNING (noPhysics, ускорение к owner). Предмет не дублируется: stack получает урон+cooldown, снаряд исчезает при возврате.

## 6. Огненная броня
Без ванильной плоской текстуры: каждый слот — 3D-геометрия с visibility по слоту.
- helmet: маска/затылок/щёки/два 3-сегментных рога; chest: пластины/core/ворот/наплечники/наручи/перчатки/спинная рама; wings: корень/плечо/предплечье/3 пальца/тонкие перепонки (offset против z-fighting); legs: пояс/таз/набедренники/колени/поножи; boots: голени/пятка/раздвоенный носок.

Wing states интерполируются: folded 0%, takeoff 8t, flight beat синусом с асимметричной фазой, glide малой амплитуды, landing fold 10t; позиции копируются с parent humanoid. Кираса даёт mayfly (не отнимает creative/spectator flight). Полный сет: Fire Resistance I, Health Boost I(+2 сердца), Strength I, Regeneration I; эффекты ambient/no particles, обновляются короткими импульсами (снятие чистит только бонусы сета). V: клиент сообщает намерение, сервер проверяет сет/состояние/cooldown; во время charge — use pose, 3D-core у ладони, частицы сходятся внутрь; на tick 16 сервер создаёт projectile.

## 7. VFX
Язык движения: энергия сходится внутрь перед атакой; огонь/искры поднимаются после impact; криолёд — угловатые радиальные осколки; аура — строгое расширяющееся кольцо (random только поддерживает форму); ambient не перекрывает модель. Presets: AMBIENT_EMBER 1–2/с; SLIME_CHARGE 4→16/с; FIREBALL_TRAIL 3/tick near, 1/tick medium; CROWN_IMPACT до 48 частиц; AURA_WAVE ring+12 sparks; ICE_BREAK 30 shards. Distance LOD: >48 блоков 50%, >96 — 15%, >128 — none. Gameplay события серверные; клиент сам разворачивает preset, particles по сети не отправляются.

## 8. UI
Две vanilla boss bars с взаимоисключающей видимостью: sealed — «Криолёд: 150», awakened — «Огненный Демон: 400» (не занимает лишнюю вертикаль). Ice bar red, boss bar yellow; overlay notched_10 (печать) / progress (босс). Charge — компактный 64×5 bar над hotbar (anchor bottom-center, safe margin 12), только 16 тиков.

## 9. Звук
Отдельные события: seal crack, awaken roar, wing blast, melee impact, aura charge/release, trident throw/impact/return, slime charge/explosion, golem step/attack/hurt, armor equip, fireball charge/launch/impact, portal. Синхронизация с impact tick, не каждый tick.

## 10. Архитектура
Fabric 1.20.1, Mojang mappings, Java 17, GeckoLib 4.4.9. Main `com.opus.fire` (registry, gameplay, world builder, networking); client `com.opus.fire.client` (models/renderers/layers/particles/HUD). Нет зависимости fire от Haiku/EXO; только init и общий keybind V. Synced data: slime size/state/timer; demon sealed/ice/action/actionTick; projectile state. NBT: фаза, HP печати, cooldown, owner UUID, построенность центра. Все owner/gameplay проверки серверные. Geo QA: имена костей существуют, нет siblings с одинаковым pivot, разделены анимируемые части. Resource QA: JSON parse, PNG dimensions/alpha, references, blockstate variants, lang keys, particles.

## 11. Проверка
1 `./gradlew build`. 2 Dedicated server: мир, вход через fire portal, однократное построение центра. 3 `/locate biome opusvsexe:fire_biom`, осмотр 4 жил/Короны/острова/деревьев. 4 Три размера слизня + 50т до взрыва. 5 Печати ровно 150 урона; проверка смены bar/партикул и 6 действий босса. 6 Каждый слот брони: геометрия, flight, V. 7 Owner immunity, возврат trident, fireball impact, multiplayer sync. 8 GUI scale 2/3/4, нет missing textures/resource warnings.