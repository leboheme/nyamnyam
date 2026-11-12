#!/usr/bin/env python3
"""Compile a material-only room TMX into the runtime room JSON format."""

import argparse
import base64
import gzip
import json
import re
import struct
import zlib
import xml.etree.ElementTree as ET
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DESIGN_DIR = ROOT / "art_files" / "stages" / "rooms"
OUTPUT_DIR = ROOT / "assets" / "data" / "rooms"
GID_MASK = 0x1FFFFFFF
MATERIAL_CODES = {"floor": "f"}
JAVA_SOURCE = ROOT / "core" / "src" / "com" / "aramirezochoa" / "nyamnyam" / "screen" / "game" / "core" / "entity"


def enum_values(source_path, enum_name):
    source = source_path.read_text(encoding="utf-8")
    match = re.search(r"public enum " + re.escape(enum_name) + r"\b[^\{]*\{", source)
    if not match:
        raise ValueError("Could not read Java enum " + enum_name)
    start = match.end()
    depth = 1
    end = start
    while end < len(source):
        char = source[end]
        if char == "{":
            depth += 1
        elif char == "}":
            depth -= 1
        elif char == ";" and depth == 1:
            break
        end += 1
    return set(re.findall(r"\b([A-Z][A-Z0-9_]*)\s*(?=[(,;])", source[start:end]))


def read_content_layouts(map_root, width, height, tile_width, tile_height, source_path):
    groups = [group for group in map_root.findall("group") if group.get("name") == "content"]
    if len(groups) > 1:
        raise ValueError("Room TMX may contain only one group named 'content'")
    if not groups:
        return []

    enemy_types = enum_values(JAVA_SOURCE / "avatar" / "enemy" / "EnemyType.java", "EnemyType")
    meal_types = enum_values(JAVA_SOURCE / "meal" / "MealType.java", "MealType")
    layouts = []
    map_pixel_height = height * tile_height
    for layer in groups[0].findall("objectgroup"):
        layout = {"name": layer.get("name", ""), "enemies": [], "meals": []}
        for obj in layer.findall("object"):
            name = obj.get("name")
            if not name or obj.get("gid") is None:
                raise ValueError("Content objects must be named tile objects in " + str(source_path))
            x = float(obj.get("x", "0"))
            y = float(obj.get("y", "0"))
            tile_x = round(x / tile_width)
            tile_y = round((map_pixel_height - y) / tile_height)
            if abs(x - tile_x * tile_width) > 0.001 or abs((map_pixel_height - y) - tile_y * tile_height) > 0.001:
                raise ValueError("Content objects must be aligned to the room grid: " + name)
            if not (0 <= tile_x < width and 0 <= tile_y < height):
                raise ValueError("Content object outside room bounds: " + name)

            if name in enemy_types:
                facing = read_properties(obj).get("facing", "RIGHT").upper()
                if facing not in ("LEFT", "RIGHT"):
                    raise ValueError("Invalid facing for enemy " + name + ": " + facing)
                layout["enemies"].append({"type": name, "x": tile_x, "y": tile_y, "facing": facing})
            elif name in meal_types:
                if name.startswith("AIR") or name.startswith("CANDY"):
                    raise ValueError("Air and candy are generated dynamically, not authored in room layouts: " + name)
                properties = read_properties(obj)
                meal = {"type": name, "x": tile_x, "y": tile_y}
                if "expiring" in properties:
                    meal["expiring"] = properties["expiring"].lower() == "true"
                layout["meals"].append(meal)
            else:
                raise ValueError("Unknown enemy or meal enum name in content layer: " + name)

        layouts.append(layout)
    return layouts


def read_properties(element):
    result = {}
    properties = element.find("properties")
    if properties is not None:
        for prop in properties.findall("property"):
            result[prop.get("name")] = prop.get("value", prop.text or "")
    return result


def read_tileset_materials(map_root, source_path):
    materials = {}
    for tileset_ref in map_root.findall("tileset"):
        first_gid = int(tileset_ref.get("firstgid"))
        source = tileset_ref.get("source")
        tileset = tileset_ref
        if source:
            tileset_path = (source_path.parent / source).resolve()
            if not tileset_path.is_file():
                raise ValueError("Tileset file not found: " + str(tileset_path))
            tileset = ET.parse(tileset_path).getroot()
        for tile in tileset.findall("tile"):
            material = read_properties(tile).get("material")
            if material:
                materials[first_gid + int(tile.get("id"))] = material
    if not materials:
        raise ValueError("No tiles with a 'material' property were found")
    return materials


def read_layer(layer, width, height):
    if int(layer.get("width", width)) != width or int(layer.get("height", height)) != height:
        raise ValueError("Room layer dimensions must match the map")
    data = layer.find("data")
    if data is None:
        raise ValueError("Room layer has no tile data")

    encoding = data.get("encoding")
    if encoding == "base64":
        raw = base64.b64decode("".join((data.text or "").split()), validate=True)
        compression = data.get("compression")
        if compression == "zlib":
            raw = zlib.decompress(raw)
        elif compression == "gzip":
            raw = gzip.decompress(raw)
        elif compression:
            raise ValueError("Unsupported TMX compression: " + compression)
    elif encoding is None:
        values = [int(tile.get("gid", "0")) for tile in data.findall("tile")]
        if len(values) != width * height:
            raise ValueError("Room layer tile count does not match its dimensions")
        return [values[y * width:(y + 1) * width] for y in range(height)]
    else:
        raise ValueError("Unsupported TMX layer encoding: " + encoding)

    expected_bytes = width * height * 4
    if len(raw) != expected_bytes:
        raise ValueError("Room layer data has an unexpected byte length")
    values = struct.unpack("<" + "I" * (width * height), raw)
    return [list(values[y * width:(y + 1) * width]) for y in range(height)]


def compile_room(room_id, force=False):
    source = DESIGN_DIR / (room_id + ".tmx")
    if not source.is_file():
        raise ValueError("Room TMX not found: " + str(source))

    root = ET.parse(source).getroot()
    if root.tag != "map" or root.get("orientation") != "orthogonal" or root.get("infinite") == "1":
        raise ValueError("Room must be a finite orthogonal Tiled map")
    width, height = int(root.get("width")), int(root.get("height"))
    tile_width, tile_height = int(root.get("tilewidth")), int(root.get("tileheight"))
    if tile_width != tile_height:
        raise ValueError("Room tiles must be square")

    layers = [layer for layer in root.findall("layer") if layer.get("name") == "room"]
    if len(layers) != 1:
        raise ValueError("Room TMX must contain exactly one tile layer named 'room'")
    tileset_materials = read_tileset_materials(root, source)
    tile_data = read_layer(layers[0], width, height)

    rows = []
    for y, row in enumerate(tile_data):
        encoded_row = []
        for x, gid in enumerate(row):
            gid &= GID_MASK
            if not gid:
                encoded_row.append(".")
                continue
            material = tileset_materials.get(gid)
            code = MATERIAL_CODES.get(material)
            if code is None:
                raise ValueError("Unsupported material at ({}, {}): {}".format(x, y, material or "<missing>"))
            encoded_row.append(code)
        rows.append("".join(encoded_row))

    result = {
        "format": "nyam-room",
        "version": 1,
        "width": width,
        "height": height,
        "tileSize": tile_width,
        "materials": {code: material.upper() for material, code in MATERIAL_CODES.items()},
        "cells": rows,
        "layoutDefinitions": read_content_layouts(root, width, height, tile_width, tile_height, source),
    }

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    target = OUTPUT_DIR / (room_id + ".json")
    if target.exists() and not force:
        raise ValueError("Room JSON already exists; pass --force to replace it: " + str(target))
    target.write_text(json.dumps(result, separators=(",", ":")) + "\n", encoding="utf-8")
    print("Compiled room " + room_id + " to " + str(target.relative_to(ROOT)))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("room", help="Room ID, for example 7")
    parser.add_argument("--force", action="store_true", help="Replace an existing room JSON")
    args = parser.parse_args()
    if not args.room.isdigit():
        parser.error("Room ID must be numeric")
    compile_room(args.room, args.force)


if __name__ == "__main__":
    main()
