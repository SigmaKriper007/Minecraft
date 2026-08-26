#!/usr/bin/env python3
"""Haiku Core — анимированная 3D-модель (янтарное ядро в гирооправе).
Geo + 64x64 атлас + glowmask + idle-анимация. Панель палитры — Haiku (MASTER_PLAN §3)."""
import json, os
from PIL import Image, ImageDraw

BASE = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "opusvsexe")
ASSETS = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "opusvsexe")

# ---------------- палитра Haiku ----------------
W  = (244, 240, 232)   # бело-янтарный светлый металл
F  = (232, 226, 212)   # светлый металл
g  = (200, 192, 176)   # средне-серый металл
S  = (90, 86, 76)      # тёмный металл (швы)
e  = (58, 58, 52)      # железо-угольный уплотнитель
A  = (178, 106, 16)    # янтарь тёмный
P  = (232, 148, 30)    # янтарь основной
fA = (255, 185, 62)    # янтарь яркий
Wc = (255, 245, 200)   # бело-жёлтое ядро (hot core)

def shade(base, k):
    return tuple(max(0, min(255, int(c * k))) for c in base)

def mix(a, b, k):
    return tuple(int(a[i] * (1 - k) + b[i] * k) for i in range(3))

# ---------------- текстуры 16x16 ----------------

def metal_pale():
    im = Image.new("RGBA", (16, 16), F)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=S)
    d.rectangle([1, 1, 14, 14], fill=F)
    for y in (5, 10):
        d.line([1, y, 14, y], fill=shade(F, 0.88))
    d.line([1, 5, 1, 10], fill=shade(F, 0.94))
    d.rectangle([2, 2, 3, 3], fill=W)
    d.rectangle([12, 2, 13, 3], fill=W)
    d.rectangle([2, 12, 3, 13], fill=g)
    d.rectangle([12, 12, 13, 13], fill=g)
    # блик сверху-слева
    for i in range(4):
        d.line([1 + i, 1, 1, 1 + i], fill=W)
    return im

def metal_dark():
    im = Image.new("RGBA", (16, 16), e)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=(30, 30, 26))
    d.line([0, 8, 15, 8], fill=S)
    d.line([8, 0, 8, 15], fill=S)
    for x in range(0, 16, 4):
        for y in range(0, 16, 4):
            d.rectangle([x, y, x + 3, y + 3], fill=shade(e, 1.0 if (x + y) % 8 else 0.92))
    d.line([2, 3, 13, 3], fill=shade(e, 1.18))
    return im

def amber_dark():
    im = Image.new("RGBA", (16, 16), A)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=shade(A, 0.55))
    for i in range(4):
        x0 = min(i * 2, 15 - i * 2 - 1); x1 = 15 - i * 2
        y0 = min(2, 15 - i - 1); y1 = 15 - i
        if x0 < x1 and y0 < y1:
            d.arc([x0, y0, x1, y1], 200, 340, fill=shade(A, 1.12))
    d.arc([0, 0, 15, 15], 130, 160, fill=shade(A, 1.25))
    return im

def amber_mid():
    im = Image.new("RGBA", (16, 16), P)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=shade(P, 0.6))
    d.ellipse([2, 2, 13, 13], fill=P)
    d.ellipse([4, 4, 11, 11], fill=fA)
    d.line([7, 5, 7, 12], fill=fA)
    d.line([4, 8, 11, 8], fill=fA)
    # свет сверху-слева
    d.arc([1, 1, 9, 9], 150, 250, fill=Wc, width=1)
    return im

def amber_bright():
    im = Image.new("RGBA", (16, 16), fA)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=P)
    d.ellipse([3, 3, 12, 12], fill=mix(fA, Wc, 0.55))
    d.ellipse([5, 5, 10, 10], fill=Wc)
    d.point((7, 7), fill=(255, 255, 255))
    d.arc([2, 2, 13, 13], 160, 230, fill=Wc, width=1)
    return im

def white_hot():
    im = Image.new("RGBA", (16, 16), Wc)
    d = ImageDraw.Draw(im)
    d.ellipse([2, 2, 13, 13], fill=(255, 255, 255))
    d.rectangle([0, 0, 15, 15], outline=P)
    d.ellipse([4, 4, 11, 11], outline=mix(Wc, fA, 0.4))
    d.point((7, 7), fill=(255, 255, 255))
    return im

def plate():
    im = Image.new("RGBA", (16, 16), F)
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 15, 15], outline=S)
    d.rectangle([2, 2, 13, 13], outline=shade(F, 0.85), fill=F)
    d.rectangle([5, 5, 10, 10], outline=shade(F, 0.82), fill=shade(F, 0.95))
    d.point((7, 7), fill=P)
    for p in ((2, 2), (13, 2), (2, 13), (13, 13)):
        d.rectangle([p[0], p[1], p[0] + 1, p[1] + 1], fill=S)
    d.line([4, 1, 11, 1], fill=W)
    return im

def gem():
    im = Image.new("RGBA", (16, 16), A)
    d = ImageDraw.Draw(im)
    d.polygon([(8, 1), (14, 8), (8, 15), (2, 8)], fill=mix(A, P, 0.6), outline=shade(A, 0.5))
    d.polygon([(8, 3), (11, 8), (8, 13), (5, 8)], fill=fA)
    d.point((8, 8), fill=Wc)
    d.line([8, 3, 8, 13], fill=mix(fA, Wc, 0.5))
    return im

TEX = {"metal_pale": metal_pale, "metal_dark": metal_dark, "amber_dark": amber_dark,
       "amber_mid": amber_mid, "amber_bright": amber_bright, "white": white_hot,
       "plate": plate, "gem": gem}
REGIONS = {"metal_pale": (0, 0), "metal_dark": (16, 0), "amber_dark": (32, 0), "amber_mid": (48, 0),
           "amber_bright": (0, 16), "white": (16, 16), "plate": (32, 16), "gem": (48, 16)}

atlas = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
for name, fn in TEX.items():
    atlas.paste(fn(), REGIONS[name])
glow = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
for name in ("amber_mid", "amber_bright", "white", "gem"):
    glow.paste(TEX[name](), REGIONS[name])

out = os.path.join(ASSETS, "textures", "item")
atlas.save(os.path.join(out, "haiku_core_3d.png"))
glow.save(os.path.join(out, "haiku_core_3d_glowmask.png"))
print("atlas+glowmask saved")

# ---------------- geo ----------------
geo = {
  "format_version": "1.12.0",
  "minecraft:geometry": [{
    "description": {
      "identifier": "geometry.haiku_core",
      "texture_width": 64, "texture_height": 64,
      "visible_bounds_width": 3, "visible_bounds_height": 3,
      "visible_bounds_offset": [0, 0.9, 0]
    },
    "bones": [
      {"name": "root", "pivot": [0, 12, 0]},
      {"name": "frame", "parent": "root", "pivot": [0, 12, 0], "cubes": [
        {"origin": [-8, 0, -8], "size": [16, 2, 16], "uv": REGIONS["plate"]},
        {"origin": [-8, 22, -8], "size": [16, 2, 16], "uv": REGIONS["plate"]},
        {"origin": [-6, 2, -6], "size": [2, 20, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [4, 2, -6], "size": [2, 20, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [-6, 2, 4], "size": [2, 20, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [4, 2, 4], "size": [2, 20, 2], "uv": REGIONS["metal_dark"]}
      ]},
      {"name": "gyro_vertical", "parent": "root", "pivot": [0, 12, 0], "cubes": [
        {"origin": [-10, 20, -1], "size": [7, 2, 2], "uv": REGIONS["metal_pale"]},
        {"origin": [3, 20, -1], "size": [7, 2, 2], "uv": REGIONS["metal_pale"]},
        {"origin": [-10, 2, -1], "size": [7, 2, 2], "uv": REGIONS["metal_pale"]},
        {"origin": [3, 2, -1], "size": [7, 2, 2], "uv": REGIONS["metal_pale"]},
        {"origin": [-12, 4, -1], "size": [2, 16, 2], "uv": REGIONS["metal_pale"]},
        {"origin": [10, 4, -1], "size": [2, 16, 2], "uv": REGIONS["metal_pale"]}
      ]},
      {"name": "gyro_horizontal", "parent": "root", "pivot": [0, 12, 0], "rotation": [90, 0, 0], "cubes": [
        {"origin": [-10, 20, -1], "size": [7, 2, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [3, 20, -1], "size": [7, 2, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [-10, 2, -1], "size": [7, 2, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [3, 2, -1], "size": [7, 2, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [-12, 4, -1], "size": [2, 16, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [10, 4, -1], "size": [2, 16, 2], "uv": REGIONS["metal_dark"]}
      ]},
      {"name": "core_mount", "parent": "root", "pivot": [0, 12, 0], "cubes": [
        {"origin": [-9, 11, -1], "size": [3, 2, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [6, 11, -1], "size": [3, 2, 2], "uv": REGIONS["metal_dark"]},
        {"origin": [-1, 10, -11], "size": [2, 2, 3], "uv": REGIONS["metal_dark"]},
        {"origin": [-1, 10, 8], "size": [2, 2, 3], "uv": REGIONS["metal_dark"]}
      ]},
      {"name": "memory_core", "parent": "core_mount", "pivot": [0, 12, 0], "rotation": [0, 45, 45], "cubes": [
        {"origin": [-5, 7, -5], "size": [10, 10, 10], "uv": REGIONS["amber_dark"]},
        {"origin": [-3, 5, -3], "size": [6, 14, 6], "uv": REGIONS["amber_mid"]},
        {"origin": [-1.5, 9, -1.5], "size": [3, 6, 3], "uv": REGIONS["amber_bright"]}
      ]},
      {"name": "hotspot", "parent": "memory_core", "pivot": [0, 12, 0], "cubes": [
        {"origin": [-2, 10, -7], "size": [4, 4, 2], "uv": REGIONS["white"]},
        {"origin": [-2, 10, 5], "size": [4, 4, 2], "uv": REGIONS["white"]}
      ]},
      {"name": "tears", "parent": "root", "pivot": [0, 12, 0], "cubes": [
        {"origin": [-13, 6, -2], "size": [2, 3, 2], "uv": REGIONS["gem"]},
        {"origin": [11, 6, -2], "size": [2, 3, 2], "uv": REGIONS["gem"]},
        {"origin": [-13, 15, -2], "size": [2, 3, 2], "uv": REGIONS["gem"]},
        {"origin": [11, 15, -2], "size": [2, 3, 2], "uv": REGIONS["gem"]}
      ]}
    ]
  }]
}
geo_path = os.path.join(ASSETS, "geo", "item", "haiku_core.geo.json")
os.makedirs(os.path.dirname(geo_path), exist_ok=True)
json.dump(geo, open(geo_path, "w"), indent=2)
print("geo saved:", geo_path)

# ---------------- анимация ----------------
anim = {
  "format_version": "1.8.0",
  "animations": {
    "idle": {
      "loop": True,
      "animation_length": 5.0,
      "bones": {
        "root": {
          "position": {"0.0": [0, 0, 0], "1.2": [0, 0.9, 0], "2.6": [0, 0, 0], "3.8": [0, -0.7, 0], "5.0": [0, 0, 0]},
          "rotation": {"0.0": [0, 0, 0], "2.5": [0, 0, -2], "5.0": [0, 0, 0]}
        },
        "gyro_vertical": {"rotation": {"0.0": [0, 0, 0], "5.0": [0, 360, 0]}},
        "gyro_horizontal": {"rotation": {"0.0": [0, 0, 0], "5.0": [0, -360, 0]}},
        "memory_core": {
          "scale": {"0.0": [0.94, 0.94, 0.94], "1.25": [1.06, 1.06, 1.06], "2.5": [0.96, 0.96, 0.96], "3.75": [1.03, 1.03, 1.03], "5.0": [0.94, 0.94, 0.94]}
        },
        "hotspot": {
          "scale": {"0.0": [0.55, 0.55, 0.55], "0.6": [1.25, 1.25, 1.25], "1.2": [0.65, 0.65, 0.65], "1.85": [1.15, 1.15, 1.15], "2.5": [0.55, 0.55, 0.55], "3.1": [1.25, 1.25, 1.25], "3.7": [0.65, 0.65, 0.65], "4.35": [1.15, 1.15, 1.15], "5.0": [0.55, 0.55, 0.55]}
        },
        "tears": {"rotation": {"0.0": [0, 0, 0], "5.0": [0, 360, 0]}}
      }
    }
  }
}
anim_path = os.path.join(ASSETS, "animations", "item", "haiku_core.animation.json")
os.makedirs(os.path.dirname(anim_path), exist_ok=True)
json.dump(anim, open(anim_path, "w"), indent=2)
print("animation saved:", anim_path)