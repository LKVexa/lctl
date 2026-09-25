#!/usr/bin/env python3
from pathlib import Path
import hashlib, sys
ROOT=Path(__file__).resolve().parents[1]
manifest=ROOT/'PACKAGE_SHA256SUMS.txt'
bad=[]; checked=0
for line in manifest.read_text(encoding='utf-8').splitlines():
    if not line.strip(): continue
    h,rel=line.split('  ',1); p=ROOT/rel
    if not p.is_file(): bad.append((rel,'MISSING')); continue
    got=hashlib.sha256(p.read_bytes()).hexdigest(); checked+=1
    if got!=h: bad.append((rel,got))
print(f'checked={checked} bad={len(bad)}')
for x in bad[:20]: print('BAD',*x)
sys.exit(1 if bad else 0)
