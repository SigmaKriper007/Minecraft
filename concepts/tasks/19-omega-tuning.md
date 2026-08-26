# Задача 19 — Тюнинг босса Haiku-Ω Omega (6 правок)

Статус: ✅ выполнено (2026-08-23)

## Правки
1. **Скорость ходьбы:** `HaikuOmegaEntity.java:163` MOVEMENT_SPEED 0.3 → 0.45.
2. **Музыка фазы 3:** `startBossMusic()` — перед `playNotifySound()` добавлен `ClientboundStopSoundPacket` (старый трек прерывается, затем стартует ускоренный pitch 1.25; без наложения).
3. **Радиус лазера = анимации:** `OmegaSkyLaserEntity.java:40` RADIUS 30.0F → 3.0F (визуальная колонна 4×4, радиус 2; метка r=2.49; 3.0 — запас вокруг геометрии).
4. **Слэш по направлению босса:** `OmegaSlashRenderer.preRender()` добавлен `poseStack.mulPose(Axis.YP.rotationDegrees(-yaw))` — GeckoLib не применяет yaw для plain Entity; теперь совпадает с arcDirection()/aimAt().
5. **Больше лазеров в ранних фазах:** `chooseAttack()` — ф1 ORBITAL доминирует (~70% дальние, ~40% ближние); ф2 ORBITAL на всех дистанциях (20–50%); ф3 без изменений.
6. **Кольцо крупнее:** `OmegaRingWaveEntity.java:42` VISUAL_RADIUS 6.225F → 15.0F (~2.4×; урон MAX_RADIUS=204 не меняется); `regen_omega_ability_models_v2.py` — сегменты кольца (H 8→300, W 56→60, T 7→8.5 блоков; фронт/ядро пропорционально), pulse scale 1.22/1.5 → 1.4/1.8, visible_bounds 1600×500, атлас 256→512. Перегенерированы omega_ring.*; QA: 0 миссов, 0 DANGER.

## QA
`./gradlew build` BUILD SUCCESSFUL; гео-модели 0 миссов, 0 DANGER.

## Дополнение (2026-08-23): реген, если ИГРОК ушёл от алтаря
- Механика лейша: реген теперь зависит от дистанции ИГРОКА до алтаря (раньше — от позиции босса).
- `PLAYER_LEASH_RADIUS = 42.0D`: игрок-цель дальше 42 блоков от якоря → босс регенит 12 HP/с и не запускает атаки (`isPlayerOutsideLeash()` в tick()); музыка не сбрасывается.
- Якорь явно привязан к алтарю: `setLeashAnchor()` из `AltarHeartBlock.summonHaikuOmega()`.
- `LEASH_RADIUS = 64.0D` остался как радиус арены для VFX/реквиема.
- Изменены: `HaikuOmegaEntity.java`, `AltarHeartBlock.java`.

## Дополнение (2026-08-23): смерть как у эндер-дракона + опыт
- `die()`: `super.die()` оставлен; дропы (CORE_OPUS, OMEGA_FRAME) без изменений. `tickDeath()` по образцу EnderDragon.tickDeath:
  - 200 тиков анимации (~10с); босс возносится (`setNoGravity(true)` + `move(SELF, 0, 0.1, 0)`);
  - взрывы EXPLOSION_EMITTER по корпусу на 180–200 тиках;
  - `xpReward=0` (чтобы die() не раздал 500 XP); 500 XP порциями: на 150–200 тике каждые 5т по 8% (~40 XP), на 200-м — финальные 20% (~100);
  - `remove(KILLED)` на 200-м тике.
  - Конструктор xpReward=0, поля deathAnimTime, метод tickDeath().
- **Фикс:** GeckoLib `death` в omega.animation.json растянут 2.2с → 10.0с (keyframe-тайминги ×4.545) — модель анимирует падение/гашение все 200 тиков.
- **Фикс:** звук взрывов смерти — новый `boss_explosion.ogg` (gen_boss_sounds.sh, boom+треск), ModSounds.BOSS_EXPLOSION, sounds.json + субтитры; в tickDeath с 180 по 200 каждые 4т BOSS_EXPLOSION (громк. 1.4, pitch 0.8–1.2), на 200-м — финальный (2.0, 0.6).

## Изменённые файлы
- `src/main/java/com/opus/entity/haiku/HaikuOmegaEntity.java` — скорость, музыка, выбор атак, смерть/опыт.
- `src/main/java/com/opus/entity/omega/OmegaSkyLaserEntity.java` — радиус.
- `src/main/java/com/opus/entity/omega/OmegaRingWaveEntity.java` — VISUAL_RADIUS.
- `src/client/java/com/opus/client/renderer/OmegaSlashRenderer.java` — поворот.
- `regen_omega_ability_models_v2.py` — генератор кольца.
- `geo/entity/omega_ring.geo.json`, `animations/.../omega_ring.animation.json`, `textures/.../omega_ring.png` — регенерированы.