# CONCEPTS — Haiku / OpusVsExe

Единственный источник истины по дизайну машин Haiku: концепты, промты, планы, решения (продолжить после перерыва).

## Структура
| Файл | Назначение |
|---|---|
| `MASTER_PLAN.md` | Главный: ВСЕ модели Haiku, дизайн-концепты, промты, палитра, порядок задач |
| `tasks/01-haiku-drones.md` | Задача 1: Haiku Drone/Drone+ (летающая тарелка) |
| `tasks/02-haiku-husk.md` | Задача 2: Haiku 1.5 Husk (человекоподобный) — выполнена |
| `tasks/03-haiku-scout.md` | Задача 3: Haiku-2 Scout (колёсный WALL-E) — выполнена |
| `tasks/04-haiku-enforcer.md` | Задача 4: Haiku-3 Enforcer — выполнена |
| `tasks/05-haiku-warden.md` | Задача 5: Haiku-4 Elite Warden — выполнена |
| `tasks/06-haiku-titan.md` | Задача 6: Haiku-5 Titan Frame — выполнена |
| `tasks/07-haiku-omega.md` | Задача 7: Haiku-Ω Omega (древний) — выполнена |
| `tasks/08-haiku-warden-rework.md` | Задача 8: Warden REWORK (толстяк с мечом) — выполнена |
| `tasks/09-haiku-titan-rework.md` | Задача 9: Titan REWORK (щит-башня + молот) — выполнена |
| `tasks/10-haiku-omega-rework.md` | Задача 10: Omega REWORK (атрибуты всех тиров) — выполнена |
| `tasks/11-sky-laser-gun.md` | Задача 11: Sky Laser Gun — выполнена |
| `tasks/12-eternal-items-textures.md` | Задача 12: текстуры AI Tear / Haiku Core / Opus Fragment — выполнена |
| `tasks/13-exo-plus.md` | Задача 13: EXO+ — экспериментальный экзоскелет (4 способности); с 2026-08-26 это **EXO-5 Vengeance** (см. задачу 48) |
| `tasks/14-exo-animations.md` | Задача 14: анимации EXO-1..5 (idle/walk/attack/hurt/death + block + chest_shot + slam) |
| `tasks/48-exo-abilities-rework.md` | Задача 48: Energy Shield/Dash с нуля, Cutting Laser → Heavy Laser, EXO-6+ → EXO-5 Vengeance — выполнена |
| `tasks/17-blood-moon-interface.md` | Задача 17: редизайн интерфейса (все меню) под кровавую луну + неон + menu_music |
| `tasks/18-colosseum-rebuild.md` | Задача 18: Вечный Колизей ПЕРЕСБОРКА с нуля (ревизия 2: 128×128, стены-колоннады, чистая арена у алтаря, весь декор с краю) — выполнена |
| `tasks/19-structures-redesign.md` | Задача 19: ПОЛНАЯ пересборка ВСЕХ структур (кроме Колизея): 6 пересобраны как одна NBT-часть + «Заброшенный город»; тиры (мобы/лут), пазлы (sequence_keypad/energy_relay) — ВЫПОЛНЕНО |
| `tasks/19-block-redesign.md` | Задача 19: пересоздание моделей/текстур/анимаций/эффектов/света 10 блоков Колизея — выполнена |
| `tasks/46-survivor-anger-crash-fix.md` | Задача 46: фикс краша злости выживших — выполнена |
| `tasks/47-item-redesign-boss-audio.md` | Задача 47: редизайн предметов 4 разделов + Haiku Core 3D + музыка боссов + Cinder Slime — ВЫПОЛНЕНА |

## Правила работы
1. Перед задачей читай `MASTER_PLAN.md` + свой `tasks/NN-*.md`.
2. Задачи поочерёдно, одна за сессию; завершил — пометь `[x]` и статус в MASTER_PLAN.
3. Новые идеи по лору/дизайну — в соответствующий файл, не в код.
4. В концепте фиксируем НАМЕРЕНИЕ, в коде — реализацию (код меняется быстрее).

## Техническое знание: координаты GeckoLib (важно для генераторов)
Подтверждено по исходникам GeckoLib 4.4.9 (GeoBone, GeoRenderer, RenderUtils, BakedModelFactory):
- Кубы в geo-JSON — АБСОЛЮТНЫЕ координаты модели (не к пивоту). Пивот кости = только ЦЕНТР ВРАЩЕНИЯ. Трансформ кости: `L(p)=T(−pos)·T(pivot)·R·S·T(−pivot)`, мир = `L_root∘L_child∘…`. При отсутствии поворотов куб рендерится в своих координатах → оружие проектируем «в руках» (остриё на земле y≈0, хват в кисти).
- Знаки: pivot.x, origin.x и rotation.x/y при загрузке ИНВЕРТИРУЮТСЯ (x-зеркало мира GeckoLib); z — без изменений. Углы в градусах, порядок поворота Z·Y·X (X применяется к точке первым).
- Фронт модели: конвенция линейки — фронт в игре смотрит на игрока при 180−yaw, поэтому в geo-JSON фронт = −Z. Генераторы ведут дизайн во +Z, `finalize()`/`mirror_z` уводит в −Z и меняет south/north местами.
- Анимации: повороты x/y инвертируются (−x, −y, z); позиция x инвертируется. Ключи времени в секундах (GeckoLib ×20 в тики).
- QA-метрики: «DANGER-пара» = две кости-дети одного родителя с одинаковым пивотом; «мисс» = анимация ссылается на кость, которой нет в гео. Обе проверки в `haiku_model_kit.py` (метод `qa()`).