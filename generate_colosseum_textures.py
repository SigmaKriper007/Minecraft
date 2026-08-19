#!/usr/bin/env python3
"""
Генерация текстур для мода OpusVsExe - Колизей Вечной Памяти
Следует инструкциям из Minecraft Texture & Visual Design System.md
"""

import os
from PIL import Image, ImageDraw

# Палитра проекта на основе существующих текстур
PALETTE = {
    # Опус - темно-серый с голубыми прожилками
    'd': (40, 40, 50),      # dark metal
    'e': (60, 60, 70),      # dark gray
    'S': (80, 80, 90),      # mid metal
    'M': (100, 100, 110),   # metal
    'D': (130, 130, 140),   # bright metal
    'f': (160, 160, 170),   # light metal
    'P': (190, 190, 200),   # pale metal
    
    # Голубая энергия Опуса
    'c': (30, 50, 70),      # dark cyan
    'C': (50, 100, 150),    # cyan
    'b': (80, 150, 200),    # bright cyan
    'B': (120, 200, 255),   # very bright cyan
    'W': (200, 230, 255),   # white-blue core
    
    # Янтарь Хаику
    'H': (100, 60, 20),     # dark amber/brown
    'A': (180, 100, 30),    # amber
    'G': (220, 140, 40),    # golden amber
    'Y': (255, 180, 50),    # bright yellow-amber
    'y': (255, 220, 100),   # light yellow
    
    # Фиолетовый кристалл (Сердце Алтаря)
    'v': (40, 20, 60),      # dark violet
    'V': (80, 40, 120),     # violet
    'p': (120, 60, 160),    # purple
    'P': (160, 80, 200),    # light purple
    'X': (200, 120, 240),   # bright purple
    'z': (240, 180, 255),   # pale purple
    
    # Разрушенный бетон
    'n': (30, 30, 35),      # near black
    'g': (70, 70, 75),      # dark gray concrete
    'k': (100, 100, 105),   # gray concrete
    'K': (130, 130, 135),   # light concrete
    's': (160, 160, 165),   # pale concrete
    
    # Ржавчина и следы войны
    'r': (80, 40, 30),      # rust dark
    'R': (140, 60, 30),     # rust
    'o': (180, 80, 40),     # rust orange
    
    # Прозрачность
    '.': None,
}

def create_texture_from_ascii(ascii_art, filename, size=16):
    """Создает PNG текстуру из ASCII арта"""
    lines = ascii_art.strip().split('\n')
    height = len(lines)
    width = max(len(line) for line in lines)
    
    # Масштабирование
    scale = size // max(width, height)
    if scale < 1:
        scale = 1
    
    img_width = width * scale
    img_height = height * scale
    
    img = Image.new('RGBA', (img_width, img_height), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    for y, line in enumerate(lines):
        for x, char in enumerate(line):
            if char in PALETTE and PALETTE[char] is not None:
                color = PALETTE[char]
                draw.rectangle(
                    [x * scale, y * scale, (x + 1) * scale - 1, (y + 1) * scale - 1],
                    fill=color + (255,)
                )
    
    img.save(filename, 'PNG')
    print(f"Создана текстура: {filename}")

# ==================== БЛОКИ ====================

# Усиленный Опус - основной строительный материал Колизея
REINFORCED_OPUS = """
dddddddddddddddd
ddMMMMMMMMMMMMdd
dMSSSSSSSSSSSMd
dMSbbbbbbbbbbSMd
dMSbCCCCCCCCCbSMd
dMSbCbbbbbbbCbSMd
dMSbCbSSSSSSCbSMd
dMSbCbSMMMMSSCbSMd
dMSbCbSMSMMMSCbSMd
dMSbCbSSSSSSCbSMd
dMSbCbbbbbbbCbSMd
dMSbCCCCCCCCCbSMd
dMSbbbbbbbbbbSMd
dMSSSSSSSSSSSMd
ddMMMMMMMMMMMMdd
dddddddddddddddd
"""

# Янтарь Хаику - полупрозрачный блок с застывшими частицами
HAIKU_AMBER_BLOCK = """
................
....HHHHHHHH....
..HHAyyyyyyyAHH.
.HAyGGGGGGGGGAyH
.HAyGGyyyyyyGGAyH
.HAyGGyAAAyyGGAyH
.HAyGGyAAAAyyGGAyH
.HAyGGyyyyyyGGAyH
.HAyGGGGGGGGGAyH
..HHAyyyyyyyAHH.
....HHHHHHHH....
................
................
................
................
................
"""

# Сердце Алтаря - кристаллический блок с вращающейся структурой
ALTAR_HEART = """
................
......vv........
.....vXXv.......
....vXzzXv......
...vXzVVzXv.....
..vXzVppVVzXv...
.vXzVpXXXXXpzXv.
vXzVpXXXVVXXXpzXv
vXzVpXXXVVXXXpzXv
.vXzVpXXXXXpzXv.
..vXzVppVVzXv...
...vXzVVzXv.....
....vXzzXv......
.....vXXv.......
......vv........
................
"""

# Разрушенный бетон Колизея
COLISSEUM_CONCRETE = """
kkkkkgkgkgkgkgkg
kgkgkkkgkgkgkgkk
kgkgkgkgkkkgkgkg
gkgkgkgkgkgkkkgk
kgkgkggkgkgkgkgk
kgkgkgkgkgkgkgkg
gkgkgkgkgkgkgkgk
kgkgkgkgkgkgkgkg
kgkgkggkgkgkgkgk
gkgkgkgkgkgkkkgk
kgkgkgkgkkkgkgkg
kgkgkkkgkgkgkgkk
kkkkkgkgkgkgkgkg
kgkgkgkgkgkgkgkg
gkgkgkgkgkgkgkgk
kgkgkgkgkgkgkgkg
"""

# Янтарная Опора - декоративный блок для опор алтаря
AMBER_PILLAR = """
HHHHHHHHHHHHHHHH
HAAAAAAAAAAAAAAH
HAyyyyyyyyyyyyAH
HAyGGGGGGGGGGGAH
HAyGGyyyyyyGGAH
HAyGGyAAAAyyGAH
HAyGGyAAAAyyGAH
HAyGGyyyyyyGGAH
HAyGGGGGGGGGGGAH
HAyyyyyyyyyyyyAH
HAAAAAAAAAAAAAAH
HHHHHHHHHHHHHHHH
HHHHHHHHHHHHHHHH
HHHHHHHHHHHHHHHH
HHHHHHHHHHHHHHHH
HHHHHHHHHHHHHHHH
"""

# Стена Колизея с энергетическими прожилками
COLOSSEUM_WALL = """
SSSSSSSSSSSSSSSS
SSbbbbbbbbbbbbSS
SbCCbbbbbbbbCCbS
SbCCbbbbbbbbCCbS
SbCCbbbbbbbbCCbS
SSbbbbbbbbbbbbSS
SSSSSSSSSSSSSSSS
SSbbbbbbbbbbbbSS
SbCCbbbbbbbbCCbS
SbCCbbbbbbbbCCbS
SbCCbbbbbbbbCCbS
SSbbbbbbbbbbbbSS
SSSSSSSSSSSSSSSS
SSbbbbbbbbbbbbSS
SbCCbbbbbbbbCCbS
SbCCbbbbbbbbCCbS
"""

# ==================== ПРЕДМЕТЫ ====================

# Ядро Haiku - сферический артефакт
HAIKU_CORE_ITEM = """
................
......vvvv......
....vvXXXXvv....
...vXXXXXXvv....
..vXXXXVVVXXXv..
.vXXXVVVVVVVXXXv
vXXVVVVVVVVVVVXXv
vXXVVVVVVVVVVVXXv
vXXXVVVVVVVVVXXXv
..vXXXXVVVXXXXv.
...vXXXXXXXXXv..
....vvXXXXXvv...
......vvvvv.....
................
................
................
"""

# Фрагмент Опуса
OPUS_FRAGMENT = """
................
................
...dd...........
..dSSd..........
.dSCCCSd........
dSCCCCCCSd......
dSCCCCCCd.......
.SCCCCC.........
..SCCC..........
...dd...........
................
................
................
................
................
................
"""

# Слеза ИИ - компонент для крафта
AI_TEAR = """
................
................
......bb........
....bbCCbb......
...bCCCCCCCb....
..bCCCCCCCCCb...
.bCCCCCCCCCCCb..
bCCCCCCCCCCCCCb.
bCCCCCCCCCCb....
.bCCCCCCb.......
..bCCCCb........
...bbbb.........
................
................
................
................
"""

# ==================== ЭФФЕКТЫ ====================

# Эффект ослепления (иконка)
FLASH_BLINDNESS_EFFECT = """
................
......WWWW......
....WWffffWW....
...WffffffffW...
..WffffffffffW..
.WffffffffffffW.
WffffffWWffffffW
WfffffWWfffffWW.
.WffffWWffffW...
..WffWWffffW....
...WWWWWWW......
....WWWW........
................
................
................
................
"""

def main():
    base_path = "/workspace/src/main/resources/assets/opusvsexe/textures"
    
    # Создаем текстуры блоков
    blocks = {
        "reinforced_opus_block": REINFORCED_OPUS,
        "haiku_amber_block": HAIKU_AMBER_BLOCK,
        "altar_heart": ALTAR_HEART,
        "colosseum_concrete": COLISSEUM_CONCRETE,
        "amber_pillar_top": AMBER_PILLAR,
        "colosseum_wall": COLOSSEUM_WALL,
    }
    
    for name, ascii_art in blocks.items():
        create_texture_from_ascii(ascii_art, f"{base_path}/block/{name}.png", size=16)
    
    # Создаем текстуры предметов
    items = {
        "haiku_core": HAIKU_CORE_ITEM,
        "opus_fragment": OPUS_FRAGMENT,
        "ai_tear": AI_TEAR,
    }
    
    for name, ascii_art in items.items():
        create_texture_from_ascii(ascii_art, f"{base_path}/item/{name}.png", size=16)
    
    # Создаем текстуру эффекта
    create_texture_from_ascii(FLASH_BLINDNESS_EFFECT, f"{base_path}/mob_effect/flash_blindness.png", size=16)
    
    print("\nВсе текстуры созданы успешно!")

if __name__ == "__main__":
    main()
