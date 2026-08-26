#!/usr/bin/env python3
"""Regenerates all reworked mob assets (DarkForest + Paradise + Hurricane).

Usage: python3 gen_all.py            # from anywhere
       python3 gen_all.py --preview  # also renders isometric previews to /tmp
"""
import subprocess, sys, os

HERE = os.path.dirname(os.path.abspath(__file__))
for script in ("darkforest.py", "paradise_mobs.py", "hurricane.py"):
    print(f"== {script}")
    subprocess.run([sys.executable, os.path.join(HERE, script)], check=True)

if "--preview" in sys.argv:
    preview = os.path.join(HERE, "preview.py")
    R = os.path.normpath(os.path.join(HERE, "..", "..", "src", "main", "resources", "assets", "opusvsexe"))
    sys.path.insert(0, HERE)
    import preview
    preview.DEBUG_BONES = set()
    preview.UV_SCALE = 1.0
    jobs = [
        ("geo/dark_forest/shade_spiderling", "textures/dark_forest/entity/shade_spiderling", 9),
        ("geo/dark_forest/gloom_broodmother", "textures/dark_forest/entity/gloom_broodmother", 4.2),
        ("geo/dark_forest/moonwing_bat", "textures/dark_forest/entity/moonwing_bat", 7),
        ("geo/paradise/sunfinch", "textures/paradise/entity/sunfinch", 22),
        ("geo/paradise/cloud_grazer", "textures/paradise/entity/cloud_grazer", 13),
        ("geo/paradise/paradise_wyvern", "textures/paradise/entity/paradise_wyvern", 5.2),
        ("geo/paradise/wind_core", "textures/paradise/entity/wind_core", 26),
        ("geo/paradise/hurricane", "textures/paradise/entity/hurricane", 13),
    ]
    for geo, tex, scale in jobs:
        name = geo.split("/")[-1]
        preview.render(f"{R}/{geo}.geo.json", f"{R}/{tex}.png", f"/tmp/preview_{name}.png", 35, 12, scale, (700, 700))
