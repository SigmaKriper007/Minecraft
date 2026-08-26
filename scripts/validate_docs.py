#!/usr/bin/env python3
"""Dependency-free structural validation for the static documentation site."""

from __future__ import annotations

import re
import sys
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlparse


ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
INDEX = DOCS / "index.html"
VOID_TAGS = {
    "area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta",
    "param", "source", "track", "wbr",
}
OPTIONAL_CLOSE = {"li", "p", "td", "th", "tr"}
REQUIRED_SECTIONS = {
    "overview", "installation", "progression", "story", "world", "entities", "bosses",
    "armor", "arsenal", "controls", "crafting", "advancements", "mechanics", "status",
}


class SiteParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__(convert_charrefs=True)
        self.ids: set[str] = set()
        self.references: list[tuple[str, str]] = []
        self.stack: list[str] = []
        self.errors: list[str] = []

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        names: set[str] = set()
        values = dict(attrs)
        for name, _ in attrs:
            if name in names:
                self.errors.append(f"duplicate attribute {name!r} on <{tag}>")
            names.add(name)

        element_id = values.get("id")
        if element_id:
            if element_id in self.ids:
                self.errors.append(f"duplicate id #{element_id}")
            self.ids.add(element_id)

        for attr in ("href", "src"):
            value = values.get(attr)
            if value:
                self.references.append((attr, value))

        if tag not in VOID_TAGS:
            if tag in OPTIONAL_CLOSE and self.stack and self.stack[-1] == tag:
                self.stack.pop()
            self.stack.append(tag)

    def handle_startendtag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        self.handle_starttag(tag, attrs)
        if tag not in VOID_TAGS and self.stack and self.stack[-1] == tag:
            self.stack.pop()

    def handle_endtag(self, tag: str) -> None:
        if tag in VOID_TAGS:
            self.errors.append(f"void element </{tag}> must not have a closing tag")
            return
        if tag not in self.stack:
            self.errors.append(f"closing </{tag}> without matching start tag")
            return
        while self.stack:
            opened = self.stack.pop()
            if opened == tag:
                return
            if opened not in OPTIONAL_CLOSE:
                self.errors.append(f"<{opened}> closed implicitly by </{tag}>")


def validate_css(path: Path, errors: list[str]) -> None:
    css = path.read_text(encoding="utf-8")
    stripped = re.sub(r"/\*.*?\*/", "", css, flags=re.S)
    if stripped.count("{") != stripped.count("}"):
        errors.append(f"{path.relative_to(ROOT)} has unbalanced braces")
    for url in re.findall(r"url\(([^)]+)\)", stripped):
        target = url.strip(" \t\r\n\"'")
        if target.startswith(("data:", "#")):
            continue
        if urlparse(target).scheme:
            errors.append(f"external CSS URL is not allowed: {target}")
            continue
        if not (path.parent / unquote(target)).resolve().is_file():
            errors.append(f"missing CSS asset: {target}")


def validate_png_signature(path: Path, errors: list[str]) -> None:
    if not path.read_bytes().startswith(b"\x89PNG\r\n\x1a\n"):
        errors.append(f"{path.relative_to(ROOT)} has an invalid PNG signature")


def main() -> int:
    errors: list[str] = []
    if not INDEX.is_file():
        print("Documentation validation failed: docs/index.html is missing", file=sys.stderr)
        return 1

    parser = SiteParser()
    parser.feed(INDEX.read_text(encoding="utf-8"))
    parser.close()
    errors.extend(parser.errors)
    for tag in reversed(parser.stack):
        if tag not in OPTIONAL_CLOSE:
            errors.append(f"unclosed <{tag}> element")

    missing_sections = REQUIRED_SECTIONS - parser.ids
    if missing_sections:
        errors.append(f"missing required sections: {sorted(missing_sections)}")

    checked_files: set[Path] = set()
    for attr, reference in parser.references:
        parsed = urlparse(reference)
        if parsed.scheme in {"http", "https", "//"}:
            errors.append(f"external dependency is not allowed: {reference}")
            continue
        if reference.startswith("#"):
            target_id = unquote(reference[1:])
            if target_id and target_id not in parser.ids:
                errors.append(f"broken fragment link: {reference}")
            continue
        if parsed.scheme or reference.startswith(("mailto:", "tel:")):
            continue
        target = (DOCS / unquote(parsed.path)).resolve()
        if not target.is_file():
            errors.append(f"missing local {attr}: {reference}")
        else:
            checked_files.add(target)

    validate_css(DOCS / "styles.css", errors)
    for png in sorted((DOCS / "assets").glob("*.png")):
        validate_png_signature(png, errors)

    if errors:
        print("Documentation validation failed:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1

    print(
        f"Validated documentation: {len(parser.ids)} unique IDs, "
        f"{len(parser.references)} references and {len(checked_files)} linked files."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
