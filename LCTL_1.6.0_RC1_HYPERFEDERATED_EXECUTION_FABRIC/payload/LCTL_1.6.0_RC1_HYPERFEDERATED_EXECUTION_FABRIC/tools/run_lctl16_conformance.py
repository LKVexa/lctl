#!/usr/bin/env python3
"""Run the bounded runtime selftest and audit descriptors; never claim descriptor execution."""
from pathlib import Path
import json, os, subprocess, sys
ROOT=Path(__file__).resolve().parents[1]
files={pol:sorted((ROOT/f'conformance/1_6/{pol}').glob('*.json')) for pol in ('positive','negative')}
fail=[]
for pol, paths in files.items():
    seen=set()
    for p in paths:
        try:
            x=json.loads(p.read_text(encoding='utf-8'))
            if x.get('schema')!='LCTL-1.6-CONFORMANCE-CASE/1.0' or x.get('polarity')!=pol or x.get('runtime_gate')!='fabric-selftest' or x.get('id') in seen:
                raise ValueError('invalid or duplicate descriptor')
            seen.add(x['id'])
        except Exception as e: fail.append({'path':str(p.relative_to(ROOT)),'reason':str(e)})
    if len(paths)!=512: fail.append({'polarity':pol,'reason':'expected 512 archived descriptors'})
try:
    cp=subprocess.run([os.environ.get('JAVA','java'),'-jar',str(ROOT/'runtime/bin/lctl-hyperfederated.jar'),'fabric-selftest'],cwd=ROOT,text=True,capture_output=True,timeout=120)
    result=json.loads(cp.stdout)
    runtime_ok=cp.returncode==0 and result.get('status')=='PASS'
except Exception as e:
    result={'error':str(e)};runtime_ok=False
ok=runtime_ok and not fail
print(json.dumps({'status':'PASS_WITH_SCOPE' if ok else 'FAIL','runtime_selftest':result,'descriptor_counts':{k:len(v) for k,v in files.items()},'descriptor_execution':False,'full_conformance_verified':False,'coverage_gap':'Archived 1.6 descriptors lack executable inputs and are not exercised by the selftest. Hash reflexivity and repeated argument rejection do not establish the named semantic groups.','descriptor_failures':fail},indent=2))
sys.exit(0 if ok else 1)
