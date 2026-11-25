#!/usr/bin/env python3
"""Compile an authored room TMX into the runtime room JSON format."""

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

    tool_tiles = read_tool_tiles(map_root, source_path)
    layouts = []
    map_pixel_height = height * tile_height
    for layer in groups[0].findall("objectgroup"):
        layout = {"name": layer.get("name", ""), "enemies": [], "meals": []}
        for obj in layer.findall("object"):
            if obj.get("gid") is None:
                raise ValueError("Content objects must use a tile from a tool tileset in " + str(source_path))
            tool = tool_tiles.get(int(obj.get("gid")) & GID_MASK)
            if not tool:
                raise ValueError("Object tile has no tool metadata in " + str(source_path))
            x = float(obj.get("x", "0"))
            y = float(obj.get("y", "0"))
            tile_x = round(x / tile_width)
            tile_y = round((map_pixel_height - y) / tile_height)
            if abs(x - tile_x * tile_width) > 0.001 or abs((map_pixel_height - y) - tile_y * tile_height) > 0.001:
                raise ValueError("Content objects must be aligned to the room grid: " + str(obj.get("name", "")))
            if not (0 <= tile_x < width and 0 <= tile_y < height):
                raise ValueError("Content object outside room bounds: " + str(obj.get("name", "")))

            if tool.get("kind") in ("enemy", "boss"):
                name = tool.get("enemyType")
                facing = read_properties(obj).get("facing", "RIGHT").upper()
                if facing not in ("LEFT", "RIGHT"):
                    raise ValueError("Invalid facing for enemy " + name + ": " + facing)
                layout["enemies"].append({"type": name, "x": tile_x, "y": tile_y, "facing": facing})
            elif tool.get("kind") == "meal":
                name = tool.get("mealType")
                if name.startswith("AIR") or name.startswith("CANDY"):
                    raise ValueError("Air and candy are generated dynamically, not authored in room layouts: " + name)
                properties = read_properties(obj)
                meal = {"type": name, "x": tile_x, "y": tile_y}
                if "expiring" in properties:
                    meal["expiring"] = properties["expiring"].lower() == "true"
                layout["meals"].append(meal)
            else:
                raise ValueError("Unsupported tool tile kind in content layer: " + str(tool.get("kind")))

        layouts.append(layout)
    return layouts


def read_properties(element):
    result = {}
    properties = element.find("properties")
    if properties is not None:
        for prop in properties.findall("property"):
            result[prop.get("name")] = prop.get("value", prop.text or "")
    return result


def read_footprint(map_root, source_path):
    value = read_properties(map_root).get("footprint")
    if value is None:
        if map_root.find("objectgroup[@name='connectors']") is not None:
            raise ValueError("Connectors require a footprint in " + str(source_path))
        return None  # Pre-modular rooms, including room 7.
    cells = set()
    for item in value.split(";"):
        match = re.fullmatch(r"\s*(\d+)\s*,\s*(\d+)\s*", item)
        if not match:
            raise ValueError("Invalid footprint cell: " + item)
        cell = (int(match.group(1)), int(match.group(2)))
        if cell in cells:
            raise ValueError("Duplicate footprint cell: " + item)
        cells.add(cell)
    if not cells or min(x for x, _ in cells) != 0 or min(y for _, y in cells) != 0:
        raise ValueError("Footprint must be nonempty and start at local x=0,y=0")
    visited = {next(iter(cells))}
    pending = list(visited)
    while pending:
        x, y = pending.pop()
        for neighbor in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)):
            if neighbor in cells and neighbor not in visited:
                visited.add(neighbor)
                pending.append(neighbor)
    if visited != cells:
        raise ValueError("Footprint is disconnected in " + str(source_path))
    return [{"x": x, "y": y} for x, y in sorted(cells)]


def connector_slots(footprint):
    cells = {(cell["x"], cell["y"]) for cell in footprint}
    slots = {}
    for side, dx, dy in (("W", -1, 0), ("E", 1, 0), ("S", 0, -1), ("N", 0, 1)):
        edges = [(x, y) for x, y in cells if (x + dx, y + dy) not in cells]
        edges.sort(key=lambda cell: (cell[1], cell[0]) if side in ("W", "E") else (cell[0], cell[1]))
        if len(edges) > 2:
            raise ValueError("More than two exterior slots on side " + side)
        for index, (x, y) in enumerate(edges):
            slots[side + str(index)] = (x, y)
    return slots


def read_connectors(map_root, footprint, width, height, tile_size, rows, source_path):
    groups = map_root.findall("objectgroup[@name='connectors']")
    if len(groups) > 1:
        raise ValueError("Room TMX may contain only one connectors layer")
    if not groups:
        return []
    slots = connector_slots(footprint)
    passes, arrivals = {}, {}
    for obj in groups[0].findall("object"):
        name = obj.get("name", "")
        if name.endswith("_arrival") and obj.find("point") is not None:
            slot = name[:-8]
            if slot in arrivals:
                raise ValueError("Duplicate arrival for " + slot)
            arrivals[slot] = (float(obj.get("x", "0")), float(obj.get("y", "0")))
        elif obj.find("polyline") is not None:
            if name in passes:
                raise ValueError("Duplicate connector " + name)
            points = obj.find("polyline").get("points", "").split()
            if len(points) != 2:
                raise ValueError("Connector must be a two-point line: " + name)
            xy = [tuple(float(v) for v in point.split(",")) for point in points]
            x0, y0 = float(obj.get("x", "0")) + xy[0][0], float(obj.get("y", "0")) + xy[0][1]
            x1, y1 = float(obj.get("x", "0")) + xy[1][0], float(obj.get("y", "0")) + xy[1][1]
            passes[name] = (x0, y0, x1, y1)
        else:
            raise ValueError("Connector objects must be pass lines or arrival points: " + name)
    if set(passes) != set(arrivals):
        raise ValueError("Every connector needs exactly one matching arrival point")
    result = []
    used_intervals = []
    for name, (x0, y0, x1, y1) in passes.items():
        if name not in slots:
            raise ValueError("Invalid or internal connector slot: " + name)
        side = name[0]
        length = 3 * tile_size if side in ("W", "E") else 2 * tile_size
        if side in ("W", "E"):
            valid = x0 == x1 and abs(y1 - y0) == length
        else:
            valid = y0 == y1 and abs(x1 - x0) == length
        values = (x0, y0, x1, y1)
        if not valid or any(abs(v / tile_size - round(v / tile_size)) > 0.001 for v in values):
            raise ValueError("Connector must be a grid-aligned 3/2-tile segment: " + name)
        if not all(0 <= x <= width * tile_size for x in (x0, x1)) or not all(0 <= y <= height * tile_size for y in (y0, y1)):
            raise ValueError("Connector outside room: " + name)
        # The outer tile ring is implicit and closed until navigation opens a slot.
        boundary = {"W": x0 == 0, "E": x0 == width * tile_size,
                    "N": y0 == 0, "S": y0 == height * tile_size}
        if not boundary[side]:
            raise ValueError("Connector must be on the physical room perimeter: " + name)
        start = min(y0, y1) if side in ("W", "E") else min(x0, x1)
        end = start + length
        if any(other_side == side and start < other_end and other_start < end
               for other_side, other_start, other_end in used_intervals):
            raise ValueError("Overlapping physical connectors on side " + side)
        used_intervals.append((side, start, end))
        column = round(min(x0, x1) / tile_size)
        row = round(min(y0, y1) / tile_size)
        if side in ("W", "E"):
            inside_x = 0 if side == "W" else width - 1
            inside_cells = [(inside_x, ry) for ry in range(row, row + 3)]
        else:
            inside_y = 0 if side == "N" else height - 1
            inside_cells = [(cx, inside_y) for cx in range(column, column + 2)]
        for cx, cy in inside_cells:
            if rows[cy][cx] != ".":
                raise ValueError("Connector passage is blocked by authored material: " + name)
        arrival_x, arrival_y = arrivals[name]
        if not (0 < arrival_x < width * tile_size and 0 < arrival_y < height * tile_size):
            raise ValueError("Arrival outside room interior: " + name)
        if side == "W" and not arrival_x > x0 or side == "E" and not arrival_x < x0 \
                or side == "N" and not arrival_y > y0 or side == "S" and not arrival_y < y0:
            raise ValueError("Arrival must be on the room side of the connector: " + name)
        if rows[int(arrival_y / tile_size)][int(arrival_x / tile_size)] != ".":
            raise ValueError("Arrival overlaps solid material: " + name)
        module_x, module_y = slots[name]
        result.append({"slot": name, "moduleX": module_x, "moduleY": module_y,
                       "x": min(x0, x1), "y": height * tile_size - max(y0, y1),
                       "length": length, "arrivalX": arrival_x,
                       "arrivalY": height * tile_size - arrival_y})
    for side in "WESN":
        same_side = sorted((item for item in result if item["slot"].startswith(side)),
                           key=lambda item: item["slot"])
        positions = [item["y"] if side in "WE" else item["x"] for item in same_side]
        if positions != sorted(positions):
            raise ValueError("Physical connector order differs from slot order on side " + side)
    return sorted(result, key=lambda item: item["slot"])


def read_attractors(map_root, width, height, tile_width, tile_height, source_path):
    groups = [group for group in map_root.findall("objectgroup") if group.get("name") == "attractors"]
    if len(groups) > 1:
        raise ValueError("Room TMX may contain only one object layer named 'attractors'")
    if not groups:
        return []
    result = []
    map_pixel_height = height * tile_height
    for obj in groups[0].findall("object"):
        tool = read_tool_tiles(map_root, source_path).get(int(obj.get("gid", "0")) & GID_MASK)
        if not tool or tool.get("kind") != "bubbleAttractor":
            raise ValueError("Room objects must use a bubble attractor tool tile in " + str(source_path))
        x = (float(obj.get("x", "0")) + float(obj.get("width", "0")) / 2) / tile_width
        y = (map_pixel_height - float(obj.get("y", "0")) - float(obj.get("height", "0")) / 2) / tile_height
        if not (0 <= x <= width and 0 <= y <= height):
            raise ValueError("Bubble attractor outside room bounds in " + str(source_path))
        result.append({"x": x, "y": y})
    return result


def read_tool_tiles(map_root, source_path):
    result = {}
    for tileset_ref in map_root.findall("tileset"):
        first_gid = int(tileset_ref.get("firstgid"))
        source = tileset_ref.get("source")
        tileset = tileset_ref
        if source:
            path = (source_path.parent / source).resolve()
            if not path.is_file():
                raise ValueError("Tileset file not found: " + str(path))
            tileset = ET.parse(path).getroot()
        for tile in tileset.findall("tile"):
            properties = read_properties(tile)
            if "kind" in properties:
                result[first_gid + int(tile.get("id"))] = properties
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
    footprint = read_footprint(root, source)

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
    connectors = read_connectors(root, footprint, width, height, tile_width, rows, source) if footprint is not None else None

    result = {
        "format": "nyam-room",
        "version": 1,
        "width": width,
        "height": height,
        "tileSize": tile_width,
        "materials": {code: material.upper() for material, code in MATERIAL_CODES.items()},
        "cells": rows,
        "layoutDefinitions": read_content_layouts(root, width, height, tile_width, tile_height, source),
        "attractors": read_attractors(root, width, height, tile_width, tile_height, source),
    }
    if footprint is not None:
        result["footprint"] = footprint
        result["connectors"] = connectors

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
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9_-]*", args.room):
        parser.error("Room ID must contain only letters, digits, _ or -")
    compile_room(args.room, args.force)


if __name__ == "__main__":
    main()
