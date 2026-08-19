# Opus vs EXE - Structure Rework (Fabric 1.20.1)

Все 6 структур переработаны с нуля по промтам из `opus_vsexe_structure_prompts_ru.md`.
Это готовый datapack-слой мода: положи содержимое `data/` в
`src/main/resources/data/` своего мода (namespace уже `opusvsexe`).

## Что внутри

- 27 jigsaw-шаблонов `.nbt`, собранных поблочно (не скопированных)
- 6 `worldgen/structure` + 6 `structure_set` с уникальными salt и spacing
- 17 `template_pool`, 6 `processor_list` на выветривание
- 25 лут-таблиц по комнатам, а не одна общая
- биом-теги и структур-теги для `/locate`

## Структуры и их испытания

| Структура | Кусков | Испытание | Главный моб |
|---|---|---|---|
| kimi_laboratory | 5 | Каскадный сбой (коридор, волны спереди и сзади) | Husk |
| coddy_workshop | 5 | Последняя смена (осада, есть путь отступления) | Enforcer |
| data_spire | 2 | Перегрузка канала (спуск вниз на время) | Scout |
| trial_grounds | 3 | Протокол эскалации, 4 фазы | Elite Warden |
| frontier_fortress | 6 | Отключение гарнизона: 3 узла, потом реактор | Warden Commander |
| haiku_citadel | 6 | 3 предбоссовых испытания + арена Омеги | Haiku-Ω |

Каждая структура имеет свой ритм: лаба — спуск, шпиль — подъём и побег вниз,
полигон — удержание арены, крепость — штурм с отключением узлов, цитадель — марш.

## Важно: блоки и мобы, которые должны существовать в моде

Шаблоны ссылаются на кастомные блоки из промтов. Если блока нет в регистре,
Minecraft просто пропустит его при генерации (дыра в геометрии), поэтому сначала
зарегистрируй их (или замени на ванильные в `structures/*.nbt`).

### Декор (простой Block)
cracked_lab_concrete, lab_floor_grate, opus_containment_glass, scorched_concrete, oil_stain,
fortress_plating, memory_glass, omega_frame, citadel_vein, tank_trap, data_conduit, memory_cable,
signal_panel, pulsing_core, raw_opus_block, stabilized_opus_block, resonant_opus_block,
core_opus_block, broken_exo_hull, exo_assembly_frame, welding_bench, core_crate, memory_sludge

### С facing (HorizontalFacingBlock)
dead_terminal, flickering_terminal, blueprint_table, broken_resonance_forge, katana_stand,
scanner_eye, pulse_turret, wave_terminal, command_terminal, memory_console

### С boolean-свойством
arena_gate/phase_gate/sealed_bulkhead (`open`), shield_node/gravity_anchor/combat_beacon (`active`),
dormant_spawner (`active`), sealed_hatch, hazard_emitter, marsh_filter, reward_vault (`open`)

### Block entity NBT в шаблонах
- `opusvsexe:dormant_spawner` -> {entity_type, spawn_count, activation_range, delay, max_nearby, persistent}
- `opusvsexe:memory_console` -> {fragment: "opusvsexe:lab_1" ...} тексты фрагментов из промтов
- `opusvsexe:trial_trigger` -> {trial_id, reset_ticks}: lab_cascade, workshop_last_shift,
  spire_overload, trial_escalation, fortress_garrison, citadel_open_maw,
  citadel_no_looking_down, citadel_drop_anchors, citadel_omega

### Мобы в спавнерах
opusvsexe:haiku_husk, haiku_scout, haiku_enforcer, haiku_elite_warden, haiku_titan_frame, haiku_omega

### Предметы в луте
raw_opus, stabilized_opus, resonant_opus, core_opus, memory_fragment, katana_op,
blueprint_exo1..5, blueprint_forge, exo_servo, exo_plating, exo_frame_part, exo5_component,
exo_overcharge_cell, haiku_circuit, signal_scrap, warden_core, enforcer_core,
haiku_command_sigil, archive_key, lab_keycard, coddy_notes

Быстрая замена несуществующего на ваниль: правка loot_tables через sed/IDE, геометрия не трогается.

## Проверка в игре

/locate structure opusvsexe:kimi_laboratory
/locate structure opusvsexe:coddy_workshop
/locate structure opusvsexe:data_spire
/locate structure opusvsexe:trial_grounds
/locate structure opusvsexe:frontier_fortress
/locate structure opusvsexe:haiku_citadel

Поштучно посмотреть любой кусок: structure block -> LOAD -> opusvsexe:kimi_laboratory/research

## Заметки по геометрии

- Все стыки jigsaw — горизонтальные (надёжнее вертикальных).
- Шпиль Данных — один шаблон 21x48x21 (лимит ванили 48), а не стопка сегментов.
- Лаборатория ставится на Y от -14 до 18, люк может быть чуть под грунтом — так и задумано.
- Цитадель: spacing 140 / separation 90, одна на большой регион.
- Выветривание идёт дважды: внутри шаблона и через processor_list.
