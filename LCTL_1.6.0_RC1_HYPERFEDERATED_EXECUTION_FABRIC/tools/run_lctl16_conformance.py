#!/usr/bin/env python3
from pathlib import Path
import json, subprocess, sys
ROOT=Path(__file__).resolve().parents[1]
pos=sorted((ROOT/'conformance/1_6/positive').glob('*.json'))
neg=sorted((ROOT/'conformance/1_6/negative').glob('*.json'))
cp=subprocess.run(['java','-jar',str(ROOT/'runtime/bin/lctl-hyperfederated.jar'),'fabric-selftest'],cwd=ROOT,text=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
try: result=json.loads(cp.stdout)
except Exception:
    print(json.dumps({'status':'FAIL','reason':'invalid runtime result','stderr':cp.stderr},indent=2)); sys.exit(1)
result['descriptor_positive_count']=len(pos)
result['descriptor_negative_count']=len(neg)
result['descriptor_count_match']=(len(pos)==512 and len(neg)==512)
result['overall_pass']=(cp.returncode==0 and result.get('status')=='PASS' and result['descriptor_count_match'])
print(json.dumps(result,indent=2))
sys.exit(0 if result['overall_pass'] else 1)
