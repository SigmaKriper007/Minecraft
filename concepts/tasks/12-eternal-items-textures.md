# Эternal-предметы Колизея: текстуры AI Tear, Haiku Core, Opus Fragment

Задача №12 (2026-08-21). Пересоздание 16×16 item-текстур трёх предметов набора Eternal Colosseum. Палитры — из существующих текстур мода (raw_opus, core_opus, haiku_amber_block, generate_colosseum_textures.py), НЕ новые.

## Принцип (STYLE ANCHOR)
| Предмет | Ключ | Стиль |
|---|---|---|
| AI Tear | `ai_tear` | **янтарная** слеза-кристалл: сущность/«кровь» Haiku (энергия машин янтарная, MASTER_PLAN §1) |
| Haiku Core | `haiku_core` | **янтарное** кристаллическое ядро-орб с белым раскалённым центром (d→A→P→f→W) |
| Opus Fragment | `opus_fragment` | осколок металла Опуса: фиолетовый металл + голубая энерго-жила (палитра raw_opus/core_opus) |

## Почему так
- Старый `ai_tear` — голубая капля без смысла; слеза ИИ = застывшая капля янтарной энергии-сущности.
- Старый `haiku_core` — фиолетовый кристалл = палитра Опуса, противоречит лору; ядро Haiku янтарное (MASTER_PLAN §1).
- Старый `opus_fragment` — серо-голубой осколок, не совпадает с фиолетовой палитрой Опуса (raw_opus: #261642/#6b3fd1/#8a5cf0/#b58cff + жила #6fd9ff).

## Палитры
Янтарь (Haiku): H `#643C14` (контур), A `#B26A10`, G `#E8941E`, Y `#FFB93E`, W `#FFF5C8` (ядро).
Опус (металл): d `#261642` (контур), B `#6b3fd1`, C `#8a5cf0`, D `#b58cff`, E `#6fd9ff` (голубая жила), W `#ffffff` (блик).

## Файлы
- `textures/item/ai_tear.png`, `haiku_core.png`, `opus_fragment.png` — заменены (16×16).
- Модели `models/item/*.json` уже существуют (parent generated, layer0) — НЕ трогали.
- Скрипт: `generate_colosseum_items.py` (ASCII → palette → PNG).

## Статус
- [x] AI Tear — янтарная слеза (2026-08-21)
- [x] Haiku Core — янтарное ядро с белым центром (2026-08-21)
- [x] Opus Fragment — фиолетовый осколок с голубой жилой (2026-08-21)