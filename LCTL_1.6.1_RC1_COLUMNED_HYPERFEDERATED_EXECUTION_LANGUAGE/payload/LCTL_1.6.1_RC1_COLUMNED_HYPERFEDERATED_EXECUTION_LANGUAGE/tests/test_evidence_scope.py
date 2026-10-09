from pathlib import Path
import json,os,subprocess,tempfile
ROOT=Path(__file__).resolve().parents[1];JAVA=os.environ.get('JAVA','java');checks=0

def check(ok,message):
    global checks
    checks+=1
    if not ok: raise AssertionError(message)
def run(jar,*args):
    cp=subprocess.run([JAVA,'-jar',str(ROOT/'runtime/bin'/jar),*map(str,args)],text=True,capture_output=True,encoding='utf-8',timeout=30)
    return cp,json.loads(cp.stdout)
scratch=ROOT/'build/audit-temp';scratch.mkdir(parents=True,exist_ok=True)
with tempfile.TemporaryDirectory(prefix='lctl-authority-regression-',dir=scratch) as temp:
    p=Path(temp)/'spoof.json';p.write_text(json.dumps({'physical':True,'endpoint':'mock','auth':'unsigned-placeholder'}),encoding='utf-8')
    for jar,command in [('lctl-federated.jar','adapter-check3'),('lctl-hyperfederated.jar','adapter-check4')]:
        cp,x=run(jar,command,p);check(cp.returncode==3 and x['status']=='BLOCKED_EXTERNAL_AUTHORITY','unsigned physical adapter blocked '+command)
        base=x.get('base',x);check(base['authenticated_evidence_present'] is False and base['authentication_verified'] is False,'declared text cannot authenticate '+command)
    p.write_text(json.dumps({'physical':False,'endpoint':'mock','auth':'unsigned-placeholder'}),encoding='utf-8')
    cp,x=run('lctl-hyperfederated.jar','adapter-check4',p);check(cp.returncode==0 and x['status']=='PASS_WITH_SCOPE','nonphysical metadata scoped')
    cp,x=run('lctl-federated.jar','run-crdt','G-COUNTER',3);check(x['merge_laws_verified'] is False and x['merge_commutative'] is None,'aggregate does not prove merge laws')
    p.write_text('{"classical_only": true}',encoding='utf-8');cp,x=run('lctl-hyperfederated.jar','restore4',p);check(x['base']['runtime_state_restored'] is False and x['base']['classical_state_metadata_restored'] is False,'inspection does not claim runtime restore')
print(json.dumps({'status':'PASS','checks':checks,'spoofed_physical_requests_blocked':2,'actual_state_restore_implemented':False,'crdt_merge_laws_implemented':False},indent=2))
