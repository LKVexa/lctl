#!/usr/bin/env python3
from pathlib import Path
import hashlib,json,re,sys
ROOT=Path(__file__).resolve().parents[1];bad=[];seen=set();checked=0
for n,line in enumerate((ROOT/'PACKAGE_SHA256SUMS.txt').read_text(encoding='utf-8').splitlines(),1):
    if not line.strip(): continue
    match=re.fullmatch(r'([0-9a-f]{64})  (.+)',line)
    if not match: bad.append({'line':n,'reason':'malformed checksum record'});continue
    digest,rel=match.groups();p=(ROOT/rel).resolve()
    if rel in seen or not p.is_relative_to(ROOT) or not p.is_file(): bad.append({'path':rel,'reason':'duplicate, escaping or missing path'});continue
    seen.add(rel);checked+=1
    if hashlib.sha256(p.read_bytes()).hexdigest()!=digest: bad.append({'path':rel,'reason':'sha256 mismatch'})
if not checked: bad.append({'reason':'empty checksum manifest'})
print(json.dumps({'status':'PASS' if not bad else 'FAIL','checked':checked,'failures':bad},indent=2))
sys.exit(1 if bad else 0)
