#!/usr/bin/env python3
"""Hurricane asset: layered realistic vortex (funnel, cloud deck, debris, dust skirt, energy bands)."""
import math
from framework import *

HCLOUD    = (62, 74, 94)
HCLOUD_D  = (44, 53, 70)
HCLOUD_L  = (110, 128, 152)
HCLOUD_HL = (168, 184, 206)
ENERGY    = (127, 243, 255)
WHITE     = (240, 250, 255)

def h_cloud(seed, alpha=215):
    """Streaky translucent storm cloud with cyan energy filaments."""
    base = noisy(HCLOUD, HCLOUD_D, HCLOUD_L, seed)
    def p(x, y, w, h, face):
        c = base(x, y, w, h, face)
        n = vnoise(x, y, seed + 50, 4)
        if n > 0.62:
            c = mix(c, HCLOUD_HL, min(1.0, (n - 0.62) * 1.8))
        if n < 0.25:
            c = mix(c, (36, 44, 60), 0.6)
        if h01(x // 2, y // 6, seed + 61) > 0.93:
            c = mix(c, ENERGY, 0.65)
        return (c[0], c[1], c[2], alpha)
    return p

def h_core(alpha=235):
    def p(x, y, w, h, face):
        t = y / max(1, h - 1)
        c = mix(WHITE, ENERGY, 0.25 + 0.55 * t)
        if face in ('east', 'west'):
            c = mix(c, HCLOUD_L, 0.25)
        if h01(x, y // 3, 71) > 0.9:
            c = WHITE
        return (c[0], c[1], c[2], alpha)
    return p

def h_band(alpha=150):
    def p(x, y, w, h, face):
        c = mix(ENERGY, (210, 250, 255), h01(y // 2, 81, 0) * 0.5)
        return (c[0], c[1], c[2], alpha)
    return p

def h_debris():
    def p(x, y, w, h, face):
        return noisy((86, 74, 62), (60, 51, 42), (116, 102, 86), 83)(x, y, w, h, face)
    return p

def build_hurricane():
    m = Model('hurricane', 256, 256, visible_bounds=(14, 12), uv_scale=2.0)
    m.bone('root', [0, 0, 0])
    m.bone('inner_core', [0, 5.5, 0], 'root', cubes=[
        {'o': [-0.4, 0, -2.2], 's': [0.8, 11, 4.4], 'paint': h_core()},
        {'o': [-2.2, 0, -0.4], 's': [4.4, 11, 0.8], 'paint': h_core()}])
    widths = [1.35, 1.75, 2.15, 2.6, 3.1, 3.65, 4.25, 4.9]
    t = 0.85
    seg_h = 1.55
    for i, w in enumerate(widths):
        y = round(i * seg_h, 2)
        cubes = [
            {'o': [-w, y, -w], 's': [round(2 * w, 2), seg_h, t], 'paint': h_cloud(101 + i)},
            {'o': [-w, y, round(w - t, 2)], 's': [round(2 * w, 2), seg_h, t], 'paint': h_cloud(101 + i)},
            {'o': [-w, y, round(-w + t, 2)], 's': [t, seg_h, round(2 * w - 2 * t, 2)], 'paint': h_cloud(101 + i)},
            {'o': [round(w - t, 2), y, round(-w + t, 2)], 's': [t, seg_h, round(2 * w - 2 * t, 2)], 'paint': h_cloud(101 + i)},
        ]
        m.bone(f'seg_{i}', [0, y, 0], 'root', rot=[0, i * 30, 0], cubes=cubes)
    m.bone('cloud_a', [0, 12.6, 0], 'root', cubes=[
        {'o': [-9, 11.8, -3.5], 's': [18, 1.6, 7], 'paint': h_cloud(121, 205)}])
    m.bone('cloud_b', [0, 13.1, 0], 'root', rot=[0, 45, 0], cubes=[
        {'o': [-8, 12.3, -3], 's': [16, 1.5, 6], 'paint': h_cloud(123, 205)}])
    m.bone('cloud_c', [0, 12.2, 0], 'root', rot=[0, 90, 0], cubes=[
        {'o': [-7.5, 11.4, -2.8], 's': [15, 1.4, 5.6], 'paint': h_cloud(125, 205)}])
    m.bone('cloud_d', [0, 13.6, 0], 'root', rot=[0, 135, 0], cubes=[
        {'o': [-6.5, 12.7, -2.4], 's': [13, 1.4, 4.8], 'paint': h_cloud(127, 205)}])
    for i, ang in enumerate([0, 55, 125, 205]):
        m.bone(f'dust_{i}', [0, 0.35, 0], 'root', rot=[0, ang, 0], cubes=[
            {'o': [-3.8, 0, -1.1], 's': [7.6, 0.7, 2.2], 'paint': h_cloud(131 + i, 190)}])
    m.bone('debris_a', [2.8, 2.2, 0], 'root', cubes=[
        {'o': [-0.7, -0.7, -0.7], 's': [1.4, 1.4, 1.4], 'paint': h_debris()}])
    m.bone('debris_b', [-3.4, 5.2, 0], 'root', cubes=[
        {'o': [-0.6, -0.6, -0.6], 's': [1.2, 1.2, 1.2], 'paint': h_debris()}])
    m.bone('debris_c', [2.2, 8.4, 0], 'root', cubes=[
        {'o': [-0.55, -0.55, -0.55], 's': [1.1, 1.1, 1.1], 'paint': h_debris()}])
    band = {'o': [2.1, 0, -0.35], 's': [0.8, 11, 0.7], 'paint': h_band()}
    m.bone('band_1', [2.5, 5.5, 0], 'root', rot=[0, 0, -7], cubes=[band])
    m.bone('band_2', [-2.5, 5.5, 0], 'root', rot=[0, 0, 7], cubes=[
        {'uv_of': band, 'o': [-2.9, 0, -0.35], 's': [0.8, 11, 0.7]}])
    return m

def anim_hurricane():
    """2.4s vortex loop: funnel segments spin fastest near the ground, cloud deck
    counter-rotates slowly, debris orbits, dust skirt whirls, energy bands twist."""
    turns = [5, 4, 4, 3, 3, 2, 2, 1]
    bones = {}
    for i, tn in enumerate(turns):
        bones[f'seg_{i}'] = {'rotation': track([(0, [0, 0, 0]), (2.4, [0, -tn * 360, 0])])}
    bones['inner_core'] = {'rotation': track([(0, [0, 0, 0]), (2.4, [0, -2160, 0])])}
    # cloud deck: slow counter-rotation + independent bob
    bob = {'cloud_a': (0.5, 0.0), 'cloud_b': (0.4, 0.6), 'cloud_c': (-0.4, 0.0), 'cloud_d': (0.3, 1.2)}
    for name, (amp, ph) in bob.items():
        bones[name] = {
            'rotation': track([(0, [0, 0, 0]), (2.4, [0, -360, 0])]),
            'position': track([(0, [0, 0, 0]), ((ph + 0.6) % 2.4, [0, amp, 0]), ((ph + 1.2) % 2.4, [0, 0, 0]), ((ph + 1.8) % 2.4, [0, -amp * 0.6, 0]), (2.4, [0, 0, 0])]),
        }
    for i, ang in enumerate([0, 55, 125, 205]):
        bones[f'dust_{i}'] = {'rotation': track([(0, [0, 0, 0]), (2.4, [0, -1080, 0])])}
    # orbiting debris with tumbling
    debris = [('debris_a', 2.8, 2.2, 2, 0.0, -720, 720),
              ('debris_b', 3.6, 5.2, 2, math.pi, 360, -720),
              ('debris_c', 2.2, 8.4, 2, math.pi / 2, -1080, 360)]
    for name, radius, height, tn, phase, rx, ry in debris:
        bones[name] = {
            'position': track(orbit_frames(radius, height, tn, 2.4, phase), lerp='catmullrom'),
            'rotation': track([(0, [0, 0, 0]), (1.2, [rx / 2, ry / 2, 0]), (2.4, [rx, ry, 0])]),
        }
    bones['band_1'] = {'rotation': track([(0, [0, 0, 0]), (1.2, [0, -720, 3]), (2.4, [0, -1440, 0])])}
    bones['band_2'] = {'rotation': track([(0, [0, 0, 0]), (1.2, [0, -900, -3]), (2.4, [0, -1800, 0])])}
    return {'vortex': build_anim('vortex', 2.4, bones)}

def main():
    m = build_hurricane()
    m.save(f'{ROOT}/geo/paradise/hurricane.geo.json', f'{ROOT}/textures/paradise/entity/hurricane.png')
    save_anim(f'{ROOT}/animations/paradise/hurricane.animation.json', anim_hurricane())

if __name__ == '__main__':
    main()
