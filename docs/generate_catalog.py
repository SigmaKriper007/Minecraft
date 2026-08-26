#!/usr/bin/env python3
"""Build the documentation recipe/trophy catalog from authoritative mod resources."""

from __future__ import annotations

import json
import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
ASSETS = ROOT / "src/main/resources/assets/opusvsexe"
DATA = ROOT / "src/main/resources/data/opusvsexe"
LANG = json.loads((ASSETS / "lang/en_us.json").read_text(encoding="utf-8"))


def title(identifier: str) -> str:
    namespace, path = identifier.split(":", 1) if ":" in identifier else ("minecraft", identifier)
    if namespace == "opusvsexe":
        return LANG.get(f"item.opusvsexe.{path}", LANG.get(f"block.opusvsexe.{path}", path.replace("_", " ").title()))
    return path.replace("_", " ").title()


def ingredient(value) -> dict:
    if isinstance(value, list):
        choices = [ingredient(entry) for entry in value]
        return {"id": " / ".join(choice["id"] for choice in choices), "name": " or ".join(choice["name"] for choice in choices)}
    if "item" in value:
        return {"id": value["item"], "name": title(value["item"])}
    tag = value.get("tag", "unknown")
    return {"id": f"#{tag}", "name": f"Any {tag.split(':')[-1].replace('_', ' ')}"}


def result_of(recipe: dict) -> tuple[str, int]:
    result = recipe.get("result", {})
    if isinstance(result, str):
        return result, 1
    return result.get("item", "minecraft:air"), int(result.get("count", 1))


def resolve_texture(item_id: str) -> Path | None:
    if not item_id.startswith("opusvsexe:"):
        return None
    path = item_id.split(":", 1)[1]
    model_path = ASSETS / f"models/item/{path}.json"
    if model_path.exists():
        model = json.loads(model_path.read_text(encoding="utf-8"))
        texture = model.get("textures", {}).get("layer0") or model.get("textures", {}).get("all")
        if texture:
            namespace, texture_path = texture.split(":", 1) if ":" in texture else ("minecraft", texture)
            if namespace == "opusvsexe":
                candidate = ASSETS / f"textures/{texture_path}.png"
                if candidate.exists():
                    return candidate
    direct = ASSETS / f"textures/item/{path}.png"
    if direct.exists():
        return direct
    matches = list((ASSETS / "textures/block").rglob(f"{path}.png"))
    return matches[0] if matches else None


def copy_item_texture(item_id: str) -> str | None:
    source = resolve_texture(item_id)
    if source is None:
        return None
    destination = DOCS / "assets/items" / f"{item_id.split(':', 1)[1]}.png"
    destination.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source, destination)
    return destination.relative_to(DOCS).as_posix()


def recipe_record(path: Path) -> dict:
    recipe = json.loads(path.read_text(encoding="utf-8"))
    result_id, count = result_of(recipe)
    recipe_type = recipe["type"]
    record = {
        "id": path.stem,
        "name": title(result_id),
        "result": result_id,
        "count": count,
        "type": "Parthenon Forge" if recipe_type == "opusvsexe:parthenon_forging" else "Shapeless" if recipe_type.endswith("crafting_shapeless") else "Crafting Table",
        "image": copy_item_texture(result_id),
    }
    if "pattern" in recipe:
        record["pattern"] = recipe["pattern"]
        record["key"] = {symbol: ingredient(value) for symbol, value in recipe["key"].items()}
    else:
        ingredients = recipe.get("ingredients", [])
        record["ingredients"] = [ingredient(value) for value in ingredients]
        if recipe_type == "opusvsexe:parthenon_forging":
            record["catalyst"] = bool(recipe.get("catalyst_remainder", False))
    return record


def main() -> None:
    recipes = [recipe_record(path) for path in sorted((DATA / "recipes").glob("*.json"))]
    trophies = []
    for texture in sorted((ASSETS / "textures/item").glob("trophy_*.png")):
        item_id = texture.stem
        destination = DOCS / "assets/items" / texture.name
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(texture, destination)
        trophies.append({
            "id": item_id,
            "name": LANG.get(f"item.opusvsexe.{item_id}", title(f"opusvsexe:{item_id}")),
            "description": LANG.get(f"item.opusvsexe.{item_id}.desc", "A preserved echo from a defeated creature."),
            "image": destination.relative_to(DOCS).as_posix(),
        })

    retired_items = {"ember_essence", "blazing_trident", "cinder_bean", "cinder_ash", "cinder_crust"}
    entries = []
    for key, name in sorted(LANG.items(), key=lambda pair: pair[1].casefold()):
        match = re.fullmatch(r"(item|block)\.opusvsexe\.([^.]+)", key)
        if not match:
            continue
        kind, entry_id = match.groups()
        if kind == "item" and entry_id in retired_items:
            continue
        if kind == "block":
            category = "Blocks"
        elif entry_id.startswith("trophy_"):
            category = "Trophies"
        elif entry_id.endswith("_spawn_egg"):
            category = "Spawn Eggs"
        elif any(part in entry_id for part in ("helmet", "chestplate", "leggings", "boots")):
            category = "Armor"
        elif any(part in entry_id for part in ("pickaxe", "_axe", "shovel", "_hoe")):
            category = "Tools"
        elif any(part in entry_id for part in ("katana", "sword", "trident", "warhammer", "laser_gun")):
            category = "Weapons"
        else:
            category = "Materials & utility"
        entries.append({"id": entry_id, "name": name, "kind": kind.title(), "category": category,
                        "image": copy_item_texture(f"opusvsexe:{entry_id}")})

    feature_sources = {
        "japanese-settlement.jpg": ROOT / "concepts/japane.jpg",
        "young-samurai.jpg": ROOT / "concepts/young_samurai.jpg",
        "angel-boy.jpg": ROOT / "concepts/angel boy.jpg",
        "dark-forest.png": ROOT / "concepts/dark_forest/task38-creature-concept.png",
        "ruined-city.png": ROOT / "concepts/Ruined city.png",
    }
    for name, source in feature_sources.items():
        if source.exists():
            destination = DOCS / "assets/features" / name
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(source, destination)

    payload = {"version": "1.0.0", "recipes": recipes, "trophies": trophies, "entries": entries}
    (DOCS / "catalog.js").write_text(
        "window.OPUS_CATALOG = " + json.dumps(payload, ensure_ascii=False, separators=(",", ":")) + ";\n",
        encoding="utf-8",
    )
    print(f"DOCS_CATALOG_OK recipes={len(recipes)} trophies={len(trophies)} entries={len(entries)}")


if __name__ == "__main__":
    main()
