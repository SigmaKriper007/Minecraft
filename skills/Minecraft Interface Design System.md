````markdown
---
name: minecraft-interface-design
description: Universal skill for designing, implementing and maintaining Minecraft mod interfaces, including HUD, screens, menus, buttons, panels, inventories, containers, icons, fonts, tooltips, animations and GUI textures.
---

# Minecraft Interface Design System

## 0. Назначение
Правила создания и изменения всего интерфейса мода. Применять к: HUD, меню, Screen, GUI, контейнерам, инвентарям, кнопкам, панелям, вкладкам, переключателям, слайдерам, progress/health/energy bars, status indicators, tooltips, notifications, popups, overlays, crosshair, hotbar, custom inventory, block/machine/entity GUI, config screens, settings, icons, UI textures, fonts, UI animations.
**Цель: единый, читаемый, технически корректный интерфейс в стиле мода.**

## 1. Главный принцип
UI = единая система (Layout, Typography, Colors, Panels, Buttons, Icons, States, Spacing, Animation, Interaction). Каждый новый элемент следует системе. Не создавать UI как набор случайных прямоугольников.

## 2. Перед работой изучи проект
Найди: Minecraft version, mod loader, Screen API, существующие Screen/Widget, GUI textures, fonts, render utilities, color constants, UI helpers, HUD, menus. Не создавай свою UI-систему, если есть подходящая.

## 3. Проверь существующий UI
Если есть экран — он STYLE REFERENCE. Изучи: цвета, отступы, размеры, шрифты, радиусы, рамки, иконки, состояния кнопок, расположение. Новые экраны — часть существующего интерфейса.

## 4. Design system
Единая система: Colors/Typography/Spacing/Radius/Borders/Shadows/Icons/States/Animation. Централизованные constants (UIColors, UISpacing, UIStyle, UIFont, UIAnimations). Не задавать параметры вручную в каждом Screen.

## 5. Цветовая система
Токены: Primary, Secondary, Background, Panel, Border, Text, Text Secondary, Accent, Success, Warning, Error, Disabled. Если есть палитра мода — используй её; не плодить палитры на экран.

## 6. Иерархия цвета
Background→Panel→Border→Button→Accent→Important element. Яркое = важное. Не весь UI одинаково яркий.

## 7. Контраст
Читаемость: Text vs Background, Icon vs Background, Button Text vs Button, Progress vs Background, Border vs Panel. Без близких оттенков для важного.

## 8. Spacing
Единая система (4/8/12/16/24/32 px). Случайные 7/13/19/23 px только по причине.

## 9. Alignment
left/center/right/grid — элементы выровнены относительно друг друга, не «примерно».

## 10. Layout
Сначала layout, потом decoration. Пример: TITLE → CONTENT AREA → ACTIONS/BUTTONS.

## 11. Responsive
Учитывать screen width/height, GUI scale, aspect ratio, window resize. Относительные координаты — не абсолютные (плохо `x=932;y=421`, хорошо `x=screenWidth/2-width/2`).

## 12. GUI Scale
Проверять при Scale 2/3/4 (+Auto). UI не должен перекрываться/исчезать/выходить за экран/ломать текст/обрезать кнопки.

## 13. HUD
Компактный, читаемый, с чёткой иерархией, не мешает игре. Не ставить поверх hotbar/health/armor/hunger/boss bar/chat. Замена vanilla — сначала изучить HUD renderer.

## 14. HUD positions
Привязка к screen edges/safe margins/anchors (Top/Bottom × Left/Center/Right). Каждый элемент — понятный anchor; не привязываться к разрешению.

## 15. HUD priority
Critical→Important→Useful→Decorative (Health→Energy→Status→Ammo→Cooldown→Decorations). Декор не ярче здоровья.

## 16. Panels
Имеют background/border/padding/content; структура HEADER/CONTENT. Не случайные рамки.

## 17. Buttons
Состояния: Normal/Hover/Pressed/Disabled/Focused — визуально различимые.

## 18. Button interaction
hover/click/disabled/visual feedback. Не одинаковая кнопка во всех состояниях.

## 19. Tooltips
У объекта, не за экран, читаемый, consistent background + padding. Не огромный без нужды.

## 20. Иконки
Единые размер/pixel density/outline/shading/visual weight (16/24/32). Не смешивать pixel и photorealistic.

## 21. Pixel-art UI
Чёткие пиксели, nearest-neighbor, ограниченная палитра, без anti-aliasing/blur. Соответствует текстурам мода.

## 22. UI textures
Проверять width/height/alpha/palette/UV. Маленькую pixel-art не растягивать сглаживанием — nearest-neighbor.

## 23. Atlas / sheets
Изучить существующий; новые — только при необходимости. Следить за UV/размером/padding/bleeding (при масштабировании).

## 24. Texture bleeding
Смешивание соседей атласа → проверить UV/padding/filtering/layout. Pixel-art без полос от соседей.

## 25. Typography
Шрифт в стиле мода. Проверять размер/вес/цвет/line height/alignment/readability. Мало шрифтов: Title/Subtitle/Body/Small.

## 26. Текст
Не вплотную к краям панели: Panel→Padding→Text (не «Text touching border»).

## 27. Text wrapping
Динамический текст — учитывать перенос строк и max width; не за экран.

## 28. Progress bars
background/fill/optional border/icon. Energy: dark bg + colored energy + bright highlight.

## 29. Состояния
Любой интерактив имеет state (ACTIVE/INACTIVE/LOCKED/DISABLED/SELECTED/HOVERED/PRESSED/ERROR/SUCCESS). Понятно не только цветом — icon/border/shape/text/animation.

## 30. Анимации
Короткие, понятные, плавные, не раздражающие: opening/closing/hover/selection/progress/notification/state change. Не анимировать всё.

## 31. Animation timing
Hover ~100–150ms, button ~100–200ms, panel ~150–250ms, notification ~200ms. Interpolation, без резких скачков.

## 32. UI depth
background→panel→nested panel→button→tooltip — каждый уровень визуально отличается. Не десять теней.

## 33. GUI containers
Учитывать: slots, item rendering, hover, selected/empty slot, player & container inventory, tooltip. Выравнивание.

## 34. Slots
background/border/item area/hover state. Не слишком маленький относительно Minecraft item.

## 35. Machine GUI
Information hierarchy: Input/Processing/Output/Power/Progress/Status. Сначала hierarchy.

## 36. Configuration screen
Category/Setting/Current Value/Control/Description. Не всё одним списком.

## 37. Navigation
Понятная навигация между экранами + возможность вернуться назад.

## 38. Keyboard
Tab/Enter/Escape/стрелки/shortcuts. Не делай интерфейс мышезависимым без нужды.

## 39. Mouse
move/left/right click/scroll/drag/release. Без перехвата событий чужих элементов.

## 40. Accessibility
Контраст, размер текста, не только цвет, читаемые tooltips, состояния, keyboard navigation.

## 41. No hardcoded UI
Общие значения — через constants (UIColors.ACCENT, UISpacing.MEDIUM, UIStyle.PANEL), не 0xFF123456 / x=173 разбросанные по проекту.

## 42. Reusable components
Часто используемое → компонент (Panel/Button/IconButton/ProgressBar/Tab/Tooltip/Slider/Toggle). Вместо копирования кода в экраны.

## 43. No code duplication
Один Button компонент для всех Screen, не копия в каждом.

## 44. Screen architecture
Разделять Layout/Components/State/Rendering/Input. Не один огромный render().

## 45. State
UI берёт данные из состояния (energy/health/progress/selectedItem/activeTab/settings). Не статичные значения (Energy=73% vs Energy=machine.getEnergy()).

## 46. Client / Server
Client→display, Server→authoritative state. Не доверять клиенту критические значения.

## 47. Networking GUI
Изменение данных — через сеть: Button→Client packet→Server validation→State change→Client update. Не менять важное только на клиенте.

## 48. Performance
Не создавать тяжёлые объекты каждый frame (текстуры/шрифты/аллокации/вычисления). Кэшировать.

## 49. Rendering
Изучить существующий renderer; не плодить pipeline. Правильные pose stack/координаты/z-order/scissor/alpha/texture binding; восстанавливать состояние.

## 50. Scissor / clipping
Scrollable — clipping; контент не рисуется вне области.

## 51. Scrolling
current/max/clamp, mouse wheel, optional scrollbar; scroll >= 0 и <= maxScroll.

## 52. Z-order
Background→Panels→Decorations→Slots→Items→Text→Hover→Tooltip. Tooltip выше всего.

## 53. UI scale
Одна единица измерения; не смешивать Minecraft GUI scale + custom pixel scale без нужды.

## 54. Anchors
TOP_LEFT/TOP_CENTER/TOP_RIGHT/CENTER/BOTTOM_*. Особенно для HUD.

## 55. UI reference
Скриншот → анализировать Layout/Spacing/Colors/Typography/Shapes/Borders/Icons/Hierarchy и воссоздать через Screen/Widgets/Textures/Components. Не копировать bitmap.

## 56. Pixel-perfect
Целочисленные координаты/размеры, nearest-neighbor, фиксированный grid, без fractional scaling, ломающего пиксели.

## 57. UI icons
Узнаваемые в малом размере: Silhouette+Contrast+Simple detail. Не перегружать 16×16.

## 58. Texture style
Pixel-art мод → UI с тем же pixel density/outline/palette/highlight direction/contrast. Не «игра в игре».

## 59. Error states
ERROR/WARNING/SUCCESS + icon/text/border, не только цвет.

## 60. Empty states
icon + message + optional action. Не оставлять пустоту.

## 61. Loading states
"Loading…"/progress indicator при загрузке; не зависший вид.

## 62. Notifications
Безопасная область, понятная иерархия, не закрывают важное, авто-исчезание для transient.

## 63. UI sound
Консистентно (hover/click/open/close/error/success). Не на каждый frame.

## 64. Адаптация под стиль мода
Dark bg + purple panels + cyan active + white text + purple highlights (напр.), но не всё неоновым.

## 65. НЕЛЬЗЯ
Случайный HTML-вид; веб-кнопки без адаптации; слишком много цветов/рамок/декора; мелкий текст; ломается на другом разрешении; перекрывает vanilla HUD; элементы за экраном; случайные отступы; разные стили экранов.

## 66. Порядок создания экрана
1 Анализ проекта → 2 Анализ UI → 3 Information hierarchy → 4 Layout → 5 Components → 6 Colors → 7 Typography → 8 Interaction → 9 Animation → 10 Textures → 11 Implementation → 12 Build → 13 In-game testing.

## 67. Порядок создания компонента (кнопка)
Purpose → Size → Position → Normal → Hover → Pressed → Disabled → Focus → Interaction → Animation.

## 68. Проверка UI
Screen open/close, Escape, Mouse, Keyboard, Buttons, Hover, Disabled, Tooltips, Text readable, GUI Scale, resize, нет clipping/overlap/z-order ошибок/texture bleeding/ошибок загрузки, нет crash (client/server).

## 69. Разрешения
Проверять 1280×720 / 1920×1080 / 2560×1440 (+ GUI Scale).

## 70. HUD check
Не перекрывает hotbar/chat/boss bar; не за границы; сохраняет position; масштабируется; актуальные данные.

## 71. Container check
Slots correct, items render, hover/click/shift-click/drag/tooltip, inventory, server sync.

## 72. Multiplayer
Client→Packet→Server→Validation→State→Client. Проверять отдельно.

## 73. Не ломай vanilla
Сначала определи Replace/Modify/Extend/Overlay; предпочтительно Extend/Overlay, если не просили замену. Не удалять vanilla-функции без требования.

## 74. Design tokens
UIColors/UISpacing/UIRadius/UIBorder/UIFonts/UIAnimations/UIIcons — централизованная смена стиля.

## 75. Финальный принцип
Интерфейс: понятный → читаемый → консистентный → функциональный → красивый. Красивый, но неюзабельный = плохой UI.

## 76. Главный rule (6 вопросов)
Пользователь понимает: что происходит? что можно нажать? текущее состояние? UI в стиле мода? работает на разных GUI Scale? технически корректен? Если хоть один NO — не завершено.

## 77. Финальная цель
HUD + MENUS + GUI = ОДНА UI-система (Colors/Typography/Spacing/Components/Icons/Animation/Interaction/Layout). Каждый экран — часть мода с самого начала. **НЕ создавай отдельные экраны. Создавай единую UI-систему мода.**
````