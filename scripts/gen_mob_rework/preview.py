#!/usr/bin/env python3
"""Crude isometric preview renderer for Blockbench geo models (sanity check)."""
import json, math, sys
from PIL import Image, ImageDraw

def load(geo_path, tex_path):
    geo = json.load(open(geo_path))['minecraft:geometry'][0]
    tex = Image.open(tex_path).convert('RGBA')
    return geo, tex

UV_SCALE = 1.0

def cube_uv_color(tex, uv, size, face):
    w, h, d = (x * UV_SCALE for x in size)
    u, v = uv
    rects = {
        'top': (u + d, v, w, d), 'bottom': (u + d + w, v, w, d),
        'east': (u, v + d, d, h), 'north': (u + d, v + d, w, h),
        'west': (u + d + w, v + d, d, h), 'south': (u + d + w + d, v + d, w, h),
    }
    fx, fy, fw, fh = rects[face]
    fw, fh = max(1, int(fw)), max(1, int(fh))
    r = g = b = a = n = 0
    for yy in range(fh):
        for xx in range(fw):
            px = tex.getpixel((min(int(fx) + xx, tex.width - 1), min(int(fy) + yy, tex.height - 1)))
            r += px[0]; g += px[1]; b += px[2]; a += px[3]; n += 1
    return (r // n, g // n, b // n, a // n)

DEBUG_BONES = set()
def render(geo_path, tex_path, out_path, yaw_deg=35, pitch_deg=18, scale=6, size=(560, 560)):
    geo, tex = load(geo_path, tex_path)
    yaw = math.radians(yaw_deg)
    pitch = math.radians(pitch_deg)
    cy, sy = math.cos(yaw), math.sin(yaw)
    cp, sp = math.cos(pitch), math.sin(pitch)

    def project(p):
        x, y, z = p
        x2 = x * cy + z * sy
        z2 = -x * sy + z * cy
        y2 = y * cp - z2 * sp
        z3 = y * sp + z2 * cp
        return (size[0] / 2 + x2 * scale, size[1] / 2 - y2 * scale), z3

    count = {}
    img = Image.new('RGBA', size, (24, 24, 28, 255))
    dr = ImageDraw.Draw(img)

    # gather world-space faces
    faces = []
    for bone in geo['bones']:
        # compose transforms
        chain = []
        b = bone
        while b is not None:
            chain.append(b)
            parent = b.get('parent')
            b = next((x for x in geo['bones'] if x['name'] == parent), None)
        def xform(point):
            # apply bone rotations from root down
            for bn in reversed(chain):
                px, py, pz = bn['pivot']
                rot = bn.get('rotation', [0, 0, 0])
                rx, ry, rz = (math.radians(a) for a in rot)
                # bedrock order: Z, then X, then Y? Blockbench uses ZYX? use Z*X*Y
                x, y, z = point[0] - px, point[1] - py, point[2] - pz
                if ry:
                    x, z = x * math.cos(ry) + z * math.sin(ry), -x * math.sin(ry) + z * math.cos(ry)
                if rx:
                    y, z = y * math.cos(rx) - z * math.sin(rx), y * math.sin(rx) + z * math.cos(rx)
                if rz:
                    x, y = x * math.cos(rz) - y * math.sin(rz), x * math.sin(rz) + y * math.cos(rz)
                point = (x + px, y + py, z + pz)
            return point
        for cube in bone.get('cubes', []):
            ox, oy, oz = cube['origin']
            w, h, d = cube['size']
            v = cube['uv']
            base_col = cube_uv_color(tex, v, [w, h, d], 'north')
            corners = {}
            for name, (cx, cy2, cz2) in {
                'nnn': (ox, oy, oz), 'nnf': (ox + w, oy, oz), 'nfn': (ox, oy + h, oz), 'nff': (ox + w, oy + h, oz),
                'ffn': (ox, oy, oz + d), 'fff': (ox + w, oy, oz + d), 'ffn2': (ox, oy + h, oz + d), 'ffff': (ox + w, oy + h, oz + d),
            }.items():
                corners[name] = xform((cx, cy2, cz2))
            quads = [
                ('top', ['nfn', 'nff', 'ffff', 'ffn2'], 1.12),
                ('bottom', ['nnn', 'ffn', 'fff', 'nnf'], 0.6),
                ('north', ['nnn', 'nnf', 'nff', 'nfn'], 0.86),
                ('south', ['ffn', 'ffn2', 'ffff', 'fff'], 0.86),
                ('east', ['nnf', 'fff', 'ffff', 'nff'], 0.74),
                ('west', ['nnn', 'nfn', 'ffn2', 'ffn'], 0.74),
            ]
            debug = bone['name'] in DEBUG_BONES
            for face, names, shade_f in quads:
                pts3 = [corners[n] for n in names]
                col = cube_uv_color(tex, v, [w, h, d], face)
                col = (int(col[0] * shade_f), int(col[1] * shade_f), int(col[2] * shade_f), 255)
                if debug:
                    hsh = abs(hash(bone['name'])) % 360
                    import colorsys
                    rgb = colorsys.hls_to_rgb(hsh/360, 0.55, 0.7)
                    col = (int(rgb[0]*255), int(rgb[1]*255), int(rgb[2]*255), 255)
                projected = []
                depth = 0
                for p3 in pts3:
                    p2, z = project(p3)
                    projected.append(p2)
                    depth += z
                # normal culling: skip back faces via winding
                (x1, y1), (x2, y2), (x3, y3) = projected[0], projected[1], projected[2]
                faces.append((depth / 4, projected, col))
                count[bone['name']] = count.get(bone['name'], 0) + 1
    faces.sort(key=lambda f: -f[0])
    for _, pts, col in faces:
        dr.polygon(pts, fill=col)
    img.save(out_path)
    print('rendered', out_path)
    print('faces:', sorted(count.items()))

if __name__ == '__main__':
    render(sys.argv[1], sys.argv[2], sys.argv[3],
           yaw_deg=float(sys.argv[4]) if len(sys.argv) > 4 else 35,
           scale=float(sys.argv[5]) if len(sys.argv) > 5 else 6)
