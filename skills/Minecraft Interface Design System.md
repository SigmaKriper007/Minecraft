
````markdown
---
name: minecraft-interface-design
description: Universal skill for designing, implementing and maintaining Minecraft mod interfaces, including HUD, screens, menus, buttons, panels, inventories, containers, icons, fonts, tooltips, animations and GUI textures.
---

# Minecraft Interface Design System

## 0. НАЗНАЧЕНИЕ

Этот skill определяет правила создания и изменения всего интерфейса Minecraft-мода.

Используй его при работе с:

- HUD;
- меню;
- Screen;
- GUI;
- контейнерами;
- инвентарями;
- кнопками;
- панелями;
- вкладками;
- переключателями;
- слайдерами;
- progress bars;
- health bars;
- energy bars;
- status indicators;
- tooltips;
- notifications;
- popups;
- overlays;
- crosshair;
- hotbar;
- custom inventory;
- block GUI;
- machine GUI;
- entity GUI;
- configuration screens;
- settings;
- icons;
- UI textures;
- fonts;
- UI animations.

Главная цель:

**создать единый, читаемый и технически корректный интерфейс, соответствующий визуальному стилю мода.**

---

# 1. ГЛАВНЫЙ ПРИНЦИП

Не создавай UI как набор случайных прямоугольников.

Интерфейс должен иметь единую систему:

```text
UI
├── Layout
├── Typography
├── Colors
├── Panels
├── Buttons
├── Icons
├── States
├── Spacing
├── Animation
└── Interaction
````

Каждый новый элемент должен соответствовать этой системе.

---

# 2. ПЕРЕД РАБОТОЙ ИЗУЧИ ПРОЕКТ

Перед изменением интерфейса обязательно найди:

* Minecraft version;
* mod loader;
* используемый Screen API;
* существующие Screen;
* существующие Widget;
* GUI textures;
* fonts;
* render utilities;
* color constants;
* UI helpers;
* существующий HUD;
* существующие menus.

Не создавай собственную UI-систему, если в проекте уже есть подходящая.

---

# 3. СНАЧАЛА ПРОВЕРЬ СУЩЕСТВУЮЩИЙ UI

Если в проекте уже есть экран:

```text
Existing Screen
      ↓
STYLE REFERENCE
```

Изучи:

* цвета;
* отступы;
* размеры;
* шрифты;
* радиусы;
* рамки;
* иконки;
* состояние кнопок;
* расположение элементов.

Новые экраны должны выглядеть как часть существующего интерфейса.

---

# 4. UI DESIGN SYSTEM

Используй единую систему:

```text
Colors
Typography
Spacing
Radius
Borders
Shadows
Icons
States
Animation
```

Не задавай каждый параметр вручную в каждом Screen.

По возможности используй централизованные constants.

Например:

```java
UIColors
UISpacing
UIStyle
UIFont
UIAnimations
```

---

# 5. ЦВЕТОВАЯ СИСТЕМА

Не используй случайные цвета.

Определи:

```text
Primary
Secondary
Background
Panel
Border
Text
Text Secondary
Accent
Success
Warning
Error
Disabled
```

Пример:

```text
Primary      → основной акцент
Secondary    → вторичный акцент
Background   → фон
Panel        → панели
Border       → границы
Text         → основной текст
Muted Text   → вторичный текст
Accent       → интерактивные элементы
```

Если мод уже имеет палитру:

**используй палитру мода.**

Не создавай новую палитру для каждого экрана.

---

# 6. ИЕРАРХИЯ ЦВЕТА

Цвет должен помогать пользователю понимать интерфейс.

Например:

```text
Background
    ↓
Panel
    ↓
Border
    ↓
Button
    ↓
Accent
    ↓
Important element
```

Самые яркие цвета должны использоваться для наиболее важных элементов.

Не делай весь UI одинаково ярким.

---

# 7. КОНТРАСТ

Текст должен быть читаемым.

Проверяй:

```text
Text vs Background
Icon vs Background
Button Text vs Button
Progress vs Background
Border vs Panel
```

Не используй близкие оттенки для важных элементов.

---

# 8. SPACING

Используй единую систему отступов.

Например:

```text
4 px
8 px
12 px
16 px
24 px
32 px
```

Не используй случайные:

```text
7 px
13 px
19 px
23 px
```

если для этого нет причины.

Интерфейс должен выглядеть структурированным.

---

# 9. ALIGNMENT

Элементы должны выравниваться относительно друг друга.

Используй:

```text
left alignment
center alignment
right alignment
grid alignment
```

Не располагай элементы приблизительно.

Например:

```text
BAD

[Button]
             [Button]


GOOD

[Button]     [Button]
```

---

# 10. LAYOUT

Для каждого Screen сначала определяй layout.

Например:

```text
┌───────────────────────────────┐
│             TITLE             │
├───────────────────────────────┤
│                               │
│        CONTENT AREA           │
│                               │
├───────────────────────────────┤
│       ACTIONS / BUTTONS       │
└───────────────────────────────┘
```

Сначала:

```text
Layout
```

потом:

```text
Visual decoration
```

Не наоборот.

---

# 11. RESPONSIVE UI

Нельзя рассчитывать UI только на одно разрешение.

Учитывай:

* screen width;
* screen height;
* GUI scale;
* aspect ratio;
* window resizing.

Не используй абсолютные координаты там, где нужен относительный layout.

Плохо:

```java
x = 932;
y = 421;
```

Лучше:

```java
x = screenWidth / 2 - width / 2;
```

или использовать layout calculations.

---

# 12. GUI SCALE

Всегда проверяй интерфейс при разных GUI Scale.

Минимум:

```text
GUI Scale 2
GUI Scale 3
GUI Scale 4
```

Если игра позволяет — проверь также Auto.

UI не должен:

* перекрываться;
* исчезать;
* выходить за экран;
* ломать текст;
* обрезать кнопки.

---

# 13. HUD

HUD должен быть:

* компактным;
* читаемым;
* не мешать игре;
* иметь чёткую иерархию.

Не размещай важные элементы там, где Minecraft уже показывает:

* hotbar;
* health;
* armor;
* hunger;
* boss bar;
* chat.

Если нужно заменить vanilla HUD:

сначала изучи существующий HUD renderer.

---

# 14. HUD POSITIONS

Не привязывай HUD без причины к конкретному разрешению.

Используй:

```text
screen edges
safe margins
anchors
```

Например:

```text
Top Left
Top Center
Top Right
Bottom Left
Bottom Center
Bottom Right
```

Каждый HUD элемент должен иметь понятный anchor.

---

# 15. HUD PRIORITY

Информация должна иметь приоритет:

```text
Critical
 ↓
Important
 ↓
Useful
 ↓
Decorative
```

Например:

```text
Health
Energy
Status
Ammo
Cooldown
Decorations
```

Не делай декоративный элемент визуально ярче здоровья.

---

# 16. PANELS

Панели должны иметь:

```text
background
border
padding
content
```

Структура:

```text
┌────────────────────────┐
│       HEADER           │
├────────────────────────┤
│                        │
│       CONTENT          │
│                        │
└────────────────────────┘
```

Не используй случайные рамки.

---

# 17. BUTTONS

Каждая кнопка должна иметь состояния:

```text
Normal
Hover
Pressed
Disabled
Focused
```

Например:

```text
Normal
████████████

Hover
▓▓▓▓▓▓▓▓▓▓▓▓

Pressed
▒▒▒▒▒▒▒▒▒▒▒▒

Disabled
░░░░░░░░░░░░
```

Состояния должны отличаться визуально.

---

# 18. BUTTON INTERACTION

Кнопка должна:

* реагировать на mouse hover;
* реагировать на click;
* иметь disabled state;
* иметь понятный visual feedback.

Не создавай кнопку, которая визуально выглядит одинаково во всех состояниях.

---

# 19. TOOLTIPS

Tooltip должен:

* появляться рядом с объектом;
* не выходить за экран;
* быть читаемым;
* иметь consistent background;
* иметь правильный padding.

Структура:

```text
┌──────────────────────┐
│ Item Name             │
│                       │
│ Description           │
│ Damage: 12            │
└──────────────────────┘
```

Не делай tooltip огромным без необходимости.

---

# 20. ИКОНКИ

Иконки должны иметь единый:

* размер;
* pixel density;
* outline;
* shading;
* visual weight.

Например:

```text
16×16
24×24
32×32
```

Не смешивай:

```text
pixel icon
+
photorealistic icon
```

---

# 21. PIXEL-ART UI

Если UI выполнен в pixel-art стиле:

используй:

* чёткие пиксели;
* nearest-neighbor;
* ограниченную палитру;
* отсутствие anti-aliasing;
* отсутствие blur.

UI должен соответствовать текстурам мода.

---

# 22. UI TEXTURES

Для PNG:

проверяй:

```text
width
height
alpha
palette
UV
```

Не растягивай маленькую pixel-art текстуру обычным сглаживанием.

Используй nearest-neighbor.

---

# 23. UI ATLAS / TEXTURE SHEETS

Если используется texture atlas:

сначала изучи существующий atlas.

Не создавай новые atlas без необходимости.

Следи за:

```text
UV coordinates
texture size
padding
bleeding
```

Особенно при масштабировании.

---

# 24. TEXTURE BLEEDING

Если соседние элементы texture atlas начинают визуально смешиваться:

проверь:

* UV;
* padding;
* texture filtering;
* atlas layout.

Pixel-art UI не должен получать полосы от соседних элементов.

---

# 25. TYPOGRAPHY

Шрифт должен соответствовать стилю мода.

Проверяй:

* размер;
* вес;
* цвет;
* line height;
* alignment;
* readability.

Не используй слишком много разных шрифтов.

Рекомендуется:

```text
Title
Subtitle
Body
Small
```

---

# 26. ТЕКСТ

Не помещай текст слишком близко к краю панели.

Используй:

```text
Panel
 ↓
Padding
 ↓
Text
```

Не:

```text
Panel
Text touching border
```

---

# 27. TEXT WRAPPING

Если текст динамический:

обязательно учитывай перенос строк.

Не допускай:

```text
This is a very long text that goes
outside the screen
```

Используй:

```text
line wrapping
max width
```

---

# 28. ПРОГРЕСС-БАРЫ

Progress bar должен иметь:

```text
background
fill
optional border
optional icon
```

Например:

```text
[████████████░░░░░░░░] 60%
```

Для energy:

```text
dark background
+
colored energy
+
bright highlight
```

---

# 29. СОСТОЯНИЯ

Любой интерактивный элемент должен иметь state.

Например:

```text
ACTIVE
INACTIVE
LOCKED
DISABLED
SELECTED
HOVERED
PRESSED
ERROR
SUCCESS
```

State должен быть понятен не только по цвету.

Используй также:

* icon;
* border;
* shape;
* text;
* animation.

---

# 30. АНИМАЦИИ

UI-анимации должны быть:

* короткими;
* понятными;
* плавными;
* не раздражающими.

Используй анимацию для:

* opening;
* closing;
* hover;
* selection;
* progress;
* notification;
* state changes.

Не анимируй всё подряд.

---

# 31. ANIMATION TIMING

Пример:

```text
Hover:
~100–150 ms

Button transition:
~100–200 ms

Panel opening:
~150–250 ms

Notification:
~200 ms
```

Используй interpolation.

Не делай резкие изменения без причины.

---

# 32. UI DEPTH

Если интерфейс использует глубину:

```text
background
↓
panel
↓
nested panel
↓
button
↓
tooltip
```

Каждый уровень должен иметь визуальное отличие.

Не используй десять теней.

---

# 33. GUI CONTAINERS

Для inventory/container GUI:

обязательно учитывать:

* slots;
* item rendering;
* hover;
* selected slot;
* empty slot;
* player inventory;
* container inventory;
* tooltip.

Все элементы должны быть выровнены.

---

# 34. SLOT DESIGN

Slot должен иметь:

```text
background
border
item area
hover state
```

Не делай слот слишком маленьким относительно Minecraft item.

---

# 35. MACHINE GUI

Для машин:

```text
Input
Processing
Output
Power
Progress
Status
```

Например:

```text
┌───────────────────────────┐
│ MACHINE                   │
│                           │
│ [INPUT] → [PROCESS] → [OUT]
│                           │
│ Energy ███████░░░░         │
│ Status: Processing         │
└───────────────────────────┘
```

Сначала определяй information hierarchy.

---

# 36. CONFIGURATION SCREEN

Настройки должны иметь:

```text
Category
Setting
Current Value
Control
Description
```

Например:

```text
Graphics

Bloom
[ ON ]

UI Scale
[ 100% ]

Show Energy
[ ON ]
```

Не помещай все настройки в один огромный список без группировки.

---

# 37. NAVIGATION

Если экранов несколько:

должна существовать понятная навигация.

Например:

```text
Main Menu
 ├── Inventory
 ├── Equipment
 ├── Research
 ├── Settings
 └── Back
```

Всегда обеспечивай понятный способ вернуться назад.

---

# 38. KEYBOARD

Если интерфейс поддерживает клавиатуру:

обеспечь:

* Tab navigation;
* Enter;
* Escape;
* стрелки;
* shortcut keys.

Не делай интерфейс полностью зависимым от мыши, если это не требуется.

---

# 39. MOUSE

Обрабатывай:

* mouse move;
* left click;
* right click;
* scroll;
* drag;
* release.

Не допускай, чтобы один элемент случайно перехватывал события другого.

---

# 40. ACCESSIBILITY

По возможности учитывай:

* достаточный контраст;
* размер текста;
* отсутствие информации только через цвет;
* читаемые tooltips;
* понятные состояния;
* keyboard navigation.

---

# 41. ЗАПРЕТ НА HARDCODED UI

Не размазывай по проекту:

```java
0xFF123456
```

или:

```java
x = 173;
y = 421;
```

если значение относится к общей UI-системе.

Используй constants:

```java
UIColors.ACCENT
UISpacing.MEDIUM
UIStyle.PANEL
```

---

# 42. UI COMPONENTS

Если один элемент используется несколько раз:

создай reusable component.

Например:

```text
Panel
Button
IconButton
ProgressBar
Tab
Tooltip
Slider
Toggle
```

Вместо копирования одного и того же кода в каждом Screen.

---

# 43. НЕ ДУБЛИРОВАТЬ КОД

Плохо:

```text
Screen A
 └── custom button code

Screen B
 └── same custom button code

Screen C
 └── same custom button code
```

Лучше:

```text
UI Components
 └── Button
       ↓
Screen A
Screen B
Screen C
```

---

# 44. SCREEN ARCHITECTURE

Для сложного интерфейса разделяй:

```text
Screen
 ├── Layout
 ├── Components
 ├── State
 ├── Rendering
 └── Input
```

Не помещай всё в один огромный render().

---

# 45. STATE

UI должен получать данные из состояния приложения.

Например:

```text
energy
health
progress
selectedItem
activeTab
settings
```

Не рисуй статические значения там, где они должны быть динамическими.

Плохо:

```text
Energy: 73%
```

если реальная энергия меняется.

Правильно:

```text
Energy = machine.getEnergy()
```

---

# 46. CLIENT / SERVER

Если Minecraft mod multiplayer:

UI должен правильно разделять:

```text
Client
Server
```

Не доверяй клиенту критические игровые значения.

Например:

```text
Client → display
Server → authoritative state
```

---

# 47. NETWORKING GUI

Если UI изменяет игровые данные:

используй существующую network architecture проекта.

Например:

```text
Button
 ↓
Client packet
 ↓
Server validation
 ↓
State change
 ↓
Client update
```

Не меняй важные игровые данные только на клиенте.

---

# 48. PERFORMANCE

Не создавай тяжёлые объекты каждый frame.

Избегай:

* постоянного создания новых текстур;
* постоянного создания шрифтов;
* огромных allocations;
* тяжёлых вычислений в render loop.

Кэшируй ресурсы там, где это необходимо.

---

# 49. RENDERING

Перед использованием custom rendering изучи существующий renderer.

Не создавай новый render pipeline без необходимости.

Соблюдай:

* правильный pose stack;
* правильный coordinate system;
* правильный z-order;
* правильный scissor;
* правильный alpha;
* правильный texture binding.

После render обязательно восстанавливай состояние, если API этого требует.

---

# 50. SCISSOR / CLIPPING

Для scrollable UI:

используй clipping/scissor.

Контент не должен рисоваться за пределами scroll area.

Например:

```text
┌───────────────┐
│ Content       │
│ Content       │
│ Content       │
│───────────────│
│ clipped       │
└───────────────┘
```

---

# 51. SCROLLING

Scrollable элементы должны иметь:

* current scroll;
* max scroll;
* clamp;
* mouse wheel;
* optional scrollbar.

Всегда ограничивай:

```text
scroll >= 0
scroll <= maxScroll
```

---

# 52. UI Z-ORDER

Рисуй элементы в правильном порядке:

```text
Background
 ↓
Panels
 ↓
Decorations
 ↓
Slots
 ↓
Items
 ↓
Text
 ↓
Hover
 ↓
Tooltip
```

Tooltip обычно должен находиться выше остальных элементов.

---

# 53. UI SCALE

Не смешивай:

```text
Minecraft GUI scale
+
custom pixel scale
```

без необходимости.

Определи единицу измерения UI и придерживайся её.

---

# 54. RESPONSIVE ANCHORS

Используй anchors:

```text
TOP_LEFT
TOP_CENTER
TOP_RIGHT
CENTER
BOTTOM_LEFT
BOTTOM_CENTER
BOTTOM_RIGHT
```

Для HUD это особенно важно.

---

# 55. UI REFERENCE

Если пользователь предоставляет скриншот интерфейса:

анализируй:

```text
Layout
Spacing
Colors
Typography
Shapes
Borders
Icons
Hierarchy
```

Не копируй изображение как bitmap, если задача требует настоящего Minecraft UI.

Воссоздай его через:

```text
Screen
Widgets
Textures
Components
```

---

# 56. PIXEL-PERFECT UI

Если пользователь требует pixel-perfect:

используй:

* целочисленные координаты;
* целочисленные размеры;
* nearest-neighbor;
* фиксированную pixel grid;
* отсутствие fractional scaling там, где оно ломает пиксели.

---

# 57. UI ICONS

Иконки должны быть узнаваемыми даже в маленьком размере.

Приоритет:

```text
Silhouette
+
Contrast
+
Simple detail
```

Не перегружай 16×16 icon мелкими деталями.

---

# 58. UI TEXTURE STYLE

Если мод использует pixel-art textures:

UI должен использовать тот же:

* pixel density;
* outline;
* palette;
* highlight direction;
* contrast.

Интерфейс не должен выглядеть как отдельная игра внутри мода.

---

# 59. ERROR STATES

Для ошибок используй отдельный state:

```text
ERROR
WARNING
SUCCESS
```

Но не ограничивайся цветом.

Добавляй:

```text
icon
+
text
+
border/state
```

---

# 60. EMPTY STATES

Если список пуст:

не оставляй просто пустое место.

Используй:

```text
icon
+
message
+
optional action
```

Например:

```text
[ icon ]

No research available

Explore the world to unlock research.
```

---

# 61. LOADING STATES

Если данные загружаются:

показывай:

```text
Loading...
```

или progress indicator.

Не оставляй интерфейс визуально зависшим.

---

# 62. NOTIFICATIONS

Notification должен:

* появляться в безопасной области;
* иметь понятную иерархию;
* не закрывать важную информацию;
* автоматически исчезать, если это transient message.

Например:

```text
┌──────────────────────┐
│ ✓ Research unlocked  │
└──────────────────────┘
```

---

# 63. UI SOUND

Если проект использует UI sounds:

используй их консистентно.

Например:

```text
hover
click
open
close
error
success
```

Не добавляй звук на каждый frame.

---

# 64. АДАПТАЦИЯ ПОД СТИЛЬ МОДА

Если основной мод использует:

```text
dark purple
cyan energy
gray metal
```

UI должен использовать те же визуальные ассоциации.

Например:

```text
Dark background
+
Purple panels
+
Cyan active state
+
White text
+
Purple highlights
```

Но не превращай каждый элемент в неоновый.

---

# 65. НЕЛЬЗЯ

Не создавай UI, который:

* выглядит как случайный HTML;
* использует стандартные веб-кнопки без адаптации;
* содержит слишком много цветов;
* имеет слишком много рамок;
* перегружен декоративными элементами;
* имеет маленький нечитаемый текст;
* ломается на другом разрешении;
* перекрывает vanilla HUD;
* содержит элементы за пределами экрана;
* использует случайные отступы;
* использует разные стили на разных экранах.

---

# 66. ПОРЯДОК СОЗДАНИЯ НОВОГО ЭКРАНА

Всегда:

```text
1. Анализ проекта
       ↓
2. Анализ существующего UI
       ↓
3. Определение information hierarchy
       ↓
4. Layout
       ↓
5. Components
       ↓
6. Colors
       ↓
7. Typography
       ↓
8. Interaction
       ↓
9. Animation
       ↓
10. Textures
       ↓
11. Implementation
       ↓
12. Build
       ↓
13. In-game testing
```

---

# 67. ПОРЯДОК СОЗДАНИЯ UI-КОМПОНЕНТА

Например, новая кнопка:

```text
Purpose
 ↓
Size
 ↓
Position
 ↓
Normal
 ↓
Hover
 ↓
Pressed
 ↓
Disabled
 ↓
Focus
 ↓
Interaction
 ↓
Animation
```

---

# 68. ПРОВЕРКА UI

После реализации проверь:

```text
[ ] Screen открывается
[ ] Screen закрывается
[ ] Escape работает
[ ] Mouse работает
[ ] Keyboard работает
[ ] Buttons работают
[ ] Hover работает
[ ] Disabled работает
[ ] Tooltips работают
[ ] Text readable
[ ] GUI Scale работает
[ ] Window resize работает
[ ] Нет clipping
[ ] Нет overlap
[ ] Нет Z-order ошибок
[ ] Нет texture bleeding
[ ] Нет ошибок загрузки ресурсов
[ ] Нет client crash
[ ] Нет server-side ошибок
```

---

# 69. ПРОВЕРКА НА РАЗНЫХ РАЗРЕШЕНИЯХ

Минимально проверить:

```text
1280×720
1920×1080
2560×1440
```

Если возможно.

Также проверить разные GUI Scale.

---

# 70. ПРОВЕРКА HUD

Для HUD:

```text
[ ] не перекрывает hotbar
[ ] не перекрывает chat
[ ] не перекрывает boss bar
[ ] не выходит за границы
[ ] сохраняет position
[ ] правильно масштабируется
[ ] отображает актуальные данные
```

---

# 71. ПРОВЕРКА CONTAINER GUI

Для контейнера:

```text
[ ] slots correct
[ ] items render
[ ] hover works
[ ] click works
[ ] shift-click works
[ ] drag works
[ ] tooltip works
[ ] inventory works
[ ] server synchronization works
```

---

# 72. ПРОВЕРКА MULTIPLAYER

Если UI взаимодействует с сервером:

```text
Client
 ↓
Packet
 ↓
Server
 ↓
Validation
 ↓
State
 ↓
Client
```

Проверяй это отдельно.

---

# 73. НЕ ЛОМАЙ VANILLA

Если мод изменяет vanilla UI:

не удаляй vanilla functionality без явного требования.

Сначала определяй:

```text
Replace
Modify
Extend
Overlay
```

Предпочтительно:

```text
Extend / Overlay
```

если пользователь не просит полную замену.

---

# 74. REUSABLE DESIGN TOKENS

По возможности создай:

```text
UIColors
UISpacing
UIRadius
UIBorder
UIFonts
UIAnimations
UIIcons
```

Например:

```java
UIColors.BACKGROUND
UIColors.PANEL
UIColors.ACCENT
UIColors.TEXT

UISpacing.SMALL
UISpacing.MEDIUM
UISpacing.LARGE
```

Это позволяет менять весь UI централизованно.

---

# 75. ФИНАЛЬНЫЙ ПРИНЦИП

Интерфейс должен быть:

**понятным → читаемым → консистентным → функциональным → красивым.**

Не наоборот.

Красивый интерфейс, которым невозможно пользоваться, является плохим UI.

---

# 76. ГЛАВНЫЙ RULE

Перед завершением любой UI-задачи задай себе:

### 1. Пользователь понимает, что здесь происходит?

### 2. Пользователь понимает, что можно нажать?

### 3. Пользователь понимает текущее состояние?

### 4. Интерфейс соответствует стилю мода?

### 5. Интерфейс работает на разных GUI Scale?

### 6. Интерфейс технически корректен?

Если хотя бы один ответ:

```text
NO
```

задача не считается завершённой.

---

# 77. ФИНАЛЬНАЯ ЦЕЛЬ

Весь интерфейс мода должен ощущаться как одна система:

```text
                    UI SYSTEM
                        │
        ┌───────────────┼───────────────┐
        │               │               │
       HUD            MENUS           GUI
        │               │               │
     Status          Settings        Machines
     Energy          Research        Inventory
     Health          Equipment       Containers
        │               │               │
        └───────────────┼───────────────┘
                        │
                 SAME DESIGN SYSTEM
                        │
             Colors / Typography
             Spacing / Components
             Icons / Animation
             Interaction / Layout
```

Каждый новый экран должен выглядеть так, будто он был частью мода с самого начала.

**НЕ СОЗДАВАЙ ОТДЕЛЬНЫЕ ЭКРАНЫ. СОЗДАВАЙ ЕДИНУЮ UI-СИСТЕМУ МОДА.**

```
```
