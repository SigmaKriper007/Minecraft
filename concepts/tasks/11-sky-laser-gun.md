# Sky Laser Gun — план реализации

**СТАТУС: ВЫПОЛНЕНО (2026-08-21).** `gradlew build` — BUILD SUCCESSFUL.

## Итоговая реализация
- **Предмет**: `sky_laser_gun` — «Sky Laser Gun»/«Небесная лазерная пушка» (`SkyLaserGunItem`, дубликат `LaserGunItem`: кулдаун 300, дальность 128, ПКМ → спавн луча).
- **Сущность**: `sky_laser` — `SkyLaserEntity`: жизнь 40т (2с), высота 80 блоков, диаметр 12, урон 15 каждые 10т; сразу по всей высоте; радиус расширяется (flare 0–20% жизни) и сужается (close 75–100%).
- **Модель**: `SkyLaserModel` — 3 слоя × 4 blade (45°): внешний (purple glow, шир.24), средний (cyan, 16), ядро (white-hot, 8). Анимация: удар с неба (ширина за 20%), пульс, сужение в последней четверти + вращение.
- **Рендер**: translucent, FULL_BRIGHT, альфа-затухание в последние 25%.
- **Текстуры**: `textures/entity/sky_laser_beam.png` (128×128, 3 полосы UV), `textures/item/sky_laser_gun.png` (32×32, вариант laser_gun со сдвигом в циан).
- **Регистрация**: ModItems, ModEntities, ModCreativeTab, OpusVsExeClient (модель-слой + рендерер).
- **Lang**: en_us / ru_ru / uk_ua.

### Попутный фикс
`HaikuFlyingMob.java` не компилировался (нет импортов `RawAnimation`/`AnimationState`/`PlayState` из GeckoLib) — импорты добавлены.

## Концепт
Дубликат Laser Gun с собственным визуалом: луч СВЕРХУ (с неба), 3 слоя свечения (внешний пульсирующий purple/blue, средний яркий cyan, ядро white). Палитра Opus: purple → cyan → white.

## Файлы (созданы)
1. `textures/entity/sky_laser_beam.png` — 128×128, 3 слоя.
2. `textures/item/sky_laser_gun.png` — 32×32.
3. `src/main/java/com/opus/entity/SkyLaserEntity.java`.
4. `src/main/java/com/opus/item/SkyLaserGunItem.java`.
5. `src/client/java/com/opus/client/model/SkyLaserModel.java` (12 blade, 3 слоя).
6. `src/client/java/com/opus/client/renderer/SkyLaserRenderer.java`.
7. `models/item/sky_laser_gun.json`. (geo НЕ нужен — EntityModel, не GeckoLib.)
8. Lang en/ru.
9. ModItems.SKY_LASER_GUN, ModEntities.SKY_LASER, ModCreativeTab, OpusVsExeClient.
10. `concepts/README.md` — запись.

## Модель луча
3 слоя × 4 blade: Outer (шир.24, semi-transparent purple), Main (16, bright cyan), Core (8, white-hot). Анимация: 0–10т удар с неба (scale Y 0→1, полная ширина); 10–60т удержание + пульс; 60–80т fade (alpha).

## Сущность
Спавн на целевой позиции (как LaserGun); длительность 40т; урон 15 (база) интервалом 10т; радиус 8, высота 80; мгновенный удар сверху вниз.