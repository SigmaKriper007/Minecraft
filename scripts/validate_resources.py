#!/usr/bin/env python3
"""Strict, dependency-free validation for the mod's authored resources."""

from __future__ import annotations

import json
import struct
import sys
import zlib
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src/main/resources"
ASSETS = RESOURCES / "assets/opusvsexe"


def unique_object(pairs: list[tuple[str, object]]) -> dict[str, object]:
    result: dict[str, object] = {}
    for key, value in pairs:
        if key in result:
            raise ValueError(f"duplicate JSON key {key!r}")
        result[key] = value
    return result


def validate_json(path: Path) -> object:
    with path.open(encoding="utf-8") as handle:
        return json.load(handle, object_pairs_hook=unique_object)


def validate_png(path: Path) -> None:
    data = path.read_bytes()
    if not data.startswith(b"\x89PNG\r\n\x1a\n"):
        raise ValueError("invalid PNG signature")
    offset = 8
    saw_header = False
    saw_end = False
    while offset + 12 <= len(data):
        length = struct.unpack(">I", data[offset:offset + 4])[0]
        kind = data[offset + 4:offset + 8]
        payload_end = offset + 8 + length
        crc_end = payload_end + 4
        if crc_end > len(data):
            raise ValueError("truncated PNG chunk")
        expected = struct.unpack(">I", data[payload_end:crc_end])[0]
        actual = zlib.crc32(kind + data[offset + 8:payload_end]) & 0xFFFFFFFF
        if expected != actual:
            raise ValueError(f"bad PNG CRC in {kind!r}")
        if kind == b"IHDR":
            width, height = struct.unpack(">II", data[offset + 8:offset + 16])
            if width <= 0 or height <= 0:
                raise ValueError("non-positive PNG dimensions")
            saw_header = True
        if kind == b"IEND":
            saw_end = True
            break
        offset = crc_end
    if not saw_header or not saw_end:
        raise ValueError("incomplete PNG")


def validate_ogg(path: Path) -> None:
    if not path.read_bytes().startswith(b"OggS"):
        raise ValueError("invalid Ogg container signature")


def validate_gecko_pairs() -> int:
    checked = 0
    for animation_path in sorted((ASSETS / "animations").rglob("*.animation.json")):
        relative = animation_path.relative_to(ASSETS / "animations")
        geo_relative = Path(str(relative).replace(".animation.json", ".geo.json"))
        geo_path = ASSETS / "geo" / geo_relative
        if not geo_path.exists():
            continue
        animation = validate_json(animation_path)
        geometry = validate_json(geo_path)
        model_bones = {
            bone["name"]
            for model in geometry.get("minecraft:geometry", [])
            for bone in model.get("bones", [])
        }
        animation_bones = {
            bone_name
            for clip in animation.get("animations", {}).values()
            for bone_name in clip.get("bones", {})
        }
        missing = animation_bones - model_bones
        if missing:
            raise ValueError(
                f"{animation_path.relative_to(ROOT)} references missing bones: {sorted(missing)}"
            )
        checked += 1
    return checked


def main() -> int:
    errors: list[str] = []
    json_files = sorted(RESOURCES.rglob("*.json"))
    png_files = sorted(RESOURCES.rglob("*.png"))
    ogg_files = sorted(RESOURCES.rglob("*.ogg"))

    for validator, paths in ((validate_json, json_files), (validate_png, png_files), (validate_ogg, ogg_files)):
        for path in paths:
            try:
                validator(path)
            except Exception as error:  # report every bad asset in one pass
                errors.append(f"{path.relative_to(ROOT)}: {error}")

    gecko_pairs = 0
    try:
        gecko_pairs = validate_gecko_pairs()
    except Exception as error:
        errors.append(str(error))

    if errors:
        print("Resource validation failed:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1

    print(
        f"Validated {len(json_files)} JSON, {len(png_files)} PNG, "
        f"{len(ogg_files)} OGG and {gecko_pairs} Gecko model/animation pairs."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
