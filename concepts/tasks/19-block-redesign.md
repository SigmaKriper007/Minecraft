# Задача 19 — Пересоздание блоков Eternal Colosseum: модели, текстуры, анимации, эффекты, свет

**Статус:** ✅ ВЫПОЛНЕНО (2026-08-23)

## 1. Требование
Пересоздать с нуля модели, текстуры, анимации, эффекты и свет для 10 блоков: Cracked Lab Concrete, Colosseum Concrete, Lab Floor Grate, Colosseum Wall, Amber Pillar, Opus Containment Glass, Reinforced Opus Block, Memory Cable, Haiku Amber Block, Omega Frame.

## 2. Дизайн-решения
### Полноразмерные (cube_all, новый дизайн текстур)
| Блок | Материал | Дизайн | Текстура | Свет | Анимация |
|---|---|---|---|---|---|
| cracked_lab_concrete | Треснувший лаб. бетон | Тёмный + трещины, арматура, копоть, хим. ожоги | 16×16 | — | — |
| colosseum_concrete | Древний аренный камень | Тёплый серо-бежевый + швы и патина | 16×16 | — | — |
| colosseum_wall | Каменная кладка | Крупные блоки, глубокие швы, выветренность | 16×16 | — | — |
| reinforced_opus_block | Усиленный металл Opus | Тёмный + рёбра жёсткости + заклёпки + синие энергошвы | 16×16 (был 18×16!) | 4 | — |

### Прозрачные (рендер-слой + alpha)
| Блок | Рендер | Дизайн | Свет | Анимация |
|---|---|---|---|---|
| lab_floor_grate | cutout (починить!) | Решётка с прозрачными ячейками, ржавчина, заклёпки | — | — |
| opus_containment_glass | translucent (починить!) | Рама + голубое стекло + циановые линии силового поля | 4 | .mcmeta ~4 кадра |

### 3D-геометрия
| Блок | Модель | Дизайн | Свет | Анимация |
|---|---|---|---|---|
| amber_pillar | cube_column | Фикс бага side==top: side — янтарные жилы, top — кольцо-капитель | 8 | .mcmeta ~4 кадра |
| memory_cable | 3D-кабель (новый) | Тонкий кабель-канал вдоль стены, трубки данных (циан/красный/маджента) | 5 | .mcmeta ~4 кадра |

### Энергетические (анимация + свет + частицы)
| Блок | Дизайн | Свет | Анимация | Частицы |
|---|---|---|---|---|
| haiku_amber_block | Янтарное ядро, пульс + свирлы, 16×64 (4 кадра) | 10 | .mcmeta, interpolate | Янтарные AMBIENT_ENTITY_EFFECT |
| omega_frame | Тёмная рама-каркас + маджента-жилы | 7 | .mcmeta ~4 кадра | — |

## 3. Технические изменения
- **Java:** `ModBlocks.java` — MEMORY_CABLE light 5, REINFORCED_OPUS_BLOCK light 4, HAIKU_AMBER_BLOCK → `AmberGlowBlock` (новый: animateTick, янтарные частицы, паттерн PulsingCoreBlock). `OpusVsExeClient.java` — LAB_FLOOR_GRATE cutout, OPUS_CONTAINMENT_GLASS translucent.
- **Ассеты:** `regen_block_textures.py` — все текстуры + .mcmeta; `models/block/memory_cable.json` — 3D. Остальные — cube_all/cube_column.

## 4. Верификация (все выполнено)
Все текстуры 16×16/16×64/16×96; нет AA/случайных цветов (≤7 цветов на текстуру); tileable; палитра мода (янтарь/циан/маджента/металл); .mcmeta корректны (5 новых); memory_cable 3D корректен (4 элемента); Java BUILD SUCCESSFUL; grate/glass рендер-слои подключены; янтарные частицы.

## 5. Итог (2026-08-23)
- 11 текстур пересозданы через `regen_block_textures.py` (ASCII→PNG), 5 анимированных (вертикальные кадры + .mcmeta interpolate).
- Исправлены баги: reinforced_opus_block 18×16→16×16; amber_pillar side/top разделены; grate/glass прозрачность работает; memory_cable из куба стал тонким 3D-кабелем.
- Свет: memory_cable +5, reinforced_opus_block +4. Haiku Amber Block — AmberGlowBlock: пульс (4 кадра) + янтарные частицы (r0.91 g0.58 b0.12) + END_ROD вспышки.