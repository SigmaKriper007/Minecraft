#!/usr/bin/env python3
"""DarkForest mob assets: shade spiderling, gloom broodmother, moonwing bat."""
import math
from framework import *

# ---------------------------------------------------------------- palette
CHITIN      = (40, 33, 50)
CHITIN_DARK = (28, 23, 37)
CHITIN_LIGHT= (54, 45, 68)
CHITIN_EDGE = (64, 54, 80)
MOSS        = (104, 116, 58)
MOSS_DARK   = (78, 88, 44)
MOSS_LIGHT  = (132, 144, 76)
BONE        = (206, 198, 180)
BONE_DARK   = (158, 150, 134)
GLOW        = (128, 247, 243)
GLOW_CORE   = (214, 255, 254)
GLOW_DIM    = (86, 205, 208)
MEMBRANE    = (46, 37, 60)
MEMBRANE_L  = (62, 50, 80)
FUR         = (36, 30, 46)
FUR_L       = (52, 44, 64)

def chitin(seed=1, moss=0.0, moss_seed=7):
    p = noisy(CHITIN, CHITIN_DARK, CHITIN_LIGHT, seed)
    if moss > 0:
        p = speckle(p, MOSS, moss, moss_seed, clump=3)
        p = speckle(p, MOSS_LIGHT, moss * 0.4, moss_seed + 3, clump=2)
    return edge_darken(p, 0.85)

def plate(seed=1):
    """Armour plate with glowing rune strip down the middle."""
    base = noisy(CHITIN_LIGHT, CHITIN, CHITIN_EDGE, seed)
    def p(x, y, w, h, face):
        c = base(x, y, w, h, face)
        # rune marks: glowing dashes along the centre line of top/north/south faces
        if face in ('top', 'north', 'south') and w > 6:
            cx = w // 2
            run = (y % 4 < 2) and (h01(y // 4, seed, face) > 0.3)
            if abs(x - cx) <= 1 and run:
                return mix(c, GLOW, 0.85)
        return edge_darken(lambda *_a: c, 0.85)(x, y, w, h, face)
    return p

def bone_mat(seed=3):
    return edge_darken(grad_v(BONE, BONE_DARK, seed, 0.06), 0.9)

def membrane(seed=11, marks=True):
    base = noisy(MEMBRANE, (36, 29, 48), MEMBRANE_L, seed)
    if not marks:
        return base
    def p(x, y, w, h, face):
        c = base(x, y, w, h, face)
        if face in ('north', 'south'):
            # cyan crescent moon markings, like the reference art
            for i in range(3):
                mx = int((0.22 + i * 0.28) * w)
                my = int((0.3 + 0.18 * ((i + seed) % 2)) * h)
                dx, dy = x - mx, y - my
                d2 = dx * dx + dy * dy
                if 4 <= d2 <= 9 and h01(i, seed, 5) > 0.25:
                    return mix(c, GLOW_DIM, 0.9)
        return c
    return p

def fur(seed=21):
    return edge_darken(speckle(noisy(FUR, CHITIN_DARK, FUR_L, seed), FUR_L, 0.30, seed + 5, 2), 0.88)

# ================================================================ SHADE SPIDERLING
# The generic mirror helper above gets fiddly; use a straightforward explicit builder instead.
def build_spiderling2():
    m = Model('shade_spiderling', 192, 192, visible_bounds=(4, 3), uv_scale=2.0)

    def head_paint():
        base = chitin(31, moss=0.10)
        big = [(0.13, 0.24, 0.26, 0.40), (0.61, 0.24, 0.26, 0.40)]
        small = [(0.08, 0.04, 0.14, 0.18), (0.28, 0.02, 0.14, 0.18),
                 (0.56, 0.02, 0.14, 0.18), (0.76, 0.04, 0.14, 0.18)]
        return eyes(base, big + small, GLOW, GLOW_CORE, 17)
    def head_glow():
        rects = [(0.13, 0.24, 0.26, 0.40), (0.61, 0.24, 0.26, 0.40), (0.08, 0.04, 0.14, 0.18),
                 (0.28, 0.02, 0.14, 0.18), (0.56, 0.02, 0.14, 0.18), (0.76, 0.04, 0.14, 0.18)]
        return eyes(solid((0, 0, 0, 0)), rects, GLOW, GLOW_CORE, 17)

    upper_paint = edge_darken(speckle(noisy(CHITIN, CHITIN_DARK, CHITIN_LIGHT, 41), BONE, 0.10, 43, 2), 0.88)
    lower_paint = edge_darken(speckle(noisy(CHITIN, CHITIN_DARK, CHITIN_LIGHT, 45), MOSS, 0.12, 47, 2), 0.88)

    leg_z = [-3.8, -1.7, 0.4, 2.5]
    leg_yaw = [-30, -11, 9, 28]

    m.bone('root', [0, 0, 0])
    m.bone('abdomen', [0, 4.6, 3.2], 'root', cubes=[
        {'o': [-3.6, 2.6, -0.2], 's': [7.2, 3.6, 6.8], 'paint': chitin(11, moss=0.22)},
        {'o': [-2.2, 6.2, 1.0], 's': [4.4, 0.8, 4.0], 'paint': noisy(MOSS, MOSS_DARK, MOSS_LIGHT, 13)},
        {'o': [-0.9, 6.0, 4.4], 's': [1.8, 1.4, 1.8], 'paint': chitin(15)},
    ])
    m.bone('thorax', [0, 4.2, -1.8], 'root', cubes=[
        {'o': [-3.0, 2.6, -4.4], 's': [6.0, 3.0, 5.0], 'paint': chitin(21, moss=0.12)},
    ])
    m.bone('head', [0, 4.2, -4.4], 'root', cubes=[
        {'o': [-2.6, 2.7, -7.1], 's': [5.2, 3.0, 2.8], 'paint': head_paint(), 'emissive': head_glow()},
    ])
    m.bone('fang_left', [1.5, 3.4, -6.5], 'head', cubes=[
        {'o': [0.9, 1.2, -8.1], 's': [1.1, 2.3, 1.6], 'paint': bone_mat(3)}])
    m.bone('fang_right', [-1.5, 3.4, -6.5], 'head', cubes=[
        {'o': [-2.0, 1.2, -8.1], 's': [1.1, 2.3, 1.6], 'paint': bone_mat(3)}])
    m.bone('palp_left', [2.4, 3.6, -6.0], 'head', cubes=[
        {'o': [2.1, 2.8, -7.8], 's': [0.8, 1.7, 2.2], 'paint': chitin(25)}])
    m.bone('palp_right', [-2.4, 3.6, -6.0], 'head', cubes=[
        {'o': [-2.9, 2.8, -7.8], 's': [0.8, 1.7, 2.2], 'paint': chitin(25)}])

    shared_upper = shared_lower = None
    for i, z in enumerate(leg_z):
        up = {'o': [2.5, 3.8, z - 0.6], 's': [5.3, 1.2, 1.2], 'paint': upper_paint}
        lo = {'o': [7.4, 3.85, z - 0.55], 's': [7.6, 1.1, 1.1], 'paint': lower_paint}
        m.bone(f'leg_{i+1}_left_upper', [2.7, 4.4, z], 'root', rot=[0, leg_yaw[i], 48], cubes=[up])
        m.bone(f'leg_{i+1}_left_lower', [7.6, 4.4, z], f'leg_{i+1}_left_upper', rot=[0, 0, -113], cubes=[lo])
        if shared_upper is None:
            shared_upper, shared_lower = up, lo
        m.bone(f'leg_{i+1}_right_upper', [-2.7, 4.4, z], 'root', rot=[0, -leg_yaw[i], -48],
               cubes=[{'uv_of': shared_upper, 'o': [-7.8, 3.8, z - 0.6], 's': [5.3, 1.2, 1.2]}])
        m.bone(f'leg_{i+1}_right_lower', [-7.6, 4.4, z], f'leg_{i+1}_right_upper', rot=[0, 0, 113],
               cubes=[{'uv_of': shared_lower, 'o': [-15.0, 3.85, z - 0.55], 's': [7.6, 1.1, 1.1]}])
    return m

# ================================================================ GLOOM BROODMOTHER
def build_broodmother():
    m = Model('gloom_broodmother', 256, 256, visible_bounds=(8, 6), uv_scale=2.0)

    def head_paint():
        base = chitin(51, moss=0.14)
        big = [(0.12, 0.26, 0.22, 0.40), (0.66, 0.26, 0.22, 0.40)]
        small = [(0.05, 0.06, 0.12, 0.18), (0.24, 0.03, 0.12, 0.18),
                 (0.39, 0.02, 0.11, 0.16), (0.51, 0.02, 0.11, 0.16),
                 (0.63, 0.03, 0.12, 0.18), (0.82, 0.06, 0.12, 0.18)]
        return eyes(base, big + small, GLOW, GLOW_CORE, 23)
    def head_glow():
        rects = [(0.12, 0.26, 0.22, 0.40), (0.66, 0.26, 0.22, 0.40), (0.05, 0.06, 0.12, 0.18),
                 (0.24, 0.03, 0.12, 0.18), (0.39, 0.02, 0.11, 0.16), (0.51, 0.02, 0.11, 0.16),
                 (0.63, 0.03, 0.12, 0.18), (0.82, 0.06, 0.12, 0.18)]
        return eyes(solid((0, 0, 0, 0)), rects, GLOW, GLOW_CORE, 23)
    def plate_glow(seed):
        def p(x, y, w, h, face):
            if face in ('top', 'north', 'south') and w > 6:
                cx = w // 2
                run = (y % 4 < 2) and (h01(y // 4, seed, face) > 0.3)
                if abs(x - cx) <= 1 and run:
                    return GLOW
            return (0, 0, 0, 0)
        return p

    def sac_paint():
        base = edge_darken(noisy((188, 182, 164), (150, 144, 126), (214, 208, 190), 61), 0.92)
        def p(x, y, w, h, face):
            c = base(x, y, w, h, face)
            # faint silhouettes of eggs / spiderlings
            if vnoise(x, y, 63, 4) > 0.62:
                return mix(c, (120, 108, 118), 0.5)
            return c
        return p

    upper_paint = edge_darken(speckle(noisy(CHITIN, CHITIN_DARK, CHITIN_LIGHT, 71), BONE, 0.10, 73, 2), 0.88)
    lower_paint = edge_darken(speckle(noisy(CHITIN, CHITIN_DARK, CHITIN_LIGHT, 75), MOSS, 0.14, 77, 2), 0.88)

    m.bone('root', [0, 0, 0])
    m.bone('abdomen', [0, 11.5, 6.0], 'root', cubes=[
        {'o': [-8, 6.5, -0.5], 's': [16, 9.5, 15], 'paint': chitin(55, moss=0.24)},
        {'o': [-5.5, 16.0, 2.0], 's': [11, 1.2, 9], 'paint': noisy(MOSS, MOSS_DARK, MOSS_LIGHT, 57)},
        {'o': [-1.0, 17.2, 4.0], 's': [2.0, 1.6, 2.0], 'paint': chitin(59)},
    ])
    m.bone('armor_plate_1', [0, 16.2, 4.5], 'abdomen', cubes=[
        {'o': [-7.5, 15.9, 0.0], 's': [15, 1.7, 10], 'paint': plate(81), 'emissive': plate_glow(81)}])
    m.bone('armor_plate_2', [0, 17.2, 7.5], 'abdomen', cubes=[
        {'o': [-6.5, 16.8, 3.0], 's': [13, 1.5, 8], 'paint': plate(83), 'emissive': plate_glow(83)}])
    m.bone('armor_plate_3', [0, 18.0, 10.0], 'abdomen', cubes=[
        {'o': [-5.0, 17.6, 6.0], 's': [10, 1.3, 6], 'paint': plate(85), 'emissive': plate_glow(85)}])
    m.bone('egg_sac', [0, 8.0, 9.5], 'abdomen', cubes=[
        {'o': [-5, 2.0, 5.0], 's': [10, 4.8, 8.5], 'paint': sac_paint()}])
    m.bone('spinneret_left', [3.5, 8.5, 14.0], 'abdomen', cubes=[
        {'o': [1.6, 4.5, 12.5], 's': [3.4, 5.0, 4.0], 'paint': chitin(87)}])
    m.bone('spinneret_right', [-3.5, 8.5, 14.0], 'abdomen', cubes=[
        {'o': [-5.0, 4.5, 12.5], 's': [3.4, 5.0, 4.0], 'paint': chitin(87)}])
    m.bone('thorax', [0, 10.0, -5.0], 'root', cubes=[
        {'o': [-7, 6.5, -10.5], 's': [14, 7.5, 11], 'paint': chitin(65, moss=0.14)}])
    m.bone('head', [0, 10.0, -10.5], 'root', cubes=[
        {'o': [-6, 6.8, -17.0], 's': [12, 7.2, 6.6], 'paint': head_paint(), 'emissive': head_glow()}])
    m.bone('fang_left', [3.4, 8.2, -15.5], 'head', cubes=[
        {'o': [1.8, 2.2, -19.0], 's': [2.4, 5.5, 3.6], 'paint': bone_mat(3)}])
    m.bone('fang_right', [-3.4, 8.2, -15.5], 'head', cubes=[
        {'o': [-4.2, 2.2, -19.0], 's': [2.4, 5.5, 3.6], 'paint': bone_mat(3)}])
    m.bone('palp_left', [5.2, 8.8, -14.0], 'head', cubes=[
        {'o': [4.5, 7.2, -18.2], 's': [1.5, 2.8, 4.4], 'paint': chitin(89)}])
    m.bone('palp_right', [-5.2, 8.8, -14.0], 'head', cubes=[
        {'o': [-6.0, 7.2, -18.2], 's': [1.5, 2.8, 4.4], 'paint': chitin(89)}])

    shared_upper = shared_lower = None
    leg_z = [-8.4, -3.2, 2.1, 7.4]
    leg_yaw = [-30, -11, 10, 29]
    for i, z in enumerate(leg_z):
        up = {'o': [5.6, 7.05, z - 1.15], 's': [9.4, 2.4, 2.4], 'paint': upper_paint}
        lo = {'o': [15.0, 7.1, z - 1.15], 's': [14.6, 2.3, 2.3], 'paint': lower_paint}
        m.bone(f'leg_{i+1}_left_upper', [5.9, 8.2, z], 'root', rot=[0, leg_yaw[i], 52], cubes=[up])
        m.bone(f'leg_{i+1}_left_lower', [15.4, 8.2, z], f'leg_{i+1}_left_upper', rot=[0, 0, -116], cubes=[lo])
        if shared_upper is None:
            shared_upper, shared_lower = up, lo
        m.bone(f'leg_{i+1}_right_upper', [-5.9, 8.2, z], 'root', rot=[0, -leg_yaw[i], -52],
               cubes=[{'uv_of': shared_upper, 'o': [-15.0, 7.05, z - 1.15], 's': [9.4, 2.4, 2.4]}])
        m.bone(f'leg_{i+1}_right_lower', [-15.4, 8.2, z], f'leg_{i+1}_right_upper', rot=[0, 0, 116],
               cubes=[{'uv_of': shared_lower, 'o': [-29.6, 7.1, z - 1.15], 's': [14.6, 2.3, 2.3]}])
    return m

# ================================================================ MOONWING BAT
def build_bat():
    m = Model('moonwing_bat', 192, 192, visible_bounds=(6, 4), uv_scale=2.0)

    def head_paint():
        base = fur(91)
        big = [(0.13, 0.24, 0.26, 0.36), (0.61, 0.24, 0.26, 0.36)]
        return eyes(base, big, GLOW, GLOW_CORE, 27)
    def head_glow():
        rects = [(0.13, 0.24, 0.26, 0.36), (0.61, 0.24, 0.26, 0.36)]
        return eyes(solid((0, 0, 0, 0)), rects, GLOW, GLOW_CORE, 27)

    m.bone('root', [0, 0, 0])
    m.bone('body', [0, 11, 1], 'root', cubes=[
        {'o': [-3.5, 7.5, -5], 's': [7, 7, 11], 'paint': fur(93)},
        {'o': [-2.5, 9.5, -6.2], 's': [5, 4, 1.6], 'paint': fur(95)},
    ])
    m.bone('head', [0, 14, -5], 'root', cubes=[
        {'o': [-3.5, 11, -10.2], 's': [7, 6.5, 5.5], 'paint': head_paint(), 'emissive': head_glow()}])
    m.bone('jaw', [0, 12, -8.8], 'head', cubes=[
        {'o': [-2, 10.4, -11.8], 's': [4, 1.8, 3.2], 'paint': edge_darken(noisy((44, 36, 56), (32, 26, 42), (58, 48, 72), 97), 0.85)}])
    m.bone('ear_left', [2.4, 17.2, -6.5], 'head', rot=[-6, 0, -10], cubes=[
        {'o': [1.7, 16.8, -7.7], 's': [2.7, 7.6, 2.3], 'paint': fur(99)},
        {'o': [2.2, 17.4, -7.4], 's': [1.7, 6.0, 1.3], 'paint': edge_darken(grad_v((196, 178, 186), (150, 132, 148), 101, 0.05), 0.92)},
    ])
    m.bone('ear_right', [-2.4, 17.2, -6.5], 'head', rot=[-6, 0, 10], cubes=[
        {'o': [-4.4, 16.8, -7.7], 's': [2.7, 7.6, 2.3], 'paint': fur(99)},
        {'o': [-3.9, 17.4, -7.4], 's': [1.7, 6.0, 1.3], 'paint': edge_darken(grad_v((196, 178, 186), (150, 132, 148), 101, 0.05), 0.92)},
    ])
    wing_paint = membrane(11, marks=True)
    wing_panel = {'o': [3.2, 13.9, -5.5], 's': [13, 0.9, 12], 'paint': wing_paint}
    m.bone('wing_left', [3.5, 14.5, 0], 'root', rot=[0, -6, -10], cubes=[wing_panel])
    f1_panel = {'o': [15.9, 14.0, -6.0], 's': [12, 0.8, 9.5], 'paint': wing_paint}
    m.bone('wing_left_finger_1', [16.2, 14.5, -1], 'wing_left', rot=[0, 12, -8], cubes=[f1_panel])
    f2_panel = {'o': [27.6, 14.2, -3.0], 's': [10, 0.7, 7], 'paint': wing_paint}
    m.bone('wing_left_finger_2', [27.9, 14.5, 1.5], 'wing_left_finger_1', rot=[0, 18, -12], cubes=[f2_panel])
    m.bone('wing_right', [-3.5, 14.5, 0], 'root', rot=[0, 6, 10], cubes=[
        {'uv_of': wing_panel, 'o': [-16.2, 13.9, -5.5], 's': [13, 0.9, 12]}])
    m.bone('wing_right_finger_1', [-16.2, 14.5, -1], 'wing_right', rot=[0, -12, 8], cubes=[
        {'uv_of': f1_panel, 'o': [-27.9, 14.0, -6.0], 's': [12, 0.8, 9.5]}])
    m.bone('wing_right_finger_2', [-27.9, 14.5, 1.5], 'wing_right_finger_1', rot=[0, -18, 12], cubes=[
        {'uv_of': f2_panel, 'o': [-37.6, 14.2, -3.0], 's': [10, 0.7, 7]}])
    foot_paint = edge_darken(noisy((30, 25, 40), (22, 18, 32), (44, 36, 56), 103), 0.85)
    m.bone('foot_left', [2, 8.5, 3], 'body', cubes=[
        {'o': [1.2, 5.8, 1.6], 's': [1.8, 2.8, 3.2], 'paint': foot_paint}])
    m.bone('foot_right', [-2, 8.5, 3], 'body', cubes=[
        {'o': [-3.0, 5.8, 1.6], 's': [1.8, 2.8, 3.2], 'paint': foot_paint}])
    m.bone('tail', [0, 10.5, 6], 'body', cubes=[
        {'o': [-1.6, 10.1, 5.8], 's': [3.2, 1.0, 3.4], 'paint': membrane(13, marks=False)}])
    return m

# ================================================================ animations
def anim_spiderling():
    idle_bones = {
        'abdomen': {'position': track([(0, [0, 0, 0]), (1.5, [0, 0.35, 0]), (3.0, [0, 0, 0])]),
                    'rotation': track([(0, [1.5, 0, 0]), (1.5, [-1.5, 0, 0]), (3.0, [1.5, 0, 0])])},
        'head': {'rotation': track([(0, [0, -6, 0]), (1.5, [0, 6, 0]), (3.0, [0, -6, 0])])},
        'palp_left': {'rotation': track([(0, [7, 0, 0]), (0.75, [-5, 0, 0]), (1.5, [7, 0, 0]), (2.25, [-5, 0, 0]), (3.0, [7, 0, 0])])},
        'palp_right': {'rotation': track([(0, [-5, 0, 0]), (0.75, [7, 0, 0]), (1.5, [-5, 0, 0]), (2.25, [7, 0, 0]), (3.0, [-5, 0, 0])])},
        'fang_left': {'rotation': track([(0, [0, -3, 0]), (1.5, [0, 3, 0]), (3.0, [0, -3, 0])])},
        'fang_right': {'rotation': track([(0, [0, 3, 0]), (1.5, [0, -3, 0]), (3.0, [0, 3, 0])])},
    }
    # scuttle: alternating tetrapod gait, A = L1 L3 R2 R4, B = L2 L4 R1 R3
    scuttle = {}
    A_up = ['leg_1_left_upper', 'leg_3_left_upper', 'leg_2_right_upper', 'leg_4_right_upper']
    B_up = ['leg_2_left_upper', 'leg_4_left_upper', 'leg_1_right_upper', 'leg_3_right_upper']
    A_lo = ['leg_1_left_lower', 'leg_3_left_lower', 'leg_2_right_lower', 'leg_4_right_lower']
    B_lo = ['leg_2_left_lower', 'leg_4_left_lower', 'leg_1_right_lower', 'leg_3_right_lower']
    for n in A_up:
        scuttle[n] = {'rotation': track([(0, [0, 13, -8]), (0.22, [0, -13, 8]), (0.44, [0, 13, -8])])}
    for n in B_up:
        scuttle[n] = {'rotation': track([(0, [0, -13, 8]), (0.22, [0, 13, -8]), (0.44, [0, -13, 8])])}
    for n in A_lo:
        scuttle[n] = {'rotation': track([(0, [16, 0, 0]), (0.11, [-4, 0, 0]), (0.22, [-8, 0, 0]), (0.33, [6, 0, 0]), (0.44, [16, 0, 0])])}
    for n in B_lo:
        scuttle[n] = {'rotation': track([(0, [-8, 0, 0]), (0.11, [6, 0, 0]), (0.22, [16, 0, 0]), (0.33, [-4, 0, 0]), (0.44, [-8, 0, 0])])}
    scuttle['thorax'] = {'position': track([(0, [0, 0.15, 0]), (0.22, [0, -0.1, 0]), (0.44, [0, 0.15, 0])])}
    scuttle['abdomen'] = {'rotation': track([(0, [0, 2, 0]), (0.22, [0, -2, 0]), (0.44, [0, 2, 0])])}
    scuttle['head'] = {'rotation': track([(0, [0, -3, 0]), (0.22, [0, 3, 0]), (0.44, [0, -3, 0])])}

    bite = {
        'thorax': {'rotation': track([(0, [0, 0, 0]), (0.15, [-12, 0, 0]), (0.3, [4, 0, 0]), (0.42, [2, 0, 0]), (0.55, [0, 0, 0])])},
        'head': {'rotation': track([(0, [0, 0, 0]), (0.15, [24, 0, 0]), (0.3, [-18, 0, 0]), (0.42, [-8, 0, 0]), (0.55, [0, 0, 0])])},
        'fang_left': {'rotation': track([(0, [0, 0, 0]), (0.18, [0, -26, 5]), (0.32, [0, 7, -2]), (0.55, [0, 0, 0])])},
        'fang_right': {'rotation': track([(0, [0, 0, 0]), (0.18, [0, 26, -5]), (0.32, [0, -7, 2]), (0.55, [0, 0, 0])])},
        'palp_left': {'rotation': track([(0, [0, 0, 0]), (0.18, [18, -12, 0]), (0.34, [-8, 8, 0]), (0.55, [0, 0, 0])])},
        'palp_right': {'rotation': track([(0, [0, 0, 0]), (0.18, [18, 12, 0]), (0.34, [-8, -8, 0]), (0.55, [0, 0, 0])])},
        'abdomen': {'rotation': track([(0, [0, 0, 0]), (0.15, [-7, 0, 0]), (0.32, [4, 0, 0]), (0.55, [0, 0, 0])])},
    }
    return {'idle': build_anim('idle', 3.0, idle_bones),
            'scuttle': build_anim('scuttle', 0.44, scuttle),
            'bite': build_anim('bite', 0.55, bite, loop=False)}

def anim_broodmother():
    idle = {
        'abdomen': {'position': track([(0, [0, 0, 0]), (1.6, [0, 0.5, 0]), (3.2, [0, 0, 0])]),
                    'rotation': track([(0, [1.5, 0, 0]), (1.6, [-1.5, 0, 0]), (3.2, [1.5, 0, 0])])},
        'armor_plate_1': {'rotation': track([(0, [1.5, 0, 0]), (1.6, [-1.0, 0, 0]), (3.2, [1.5, 0, 0])])},
        'armor_plate_2': {'rotation': track([(0, [2, 0, 0]), (0.8, [0.5, 0, 0]), (1.6, [2, 0, 0]), (2.4, [0.5, 0, 0]), (3.2, [2, 0, 0])])},
        'armor_plate_3': {'rotation': track([(0, [0.5, 0, 0]), (0.8, [2, 0, 0]), (1.6, [0.5, 0, 0]), (2.4, [2, 0, 0]), (3.2, [0.5, 0, 0])])},
        'egg_sac': {'position': track([(0, [0, 0, 0]), (1.6, [0, -0.35, 0]), (3.2, [0, 0, 0])])},
        'head': {'rotation': track([(0, [0, 8, 0]), (1.6, [0, -8, 0]), (3.2, [0, 8, 0])])},
        'palp_left': {'rotation': track([(0, [8, 0, 0]), (0.8, [-4, 0, 0]), (1.6, [8, 0, 0]), (2.4, [-4, 0, 0]), (3.2, [8, 0, 0])])},
        'palp_right': {'rotation': track([(0, [-4, 0, 0]), (0.8, [8, 0, 0]), (1.6, [-4, 0, 0]), (2.4, [8, 0, 0]), (3.2, [-4, 0, 0])])},
        'fang_left': {'rotation': track([(0, [0, -4, 0]), (1.6, [0, 4, 0]), (3.2, [0, -4, 0])])},
        'fang_right': {'rotation': track([(0, [0, 4, 0]), (1.6, [0, -4, 0]), (3.2, [0, 4, 0])])},
        'spinneret_left': {'rotation': track([(0, [4, 0, 0]), (1.6, [-3, 0, 0]), (3.2, [4, 0, 0])])},
        'spinneret_right': {'rotation': track([(0, [-3, 0, 0]), (1.6, [4, 0, 0]), (3.2, [-3, 0, 0])])},
    }
    walk = {}
    A_up = ['leg_1_left_upper', 'leg_3_left_upper', 'leg_2_right_upper', 'leg_4_right_upper']
    B_up = ['leg_2_left_upper', 'leg_4_left_upper', 'leg_1_right_upper', 'leg_3_right_upper']
    A_lo = ['leg_1_left_lower', 'leg_3_left_lower', 'leg_2_right_lower', 'leg_4_right_lower']
    B_lo = ['leg_2_left_lower', 'leg_4_left_lower', 'leg_1_right_lower', 'leg_3_right_lower']
    for n in A_up:
        walk[n] = {'rotation': track([(0, [0, 14, -9]), (0.425, [0, -14, 9]), (0.85, [0, 14, -9])])}
    for n in B_up:
        walk[n] = {'rotation': track([(0, [0, -14, 9]), (0.425, [0, 14, -9]), (0.85, [0, -14, 9])])}
    for n in A_lo:
        walk[n] = {'rotation': track([(0, [18, 0, 0]), (0.2125, [4, 0, 0]), (0.425, [-9, 0, 0]), (0.6375, [6, 0, 0]), (0.85, [18, 0, 0])])}
    for n in B_lo:
        walk[n] = {'rotation': track([(0, [-9, 0, 0]), (0.2125, [6, 0, 0]), (0.425, [18, 0, 0]), (0.6375, [4, 0, 0]), (0.85, [-9, 0, 0])])}
    walk['thorax'] = {'position': track([(0, [0, 0.3, 0]), (0.2125, [0, -0.2, 0]), (0.425, [0, 0.3, 0]), (0.6375, [0, -0.2, 0]), (0.85, [0, 0.3, 0])])}
    walk['abdomen'] = {'rotation': track([(0, [0, 0, 2.5]), (0.425, [0, 0, -2.5]), (0.85, [0, 0, 2.5])])}
    walk['head'] = {'rotation': track([(0, [0, -4, 0]), (0.425, [0, 4, 0]), (0.85, [0, -4, 0])])}

    bite = {
        'thorax': {'rotation': track([(0, [0, 0, 0]), (0.16, [-14, 0, 0]), (0.32, [5, 0, 0]), (0.46, [2, 0, 0]), (0.6, [0, 0, 0])])},
        'head': {'rotation': track([(0, [0, 0, 0]), (0.16, [26, 0, 0]), (0.32, [-20, 0, 0]), (0.46, [-8, 0, 0]), (0.6, [0, 0, 0])])},
        'fang_left': {'rotation': track([(0, [0, 0, 0]), (0.2, [0, -28, 6]), (0.36, [0, 8, -3]), (0.6, [0, 0, 0])])},
        'fang_right': {'rotation': track([(0, [0, 0, 0]), (0.2, [0, 28, -6]), (0.36, [0, -8, 3]), (0.6, [0, 0, 0])])},
        'palp_left': {'rotation': track([(0, [0, 0, 0]), (0.2, [20, -14, 0]), (0.38, [-10, 9, 0]), (0.6, [0, 0, 0])])},
        'palp_right': {'rotation': track([(0, [0, 0, 0]), (0.2, [20, 14, 0]), (0.38, [-10, -9, 0]), (0.6, [0, 0, 0])])},
        'abdomen': {'rotation': track([(0, [0, 0, 0]), (0.16, [-7, 0, 0]), (0.36, [4, 0, 0]), (0.6, [0, 0, 0])])},
        'egg_sac': {'position': track([(0, [0, 0, 0]), (0.2, [0, 0.4, 0]), (0.4, [0, -0.3, 0]), (0.6, [0, 0, 0])])},
    }
    web = {
        'abdomen': {'position': track([(0, [0, 0, 0]), (0.5, [0, 0.8, 0]), (0.9, [0, 0.9, 0]), (1.1, [0, 0.1, 0]), (1.5, [0, 0, 0]), (2.0, [0, 0, 0])]),
                    'rotation': track([(0, [0, 0, 0]), (0.5, [16, 0, 0]), (0.9, [18, 0, 0]), (1.1, [-7, 0, 0]), (1.5, [-2, 0, 0]), (2.0, [0, 0, 0])])},
        'spinneret_left': {'rotation': track([(0, [0, 0, 0]), (0.4, [-14, 0, -7]), (0.7, [10, 0, 8]), (0.95, [-18, 0, -10]), (1.05, [15, 0, 10]), (1.3, [0, 0, 0]), (2.0, [0, 0, 0])])},
        'spinneret_right': {'rotation': track([(0, [0, 0, 0]), (0.4, [-14, 0, 7]), (0.7, [10, 0, -8]), (0.95, [-18, 0, 10]), (1.05, [15, 0, -10]), (1.3, [0, 0, 0]), (2.0, [0, 0, 0])])},
        'thorax': {'rotation': track([(0, [0, 0, 0]), (0.5, [6, 0, 0]), (1.0, [8, 0, 0]), (1.4, [0, 0, 0]), (2.0, [0, 0, 0])])},
        'head': {'rotation': track([(0, [0, 0, 0]), (0.5, [10, 0, 0]), (1.0, [12, 0, 0]), (1.4, [0, 0, 0]), (2.0, [0, 0, 0])])},
        'armor_plate_1': {'rotation': track([(0, [0, 0, 0]), (0.5, [8, 0, 0]), (1.0, [9, 0, 0]), (1.5, [0, 0, 0]), (2.0, [0, 0, 0])])},
        'egg_sac': {'position': track([(0, [0, 0, 0]), (0.6, [0, 0.5, 0]), (1.1, [0, -0.4, 0]), (1.6, [0, 0, 0]), (2.0, [0, 0, 0])])},
    }
    # slam: crouch -> leap (entity leaps at 0.8s) -> crash (~1.2s) -> recover
    slam = {
        'thorax': {'position': track([(0, [0, 0, 0]), (0.5, [0, -1.2, 0]), (0.8, [0, 0.9, 0]), (1.2, [0, -0.7, 0]), (1.6, [0, 0, 0]), (2.1, [0, 0, 0])]),
                   'rotation': track([(0, [0, 0, 0]), (0.5, [16, 0, 0]), (0.8, [-11, 0, 0]), (1.2, [18, 0, 0]), (1.6, [6, 0, 0]), (2.1, [0, 0, 0])])},
        'abdomen': {'rotation': track([(0, [0, 0, 0]), (0.5, [11, 0, 0]), (0.8, [-7, 0, 0]), (1.2, [13, 0, 0]), (2.1, [0, 0, 0])])},
        'head': {'rotation': track([(0, [0, 0, 0]), (0.5, [-9, 0, 0]), (0.8, [6, 0, 0]), (1.2, [15, 0, 0]), (2.1, [0, 0, 0])])},
        'egg_sac': {'position': track([(0, [0, 0, 0]), (0.5, [0, 0.5, 0]), (1.2, [0, -0.6, 0]), (1.7, [0, 0.3, 0]), (2.1, [0, 0, 0])])},
    }
    for i in range(1, 5):
        for side in ('left', 'right'):
            sz = 1 if side == 'left' else -1
            slam[f'leg_{i}_{side}_upper'] = {'rotation': track([
                (0, [0, 0, 0]), (0.5, [0, -6 * sz, -16 * sz]), (0.8, [0, 8 * sz, 16 * sz]),
                (1.2, [0, -10 * sz, -24 * sz]), (1.7, [0, 0, 4 * sz]), (2.1, [0, 0, 0])])}
            slam[f'leg_{i}_{side}_lower'] = {'rotation': track([
                (0, [0, 0, 0]), (0.5, [22, 0, 0]), (0.8, [-15, 0, 0]),
                (1.2, [24, 0, 0]), (1.7, [-4, 0, 0]), (2.1, [0, 0, 0])])}
    return {'idle': build_anim('idle', 3.2, idle),
            'walk': build_anim('walk', 0.85, walk),
            'bite': build_anim('bite', 0.6, bite, loop=False),
            'web_cast': build_anim('web_cast', 2.0, web, loop=False),
            'slam': build_anim('slam', 2.1, slam, loop=False)}

def anim_bat():
    # rests: wing_left [0,-6,-10], finger_1 [0,12,-8], finger_2 [0,18,-12], ears [-6,0,+-10]
    # keyframes below are DELTAS added on top of the geo rest pose
    dur = 1.3
    hover = {
        'wing_left': {'rotation': track([(0, [0, 0, 58]), (dur * 0.5, [0, 0, -38]), (dur, [0, 0, 58])])},
        'wing_right': {'rotation': track([(0, [0, 0, -58]), (dur * 0.5, [0, 0, 38]), (dur, [0, 0, -58])])},
        'wing_left_finger_1': {'rotation': track([(0, [0, 0, 26]), (dur * 0.5, [0, 0, -10]), (dur, [0, 0, 26])])},
        'wing_right_finger_1': {'rotation': track([(0, [0, 0, -26]), (dur * 0.5, [0, 0, 10]), (dur, [0, 0, -26])])},
        'wing_left_finger_2': {'rotation': track([(0, [0, 0, 36]), (dur * 0.5, [0, 0, -13]), (dur, [0, 0, 36])])},
        'wing_right_finger_2': {'rotation': track([(0, [0, 0, -36]), (dur * 0.5, [0, 0, 13]), (dur, [0, 0, -36])])},
        'body': {'position': track([(0, [0, 0, 0]), (dur * 0.5, [0, 0.7, 0]), (dur, [0, 0, 0])]),
                 'rotation': track([(0, [3, 0, 0]), (dur * 0.5, [-3, 0, 0]), (dur, [3, 0, 0])])},
        'head': {'rotation': track([(0, [4, 0, 0]), (dur * 0.5, [-2, 0, 0]), (dur, [4, 0, 0])])},
        'ear_left': {'rotation': track([(0, [0, 0, 0]), (0.3, [5, 0, -6]), (0.6, [0, 0, 0]), (1.0, [-3, 0, 4]), (dur, [0, 0, 0])])},
        'ear_right': {'rotation': track([(0, [0, 0, 0]), (0.3, [5, 0, 6]), (0.6, [0, 0, 0]), (1.0, [-3, 0, -4]), (dur, [0, 0, 0])])},
        'tail': {'rotation': track([(0, [0, 0, 6]), (dur * 0.5, [0, 0, -6]), (dur, [0, 0, 6])])},
        'foot_left': {'rotation': track([(0, [10, 0, 0]), (dur * 0.5, [-6, 0, 0]), (dur, [10, 0, 0])])},
        'foot_right': {'rotation': track([(0, [10, 0, 0]), (dur * 0.5, [-6, 0, 0]), (dur, [10, 0, 0])])},
    }
    dur = 0.42
    fly = {
        'wing_left': {'rotation': track([(0, [0, 0, 72]), (dur * 0.5, [0, 0, -52]), (dur, [0, 0, 72])])},
        'wing_right': {'rotation': track([(0, [0, 0, -72]), (dur * 0.5, [0, 0, 52]), (dur, [0, 0, -72])])},
        'wing_left_finger_1': {'rotation': track([(0, [0, 0, 34]), (dur * 0.3, [0, 0, -12]), (dur * 0.5, [0, 0, -30]), (dur * 0.75, [0, 0, -10]), (dur, [0, 0, 34])])},
        'wing_right_finger_1': {'rotation': track([(0, [0, 0, -34]), (dur * 0.3, [0, 0, 12]), (dur * 0.5, [0, 0, 30]), (dur * 0.75, [0, 0, 10]), (dur, [0, 0, -34])])},
        'wing_left_finger_2': {'rotation': track([(0, [0, 0, 46]), (dur * 0.3, [0, 0, -16]), (dur * 0.5, [0, 0, -40]), (dur * 0.75, [0, 0, -14]), (dur, [0, 0, 46])])},
        'wing_right_finger_2': {'rotation': track([(0, [0, 0, -46]), (dur * 0.3, [0, 0, 16]), (dur * 0.5, [0, 0, 40]), (dur * 0.75, [0, 0, 14]), (dur, [0, 0, -46])])},
        'body': {'position': track([(0, [0, 0, 0]), (dur * 0.5, [0, 0.5, 0]), (dur, [0, 0, 0])]),
                 'rotation': track([(0, [-5, 0, 0]), (dur * 0.5, [7, 0, 0]), (dur, [-5, 0, 0])])},
        'head': {'rotation': track([(0, [3, 0, 0]), (dur * 0.5, [-4, 0, 0]), (dur, [3, 0, 0])])},
        'tail': {'rotation': track([(0, [8, 0, 0]), (dur * 0.5, [-4, 0, 0]), (dur, [8, 0, 0])])},
        'foot_left': {'rotation': track([(0, [24, 0, 0]), (dur, [24, 0, 0])])},
        'foot_right': {'rotation': track([(0, [24, 0, 0]), (dur, [24, 0, 0])])},
    }
    # dive: wings swept BACK (+rotY for the +X wing) and folded, body nose-down
    dive = {
        'wing_left': {'rotation': track([(0, [0, 0, 0]), (0.18, [0, 34, 30]), (0.5, [0, 34, 30])])},
        'wing_right': {'rotation': track([(0, [0, 0, 0]), (0.18, [0, -34, -30]), (0.5, [0, -34, -30])])},
        'wing_left_finger_1': {'rotation': track([(0, [0, 0, 0]), (0.18, [0, 22, 18]), (0.5, [0, 22, 18])])},
        'wing_right_finger_1': {'rotation': track([(0, [0, 0, 0]), (0.18, [0, -22, -18]), (0.5, [0, -22, -18])])},
        'wing_left_finger_2': {'rotation': track([(0, [0, 0, 0]), (0.18, [0, 22, 24]), (0.5, [0, 22, 24])])},
        'wing_right_finger_2': {'rotation': track([(0, [0, 0, 0]), (0.18, [0, -22, -24]), (0.5, [0, -22, -24])])},
        'body': {'rotation': track([(0, [0, 0, 0]), (0.18, [24, 0, 0]), (0.5, [24, 0, 0])])},
        'head': {'rotation': track([(0, [0, 0, 0]), (0.18, [-16, 0, 0]), (0.5, [-16, 0, 0])])},
        'ear_left': {'rotation': track([(0, [0, 0, 0]), (0.18, [-20, 0, 6]), (0.5, [-20, 0, 6])])},
        'ear_right': {'rotation': track([(0, [0, 0, 0]), (0.18, [-20, 0, -6]), (0.5, [-20, 0, -6])])},
        'jaw': {'rotation': track([(0, [0, 0, 0]), (0.2, [12, 0, 0]), (0.5, [12, 0, 0])])},
        'tail': {'rotation': track([(0, [0, 0, 0]), (0.18, [10, 0, 0]), (0.5, [10, 0, 0])])},
        'foot_left': {'rotation': track([(0, [0, 0, 0]), (0.18, [18, 0, 0]), (0.5, [18, 0, 0])])},
        'foot_right': {'rotation': track([(0, [0, 0, 0]), (0.18, [18, 0, 0]), (0.5, [18, 0, 0])])},
    }
    # sonar: ears perk FORWARD (+rotX), jaw wide, wings flare up
    sonar = {
        'ear_left': {'rotation': track([(0, [0, 0, 0]), (0.25, [30, 0, -8]), (0.55, [24, 0, -4]), (0.9, [0, 0, 0])])},
        'ear_right': {'rotation': track([(0, [0, 0, 0]), (0.25, [30, 0, 8]), (0.55, [24, 0, 4]), (0.9, [0, 0, 0])])},
        'jaw': {'rotation': track([(0, [0, 0, 0]), (0.2, [34, 0, 0]), (0.55, [28, 0, 0]), (0.9, [0, 0, 0])])},
        'head': {'rotation': track([(0, [0, 0, 0]), (0.25, [-14, 0, 0]), (0.9, [0, 0, 0])])},
        'body': {'rotation': track([(0, [0, 0, 0]), (0.25, [-10, 0, 0]), (0.9, [0, 0, 0])]),
                 'position': track([(0, [0, 0, 0]), (0.25, [0, 0.4, 0]), (0.9, [0, 0, 0])])},
        'wing_left': {'rotation': track([(0, [0, 0, 0]), (0.25, [0, -8, 74]), (0.6, [0, -4, 64]), (0.9, [0, 0, 0])])},
        'wing_right': {'rotation': track([(0, [0, 0, 0]), (0.25, [0, 8, -74]), (0.6, [0, 4, -64]), (0.9, [0, 0, 0])])},
        'wing_left_finger_1': {'rotation': track([(0, [0, 0, 0]), (0.25, [0, -8, -22]), (0.9, [0, 0, 0])])},
        'wing_right_finger_1': {'rotation': track([(0, [0, 0, 0]), (0.25, [0, 8, 22]), (0.9, [0, 0, 0])])},
        'wing_left_finger_2': {'rotation': track([(0, [0, 0, 0]), (0.25, [0, -10, -22]), (0.9, [0, 0, 0])])},
        'wing_right_finger_2': {'rotation': track([(0, [0, 0, 0]), (0.25, [0, 10, 22]), (0.9, [0, 0, 0])])},
    }
    return {'hover': build_anim('hover', 1.3, hover),
            'fly': build_anim('fly', 0.42, fly),
            'dive': build_anim('dive', 0.5, dive, loop=False),
            'sonar': build_anim('sonar', 0.9, sonar, loop=False)}

# ================================================================ main
def main():
    m = build_spiderling2()
    m.save(f'{ROOT}/geo/dark_forest/shade_spiderling.geo.json',
           f'{ROOT}/textures/dark_forest/entity/shade_spiderling.png',
           f'{ROOT}/textures/dark_forest/entity/shade_spiderling_emissive.png')
    save_anim(f'{ROOT}/animations/dark_forest/shade_spiderling.animation.json', anim_spiderling())

    m = build_broodmother()
    m.save(f'{ROOT}/geo/dark_forest/gloom_broodmother.geo.json',
           f'{ROOT}/textures/dark_forest/entity/gloom_broodmother.png',
           f'{ROOT}/textures/dark_forest/entity/gloom_broodmother_emissive.png')
    save_anim(f'{ROOT}/animations/dark_forest/gloom_broodmother.animation.json', anim_broodmother())

    m = build_bat()
    m.save(f'{ROOT}/geo/dark_forest/moonwing_bat.geo.json',
           f'{ROOT}/textures/dark_forest/entity/moonwing_bat.png',
           f'{ROOT}/textures/dark_forest/entity/moonwing_bat_emissive.png')
    save_anim(f'{ROOT}/animations/dark_forest/moonwing_bat.animation.json', anim_bat())

if __name__ == '__main__':
    main()
