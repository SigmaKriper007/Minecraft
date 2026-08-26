# PLAN — «Пепельный Серафим» (Ashen Seraph)

## Границы
- Автономный дубликат Fire Biom (предметы + мобы + блоки) с ПОЛНОСТЬЮ новым визуальным концептом, не производным от Fire Biom.
- Java в `com.opus.ember.*`, ресурсы в `ember`-каталогах.
- Функционал копируется; текстуры/модели/анимации — только свои.
- Измерение/worldgen не копируются (портал — декоративная мембрана).

## Имена
- Мобы: `ember_slime`, `obsidian_golem`, `flame_demon`.
- Предметы: `ember_essence`, `blazing_trident`, `ember_helmet/chestplate/leggings/boots`.
- Блоки: `cinder_soil`, `cinder_crust`, `cinder_ash`, `cinder_log`, `cinder_leaves`, `cinder_vine`, `cinder_bean`, `cinder_seal`, `cinder_portal`.
- Снаряды: `ember_fireball`, `blazing_trident`, `ember_aura_wave`.
- Вкладка: `ember_tab`. Частицы: `ember_spark`, `ember_ash`.

## Этапы
1. [x] Новый концепт «Пепельный Серафим» (DESIGN.md): bone/ash/ember палитра, шлем-«Лик Падшего» (маска+вуаль+сломанный нимб+рога), кираса-«Сердце Углей» (рёбра+сердце+две пары крыльев).
2. [x] Main Java: EmberLine, registry (blocks/items/entities/particles/tab), item, entity, projectile, network, sound + блоки (CinderBeanBlock/CinderVineBlock).
3. [x] Client Java: модели/рендереры 6 сущностей; броня с нуля (EmberHelmetModel, EmberPlateModel) + слои (вуаль/нимб/рога, две пары крыльев в контрапункте).
4. [x] Генераторы: `gen_ember_geo.py`, `gen_ember_textures.py` (блоки+броня+сущности), `gen_ember_json.py` (блоки+предметы+particles+atlas).
5. [x] Локализация en/ru/uk, sounds.json, атлас.
7. [x] Редизайн крыльев кирасы: 3 «пера» → плоский мембранный скелет (humerus/claw/forearm/tip_spike + 4 двухзвенных пальца + 3 рваные мембраны + arm_web), превью `preview_ember_wings.py`, анимации folded/beat/glide/landing.
8. [x] Переработка крыльев (исправлена деформация): убран yaw (причина разрыва костей), фикс рендер-матрицы превью `T(pivot)·R`, ОДНА пара крыльев с симметричным махом вместо двух пар в контрапункте.

## Приёмка
- Три моба с новыми именами/моделями/анимациями (не копии Fire).
- Шлем: костяная маска + вуаль + сломанный нимб + изогнутые рога.
- Нагрудник: рёбра + тлеющее сердце + ОДНА пара крыльев с полётной анимацией (folded/beat/glide/landing).
- 9 блоков с новыми текстурами, моделями и blockstates.
- Полный сет: Fire Resistance I, Health Boost I, Strength I, Regeneration I; кираса — mayfly; V — серверно проверенный фаербол.
- `gradlew build` успешен; нет missing textures / resource warnings.