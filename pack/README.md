# Opus vs EXE — Datapack структур (Fabric 1.20.1)

Актуальный datapack-слой мода (`namespace: opusvsexe`). Каждая структура — **одна
цельная NBT-часть** (без хрупких jigsaw-сборок), генерируется Python-генераторами
(`regen_*.py` в корне репозитория, общий каркас `structure_toolkit.py`).

Содержимое папки `data/` продублировано в `src/main/resources/data/` мода.

## Структуры

| Структура | Тир | Габарит NBT | Биомы (тег) | Мобы | Испытание | Мотив |
|---|---|---|---|---|---|---|
| kimi_laboratory | T1 | 100×30×48 | `has_kimi_laboratory` (#is_overworld) | Husk, Scout | «Каскад памяти» (keypad×4) + энергодверь | подземная лаба-склеп |
| coddy_workshop | T1 | 96×22×56 | `has_coddy_workshop` | Husk, Scout | «Последняя смена» (keypad×4 у печей) + тир | кузня-полуразрушенный цех |
| data_spire | T2 | 26×96×26 | `has_data_spire` | Scout, Enforcer | «Перегрузка шпиля» (keypad×4) | вертикальная башня-сервер |
| trial_grounds | T2 | 96×24×64 | `has_trial_grounds` | Husk/Scout/Enforcer (волны) | «Эскалация» (keypad×3 + живые волны) + ложные хранилища | военный полигон |
| abandoned_city | T2–3 | 112×68×112 | `has_abandoned_city` | толпы Husk/Scout, Enforcer, «Синдикат»×3 | «Реликвии города» (keypad×4, подсказки по кварталам) | руины мегаполиса |
| frontier_fortress | T3 | 72×48×72 | `has_frontier_fortress` | Enforcer, Warden (мин.-босс) | «Гарнизон» (keypad×4, порядок казематов) | бастион с keep |
| haiku_citadel | T4 | 80×56×80 | `has_haiku_citadel` | Warden, Titan, элиты | Пасть→Неф→Якоря→Ядро | храм-мозг ИИ |
| eternal_colosseum | Финальный | 129×72×129 | `has_eternal_colosseum` | Haiku-Ω | призыв Ядра | Колизей с алтарём |
| paradise_island | Отдельная ветка | 97×72×97 | `has_paradise_island` | Sunfinch, Cloud Grazer, Paradise Wyvern, Angel Boy | небесная экология + трёхфазный суд | парящий сад и Парфенон |
| moon_fountain | Отдельная ветка | 49×22×49 | `has_moon_fountain` | Shade Spiderling, Gloom Broodmother, Moonwing Bat, Mossbound Enderman | пробуждение босса + ритуал 4 Rootbound Eye + Moonflower Heart | лунный фонтан Тёмного леса |
| survivor_settlement | Поселения | 91×31×91 | `has_survivor_settlement` | Survivor ×12 | торговля четырьмя маршрутными компасами, защита от Haiku при оружии Opus | укреплённая живая деревня |
| japanese_settlement | Поселения | 97×36×97 | `has_japanese_settlement` | Black Ninja ×8, Samurai ×4, Young Samurai ×1 | дымный рывок, длинный выпад, двухфазный бой с Кровавым цветком | замок с сакурой, рекой, мостом и тории |

`/locate structure opusvsexe:<id>` — id совпадает с именем в таблице.

## Блоки, используемые шаблонами

Все есть в реестре мода; отсутствующего блока не будет — генерация просто пропустит его.

### Декор (простой Block, без свойств)
`cracked_lab_concrete`, `lab_floor_grate`, `opus_containment_glass`, `memory_glass`,
`scorched_concrete`, `oil_stain`, `fortress_plating`, `omega_frame`, `citadel_vein`,
`tank_trap`, `memory_sludge`, `marsh_filter`, `colosseum_concrete`, `colosseum_wall`,
`reinforced_opus_block`, `haiku_amber_block` (бедрок-прочность, свет 10), `pulsing_core`,
`core_crate`, `broken_exo_hull`, `exo_assembly_frame`, `raw/stabilized/resonant/core_opus_block`,
`opus_ore`; ваниль: `deepslate_tiles/bricks`, `polished_blackstone`, `obsidian`, `lava`, `end_rod`.

### С facing / питанием (OpusHorizontal / PoweredHorizontal)
`data_conduit`, `memory_cable`, `signal_panel`, `welding_bench`, `hazard_emitter`,
`dead_terminal`, `flickering_terminal`, `blueprint_table`, `broken_resonance_forge`,
`katana_stand`, `scanner_eye`, `pulse_turret`, `memory_console` (facing+used);
`wave_terminal`, `command_terminal`, `force_field_projector` (facing+powered).

### Переключатели (ToggleBlock)
`arena_gate`, `sealed_bulkhead`, `sealed_hatch` (`facing`+`open`);
`phase_gate`, `shield_node`, `gravity_anchor`, `combat_beacon` (`facing`+`active`).
Взаимодействие — клик (тумблер), у `active` — свет при включении.

### Энергобарьеры
`energy_barrier` / `energy_barrier_red` / `energy_barrier_blue` (`facing`, панели, неразрушимы),
`energy_beam` (`axis`, луч), `phased_barrier` (`facing`+`active`, красстоун-сигнал ОТКРЫВАЕТ).

### Block entity + испытания
- `trial_trigger` (`triggered`): шаг/красстоун → разблокирует `reward_vault` в радиусе 16,
  сигнал 15 на 20т. NBT: `cooldown`.
- `reward_vault` (`open`): запертым выводит сообщение; после unlock — контейнер 27 слотов,
  лут из `LootTable`. NBT: `LootTable`.
- `dormant_spawner` (`active`): спавнит моба из NBT `entity_id` (интервал 120т, до 4 рядом).
  Поведение включается `last_active=true` (для живых волн).
- `resonance_forge` (`facing`+`lit`): крафт Опус-заготовок RAW→STAB→RES→CORE (огниво/зажигалка).
- `memory_console` (`facing`+`used`): показывает текст NBT `memory_text` (подсказки пазлов).
- `moon_fountain_core`: хранит победу, UUID босса и 100-тиковый ритуал; `rootbound_pedestal` (`charged`) принимает/возвращает один Rootbound Eye.
- `altar_heart` (`activated`): призыв Haiku-Ω (по Ядру Haiku).

### Пазл-блоки (задача 19)
- `sequence_keypad` (`facing`+`solved`): «память-порядок». NBT: `puzzle_id` (группа),
  `order` (позиция в последовательности). Клик: правильный шаг → solved; ошибка → сброс всей
  группы (lightness) + урон; при решении последней — разблокирует `reward_vault` в r16.
- `energy_relay` (`powered`): красстоун-проводник. Проведение питания от источника/рычага
  по цепочке реле к `phased_barrier`/`trial_trigger` («маршрут энергии»). Передаёт сигнал 15.

## Мобы в спавнерах (`dormant_spawner.entity_id`)
`opusvsexe:haiku_1_5`, `haiku_2` (Scout), `haiku_3` (Enforcer), `haiku_4` (Warden),
`haiku_5` (Titan), `haiku_omega`; (дроны и EXO — для других сцен).

## Лут-таблицы
`chests/<structure>/*.json`. Пул внутри структуры задаётся через
`LootTable` у `reward_vault`/`chest`. Лестница по тирам:
T1 → raw/stabilized_opus; T2 → stabilized/resonant_opus; T3 → resonant/core_opus;
T4 → core_opus + катана-ОП/ядро Haiku. Памятные фрагменты по номерам лора (1–15).

## Порядок проверки в игре
```
/locate structure opusvsexe:abandoned_city
/locate structure opusvsexe:kimi_laboratory
/locate structure opusvsexe:coddy_workshop
/locate structure opusvsexe:data_spire
/locate structure opusvsexe:trial_grounds
/locate structure opusvsexe:frontier_fortress
/locate structure opusvsexe:haiku_citadel
/locate structure opusvsexe:eternal_colosseum
/locate structure opusvsexe:moon_fountain
/locate structure opusvsexe:survivor_settlement
/locate structure opusvsexe:japanese_settlement
```
Пазл-блоки (sequence_keypad/energy_relay) удобно тестить structure-stick'ом на предметной машине.
