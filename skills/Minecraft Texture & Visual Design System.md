---
name: minecraft-textures-and-visuals
description: Universal skill for creating, editing and implementing Minecraft pixel-art textures, entity skins, layered textures, item textures, armor, blocks, weapons, models and visual assets. Maintains a consistent visual language across the entire mod.

# Minecraft Texture & Visual Design System

## 0. Назначение
Единые правила работы со ВСЕМИ визуальными ресурсами мода: предметы, оружие, инструменты, броня, шлемы, мобы, entity textures/models, двухслойные/многослойные текстуры, блоки, руда, декор, сундуки, верстаки, механизмы, GUI, эффекты, частицы, UI-текстуры, icons, 2D/3D item models, UV-развёртки, PNG, pixel-art, ASCII/palette-based procedural textures.
**Главная задача: весь мод — единая визуальная вселенная.** Не создавай предмет независимо от остальных; новые текстуры соответствуют существующему художественному языку.

## 1. Главный принцип
Роль: pixel artist + Minecraft resource artist + technical artist + mod developer. Перед новым ресурсом: изучи проект → найди похожие ресурсы → определи размер/палитру/UV-layout/стиль shading/толщину контуров → существующие ModelPart/Renderer → только затем создавай. Существующий похожий объект = визуальный эталон. Не создавай стиль без нужды.

## 2. Визуальная целостность
Все ресурсы — один художник: общий уровень детализации, pixel density, контраст, освещение, контуры, цветовая логика, блики/тени, материал, визуальный язык. Great Helm-эталон → вся броня «от того же кузнеца».

## 3. Pixel art
Основной стиль: Minecraft pixel art / low-res texture. НЕЛЬЗЯ: anti-aliasing, blur, мягкие градиенты, фотореализм, subpixel details, случайный шум, высокочастотную детализацию. Каждый пиксель = решение; 1px = 1 визуальное решение, не компенсация плохого силуэта цветами.

## 4. Размер текстуры
сначала определи размер: 16/32/64/128/256. Маленький предмет 16×16; сложный — 16 или 32; humanoid skin 64×64; сложные entity 64–128. Не повышай разрешение без нужды.

## 5. Прозрачность
transparent = отсутствие пикселя; не заменяй alpha на RGB(0,0,0); без фона вокруг предмета; всё вне предмета = alpha 0.

## 6. Контур
Для малых предметов: ~1 px, темнее поверхности, не обязательно чёрный, по материалу. Помогает читаемости силуэта; не обводи бессмысленно.

## 7. Освещение
Единое направление света сверху-слева (свет ↘ объект ↘ тень). Светлые P/f, средние A/B/G, тёмные d/S/e. Не менять направление между текстурами.

## 8. Материалы
Своя система оттенков: Металл (dark/mid/light metal/highlight), Камень (dark/stone/light/noise), Кожа (dark/leather/light/highlight), Кристалл (dark/main/bright/highlight energy/white core). Одна схема на всё недопустима.

## 9. Палитра
Есть палитра → никогда не добавляй цвета без нужды. Процедурный art: character→RGB (d=dark purple, A=purple, P=light purple, f=pale purple, c=dark cyan, C=cyan, W=white, S=dark metal, e=dark gray, H=dark brown, M=brown, D=bright metal и т.д.). Уже есть gen_textures.py → изучи его палитру; не создавай вторую.

## 10. Procedural ASCII textures
Строка = ряд пикселей, `.` = прозрачность. Требования: фиксированный размер, строки одной длины, только допустимые символы, симметрия где нужно, контролируемый силуэт. Проверка: height==required, width каждого ряда==required.

## 11. Не подменять pixel art AI
НЕЛЬЗЯ AI-генерация/blurry/anti-aliased PNG/photorealistic. Правильный pipeline: Design→ASCII/pixel matrix→palette→Python→PNG→Minecraft. AI-референс → manual pixel reconstruction → палитра → финал.

## 12. Предметы
Силуэт → направление → material → dark outline → цвета → highlights → читаемость на 100%. Оружие: рукоять/гарда/лезвие/наконечник/направление. Не переворачивай геометрию; неправильно в руке → чини transform/rotation, не текстуру.

## 13. Item models
Найди item JSON/parent/texture/display transforms/custom renderer/3D. Обычный предмет — стандартный vanilla model; сложное — custom 3D + renderer, но без необходимости не плоди renderer.

## 14. Положение в руке
Проверять rotation/translation/scale/hand side/first+third person. Логика hand→handle→guard→blade→tip; рукоять в руке. Не переворачивай текстуру при проблеме transform.

## 15. Двухслойные текстуры
INNER LAYER + OUTER LAYER. Outer: отдельная геометрия, чуть больше Inner, той же части тела, связанная UV, с прозрачностью.

## 16. Entity skins
Humanoid: Minecraft-compatible UV layout, 64×64, структура Head/Body/Left&Right Arm/Left&Right Leg. Outer layer: Head/Inner+Outer, Body/Inner+Outer и т.д.

## 17. Outer layer ≠ просто текстура
Должен быть отдельной геометрией: Inner Model + Outer Model (чуть крупнее).

## 18. Анимация outer layer
Наследует transform родителя (Head→Outer Helmet, LeftArm→Shoulder→Forearm→Gauntlet). Нет независимых неподвижных частей.

## 19. Броня
Набор отдельных пластин, не один гладкий mesh: Helmet/Chest/Shoulders/Upper&Forearms/Elbows/Gauntlets/Belt/Hip Plates/Thighs/Knees/Shins/Boots — каждая со своей формой.

## 20. Единый set
Хороший элемент = эталон сета (Great Helm → REFERENCE → Chest/Shoulders/Arms/Legs/Boots adapt). Сохранять материал/цвет/толщину/полосы/края/заклёпки/highlights/shadows.

## 21. Medieval plate style
Крупные пластины, overlapping plates, rivets, reinforced edges, pauldrons, gauntlets, knee guards, greaves, sabatons, belts, central plates. НЕ sci-fi/tactical/modern/smooth exoskeleton.

## 22. Блоки
Учитывать все стороны (top/bottom/north/south/east/west); разные стороны ≠ одна текстура.

## 23. Блоки с узорами
Руда: stone base + ore veins. Жилы вписываются в камень, не RGB-пятна, палитра проекта, лёгкий shading, читаемость при тайлинге.

## 24. Tileable
Левый край ↔ правый, верх ↔ низ. Без швов/уникального центра/выдающего повторение. Проверять в сетке 2×2.

## 25. Блоки с анимацией
Огонь/энергия/свечение/вращение/активность → base + active текстуры (furnace_front / furnace_front_lit). Не менять всю текстуру для состояния.

## 26. Свечение
dark→main→bright→white hot core (d→A→P→f→W). Яркость локальная; не весь объект яркий.

## 27. Кристаллы
Тёмная основа + основные/светлые грани + энергожила + hotspot; геометрическая форма; без фото-прозрачности.

## 28. Материалы должны отличаться
Металл: sharp highlights; Камень: rough; Кожа: soft clusters; Кристалл: faceted highlights; Энергия: bright center; Дерево: directional grain.

## 29. Entity model scale
Visual Scale (модель ×2) и Physical Dimensions (EntityDimensions ×2) раздельно; не huge model + tiny hitbox. Проверять collision/eye height/attack range/navigation/pathfinding/bounding box.

## 30. Текстура и модель — разные вещи
Катана перевёрнута → rotation/translation; броня пересекается → ModelPart geometry/scale; цвет неверный → texture/palette; UV → UV mapping.

## 31. Референсы
Изображение = design reference: силуэт/пропорции/материалы/цвет/форма/детали/язык. Не копировать фото-детали; адаптировать под blocky geometry + pixel art + limited resolution.

## 32. Приоритеты
1 Silhouette, 2 Shape, 3 Proportions, 4 Material, 5 Major details, 6 Shading, 7 Highlights, 8 Micro-details. Не начинай с мелкого; силуэт не спасти пикселями.

## 33. Силуэт
На малой текстуре важнее деталей. Узнаваем без внутренних деталей = хороший.

## 34. Симметрия
Броня/шлемы/humanoids/механизмы: сначала симметричная основа (LEFT=RIGHT), потом асимметричные детали. Без случайной асимметрии.

## 35. Техническая проверка PNG
Формат/dimensions/alpha/нет фона/допустимые цвета/нет AA/нет лишних цветов/путь/имя/resource location. Palette-система: каждый цвет в разрешённой палитре.

## 36. Проверка ASCII
width exact, height exact (16×16 = 16 строк × 16 символов). Запрещены лишние пробелы/символы/неверная длина/неизвестные palette chars.

## 37. Проверка UV
Перед текстурой: resolution + face coordinates + UV. Не рисуй вслепую; неизвестна UV → изучи ModelPart/существующую текстуру.

## 38. Не ломать архитектуру
READ→UNDERSTAND→PLAN→MODIFY→BUILD→TEST. Не GUESS→REWRITE ALL. Без нужды нельзя: переименовывать registry IDs/менять package/удалять модели/создавать дубликаты/менять loader/mappings/renderer/удалять текстуры.

## 39. Версия Minecraft
Определи MC version/Fabric/NeoForge/loader/mappings/Java/rendering APIs. Не копируй чужую версию без проверки; адаптируй.

## 40. Build и validation
compile/build → fix errors → resource loading → клиент → texture/model/animation/layering/transparency/scale/UV → нет Z-fighting. Компиляция ≠ готово.

## 41. Z-fighting
Два близких слоя (Inner/Outer) → Outer имеет offset. Flickering → проверь scale/geometry/offset/render order.

## 42. Performance
few ModelParts + smart geometry + good texture, не сотни кубов. Особенно для массовых сущностей.

## 43. Имена
Понятные: helmet_outer/chest_outer/left_shoulder/right_knee/left_gauntlet… Не: part1/part2/thing/newThing/test2/final_final.

## 44. Описание изменений
Всегда сообщай Changed files/Textures/Models/Renderers/UV/Layers + что/почему/размеры/слои/как проверить.

## 45. Порядок принятия решений
«текстуру»: какая? размер? материал? палитра? аналог? UV? → создание. «модель»: какая Entity/Item? существующая Model? Renderer? ModelPart? Parent? → создание. «второй слой»: есть Inner? Outer? UV? ModelPart? scale? render order? transparency?

## 46. Приоритет качества
1 Работоспособность Minecraft, 2 Совместимость с архитектурой, 3 Корректная геометрия, 4 Корректный UV, 5 Читаемый силуэт, 6 Единый стиль, 7 Палитра, 8 Детализация. Не жертвуй техкорректностью ради красоты.

## 47. Главный rule (3 вопроса)
Выглядит как Minecraft? Часть моего мода? Технически корректно в игре? Только YES×3 = готово.

## 48. Финальный visual check
Размер PNG, alpha, палитра, силуэт, контур, свет, material shading, UV, ModelPart, layer, scale, анимация, нет Z-fighting/лишних цветов/AA/случайных деталей, стиль мода, компилируется, отображается.

## 49. Style anchor
«сделай остальные в таком же стиле» → используй объект как STYLE ANCHOR: silhouette/material language/palette/contrast/highlight direction/outline style/detail density/geometry language/decorative motifs → перенеси на новые. Копируй язык, не форму буквально.

## 50. Запрет на случайный redesign
«такого же стиля» = сохранить концепцию и язык, НЕ новый дизайн. Фиолетовые полосы/золото/серый металл/тёмный контур не заменять на красный/зелёный/синий/другой металл без прямого указания.

## 51. Финальная цель
ЭNTITIES+BLOCKS+ARMOR+ORES+WEAPONS+MACHINES+ITEMS+TILES = один визуальный язык. Каждый ресурс — будто всегда существовал в этом мире. **НЕ просто текстуры. Единая визуальная система мода.**