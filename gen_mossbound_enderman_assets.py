#!/usr/bin/env python3
"""Generate the reference-faithful Mossbound Enderman model and VFX package."""

from __future__ import annotations

import json
import math
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parent
A = ROOT / "src/main/resources/assets/opusvsexe"
SHEET = ROOT / "concepts/dark_forest/mossbound-material-sheet.png"
TILES = {
    "bark": (2, 2), "plum": (66, 2), "moss": (130, 2), "vine": (194, 2),
    "root": (2, 130), "antler": (66, 130), "petal": (130, 130), "cyan": (194, 130),
}


def write(path: Path, value: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2) + "\n", encoding="utf-8")


def uv(material: str) -> dict:
    x, y = TILES[material]
    face = {"uv": [x, y], "uv_size": [60, 124]}
    return {side: dict(face) for side in ("north", "east", "south", "west", "up", "down")}


def cube(origin, size, material="bark", inflate=None) -> dict:
    value = {"origin": origin, "size": size, "uv": uv(material)}
    if inflate is not None:
        value["inflate"] = inflate
    return value


def bone(name, pivot, parent=None, cubes=None, rotation=None) -> dict:
    value = {"name": name, "pivot": pivot}
    if parent is not None:
        value["parent"] = parent
    if cubes:
        value["cubes"] = cubes
    if rotation:
        value["rotation"] = rotation
    return value


def geo(identifier: str, bones: list[dict], bounds=(7, 7, 2.8)) -> dict:
    return {"format_version": "1.12.0", "minecraft:geometry": [{
        "description": {"identifier": identifier, "texture_width": 256, "texture_height": 256,
                        "visible_bounds_width": bounds[0], "visible_bounds_height": bounds[1],
                        "visible_bounds_offset": [0, bounds[2], 0]},
        "bones": bones,
    }]}


def mx(x: float, width: float, sign: int) -> float:
    return x if sign > 0 else -x - width


def boss_geometry() -> list[dict]:
    b = [
        bone("root", [0, 0, 0]),
        bone("pelvis", [0, 30, 1], "root", [cube([-7, 24, -4], [14, 13, 9]), cube([-8, 27, -3], [4, 8, 8], "plum"), cube([4, 25, -4.5], [4, 10, 8], "plum")]),
        bone("spine_low", [0, 35, 2], "pelvis", [cube([-8, 33, -4], [16, 12, 10]), cube([-5, 35, 5], [10, 9, 3], "plum")], [-8, 0, 0]),
        bone("torso", [0, 42, 1], "spine_low", [cube([-10, 37, -6], [20, 17, 12]), cube([-11, 42, -5], [7, 12, 10], "plum"), cube([5, 39, -6.5], [6, 14, 11], "plum")], [-10, 0, 0]),
        bone("chest", [0, 49, 1], "torso", [cube([-12, 43, -6], [24, 14, 13]), cube([-13, 48, -4], [7, 10, 12], "plum"), cube([7, 46, -5], [6, 11, 12], "plum")], [-10, 0, 0]),
        bone("hump", [0, 53, 5], "chest", [cube([-13, 47, -2], [26, 14, 16]), cube([-10, 54, 10], [20, 8, 6], "plum"), cube([-14, 50, 5], [6, 10, 9], "plum")], [-16, 0, 0]),
        bone("neck", [0, 54, -4], "chest", [cube([-5, 49, -10], [10, 12, 10]), cube([-4, 54, -11], [8, 8, 5], "plum")], [22, 0, 0]),
        bone("head", [0, 56, -9], "neck", [cube([-7, 49, -16], [14, 16, 12]), cube([-6, 52, -17], [12, 11, 3], "plum"), cube([-7.5, 58, -15.5], [15, 7, 10], "plum")], [3, 0, 0]),
        bone("jaw", [0, 52, -15], "head", [cube([-5, 48, -18], [10, 6, 5]), cube([-3, 47, -17], [6, 3, 4], "plum")]),
        bone("brow_left", [4, 58, -17], "head", [cube([1, 57, -18.2], [6, 3, 2])], [0, 0, -8]),
        bone("brow_right", [-4, 58, -17], "head", [cube([-7, 57, -18.2], [6, 3, 2])], [0, 0, 8]),
        bone("eye_left", [3.5, 56, -18], "head", [cube([1.5, 54.5, -18.45], [4, 2.5, .7], "cyan")]),
        bone("eye_right", [-3.5, 56, -18], "head", [cube([-5.5, 54.5, -18.45], [4, 2.5, .7], "cyan")]),
        bone("face_moss", [0, 62, -15], "head", [cube([-7.8, 61, -16.2], [5, 2, 5], "moss"), cube([3, 63, -14], [4, 2, 5], "moss")]),
    ]

    # Broad crown of articulated, branching antlers.
    for side, sign in (("left", 1), ("right", -1)):
        ox = lambda x, w: mx(x, w, sign)
        rz = lambda a: -sign * a
        b += [
            bone(f"antler_{side}_root", [sign * 5, 62, -9], "head", [cube([ox(4.5, 4), 60, -11], [4, 10, 4], "antler"), cube([ox(5, 3), 64, -10.5], [3, 7, 3], "root")], [0, 0, rz(22)]),
            bone(f"antler_{side}_lower", [sign * 8, 69, -9], f"antler_{side}_root", [cube([ox(7, 3.5), 67, -11], [3.5, 11, 4], "antler")], [0, 0, rz(10)]),
            bone(f"antler_{side}_outer", [sign * 12, 76, -9], f"antler_{side}_lower", [cube([ox(11, 3), 74, -10.5], [3, 10, 3], "antler")], [0, 0, sign * 5]),
            bone(f"antler_{side}_crown", [sign * 17, 81, -9], f"antler_{side}_outer", [cube([ox(16, 2.5), 79, -10], [2.5, 9, 2.5], "antler")], [0, 0, sign * 8]),
            bone(f"antler_{side}_tip", [sign * 21, 85, -9], f"antler_{side}_crown", [cube([ox(20, 2), 84, -9.8], [2, 8, 2], "antler")], [0, 0, sign * 8]),
            bone(f"antler_{side}_tine_low", [sign * 9, 71, -9], f"antler_{side}_lower", [cube([ox(8, 2.5), 70, -10], [2.5, 8, 2.5], "antler")], [0, 0, sign * 42]),
            bone(f"antler_{side}_tine_mid", [sign * 14, 78, -9], f"antler_{side}_outer", [cube([ox(13, 2), 77, -9.8], [2, 8, 2], "antler")], [0, 0, sign * 38]),
            bone(f"antler_{side}_tine_high", [sign * 18, 83, -9], f"antler_{side}_crown", [cube([ox(17, 2), 82, -9.8], [2, 7, 2], "antler")], [0, 0, sign * 31]),
        ]

    # Extreme arms with palms, thumbs and eight independently rigged phalanges.
    for side, sign in (("left", 1), ("right", -1)):
        ox = lambda x, w: mx(x, w, sign)
        x = sign * 11
        b += [
            bone(f"arm_{side}_upper", [x, 50, 0], "chest", [cube([ox(9, 7), 31, -4], [7, 20, 8]), cube([ox(10, 5), 38, -4.8], [5, 11, 2], "plum"), cube([ox(8.5, 4), 43, 3], [4, 7, 3], "moss")], [0, 0, sign * 5]),
            bone(f"elbow_{side}", [x, 32, 0], f"arm_{side}_upper", [cube([ox(8.5, 6), 28, -4.5], [6, 7, 9], "plum")]),
            bone(f"arm_{side}_lower", [x, 31, 0], f"elbow_{side}", [cube([ox(9, 6), 10, -3.5], [6, 22, 7]), cube([ox(9.5, 5), 17, -4.2], [5, 11, 2], "plum")], [0, 0, -sign * 3]),
            bone(f"wrist_{side}", [x, 12, 0], f"arm_{side}_lower", [cube([ox(8.5, 7), 8, -4.5], [7, 7, 9], "plum")]),
            bone(f"hand_{side}", [x, 10, 0], f"wrist_{side}", [cube([ox(7.5, 8), 5, -6], [8, 8, 12]), cube([ox(8, 7), 7, -6.8], [7, 5, 2], "plum")]),
            bone(f"thumb_{side}", [sign * 9, 9, -5], f"hand_{side}", [cube([ox(7.5, 2.5), 3, -7], [2.5, 8, 2.5])], [-12, 0, sign * 24]),
        ]
        for i, z in enumerate((-4.8, -1.8, 1.2, 4.2), 1):
            fx = sign * (11 + (i - 2.5) * .35)
            b += [
                bone(f"finger_{side}_{i}_prox", [fx, 7, z], f"hand_{side}", [cube([ox(abs(fx) - .9, 1.8), 1.8, z - .8], [1.8, 6, 1.6])], [sign * (i - 2.5) * 2.5, 0, 0]),
                bone(f"finger_{side}_{i}_tip", [fx, 2.4, z], f"finger_{side}_{i}_prox", [cube([ox(abs(fx) - .75, 1.5), 0, z - .7], [1.5, 3.2, 1.4], "antler")], [5 + i * 2, 0, 0]),
            ]

    # Narrow legs and splayed, root-like toes.
    for side, sign in (("left", 1), ("right", -1)):
        ox = lambda x, w: mx(x, w, sign)
        hip = sign * 5
        b += [
            bone(f"leg_{side}_upper", [hip, 30, 1], "pelvis", [cube([ox(2, 7), 13, -3.5], [7, 18, 8]), cube([ox(3, 5), 20, -4], [5, 9, 2], "plum")]),
            bone(f"knee_{side}", [hip, 14, 1], f"leg_{side}_upper", [cube([ox(2, 7), 11, -4], [7, 7, 9], "plum")], [2, 0, 0]),
            bone(f"leg_{side}_lower", [hip, 13, 1], f"knee_{side}", [cube([ox(2.5, 6), 1, -3], [6, 13, 7]), cube([ox(3, 5), 4, -3.7], [5, 7, 2], "plum")], [2, 0, 0]),
            bone(f"ankle_{side}", [hip, 2, 0], f"leg_{side}_lower", [cube([ox(2, 7), 0, -4], [7, 5, 8], "plum")]),
            bone(f"foot_{side}", [hip, 1, -1], f"ankle_{side}", [cube([ox(1.5, 8), 0, -9], [8, 4, 13])]),
        ]
        for i, xoff in enumerate((-2.6, 0, 2.6), 1):
            tx = hip + xoff
            b.append(bone(f"toe_{side}_{i}", [tx, 1.5, -7], f"foot_{side}", [cube([tx - 1, 0, -12 - abs(xoff) * .3], [2, 2, 8 + abs(xoff) * .3], "root")], [0, sign * xoff * 5, 0]))

    # Surface vegetation and spiral root bindings.
    b += [
        bone("moss_shoulders", [0, 53, 3], "chest", [cube([-14, 51, -4], [11, 5, 14], "moss", .25), cube([4, 50, -5], [10, 6, 15], "moss", .25), cube([-8, 55, 6], [17, 4, 9], "moss", .2)]),
        bone("moss_back", [0, 57, 10], "hump", [cube([-10, 57, 9], [9, 4, 8], "moss", .2), cube([2, 56, 10], [8, 5, 8], "moss", .2)]),
        bone("moss_chest_left", [7, 49, -5], "chest", [cube([4, 47, -7], [7, 7, 3], "moss", .15)]),
        bone("moss_head", [0, 63, -11], "head", [cube([-6, 62, -15], [11, 3, 9], "moss", .15)]),
    ]
    for side, sign in (("left", 1), ("right", -1)):
        x = sign * 11
        for i, (y, angle) in enumerate(((26, 16), (20, -20), (14, 18)), 1):
            b.append(bone(f"root_wrap_arm_{side}_{i}", [x, y, 0], f"arm_{side}_lower", [cube([x - 4, y - 1.2, -4.6], [8, 2.4, 9.2], "root", .15)], [0, sign * angle, sign * (8 if i != 2 else -8)]))
        hip = sign * 5
        for i, (y, angle) in enumerate(((10, -18), (6, 20), (3, -14)), 1):
            b.append(bone(f"root_wrap_leg_{side}_{i}", [hip, y, 0], f"leg_{side}_lower", [cube([hip - 4, y - 1, -4.2], [8, 2.2, 8.4], "root", .12)], [0, sign * angle, sign * (7 if i != 2 else -7)]))

    vines = [
        ("vine_back_left", [-8, 56, 12], "hump", 17, 5), ("vine_back_mid", [1, 58, 14], "hump", 20, -3),
        ("vine_back_right", [9, 55, 11], "hump", 15, -6), ("vine_shoulder_left", [12, 51, 5], "chest", 22, 7),
        ("vine_shoulder_right", [-12, 51, 3], "chest", 18, -7),
    ]
    for name, pivot, parent, length, angle in vines:
        x, y, z = pivot
        b += [
            bone(name, pivot, parent, [cube([x - 1, y - length * .55, z - 1], [2, length * .6, 2], "vine")], [0, 0, angle]),
            bone(name + "_tip", [x, y - length * .52, z], name, [cube([x - .7, y - length, z - .7], [1.4, length * .52, 1.4], "vine")], [8, 0, -angle * .6]),
        ]

    # One flower: eight outer petals, four inner petals and a cyan crystal throat.
    b += [
        bone("flower_stem", [0, 59, 9], "hump", [cube([-1.3, 57, 8], [2.6, 14, 2.6], "root"), cube([-2.2, 63, 7.2], [4.4, 5, 4.4], "moss")], [-24, 0, 0]),
        bone("flower_core", [0, 70, 13], "flower_stem", [cube([-3.5, 68, 9.5], [7, 6, 7], "cyan"), cube([-2.3, 73, 10.7], [4.6, 4, 4.6], "petal")]),
    ]
    for i in range(8):
        angle = i * 45
        b.append(bone(f"flower_petal_outer_{i + 1}", [0, 71, 13], "flower_core", [cube([-2.3, 69, 12], [4.6, 2.4, 9], "petal")], [-34, angle, 0]))
    for i in range(4):
        b.append(bone(f"flower_petal_inner_{i + 1}", [0, 72, 13], "flower_core", [cube([-2, 71, 12], [4, 2, 8], "petal")], [-52, 22.5 + i * 90, 0]))
    for i, (x, h, rx, rz) in enumerate(((0, 8, 0, 0), (2.2, 6.5, -14, 18), (-2.2, 6.5, -14, -18), (0, 6, 18, 0), (0, 5.5, -20, 0)), 1):
        b.append(bone(f"flower_crystal_{i}", [0, 73, 13], "flower_core", [cube([x - 1.2, 72, 11.8], [2.4, h, 2.4], "cyan")], [rx, i * 17, rz]))
    return b


def ch(name: str, keys: dict) -> dict:
    return {name: {str(t): value for t, value in keys.items()}}


def rot(keys): return ch("rotation", keys)
def pos(keys): return ch("position", keys)
def scl(keys): return ch("scale", keys)


def channels(*values: dict) -> dict:
    out = {}
    for value in values: out.update(value)
    return out


def anim(length, bones, loop=False):
    value = {"animation_length": length, "bones": bones}
    if loop: value["loop"] = True
    return value


def sym(bones, left, right, left_keys, right_keys=None):
    bones[left] = rot(left_keys)
    if right_keys is None:
        right_keys = {t: [v[0], -v[1], -v[2]] for t, v in left_keys.items()}
    bones[right] = rot(right_keys)


def flower_pose(open_angle, length, pulse=False):
    bones = {}
    for i in range(1, 9):
        sign = -1 if i % 2 else 1
        bones[f"flower_petal_outer_{i}"] = rot({0: [18, 0, 0], .65: [-open_angle, 0, sign * 7], length - .45: [-open_angle + 3, 0, -sign * 5], length: [18, 0, 0]})
    for i in range(1, 5):
        bones[f"flower_petal_inner_{i}"] = rot({0: [28, 0, 0], .55: [-open_angle - 12, 0, 0], length - .4: [-open_angle - 8, 0, 0], length: [28, 0, 0]})
    if pulse: bones["flower_core"] = scl({0: [1, 1, 1], .65: [1.22, 1.22, 1.22], length - .5: [1.12, 1.12, 1.12], length: [1, 1, 1]})
    return bones


def boss_animations() -> dict:
    a = {}
    a["dormant"] = anim(3.6, {
        "root": pos({0: [0, -2.5, 0], 1.8: [0, -2.2, 0], 3.6: [0, -2.5, 0]}),
        "spine_low": rot({0: [20, 0, 0], 1.8: [22, 0, 0], 3.6: [20, 0, 0]}),
        "torso": rot({0: [25, 0, 0], 1.8: [27, 0, 0], 3.6: [25, 0, 0]}),
        "chest": rot({0: [18, 0, 0], 1.8: [20, 0, 0], 3.6: [18, 0, 0]}),
        "head": rot({0: [25, 0, 0], 3.6: [25, 0, 0]}),
        "arm_left_upper": rot({0: [14, 0, 8], 3.6: [14, 0, 8]}), "arm_right_upper": rot({0: [14, 0, -8], 3.6: [14, 0, -8]}),
        "flower_core": scl({0: [.65, .65, .65], 3.6: [.65, .65, .65]}),
    }, True)

    awaken = {
        "root": channels(pos({0: [0, -2.5, 0], .5: [0, -3.2, 0], 1: [0, .6, 0], 1.6: [0, 0, 0]}), rot({0: [0, 0, 0], .72: [0, 0, -3], .9: [0, 0, 3], 1.6: [0, 0, 0]})),
        "spine_low": rot({0: [20, 0, 0], .55: [28, 0, 0], 1.05: [-8, 0, 0], 1.6: [0, 0, 0]}),
        "torso": rot({0: [25, 0, 0], .55: [34, 0, 0], 1.05: [-18, 0, 0], 1.6: [0, 0, 0]}),
        "chest": rot({0: [18, 0, 0], .65: [28, 0, 0], 1.05: [-12, 0, 0], 1.6: [0, 0, 0]}),
        "head": rot({0: [25, 0, 0], .58: [38, 0, 0], .92: [-32, 0, 0], 1.25: [8, 0, 0], 1.6: [0, 0, 0]}),
        "jaw": rot({0: [0, 0, 0], .8: [24, 0, 0], 1.25: [8, 0, 0], 1.6: [0, 0, 0]}),
        "moss_shoulders": scl({0: [.9, .9, .9], .95: [1.16, 1.16, 1.16], 1.6: [1, 1, 1]}),
        "flower_core": scl({0: [.65, .65, .65], .9: [1.25, 1.25, 1.25], 1.6: [1, 1, 1]}),
    }
    sym(awaken, "arm_left_upper", "arm_right_upper", {0: [14, 0, 8], .55: [30, -8, 18], 1.02: [-52, 12, -24], 1.6: [0, 0, 0]}, {0: [14, 0, -8], .55: [30, 8, -18], 1.02: [-52, -12, 24], 1.6: [0, 0, 0]})
    a["awaken"] = anim(1.6, awaken)

    idle = {
        "root": channels(pos({0: [0, 0, 0], 1.5: [0, .25, 0], 3: [0, 0, 0]}), rot({0: [0, -1.5, 0], 1.5: [0, 1.5, 0], 3: [0, -1.5, 0]})),
        "spine_low": rot({0: [0, 0, -1], 1.5: [-2, 0, 1], 3: [0, 0, -1]}),
        "chest": channels(rot({0: [0, -1, 0], 1.5: [-2.5, 1, 0], 3: [0, -1, 0]}), scl({0: [1, 1, 1], 1.5: [1.015, 1.025, 1.015], 3: [1, 1, 1]})),
        "head": rot({0: [1, -5, 0], .75: [-1, 0, 0], 1.5: [0, 6, 0], 2.25: [-1, 0, 0], 3: [1, -5, 0]}),
        "jaw": rot({0: [0, 0, 0], 1.5: [3, 0, 0], 3: [0, 0, 0]}),
        "flower_core": channels(rot({0: [0, -3, -2], 1.5: [2, 3, 2], 3: [0, -3, -2]}), scl({0: [1, 1, 1], 1.5: [1.06, 1.06, 1.06], 3: [1, 1, 1]})),
    }
    sym(idle, "arm_left_upper", "arm_right_upper", {0: [1, 0, 1], 1.5: [-2, 0, -1], 3: [1, 0, 1]})
    sym(idle, "arm_left_lower", "arm_right_lower", {0: [0, 0, -1], 1.5: [2, 0, 1], 3: [0, 0, -1]})
    for name in ("vine_back_left", "vine_back_mid", "vine_back_right", "vine_shoulder_left", "vine_shoulder_right"):
        idle[name] = rot({0: [0, 0, -3], 1.5: [2, 0, 4], 3: [0, 0, -3]}); idle[name + "_tip"] = rot({0: [0, 0, 4], 1.5: [-3, 0, -5], 3: [0, 0, 4]})
    a["idle"] = anim(3, idle, True)

    walk = {
        "root": channels(pos({0: [0, 0, 0], .32: [0, .55, 0], .64: [0, 0, 0], .96: [0, .55, 0], 1.28: [0, 0, 0]}), rot({0: [0, 0, -2], .64: [0, 0, 2], 1.28: [0, 0, -2]})),
        "pelvis": rot({0: [0, -5, 0], .64: [0, 5, 0], 1.28: [0, -5, 0]}), "chest": rot({0: [1, 4, 2], .64: [-2, -4, -2], 1.28: [1, 4, 2]}),
        "head": rot({0: [-2, -3, 1], .32: [4, 0, 0], .64: [-2, 3, -1], .96: [4, 0, 0], 1.28: [-2, -3, 1]}),
        "leg_left_upper": rot({0: [28, 0, 0], .64: [-26, 0, 0], 1.28: [28, 0, 0]}), "leg_right_upper": rot({0: [-26, 0, 0], .64: [28, 0, 0], 1.28: [-26, 0, 0]}),
        "knee_left": rot({0: [4, 0, 0], .32: [22, 0, 0], .64: [7, 0, 0], 1.28: [4, 0, 0]}), "knee_right": rot({0: [7, 0, 0], .64: [4, 0, 0], .96: [22, 0, 0], 1.28: [7, 0, 0]}),
        "foot_left": rot({0: [-8, 0, 0], .32: [13, 0, 0], .64: [-6, 0, 0], 1.28: [-8, 0, 0]}), "foot_right": rot({0: [-6, 0, 0], .64: [-8, 0, 0], .96: [13, 0, 0], 1.28: [-6, 0, 0]}),
        "arm_left_upper": rot({0: [-19, 0, 3], .64: [17, 0, -2], 1.28: [-19, 0, 3]}), "arm_right_upper": rot({0: [17, 0, -3], .64: [-19, 0, 2], 1.28: [17, 0, -3]}),
        "arm_left_lower": rot({0: [7, 0, 0], .64: [-6, 0, 0], 1.28: [7, 0, 0]}), "arm_right_lower": rot({0: [-6, 0, 0], .64: [7, 0, 0], 1.28: [-6, 0, 0]}),
    }
    for name in ("vine_back_left", "vine_back_mid", "vine_back_right", "vine_shoulder_left", "vine_shoulder_right"):
        walk[name] = rot({0: [-9, 0, -4], .64: [8, 0, 4], 1.28: [-9, 0, -4]}); walk[name + "_tip"] = rot({0: [11, 0, 5], .64: [-10, 0, -5], 1.28: [11, 0, 5]})
    a["walk"] = anim(1.28, walk, True)

    sweep = {
        "root": rot({0: [0, 0, 0], .72: [0, -18, -5], 1.02: [0, -31, -7], 1.1: [0, 54, 8], 1.42: [0, 66, 4], 2.3: [0, 0, 0]}),
        "pelvis": rot({0: [0, 0, 0], .72: [0, -18, 0], 1.02: [0, -28, 0], 1.1: [0, 46, 0], 1.42: [0, 55, 0], 2.3: [0, 0, 0]}),
        "torso": rot({0: [0, 0, 0], .72: [-8, -30, -5], 1.02: [-12, -48, -8], 1.1: [10, 72, 9], 1.42: [6, 82, 5], 2.3: [0, 0, 0]}),
        "chest": rot({0: [0, 0, 0], .72: [-10, -25, 0], 1.02: [-16, -38, 0], 1.1: [13, 58, 0], 1.42: [7, 65, 0], 2.3: [0, 0, 0]}),
        "head": rot({0: [0, 0, 0], .72: [12, -18, -5], 1.02: [18, -30, -8], 1.1: [-18, 42, 10], 1.42: [-10, 50, 5], 2.3: [0, 0, 0]}),
        "arm_left_upper": rot({0: [0, 0, 0], .8: [20, -18, 22], 1.1: [-32, 42, -26], 2.3: [0, 0, 0]}), "arm_right_upper": rot({0: [0, 0, 0], .8: [20, 18, -22], 1.1: [-32, -42, 26], 2.3: [0, 0, 0]}),
    }
    for side, sign in (("left", 1), ("right", -1)):
        sweep[f"antler_{side}_root"] = rot({0: [0, 0, 0], .82: [8, -sign * 10, sign * 5], 1.1: [-10, sign * 18, -sign * 8], 2.3: [0, 0, 0]})
        sweep[f"antler_{side}_outer"] = rot({0: [0, 0, 0], 1.02: [0, 0, sign * 7], 1.18: [0, 0, -sign * 9], 2.3: [0, 0, 0]})
    a["antler_sweep"] = anim(2.3, sweep)

    root_cast = {
        "root": pos({0: [0, 0, 0], .42: [0, .4, 0], .6: [0, -1.1, 0], .86: [0, -.4, 0], 2.6: [0, 0, 0]}),
        "spine_low": rot({0: [0, 0, 0], .42: [-12, 0, 0], .6: [18, 0, 0], 1.35: [12, 0, 0], 2.6: [0, 0, 0]}),
        "chest": rot({0: [0, 0, 0], .42: [-18, 0, 0], .6: [28, 0, 0], 1.35: [18, 0, 0], 2.6: [0, 0, 0]}), "head": rot({0: [0, 0, 0], .45: [-12, 0, 0], .6: [20, 0, 0], 1.7: [14, 0, 0], 2.6: [0, 0, 0]}),
    }
    sym(root_cast, "arm_left_upper", "arm_right_upper", {0: [0, 0, 0], .42: [-78, -10, -22], .6: [36, 0, 16], 1.2: [58, 8, 12], 2.6: [0, 0, 0]}, {0: [0, 0, 0], .42: [-78, 10, 22], .6: [36, 0, -16], 1.2: [58, -8, -12], 2.6: [0, 0, 0]})
    sym(root_cast, "arm_left_lower", "arm_right_lower", {0: [0, 0, 0], .42: [-40, 0, -8], .6: [56, 0, 8], 1.2: [36, 0, 4], 2.6: [0, 0, 0]})
    for side in ("left", "right"):
        for i in range(1, 5): root_cast[f"finger_{side}_{i}_prox"] = rot({0: [0, 0, 0], .5: [-20 - i * 3, 0, 0], .68: [18 + i * 2, 0, 0], 1.4: [8, 0, 0], 2.6: [0, 0, 0]})
    a["root_cast"] = anim(2.6, root_cast)

    a["marked_step"] = anim(2.4, {
        "root": channels(pos({0: [0, 0, 0], .45: [0, .5, 0], .72: [0, .8, 0], 1.25: [0, .8, 0], 1.4: [0, -.6, 0], 1.65: [0, .25, 0], 2.4: [0, 0, 0]}), scl({0: [1, 1, 1], .62: [1.08, .92, 1.08], .78: [.12, 1.25, .12], 1.3: [.12, 1.25, .12], 1.42: [1.28, .78, 1.28], 1.65: [.92, 1.06, .92], 2.4: [1, 1, 1]})),
        "torso": rot({0: [0, 0, 0], .62: [-14, 18, 0], 1.3: [-14, 18, 0], 1.42: [12, -15, 0], 2.4: [0, 0, 0]}), "head": rot({0: [0, 0, 0], .62: [-8, -24, 0], 1.3: [-8, -24, 0], 1.42: [15, 18, 0], 2.4: [0, 0, 0]}),
        "flower_core": scl({0: [1, 1, 1], .7: [1.35, 1.35, 1.35], 1.38: [.65, .65, .65], 1.55: [1.18, 1.18, 1.18], 2.4: [1, 1, 1]}),
    })

    orb = {"chest": rot({0: [0, 0, 0], .5: [-13, 0, 0], .8: [-18, 0, 0], 1.2: [-9, 0, 0], 2.4: [0, 0, 0]}), "head": rot({0: [0, 0, 0], .5: [-16, 0, 0], .8: [9, 0, 0], 1.25: [4, 0, 0], 2.4: [0, 0, 0]}), "flower_core": scl({0: [1, 1, 1], .55: [1.28, 1.28, 1.28], .8: [1.5, 1.5, 1.5], 1.1: [1.05, 1.05, 1.05], 2.4: [1, 1, 1]})}
    sym(orb, "arm_left_upper", "arm_right_upper", {0: [0, 0, 0], .5: [-58, -18, -22], .8: [-82, -28, -34], 1: [-35, 22, 24], 2.4: [0, 0, 0]}, {0: [0, 0, 0], .5: [-58, 18, 22], .8: [-82, 28, 34], 1: [-35, -22, -24], 2.4: [0, 0, 0]})
    sym(orb, "arm_left_lower", "arm_right_lower", {0: [0, 0, 0], .5: [-48, 0, -12], .8: [-72, 0, -18], 1: [30, 0, 15], 2.4: [0, 0, 0]})
    for side in ("left", "right"):
        for i in range(1, 5): orb[f"finger_{side}_{i}_prox"] = rot({0: [0, 0, 0], .55: [-18 - i * 4, 0, 0], .8: [-28 - i * 3, 0, 0], 1.1: [12, 0, 0], 2.4: [0, 0, 0]})
    a["orb_cast"] = anim(2.4, orb)

    bloom = {"root": channels(pos({0: [0, 0, 0], .6: [0, .65, 0], 1.15: [0, -.45, 0], 2.2: [0, .3, 0], 2.9: [0, 0, 0]}), rot({0: [0, 0, 0], .6: [0, 0, -3], 1.15: [0, 0, 3], 2.9: [0, 0, 0]})), "spine_low": rot({0: [0, 0, 0], .6: [-18, 0, 0], 1.1: [10, 0, 0], 2.2: [-8, 0, 0], 2.9: [0, 0, 0]}), "chest": rot({0: [0, 0, 0], .6: [-28, 0, 0], 1.1: [14, 0, 0], 2.2: [-12, 0, 0], 2.9: [0, 0, 0]}), "head": rot({0: [0, 0, 0], .6: [-20, 0, 0], 1.1: [18, 0, 0], 2.2: [-9, 0, 0], 2.9: [0, 0, 0]})}
    bloom.update(flower_pose(48, 2.9, True)); a["bloomfall"] = anim(2.9, bloom)

    echo = {"root": channels(rot({0: [0, 0, 0], .28: [0, -10, -3], .6: [0, 10, 3], .92: [0, -14, -4], 1.25: [0, 14, 4], 1.7: [0, 0, 0], 2.7: [0, 0, 0]}), scl({0: [1, 1, 1], .56: [1.06, 1.06, 1.06], .62: [.94, .94, .94], 1.7: [1.08, 1.08, 1.08], 1.82: [1, 1, 1], 2.7: [1, 1, 1]})), "head": rot({0: [0, -36, 0], .28: [0, 40, 0], .6: [0, -44, 0], .92: [0, 46, 0], 1.25: [0, -30, 0], 1.7: [-12, 0, 0], 2.7: [0, 0, 0]}), "jaw": rot({0: [0, 0, 0], .6: [18, 0, 0], 1.25: [6, 0, 0], 2.7: [0, 0, 0]}), "flower_core": scl({0: [1, 1, 1], .6: [1.4, 1.4, 1.4], 1.7: [1.18, 1.18, 1.18], 2.7: [1, 1, 1]})}
    sym(echo, "arm_left_upper", "arm_right_upper", {0: [0, 0, 0], .6: [-44, -18, -22], 1.25: [-64, 24, 30], 1.7: [18, -8, -10], 2.7: [0, 0, 0]}, {0: [0, 0, 0], .6: [-44, 18, 22], 1.25: [-64, -24, -30], 1.7: [18, 8, 10], 2.7: [0, 0, 0]}); a["echo_double"] = anim(2.7, echo)

    rush = {"root": channels(pos({0: [0, 0, 0], .4: [0, -.8, 1.5], .95: [0, -.9, 3], 1.3: [0, .25, -1], 1.65: [0, 0, 0], 2.6: [0, 0, 0]}), rot({0: [0, 0, 0], .4: [9, 0, 0], .95: [-14, 0, 0], 1.3: [8, 0, 0], 2.6: [0, 0, 0]})), "spine_low": rot({0: [0, 0, 0], .4: [20, 0, 0], .95: [-31, 0, 0], 1.3: [12, 0, 0], 2.6: [0, 0, 0]}), "torso": rot({0: [0, 0, 0], .4: [28, 0, 0], .95: [-42, 0, 0], 1.3: [18, 0, 0], 2.6: [0, 0, 0]}), "chest": rot({0: [0, 0, 0], .4: [24, 0, 0], .95: [-38, 0, 0], 1.3: [16, 0, 0], 2.6: [0, 0, 0]}), "head": rot({0: [0, 0, 0], .4: [18, 0, 0], .95: [-28, 0, 0], 1.3: [10, 0, 0], 2.6: [0, 0, 0]}), "arm_left_upper": rot({0: [0, 0, 0], .4: [-34, -12, -28], .95: [42, 8, 18], 1.3: [-16, 0, -8], 2.6: [0, 0, 0]}), "arm_right_upper": rot({0: [0, 0, 0], .4: [-34, 12, 28], .95: [42, -8, -18], 1.3: [-16, 0, 8], 2.6: [0, 0, 0]})}
    for side, sign in (("left", 1), ("right", -1)):
        rush[f"antler_{side}_root"] = rot({0: [0, 0, 0], .4: [-18, 0, sign * 10], .95: [24, 0, -sign * 12], 1.4: [0, 0, 0], 2.6: [0, 0, 0]})
        rush[f"antler_{side}_lower"] = rot({0: [0, 0, 0], .4: [-12, 0, sign * 7], .95: [18, 0, -sign * 9], 1.4: [0, 0, 0], 2.6: [0, 0, 0]})
    a["eclipse_rush"] = anim(2.6, rush)

    phase = {"root": channels(pos({0: [0, 0, 0], .42: [0, -.8, 0], .78: [0, 1.1, 0], 1.18: [0, -.45, 0], 1.8: [0, 0, 0]}), scl({0: [1, 1, 1], .42: [.88, 1.12, .88], .78: [1.2, 1.2, 1.2], 1.18: [.95, .95, .95], 1.8: [1, 1, 1]})), "torso": rot({0: [0, 0, 0], .42: [24, 0, 0], .78: [-24, 0, 0], 1.18: [14, 0, 0], 1.8: [0, 0, 0]}), "head": rot({0: [0, 0, 0], .42: [30, 0, 0], .78: [-34, 0, 0], 1.18: [18, 0, 0], 1.8: [0, 0, 0]}), "jaw": rot({0: [0, 0, 0], .6: [25, 0, 0], 1.25: [8, 0, 0], 1.8: [0, 0, 0]}), "flower_core": scl({0: [1, 1, 1], .42: [.8, .8, .8], .78: [1.65, 1.65, 1.65], 1.18: [1.15, 1.15, 1.15], 1.8: [1, 1, 1]})}
    sym(phase, "arm_left_upper", "arm_right_upper", {0: [0, 0, 0], .42: [22, 0, 18], .78: [-54, -18, -34], 1.18: [18, 0, 12], 1.8: [0, 0, 0]}, {0: [0, 0, 0], .42: [22, 0, -18], .78: [-54, 18, 34], 1.18: [18, 0, -12], 1.8: [0, 0, 0]}); phase.update(flower_pose(38, 1.8)); a["phase_shift"] = anim(1.8, phase)

    flower = {"spine_low": rot({0: [0, 0, 0], .6: [-12, 0, 0], 1.2: [-8, 0, 0], 2.6: [-10, 0, 0], 3.2: [0, 0, 0]}), "chest": rot({0: [0, 0, 0], .6: [-20, 0, 0], 1.2: [-14, 0, 0], 2.6: [-17, 0, 0], 3.2: [0, 0, 0]}), "head": rot({0: [0, 0, 0], .6: [14, 0, 0], 1.2: [8, -6, 0], 2.6: [10, 6, 0], 3.2: [0, 0, 0]}), "flower_stem": rot({0: [0, 0, 0], .6: [-8, 0, 0], 1.2: [-4, -4, 0], 2.6: [-6, 4, 0], 3.2: [0, 0, 0]}), "flower_core": channels(scl({0: [.75, .75, .75], .55: [1.42, 1.42, 1.42], 1.2: [1.24, 1.24, 1.24], 2.6: [1.36, 1.36, 1.36], 3.2: [.75, .75, .75]}), rot({0: [0, 0, 0], 1.2: [0, -5, 0], 2.6: [0, 5, 0], 3.2: [0, 0, 0]}))}
    for i in range(1, 9):
        sign = -1 if i % 2 else 1; flower[f"flower_petal_outer_{i}"] = rot({0: [28, 0, 0], .55: [-58, 0, sign * 9], 1.2: [-52, 0, -sign * 6], 2.6: [-60, 0, sign * 7], 3.2: [28, 0, 0]})
    for i in range(1, 5): flower[f"flower_petal_inner_{i}"] = rot({0: [34, 0, 0], .48: [-70, 0, 0], 1.2: [-62, 0, 0], 2.6: [-68, 0, 0], 3.2: [34, 0, 0]})
    for i in range(1, 6): flower[f"flower_crystal_{i}"] = scl({0: [.65, .65, .65], .6: [1.3, 1.3, 1.3], 1.2: [1.05, 1.12, 1.05], 2.6: [1.2, 1.28, 1.2], 3.2: [.65, .65, .65]})
    a["flower_open"] = anim(3.2, flower, True)

    a["hurt"] = anim(.45, {"root": rot({0: [0, 0, 0], .08: [6, 0, 4], .2: [-4, 0, -3], .45: [0, 0, 0]}), "torso": rot({0: [0, 0, 0], .08: [14, 8, 5], .2: [-6, -4, -3], .45: [0, 0, 0]}), "head": rot({0: [0, 0, 0], .08: [-18, -10, -7], .2: [8, 5, 4], .45: [0, 0, 0]}), "flower_core": scl({0: [1, 1, 1], .08: [.8, .8, .8], .2: [1.12, 1.12, 1.12], .45: [1, 1, 1]})})

    death = {"root": channels(pos({0: [0, 0, 0], .65: [0, -.6, 0], 1.25: [0, -.4, 0], 2.15: [0, -2.5, 0], 3: [0, -3, 0]}), rot({0: [0, 0, 0], .65: [0, 0, -4], 1.25: [0, 0, 12], 2.15: [0, 0, 76], 2.65: [0, 0, 88], 3: [0, 0, 90]})), "spine_low": rot({0: [0, 0, 0], .65: [20, 0, 0], 1.25: [-18, 0, 0], 2.15: [12, 0, 0], 3: [15, 0, 0]}), "torso": rot({0: [0, 0, 0], .65: [28, 0, 0], 1.25: [-26, 0, 0], 2.15: [18, 0, 0], 3: [22, 0, 0]}), "head": rot({0: [0, 0, 0], .65: [-24, 0, 0], 1.25: [32, 0, 0], 2.15: [-10, 0, 0], 3: [-14, 0, 0]}), "jaw": rot({0: [0, 0, 0], .65: [22, 0, 0], 1.25: [9, 0, 0], 3: [14, 0, 0]}), "arm_left_upper": rot({0: [0, 0, 0], 1.25: [-34, -8, -20], 2.15: [18, 0, 16], 3: [22, 0, 20]}), "arm_right_upper": rot({0: [0, 0, 0], 1.25: [-34, 8, 20], 2.15: [18, 0, -16], 3: [22, 0, -20]}), "flower_core": scl({0: [1, 1, 1], 1.25: [1.55, 1.55, 1.55], 2.15: [.85, .85, .85], 2.65: [.3, .3, .3], 3: [0, 0, 0]})}
    for name in ("vine_back_left", "vine_back_mid", "vine_back_right", "vine_shoulder_left", "vine_shoulder_right"): death[name] = rot({0: [0, 0, 0], 1.25: [-12, 0, -10], 2.15: [24, 0, 18], 3: [28, 0, 22]})
    a["death"] = anim(3, death)
    return {"format_version": "1.8.0", "animations": a}


def attack_geometry() -> dict[str, list[dict]]:
    attacks = {}
    roots = [bone("root", [0, 0, 0]), bone("root_core", [0, 1, 0], "root", [cube([-3, 0, -3], [6, 4, 6], "cyan")])]
    for i in range(8): roots.append(bone(f"root_tendril_{i + 1}", [0, 1, 0], "root", [cube([-1.6, 0, -3], [3.2, 3, 21], "root"), cube([-.9, 2, 14], [1.8, 8, 7], "vine")], [0, i * 45, 0]))
    attacks["root"] = roots

    step = [bone("root", [0, 0, 0]), bone("step_core", [0, 1, 0], "root", [cube([-4, 0, -4], [8, 2, 8], "cyan")])]
    for i in range(12): step.append(bone(f"step_ring_{i + 1}", [0, 0, 0], "root", [cube([-1.1, 0, -20], [2.2, 1.4, 8], "cyan")], [0, i * 30, 0]))
    for i in range(6):
        angle = i * 60; rad = math.radians(angle); x, z = math.sin(rad) * 14, math.cos(rad) * 14
        step.append(bone(f"step_shard_{i + 1}", [x, 0, z], "root", [cube([x - 1.2, 0, z - 1.2], [2.4, 10, 2.4], "antler")], [-12, angle, 0]))
    attacks["step"] = step

    attacks["orb"] = [bone("root", [0, 4, 0]), bone("orb_core", [0, 4, 0], "root", [cube([-4, 0, -4], [8, 8, 8], "cyan"), cube([-2.5, 1.5, -6], [5, 5, 12], "petal")]), bone("orb_ring_x", [0, 4, 0], "root", [cube([-8, 3, -1], [16, 2, 2], "cyan")], [0, 0, 35]), bone("orb_ring_z", [0, 4, 0], "root", [cube([-1, 3, -8], [2, 2, 16], "cyan")], [35, 0, 0]), bone("orb_wisp", [0, 4, 0], "root", [cube([-1, 4, -1], [2, 8, 2], "cyan")])]

    bloom = [bone("root", [0, 0, 0]), bone("bloom_stem", [0, 1, 0], "root", [cube([-1, 0, -1], [2, 10, 2], "vine")]), bone("bloom_core", [0, 4, 0], "bloom_stem", [cube([-3, 1, -3], [6, 5, 6], "cyan")])]
    for i in range(8): bloom.append(bone(f"bloom_petal_{i + 1}", [0, 4, 0], "bloom_core", [cube([-2.5, 3, -1], [5, 1.8, 14], "petal")], [-25, i * 45, 0]))
    for i in range(4): bloom.append(bone(f"bloom_spike_{i + 1}", [0, 4, 0], "bloom_core", [cube([-.8, 3, -.8], [1.6, 12, 1.6], "cyan")], [-18, i * 90, 22]))
    attacks["bloom"] = bloom

    echo = [bone("root", [0, 0, 0]), bone("echo_pelvis", [0, 23, 0], "root", [cube([-5, 18, -3], [10, 10, 6], "plum")]), bone("echo_torso", [0, 30, 0], "echo_pelvis", [cube([-7, 26, -4], [14, 15, 8])], [-14, 0, 0]), bone("echo_head", [0, 40, -4], "echo_torso", [cube([-5, 35, -9], [10, 11, 8], "plum"), cube([-3.8, 39, -9.4], [2.4, 2, .6], "cyan"), cube([1.4, 39, -9.4], [2.4, 2, .6], "cyan")])]
    for side, sign in (("left", 1), ("right", -1)):
        echo += [bone(f"echo_arm_{side}", [sign * 7, 35, 0], "echo_torso", [cube([sign * 7 - (0 if sign > 0 else 4), 7, -2], [4, 29, 4])], [0, 0, sign * 7]), bone(f"echo_leg_{side}", [sign * 3, 22, 0], "echo_pelvis", [cube([sign * 3 - (0 if sign > 0 else 4), 0, -2], [4, 23, 4])]), bone(f"echo_antler_{side}", [sign * 4, 45, -4], "echo_head", [cube([sign * 4 - (0 if sign > 0 else 2), 44, -5], [2, 13, 2], "antler")], [0, 0, -sign * 32])]
    attacks["echo"] = echo

    attacks["rush"] = [bone("root", [0, 8, 0]), bone("rush_core", [0, 8, 0], "root", [cube([-4, 4, -4], [8, 8, 8], "cyan")]), bone("rush_lance", [0, 8, 0], "root", [cube([-2, 6, -26], [4, 4, 52], "antler")]), bone("rush_wake_left", [0, 8, 0], "root", [cube([3, 5, -19], [3, 6, 38], "cyan")], [0, -24, 0]), bone("rush_wake_right", [0, 8, 0], "root", [cube([-6, 5, -19], [3, 6, 38], "cyan")], [0, 24, 0]), bone("rush_horn_left", [0, 8, -9], "root", [cube([-2, 7, -25], [3, 3, 22], "antler")], [0, -35, -25]), bone("rush_horn_right", [0, 8, -9], "root", [cube([-1, 7, -25], [3, 3, 22], "antler")], [0, 35, 25])]
    return attacks


def attack_animations() -> dict:
    root_snare = {"root": scl({0: [.08, .08, .08], .28: [.35, .15, .35], .85: [1, .35, 1], 1.18: [1.08, 1.65, 1.08], 1.42: [.96, .82, .96], 1.7: [.72, .18, .72]}), "root_core": channels(scl({0: [.3, .3, .3], .85: [.8, .8, .8], 1.18: [1.75, 1.75, 1.75], 1.7: [0, 0, 0]}), rot({0: [0, 0, 0], 1.7: [0, 360, 0]}))}
    for i in range(1, 9): root_snare[f"root_tendril_{i}"] = rot({0: [0, 0, -28], .55: [0, 0, 0], .98: [-18, 0, 0], 1.18: [-78, 0, 0], 1.42: [-48, 0, 0], 1.7: [18, 0, 0]})

    step = {"root": channels(rot({0: [0, 0, 0], 1.55: [0, 360, 0]}), scl({0: [.3, .15, .3], .35: [1, .35, 1], 1.05: [1.12, .4, 1.12], 1.2: [1.45, 1.45, 1.45], 1.42: [.72, .12, .72], 1.55: [0, 0, 0]})), "step_core": scl({0: [.3, .3, .3], 1.05: [1.1, .4, 1.1], 1.2: [2, 2, 2], 1.55: [0, 0, 0]})}
    for i in range(1, 13): step[f"step_ring_{i}"] = rot({0: [0, 0, -18], .35: [0, 0, 0], 1.05: [0, 0, 8], 1.2: [-58, 0, -6], 1.55: [-85, 0, 0]})
    for i in range(1, 7): step[f"step_shard_{i}"] = channels(pos({0: [0, -8, 0], .4: [0, 0, 0], 1.05: [0, 2, 0], 1.2: [0, 13, 0], 1.55: [0, -8, 0]}), scl({0: [.2, .2, .2], .4: [1, 1, 1], 1.2: [1.25, 1.6, 1.25], 1.55: [0, 0, 0]}))

    orb = {"root": channels(rot({0: [0, 0, 0], .8: [0, 360, 0]}), scl({0: [.88, .88, .88], .4: [1.12, 1.12, 1.12], .8: [.88, .88, .88]})), "orb_core": rot({0: [0, 0, 0], .8: [360, -360, 180]}), "orb_ring_x": rot({0: [0, 0, 0], .8: [0, 360, 0]}), "orb_ring_z": rot({0: [0, 0, 0], .8: [360, 0, 0]}), "orb_wisp": channels(pos({0: [0, -5, 0], .4: [0, 4, 0], .8: [0, -5, 0]}), scl({0: [.4, .4, .4], .4: [1, 1.3, 1], .8: [.4, .4, .4]}))}

    bloom = {"root": scl({0: [.15, .15, .15], .45: [.7, .35, .7], 1.35: [1, .7, 1], 1.6: [1.5, 1.5, 1.5], 1.88: [1.05, .7, 1.05], 2.1: [0, 0, 0]}), "bloom_stem": scl({0: [.25, .25, .25], 1.15: [.8, .8, .8], 1.6: [1.35, 1.7, 1.35], 2.1: [0, 0, 0]}), "bloom_core": channels(rot({0: [0, 0, 0], 2.1: [0, 360, 0]}), scl({0: [.3, .3, .3], 1.35: [.7, .7, .7], 1.6: [1.8, 1.8, 1.8], 2.1: [0, 0, 0]}))}
    for i in range(1, 9): bloom[f"bloom_petal_{i}"] = rot({0: [68, 0, 0], 1.3: [42, 0, 0], 1.6: [-70, 0, 0], 1.86: [-48, 0, 0], 2.1: [70, 0, 0]})
    for i in range(1, 5): bloom[f"bloom_spike_{i}"] = scl({0: [.2, .2, .2], 1.35: [.6, .6, .6], 1.6: [1.5, 1.8, 1.5], 2.1: [0, 0, 0]})

    echo = {"root": channels(pos({0: [0, -2, 0], .28: [0, 0, 0], .72: [0, .5, 0], 1.1: [0, 0, 0], 1.6: [0, -2, 0]}), rot({0: [0, -18, -4], .32: [0, 20, 4], .64: [0, -16, -4], .96: [0, 18, 4], 1.28: [0, -10, -2], 1.6: [0, 0, 0]}), scl({0: [.15, .4, .15], .28: [1, 1, 1], 1.1: [1.08, 1.08, 1.08], 1.28: [1.35, 1.35, 1.35], 1.6: [0, 0, 0]})), "echo_head": rot({0: [0, -28, 0], .4: [0, 30, 0], .8: [0, -32, 0], 1.2: [-20, 0, 0], 1.6: [0, 0, 0]}), "echo_arm_left": rot({0: [20, 0, 12], .8: [-30, -10, -18], 1.2: [-62, 18, -30], 1.6: [0, 0, 0]}), "echo_arm_right": rot({0: [20, 0, -12], .8: [-30, 10, 18], 1.2: [-62, -18, 30], 1.6: [0, 0, 0]})}

    rush = {"root": channels(pos({0: [0, 0, 0], .9: [0, 0, -2], 1.25: [0, 0, 2], 2.1: [0, 0, 7]}), rot({0: [0, 0, 0], 2.1: [0, 720, 0]}), scl({0: [.18, .18, .18], .32: [.7, .7, .7], .9: [1, 1, 1], 1.25: [1.45, 1.45, 1.45], 2.1: [.8, .8, .8]})), "rush_core": channels(rot({0: [0, 0, 0], 2.1: [360, -720, 0]}), scl({0: [.5, .5, .5], .9: [1, 1, 1], 1.25: [1.8, 1.8, 1.8], 2.1: [.7, .7, .7]})), "rush_lance": scl({0: [.2, .2, .2], .9: [1, 1, 1], 1.25: [1.15, 1.15, 1.6], 2.1: [.6, .6, 1.2]}), "rush_wake_left": rot({0: [0, 0, 22], .9: [0, 0, -10], 1.25: [0, 0, 28], 2.1: [0, 0, 10]}), "rush_wake_right": rot({0: [0, 0, -22], .9: [0, 0, 10], 1.25: [0, 0, -28], 2.1: [0, 0, -10]}), "rush_horn_left": rot({0: [0, -18, -16], .9: [0, 12, 10], 1.25: [-12, -24, -28], 2.1: [0, 0, 0]}), "rush_horn_right": rot({0: [0, 18, 16], .9: [0, -12, -10], 1.25: [-12, 24, 28], 2.1: [0, 0, 0]})}

    return {"format_version": "1.8.0", "animations": {
        "root_snare": anim(1.7, root_snare), "marked_step": anim(1.55, step), "moonwell_orb": anim(.8, orb, True),
        "bloomfall_effect": anim(2.1, bloom), "echo_double_effect": anim(1.6, echo), "eclipse_rush_effect": anim(2.1, rush),
    }}


def make_textures() -> None:
    if not SHEET.exists(): raise FileNotFoundError(f"Missing generated material source: {SHEET}")
    source = Image.open(SHEET).convert("RGB")
    atlas = source.resize((128, 128), Image.Resampling.NEAREST)
    atlas = atlas.quantize(colors=48, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE).convert("RGBA")
    atlas = atlas.resize((256, 256), Image.Resampling.NEAREST)
    target = A / "textures/dark_forest/entity/mossbound_enderman.png"; target.parent.mkdir(parents=True, exist_ok=True); atlas.save(target)
    # Pre-baked emissive overlay (bright crystal art on transparent background).
    # Rendered by EmissiveGlowGeoLayer with RenderType.entityTranslucentEmissive,
    # so GeckoLib's runtime glowmask merging (which zeroes base-texture pixels
    # and can leave an uninitialised/black glow texture) is never involved.
    emissive = Image.new("RGBA", atlas.size, (0, 0, 0, 0)); emissive.paste(atlas.crop((192, 128, 256, 256)), (192, 128)); emissive.save(target.with_name("mossbound_enderman_emissive.png"))

    attack_names = ("root", "step", "orb", "bloom", "echo", "rush")
    out = A / "textures/dark_forest/attacks"; out.mkdir(parents=True, exist_ok=True)
    for name in attack_names:
        # Ability geometry shares the material layout of the boss, so every
        # effect stays visually related while selecting its own subset of it.
        image = atlas.copy()
        if name == "echo": image.putalpha(Image.new("L", image.size, 176))
        image.save(out / f"{name}.png")
        ability_emissive = Image.new("RGBA", atlas.size, (0, 0, 0, 0))
        ability_emissive.paste(atlas.crop((192, 128, 256, 256)), (192, 128))
        ability_emissive.save(out / f"{name}_emissive.png")


def validate_assets() -> None:
    """Fail regeneration immediately on a broken hierarchy or clip contract."""
    model = json.loads((A / "geo/dark_forest/mossbound_enderman.geo.json").read_text(encoding="utf-8"))
    bones = model["minecraft:geometry"][0]["bones"]
    names = {entry["name"] for entry in bones}
    if len(names) != len(bones): raise ValueError("Duplicate boss bone name")
    for entry in bones:
        if entry.get("parent") not in names and entry.get("parent") is not None: raise ValueError(f"Missing parent for {entry['name']}")
        for box in entry.get("cubes", []):
            if any(float(value) <= 0 for value in box["size"]): raise ValueError(f"Non-positive cube on {entry['name']}")

    animation_data = json.loads((A / "animations/dark_forest/mossbound_enderman.animation.json").read_text(encoding="utf-8"))["animations"]
    expected = {"dormant", "awaken", "idle", "walk", "antler_sweep", "root_cast", "marked_step", "orb_cast", "bloomfall", "echo_double", "eclipse_rush", "phase_shift", "flower_open", "hurt", "death"}
    if set(animation_data) != expected: raise ValueError(f"Boss clip contract differs: {set(animation_data) ^ expected}")
    for clip, data in animation_data.items():
        missing = set(data.get("bones", {})) - names
        if missing: raise ValueError(f"Animation {clip} references missing bones: {sorted(missing)}")

    attack_clips = json.loads((A / "animations/dark_forest/mossbound_attacks.animation.json").read_text(encoding="utf-8"))["animations"]
    clip_for_geo = {"root": "root_snare", "step": "marked_step", "orb": "moonwell_orb", "bloom": "bloomfall_effect", "echo": "echo_double_effect", "rush": "eclipse_rush_effect"}
    for ability, clip in clip_for_geo.items():
        data = json.loads((A / f"geo/dark_forest/attacks/{ability}.geo.json").read_text(encoding="utf-8"))
        ability_names = {entry["name"] for entry in data["minecraft:geometry"][0]["bones"]}
        missing = set(attack_clips[clip].get("bones", {})) - ability_names
        if missing: raise ValueError(f"Ability {ability} clip references missing bones: {sorted(missing)}")
        for suffix in ("", "_emissive"):
            texture = Image.open(A / f"textures/dark_forest/attacks/{ability}{suffix}.png")
            if texture.size != (256, 256): raise ValueError(f"Wrong {ability}{suffix} texture size: {texture.size}")


def main() -> None:
    bones = boss_geometry()
    write(A / "geo/dark_forest/mossbound_enderman.geo.json", geo("geometry.mossbound_enderman", bones))
    write(A / "animations/dark_forest/mossbound_enderman.animation.json", boss_animations())
    for name, ability_bones in attack_geometry().items(): write(A / f"geo/dark_forest/attacks/{name}.geo.json", geo(f"geometry.mossbound_{name}", ability_bones, (5, 6, 2.2)))
    write(A / "animations/dark_forest/mossbound_attacks.animation.json", attack_animations())
    make_textures()
    validate_assets()
    print(f"Mossbound Enderman: {len(bones)} bones, {sum(len(b.get('cubes', [])) for b in bones)} cubes")


if __name__ == "__main__": main()
