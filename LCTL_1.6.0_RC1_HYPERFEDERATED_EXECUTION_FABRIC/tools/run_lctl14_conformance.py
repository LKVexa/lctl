#!/usr/bin/env python3
from pathlib import Path
import json, subprocess, tempfile, sys, time
ROOT=Path(__file__).resolve().parents[1]
JAR=ROOT/'runtime/bin/lctl-mesh.jar'
JAVA=['java','-jar',str(JAR)]

def run(args):
    return subprocess.run(JAVA+list(map(str,args)),cwd=ROOT,text=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE)

def good_json(cp):
    if cp.returncode!=0: return False
    try: return json.loads(cp.stdout).get('status','PASS') in ('PASS','PASS_WITH_SCOPE') or json.loads(cp.stdout).get('overall_pass') is True
    except Exception: return False

pos_files=sorted((ROOT/'conformance/1_4/positive').glob('*.json'))
neg_files=sorted((ROOT/'conformance/1_4/negative').glob('*.json'))
pos_pass=0; neg_pass=0; failures=[]
start=time.time()
for f in pos_files:
    x=json.loads(f.read_text())
    fam=x['family']
    if fam=='property': cp=run(['property-test',x['seed'],x['count']])
    elif fam=='metamorphic': cp=run(['metamorphic-test',x['seed'],x['count']])
    elif fam=='collective': cp=run(['run-collective',x['type'],x['workers']])
    elif fam=='protocol': cp=run(['protocol-run',ROOT/x['program'],x['seed']])
    else: cp=None
    ok=cp is not None and good_json(cp)
    if ok: pos_pass+=1
    else: failures.append({'id':x['id'],'stdout':cp.stdout if cp else '', 'stderr':cp.stderr if cp else 'unknown family'})
for f in neg_files:
    x=json.loads(f.read_text()); fam=x['family']
    if fam=='invalid_consistency': cp=run(['consistency-check',ROOT/x['program'],x['profile']])
    elif fam=='invalid_collective': cp=run(['run-collective',x['type'],x['workers']])
    elif fam=='invalid_protocol': cp=run(['protocol-run',ROOT/x['program'],x['seed']])
    elif fam=='corrupt_replay':
        tf=Path(tempfile.mkstemp(prefix='lctl14bad-',suffix='.trace')[1])
        tf.write_text(f"0|GENESIS|deadbeef|TASK|{x['variant']}|bad\nFINAL|deadbeef|badresult|badsource\n")
        cp=run(['reconstruct',tf]); tf.unlink(missing_ok=True)
    else: cp=None
    ok=cp is not None and cp.returncode!=0
    if ok: neg_pass+=1
    else: failures.append({'id':x['id'],'stdout':cp.stdout if cp else '', 'stderr':cp.stderr if cp else 'unknown family'})
result={'schema':'LCTL-1.4-CONFORMANCE/1.0','positive':{'total':len(pos_files),'pass':pos_pass,'fail':len(pos_files)-pos_pass},'negative':{'total':len(neg_files),'pass':neg_pass,'fail':len(neg_files)-neg_pass},'overall':{'total':len(pos_files)+len(neg_files),'pass':pos_pass+neg_pass,'fail':len(pos_files)+len(neg_files)-pos_pass-neg_pass},'elapsed_seconds':time.time()-start,'failures':failures}
print(json.dumps(result,indent=2))
sys.exit(0 if result['overall']['fail']==0 else 1)
