# Задача 17 — Кровавая луна: полный редизайн интерфейса (все меню)

> Статус: В РАБОТЕ → выполнено (2026-08-22). Сфера: UI (НЕ трогаем инвентари).

## 0. Запрос
Весь интерфейс (все меню) под тематику **кровавой луны**. Исключение — **инвентари** (ExoInventoryScreen, RewardVaultScreen, ванильные, креатив). Плавные неоновые анимации обязательны. Вся гамма — кровавая луна. В меню играет `menu_music`.

## 1. STYLE ANCHOR — существующий концепт «кровавой луны»
Источник (в коде): `CrimsonMoonClient` — эффект во время боя с Омегой (MASTER_PLAN §ТЮНИНГ 2026-08-22).
- Небо `BLOOD_SKY=(0.45,0.06,0.08)`, туман `BLOOD_FOG=(0.30,0.05,0.05)`; 90 красных комет + красные звёзды; плавный подъём 3с/затухание 6с.
Перенос в интерфейс: багрово-чёрная глубина, неоновый красный акцент, янтарь Хаику — вторичный «машинный» акцент.

## 2. Цветовая гамма (дизайн-токены, ARGB для GuiGraphics) — `BloodMoonTheme`
```text
VOID 0xFF0A0306  почти чёрный(красный подтон) | DEEP 0xFF160508 фон сцены | GROUND 0xFF1E070B силуэт земли
PANEL_BG 0xE020070D полупрозрачная тёмно-кровавая панель | PANEL_SOFT 0xE02A0A12 (кнопка normal) | PANEL_EDGE 0xFF4A121A рамка
BLOOD 0xFF8B1E2A глубокий кровавый (primary) | BLOOD_BRIGHT 0xFFB3202E | CRIMSON 0xFFE01020 ядро
NEON 0xFFFF3B45 неоновый красный (accent/hover) | NEON_SOFT 0x66FF3B45 glow
MOON 0xFFC42030 тело луны | MOON_DARK 0xFF7E0E18 тень | MOON_GLOW 0x55FF5A5A ореол
TEXT 0xFFEDD9D0 костяной белый | TEXT_MUTED 0xFF9C6B6E | TEXT_PALE 0xFFFF6B70 подсветка
AMBER 0xFFE8941E | AMBER_BRIGHT 0xFFFFB93E (янтарь Хаику — «кровь машин»)
```
Иерархия (skill §6): яркое = важное; неон только на интерактиве, фон глухой.

## 3. Типографика
Ванильный шрифт Minecraft. Заголовок «OPUS VS EXE» — крупно, многослойная неоновая подсветка (текст рисуется несколько раз со смещением и низкой альфой). Подзаголовок `menu.opusvsexe.subtitle`, приглушённый. Кнопки — костяной белый; hover — бледно-кровавый.

## 4. Анимации (плавные, неоновые; синусоиды/lerp на `guiRenderTime`)
| Элемент | Анимация | Период |
|---|---|---|
| Заголовок | пульс яркости 0.85→1.0 + сдвиг glow | ~2.2с |
| Луна | ореол дышит (радиус/альфа) | ~3.5с |
| Кнопка hover | рамка до NEON + мягкое свечение | 150мс ease |
| Кнопка pressed | затухание | 100мс |
| Эмберы | ~70 частиц всплывают, мерцают | 4–9с |
| Кометы | ~40 красных штрихов падают | 4–7с |
| Панель заголовка | рамка пульсирует | ~3с |

## 5. Сцена фона (BloodMoonTitleScreen)
Луна справа-сверху (ореол дышит, полумесяц), редкие звёзды-искры, кометы + эмберы, вертикальный градиент (тёмно-кровавый верх → почти чёрный низ), силуэт земли/тумана. Всё процедурно (fillGradient + triangle-fan круги + PositionColor), без PNG — «плавный неон», не pixel-art.

## 6. Layout главного меню
Луна сверху; «OPUS VS EXE» неоновый заголовок; подзаголовок «И наступила эпоха Haiku»; кнопки [Одиночная игра][Сетевая игра][Настройки][Выход] центрированы, единая ширина, шаг 12px; версия·copyright. Кнопки — `BloodMoonButton` (позже глобальный AbstractButtonMixin).

## 7. Музыка в меню
`menu_music.mp3` (5:51) → `sounds/music/menu_music.ogg` (libvorbis -q 4, stream). sounds.json: category `music`, stream:true. `ModSounds.MENU_MUSIC`. Проигрывание через ванильный `MusicManager` — миксин `MinecraftMusicMixin` на `Minecraft.getSituationalMusic()`: экран=BloodMoonTitleScreen → `new Music(Holder.direct(MENU_MUSIC), 20, 600, true)` (автоцикл, гашение при входе в мир/настройки, громкости штатно).

## 8. Объём редизайна
1. Главное меню — `BloodMoonTitleScreen` + `TitleScreenMixin`/`TitleScreenReplaceMixin` + `MinecraftMusicMixin`.
2. HUD (`ExoHud`) — кроваво-неоновая гамма + плавный пульс баров.
3. Пауза (`GameMenuScreen`) — кроваво-неоновый фон через миксин (только GameMenuScreen).
4. Инвентари — НЕ ТРОГАЕМ.

## 9. Технические детали
Новый пакет `com.opus.client.gui` (BloodMoonTheme, BloodMoonButton, BloodMoonTitleScreen). Миксины в `src/client/resources/opusvsexe.client.mixins.json`. `Button.builder(...).bounds().build()` — база 1.20.1; `BloodMoonButton extends Button` (protected 6-арг ctor) переопределяет `renderWidget`. Круг — triangle-fan (TRIANGLE_FAN, POSITION_COLOR, getPositionColorShader) с восстановлением blend/texture. Все цвета/тайминги через BloodMoonTheme. Проверка: build + runClient (GUI Scale 2/3/4, ресайз).

## 10. Чеклист завершения (skill §68) — выполнено
Тайтл open/close, Escape; кнопки + hover/pressed; музыка в меню и гасится в мире; нет клиппинга/перекрытий; работает на Scale 2/3/4; инвентари не изменились; нет crash.

## 11. Фикс «залипший экран» + глобальный редизайн (2026-08-22)
**Баг:** при выборе опции и выходе старый экран оставался виден; причина — `TitleScreenMixin` вызывал `mc.setScreen()` ре-ентрантно внутри `TitleScreen.init()`; `Minecraft.setScreen(null)` при level==null сам создаёт TitleScreen → «хвосты» на путях назад.
**Фикс:** `TitleScreenReplaceMixin` — перехват аргумента `Minecraft.setScreen(Screen)` на HEAD; подмена покрывает оба пути (явный и неявный). Без ре-ентрантности.
**Глобальные кнопки:** `AbstractButtonMixin` перехватывает `AbstractButton.renderWidget` и рисует все текстовые кнопки в кроваво-неоновом стиле (normal/hover/pressed/disabled), hover через `@Unique`-поле. Контролы (чекбоксы/слайдеры/cycle) переопределяют renderWidget сами.
**Все меню:** `ScreenBackgroundMixin` — фон для всех экранов кроме инвентарей (`AbstractContainerScreen`) и тайтла; level==null → кровавый фон (выбор/создание мира, настройки, мультиплеер, realms); в игре — пауза и настройки; оверлеи (чат/смерть/книга) не трогаем.

## 12. Фикс «два Esc» + музыка во всех опциях (2026-08-22)
- **Esc:** `BloodMoonTitleScreen.shouldCloseOnEsc()` → `false` (как у ванильного).
- **Музыка:** `MinecraftMusicMixin` → menu_music при ЛЮБОМ level==null (все меню); в игре — ванильная.
- **Single/Multiplayer:** `SelectWorldScreen`/`JoinMultiplayerScreen` не зовут renderBackground → отдельные миксины рисуют renderMenuBackground в начале render.
- **Ползунки:** `AbstractSliderButtonMixin` — неоновый слайдер (трек/заполнение/ручка/рамка).

## 13. Фон для всех опций + кнопки выхода (2026-08-22)
- **Фон:** `ScreenBackgroundMixin` ловит сам `renderDirtBackground` (единственный источник «земляного» фона) → рисует renderMenuBackground; покрывает ВСЕ экраны с грязью (под-опции, realms и т.д.).
- **Кнопки выхода:** `BloodMoonTitleScreen.added()` сбрасывает `startNanos` — fade-in одинаково при Esc и через кнопки «Готово/Назад».

## 14. Анимация на весь экран (2026-08-22)
Баг: координаты эмберов/комет жёстко `[0,640]` → на широких экранах частицы слева, верх почти чёрный; renderMenuBackground без луны/комет.
Фикс: `BloodMoonScene` — координаты нормализованы (0..1), пересоздаются при resize (`seed(width,height)`); renderMenuBackground рисует полную сцену (градиент с пульсом + дымка + кометы + эмберы + луна + горизонт) с чуть меньшей луной.