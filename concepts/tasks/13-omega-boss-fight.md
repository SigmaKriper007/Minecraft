# Задача 13 — Haiku-Ω Omega: полный бой финального босса + новый Вечный Колизей

Статус: ✅ боевой функционал реализован (2026-08-21)

## Референсы
MASTER_PLAN §5.7/§6/§7, tasks/07 и tasks/10 (модель/риг Омеги), lore.md. Решения пользователя (2026-08-21): полный гейт урона Opus; лейш-зона вместо барьеров; миньоны свободно спавнятся (интенсивность по фазам); Колизей с нуля с максимальной детализацией.

## 0. Инструментарий (`haiku_model_kit.py`, воссоздан)
Скрипты исчезли из репо — каркас восстановлен по МЕТОДИЧКА §6.1: абсолютные координаты кубов, pivots=центры вращения, `mirror_x()`, `finalize()` (+Z→−Z, смена пар north/south, east/west), атлас FFD с контролем переполнения, QA (миссы=0, DANGER=0), FK pose-композиция, NBT-писатель структур (gzip, big-endian, vanilla-схема).

## 1. Новый Вечный Колизей — `generate_colosseum.py`
Старый center_altar.nbt заменён генерацией с нуля. 128×208×128, каркас y=8..15, пол y=16 (мировая поверхность через worldgen absolute −48 + beard_box):
- **Пол 3 кольца**: центр r≤16 (colosseum_concrete + янтарные кольца r=5/10/14 + янтарный крест + полированный центр), промежуток r≤32 (cracked_lab_concrete + выжженные пятна + lab_grate решётки + 8 кабельных магистралей), внешнее r≤54 (colosseum_wall + титановые плитки + 6 лавовых чаш);
- **Ограда** r56..62 до y=63: обсидиановая капитель + зубцы, 4 триумфальные арки (sealed_bulkhead + янтарный импост);
- **16 бронзовых колонн** r≈63 (шаг 22.5°), **8 янтарных пилон-факелов** r=30 (amber_pillar + end_rod), **4 столба призыва** у алтаря r=8, **8 пульсирующих ядер** r=24;
- **8 трибун** r=48: чётные — «трон EXO» (sealed_bulkhead + dormant_spawner + core_crate + broken_exo_hull), нечётные — спавнер + обломки; **5 сундуков** (containment/lab/server/vault/lodge);
- **4 «головы» проигравших машин** на диагоналях r=52 (обсидиан + янтарные глаза); «архив памяти» (containment_glass + терминалы + welding_bench); подвесной «мозг» из кабелей/signal_panel y=80;
- **Алтарь**: пьедестал 3×3 (y17..19) + `altar_heart` y=20.

## 2. Механики боя — `HaikuOmegaEntity` (полный реврайт)
- **Опус-гейт (полный):** урон ТОЛЬКО от тега `#opusvsexe:opus_weapon` (katana_op/gold/refined, opus_warhammer, light/heavy/sky_laser_gun) и от EXO-мобов/снарядов. Остальное: 0 урона + `boss_deflect` + CRIT-искры. TagKey `ModTags.OPUS_WEAPON`.
- **Фазы по HP:** 100–50%/50–25%/25–0%. Переходы: `phase_open`/`enrage_roar`, взрыв `boss_phase_shift`, EXPLOSION_EMITTER, пакет OMEGA_FX, сообщения. Фаза 2: окно уязвимого ядра 5с урон ×2 (BOSS_CORE_HIT). Фаза 3: музыка DOOM_ETERNAL pitch 1.25.
- **Лейш:** якорь = спавн/алтарь, R=64; вне зоны не бросается, реген 12 HP/s, возврат к алтарю.
- **Планировщик атак** (паузы по фазам 70/45/30т; выбор по фазе/дистанции):
  | Атака | Фаза | Сущность/эффект | Урон |
  |---|---|---|---|
  | MELEE кулак | 1-3 | конус (reach 10), KB | 25 |
  | VOLLEY турель | 1-3 | OmegaShrapnel ×3/9/14, скор 1.6, сплэш r=7 | 6/шар |
  | ORBITAL гнев | 1,3 | OmegaSkyLaser ×5/8, зона 28×24 | 14/10т |
  | SLASH взмах | 2-3 | OmegaSlash сектор 110°, r=11.5, h=5 | 16 |
  | RING кольцо | 2-3 | OmegaRingWave растёт до 34 | 8 + KB+подброс |
  | SLAM гнев-шаг | 1-3 | shockwave r=14/22 | 12 |
  | TELEPORT | 2 | за спину + хук | 20 |
  | REQUIEM (ф3) | 3 | OmegaSkyLaser ×14 веером | 14/10т |
- **Миньоны:** ф1 — каждые 20с по 1 (Drone/Scout, кап 2); ф2 — каждые 7.5с по 3 (Husk/Drone 50/50, кап 7); ф3 — каждые 8с по 2 (Enforcer/Drone+ 50/50, кап 6). Тег `omega_minion`, цель игрок, снимаются на смерть босса.
- **Боссбар** (жёлтый), имя по фазам; NBT: фаза, якорь, время открытого ядра.

## 3-4. Новые сущности атак (пакет com.opus.entity.omega)
| Сущность | id | Модель | Поведение |
|---|---|---|---|
| OmegaShrapnelEntity | omega_shrapnel | шар+хвост, anim flight/impact | снаряд 1.3 б/т, взрыв-сплэш r=2.5, 160т |
| OmegaSkyLaserEntity | omega_sky_laser | звезда-телеграф 4с → колонна 2с | PREVIEW 80т + STRIKE 40т, урон 14/10т r=2.5 |
| OmegaSlashEntity | omega_slash | дуга 4 сегмента spawn/hold/out | телеграф 5т, сектор ±55°, r=5.5 |
| OmegaRingWaveEntity | omega_ring_wave | 16-сегментное кольцо expand | растёт 3с до r=20, одноразовый hit |

Все: GeoAnimatable, owner-skip, fullBright, регистрация в ModEntities + OpusVsExeClient; geo через kit (атласы 64×64, QA 0 миссов/0 DANGER).

## 5. Анимации босса — `generate_omega_battle_animations.py`
omega.animation.json: 15 анимаций (idle/walk/hurt/death сохранены; attack/special заменены на attack_melee/volley/orbital/slash/ring/slam/teleport/requiem + phase_open/enrage_roar/summon_call). Все playOnce.

## 6. Звуки — `gen_boss_sounds.sh` (ffmpeg, 12 ogg)
boss_roar, boss_step, boss_punch, boss_turret_shot, boss_orbital_warn, boss_laser, boss_ring_burst, boss_teleport, boss_core_hit, boss_deflect, boss_phase_shift, boss_slam. В sounds.json (+субтитры), ModSounds, lang en/ru.

## 7. Сеть/VFX — канал OMEGA_FX (S→C)
`ModNetwork.sendOmegaFx(...)` → `OmegaClientNetwork` → `OmegaCameraShake` (тряска до ~2.6°, миксин GameRendererMixin#render HEAD). Типы: SHAKE_MINOR/MAJOR, PHASE_OPEN, PHASE_ENRAGE, REQUIEM.

## ТЮНИНГ 2026-08-22 (задача 13.1)
Атаки пропорциональны корпусу (25 блоков), миньоны интенсивнее, анимации = окна исполнения.
- **Масштаб моделей:** шарики турели ×3 (hitbox 2.1³, сплэш r=7), дуга слэша 2.3× (r=11.5, h=5), колонна орбиталки 2× (r=3, ~90 блоков), кольцо до r=34 (рендер modelRadius 19.5), expand ×2 скорости.
- **Зоны:** слэм 10/16→14/22; веер турели точнее (0.06→0.035), скор 1.3→1.6; орбитал полоса ~28×24 вместо 16×10.
- **Миньоны:** ф1 каждые 20с по 1 (кап 2), ф2 каждые 7.5с по 3 (кап 7), ф3 каждые 8с по 2 (кап 6).
- **Окна анимаций = окна исполнения:** MELEE 22т, VOLLEY 22т, ORBITAL 36т, SLASH 20т, RING 36т, SLAM 26т, TELEPORT 16т, REQUIEM 44т, summon 26т. Перед атакой босс поворачивает корпус/взгляд (LookControl 25/30).
- **Фикс слэша:** направление по ванильной формуле yaw (dir=(-sin yaw,0,cos yaw)) + закрепление вектором на цель — дуга всегда «под рукой».
- visible_bounds VFX-гео расширены.

## ТЮНИНГ 2026-08-22 (задача 13.2)
- **×16-конвенция:** подтверждена байткодом GeckoLib (BakedModelFactory делит на 16) — все гео-атаки в юнитах ×16 от блочных, рендеры scale(1.0F).
- **Кольцо:** r ×6 → 204 блока (гео r=3264, 16 сегментов 896×8×110). Раскатывается МГНОВЕННО (тик 1 бьёт по всему радиусу: 8 урона, KB 2.4/подброс 0.9), живёт 62т (END_ROD искры). Звук omega_ring_wave.
- **Небесный лазер:** высота 80 (pillar 960×1280×960 = 60×80×60), ширина ×5 (звезда S=360). Телеграф 80т (sky_laser_warn, tick 1), удар — sky_laser_omega + 40 LAVA, колонна 14/10т, радиус 30.
- **Слэш:** ×12 — дуга r=138 (4 сегмента r=2208, 896×224×160), высота 6, сектор 110°. Урон + звук omega_slash + 60 FLAME.
- **Звуки (6 новых ogg):** haiku_omega_death (в die(), громкость 1.6), omega_ring_wave, omega_slash, sky_laser_warn, sky_laser_omega, altar_heart_loop (120с стрим). В sounds.json + lang.
- **Бедрок-прочность:** haiku_amber_block и altar_heart — `strength(-1.0f, 3600000.0f)`.
- **Кровавая луна:** серверный OmegaMoonTracker (END_WORLD_TICK, broadcast FX_MOON_START/END, пульс каждые 40т пока босс жив, JOIN-догон); клиент CrimsonMoonClient (плавный 3с/6с, автогашение 5с, ретинт неба CrimsonSkyMixin + тумана CrimsonFogMixin — только цвет, не густота; 90 красных комет через WorldRenderEvents.BEFORE_DEBUG_RENDER).
- **Эмбиент алтаря:** AltarHeartBlockEntity-«маяк» + клиентский AltarHeartAmbience (сканирует чанки, луп altar_heart_loop в r=64, Attenuation.NONE).

## Открытые вопросы / долг
- [ ] Музыка фазы 3: DOOM_ETERNAL pitch 1.25; отдельный трек можно позже (setInterval/stop+start).
- [ ] Лейш-якорь: сейчас позиция при появлении (алтарь знает y+4). Если жёстко к блоку — передавать BlockPos из AltarHeartBlock (setter).
- [ ] Баланс: урон/паузы в константах HaikuOmegaEntity — тончать после прогона.
- [ ] Уведомление Ядра: hitbox ядра = урон-окно ×2 (пространственное), не отдельный hitbox 2×2×2; при нужде — OmegaCoreEntity.

## QA / приёмка
- NBT Колизеев: 0 дефектов, 0 out-of-bounds, сундуки 5, алтарь [64,20,64].
- Anim QA: 0 ошибок для geo omega (28 костей).
- `./gradlew build` — см. итог в конце файла.

## История
- 2026-08-21: генераторы, сущности, боёвка, звук, сеть, анимации, lang.