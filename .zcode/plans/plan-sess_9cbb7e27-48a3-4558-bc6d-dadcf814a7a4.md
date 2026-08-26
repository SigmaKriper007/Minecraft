## План правок босса Haiku-Ω Omega (6 пунктов)

### 1. Скорость ходьбы Хайку Омега
Файл: `src/main/java/com/opus/entity/haiku/HaikuOmegaEntity.java:163`
- `Attributes.MOVEMENT_SPEED` 0.3 → **0.45** (заметно быстрее; ускорятся и подход к цели 0.6/0.8, и stroll-брождение).

### 2. Музыка не должна накладываться при ускорении на фазе 3
Файл: тот же, метод `startBossMusic()` (~строка 688)
- Сейчас при переходе в фазу 3 `musicRestartTick=0` → `startBossMusic()` с pitch 1.25 запускает НОВЫЙ экземпляр звука поверх старого (pitch 1.0), который продолжает играть.
- Фикс: перед `playNotifySound()` отправлять игроку `ClientboundStopSoundPacket(OpusVsExe.id("doom_eternal"), SoundSource.RECORDS)` — старый трек прерывается, затем стартует ускоренный. Оба класса уже импортированы в файле.

### 3. Область поражения лазеров = анимации
Файл: `src/main/java/com/opus/entity/omega/OmegaSkyLaserEntity.java:40`
- `RADIUS = 30.0F` → **3.0F**. Визуальная колонна сейчас 4×4 блока (радиус 2), метка-телеграф r=2.49 — урон 30 блоков был в разы больше картинки. 3.0 даёт небольшой запас вокруг видимой колонны. LAVA-частицы при ударе используют RADIUS — автоматически совпадут с колонной.

### 4. Слэш всегда в одну сторону → по направлению босса
Файл: `src/client/java/com/opus/client/renderer/OmegaSlashRenderer.java`
- `OmegaSlashEntity` — plain Entity; GeckoLib НЕ применяет к нему yaw-поворот (поэтому BlasterBeam/ExoLaser рендереры крутят модель вручную). У слэша поворота нет → дуга всегда смотрит в одну сторону.
- Фикс: в `preRender()` после `super.preRender()` добавить:
  `float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot()); poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));`
  (паттерн из `BlasterBeamRenderer`, импорты `com.mojang.math.Axis` + `net.minecraft.util.Mth`). Модель дуги авторится с центром на +Z, поворот `-yaw` совмещает её с направлением босса (`arcDirection` уже считается так же).

### 5. На первых фазах чаще лазеры
Файл: `HaikuOmegaEntity.java`, метод `chooseAttack()` (~строка 391)
- Фаза 1: ORBITAL (небесный лазер) сейчас 45% на дистанции>30 и 30% на 14–30, на ближней — 0%.
  Новые вероятности: >30 → ORBITAL 70%/VOLLEY 30%; 14–30 → ORBITAL 50%/VOLLEY 25%/SLAM 25%; ближняя → ORBITAL 40%/VOLLEY 40%/SLAM 20%.
- Фаза 2: ORBITAL добавить на все дистанции (>30 → 50%/VOLLEY 25%/TELEPORT 25%; 14–30 → ORBITAL 30%/SLASH 35%/SLAM 35%; ближняя → ORBITAL 20%).
- Фаза 3 не трогаем (там уже REQUIEM+ORBITAL).

### 6. Кольцо (шоквейв) — модель и анимация крупнее
- `src/main/java/com/opus/entity/omega/OmegaRingWaveEntity.java:42`: `VISUAL_RADIUS` 6.225F → **15.0F** (кольцо визуально ~2.4× шире; урон MAX_RADIUS=204 не меняется).
- `regen_omega_ability_models_v2.py`, функция `ring()`:
  - сегменты крупнее: корпус W 56→~60 блоков, H 8→~300 юнитов (видимая высота ~1.2 блока вместо 0.015), T 7→~8.5 блоков; горячий фронт и ядро пропорционально;
  - анимация `pulse` заметнее: scale 1.22/1.5 → 1.4/1.8 (волна сильнее бежит по кольцу);
  - `visible_bounds` кольца увеличить (высота 24 → ~420, ширина 900 → ~1600), чтобы высокие сегменты не отсекались.
- Перегенерировать: запустить `python3 regen_omega_ability_models_v2.py` (QA: 0 миссов, 0 DANGER) — обновятся `omega_ring.geo.json`, `omega_ring.animation.json`, текстура кольца.

### Проверка
- `./gradlew build` — сборка без ошибок.
- Обновить `concepts/tasks/` (создать `19-omega-tuning.md` с решениями и статусом, согласно agent.md).

Изменяемые файлы (7): HaikuOmegaEntity.java, OmegaSkyLaserEntity.java, OmegaSlashRenderer.java, OmegaRingWaveEntity.java, regen_omega_ability_models_v2.py, сгенерированные omega_ring.* + concepts/tasks/19-omega-tuning.md.