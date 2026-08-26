#!/usr/bin/env python3
"""Procedural asset generator: Blockbench geo models + pixel-art textures + animations.

Design conventions (match existing mod assets):
- front of the mob faces -Z; entity "left" side is +X
- box (cube) UV layout: row1 = top|bottom, row2 = east|north|west|south
- animation rotation keyframes are ABSOLUTE bone rotations (replace geo rest rotation)
"""
import json
import math
import os
from PIL import Image

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'src', 'main', 'resources', 'assets', 'opusvsexe')

# ---------------------------------------------------------------- utilities
def h01(*args):
    """Deterministic hash -> float in [0,1)."""
    h = 2166136261
    for a in args:
        if isinstance(a, str):
            a = sum(ord(ch) * (i + 7) for i, ch in enumerate(a))
        h ^= (int(a) * 2654435761) & 0xFFFFFFFF
        h = (h * 16777619) & 0xFFFFFFFF
        h ^= h >> 13
    return (h & 0xFFFFFF) / 0xFFFFFF

def c01(v):
    return max(0, min(255, int(round(v))))

def mix(c1, c2, t):
    return (c01(c1[0] + (c2[0] - c1[0]) * t), c01(c1[1] + (c2[1] - c1[1]) * t),
            c01(c1[2] + (c2[2] - c1[2]) * t), 255)

def shade(c, f):
    return (c01(c[0] * f), c01(c[1] * f), c01(c[2] * f), 255)

def vnoise(x, y, seed, scale=3):
    """Cheap clumpy value noise in [0,1)."""
    gx, gy = math.floor(x / scale), math.floor(y / scale)
    fx, fy = (x / scale) - gx, (y / scale) - gy
    a = h01(gx, gy, seed)
    b = h01(gx + 1, gy, seed)
    c = h01(gx, gy + 1, seed)
    d = h01(gx + 1, gy + 1, seed)
    sx = fx * fx * (3 - 2 * fx)
    sy = fy * fy * (3 - 2 * fy)
    top = a + (b - a) * sx
    bot = c + (d - c) * sx
    return top + (bot - top) * sy

# ---------------------------------------------------------------- painters
# painter(x, y, w, h, face) -> (r,g,b,a)
# faces: 'top','bottom','east','north','west','south'

def solid(color):
    def p(x, y, w, h, face):
        return color
    return p

def noisy(base, dark, light, seed=1, fine=0.35):
    def p(x, y, w, h, face):
        n = vnoise(x, y, seed, 3)
        f = h01(x, y, seed + 101)
        c = base
        if n < 0.34:
            c = mix(base, dark, 0.7)
        elif n > 0.72:
            c = mix(base, light, 0.55)
        if f < fine * 0.5:
            c = shade(c, 0.9)
        elif f > 1 - fine * 0.35:
            c = shade(c, 1.1)
        return c
    return p

def grad_v(top, bottom, seed=2, amp=0.10):
    def p(x, y, w, h, face):
        t = y / max(1, h - 1)
        c = mix(top, bottom, t)
        n = (h01(x, y, seed) - 0.5) * 2 * 255 * amp
        return (c01(c[0] + n), c01(c[1] + n), c01(c[2] + n), 255)
    return p

def speckle(inner, color, density, seed=5, clump=2, soft=None):
    def p(x, y, w, h, face):
        base = inner(x, y, w, h, face)
        if vnoise(x, y, seed, clump) < density:
            t = 0.55 + h01(x, y, seed + 31) * 0.45
            return mix(base, color, soft if soft is not None else t)
        return base
    return p

def edge_darken(inner, amount=0.82):
    def p(x, y, w, h, face):
        c = inner(x, y, w, h, face)
        if x == 0 or y == 0 or x == w - 1 or y == h - 1:
            return shade(c, amount)
        return c
    return p

def overlay_faces(base_map, fallback):
    def p(x, y, w, h, face):
        return base_map.get(face, fallback)(x, y, w, h, face)
    return p

def eyes(base, eye_list, glow_color, glow_core, seed=17):
    """eye_list: [(nx, ny, nw, nh[, big])] normalized on the 'north' face."""
    def p(x, y, w, h, face):
        c = base(x, y, w, h, face)
        if face != 'north':
            return c
        for entry in eye_list:
            nx, ny, nw, nh = entry[:4]
            x0, y0 = int(nx * w), int(ny * h)
            ew, eh = max(1, int(nw * w)), max(1, int(nh * h))
            if x0 <= x < x0 + ew and y0 <= y < y0 + eh:
                inner = (x > x0 or ew <= 2) and (y > y0 or eh <= 2) and (x < x0 + ew - 1 or ew <= 2) and (y < y0 + eh - 1 or eh <= 2)
                return glow_core if inner else glow_color
        return c
    return p

# ---------------------------------------------------------------- atlas + cubes
class Model:
    def __init__(self, name, tex_w, tex_h, identifier=None, visible_bounds=(6, 6), uv_scale=1.0):
        self.name = name
        self.identifier = identifier or ('geometry.' + name)
        self.visible_bounds = visible_bounds
        self.uv_scale = uv_scale
        self.tex_w, self.tex_h = tex_w, tex_h
        self.img = Image.new('RGBA', (tex_w, tex_h), (0, 0, 0, 0))
        self.emissive = None
        self.bones = []
        self._u = 0
        self._v = 0
        self._row_h = 0

    def alloc_uv(self, w, h, d):
        s = self.uv_scale
        tw = 2 * (w + d) * s
        th = (d + h) * s
        if self._u + tw > self.tex_w:
            self._u = 0
            self._v += self._row_h
            self._row_h = 0
        if self._v + th > self.tex_h:
            raise RuntimeError(f'UV atlas overflow for {self.name}: need {tw}x{th} at ({self._u},{self._v})')
        uv = (round(self._u, 2), round(self._v, 2))
        self._u += tw
        self._row_h = max(self._row_h, th)
        return uv

    def bone(self, name, pivot, parent=None, rot=None, cubes=None):
        bone = {'name': name, 'pivot': pivot}
        if parent:
            bone['parent'] = parent
        if rot:
            bone['rotation'] = rot
        bone_cubes = []
        for spec in (cubes or []):
            if 'uv_of' in spec:
                # reuse an already-painted UV region, but with a fresh origin/size
                src = spec['uv_of']
                o, s, uv = spec['o'], spec['s'], src['_uv']
            elif 'mirror_of' in spec:
                src = spec['mirror_of']
                o, s, uv = src['_o'], src['_s'], src['_uv']
                if spec.get('flip'):
                    o = [-o[0] - s[0], o[1], o[2]]
                if 'dx' in spec:
                    o = [o[0] + spec['dx'], o[1], o[2]]
            else:
                o, s, paint = spec['o'], spec['s'], spec['paint']
                uv = self.alloc_uv(*s)
                self.paint_cube(uv, s, paint)
                if spec.get('emissive'):
                    if self.emissive is None:
                        self.emissive = Image.new('RGBA', (self.tex_w, self.tex_h), (0, 0, 0, 0))
                    self.paint_cube(uv, s, spec['emissive'], target='emissive')
                spec['_o'], spec['_s'], spec['_uv'] = o, s, uv
            cube = {'origin': [round(v, 2) for v in o], 'size': [round(v, 2) for v in s], 'uv': list(uv)}
            bone_cubes.append(cube)
        if bone_cubes:
            bone['cubes'] = bone_cubes
        self.bones.append(bone)
        return bone

    def paint_cube(self, uv, s, paint, target='base'):
        img = self.img if target == 'base' else self.emissive
        w, h, d = (v * self.uv_scale for v in s)
        u, v = uv
        faces = {
            'top': (u + d, v, w, d),
            'bottom': (u + d + w, v, w, d),
            'east': (u, v + d, d, h),
            'north': (u + d, v + d, w, h),
            'west': (u + d + w, v + d, d, h),
            'south': (u + d + w + d, v + d, w, h),
        }
        px = img.load()
        for face, (fx, fy, fw, fh) in faces.items():
            iw, ih = max(1, int(math.ceil(fw))), max(1, int(math.ceil(fh)))
            for yy in range(ih):
                for xx in range(iw):
                    col = paint(xx, yy, iw, ih, face)
                    if len(col) == 3:
                        col = (col[0], col[1], col[2], 255)
                    X, Y = int(fx) + xx, int(fy) + yy
                    if 0 <= X < self.tex_w and 0 <= Y < self.tex_h:
                        px[X, Y] = col

    def save(self, geo_path, tex_path, emissive_path=None):
        desc = {
            'identifier': self.identifier,
            'texture_width': self.tex_w,
            'texture_height': self.tex_h,
            'visible_bounds_width': self.visible_bounds[0],
            'visible_bounds_height': self.visible_bounds[1],
            'visible_bounds_offset': [0, round(self.visible_bounds[1] * 0.4, 2), 0],
        }
        geo = {'format_version': '1.12.0', 'minecraft:geometry': [{'description': desc, 'bones': self.bones}]}
        os.makedirs(os.path.dirname(geo_path), exist_ok=True)
        os.makedirs(os.path.dirname(tex_path), exist_ok=True)
        with open(geo_path, 'w') as f:
            json.dump(geo, f, indent=1)
        self.img.save(tex_path)
        if emissive_path and self.emissive is not None:
            self.emissive.save(emissive_path)
        print(f'saved {os.path.basename(geo_path)} ({len(self.bones)} bones) + {os.path.basename(tex_path)}')

# ---------------------------------------------------------------- animation helpers
def track(frames, lerp=None):
    """frames: list of (time, [x,y,z]); optional catmullrom lerp."""
    out = {}
    for item in frames:
        t, v = item[0], item[1]
        if lerp:
            out[str(t)] = {'vector': v, 'lerp_mode': lerp}
        else:
            out[str(t)] = v
    return out

def orbit_frames(radius, height, turns, duration, phase=0.0):
    n = max(8, turns * 8)
    frames = []
    for i in range(n + 1):
        t = round(duration * i / n, 3)
        ang = phase + (i / n) * turns * 2 * math.pi
        frames.append((t, [round(math.cos(ang) * radius, 3), height, round(math.sin(ang) * radius, 3)]))
    return frames

def build_anim(name, length, bones, loop=True):
    anim = {'animation_length': length, 'bones': bones}
    if loop:
        anim = {'loop': True, **anim}
    return anim

def save_anim(path, animations):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        json.dump({'format_version': '1.8.0', 'animations': animations}, f, indent=1)
    print(f'saved {os.path.basename(path)} ({len(animations)} animations)')
