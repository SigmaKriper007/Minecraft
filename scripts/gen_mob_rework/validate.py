#!/usr/bin/env python3
"""Validate geo/animation/texture consistency for a list of asset triplets."""
import json, sys, math

def validate(geo_path, anim_path, tex_path):
    geo = json.load(open(geo_path))['minecraft:geometry'][0]
    tex = json.load(open(geo_path))  # for desc
    desc = geo['description']
    tw, th = desc['texture_width'], desc['texture_height']
    bones = {b['name']: b for b in geo['bones']}
    errors = []
    # uv bounds
    for b in geo['bones']:
        parent = b.get('parent')
        if parent and parent not in bones:
            errors.append(f"bone {b['name']}: missing parent {parent}")
        for c in b.get('cubes', []):
            u, v = c['uv']
            w, h, d = c['size']
            need_w, need_h = 2 * (w + d), d + h
            if u + need_w > tw + 0.01 or v + need_h > th + 0.01:
                errors.append(f"bone {b['name']}: uv ({u},{v}) + {need_w}x{need_h} exceeds {tw}x{th}")
            if u < -0.01 or v < -0.01:
                errors.append(f"bone {b['name']}: negative uv")
    # animations
    if anim_path:
        anims = json.load(open(anim_path))['animations']
        for aname, anim in anims.items():
            for bone_name in anim.get('bones', {}):
                if bone_name not in bones:
                    errors.append(f"anim {aname}: bone '{bone_name}' not in geo")
            length = anim.get('animation_length', 0)
            for bone_name, channels in anim.get('bones', {}).items():
                for ch in ('rotation', 'position'):
                    for t in (channels.get(ch) or {}):
                        try:
                            tv = float(t)
                        except ValueError:
                            errors.append(f"anim {aname}/{bone_name}/{ch}: bad time key {t}")
                            continue
                        if tv > length + 0.01:
                            errors.append(f"anim {aname}/{bone_name}/{ch}: key {t}s > length {length}s")
            # loop continuity for looping anims: first/last rotation keys should match mod 360
            if anim.get('loop'):
                for bone_name, channels in anim.get('bones', {}).items():
                    rot = channels.get('rotation') or {}
                    if not rot:
                        continue
                    keys = sorted(rot.keys(), key=float)
                    first, last = rot[keys[0]], rot[keys[-1]]
                    for i in range(3):
                        diff = abs(first[i] - last[i]) % 360
                        if min(diff, 360 - diff) > 1.0:
                            errors.append(f"anim {aname}/{bone_name}: loop discontinuity axis {i}: {first[i]} vs {last[i]}")
                            break
    print(('FAIL ' if errors else 'OK   ') + geo_path.split('/')[-1])
    for e in errors:
        print('   -', e)
    return not errors

if __name__ == '__main__':
    ok = True
    for triplet in sys.argv[1:]:
        geo, anim, tex = triplet.split(',')
        ok &= validate(geo, anim if anim != '-' else None, tex)
    sys.exit(0 if ok else 1)
