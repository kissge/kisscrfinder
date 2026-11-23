#!/usr/bin/env python3
"""Generate app/src/main/assets/radicals.bin from the Unicode Unihan database.

Byte i of the output is the Kangxi radical number (1-214) of code point
FIRST + i, or 0 if it is not a CJK ideograph. Source field: kRSUnicode.
Unihan data is (c) Unicode, Inc., under the Unicode License v3.

Usage: python3 -I scripts/generate_radicals.py
"""

import io
import pathlib
import re
import urllib.request
import zipfile

URL = "https://www.unicode.org/Public/UCD/latest/ucd/Unihan.zip"
FIRST, LAST = 0x3400, 0xFAFF  # CJK Ext A .. CJK Compatibility Ideographs
OUT = pathlib.Path(__file__).resolve().parent.parent / "app/src/main/assets/radicals.bin"

LINE = re.compile(r"^U\+([0-9A-F]+)\tkRSUnicode\t(\d+)")


def main() -> None:
    with urllib.request.urlopen(URL) as response:
        archive = zipfile.ZipFile(io.BytesIO(response.read()))

    table = bytearray(LAST - FIRST + 1)
    count = 0
    for name in archive.namelist():
        if not name.endswith(".txt"):
            continue
        for line in archive.read(name).decode("utf-8").splitlines():
            match = LINE.match(line)
            if not match:
                continue
            code_point, radical = int(match[1], 16), int(match[2])
            if FIRST <= code_point <= LAST and 1 <= radical <= 214:
                table[code_point - FIRST] = radical
                count += 1

    OUT.write_bytes(bytes(table))
    print(f"wrote {OUT} ({len(table)} bytes, {count} ideographs)")


if __name__ == "__main__":
    main()
