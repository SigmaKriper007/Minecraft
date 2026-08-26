# Задача 14 — Анимации EXO-1..5 (все, кроме EXO-6+)

Переработка с нуля всех анимаций общего экзоскелета `ExosuitEntity` (EXO-1 Sentinel … EXO-5 Vengeance), использующих `exo.geo.json` + `exo.animation.json` (рендер `ExoOmenModel`/`ExoOmenRenderer`, текстура `exo_omen.png`). EXO-6+ (Exo6Plus) НЕ трогается — у него свой `exo_plus.*`.

> Статус: выполнено (2026-08-21).

## 1. Костный каркас (из exo.geo.json)
```
the_omen (root)
├── left_hip_joint → left_thigh_joint → left_calf_joint → left_foot_joint
├── right_hip_joint → right_thigh_joint → right_calf_joint → right_foot_joint
└── hips
    ├── loincloth
    └── torso
        ├── head
        ├── collar_guard_* (4 шт.)
        ├── right_arm_joint → right_arm_joint2 → right_hand
        └── left_arm_joint → left_arm_joint2   (левой кисти НЕТ)
```
Особенность: `left_hand` отсутствует (анимируются только left_arm_joint/arm_joint2), `right_hand` есть.

## 2. Конвенции знаков (Blockbench/GeckoLib, как в exo_plus)
- фронт = −Z; +X поворот = наклон/взмах ВПЕРЁД (−Z).
- висящая рука: `+X` = вперёд, `−X` = назад/вверх над головой; `+Z` = к +X (к центру для левой, наружу для правой), `−Z` = к −X.
- «раздвинуть руки в стороны»: левая `Z −55`, правая `Z +55`.

## 3. Анимации (exo.animation.json)
| Имя | Тип | Описание |
|---|---|---|
| `idle` | loop | «дыхание» торса (8°→11°), микродрейф рук/головы |
| `walk` | loop | полный шаг: бедро ±28°, колено, стопа, руки в противофазе ±22°, таз ±4° |
| `attack` | play | удар правым кулаком (замах −55° → +45°, кисть `right_hand`) |
| `hurt` | play | отшатывание: торс/голова назад, руки дёргаются |
| `death` | play (hold) | падение вперёд: таз +45°, торс +70°, колени −75° |
| `block` | loop | БЛОК: руки подняты вперёд, предплечья перед собой (X +60/+70) |
| `chest_shot` | play | ВЫСТРЕЛ ИЗ ГРУДИ: руки в стороны (Z ∓55), грудь вперёд (торс +18°) |
| `slam` | play | удар по земле: обе руки вверх (−130°) → вниз (+40°), присед |

## 4. Логика проигрывания (ExosuitEntity.animationPredicate)
Приоритет: 1 `death` (isDeadOrDying) → 2 способность (`abilityAnimName(slot)`: chest_shot/slam) → 3 `attack` (attackTicks>0) → 4 `hurt` (hurtTime>0) → 5 `block` (isShieldActive: guard_stance/energy_shield/fortify) → 6 `walk`/`idle`.
Хелпер `playOnce(state, anim, repeat)` (как в HaikuMob) — forceAnimationReset при перезапуске; repeat=false для смерти.

## 5. Привязка способностей (override в EXO-1..5)
| Класс | Слот | Способность | Анимация |
|---|---|---|---|
| Exo1Sentinel | 0 | fist_slam (shockwave) | `slam` |
| Exo1Sentinel | 1 | guard_stance | (block через флаг щита) |
| Exo2Hunter | 1 | mining_laser (beam) | `chest_shot` |
| Exo3Vanguard | 2 | shockwave | `slam` |
| Exo4Titan | 0 | seismic_stomp | `slam` |
| Exo5Vengeance | 1 | volley (cone) | `chest_shot` |

`abilityAnimDuration`: slam 22т, chest_shot 16т, прочие 20.

## 6. Файлы (результат)
- `animations/entity/exo.animation.json` — переписан с нуля (8 анимаций).
- `entity/custom/ExosuitEntity.java` — HURT/DEATH/BLOCK + playOnce + приоритеты.
- `Exo1Sentinel.java`, `Exo2Hunter.java`, `Exo3Vanguard.java`, `Exo4Titan.java`, `Exo5Vengeance.java` — override abilityAnimName/Duration.