#!/usr/bin/env python3
from pathlib import Path
import json,os,re,subprocess,sys,tempfile
ROOT=Path(__file__).resolve().parents[1]
families={'REMOTE_CNOT_AND_GCOUNTER':'G-COUNTER','REMOTE_CNOT_AND_PNCOUNTER':'PN-COUNTER','REMOTE_CNOT_AND_GSET':'G-SET','REMOTE_CNOT_AND_ORSET':'OR-SET'}
fail=[];cases=[]
for pol in ('positive','negative'):
    files=sorted((ROOT/f'conformance/1_5/{pol}').glob('*.json'));seen=set()
    if len(files)!=256: fail.append({'polarity':pol,'error':'expected 256 cases'})
    for p in files:
        try:
            x=json.loads(p.read_text(encoding='utf-8'));case=x['id'];replicas=x['replicas']
            if not re.fullmatch('[PN][0-9]{3}',case) or case in seen or type(replicas) is not int: raise ValueError('invalid descriptor identity or replica shape')
            seen.add(case)
            if pol=='positive':
                if x['family'] not in families or type(x['seed']) is not int: raise ValueError('unsupported positive family/seed')
                cases.append(f"P\t{case}\t{x['seed']}\t{replicas}\t{families[x['family']]}\n")
            else:
                if x['family']!='INVALID_CRDT_TYPE' or not re.fullmatch('INVALID_[0-9]+',x['type']): raise ValueError('unsupported negative shape')
                cases.append(f"N\t{case}\t0\t{replicas}\t{x['type']}\n")
        except Exception as e: fail.append({'path':str(p.relative_to(ROOT)),'reason':str(e)})
if fail:
    print(json.dumps({'status':'FAIL','descriptor_failures':fail},indent=2));sys.exit(1)
scratch=ROOT/'build/audit-temp';scratch.mkdir(parents=True,exist_ok=True)
with tempfile.TemporaryDirectory(prefix='descriptor-1-5-',dir=scratch) as temp:
    path=Path(temp)/'cases.tsv';path.write_text(''.join(cases),encoding='utf-8')
    try:
        cp=subprocess.run([os.environ.get('JAVA','java'),'-cp',str(ROOT/'runtime/bin/lctl-federated.jar'),'LctlDescriptorConformanceMain',str(path)],cwd=ROOT,text=True,capture_output=True,timeout=120)
        result=json.loads(cp.stdout)
        print(json.dumps(result,indent=2));sys.exit(0 if cp.returncode==0 and result.get('status')=='PASS_WITH_SCOPE' else 1)
    except Exception as e: print(json.dumps({'status':'FAIL','reason':str(e)},indent=2));sys.exit(1)
