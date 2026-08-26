# Задача 1 — Haiku Drone и Haiku Drone+ (летающая ветвь)

## Цель
1. Создать двух существ: **Haiku Drone** (мал.) и **Haiku Drone+** (крупн.).
2. **Перенести модель «тарелки»** с наземных Haiku на дронов: дроны рендерятся `haiku.geo.json` + `haiku_ai.png` + анимации; наземные Haiku (1.5,2,3,4,5,Omega) временно на `PlaceholderMobRenderer` до собственных моделей (см. MASTER_PLAN §5).
3. Haiku — наземные; дроны — единственная воздушная ветвь.

## Решения
| Параметр | Drone (X) | Drone+ (X+) |
|---|---|---|
| ID | `haiku_drone` | `haiku_drone_plus` |
| класс | `HaikuDroneEntity` | `HaikuDronePlusEntity` |
| hitbox | 0.6×0.5 | 1.0×0.9 |
| HP | 8 | 30 |
| скорость | 0.32 (летающий) | 0.28 |
| атака | таран 2 | таран 6 |
| client tracking | 10 | 12 |
| цвет яйца | 0xE8E2D4/0xFFB93E | 0xB8A88A/0xFF7A1A |
| категория | MONSTER | MONSTER |
| цели | игрок + железный голем | игрок + железный голем |

**Полёт:** классический плавающий моб (noGravity+FloatGoal), низко (как Vex/Allay), патруль+преследование по прямой. Финал: `FloatGoal`, `MeleeAttackGoal`, `LookAtPlayerGoal`, `RandomLookAroundGoal`; targets `HurtByTargetGoal`, `NearestAttackableTargetGoal(Player)`, `IronGolem`.
Масштаб: тарелка 1.875 блока → Drone 0.5 (scale 0.27), Drone+ 0.9 (scale 0.48); рендерер считает по `getBbHeight()`.

## Файлы
Новые: `src/main/java/com/opus/entity/haiku/HaikuDroneEntity.java`, `HaikuDronePlusEntity.java`.
Правки: `ModEntities` (+2 EntityType), `OpusVsExe` (атрибуты), `OpusVsExeClient` (рендереры: дроны→HaikuRenderer; наземные 1_5..omega→Placeholder), `ModItems` (2 яйца в креатив-таб), `lang/en_us+ru_ru` (4 строки).

## Дополнительно (лор)
- PlaceholderMobRenderer масштабирует по высоте — для Omega (25/1.8) снова огромный гуманоид (приемлемо до Задачи 7).
- Дронам доступны old анимации `idle`/`drift` (левитация уже в них).

## QA
1. `./gradlew build -x test` зелёный. 2. Яйца в креатив-табе. 3. Дрон ~0.27 / Дрон+ ~0.48 рядом. 4. Левитируют (idle), крен при движении (drift), НЕ касаются земли. 5. Наземные Haiku — снова placeholder.

## РЕЗУЛЬТАТ (2026-08-20)
[x] `HaikuDroneEntity` (0.6×0.5, HP8, урон2, скор0.32) и `HaikuDronePlusEntity` (1.0×0.9, HP30, урон6, броня4, скор0.28).
[x] ModEntities+2, атрибуты, рендереры (дроны→HaikuRenderer; haiku_1_5..omega→Placeholder). Яйца в табе. Lang 4 строки en/ru.
[x] BUILD SUCCESSFUL; в jar — классы и lang.
[ ] ОТЛОЖЕНО: лазерная атака Drone+ через LaserEntity (отдельная боевая задача).

---
## ДОРАБОТКА «НАСТОЯЩИЙ ПОЛЁТ» (2026-08-21)
Проблема: дроны левитировали (noGravity+FloatGoal), но не перемещались по воздуху (FloatGoal — только вода, WaterAvoidingRandomStroll — наземный). Висели на месте.
Решение: базовый класс `HaikuFlyingMob extends HaikuMob`: `moveControl=HaikuFlyMoveControl(this,0.9F)` (3D-контроль, как Vex/Allay; гасит скорость у цели, поворот по направлению); `FlyingPathNavigation`; `setNoGravity(true)`; анимации idle/drift + новая `attack` (рывок корпуса 0.35с, в haiku.animation.json), swinging через playOnce; цели MeleeAttackGoal/LookAtPlayerGoal/RandomStrollGoal/RandomLookAroundGoal, targets игрок+голем; FloatGoal УБРАН.
`HaikuDroneEntity`/`DronePlusEntity` → `extends HaikuFlyingMob`, атрибут `FLYING_SPEED` (0.6/0.55).
QA: BUILD SUCCESSFUL; летит по воздуху и таранит; attack проигрывается.