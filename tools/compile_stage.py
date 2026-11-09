#!/usr/bin/env python3
"""Compile a material-only room TMX into the runtime room JSON format."""

import argparse
import base64
import gzip
import json
import struct
import zlib
import xml.etree.ElementTree as ET
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DESIGN_DIR = ROOT / "art_files" / "stages" / "rooms"
OUTPUT_DIR = ROOT / "assets" / "data" / "rooms"
GID_MASK = 0x1FFFFFFF
MATERIAL_CODES = {"floor": "f"}


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
