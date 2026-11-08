#!/usr/bin/env python3
"""Copy designed TMX stages into the runtime assets folder."""

import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DESIGN_DIR = ROOT / "art_files" / "stages" / "tmx"
RUNTIME_DIR = ROOT / "assets" / "data" / "stages"
IMAGE_SOURCE = re.compile(r'(<image\b[^>]*\bsource=")([^"]+)(")')


def promote_stage(stage_number):
    source = DESIGN_DIR / (stage_number + ".tmx")
    target = RUNTIME_DIR / (stage_number + ".tmx")
    if not source.is_file():
        raise ValueError("Design TMX not found: " + str(source))

    content = source.read_text(encoding="utf-8")
    root = ET.fromstring(content)
    if root.tag != "map":
        raise ValueError("Not a TMX map: " + str(source))

    replacements = {}
    for image in root.iter("image"):
        image_source = image.get("source")
        image_name = Path(image_source).name
        if not (RUNTIME_DIR / image_name).is_file():
            raise ValueError("Runtime tileset image not found: " + image_name)
        replacements[image_source] = image_name

    def replace_image_source(match):
        return match.group(1) + replacements.get(match.group(2), match.group(2)) + match.group(3)

    content = IMAGE_SOURCE.sub(replace_image_source, content)
    ET.fromstring(content)
    target.write_text(content, encoding="utf-8")
    print("Promoted stage " + stage_number + " to " + str(target.relative_to(ROOT)))


def main():
    if len(sys.argv) < 2:
        raise SystemExit("Usage: python3 tools/promote_stages.py <level> [<level> ...]")
    for stage_number in sys.argv[1:]:
        if not stage_number.isdigit():
            raise ValueError("Stage numbers must be numeric: " + stage_number)
        promote_stage(stage_number)


if __name__ == "__main__":
    main()
