# Задача 19 — ПОЛНАЯ промышленная пересборка СТРУКТУР мода (кроме Колизея)

Статус: ВЫПОЛНЕНО (2026-08-23)

Цель: переработать С НУЛЯ все структуры (кроме Колизея), честная прогрессия сложности (мобы+лут по тирам), настоящие испытания-головоломки (новые механики блоков), структуры больше/длиннее, новая «Заброшенный город». Всё генерируется кодом (Python), как Колизей.

## 1. Текущее состояние (проверено 2026-08-23)
- Worldgen — только datapack jigsaw (NBT + JSON). Java-структуры из ModStructures (resonance_mine/kimi_lab/kodi_forge/exo_hangar/haiku_citadel) НЕ вызываются (мёртвый код).
- 7 структур: kimi_laboratory, coddy_workshop, data_spire, trial_grounds, frontier_fortress, haiku_citadel, eternal_colosseum. Теги: tier1={kimi,coddy}, tier_end={haiku_citadel}, opus_structures (6, без колизея).
- 28 NBT-частей; многочастные стыкуются через `minecraft:jigsaw`.
- Механики блоков есть: trial_trigger (шаг/красстоун → reward_vault + сигнал 15 на 20т), reward_vault (заперт до триггера), dormant_spawner (entity_id из NBT, интервал 120т, до 4), Toggle-семейство (arena_gate/phase_gate/sealed_bulkhead/shield_node/gravity_anchor/combat_beacon), phased_barrier (сигнал ОТКРЫВАЕТ), force_field_projector/energy_barrier/energy_beam (декор), memory_console (USED+memory_text), resonance_forge (LIT, цепочка RAW→STAB→RES→CORE), pulsing_core, altar_heart (Колизей).
- Пробелы пазлов: нет «память-порядка», «маршрута энергии», «ключей».

## 2. Новая прогрессия (ТИРЫ)
Тир определяет мобов и лут. Лестница Опуса: raw→stabilized→resonant→core. Лестница Haiku: Husk→Scout→Enforcer→Warden→Titan→Omega.
| Тир | Структуры | Мобы | Лут | Фрагменты | Пазлы |
|---|---|---|---|---|---|
| T1 «железо» | kimi_laboratory, coddy_workshop | Husk, Scout | raw→stabilized | #1–5 | учебные: каскад памяти, зажигание горнов |
| T2 «алмаз» | data_spire, trial_grounds, abandoned_city | Scout, Enforcer (город — толпы) | stabilized→resonant | #6–10 | энерго-маршрут, арена волн, ложные хранилища |
| T3 «Опус» | frontier_fortress | Enforcer-гарнизон, Warden (мини-босс) | resonant→core | #9–12 | гарнизон: порядок щитов, осада |
| T4 «Ядро» | haiku_citadel | Warden, Titan, элиты, мини-боссы | core + катана-ОП | #13–15 | многоступенчатый храм (4 стадии) |
| True End | eternal_colosseum (готов) | Omega | — | — | — |

Лут-лестница: T1 raw 4–8 + stabilized 1–3 (фрагменты #1–5); T2 stabilized 3–8 + resonant 1–4 (карты/руны, #6–10); T3 resonant 6–14 + core 1–3 (netherite_scrap, трофеи, #9–12); T4 core 2–5 + resonant 4–8 + финал (katana_op/haiku_core, #13–15).

## 3. Новые механики блоков для пазлов (Java)
1. `opusvsexe:sequence_keypad` — «Клавиатура последовательности» (память-порядок, как Simon). BE nbt: `sequence` (порядок [0..9]), `progress`, `wrong_flag`. Клик: верный шаг → progress++, END_ROD; неверно → progress=0, лёгкий урон + тревога (труп-триггер), мигание; завершение → разблокировать reward_vault в r16 + сигнал 15 + solved. Визуал: amber-лицо с семью цифрами, solved → зелёный.
2. `opusvsexe:energy_relay` — «Энергоузел» (маршрут энергии). На соседнем обновлении ищем энерго-соседей (powered) и прокачиваем сигнал по цепочке; свойство `powr`. Применение: путь от источника к phase_gate/phased_barrier/shield_node («проведи линию»; кабели и реле). Визуал: amber-кольцо, powered → луч к следующему узлу.
3. (опц.) `archaic_key`/`lock_seal` — если нужен пазл «ключ-хранилище»; иначе reward_vault + sequence.

Правила пазлов: ошибка = лёгкий трап + сброс шагов (не блок прогресса), смертью карается только паркур; у каждого «ветка-подсказка» (memory_console рядом); волны через dormant_spawner / combat_beacon / energy_relay.

## 4. Общая техническая база — `structure_toolkit.py` (по образцу haiku_model_kit)
palette + set_ + r2 (как regen_colosseum_128.py); jigsaw-стык `jigsaw(x,y,z,name,target_pool)` (`minecraft:empty` для концов, orientation north/south/east/west_up); helpers интерьера (стены/пол/потолок, двери, окна, лестницы, пандусы, обломки, спавнер(entity_id), сундук(loot_table), keypad(sequence), relay, колонны). QA: nbtlib (bounds, палитра, jigsaw target_pool, сундуки/спавнеры, «плавающие»). Запись в pack/ И src/main/resources (двойная синхронизация).

Порядок генерации: генератор → NBT → template_pool JSON (если многочаст) → structure JSON → biome-тег has_<id> → лут-таблицы → lang → QA.

## 5. Проектирование каждой структуры (все станут БОЛЬШЕ)
### 5.1 kimi_laboratory (T1, underground)
Лор: рождение Haiku, убийство Кими. Вертикальная шахта (9×30×9) → коридор 3×60+ → левое крыло «Воспитание Haiku» (капсулы) → правое «Кабинет Кими» → дальний зал «Исследования» → «Сердце памяти». ~72×40×72.
Испытание «КАСКАД ПАМЯТИ»: 4 sequence_keypad, порядок из memory_console (#3→#4→#5→#6); решил → reward_vault memory_vault; ошибка — свет гаснет + волна Husk. Второе «ПРОТОКОЛ ЭВАКУАЦИИ»: коридор с phased_barrier через 2 energy_relay. Мобы: Husk-спавнеры, Scouts. Лут: blueprint/locker/office/research + memory_vault. Фрагменты #1–4. Части: entrance_shaft, corridor, nursery_wing, office_wing, research_hall, heart_vault.

### 5.2 coddy_workshop (T1, surface)
Лор: резонансное производство Опуса. Двор (40×22) → цех литья (36×28, горны) → сборочная линия (34×22) → полигон-тир (10×60) → казармы-руины. ~80×50.
Испытание «ПОСЛЕДНЯЯ СМЕНА»: зажечь 4 resonance_forge В ПОРЯДКЕ (цвет ember из памяти) → LIT по порядку → armory_vault (resonant, #5); ошибка — печь гаснет 20т + мобы. Второе «ТИРЫ»: 3 мишени-trial_trigger → трофеи. Мобы: Husk, Scouts. Лут: yard/parts/tools/coddy + armory_vault. Части: yard, foundry_hall, assembly_line, firing_range, barracks_ruin.

### 5.3 data_spire (T2, башня)
Часть Инфраструктуры памяти. 2–3 этажа + крыша; вертикаль 24×84×24 + аннекс. ~30×92×30.
Испытание «ПЕРЕГРУЗКА ШПИЛЯ»: на каждом этаже цепочка energy_relay к порту; наверху overload-узел (3 ряда реле) → core_vault (stabilized/resonant, #6–10). Второе: «белое окно» — деактивация phased_barrier рычагом-консолью. Мобы: Scout, Enforcer (охрана ядра). Лут: root/refinery/archive/core. Части: spire (многоэтажная, jigsaw по этажам), annex.

### 5.4 trial_grounds (T2, surface)
Арена тестов. Зона подготовки → арена 48×32×48 (трибуны) → лабиринт «хранилищ-обманок» → зал трофеев. ~90×20×70.
Испытание «ЭСКАЛАЦИЯ»: 3 волны (Husk→Scout→Enforcer) через dormant_spawner по combat_beacon; после каждой — сегмент energy_relay; все сегменты → reward_vault (resonant). Испытание «ОБМАН»: 4 reward_vault, один настоящий (по memory_console «ключ»). Мобы: Enforcer, волны. Лут: prep_armory/medbay/record_room/trophy/reward_vault. Части: prep_hub, arena, vault_maze, trophy_hall.

### 5.5 frontier_fortress (T3, surface)
Последняя оборона людей. Внешние стены с башнями (~60×60) → двор → keep (32×64×32) → 4 каземата (ammo/med/repair/power). ~72×70×72.
Испытание «ГАРНИЗОН»: energy_relay-контур по стене → разблокировать 4 shield_node (порядок по питанию ammo→power→med→repair); в power — Warden (мини-босс). Готово → keep_armory (resonant/core, #9–12) + осадные волны Enforcer. Мобы: Enforcer-гарнизоны, Warden. Лут: ammo/repair/med_scrap/command/reactor + keep_armory. Части: curtain_wall, keep, yard, casemate_*.

### 5.6 haiku_citadel (T4, финал)
«Живой храм-мозг» ИИ. Монументальные ворота → неф (10×70) → галереи → арена (48×36×48) → «Ядро» (24×30×24) → postboss. ~80×60×80.
Испытания (4 стадии): «ОТКРЫТАЯ ПАСТЬ» (5 sequence_keypad по столпам) → «НЕ СМОТРИ ВНИЗ» (проход по перекрытиям, phased_barrier по таймеру/реле; ошибка — «канава» с Husk) → «ЯКОРЯ ПАДЕНИЯ» (3 gravity_anchor реле-цепями под давлением Titan-стража) → «ЯДРО» (2 Warden + Titan; altar с core_vault: katana_op, haiku_core, #13–15). Мобы: Warden/Titan-элиты. Лут: approach/gallery/nave/core/postboss. Части: gate, nave, gallery_l/r, arena, core_sanctum, postboss.

### 5.7 (T2–3, НОВАЯ) — ЗАБРОШЕННЫЙ ГОРОД
Руины человеческого мегаполиса; Опус помнит его. ~128×128, слои (jigsaw-части ~56×56): plaza (площадь+ратуша), residential (многоэтажки-паркур), commerce (рынок), industrial (завод), office_block (небоскрёб 20×64×20, лифтовая спираль), outskirts (парковка/бензоколонка/бункер).
Испытание «РЕЛИКВИИ ГОРОДА»: 3 разрозненных sequence/relay-ключа в кварталах → ратуша с 5 reward_vault (казна города). Кварталы — мелочь (крыши/подвалы/сирены → волны). Мобы: толпы Husk/Scout, Enforcer (завод/офис), «Синдикат» (3 Enforcer) в бункере. Лут: locker/parts/tools/food/document/data/refinery/repair + сокровищница (T2–3, #8–12). Части: plaza, residential_a/b, commerce, industrial, office_block, outskirts. Bounding ~130×70×130. Биом: plains/forest/desert — тег `has_abandoned_city`.

## 6. Таблица: сейчас vs станет
| Структура | Сейчас | Станет |
|---|---|---|
| kimi_laboratory | 9×26×9 + туннели | ~72×40×72 |
| coddy_workshop | 5 частей до 30×16×26 | ~80×50 |
| data_spire | 21×48×21 | ~30×92×30 |
| trial_grounds | 36×32×36 | ~90×20×70 |
| frontier_fortress | 32×47×32 | ~72×70×72 |
| haiku_citadel | 6 частей (гаунтлет 42×48) | ~80×60×80 |
| abandoned_city (новый) | — | 7 частей, ~128×128, bbox ~130×70×130 |

## 7. План работы по шагам
0. structure_toolkit.py. 1. Java: sequence_keypad + energy_relay (BE, реестр, блостейты, модели, текстуры, lang, звуки), build. 2–8. Пересборка kimi (T1, шаблон) → coddy (T1) → data_spire (T2) → trial_grounds (T2) → abandoned_city (T2–3) → frontier (T3) → citadel (T4). 9. Глобальная связка: structure JSON, pools, biome-теги, структура-теги, лут-таблицы, синхронизация pack↔resources, README+structures.md+MASTER_PLAN, build, NBT-QA.

Критерии готовности: NBT валиден (0 out-of-bounds, jigsaw-стыки на существующие пулы); ≥1 испытание; мобы ≤ тиру/лору; лут по тиру; JSON обновлены + синхронизированы; build зелёный; концепты и README обновлены.

## РЕШЕНИЕ (2026-08-23): СТРУКТУРЫ = ОДНА БОЛЬШАЯ NBT-ЧАСТЬ
Проверено: многочастные jigsaw-сборки неконсистентны (напр., у kimi hall.nbt нет jigsaw-ответки) — рискованно. Поэтому КАЖДУЮ структуру — ОДНА большая NBT-часть (как Колизей): детерминированно, легко QA, jigsaw-сборка не нужна (только start-пул). «Части» плана → внутренние отсеки. Старые NBT-части и лишние пулы удаляются. Пазл-блоки реализованы (Java): sequence_keypad (nbt puzzle_id/order), energy_relay (красстоун-проводник; реле вертикально передают питание). «Маршрут энергии» = рычаг→редстоун→реле→phased_barrier.

## СТАТУС ПО ШАГАМ
- Шаг 0 (каркас) — готово: structure_toolkit.py.
- Шаг 1 (Java-блоки) — готово: sequence_keypad + energy_relay (build зелёный).
- Шаг 2 (kimi) — готово: regen_kimi_lab.py, 100×30×48, каскад памяти + энергодверь.

## ИТОГ ВЫПОЛНЕНИЯ (2026-08-23)
Каждая структура = ОДНА большая NBT-часть (формат kit), пишется в pack + src/main/resources. QA (nbtlib): 0 out-of-bounds, свойства блоков валидны, все лут/спавнеры существуют, 8/8 OK.
| Структура | Генератор | Габарит | Испытание | Лут | Мобы |
|---|---|---|---|---|---|
| kimi_laboratory (T1) | regen_kimi_lab.py | 100×30×48 | КАСКАД ПАМЯТИ (4 keypad #3→#6) + энергодверь (реле+редстоун) | memory_vault, heart_vault | Husk, Scout |
| coddy_workshop (T1) | regen_coddy_workshop.py | 96×22×56 | ПОСЛЕДНЯЯ СМЕНА (4 keypad) + тир-триггеры | armory, trophy | Husk, Scout |
| data_spire (T2) | regen_data_spire.py | 26×96×26 (5 этажей+шпиль) | ПЕРЕГРУЗКА ШПИЛЯ (4 keypad) | overload | Scout, Enforcer |
| trial_grounds (T2) | regen_trial_grounds.py | 96×24×64 | ЭСКАЛАЦИЯ (3 keypad, волны) + ложные хранилища | (без новых) | Husk/Scout/Enforcer |
| abandoned_city (T2–3, НОВАЯ) | regen_abandoned_city.py | 112×68×112 | РЕЛИКВИИ ГОРОДА (4 keypad у ратуши) | locker/market/document/refinery/bunker/treasury | улицы Husk/Scout, Enforcer, «Синдикат»×3 |
| frontier_fortress (T3) | regen_frontier.py | 72×48×72 | ГАРНИЗОН (4 keypad, порядок казематов) + Warden-мини-босс | keep_armory | Enforcer, Warden |
| haiku_citadel (T4) | regen_citadel.py | 80×56×80 | ПАСТЬ (5 keypad) → НЕВЗОРА (обрывы) → ЯКОРЯ (реле+gravity) → ЯДРО (Warden/Titan) | (без новых) | Warden, Titan |
| eternal_colosseum | (задача 18) | 129×72×129 | финал Omega | — | Omega |

Новые Java-блоки: sequence_keypad (порт.клавиатура: puzzle_id, order, сброс при ошибке, unlock reward_vault в r16), energy_relay (красстоун-проводник, вертикальная стойка). Новые теги: tier1/tier2/tier3/tier_end; abandoned_city в opus_structures (тег has_abandoned_city). Все генераторы в корне репозитория. `./gradlew build` BUILD SUCCESSFUL.

## 8. Открытые вопросы
- [ ] Подтвердить список структур (6+город) и тиры (T1/T2/T2–3/T3/T4).
- [ ] Устранить «мёртвый» Java-код структур (ModStructures/resonance_mine...) — вне scope или удалить.
- [ ] Новые блоки — достаточен ли минимальный набор (2 шт)?
- [ ] Город: bounding ~130×130, 7 частей, биом-тег — ок?

## История
- 2026-08-23: мастер-план пересборки всех структур + новая структура «Заброшенный город».