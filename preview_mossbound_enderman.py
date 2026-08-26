#!/usr/bin/env python3
"""Render a lightweight multi-view QA sheet for the GeckoLib bind pose."""

import json
import math
from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent
GEO = ROOT / "src/main/resources/assets/opusvsexe/geo/dark_forest/mossbound_enderman.geo.json"
OUT = ROOT / "build/preview_mossbound_enderman/mossbound_enderman_multiview.png"
COLORS = {
    (2, 2): (23, 19, 26), (66, 2): (50, 39, 50), (130, 2): (48, 66, 21), (194, 2): (80, 91, 27),
    (2, 130): (73, 48, 22), (66, 130): (50, 43, 40), (130, 130): (221, 218, 190), (194, 130): (58, 205, 220),
}


def identity(): return [[1, 0, 0, 0], [0, 1, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]]


def mul(a, b): return [[sum(a[i][k] * b[k][j] for k in range(4)) for j in range(4)] for i in range(4)]


def translate(x, y, z):
    m = identity(); m[0][3], m[1][3], m[2][3] = x, y, z; return m


def rotate_x(a):
    c, s = math.cos(a), math.sin(a); m = identity(); m[1][1], m[1][2], m[2][1], m[2][2] = c, -s, s, c; return m


def rotate_y(a):
    c, s = math.cos(a), math.sin(a); m = identity(); m[0][0], m[0][2], m[2][0], m[2][2] = c, s, -s, c; return m


def rotate_z(a):
    c, s = math.cos(a), math.sin(a); m = identity(); m[0][0], m[0][1], m[1][0], m[1][1] = c, -s, s, c; return m


def apply(m, p):
    v = [p[0], p[1], p[2], 1]
    return [sum(m[i][k] * v[k] for k in range(4)) for i in range(3)]


def bone_matrix(entry, parent):
    px, py, pz = entry["pivot"]
    rx, ry, rz = [math.radians(v) for v in entry.get("rotation", [0, 0, 0])]
    local = mul(translate(px, py, pz), mul(rotate_z(rz), mul(rotate_y(ry), mul(rotate_x(rx), translate(-px, -py, -pz)))))
    return mul(parent, local)


def material(box):
    north = box["uv"]["north"]["uv"]
    return COLORS.get(tuple(north), (100, 40, 100))


def shade(color, factor): return tuple(max(0, min(255, int(channel * factor))) for channel in color)


def collect():
    bones = json.loads(GEO.read_text(encoding="utf-8"))["minecraft:geometry"][0]["bones"]
    by_name, matrices = {entry["name"]: entry for entry in bones}, {}

    def resolve(name):
        if name in matrices: return matrices[name]
        entry = by_name[name]
        parent = resolve(entry["parent"]) if entry.get("parent") else identity()
        matrices[name] = bone_matrix(entry, parent)
        return matrices[name]

    faces = []
    face_indices = [
        ((0, 4, 6, 2), .72), ((1, 3, 7, 5), 1.02), ((0, 1, 5, 4), .82),
        ((2, 6, 7, 3), 1.15), ((0, 2, 3, 1), .9), ((4, 5, 7, 6), .66),
    ]
    for entry in bones:
        matrix = resolve(entry["name"])
        for box in entry.get("cubes", []):
            x, y, z = box["origin"]; w, h, d = box["size"]
            points = [apply(matrix, p) for p in ((x, y, z), (x+w, y, z), (x, y+h, z), (x+w, y+h, z), (x, y, z+d), (x+w, y, z+d), (x, y+h, z+d), (x+w, y+h, z+d))]
            color = material(box)
            for indices, light in face_indices: faces.append(([points[i] for i in indices], shade(color, light)))
    return faces


def camera(point, yaw, pitch):
    x, y, z = point[0], point[1], point[2]
    cy, sy, cp, sp = math.cos(yaw), math.sin(yaw), math.cos(pitch), math.sin(pitch)
    x, z = cy*x + sy*z, -sy*x + cy*z
    y, z = cp*y - sp*z, sp*y + cp*z
    return x, y, z


def render_view(canvas, faces, box, yaw, pitch, title, scale=5.3, player=False):
    draw = ImageDraw.Draw(canvas)
    x0, y0, x1, y1 = box; cx, cy = (x0+x1)/2, y1-80
    projected = []
    for points, color in faces:
        transformed = [camera(p, yaw, pitch) for p in points]
        projected.append((sum(p[2] for p in transformed)/4, [(cx+p[0]*scale, cy-p[1]*scale) for p in transformed], color))
    for _, polygon, color in sorted(projected, key=lambda item: item[0], reverse=True):
        draw.polygon(polygon, fill=color, outline=shade(color, .52))
    draw.text((x0+18, y0+16), title, fill=(220, 228, 224))
    draw.line((x0+16, y1-78, x1-16, y1-78), fill=(70, 77, 72), width=2)
    if player:
        px = x1-52; ground = y1-80
        draw.rectangle((px-9, ground-32*scale, px+9, ground-24*scale), fill=(105, 110, 109))
        draw.rectangle((px-11, ground-24*scale, px+11, ground-10*scale), fill=(90, 95, 94))
        draw.rectangle((px-9, ground-10*scale, px-1, ground), fill=(78, 83, 82)); draw.rectangle((px+1, ground-10*scale, px+9, ground), fill=(78, 83, 82))


def main():
    faces = collect()
    canvas = Image.new("RGB", (1500, 900), (11, 14, 13))
    render_view(canvas, faces, (0, 0, 500, 900), math.radians(-28), math.radians(7), "THREE-QUARTER", 5.0)
    render_view(canvas, faces, (500, 0, 1000, 900), 0, 0, "FRONT + PLAYER SCALE", 5.1, True)
    render_view(canvas, faces, (1000, 0, 1500, 900), math.radians(90), 0, "SIDE", 5.1)
    OUT.parent.mkdir(parents=True, exist_ok=True); canvas.save(OUT); print(OUT)


if __name__ == "__main__": main()
