#!/usr/bin/env python3
"""Generate the reworked Angel Boy seraph package: model, animations, textures, abilities.

Design contract (concept: six-winged seraph, auburn hair, gold laurel + bands, ruby
pendant, white chiton, tilted gold halo, BLUE eyes):
  * boss geo      -> geo/paradise/angel_boy.geo.json
  * boss clips    -> animations/paradise/angel_boy.animation.json (14 clips)
  * ability geo   -> geo/paradise/{halo_lance,seraphic_crosswind,seraphic_feather,
                     wingbeat_ring,angel_ascension,ruby_descent}.geo.json
  * ability clips -> animations/paradise/angel_attacks.animation.json (one clip per kind)
  * textures      -> textures/paradise/entity/*.png (+ angel_boy_emissive.png glow mask)
"""

from __future__ import annotations

import json
import math
import random
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent
A = ROOT / "src/main/resources/assets/opusvsexe"

# ---------------------------------------------------------------- material sheet
TILE = 60
TILES = {
    "skin": (2, 2), "skin_chest": (66, 2), "skin_face": (130, 2), "hair": (194, 2),
    "hair_shade": (2, 66), "cloth": (66, 66), "cloth_shade": (130, 66), "gold": (194, 66),
    "gold_bright": (2, 130), "feather_blue": (66, 130), "feather_brown": (130, 130), "feather_white": (194, 130),
    "feather_dark": (2, 194), "gem_ruby": (66, 194), "eye_blue": (130, 194), "filigree": (194, 194),
}
EMISSIVE_TILES = ("gold_bright", "gem_ruby", "eye_blue")


def _noise(draw, x: int, y: int, rng: random.Random, base, spread: int) -> None:
    value = tuple(max(0, min(255, channel + rng.randint(-spread, spread))) for channel in base)
    draw.rectangle((x, y, x, y), fill=value)


def _barbs(img: Image.Image, base, dark, edge, step: int = 5) -> Image.Image:
    """Diagonal feather barbs with a lit leading edge."""
    draw = ImageDraw.Draw(img)
    w, h = img.size
    for y in range(h):
        for x in range(w):
            _noise(draw, x, y, random.Random((x * 31 + y * 17) ^ 0x5EED), base, 6)
    for offset in range(-h, w + h, step):
        draw.line((offset, h, offset + h, 0), fill=(*dark, 255), width=1)
    for x in range(w):
        draw.point((x, 0), fill=(*edge, 255))
        draw.point((x, 1), fill=(*edge, 255))
    return img


def _cloth_tile(base, fold, stitch) -> Image.Image:
    img = Image.new("RGBA", (TILE, TILE), (*base, 255))
    draw = ImageDraw.Draw(img)
    for x in range(0, TILE, 7):
        draw.line((x, 0, x, TILE), fill=(*fold, 255), width=2)
    for y in range(3, TILE, 11):
        for x in range((y * 3) % 9, TILE, 9):
            draw.point((x, y), fill=(*stitch, 255))
    rng = random.Random(7)
    for y in range(TILE):
        for x in range(TILE):
            if rng.random() < .12:
                _noise(draw, x, y, rng, base, 5)
    return img


def _gold_tile(base, light, dark, bright_center=False) -> Image.Image:
    img = Image.new("RGBA", (TILE, TILE), (*base, 255))
    draw = ImageDraw.Draw(img)
    draw.rectangle((0, 0, TILE - 1, 2), fill=(*light, 255))
    draw.rectangle((0, TILE - 3, TILE - 1, TILE - 1), fill=(*dark, 255))
    for x in range(0, TILE, 15):
        draw.line((x, 0, x, TILE), fill=(*dark, 255), width=1)
        draw.line((x + 1, 3, x + 1, TILE - 3), fill=(*light, 255), width=1)
    if bright_center:
        cx = cy = TILE // 2
        for radius, shade in ((26, light), (16, (255, 244, 200)), (8, (255, 252, 232))):
            draw.ellipse((cx - radius, cy - radius, cx + radius, cy + radius), fill=(*shade, 255))
    rng = random.Random(11)
    for y in range(TILE):
        for x in range(TILE):
            if rng.random() < .10:
                _noise(draw, x, y, rng, base, 8)
    return img


def _skin_tile(base, spread: int, seed: int) -> Image.Image:
    img = Image.new("RGBA", (TILE, TILE), (*base, 255))
    draw = ImageDraw.Draw(img)
    rng = random.Random(seed)
    for y in range(TILE):
        for x in range(TILE):
            if rng.random() < .55:
                _noise(draw, x, y, rng, base, spread)
    return img


def paint_skin_chest() -> Image.Image:
    img = _skin_tile((226, 176, 140), 7, 3)
    draw = ImageDraw.Draw(img)
    muscle = (198, 144, 108)
    draw.arc((6, 12, 28, 30), 20, 160, fill=(*muscle, 255), width=2)
    draw.arc((32, 12, 54, 30), 20, 160, fill=(*muscle, 255), width=2)
    draw.line((30, 22, 30, 44), fill=(*muscle, 255), width=2)
    for y in (34, 42):
        draw.line((20, y, 40, y), fill=(*muscle, 255), width=1)
    chain = (222, 186, 96)
    draw.line((8, 4, 26, 20), fill=(*chain, 255), width=2)
    draw.line((52, 4, 34, 20), fill=(*chain, 255), width=2)
    draw.ellipse((25, 18, 35, 28), outline=(*chain, 255), width=2)
    draw.rectangle((27, 20, 33, 26), fill=(198, 36, 52, 255))
    return img


def paint_skin_face() -> Image.Image:
    img = _skin_tile((226, 176, 140), 6, 5)
    draw = ImageDraw.Draw(img)
    hair = (146, 66, 30)
    draw.rectangle((0, 0, TILE - 1, 7), fill=(*hair, 255))
    for x in range(2, TILE, 9):
        draw.line((x, 7, x + 3, 12), fill=(*hair, 255), width=2)
    draw.line((10, 22, 24, 22), fill=(*hair, 255), width=2)
    draw.line((36, 22, 50, 22), fill=(*hair, 255), width=2)
    draw.line((27, 40, 33, 40), fill=(184, 120, 92, 255), width=2)
    blush = (214, 140, 110)
    draw.point((8, 30), fill=(*blush, 255))
    draw.point((9, 31), fill=(*blush, 255))
    draw.point((51, 30), fill=(*blush, 255))
    draw.point((50, 31), fill=(*blush, 255))
    return img


def paint_hair(base: tuple, seed: int) -> Image.Image:
    img = Image.new("RGBA", (TILE, TILE), (*base, 255))
    draw = ImageDraw.Draw(img)
    rng = random.Random(seed)
    dark = tuple(int(channel * .74) for channel in base)
    light = tuple(min(255, int(channel * 1.24)) for channel in base)
    for x in range(0, TILE, 4):
        offset = rng.randint(-2, 2)
        draw.line((x, 0, x + offset, TILE), fill=(*dark, 255), width=1)
        if x % 8 == 0:
            draw.line((x + 1, 0, x + 1 + offset, TILE), fill=(*light, 255), width=1)
    for _ in range(40):
        _noise(draw, rng.randrange(TILE), rng.randrange(TILE), rng, base, 9)
    return img


def paint_gem() -> Image.Image:
    img = Image.new("RGBA", (TILE, TILE), (148, 20, 34, 255))
    draw = ImageDraw.Draw(img)
    draw.polygon(((30, 2), (56, 30), (30, 58), (4, 30)), fill=(198, 36, 52, 255))
    draw.polygon(((30, 2), (56, 30), (30, 30)), fill=(238, 96, 108, 255))
    draw.polygon(((30, 30), (56, 30), (30, 58)), fill=(168, 26, 42, 255))
    draw.polygon(((30, 2), (4, 30), (30, 30)), fill=(222, 66, 80, 255))
    draw.rectangle((26, 12, 29, 15), fill=(255, 214, 220, 255))
    draw.rectangle((34, 34, 36, 36), fill=(255, 180, 190, 255))
    return img


def paint_eye() -> Image.Image:
    img = Image.new("RGBA", (TILE, TILE), (60, 110, 190, 255))
    draw = ImageDraw.Draw(img)
    draw.ellipse((4, 10, 56, 50), fill=(110, 180, 255, 255))
    draw.ellipse((16, 18, 44, 42), fill=(170, 220, 255, 255))
    draw.ellipse((24, 24, 36, 36), fill=(235, 248, 255, 255))
    draw.rectangle((26, 12, 29, 15), fill=(255, 255, 255, 255))
    return img


def paint_filigree() -> Image.Image:
    img = _gold_tile((206, 164, 74), (240, 206, 120), (150, 112, 44))
    draw = ImageDraw.Draw(img)
    for y in range(6, TILE, 12):
        draw.arc((8, y - 5, 28, y + 5), 0, 180, fill=(150, 112, 44, 255), width=1)
        draw.arc((32, y - 5, 52, y + 5), 180, 360, fill=(150, 112, 44, 255), width=1)
    return img


def make_material_sheet() -> Image.Image:
    atlas = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    painters = {
        "skin": lambda: _skin_tile((226, 176, 140), 7, 3),
        "skin_chest": paint_skin_chest,
        "skin_face": paint_skin_face,
        "hair": lambda: paint_hair((158, 74, 34), 21),
        "hair_shade": lambda: paint_hair((126, 56, 26), 22),
        "cloth": lambda: _cloth_tile((240, 236, 228), (216, 210, 198), (188, 180, 166)),
        "cloth_shade": lambda: _cloth_tile((214, 208, 196), (188, 180, 166), (160, 152, 138)),
        "gold": lambda: _gold_tile((206, 164, 74), (240, 206, 120), (150, 112, 44)),
        "gold_bright": lambda: _gold_tile((232, 190, 92), (255, 228, 140), (176, 132, 54), True),
        "feather_blue": lambda: _barbs(Image.new("RGBA", (TILE, TILE)), (110, 132, 156), (88, 108, 132), (150, 172, 196)),
        "feather_brown": lambda: _barbs(Image.new("RGBA", (TILE, TILE)), (156, 106, 54), (126, 82, 40), (192, 146, 90)),
        "feather_white": lambda: _barbs(Image.new("RGBA", (TILE, TILE)), (238, 236, 230), (212, 208, 198), (250, 249, 245)),
        "feather_dark": lambda: _barbs(Image.new("RGBA", (TILE, TILE)), (104, 68, 38), (84, 54, 30), (134, 92, 52)),
        "gem_ruby": paint_gem,
        "eye_blue": paint_eye,
        "filigree": paint_filigree,
    }
    for name, (x, y) in TILES.items():
        atlas.paste(painters[name](), (x, y))
    return atlas


# ---------------------------------------------------------------- geometry helpers
def write(path: Path, value: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2) + "\n", encoding="utf-8")


def uv(material: str) -> dict:
    x, y = TILES[material]
    face = {"uv": [x, y], "uv_size": [TILE, TILE]}
    return {side: dict(face) for side in ("north", "east", "south", "west", "up", "down")}


def uv_faces(front: str, others: str) -> dict:
    result = uv(others)
    x, y = TILES[front]
    result["north"] = {"uv": [x, y], "uv_size": [TILE, TILE]}
    return result


def cube(origin, size, material="skin", inflate=None, front=None) -> dict:
    value = {"origin": origin, "size": size, "uv": uv_faces(front, material) if front else uv(material)}
    if inflate is not None:
        value["inflate"] = inflate
    return value


def bone(name, pivot, parent=None, cubes=None, rotation=None) -> dict:
    value = {"name": name, "pivot": pivot}
    if parent is not None:
        value["parent"] = parent
    if cubes:
        value["cubes"] = cubes
    if rotation is not None:
        value["rotation"] = rotation
    return value


def geo(identifier: str, bones: list[dict], texture_width=256, texture_height=256,
        bounds=(9, 5.2, 1.6)) -> dict:
    return {"format_version": "1.12.0", "minecraft:geometry": [{
        "description": {"identifier": identifier, "texture_width": texture_width,
                        "texture_height": texture_height,
                        "visible_bounds_width": bounds[0], "visible_bounds_height": bounds[1],
                        "visible_bounds_offset": [0, bounds[2], 0]},
        "bones": bones,
    }]}


def mx(x: float, width: float, sign: int) -> float:
    return x if sign > 0 else -x - width


def mirror_rot(rotation):
    return [-rotation[0], -rotation[1], -rotation[2]]


# ---------------------------------------------------------------- boss geometry
def wing_bones(side: str, sign: int, tier: str, pivot, base_rot, lengths, covert_mat, arm_mat, tip_mat) -> list[dict]:
    """One seraph wing: covert root -> arm with secondary row -> three fan feathers."""
    ox = lambda x, w: mx(x, w, sign)
    ry = lambda a: -sign * a
    rz = lambda a: -sign * a
    root_rot = [base_rot[0], ry(base_rot[1]), rz(base_rot[2])]
    arm_len, fan1_len, fan2_len, fan3_len = lengths
    arm_pivot = [sign * (pivot[0] + 4.5), pivot[1], pivot[2] + .5]
    fan1_pivot = [sign * (pivot[0] + 4.5 + arm_len), pivot[1], pivot[2] + 1]
    fan2_pivot = [sign * (pivot[0] + 4.5 + arm_len + fan1_len), pivot[1], pivot[2] + 1.6]
    fan3_pivot = [sign * (pivot[0] + 4.5 + arm_len + fan1_len + fan2_len), pivot[1], pivot[2] + 2.2]
    return [
        bone(f"wing_{tier}_{side}_root", [sign * pivot[0], pivot[1], pivot[2]], "body",
             [cube([ox(pivot[0] - 1, 6), pivot[1] - 1.6, pivot[2] - 1.6], [6, 3.6, 4.6], covert_mat),
              cube([ox(pivot[0] + 4, 4.5), pivot[1] - 1, pivot[2] - 1.1], [4.5, 2.8, 3.8], covert_mat)],
             root_rot),
        bone(f"wing_{tier}_{side}_arm", arm_pivot, f"wing_{tier}_{side}_root",
             [cube([ox(pivot[0] + 4.5, arm_len), pivot[1] - 1.5, pivot[2] - .4], [arm_len, 3, 4.8], arm_mat),
              cube([ox(pivot[0] + 5.5, arm_len - 2), pivot[1] - 3.6, pivot[2] + 1.6], [arm_len - 2, 2.2, 3.2], arm_mat)],
             [0, ry(12), rz(-4)]),
        bone(f"wing_{tier}_{side}_fan1", fan1_pivot, f"wing_{tier}_{side}_arm",
             [cube([ox(pivot[0] + 4.5 + arm_len, fan1_len), pivot[1] - 1.2, pivot[2] + .3], [fan1_len, 2.8, 3.4], arm_mat),
              cube([ox(pivot[0] + 5.5 + arm_len, fan1_len - 2), pivot[1] - 3.4, pivot[2] + 1.9], [fan1_len - 2, 2, 2.6], arm_mat)],
             [3, ry(9), rz(-3)]),
        bone(f"wing_{tier}_{side}_fan2", fan2_pivot, f"wing_{tier}_{side}_fan1",
             [cube([ox(pivot[0] + 4.5 + arm_len + fan1_len, fan2_len), pivot[1] - 1, pivot[2] + .9], [fan2_len, 2.5, 3], arm_mat),
              cube([ox(pivot[0] + 5.5 + arm_len + fan1_len, fan2_len - 2), pivot[1] - 3, pivot[2] + 2.4], [fan2_len - 2, 1.8, 2.4], arm_mat)],
             [5, ry(11), rz(-2)]),
        bone(f"wing_{tier}_{side}_fan3", fan3_pivot, f"wing_{tier}_{side}_fan2",
             [cube([ox(pivot[0] + 4.5 + arm_len + fan1_len + fan2_len, fan3_len), pivot[1] - .9, pivot[2] + 1.5], [fan3_len, 2.3, 2.6], tip_mat),
              cube([ox(pivot[0] + 5.5 + arm_len + fan1_len + fan2_len, fan3_len - 2), pivot[1] - 2.7, pivot[2] + 2.9], [fan3_len - 2, 1.6, 2.2], tip_mat)],
             [7, ry(13), rz(-1)]),
    ]


def boss_geometry() -> list[dict]:
    bones = [
        bone("root", [0, 0, 0]),
        bone("body", [0, 12, 0], "root", [
            cube([-4, 12, -2], [8, 12, 4], "skin", front="skin_chest"),
            cube([-2, 23.2, -1.4], [4, 2, 3.2], "skin", inflate=.1),
            cube([-4.5, 11, -2.5], [9, 2.2, 5], "gold", inflate=.06),
        ]),
        bone("chest_gem", [0, 19.5, -2.5], "body",
             [cube([-1, 18.5, -3.1], [2, 2, 1], "gem_ruby", inflate=.12)]),
        bone("head", [0, 24, 0], "body", [
            cube([-4, 24, -4], [8, 8, 8], "skin", front="skin_face"),
            cube([-4.4, 31.2, -4.4], [8.8, 2.8, 8.8], "hair"),
            cube([-4.4, 25.4, 3], [8.8, 6.6, 1.6], "hair_shade"),
            cube([-4.4, 29.4, -4.7], [8.8, 2.6, 1.5], "hair"),
            cube([-4.4, 26.6, -4.6], [1.4, 3.4, 4.6], "hair_shade"),
            cube([3, 26.6, -4.6], [1.4, 3.4, 4.6], "hair_shade"),
            cube([-3.4, 30.4, -3.6], [2.4, 1, 1.4], "gold"),
            cube([1, 30.4, -3.6], [2.4, 1, 1.4], "gold"),
        ]),
        bone("eye_left", [2.2, 27.6, -4], "head", [cube([1.2, 27, -4.35], [2.2, 1.8, .6], "eye_blue")]),
        bone("eye_right", [-2.2, 27.6, -4], "head", [cube([-3.4, 27, -4.35], [2.2, 1.8, .6], "eye_blue")]),
    ]

    bones.append(bone("halo", [0, 33.6, .4], "head", rotation=[0, 0, 14]))
    for i in range(8):
        bones.append(bone(f"halo_seg_{i + 1}", [0, 33.6, .4], "halo",
                          [cube([-1.5, 33.05, -7.2], [3, 1.1, 1.6], "gold_bright")], [0, i * 45, 0]))

    for side, sign in (("left", 1), ("right", -1)):
        ox = lambda x, w: mx(x, w, sign)
        bones += [
            bone(f"arm_{side}_upper", [sign * 4, 22, 0], "body",
                 [cube([ox(4, 3.8), 14, -1.9], [3.8, 8, 3.8], "skin"),
                  cube([ox(3.8, 4.2), 19.4, -2.1], [4.2, 2, 4.2], "gold", inflate=.08)]),
            bone(f"arm_{side}_fore", [sign * 4, 15, 0], f"arm_{side}_upper",
                 [cube([ox(4.1, 3.4), 9, -1.7], [3.4, 6, 3.4], "skin"),
                  cube([ox(3.9, 3.8), 13.2, -1.9], [3.8, 1.8, 3.8], "gold", inflate=.08)]),
            bone(f"arm_{side}_hand", [sign * 4, 9, 0], f"arm_{side}_fore",
                 [cube([ox(4.1, 3.4), 6, -1.7], [3.4, 3, 3.4], "skin")]),
        ]

    bones += [
        bone("drapery", [0, 12, 0], "body", [
            cube([-4, 4.5, -2.3], [8, 7.6, 1.7], "cloth"),
            cube([-4, 4.5, .6], [8, 7.6, 1.7], "cloth_shade"),
            cube([-4.4, 3.4, -2.6], [8.8, 1.5, 5.2], "filigree"),
        ]),
        bone("fold_left", [3.6, 12, 0], "drapery", [cube([3.2, 4.8, -1.9], [1.9, 7.4, 3.8], "cloth")]),
        bone("fold_right", [-3.6, 12, 0], "drapery", [cube([-5.1, 4.8, -1.9], [1.9, 7.4, 3.8], "cloth")]),
    ]

    for side, sign in (("left", 1), ("right", -1)):
        ox = lambda x, w: mx(x, w, sign)
        bones += [
            bone(f"leg_{side}_upper", [sign * 2, 12, 0], "root",
                 [cube([ox(0, 4), 6, -2], [4, 6, 4], "cloth_shade")]),
            bone(f"leg_{side}_lower", [sign * 2, 6, 0], f"leg_{side}_upper",
                 [cube([ox(.3, 3.4), 1.4, -1.7], [3.4, 4.6, 3.4], "skin"),
                  cube([ox(0, 4), .2, -2.5], [4, 1.3, 4.9], "cloth_shade"),
                  cube([ox(0, 4), 1.4, -1.4], [4, .8, 2.6], "gold")]),
        ]

    wings = [
        ("upper", 21, 2.0, [-10, 16, -14], (9, 12, 12, 11), "feather_blue", "feather_brown", "feather_white"),
        ("mid", 17.4, 2.2, [-4, 22, -22], (8, 10, 10, 9), "feather_blue", "feather_brown", "feather_white"),
        ("low", 14.2, 2.4, [2, 26, -28], (7, 9, 9, 8), "feather_dark", "feather_dark", "feather_white"),
    ]
    for side, sign in (("left", 1), ("right", -1)):
        for tier, y, z, base_rot, lengths, covert, arm_mat, tip in wings:
            bones += wing_bones(side, sign, tier, (2.5, y, z), base_rot, lengths, covert, arm_mat, tip)
    return bones


# ---------------------------------------------------------------- animation helpers
def ch(kind: str, keys: dict) -> dict:
    return {kind: {str(t): v for t, v in sorted(keys.items())}}


def rot(keys: dict) -> dict:
    return ch("rotation", keys)


def pos(keys: dict) -> dict:
    return ch("position", keys)


def scl(keys: dict) -> dict:
    return ch("scale", keys)


def channels(*values: dict) -> dict:
    out: dict = {}
    for value in values:
        out.update(value)
    return out


def anim(length: float, bones: dict, loop: bool = False) -> dict:
    return {"loop": loop, "animation_length": length, "bones": bones}


def mirrored(bones: dict, template: dict) -> dict:
    """Duplicate every '*_left' channel as a mirrored '*_right' channel (X mirror plane)."""
    for key, channel in template.items():
        if not key.endswith("_left"):
            continue
        other = key[:-4] + "right"
        bones[other] = {
            kind: {t: ([-v[0], -v[1], -v[2]] if kind == "rotation" else ([-v[0], v[1], v[2]] if kind == "position" else list(v)))
                   for t, v in frames.items()}
            for kind, frames in channel.items()
        }
    return bones


def wing_set(bones: dict, root_z: dict, root_x: dict | None = None, arm_y: dict | None = None,
             arm_z: dict | None = None, fan1_z: dict | None = None) -> dict:
    """Apply one wing pose timeline to all six wings (left written, right mirrored)."""
    tiers = ("upper", "mid", "low")

    def pick(source, t, index):
        if source is None or t not in source:
            return 0.0
        value = source[t]
        return value[index] if isinstance(value, (list, tuple)) else value

    for index, tier in enumerate(tiers):
        keys = {t: [pick(root_x, t, index), 0, pick(root_z, t, index)] for t in root_z}
        mirrored(bones, {f"wing_{tier}_left_root": rot(keys)})
        arm_times = arm_z or arm_y
        if arm_times:
            arm_keys = {t: [0, pick(arm_y, t, index), pick(arm_z, t, index)] for t in arm_times}
            mirrored(bones, {f"wing_{tier}_left_arm": rot(arm_keys)})
        if fan1_z:
            mirrored(bones, {f"wing_{tier}_left_fan1": rot({t: [0, 0, pick(fan1_z, t, index)] for t in fan1_z})})
    return bones


CLOSED_Z = (58, 62, 66)
OPEN_Z = (20, 26, 32)
REST_ARM_Z = (10, 12, 14)


def arms_mirror(bones: dict, upper: dict, fore: dict | None = None, hand: dict | None = None) -> dict:
    mirrored(bones, {f"arm_left_upper": rot(upper)})
    if fore:
        mirrored(bones, {f"arm_left_fore": rot(fore)})
    if hand:
        mirrored(bones, {f"arm_left_hand": rot(hand)})
    return bones


def legs_mirror(bones: dict, upper: dict, lower: dict) -> dict:
    mirrored(bones, {f"leg_left_upper": rot(upper), f"leg_left_lower": rot(lower)})
    return bones


# ---------------------------------------------------------------- boss animations
def boss_animations() -> dict:
    clips: dict[str, dict] = {}

    # Dormant: kneeling prayer, wings folded tight, halo sway.
    dormant = {
        "body": channels(pos({0: [0, -.4, 0], 2: [0, -.2, 0], 4: [0, -.4, 0]}), rot({0: [4, 0, 0]})),
        "head": rot({0: [24, 0, 0], 1.6: [24, -16, 4], 3.1: [24, 14, -4], 4: [24, 0, 0]}),
        "halo": rot({0: [0, 0, 14], 2: [0, 10, 18], 4: [0, 0, 14]}),
        "drapery": rot({0: [2, 0, 0]}),
    }
    arms_mirror(dormant, {0: [-24, 0, 14]}, {0: [-78, 18, 0]}, {0: [-16, 0, 0]})
    legs_mirror(dormant, {0: [-42, 0, 6]}, {0: [78, 0, -4]})
    wing_set(dormant, root_z={0: CLOSED_Z, 2: [60, 64, 68], 4: CLOSED_Z}, arm_z={0: REST_ARM_Z})
    clips["dormant"] = anim(4, dormant, True)

    # Idle ground: weight shift, curious head, breathing folded wings.
    idle = {
        "body": channels(pos({0: [0, 0, 0], 1.6: [0, .3, 0], 3.2: [0, 0, 0]}),
                         rot({0: [0, 0, 1.2], 1.6: [0, 0, -1.2], 3.2: [0, 0, 1.2]})),
        "head": rot({0: [0, -10, 2], 1.1: [4, 12, -2], 2.2: [-2, -14, 3], 3.2: [0, -10, 2]}),
        "halo": channels(pos({0: [0, 0, 0], 1.6: [0, .5, 0], 3.2: [0, 0, 0]}),
                         rot({0: [0, 6, 14], 1.6: [0, -6, 15], 3.2: [0, 6, 14]})),
        "drapery": rot({0: [0, 0, 1.5], 1.6: [0, 0, -1.5], 3.2: [0, 0, 1.5]}),
    }
    arms_mirror(idle, {0: [4, 0, 7], 1.6: [2, 0, 9], 3.2: [4, 0, 7]}, {0: [-7, 0, 4]}, {0: [0, 0, 0]})
    legs_mirror(idle, {0: [2, 0, 3], 1.6: [-2, 0, 3], 3.2: [2, 0, 3]}, {0: [4, 0, 0]})
    mirrored(idle, {"fold_left": rot({0: [0, 0, 2], 1.6: [0, 0, 5], 3.2: [0, 0, 2]})})
    wing_set(idle, root_z={0: [60, 64, 68], 1.6: CLOSED_Z, 3.2: [60, 64, 68]},
             arm_z={0: REST_ARM_Z, 1.6: [12, 14, 16], 3.2: REST_ARM_Z})
    clips["idle_ground"] = anim(3.2, idle, True)

    # Idle flight: hovering with deep slow flaps, knees tucked.
    flap_times = (0, .6, 1.2, 1.8, 2.4)
    flap_z = {0: OPEN_Z, .6: [68, 72, 76], 1.2: OPEN_Z, 1.8: [68, 72, 76], 2.4: OPEN_Z}
    flight = {
        "body": channels(pos({t: [0, .6 if i % 2 == 0 else 1.3, 0] for i, t in enumerate(flap_times)}),
                         rot({0: [6, 0, 0]})),
        "head": rot({0: [-6, 0, 0]}),
        "halo": channels(pos({t: [0, .4 if i % 2 == 0 else 1, 0] for i, t in enumerate(flap_times)}),
                         rot({0: [0, 0, 14], 1.2: [0, 14, 12], 2.4: [0, 0, 14]})),
        "drapery": rot({t: [-4 if i % 2 == 0 else -9, 0, 0] for i, t in enumerate(flap_times)}),
    }
    arms_mirror(flight, {t: [-10 if i % 2 == 0 else -16, 0, 16 if i % 2 == 0 else 22] for i, t in enumerate(flap_times)},
                {0: [-12, 0, 6]}, {0: [-6, 0, 0]})
    legs_mirror(flight, {0: [-16, 0, 5]}, {0: [30, 0, 0]})
    mirrored(flight, {"fold_left": rot({t: [0, 0, 6 if i % 2 == 0 else 14] for i, t in enumerate(flap_times)})})
    wing_set(flight, root_z=flap_z,
             arm_z={t: [8, 10, 12] if i % 2 == 0 else [22, 24, 26] for i, t in enumerate(flap_times)},
             fan1_z={t: [-4, -4, -4] if i % 2 == 0 else [10, 10, 10] for i, t in enumerate(flap_times)})
    clips["idle_flight"] = anim(2.4, flight, True)

    # Walk: brisk steps, wing counterbalance.
    walk = {
        "body": channels(pos({0: [0, 0, 0], .28: [0, .5, 0], .55: [0, 0, 0], .83: [0, .5, 0], 1.1: [0, 0, 0]}),
                         rot({0: [0, 3, 1], .55: [0, -3, -1], 1.1: [0, 3, 1]})),
        "head": rot({0: [-2, -3, 0], .55: [-2, 3, 0], 1.1: [-2, -3, 0]}),
        "halo": pos({0: [0, 0, 0], .28: [0, .4, 0], .55: [0, 0, 0], .83: [0, .4, 0], 1.1: [0, 0, 0]}),
        "drapery": rot({0: [3, -4, 1], .55: [3, 4, -1], 1.1: [3, -4, 1]}),
    }
    arms_mirror(walk, {0: [26, 0, 6], .55: [-24, 0, 6], 1.1: [26, 0, 6]}, {0: [-10, 0, 4]})
    mirrored(walk, {"arm_left_upper": rot({0: [26, 0, 6], .55: [-24, 0, 6], 1.1: [26, 0, 6]})})
    walk["arm_right_upper"] = rot({0: [-24, 0, -6], .55: [26, 0, -6], 1.1: [-24, 0, -6]})
    walk["arm_right_fore"] = rot({0: [-10, 0, -4]})
    walk["arm_left_fore"] = rot({0: [-10, 0, 4]})
    legs_mirror(walk, {0: [-30, 0, 2], .55: [26, 0, 2], 1.1: [-30, 0, 2]},
                {0: [10, 0, 0], .28: [42, 0, 0], .55: [6, 0, 0], 1.1: [10, 0, 0]})
    walk["leg_right_upper"] = rot({0: [26, 0, -2], .55: [-30, 0, -2], 1.1: [26, 0, -2]})
    walk["leg_right_lower"] = rot({0: [6, 0, 0], .28: [10, 0, 0], .55: [42, 0, 0], 1.1: [6, 0, 0]})
    wing_set(walk, root_z={0: CLOSED_Z, .28: [56, 60, 64], .55: CLOSED_Z, .83: [56, 60, 64], 1.1: CLOSED_Z},
             arm_z={0: REST_ARM_Z})
    clips["walk"] = anim(1.1, walk, True)

    # Awaken rebuff: wings BURST open, arms flung wide, halo flare spin.
    rebuff = {
        "body": channels(pos({0: [0, -.4, 0], .35: [0, .8, 0], 1.2: [0, .6, 0], 2: [0, 0, 0]}),
                         rot({0: [6, 0, 0], .35: [-8, 0, 0], 1.2: [-4, 0, 0], 2: [0, 0, 0]})),
        "head": rot({0: [24, 0, 0], .35: [-16, 0, 0], 1.2: [-8, 0, 0], 2: [0, 0, 0]}),
        "halo": channels(pos({0: [0, 0, 0], .35: [0, 1.4, 0], 2: [0, 0, 0]}),
                         rot({0: [0, 0, 14], .35: [0, 180, 14], .9: [0, 540, 14], 2: [0, 720, 14]})),
        "drapery": channels(rot({0: [2, 0, 0], .35: [-6, 0, 0], 2: [0, 0, 0]}),
                            pos({0: [0, 0, 0], .35: [0, .4, 0], 2: [0, 0, 0]})),
    }
    arms_mirror(rebuff, {0: [-24, 0, 14], .35: [-34, 0, 46], 1.2: [-20, 0, 34], 2: [0, 0, 8]},
                {0: [-78, 18, 0], .35: [-14, 0, 0], 2: [-7, 0, 4]},
                {0: [-16, 0, 0], .35: [-8, 0, 0], 2: [0, 0, 0]})
    legs_mirror(rebuff, {0: [-42, 0, 6], .35: [-8, 0, 4], 2: [0, 0, 2]},
                {0: [78, 0, -4], .35: [10, 0, 0], 2: [4, 0, 0]})
    wing_set(rebuff, root_z={0: CLOSED_Z, .35: [6, 12, 18], 1.2: OPEN_Z, 2: [24, 30, 36]},
             root_x={0: [-4, -4, 2], .35: [-18, -12, -6], 1.2: [-10, -4, 2], 2: [-10, -4, 2]},
             arm_z={0: REST_ARM_Z, .35: [4, 6, 8], 1.2: [8, 10, 12], 2: REST_ARM_Z},
             fan1_z={0: [0, 0, 0], .35: [-8, -8, -8], 1.2: [-4, -4, -4], 2: [0, 0, 0]})
    clips["awaken_rebuff"] = anim(2, rebuff)

    # Phase shift: wings wrap the body, then burst with halo whirl.
    phase = {
        "body": channels(pos({0: [0, 0, 0], .7: [0, -1.4, 0], 1.05: [0, .8, 0], 2.2: [0, 0, 0]}),
                         rot({0: [0, 0, 0], .7: [12, 0, 0], 1.05: [-10, 0, 0], 2.2: [0, 0, 0]})),
        "head": rot({0: [0, 0, 0], .7: [20, 0, 0], 1.05: [-14, 0, 0], 2.2: [0, 0, 0]}),
        "halo": channels(pos({0: [0, 0, 0], .7: [0, -.8, 0], 1.05: [0, 1.6, 0], 2.2: [0, 0, 0]}),
                         rot({0: [0, 0, 14], .7: [0, -120, 26], 1.05: [0, 480, 14], 2.2: [0, 720, 14]})),
    }
    arms_mirror(phase, {0: [0, 0, 8], .7: [-30, 40, 34], 1.05: [-30, 0, 44], 2.2: [0, 0, 8]},
                {0: [-7, 0, 4], .7: [-64, 0, 0], 1.05: [-10, 0, 0], 2.2: [-7, 0, 4]})
    legs_mirror(phase, {0: [0, 0, 2], .7: [-24, 0, 5], 1.05: [-6, 0, 3], 2.2: [0, 0, 2]},
                {0: [4, 0, 0], .7: [46, 0, 0], 1.05: [10, 0, 0], 2.2: [4, 0, 0]})
    wing_set(phase, root_z={0: CLOSED_Z, .7: [74, 78, 82], 1.05: [4, 10, 16], 2.2: [24, 30, 36]},
             root_x={0: [0, 0, 0], .7: [10, 8, 6], 1.05: [-16, -10, -4], 2.2: [-10, -4, 2]},
             arm_y={0: [0, 0, 0], .7: [34, 34, 34], 1.05: [-12, -12, -12], 2.2: [0, 0, 0]},
             arm_z={0: REST_ARM_Z, .7: [16, 18, 20], 1.05: [6, 8, 10], 2.2: REST_ARM_Z})
    clips["phase_shift"] = anim(2.2, phase)

    # Halo lances: arms raise, rings converge, SLAM down.
    lance = {
        "body": rot({0: [0, 0, 0], .9: [-6, 0, 0], 1.05: [-14, 0, 0], 1.7: [-10, 0, 0], 2.8: [0, 0, 0]}),
        "head": rot({0: [0, 0, 0], .9: [-24, 0, 0], 1.05: [8, 0, 0], 2.8: [0, 0, 0]}),
        "halo": channels(pos({0: [0, 0, 0], .9: [0, 1.2, 0], 1.05: [0, .2, 0], 2.8: [0, 0, 0]}),
                         rot({0: [0, 0, 14], .9: [0, 240, 14], 1.05: [0, 300, 8], 2.8: [0, 360, 14]})),
        "drapery": rot({0: [0, 0, 0], 1.05: [-6, 0, 0], 2.8: [0, 0, 0]}),
    }
    arms_mirror(lance, {0: [0, 0, 8], .9: [-142, 0, 14], 1.05: [-38, 0, 12], 1.7: [-24, 0, 10], 2.8: [0, 0, 8]},
                {0: [-7, 0, 4], .9: [-18, 0, 0], 1.05: [-30, 0, 0], 2.8: [-7, 0, 4]})
    legs_mirror(lance, {0: [0, 0, 2], .9: [-8, 0, 3], 1.05: [-16, 0, 4], 2.8: [0, 0, 2]},
                {0: [4, 0, 0], 1.05: [18, 0, 0], 2.8: [4, 0, 0]})
    wing_set(lance, root_z={0: [24, 30, 36], .9: [16, 22, 28], 1.05: [10, 16, 22], 2.8: [24, 30, 36]},
             arm_z={0: REST_ARM_Z, 1.05: [4, 6, 8], 2.8: REST_ARM_Z})
    clips["halo_lances"] = anim(2.8, lance)

    # Crosswind: gather back, WHIP all six wings forward.
    crosswind = {
        "body": rot({0: [0, 0, 0], 1.2: [14, 0, 0], 1.5: [-12, 0, 0], 2.4: [-6, 0, 0], 3.6: [0, 0, 0]}),
        "head": rot({0: [0, 0, 0], 1.2: [16, 0, 0], 1.5: [-10, 0, 0], 3.6: [0, 0, 0]}),
        "halo": channels(pos({0: [0, 0, 0], 1.2: [0, -.4, 0], 1.5: [0, .6, 0], 3.6: [0, 0, 0]}),
                         rot({0: [0, 0, 14], 1.2: [0, -40, 20], 1.5: [0, 60, 12], 3.6: [0, 0, 14]})),
        "drapery": rot({0: [0, 0, 0], 1.2: [8, 0, 0], 1.5: [-10, 0, 0], 3.6: [0, 0, 0]}),
    }
    arms_mirror(crosswind, {0: [0, 0, 8], 1.2: [34, 0, 22], 1.5: [-96, 0, 16], 2.4: [-40, 0, 12], 3.6: [0, 0, 8]},
                {0: [-7, 0, 4], 1.2: [-20, 0, 0], 1.5: [-16, 0, 0], 3.6: [-7, 0, 4]})
    legs_mirror(crosswind, {0: [0, 0, 2], 1.2: [-10, 0, 3], 1.5: [-18, 0, 4], 3.6: [0, 0, 2]},
                {0: [4, 0, 0], 1.5: [22, 0, 0], 3.6: [4, 0, 0]})
    wing_set(crosswind, root_z={0: [24, 30, 36], 1.2: [78, 82, 86], 1.5: [8, 14, 20], 2.4: [16, 22, 28], 3.6: [24, 30, 36]},
             root_x={0: [0, 0, 0], 1.2: [12, 10, 8], 1.5: [-14, -8, -2], 3.6: [0, 0, 0]},
             arm_y={0: [0, 0, 0], 1.2: [22, 22, 22], 1.5: [-18, -18, -18], 3.6: [0, 0, 0]},
             fan1_z={0: [0, 0, 0], 1.2: [10, 10, 10], 1.5: [-12, -12, -12], 3.6: [0, 0, 0]})
    clips["crosswind"] = anim(3.6, crosswind)

    # Feather verdict: draw the bow, release a volley.
    verdict = {
        "body": rot({0: [0, 0, 0], .8: [-4, -14, 0], .95: [-8, 8, 0], 2.2: [0, 0, 0]}),
        "head": rot({0: [0, 0, 0], .8: [-6, -22, 0], .95: [-8, 6, 0], 2.2: [0, 0, 0]}),
        "halo": rot({0: [0, 0, 14], .8: [0, -18, 16], .95: [0, 24, 12], 2.2: [0, 0, 14]}),
        "arm_left_upper": rot({0: [0, 0, 8], .8: [-88, -14, 10], .95: [-92, 6, 10], 2.2: [0, 0, 8]}),
        "arm_left_fore": rot({0: [-7, 0, 4], .8: [-16, 0, 0], 2.2: [-7, 0, 4]}),
        "arm_left_hand": rot({0: [0, 0, 0], .8: [-20, 0, 0], 2.2: [0, 0, 0]}),
        "arm_right_upper": rot({0: [0, 0, -8], .8: [-38, -30, -14], .95: [-96, 10, -10], 2.2: [0, 0, -8]}),
        "arm_right_fore": rot({0: [-7, 0, -4], .8: [-84, 0, 0], .95: [-14, 0, 0], 2.2: [-7, 0, -4]}),
        "arm_right_hand": rot({0: [0, 0, 0], .8: [-30, 0, 0], .95: [-8, 0, 0], 2.2: [0, 0, 0]}),
        "drapery": rot({0: [0, 0, 0], .8: [0, -8, 0], .95: [0, 6, 0], 2.2: [0, 0, 0]}),
    }
    legs_mirror(verdict, {0: [0, 0, 2], .8: [-8, -6, 3], 2.2: [0, 0, 2]},
                {0: [4, 0, 0], .8: [14, 0, 0], 2.2: [4, 0, 0]})
    wing_set(verdict, root_z={0: [24, 30, 36], .8: [30, 36, 42], .95: [14, 20, 26], 2.2: [24, 30, 36]},
             arm_z={0: REST_ARM_Z, .8: [16, 18, 20], .95: [6, 8, 10], 2.2: REST_ARM_Z})
    clips["feather_verdict"] = anim(2.2, verdict)

    # Wingbeat: three crouched pulses with double flaps.
    wb_root = {0: [24, 30, 36]}
    for t in (.5, 1.1, 1.7):
        wb_root[t - .18] = [64, 68, 72]
        wb_root[t] = [12, 18, 24]
        wb_root[t + .16] = [40, 46, 52]
    wb_root[2.9] = [24, 30, 36]
    wingbeat = {
        "body": channels(pos({0: [0, 0, 0], .32: [0, -1.3, 0], .5: [0, .4, 0], .92: [0, -1.3, 0], 1.1: [0, .4, 0], 1.52: [0, -1.3, 0], 1.7: [0, .5, 0], 2.9: [0, 0, 0]}),
                         rot({0: [0, 0, 0], .32: [10, 0, 0], .5: [-8, 0, 0], 2.9: [0, 0, 0]})),
        "head": rot({0: [0, 0, 0], .32: [12, 0, 0], .5: [-8, 0, 0], 2.9: [0, 0, 0]}),
        "halo": pos({0: [0, 0, 0], .32: [0, -.9, 0], .5: [0, .8, 0], .92: [0, -.9, 0], 1.1: [0, .8, 0], 1.52: [0, -.9, 0], 1.7: [0, 1, 0], 2.9: [0, 0, 0]}),
        "drapery": rot({0: [0, 0, 0], .32: [8, 0, 0], .5: [-6, 0, 0], 2.9: [0, 0, 0]}),
    }
    arms_mirror(wingbeat, {0: [0, 0, 8], .32: [-30, 0, 30], .5: [-14, 0, 40], 2.9: [0, 0, 8]},
                {0: [-7, 0, 4], .32: [-24, 0, 0], 2.9: [-7, 0, 4]})
    legs_mirror(wingbeat, {0: [0, 0, 2], .32: [-26, 0, 5], .5: [-8, 0, 3], 2.9: [0, 0, 2]},
                {0: [4, 0, 0], .32: [48, 0, 0], .5: [12, 0, 0], 2.9: [4, 0, 0]})
    wing_set(wingbeat, root_z=wb_root, arm_z={t: [6, 8, 10] for t in wb_root},
             fan1_z={t: [-6, -6, -6] for t in wb_root})
    clips["wingbeat"] = anim(2.9, wingbeat)

    # Ascension grip: left hand twirls skyward, wings spiral.
    ascension = {
        "body": rot({0: [0, 0, 0], .6: [-8, 10, 0], 2.4: [-8, -6, 0], 3.2: [0, 0, 0]}),
        "head": rot({0: [0, 0, 0], .6: [-22, 12, 0], 2.4: [-18, -8, 0], 3.2: [0, 0, 0]}),
        "halo": rot({0: [0, 0, 14], .6: [0, 180, 14], 2.4: [0, 900, 14], 3.2: [0, 1080, 14]}),
        "arm_left_upper": rot({0: [0, 0, 8], .6: [-158, 0, 18], 2.4: [-152, -30, 18], 3.2: [0, 0, 8]}),
        "arm_left_fore": rot({0: [-7, 0, 4], .6: [-12, 0, 0], 1.2: [-12, 90, 0], 1.8: [-12, 200, 0], 2.4: [-12, 320, 0], 3.2: [-7, 0, 4]}),
        "arm_left_hand": rot({0: [0, 0, 0], .6: [-10, 0, 0], 2.4: [-10, 0, 0], 3.2: [0, 0, 0]}),
        "arm_right_upper": rot({0: [0, 0, -8], .6: [-18, 0, -26], 2.4: [-14, 0, -22], 3.2: [0, 0, -8]}),
        "arm_right_fore": rot({0: [-7, 0, -4], .6: [-40, 0, 0], 2.4: [-30, 0, 0], 3.2: [-7, 0, -4]}),
        "drapery": rot({0: [0, 0, 0], .6: [-6, 8, 0], 2.4: [-6, -8, 0], 3.2: [0, 0, 0]}),
    }
    legs_mirror(ascension, {0: [0, 0, 2], .6: [-10, 0, 4], 2.4: [-8, 6, 4], 3.2: [0, 0, 2]},
                {0: [4, 0, 0], .6: [16, 0, 0], 3.2: [4, 0, 0]})
    wing_set(ascension, root_z={0: [24, 30, 36], .6: [44, 50, 56], 1.2: [30, 36, 42], 1.8: [48, 54, 60], 2.4: [30, 36, 42], 3.2: [24, 30, 36]},
             arm_y={0: [0, 0, 0], .6: [16, 16, 16], 1.2: [-14, -14, -14], 1.8: [16, 16, 16], 2.4: [-14, -14, -14], 3.2: [0, 0, 0]},
             arm_z={0: REST_ARM_Z, .6: [14, 16, 18], 3.2: REST_ARM_Z})
    clips["ascension_grip"] = anim(3.2, ascension)

    # Ruby descent: aim down, dive, ruby impact.
    descent = {
        "body": rot({0: [0, 0, 0], .6: [12, 0, 0], 2.1: [10, 0, 0], 2.25: [-8, 0, 0], 3.5: [0, 0, 0]}),
        "head": rot({0: [0, 0, 0], .6: [24, 0, 0], 2.25: [-12, 0, 0], 3.5: [0, 0, 0]}),
        "halo": channels(pos({0: [0, 0, 0], .6: [0, -.6, 0], 2.25: [0, .8, 0], 3.5: [0, 0, 0]}),
                         rot({0: [0, 0, 14], .6: [0, -60, 22], 2.25: [0, 120, 10], 3.5: [0, 0, 14]})),
        "arm_right_upper": rot({0: [0, 0, -8], .6: [-58, -8, -12], 2.1: [-54, -8, -12], 2.25: [-30, 0, -30], 3.5: [0, 0, -8]}),
        "arm_right_fore": rot({0: [-7, 0, -4], .6: [-10, 0, 0], 2.25: [-20, 0, 0], 3.5: [-7, 0, -4]}),
        "arm_right_hand": rot({0: [0, 0, 0], .6: [-14, 0, 0], 2.25: [0, 0, 0], 3.5: [0, 0, 0]}),
        "arm_left_upper": rot({0: [0, 0, 8], .6: [30, 0, 30], 2.25: [-40, 0, 34], 3.5: [0, 0, 8]}),
        "arm_left_fore": rot({0: [-7, 0, 4], .6: [-18, 0, 0], 3.5: [-7, 0, 4]}),
        "drapery": rot({0: [0, 0, 0], .6: [10, 0, 0], 2.25: [-8, 0, 0], 3.5: [0, 0, 0]}),
    }
    legs_mirror(descent, {0: [0, 0, 2], .6: [-14, 0, 4], 2.25: [-20, 0, 5], 3.5: [0, 0, 2]},
                {0: [4, 0, 0], .6: [24, 0, 0], 3.5: [4, 0, 0]})
    wing_set(descent, root_z={0: [24, 30, 36], .6: [70, 74, 78], 2.1: [66, 70, 74], 2.25: [10, 16, 22], 3.5: [24, 30, 36]},
             root_x={0: [0, 0, 0], .6: [14, 12, 10], 2.25: [-16, -10, -4], 3.5: [0, 0, 0]},
             fan1_z={0: [0, 0, 0], .6: [12, 12, 12], 2.25: [-10, -10, -10], 3.5: [0, 0, 0]})
    clips["ruby_descent"] = anim(3.5, descent)

    # Punch: quick one-two with body twist (hit ticks 10 and 20 -> 0.5s / 1.0s).
    punch = {
        "body": rot({0: [0, 0, 0], .3: [4, -16, 0], .5: [2, 20, 0], .8: [0, 8, 0], 1.0: [2, -18, 0], 1.5: [0, 0, 0]}),
        "head": rot({0: [0, 0, 0], .3: [4, -12, 0], .5: [0, 14, 0], 1.0: [0, -14, 0], 1.5: [0, 0, 0]}),
        "halo": pos({0: [0, 0, 0], .3: [0, -.2, 0], .5: [0, .3, 0], 1.0: [0, .3, 0], 1.5: [0, 0, 0]}),
        "arm_right_upper": rot({0: [0, 0, -8], .3: [34, -26, -14], .5: [-88, 10, -8], .8: [-30, 0, -10], 1.0: [-40, -10, -16], 1.5: [0, 0, -8]}),
        "arm_right_fore": rot({0: [-7, 0, -4], .3: [-96, 0, 0], .5: [-8, 0, 0], 1.0: [-40, 0, 0], 1.5: [-7, 0, -4]}),
        "arm_right_hand": rot({0: [0, 0, 0], .3: [-24, 0, 0], .5: [0, 0, 0], 1.5: [0, 0, 0]}),
        "arm_left_upper": rot({0: [0, 0, 8], .3: [-30, 0, 14], .5: [26, 0, 18], .8: [30, 14, 16], 1.0: [-84, -8, 10], 1.5: [0, 0, 8]}),
        "arm_left_fore": rot({0: [-7, 0, 4], .3: [-72, 0, 0], .5: [-84, 0, 0], 1.0: [-10, 0, 0], 1.5: [-7, 0, 4]}),
        "arm_left_hand": rot({0: [0, 0, 0], .3: [-20, 0, 0], 1.0: [0, 0, 0], 1.5: [0, 0, 0]}),
        "drapery": rot({0: [0, 0, 0], .3: [2, -10, 0], .5: [0, 12, 0], 1.0: [0, -12, 0], 1.5: [0, 0, 0]}),
    }
    legs_mirror(punch, {0: [0, 0, 2], .5: [-16, 4, 3], 1.0: [10, -4, 3], 1.5: [0, 0, 2]},
                {0: [4, 0, 0], .5: [22, 0, 0], 1.0: [8, 0, 0], 1.5: [4, 0, 0]})
    wing_set(punch, root_z={0: [24, 30, 36], .3: [34, 40, 46], .5: [16, 22, 28], .8: [26, 32, 38], 1.0: [14, 20, 26], 1.5: [24, 30, 36]},
             arm_z={0: REST_ARM_Z, .5: [8, 10, 12], 1.0: [8, 10, 12], 1.5: REST_ARM_Z})
    clips["punch"] = anim(1.5, punch)

    # Death: wings wrap, then burst wide as the body ascends (entity rises via tickDeath).
    death = {
        "body": channels(pos({0: [0, 0, 0], .8: [0, -.6, 0], 1.4: [0, .3, 0], 2: [0, .1, 0], 2.4: [0, .3, 0], 3: [0, .1, 0]}),
                         rot({0: [0, 0, 0], .8: [10, 0, 1], 1.4: [-8, 0, -1], 2: [-6, 0, 1], 2.4: [-8, 0, -1], 3: [-6, 0, 0]})),
        "head": rot({0: [10, 0, 0], .8: [16, 0, 0], 1.4: [-24, 0, 0], 3: [-20, 0, 0]}),
        "halo": channels(pos({0: [0, 0, 0], .8: [0, -.5, 0], 1.4: [0, 1.2, 0], 3: [0, 1, 0]}),
                         rot({0: [0, 0, 14], .8: [0, -60, 26], 1.4: [0, 420, 16], 2.2: [0, 980, 14], 3: [0, 1440, 14]})),
        "arm_left_upper": rot({0: [0, 0, 8], .8: [-26, 30, 30], 1.4: [-46, 0, 58], 3: [-42, 0, 54]}),
        "arm_left_fore": rot({0: [-7, 0, 4], .8: [-52, 0, 0], 1.4: [-12, 0, 0], 3: [-10, 0, 0]}),
        "arm_left_hand": rot({0: [0, 0, 0], .8: [-18, 0, 0], 1.4: [0, 0, 0], 3: [0, 0, 0]}),
        "leg_left_upper": rot({0: [0, 0, 2], .8: [-18, 0, 4], 1.4: [-8, 0, 3], 3: [-8, 0, 3]}),
        "leg_left_lower": rot({0: [4, 0, 0], .8: [34, 0, 0], 1.4: [14, 0, 0], 3: [14, 0, 0]}),
        "drapery": rot({0: [0, 0, 0], .8: [8, 0, 0], 1.4: [-10, 0, 0], 2: [-6, 0, 2], 2.4: [-10, 0, -2], 3: [-6, 0, 0]}),
    }
    mirrored(death, {"arm_left_upper": rot({0: [0, 0, 8], .8: [-26, 30, 30], 1.4: [-46, 0, 58], 3: [-42, 0, 54]}),
                     "arm_left_fore": rot({0: [-7, 0, 4], .8: [-52, 0, 0], 1.4: [-12, 0, 0], 3: [-10, 0, 0]}),
                     "arm_left_hand": rot({0: [0, 0, 0], .8: [-18, 0, 0], 1.4: [0, 0, 0], 3: [0, 0, 0]}),
                     "leg_left_upper": rot({0: [0, 0, 2], .8: [-18, 0, 4], 1.4: [-8, 0, 3], 3: [-8, 0, 3]}),
                     "leg_left_lower": rot({0: [4, 0, 0], .8: [34, 0, 0], 1.4: [14, 0, 0], 3: [14, 0, 0]})})
    wing_set(death, root_z={0: CLOSED_Z, .8: [70, 74, 78], 1.4: [6, 10, 14], 3: [10, 14, 18]},
             root_x={0: [0, 0, 0], .8: [12, 10, 8], 1.4: [-22, -16, -10], 3: [-20, -14, -8]},
             arm_y={0: [0, 0, 0], .8: [26, 26, 26], 1.4: [-10, -10, -10], 3: [-8, -8, -8]},
             fan1_z={0: [0, 0, 0], .8: [12, 12, 12], 1.4: [-8, -8, -8], 3: [-6, -6, -6]})
    clips["death"] = anim(3, death)

    return clips


# ---------------------------------------------------------------- attack geometry
def ring_bones(prefix: str, parent: str, count: int, radius: float, size, material: str, y: float = 0) -> list[dict]:
    bones = []
    for i in range(count):
        bones.append(bone(f"{prefix}_{i + 1}", [0, y, 0], parent,
                          [cube([-size[0] / 2, y - size[1] / 2, -radius - size[2] / 2],
                                [size[0], size[1], size[2]], material)], [0, i * 360 / count, 0]))
    return bones


def attack_geometry() -> dict[str, list[dict]]:
    attacks: dict[str, list[dict]] = {}

    lance = [
        bone("root", [0, 0, 0]),
        bone("core", [0, 0, 0], "root", [cube([-1.3, 0, -1.3], [2.6, 22, 2.6], "gold_bright")]),
        bone("beam_w", [0, 0, 0], "root", [cube([-3.2, 1, -.5], [6.4, 20, 1], "gold")]),
        bone("beam_d", [0, 0, 0], "root", [cube([-.5, 1, -3.2], [1, 20, 6.4], "gold")]),
        bone("cap", [0, 22, 0], "root", [cube([-2, 0, -2], [4, 2.4, 4], "gem_ruby")]),
    ]
    lance += ring_bones("ring", "root", 8, 5.2, (2.2, 1.2, 1.6), "gold")
    attacks["halo_lance"] = lance

    crosswind = [bone("root", [0, 0, 0]),
                 bone("core", [0, 6.4, 0], "root", [cube([-1.6, -1.6, -1.6], [3.2, 3.2, 3.2], "gold_bright")])]
    for i in range(6):
        x = -12.5 + i * 5
        crosswind.append(bone(f"wall_{i + 1}", [x + 1.6, 6, 0], "root", [
            cube([x, 2.5, -.7], [3.2, 7.5, 1.4], "feather_white"),
            cube([x + .4, 1, .2], [2.4, 6, 1.2], "feather_blue"),
        ], [0, 0, -8 + i * 3]))
    crosswind.append(bone("edge", [0, 10.4, 0], "root", [cube([-13.5, 0, -.8], [27, 1.4, 1.6], "gold")]))
    crosswind += ring_bones("ring", "root", 6, 3.4, (1.8, 1, 1.4), "gold", y=6.4)
    attacks["seraphic_crosswind"] = crosswind

    attacks["seraphic_feather"] = [
        bone("root", [0, 0, 0]),
        bone("core", [0, 0, 0], "root", [cube([-.9, -.9, -1.8], [1.8, 1.8, 3.6], "gold_bright")]),
        bone("feather", [0, 0, 0], "root", [
            cube([-.5, -.5, -6.5], [1, 1, 13], "gold"),
            cube([-2.8, -.4, -5.5], [5.6, .8, 8], "feather_white"),
            cube([-2.2, -.4, -3.5], [4.4, .8, 6], "feather_blue"),
        ]),
        bone("vane_b", [0, 0, 0], "root", [cube([-2.4, -.4, -4.5], [4.8, .8, 7], "feather_white")], [0, 180, 0]),
    ] + ring_bones("ring", "root", 4, 2.6, (1.4, .8, 1.2), "gold")

    ring = [
        bone("root", [0, 0, 0]),
        bone("core", [0, 0, 0], "root", [cube([-1.4, -.7, -1.4], [2.8, 1.4, 2.8], "gold_bright")]),
    ]
    for i in range(16):
        material = "feather_white" if i % 2 == 0 else "gold"
        ring.append(bone(f"ring_{i + 1}", [0, 0, 0], "root",
                         [cube([-1, -.5, -11.5], [2, 1, 3], material)], [0, i * 22.5, 0]))
    attacks["wingbeat_ring"] = ring

    ascension = [
        bone("root", [0, 0, 0]),
        bone("core", [0, 0, 0], "root", [cube([-1.2, 0, -1.2], [2.4, 18, 2.4], "gold_bright")]),
        bone("funnel_a", [0, 0, 0], "root", [cube([-6, 0, -6], [12, 1.2, 12], "gold")]),
        bone("funnel_b", [0, 6, 0], "root", [cube([-4.6, 0, -4.6], [9.2, 1.2, 9.2], "gold")], [0, 22, 0]),
        bone("funnel_c", [0, 12, 0], "root", [cube([-3.2, 0, -3.2], [6.4, 1.2, 6.4], "gold")], [0, 44, 0]),
        bone("spiral_a", [0, 3, 0], "root", [cube([-5.4, -.5, -.8], [10.8, 1, 1.6], "feather_white")], [0, 30, 0]),
        bone("spiral_b", [0, 9, 0], "root", [cube([-5.4, -.5, -.8], [10.8, 1, 1.6], "feather_white")], [0, 150, 0]),
        bone("spiral_c", [0, 15, 0], "root", [cube([-5.4, -.5, -.8], [10.8, 1, 1.6], "feather_white")], [0, 270, 0]),
    ] + ring_bones("ring", "root", 8, 7.4, (2, 1, 1.4), "gold")
    attacks["angel_ascension"] = ascension

    descent = [
        bone("root", [0, 0, 0]),
        bone("core", [0, 14, 0], "root", [
            cube([-1.6, -6, -1.6], [3.2, 12, 3.2], "gem_ruby"),
            cube([-2.6, -1, -2.6], [5.2, 2, 5.2], "gem_ruby"),
        ]),
        bone("blade", [0, 14, 0], "root", [cube([-.7, -9, -.7], [1.4, 5, 1.4], "gold_bright")]),
        bone("target", [0, .3, 0], "root", [cube([-4.6, 0, -4.6], [9.2, .7, 9.2], "gold")], [0, 22, 0]),
    ]
    descent += ring_bones("ring", "root", 8, 5.6, (2.2, .8, 1.6), "gem_ruby", y=.4)
    attacks["ruby_descent"] = descent
    return attacks


# ---------------------------------------------------------------- attack animations
def attack_animations() -> dict:
    clips: dict[str, dict] = {}

    lance = {
        "root": scl({0: [.2, .2, .2], .3: [1, 1.05, 1], .8: [1, 1, 1]}),
        "core": channels(scl({0: [1, .6, 1], .4: [1.15, 1.1, 1.15], .8: [1, 1, 1]}),
                         rot({0: [0, 0, 0], .8: [0, 220, 0]})),
        "beam_w": scl({0: [.4, .3, 1], .3: [1, 1, 1]}),
        "beam_d": scl({0: [1, .3, .4], .3: [1, 1, 1]}),
        "cap": pos({0: [0, 4, 0], .35: [0, 0, 0]}),
    }
    for i in range(8):
        lance[f"ring_{i + 1}"] = channels(pos({0: [0, 14, 0], .45: [0, .4, 0], .8: [0, 0, 0]}),
                                          scl({0: [.4, .4, .4], .45: [1.1, 1.1, 1.1], .8: [1, 1, 1]}))
    clips["halo_lance_active"] = anim(.8, lance, True)

    crosswind = {"root": scl({0: [.5, .6, .5], .4: [1, 1, 1]}),
                 "core": rot({0: [0, 0, 0], .8: [0, 360, 0]}),
                 "edge": pos({0: [0, -1, 0], .4: [0, 0, 0]})}
    for i in range(6):
        crosswind[f"wall_{i + 1}"] = rot({0: [0, 0, -20 + i * 4], .35: [0, 0, -8 + i * 3]})
        crosswind[f"ring_{i + 1}"] = rot({0: [0, 0, 0], .8: [0, 360, 0]})
    clips["crosswind_active"] = anim(.8, crosswind, True)

    feather = {
        "root": rot({0: [0, 0, 0], .8: [0, 0, 360]}),
        "core": scl({0: [.9, .9, .9], .4: [1.25, 1.25, 1.25], .8: [.9, .9, .9]}),
        "feather": rot({0: [0, 0, 0], .8: [0, 0, 360]}),
        "vane_b": rot({0: [0, 180, 0], .8: [0, 540, 0]}),
    }
    for i in range(4):
        feather[f"ring_{i + 1}"] = rot({0: [0, 0, 0], .8: [360, 0, 0]})
    clips["feather_active"] = anim(.8, feather, True)

    ring_clip = {"root": rot({0: [0, 0, 0], .8: [0, 90, 0]}),
                 "core": scl({0: [1, 1.4, 1], .4: [.8, .8, .8], .8: [1, 1.4, 1]})}
    for i in range(16):
        ring_clip[f"ring_{i + 1}"] = scl({0: [1, 1, 1], .4: [1, 1.6, 1], .8: [1, 1, 1]})
    clips["ring_active"] = anim(.8, ring_clip, True)

    ascension = {
        "root": rot({0: [0, 0, 0], .8: [0, 120, 0]}),
        "core": scl({0: [1, 1, 1], .4: [1.1, 1.25, 1.1], .8: [1, 1, 1]}),
        "funnel_a": pos({0: [0, -1, 0], .8: [0, 1.5, 0]}),
        "funnel_b": pos({0: [0, -1, 0], .8: [0, 1.5, 0]}),
        "funnel_c": pos({0: [0, -1, 0], .8: [0, 1.5, 0]}),
        "spiral_a": rot({0: [0, 30, 0], .8: [0, 200, 0]}),
        "spiral_b": rot({0: [0, 150, 0], .8: [0, 320, 0]}),
        "spiral_c": rot({0: [0, 270, 0], .8: [0, 440, 0]}),
    }
    for i in range(8):
        ascension[f"ring_{i + 1}"] = channels(pos({0: [0, 0, 0], .8: [0, 16, 0]}),
                                              scl({0: [1, 1, 1], .7: [.5, .5, .5], .8: [0, 0, 0]}))
    clips["ascension_active"] = anim(.8, ascension, True)

    descent = {
        "root": scl({0: [.6, .6, .6], .3: [1, 1, 1]}),
        "core": channels(pos({0: [0, 10, 0], .55: [0, -12.5, 0], .7: [0, -12.5, 0], 1: [0, -12.5, 0]}),
                         scl({0: [.8, 1.4, .8], .55: [1.3, 1, 1.3], .7: [1.6, .4, 1.6], 1: [0, 0, 0]})),
        "blade": pos({0: [0, 8, 0], .55: [0, -6, 0]}),
        "target": scl({0: [.3, .3, .3], .3: [1, 1, 1], .7: [1.25, 1.25, 1.25], 1: [0, 0, 0]}),
    }
    for i in range(8):
        descent[f"ring_{i + 1}"] = rot({0: [0, 0, 0], .55: [0, 160, 0], 1: [0, 320, 0]})
    clips["descent_active"] = anim(1, descent, True)

    return clips


# ---------------------------------------------------------------- attack textures
GOLD = (232, 190, 92, 255)
GOLD_LIGHT = (255, 228, 140, 255)
WHITE = (240, 236, 228, 255)
BLUE = (110, 132, 156, 255)
RUBY = (198, 36, 52, 255)
RUBY_LIGHT = (238, 96, 108, 255)
TRANS = (0, 0, 0, 0)


def _base(size=128) -> Image.Image:
    return Image.new("RGBA", (size, size), TRANS)


def _feather_stripes(draw, box, base, dark, light, step=6) -> None:
    x0, y0, x1, y1 = box
    draw.rectangle(box, fill=base)
    for offset in range(x0 - (y1 - y0), x1 + 1, step):
        draw.line((offset, y1, offset + (y1 - y0), y0), fill=dark, width=1)
    draw.line((x0, y0, x1, y0), fill=light, width=2)


def tex_lance() -> Image.Image:
    img = _base(); d = ImageDraw.Draw(img)
    d.rectangle((52, 4, 76, 124), fill=GOLD)
    d.rectangle((58, 4, 70, 124), fill=GOLD_LIGHT)
    d.rectangle((52, 4, 55, 124), fill=(176, 132, 54, 255))
    for box in ((4, 40, 44, 60), (84, 40, 124, 60), (4, 84, 44, 104), (84, 84, 124, 104)):
        d.rectangle(box, fill=GOLD)
        d.rectangle((box[0] + 6, box[1] + 6, box[2] - 4, box[3] - 6), fill=GOLD_LIGHT)
    d.polygon(((52, 4), (76, 4), (64, 18)), fill=RUBY_LIGHT)
    return img


def tex_crosswind() -> Image.Image:
    img = _base(); d = ImageDraw.Draw(img)
    for i in range(6):
        x = 4 + i * 21
        _feather_stripes(d, (x, 30, x + 15, 120), WHITE, (210, 206, 196), (252, 250, 246))
        _feather_stripes(d, (x + 3, 60, x + 12, 120), BLUE, (88, 108, 132), (150, 172, 196))
    d.rectangle((0, 14, 128, 24), fill=GOLD)
    d.rectangle((0, 16, 128, 19), fill=GOLD_LIGHT)
    d.rectangle((56, 52, 72, 68), fill=GOLD_LIGHT)
    return img


def tex_feather() -> Image.Image:
    img = _base(); d = ImageDraw.Draw(img)
    _feather_stripes(d, (44, 8, 84, 120), WHITE, (210, 206, 196), (252, 250, 246), 7)
    _feather_stripes(d, (52, 30, 76, 110), BLUE, (88, 108, 132), (150, 172, 196), 6)
    d.rectangle((60, 2, 68, 126), fill=GOLD)
    d.rectangle((61, 2, 63, 126), fill=GOLD_LIGHT)
    d.ellipse((56, 54, 72, 70), fill=GOLD_LIGHT)
    d.ellipse((60, 58, 68, 66), fill=RUBY_LIGHT)
    return img


def tex_ring() -> Image.Image:
    img = _base(); d = ImageDraw.Draw(img)
    cx = cy = 64
    for i in range(16):
        angle = math.radians(i * 22.5)
        x = cx + math.cos(angle) * 46
        y = cy + math.sin(angle) * 46
        d.rectangle((x - 6, y - 6, x + 6, y + 6), fill=WHITE if i % 2 == 0 else GOLD)
        d.rectangle((x - 6, y - 6, x + 6, y - 4), fill=GOLD_LIGHT if i % 2 else (252, 250, 246, 255))
    d.ellipse((52, 52, 76, 76), fill=GOLD_LIGHT)
    return img


def tex_ascension() -> Image.Image:
    img = _base(); d = ImageDraw.Draw(img)
    d.rectangle((52, 4, 76, 124), fill=GOLD_LIGHT)
    d.rectangle((56, 4, 72, 124), fill=(255, 244, 200, 255))
    for y in (10, 50, 90):
        d.rectangle((20, y, 108, y + 8), fill=GOLD)
        d.rectangle((24, y + 2, 104, y + 4), fill=GOLD_LIGHT)
    for x in (16, 100):
        _feather_stripes(d, (x, 30, x + 12, 110), WHITE, (210, 206, 196), (252, 250, 246))
    return img


def tex_descent() -> Image.Image:
    img = _base(); d = ImageDraw.Draw(img)
    d.polygon(((64, 6), (92, 64), (64, 122), (36, 64)), fill=RUBY)
    d.polygon(((64, 6), (92, 64), (64, 64)), fill=RUBY_LIGHT)
    d.polygon(((64, 64), (92, 64), (64, 122)), fill=(168, 26, 42, 255))
    d.polygon(((64, 6), (36, 64), (64, 64)), fill=(222, 66, 80, 255))
    d.rectangle((60, 20, 63, 24), fill=(255, 214, 220, 255))
    d.ellipse((16, 100, 48, 124), fill=GOLD)
    d.ellipse((80, 100, 112, 124), fill=GOLD)
    d.ellipse((24, 106, 40, 118), fill=GOLD_LIGHT)
    d.ellipse((88, 106, 104, 118), fill=GOLD_LIGHT)
    return img


# ---------------------------------------------------------------- output assembly
ATTACK_TEXTURES = {
    "halo_lance": tex_lance,
    "seraphic_crosswind": tex_crosswind,
    "seraphic_feather": tex_feather,
    "wingbeat_ring": tex_ring,
    "angel_ascension": tex_ascension,
    "ruby_descent": tex_descent,
}

ATTACK_CLIP_FOR_GEO = {
    "halo_lance": "halo_lance_active",
    "seraphic_crosswind": "crosswind_active",
    "seraphic_feather": "feather_active",
    "wingbeat_ring": "ring_active",
    "angel_ascension": "ascension_active",
    "ruby_descent": "descent_active",
}

BOSS_CLIPS = {"dormant", "idle_ground", "idle_flight", "walk", "awaken_rebuff", "phase_shift",
              "halo_lances", "crosswind", "feather_verdict", "wingbeat", "ascension_grip",
              "ruby_descent", "punch", "death"}


def validate_assets() -> None:
    model = json.loads((A / "geo/paradise/angel_boy.geo.json").read_text(encoding="utf-8"))
    bones = model["minecraft:geometry"][0]["bones"]
    names = {entry["name"] for entry in bones}
    if len(names) != len(bones):
        raise ValueError("Duplicate boss bone name")
    for entry in bones:
        if entry.get("parent") not in names and entry.get("parent") is not None:
            raise ValueError(f"Missing parent for {entry['name']}")
        for box in entry.get("cubes", []):
            if any(float(value) <= 0 for value in box["size"]):
                raise ValueError(f"Non-positive cube on {entry['name']}")

    animation_data = json.loads((A / "animations/paradise/angel_boy.animation.json").read_text(encoding="utf-8"))["animations"]
    if set(animation_data) != BOSS_CLIPS:
        raise ValueError(f"Boss clip contract differs: {set(animation_data) ^ BOSS_CLIPS}")
    for clip, data in animation_data.items():
        missing = set(data.get("bones", {})) - names
        if missing:
            raise ValueError(f"Animation {clip} references missing bones: {sorted(missing)}")

    attack_clips = json.loads((A / "animations/paradise/angel_attacks.animation.json").read_text(encoding="utf-8"))["animations"]
    for ability, clip in ATTACK_CLIP_FOR_GEO.items():
        data = json.loads((A / f"geo/paradise/{ability}.geo.json").read_text(encoding="utf-8"))
        ability_names = {entry["name"] for entry in data["minecraft:geometry"][0]["bones"]}
        missing = set(attack_clips[clip].get("bones", {})) - ability_names
        if missing:
            raise ValueError(f"Ability {ability} clip references missing bones: {sorted(missing)}")
        texture = Image.open(A / f"textures/paradise/entity/{ability}.png")
        if texture.size != (128, 128):
            raise ValueError(f"Wrong {ability} texture size: {texture.size}")

    boss_texture = Image.open(A / "textures/paradise/entity/angel_boy.png")
    emissive = Image.open(A / "textures/paradise/entity/angel_boy_emissive.png")
    if boss_texture.size != (256, 256) or emissive.size != (256, 256):
        raise ValueError("Boss texture size mismatch")
    alpha = emissive.getchannel("A")
    if alpha.getextrema() != (0, 255):
        raise ValueError("Emissive mask must be binary transparent/opaque")


def main() -> None:
    write(A / "geo/paradise/angel_boy.geo.json", geo("geometry.angel_boy", boss_geometry()))
    write(A / "animations/paradise/angel_boy.animation.json",
          {"format_version": "1.8.0", "animations": boss_animations()})
    for ability, bones in attack_geometry().items():
        write(A / f"geo/paradise/{ability}.geo.json", geo(f"geometry.{ability}", bones, 128, 128, (4, 4, 1)))
    write(A / "animations/paradise/angel_attacks.animation.json",
          {"format_version": "1.8.0", "animations": attack_animations()})

    atlas = make_material_sheet()
    entity_dir = A / "textures/paradise/entity"
    entity_dir.mkdir(parents=True, exist_ok=True)
    atlas.save(entity_dir / "angel_boy.png")

    emissive = Image.new("RGBA", atlas.size, (0, 0, 0, 0))
    for name in EMISSIVE_TILES:
        x, y = TILES[name]
        emissive.paste(atlas.crop((x, y, x + TILE, y + TILE)), (x, y))
    emissive.save(entity_dir / "angel_boy_emissive.png")

    for ability, painter in ATTACK_TEXTURES.items():
        painter().save(entity_dir / f"{ability}.png")

    validate_assets()
    print("angel boy assets generated and validated")


if __name__ == "__main__":
    main()
