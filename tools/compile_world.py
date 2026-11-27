#!/usr/bin/env python3
"""Compile all authored rooms referenced by a world catalog."""

import argparse
import json
import re

from compile_stage import ROOT, OUTPUT_DIR, compile_room


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("world", help="World catalog name, for example world_01")
    parser.add_argument("--force", action="store_true", help="Replace existing room JSON files")
    args = parser.parse_args()
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9_-]*", args.world):
        parser.error("Invalid world catalog name")
    catalog = ROOT / "assets" / "data" / "worlds" / (args.world + ".json")
    rooms = json.loads(catalog.read_text(encoding="utf-8"))["rooms"]
    if not rooms or len(set(rooms)) != len(rooms):
        parser.error("World rooms must be nonempty and unique")
    if not args.force:
        existing = [room for room in rooms if (OUTPUT_DIR / (room + ".json")).exists()]
        if existing:
            parser.error("Room JSON already exists; pass --force to replace: " + ", ".join(existing))
    for room in rooms:
        compile_room(room, args.force)


if __name__ == "__main__":
    main()
