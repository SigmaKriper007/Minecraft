# Задача 15.4 — Босс поворачивается к цели: направленные слэш/кулак/шоквейв

## Проблема
Босс Хайку-Ω при касте атак НЕ поворачивает корпус к цели (`getLookControl().setLookAt` крутит только голову, а `getLookAngle()`/рендер-модель считаются от `getYRot()` тела). Итог: кулак (MELEE) бьёт через `getLookAngle()` всегда в одном фиксированном направлении; слэш-дуга и удар-шоквейв выглядят и направлены мимо игрока.

## Шаг 1. Поворот босса к цели (HaikuOmegaEntity)
Добавить приватный хелпер:
```java
private void faceTarget(LivingEntity target) {
    double dx = target.getX() - this.getX();
    double dz = target.getZ() - this.getZ();
    float yaw = (float) (Math.atan2(-dx, dz) * 180.0D / Math.PI);
    this.setYRot(yaw);
    this.setYBodyRot(yaw);
    this.setYHeadRot(yaw);
}
```
(та же ванильная формула yaw, что и в `OmegaSlashEntity.aimAt` — вектор движения = (−sin yaw, 0, cos yaw)).

Вызвать `faceTarget(target)` в `executeAttack()` перед действием для направленных атак: `MELEE`, `SLASH`, `SLAM`, `TELEPORT` (телепорт тоже за спину цели — босс должен смотреть туда). `RING`/`VOLLEY`/`ORBITAL`/`REQUIEM` — радиальные/глобальные, поворот не обязателен, но для единообразия можно повернуть и их (не мешает).

## Шаг 2. Кулак (MELEE) — бить по направлению к цели
В `executeAttack` case MELEE заменить `Vec3 front = this.getLookAngle()...` на явный вектор от босса к цели:
```java
Vec3 front = target.position().subtract(this.position()).multiply(1, 0, 1).normalize();
```
— кулак гарантированно бьёт в сторону игрока независимо от состояния LookControl.

## Шаг 3. Слэш — согласовать с поворотом босса
`SLASH` и так ставит `aimAt(target.position())` и `centralDir` — направление урона корректно. После добавления `faceTarget(target)` анимация `attack_slash` босса (взмах рукой) и дуга-сущность будут смотреть в одну сторону — визуально согласовано. Дополнительных правок в OmegaSlashEntity не требуется (проверить, что `setYRot` в `aimAt` и `faceTarget` дают одинаковый yaw — обе формулы идентичны).

## Шаг 4. Шоквейв (SLAM)
`CombatEffects.shockwave` радиальный — после поворота босса удар в землю и волна визуально идут в сторону цели (позиция удара перед боссом). Точка удара — позиция босса (без изменений), теперь босс обращён к игроку.

## Шаг 5. Сборка
`GRADLE_USER_HOME=/home/shutniko/.gradle-local ./gradlew build` → фиксы компиляции.

## Критерии приёмки
1. Перед кулаком/слэшем/слэмом/телепортом босс разворачивает корпус к цели.
2. Кулак бьёт в сторону игрока (не фиксированное направление).
3. Дуга слэша и анимация взмаха направлены к цели.
4. `./gradlew build` зелёный.