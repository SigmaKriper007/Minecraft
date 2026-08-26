#!/usr/bin/env python3
"""Paradise mob assets: sunfinch, cloud grazer, paradise wyvern, wind core."""
import math
from framework import *

# ---------------------------------------------------------------- palette
GOLD      = (242, 185, 59)
GOLD_DK   = (206, 142, 34)
ORANGE    = (224, 123, 31)
CREAM     = (255, 243, 217)
BURNT     = (179, 98, 27)
BEAK      = (107, 74, 35)
RUBY      = (192, 57, 43)

WOOL      = (244, 246, 248)
WOOL_SH   = (201, 210, 220)
FACE      = (143, 163, 184)
FACE_DK   = (108, 126, 148)
HOOF      = (92, 107, 122)
HORN      = (232, 197, 106)

W_CREAM   = (245, 239, 226)
W_CREAM_D = (216, 205, 182)
W_GOLD    = (217, 169, 76)
MEMB      = (191, 233, 239)
MEMB_D    = (127, 207, 224)
LEATHER   = (138, 90, 51)
LEATHER_D = (104, 66, 38)
EYE_CYAN  = (87, 230, 230)
ENERGY    = (127, 243, 255)
WHITE     = (240, 250, 255)

def feather(seed=1, base=GOLD, dark=GOLD_DK, light=CREAM):
    return edge_darken(speckle(noisy(base, dark, light, seed), light, 0.18, seed + 9, 2), 0.9)

def wool_mat(seed=1):
    return edge_darken(speckle(noisy(WOOL, WOOL_SH, (255, 255, 255), seed), WOOL_SH, 0.35, seed + 5, 2), 0.94)

def membrane_feather(seed=1):
    base = noisy(MEMB, MEMB_D, (222, 244, 248), seed)
    def p(x, y, w, h, face):
        c = base(x, y, w, h, face)
        if face in ('north', 'south', 'top', 'bottom') and h > 3:
            if (x * 2 + (y % 2)) % 5 == 0:
                return mix(c, (235, 248, 250), 0.5)
        return c
    return p

# ================================================================ SUNFINCH
def build_sunfinch():
    m = Model('sunfinch', 128, 128, visible_bounds=(3, 2), uv_scale=2.0)

    def head_paint():
        base = feather(31)
        return eyes(base, [(0.18, 0.32, 0.16, 0.22), (0.66, 0.32, 0.16, 0.22)], (40, 34, 44), (16, 12, 20), 37)
    crest = feather(33, ORANGE, BURNT, GOLD)

    m.bone('root', [0, 0, 0])
    m.bone('body', [0, 7, 0], 'root', cubes=[
        {'o': [-2.5, 4.5, -4], 's': [5, 5, 8], 'paint': feather(11)}])
    m.bone('head', [0, 10, -3.5], 'root', cubes=[
        {'o': [-2, 7.5, -7], 's': [4, 4.5, 4], 'paint': head_paint()}])
    m.bone('beak', [0, 9.5, -7], 'head', cubes=[
        {'o': [-0.8, 8.8, -9.2], 's': [1.6, 1.6, 2.2], 'paint': edge_darken(noisy(BEAK, (84, 58, 27), (134, 96, 50), 35), 0.88)}])
    m.bone('crest_center', [0, 12, -4], 'head', cubes=[
        {'o': [-0.5, 12, -5.8], 's': [1, 2.5, 2], 'paint': crest}])
    m.bone('crest_left', [1.2, 11.5, -4], 'head', rot=[0, 0, -24], cubes=[
        {'o': [1.0, 11.2, -5.8], 's': [0.8, 2.2, 2], 'paint': crest}])
    m.bone('crest_right', [-1.2, 11.5, -4], 'head', rot=[0, 0, 24], cubes=[
        {'o': [-1.8, 11.2, -5.8], 's': [0.8, 2.2, 2], 'paint': crest}])
    wing = membrane_feather(41)
    wing_l = {'o': [2.2, 8, -3], 's': [4.8, 0.9, 7], 'paint': wing}
    m.bone('wing_left', [2.4, 8.5, -0.5], 'root', rot=[0, 0, -8], cubes=[wing_l])
    m.bone('wing_left_tip', [6.8, 8.5, 1.5], 'wing_left', rot=[0, 14, 0], cubes=[
        {'o': [6.6, 8.2, -1.5], 's': [4.2, 0.7, 5.5], 'paint': feather(43, BURNT, (140, 74, 20), ORANGE)}])
    m.bone('wing_right', [-2.4, 8.5, -0.5], 'root', rot=[0, 0, 8], cubes=[
        {'uv_of': wing_l, 'o': [-7.0, 8, -3], 's': [4.8, 0.9, 7]}])
    m.bone('wing_right_tip', [-6.8, 8.5, 1.5], 'wing_right', rot=[0, -14, 0], cubes=[
        {'o': [-10.8, 8.2, -1.5], 's': [4.2, 0.7, 5.5], 'paint': feather(43, BURNT, (140, 74, 20), ORANGE)}])
    m.bone('tail', [0, 7.5, 4], 'root', rot=[-14, 0, 0], cubes=[
        {'o': [-2, 6.8, 4], 's': [4, 0.9, 4.5], 'paint': feather(45, ORANGE, BURNT, GOLD)}])
    m.bone('tail_stream_left', [1.4, 7.2, 7.5], 'tail', rot=[-10, 0, -8], cubes=[
        {'o': [1.2, 6.9, 7], 's': [0.7, 0.5, 4.5], 'paint': feather(47, GOLD, ORANGE, CREAM)}])
    m.bone('tail_stream_right', [-1.4, 7.2, 7.5], 'tail', rot=[-10, 0, 8], cubes=[
        {'o': [-1.9, 6.9, 7], 's': [0.7, 0.5, 4.5], 'paint': feather(47, GOLD, ORANGE, CREAM)}])
    leg = edge_darken(noisy((196, 148, 74), (160, 116, 52), (226, 182, 104), 51), 0.88)
    m.bone('leg_left', [1.2, 4.5, 0], 'root', cubes=[
        {'o': [0.8, 2.8, -0.4], 's': [0.8, 1.8, 0.8], 'paint': leg}])
    m.bone('leg_right', [-1.2, 4.5, 0], 'root', cubes=[
        {'o': [-1.6, 2.8, -0.4], 's': [0.8, 1.8, 0.8], 'paint': leg}])
    return m

# ================================================================ CLOUD GRAZER
def build_grazer():
    m = Model('cloud_grazer', 256, 256, visible_bounds=(4, 3), uv_scale=2.0)

    def face_paint():
        base = edge_darken(noisy(FACE, FACE_DK, (172, 190, 208), 61), 0.9)
        return eyes(base, [(0.14, 0.3, 0.14, 0.2), (0.72, 0.3, 0.14, 0.2)], (46, 54, 70), (20, 24, 34), 63)

    m.bone('root', [0, 0, 0])
    m.bone('body', [0, 11, 0], 'root', cubes=[
        {'o': [-6, 6, -8], 's': [12, 9, 15], 'paint': wool_mat(11)},
        {'o': [-7.5, 8, -9.5], 's': [3.5, 5, 4], 'paint': wool_mat(13)},
        {'o': [4.0, 8, -9.5], 's': [3.5, 5, 4], 'paint': wool_mat(15)},
        {'o': [-7.5, 8, 5.5], 's': [3.5, 5, 4], 'paint': wool_mat(17)},
        {'o': [4.0, 8, 5.5], 's': [3.5, 5, 4], 'paint': wool_mat(19)},
        {'o': [-5, 15, -6], 's': [10, 2.5, 11], 'paint': wool_mat(21)},
    ])
    m.bone('head', [0, 13.5, -8], 'root', cubes=[
        {'o': [-3.5, 10.5, -13.5], 's': [7, 6.5, 6], 'paint': face_paint()},
        {'o': [-2.5, 16.5, -12], 's': [5, 2, 4.5], 'paint': wool_mat(23)},
    ])
    horn = grad_v(HORN, (198, 160, 78), 25, 0.05)
    m.bone('horn_left', [2.6, 16.2, -10], 'head', rot=[0, 0, -28], cubes=[
        {'o': [2.2, 16, -11.5], 's': [1.2, 1.2, 3], 'paint': horn}])
    m.bone('horn_right', [-2.6, 16.2, -10], 'head', rot=[0, 0, 28], cubes=[
        {'o': [-3.4, 16, -11.5], 's': [1.2, 1.2, 3], 'paint': horn}])
    m.bone('ear_left', [3.2, 15, -9], 'head', cubes=[
        {'o': [2.8, 14.6, -9.8], 's': [1.4, 1.6, 2.4], 'paint': edge_darken(noisy(FACE, FACE_DK, (172, 190, 208), 27), 0.88)}])
    m.bone('ear_right', [-3.2, 15, -9], 'head', cubes=[
        {'o': [-4.2, 14.6, -9.8], 's': [1.4, 1.6, 2.4], 'paint': edge_darken(noisy(FACE, FACE_DK, (172, 190, 208), 27), 0.88)}])
    leg = edge_darken(noisy(FACE, FACE_DK, (150, 168, 188), 29), 0.86)
    def hoof_paint(x, y, w, h, face):
        if face == 'bottom' or (face in ('north', 'south', 'east', 'west') and y >= h - 2):
            return edge_darken(noisy(HOOF, (72, 85, 100), (118, 134, 150), 31), 0.85)(x, y, w, h, face)
        return leg(x, y, w, h, face)
    for name, px, pz in [('leg_front_left', 4, -5.5), ('leg_front_right', -4, -5.5),
                         ('leg_back_left', 4, 4.5), ('leg_back_right', -4, 4.5)]:
        m.bone(name, [px, 7, pz], 'root', cubes=[
            {'o': [px - 1.1, 2.4, pz - 1.1], 's': [2.2, 4.6, 2.2], 'paint': hoof_paint}])
    m.bone('tail', [0, 11, 7], 'root', rot=[18, 0, 0], cubes=[
        {'o': [-1.5, 10, 7], 's': [3, 2.5, 3], 'paint': wool_mat(33)}])
    return m

# ================================================================ PARADISE WYVERN
def build_wyvern():
    m = Model('paradise_wyvern', 256, 256, visible_bounds=(8, 5), uv_scale=2.0)

    def head_paint():
        base = feather(71, W_CREAM, W_CREAM_D, (255, 250, 240))
        return eyes(base, [(0.16, 0.3, 0.16, 0.24), (0.68, 0.3, 0.16, 0.24)], EYE_CYAN, (214, 255, 254), 73)

    body = feather(75, W_CREAM, W_CREAM_D, (255, 250, 240))
    gold_trim = edge_darken(speckle(noisy(W_GOLD, (188, 142, 54), (238, 202, 118), 77), (238, 202, 118), 0.2, 79, 2), 0.9)
    memb = membrane_feather(81)

    m.bone('root', [0, 9, 0])
    m.bone('torso', [0, 12, -1], 'root', cubes=[
        {'o': [-4.5, 8, -8], 's': [9, 9, 12], 'paint': body},
        {'o': [-3.5, 7.5, 2], 's': [7, 7, 7], 'paint': body},
    ])
    m.bone('saddle', [0, 17, -2], 'torso', cubes=[
        {'o': [-4, 15.5, -5], 's': [8, 2.5, 9], 'paint': edge_darken(noisy(LEATHER, LEATHER_D, (168, 118, 72), 83), 0.88)},
        {'o': [-3, 17.5, -6], 's': [6, 1.5, 2], 'paint': gold_trim},
        {'o': [-3.5, 17.5, 2.5], 's': [7, 1.8, 2], 'paint': gold_trim},
    ])
    m.bone('neck_1', [0, 14, -8], 'root', cubes=[
        {'o': [-3, 10.5, -14], 's': [6, 6.5, 7], 'paint': body}])
    m.bone('neck_2', [0, 15.5, -13.5], 'neck_1', cubes=[
        {'o': [-2.6, 12, -19.5], 's': [5.2, 6, 6.5], 'paint': body}])
    m.bone('head', [0, 16.5, -19], 'neck_2', cubes=[
        {'o': [-2.8, 13.5, -26], 's': [5.6, 5.6, 7.5], 'paint': head_paint()},
        {'o': [-1.8, 14, -28.5], 's': [3.6, 3, 3.5], 'paint': feather(85, W_CREAM, W_CREAM_D, (255, 250, 240))},
    ])
    m.bone('jaw', [0, 13.8, -24.5], 'head', cubes=[
        {'o': [-1.6, 12.6, -28], 's': [3.2, 1.6, 4], 'paint': edge_darken(noisy(W_CREAM_D, (192, 180, 158), (232, 222, 202), 87), 0.88)}])
    horn = grad_v(W_GOLD, (188, 142, 54), 89, 0.05)
    m.bone('horn_left', [2.4, 18.6, -24], 'head', rot=[-20, 10, 0], cubes=[
        {'o': [1.9, 18.2, -26.5], 's': [1.2, 1.2, 3.2], 'paint': horn}])
    m.bone('horn_right', [-2.4, 18.6, -24], 'head', rot=[-20, -10, 0], cubes=[
        {'o': [-3.1, 18.2, -26.5], 's': [1.2, 1.2, 3.2], 'paint': horn}])
    crest = feather(91, RUBY, (150, 40, 30), (226, 96, 80))
    m.bone('crest_center', [0, 19.6, -22], 'head', rot=[-14, 0, 0], cubes=[
        {'o': [-0.5, 19.2, -24.4], 's': [1, 1.1, 3.4], 'paint': crest}])
    m.bone('crest_left', [1.6, 19, -22], 'head', rot=[-12, 0, -26], cubes=[
        {'o': [1.2, 18.6, -24.2], 's': [1.1, 1.1, 3], 'paint': crest}])
    m.bone('crest_right', [-1.6, 19, -22], 'head', rot=[-12, 0, 26], cubes=[
        {'o': [-2.3, 18.6, -24.2], 's': [1.1, 1.1, 3], 'paint': crest}])
    wing_l = {'o': [4, 14.4, -7], 's': [16, 0.9, 13], 'paint': memb}
    f1_l = {'o': [19.5, 14.5, -9], 's': [15, 0.8, 11], 'paint': memb}
    tip_l = {'o': [34, 14.6, -6], 's': [12, 0.7, 8], 'paint': memb}
    m.bone('wing_left', [4.5, 15, -3], 'root', rot=[0, -8, -10], cubes=[wing_l])
    m.bone('wing_left_finger_1', [20, 15, -1], 'wing_left', rot=[0, 14, -6], cubes=[f1_l])
    m.bone('wing_left_tip', [34.5, 15, 1], 'wing_left_finger_1', rot=[0, 22, -10], cubes=[tip_l])
    m.bone('wing_right', [-4.5, 15, -3], 'root', rot=[0, 8, 10], cubes=[
        {'uv_of': wing_l, 'o': [-20, 14.4, -7], 's': [16, 0.9, 13]}])
    m.bone('wing_right_finger_1', [-20, 15, -1], 'wing_right', rot=[0, -14, 6], cubes=[
        {'uv_of': f1_l, 'o': [-34.5, 14.5, -9], 's': [15, 0.8, 11]}])
    m.bone('wing_right_tip', [-34.5, 15, 1], 'wing_right_finger_1', rot=[0, -22, 10], cubes=[
        {'uv_of': tip_l, 'o': [-46, 14.6, -6], 's': [12, 0.7, 8]}])
    leg = feather(93, W_CREAM, W_CREAM_D, (255, 250, 240))
    claw = edge_darken(noisy(W_GOLD, (188, 142, 54), (238, 202, 118), 95), 0.88)
    m.bone('leg_left', [4.5, 9.5, 3], 'root', rot=[0, 0, 18], cubes=[
        {'o': [3.2, 5.5, 1.5], 's': [2.6, 5, 4], 'paint': leg},
        {'o': [3.4, 3.8, 2], 's': [2.2, 1.8, 3.4], 'paint': claw}])
    m.bone('leg_right', [-4.5, 9.5, 3], 'root', rot=[0, 0, -18], cubes=[
        {'o': [-5.8, 5.5, 1.5], 's': [2.6, 5, 4], 'paint': leg},
        {'o': [-5.6, 3.8, 2], 's': [2.2, 1.8, 3.4], 'paint': claw}])
    m.bone('tail_1', [0, 12, 7], 'root', cubes=[
        {'o': [-2.4, 10.5, 7], 's': [4.8, 4.5, 7], 'paint': body}])
    m.bone('tail_2', [0, 12, 13.5], 'tail_1', cubes=[
        {'o': [-2.1, 10.8, 13.5], 's': [4.2, 4, 6.5], 'paint': body}])
    m.bone('tail_3', [0, 12, 19.5], 'tail_2', cubes=[
        {'o': [-1.8, 11, 19.5], 's': [3.6, 3.5, 6], 'paint': body}])
    m.bone('tail_4', [0, 12, 25], 'tail_3', cubes=[
        {'o': [-1.5, 11.2, 25], 's': [3, 3, 5.5], 'paint': body}])
    m.bone('tail_5', [0, 12, 30], 'tail_4', cubes=[
        {'o': [-1.2, 11.4, 30], 's': [2.4, 2.5, 5], 'paint': body}])
    fin = membrane_feather(97)
    m.bone('tail_fin_left', [1.2, 12.5, 34.5], 'tail_5', rot=[0, -14, -24], cubes=[
        {'o': [0.8, 12, 33.5], 's': [2.8, 0.6, 5], 'paint': fin}])
    m.bone('tail_fin_right', [-1.2, 12.5, 34.5], 'tail_5', rot=[0, 14, 24], cubes=[
        {'o': [-3.6, 12, 33.5], 's': [2.8, 0.6, 5], 'paint': fin}])
    return m

# ================================================================ WIND CORE
def build_wind_core():
    m = Model('wind_core', 128, 128, visible_bounds=(2, 2), uv_scale=2.0)
    core = edge_darken(grad_v(WHITE, ENERGY, 5, 0.06), 0.95)
    ring = grad_v((207, 246, 255), ENERGY, 7, 0.05)
    bar_a = {'o': [-4.5, -0.5, -0.5], 's': [9, 1, 1], 'paint': ring}
    bar_b = {'o': [-0.5, -0.5, -4.5], 's': [1, 1, 9], 'paint': ring}
    m.bone('root', [0, 0, 0])
    m.bone('core', [0, 0, 0], 'root', cubes=[
        {'o': [-2.5, -2.5, -2.5], 's': [5, 5, 5], 'paint': core}])
    m.bone('ring_x', [0, 0, 0], 'root', rot=[0, 35, 0], cubes=[bar_a, bar_b])
    m.bone('ring_z', [0, 0, 0], 'root', rot=[35, 0, 45], cubes=[
        {'uv_of': bar_a, 'o': [-4.5, -0.5, -0.5], 's': [9, 1, 1]},
        {'uv_of': bar_b, 'o': [-0.5, -0.5, -4.5], 's': [1, 1, 9]}])
    mote = solid(ENERGY)
    for i in range(3):
        m.bone(f'mote_{i+1}', [0, 0, 0], 'root', cubes=[
            {'o': [-0.75, -0.75, -0.75], 's': [1.5, 1.5, 1.5], 'paint': mote}])
    return m

# ================================================================ animations
def anim_sunfinch():
    perch = {
        'body': {'position': track([(0, [0, 0, 0]), (1.2, [0, 0.25, 0]), (2.4, [0, 0, 0])]),
                 'rotation': track([(0, [1.2, 0, 0]), (1.2, [-1.2, 0, 0]), (2.4, [1.2, 0, 0])])},
        'head': {'rotation': track([(0, [0, -16, 0]), (0.8, [0, 14, 0]), (1.6, [0, -16, 0]), (2.0, [4, 0, 0]), (2.4, [0, -16, 0])])},
        'crest_center': {'rotation': track([(0, [0, 0, 0]), (0.6, [-7, 0, 0]), (1.2, [0, 0, 0]), (1.8, [-7, 0, 0]), (2.4, [0, 0, 0])])},
        'crest_left': {'rotation': track([(0, [0, 0, 0]), (0.6, [-5, 0, -6]), (1.2, [0, 0, 0]), (1.8, [-5, 0, -6]), (2.4, [0, 0, 0])])},
        'crest_right': {'rotation': track([(0, [0, 0, 0]), (0.6, [-5, 0, 6]), (1.2, [0, 0, 0]), (1.8, [-5, 0, 6]), (2.4, [0, 0, 0])])},
        'tail': {'rotation': track([(0, [0, 0, 0]), (0.8, [-6, 4, 0]), (1.6, [4, -4, 0]), (2.4, [0, 0, 0])])},
        'tail_stream_left': {'rotation': track([(0, [0, 0, 0]), (0.7, [6, 2, -6]), (1.4, [0, 0, 0]), (2.1, [4, -2, -4]), (2.4, [0, 0, 0])])},
        'tail_stream_right': {'rotation': track([(0, [0, 0, 0]), (0.7, [2, -2, 4]), (1.4, [6, 0, 6]), (2.1, [0, 0, 0]), (2.4, [0, 0, 0])])},
    }
    dur = 0.38
    fly = {
        'wing_left': {'rotation': track([(0, [0, 0, -60]), (dur * 0.5, [0, 0, 60]), (dur, [0, 0, -60])])},
        'wing_right': {'rotation': track([(0, [0, 0, 60]), (dur * 0.5, [0, 0, -60]), (dur, [0, 0, 60])])},
        'wing_left_tip': {'rotation': track([(0, [0, 0, -84]), (dur * 0.35, [0, 0, -22]), (dur * 0.5, [0, 0, 74]), (dur * 0.75, [0, 0, 10]), (dur, [0, 0, -84])])},
        'wing_right_tip': {'rotation': track([(0, [0, 0, 84]), (dur * 0.35, [0, 0, 22]), (dur * 0.5, [0, 0, -74]), (dur * 0.75, [0, 0, -10]), (dur, [0, 0, 84])])},
        'body': {'position': track([(0, [0, 0, 0]), (dur * 0.5, [0, 0.4, 0]), (dur, [0, 0, 0])]),
                 'rotation': track([(0, [-6, 0, 0]), (dur * 0.5, [5, 0, 0]), (dur, [-6, 0, 0])])},
        'head': {'rotation': track([(0, [4, 0, 0]), (dur * 0.5, [-3, 0, 0]), (dur, [4, 0, 0])])},
        'tail': {'rotation': track([(0, [8, 0, 0]), (dur * 0.5, [0, 0, 0]), (dur, [8, 0, 0])])},
        'tail_stream_left': {'rotation': track([(0, [8, 0, -8]), (dur * 0.5, [0, 0, 2]), (dur, [8, 0, -8])])},
        'tail_stream_right': {'rotation': track([(0, [8, 0, 8]), (dur * 0.5, [0, 0, -2]), (dur, [8, 0, 8])])},
        'crest_center': {'rotation': track([(0, [-16, 0, 0]), (dur, [-16, 0, 0])])},
        'crest_left': {'rotation': track([(0, [-14, 0, -10]), (dur, [-14, 0, -10])])},
        'crest_right': {'rotation': track([(0, [-14, 0, 10]), (dur, [-14, 0, 10])])},
        'leg_left': {'rotation': track([(0, [22, 0, 0]), (dur, [22, 0, 0])])},
        'leg_right': {'rotation': track([(0, [22, 0, 0]), (dur, [22, 0, 0])])},
    }
    return {'perch': build_anim('perch', 2.4, perch),
            'fly': build_anim('fly', dur, fly)}

def anim_grazer():
    idle = {
        'body': {'position': track([(0, [0, 0, 0]), (1.5, [0, 0.35, 0]), (3.0, [0, 0, 0])]),
                 'rotation': track([(0, [1.2, 0, 0]), (1.5, [-1.0, 0, 0]), (3.0, [1.2, 0, 0])])},
        'head': {'rotation': track([(0, [0, -12, 0]), (1.0, [0, 10, 0]), (2.0, [2, 0, 0]), (2.5, [8, 0, 0]), (3.0, [0, -12, 0])])},
        'ear_left': {'rotation': track([(0, [0, 0, 0]), (0.7, [-8, 0, 0]), (1.1, [0, 0, 0]), (2.3, [-10, 0, 0]), (2.7, [0, 0, 0]), (3.0, [0, 0, 0])])},
        'ear_right': {'rotation': track([(0, [0, 0, 0]), (1.4, [-8, 0, 0]), (1.8, [0, 0, 0]), (3.0, [0, 0, 0])])},
        'tail': {'rotation': track([(0, [0, -10, 0]), (0.75, [0, 10, 0]), (1.5, [0, -10, 0]), (2.25, [0, 10, 0]), (3.0, [0, -10, 0])])},
    }
    dur = 1.1
    walk = {
        'leg_front_left': {'rotation': track([(0, [26, 0, 0]), (dur * 0.5, [-26, 0, 0]), (dur, [26, 0, 0])])},
        'leg_back_right': {'rotation': track([(0, [26, 0, 0]), (dur * 0.5, [-26, 0, 0]), (dur, [26, 0, 0])])},
        'leg_front_right': {'rotation': track([(0, [-26, 0, 0]), (dur * 0.5, [26, 0, 0]), (dur, [-26, 0, 0])])},
        'leg_back_left': {'rotation': track([(0, [-26, 0, 0]), (dur * 0.5, [26, 0, 0]), (dur, [-26, 0, 0])])},
        'body': {'position': track([(0, [0, 0, 0]), (dur * 0.25, [0, 0.3, 0]), (dur * 0.5, [0, 0, 0]), (dur * 0.75, [0, 0.3, 0]), (dur, [0, 0, 0])]),
                 'rotation': track([(0, [0, 0, 1.5]), (dur * 0.5, [0, 0, -1.5]), (dur, [0, 0, 1.5])])},
        'head': {'rotation': track([(0, [3, 0, 0]), (dur * 0.5, [-3, 0, 0]), (dur, [3, 0, 0])])},
        'tail': {'rotation': track([(0, [0, 8, 0]), (dur * 0.5, [0, -8, 0]), (dur, [0, 8, 0])])},
    }
    return {'idle': build_anim('idle', 3.0, idle),
            'walk': build_anim('walk', dur, walk)}

def anim_wyvern():
    # rests: wing_left [0,-8,-10], f1 [0,14,-6], tip [0,22,-10], legs [0,0,+-18], crest_c [-14,0,0]
    # keyframes are DELTAS on top of the rest pose
    tail_bones = ['tail_1', 'tail_2', 'tail_3', 'tail_4', 'tail_5']
    idle = {
        'wing_left': {'rotation': track([(0, [0, 0, 18]), (1.1, [0, 0, -16]), (2.2, [0, 0, 18])])},
        'wing_right': {'rotation': track([(0, [0, 0, -18]), (1.1, [0, 0, 16]), (2.2, [0, 0, -18])])},
        'wing_left_finger_1': {'rotation': track([(0, [0, 0, 4]), (1.1, [0, 0, -10]), (2.2, [0, 0, 4])])},
        'wing_right_finger_1': {'rotation': track([(0, [0, 0, -4]), (1.1, [0, 0, 10]), (2.2, [0, 0, -4])])},
        'wing_left_tip': {'rotation': track([(0, [0, 0, 6]), (1.1, [0, 0, -10]), (2.2, [0, 0, 6])])},
        'wing_right_tip': {'rotation': track([(0, [0, 0, -6]), (1.1, [0, 0, 10]), (2.2, [0, 0, -6])])},
        'torso': {'position': track([(0, [0, 0, 0]), (1.1, [0, 0.5, 0]), (2.2, [0, 0, 0])]),
                  'rotation': track([(0, [1.5, 0, 0]), (1.1, [-1.5, 0, 0]), (2.2, [1.5, 0, 0])])},
        'neck_1': {'rotation': track([(0, [-2, 0, 0]), (1.1, [2, 0, 0]), (2.2, [-2, 0, 0])])},
        'neck_2': {'rotation': track([(0, [2, 0, 0]), (1.1, [-2, 0, 0]), (2.2, [2, 0, 0])])},
        'head': {'rotation': track([(0, [0, -10, 0]), (0.9, [2, 8, 0]), (1.7, [0, -6, 0]), (2.2, [0, -10, 0])])},
        'leg_left': {'rotation': track([(0, [4, 0, 0]), (1.1, [-4, 0, 0]), (2.2, [4, 0, 0])])},
        'leg_right': {'rotation': track([(0, [4, 0, 0]), (1.1, [-4, 0, 0]), (2.2, [4, 0, 0])])},
    }
    for i, tb in enumerate(tail_bones):
        idle[tb] = {'rotation': track([(0, [0, 0, 0]), (0.55, [0, 7 - i, 0]), (1.1, [0, 0, 0]), (1.65, [0, -7 + i, 0]), (2.2, [0, 0, 0])])}

    dur = 1.1
    fly = {
        'wing_left': {'rotation': track([(0, [0, 0, 64]), (dur * 0.5, [0, 0, -30]), (dur, [0, 0, 64])])},
        'wing_right': {'rotation': track([(0, [0, 0, -64]), (dur * 0.5, [0, 0, 30]), (dur, [0, 0, -64])])},
        'wing_left_finger_1': {'rotation': track([(0, [0, 0, 8]), (dur * 0.3, [0, 0, -2]), (dur * 0.5, [0, 0, -24]), (dur * 0.75, [0, 0, -6]), (dur, [0, 0, 8])])},
        'wing_right_finger_1': {'rotation': track([(0, [0, 0, -8]), (dur * 0.3, [0, 0, 2]), (dur * 0.5, [0, 0, 24]), (dur * 0.75, [0, 0, 6]), (dur, [0, 0, -8])])},
        'wing_left_tip': {'rotation': track([(0, [0, 0, 10]), (dur * 0.5, [0, 0, -26]), (dur, [0, 0, 10])])},
        'wing_right_tip': {'rotation': track([(0, [0, 0, -10]), (dur * 0.5, [0, 0, 26]), (dur, [0, 0, -10])])},
        'torso': {'position': track([(0, [0, 0, 0]), (dur * 0.5, [0, 0.6, 0]), (dur, [0, 0, 0])]),
                  'rotation': track([(0, [-4, 0, 0]), (dur * 0.5, [5, 0, 0]), (dur, [-4, 0, 0])])},
        'neck_1': {'rotation': track([(0, [-5, 0, 0]), (dur * 0.5, [3, 0, 0]), (dur, [-5, 0, 0])])},
        'neck_2': {'rotation': track([(0, [3, 0, 0]), (dur * 0.5, [-2, 0, 0]), (dur, [3, 0, 0])])},
        'head': {'rotation': track([(0, [3, 0, 0]), (dur * 0.5, [-3, 0, 0]), (dur, [3, 0, 0])])},
        'leg_left': {'rotation': track([(0, [30, 0, 0]), (dur, [30, 0, 0])])},
        'leg_right': {'rotation': track([(0, [30, 0, 0]), (dur, [30, 0, 0])])},
    }
    for i, tb in enumerate(tail_bones):
        fly[tb] = {'rotation': track([(0, [0, 0, 0]), (0.28, [0, 10 - i * 1.5, 0]), (0.55, [0, 0, 0]), (0.83, [0, -10 + i * 1.5, 0]), (1.1, [0, 0, 0])])}

    sit = {
        'wing_left': {'rotation': track([(0, [0, 32, -36]), (3.2, [0, 32, -36])])},
        'wing_right': {'rotation': track([(0, [0, -32, 36]), (3.2, [0, -32, 36])])},
        'wing_left_finger_1': {'rotation': track([(0, [0, 26, -24]), (3.2, [0, 26, -24])])},
        'wing_right_finger_1': {'rotation': track([(0, [0, -26, 24]), (3.2, [0, -26, 24])])},
        'wing_left_tip': {'rotation': track([(0, [0, 28, -24]), (3.2, [0, 28, -24])])},
        'wing_right_tip': {'rotation': track([(0, [0, -28, 24]), (3.2, [0, -28, 24])])},
        'torso': {'position': track([(0, [0, -1.5, 0]), (1.6, [0, -1.1, 0]), (3.2, [0, -1.5, 0])])},
        'neck_1': {'rotation': track([(0, [-14, 0, 0]), (3.2, [-14, 0, 0])])},
        'neck_2': {'rotation': track([(0, [18, 0, 0]), (3.2, [18, 0, 0])])},
        'head': {'rotation': track([(0, [0, -14, 0]), (1.1, [4, 12, 0]), (2.2, [0, -8, 0]), (3.2, [0, -14, 0])])},
        'jaw': {'rotation': track([(0, [0, 0, 0]), (2.4, [7, 0, 0]), (2.8, [0, 0, 0]), (3.2, [0, 0, 0])])},
        'leg_left': {'rotation': track([(0, [0, 0, 0]), (3.2, [0, 0, 0])])},
        'leg_right': {'rotation': track([(0, [0, 0, 0]), (3.2, [0, 0, 0])])},
    }
    for i, tb in enumerate(tail_bones):
        sit[tb] = {'rotation': track([(0, [0, 16 - i, 0]), (3.2, [0, 16 - i, 0])])}

    cast = {
        'neck_1': {'rotation': track([(0, [0, 0, 0]), (0.3, [-20, 0, 0]), (0.6, [-14, 0, 0]), (0.9, [0, 0, 0])])},
        'neck_2': {'rotation': track([(0, [0, 0, 0]), (0.3, [-8, 0, 0]), (0.9, [0, 0, 0])])},
        'head': {'rotation': track([(0, [0, 0, 0]), (0.3, [-26, 0, 0]), (0.6, [-18, 0, 0]), (0.9, [0, 0, 0])])},
        'jaw': {'rotation': track([(0, [0, 0, 0]), (0.35, [36, 0, 0]), (0.65, [26, 0, 0]), (0.9, [0, 0, 0])])},
        'wing_left': {'rotation': track([(0, [0, 0, 0]), (0.3, [0, -12, 60]), (0.55, [0, 0, -48]), (0.9, [0, 0, 0])])},
        'wing_right': {'rotation': track([(0, [0, 0, 0]), (0.3, [0, 12, -60]), (0.55, [0, 0, 48]), (0.9, [0, 0, 0])])},
        'wing_left_finger_1': {'rotation': track([(0, [0, 0, 0]), (0.3, [0, -8, -20]), (0.55, [0, 0, 10]), (0.9, [0, 0, 0])])},
        'wing_right_finger_1': {'rotation': track([(0, [0, 0, 0]), (0.3, [0, 8, 20]), (0.55, [0, 0, -10]), (0.9, [0, 0, 0])])},
        'wing_left_tip': {'rotation': track([(0, [0, 0, 0]), (0.3, [0, -12, -22]), (0.55, [0, 0, 6]), (0.9, [0, 0, 0])])},
        'wing_right_tip': {'rotation': track([(0, [0, 0, 0]), (0.3, [0, 12, 22]), (0.55, [0, 0, -6]), (0.9, [0, 0, 0])])},
        'torso': {'rotation': track([(0, [0, 0, 0]), (0.3, [-4, 0, 0]), (0.55, [6, 0, 0]), (0.9, [0, 0, 0])])},
        'crest_center': {'rotation': track([(0, [0, 0, 0]), (0.3, [-12, 0, 0]), (0.9, [0, 0, 0])])},
        'leg_left': {'rotation': track([(0, [4, 0, 0]), (0.9, [4, 0, 0])])},
        'leg_right': {'rotation': track([(0, [4, 0, 0]), (0.9, [4, 0, 0])])},
    }
    for i, tb in enumerate(tail_bones):
        cast[tb] = {'rotation': track([(0, [0, 0, 0]), (0.3, [0, -12 + i * 2, 0]), (0.55, [0, 12 - i * 2, 0]), (0.9, [0, 0, 0])])}
    return {'idle_flight': build_anim('idle_flight', 2.2, idle),
            'fly': build_anim('fly', dur, fly),
            'sit': build_anim('sit', 3.2, sit),
            'wind_cast': build_anim('wind_cast', 0.9, cast, loop=False)}

def anim_wind_core():
    fly = {
        'ring_x': {'rotation': track([(0, [0, 0, 0]), (1.2, [360, 0, 0])])},
        'ring_z': {'rotation': track([(0, [0, 0, 0]), (1.2, [0, -360, 0])])},
        'core': {'position': track([(0, [0, 0, 0]), (0.6, [0, 0.3, 0]), (1.2, [0, 0, 0])])},
    }
    for i in range(3):
        fly[f'mote_{i+1}'] = {'position': track(orbit_frames(4, 0.6 if i == 0 else (-0.5 if i == 1 else 0.1), 2, 1.2, phase=i * 2 * math.pi / 3), lerp='catmullrom')}
    return {'fly': build_anim('fly', 1.2, fly)}

# ================================================================ main
def main():
    m = build_sunfinch()
    m.save(f'{ROOT}/geo/paradise/sunfinch.geo.json', f'{ROOT}/textures/paradise/entity/sunfinch.png')
    save_anim(f'{ROOT}/animations/paradise/sunfinch.animation.json', anim_sunfinch())

    m = build_grazer()
    m.save(f'{ROOT}/geo/paradise/cloud_grazer.geo.json', f'{ROOT}/textures/paradise/entity/cloud_grazer.png')
    save_anim(f'{ROOT}/animations/paradise/cloud_grazer.animation.json', anim_grazer())

    m = build_wyvern()
    m.save(f'{ROOT}/geo/paradise/paradise_wyvern.geo.json', f'{ROOT}/textures/paradise/entity/paradise_wyvern.png')
    save_anim(f'{ROOT}/animations/paradise/paradise_wyvern.animation.json', anim_wyvern())

    m = build_wind_core()
    m.save(f'{ROOT}/geo/paradise/wind_core.geo.json', f'{ROOT}/textures/paradise/entity/wind_core.png')
    save_anim(f'{ROOT}/animations/paradise/wind_core.animation.json', anim_wind_core())

if __name__ == '__main__':
    main()
