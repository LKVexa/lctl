#!/usr/bin/env python3
"""Verify corpus integrity, linked projections and current compiler behavior."""
from pathlib import Path
import argparse,hashlib,json,os,shutil,subprocess,sys,tempfile
ROOT=Path(__file__).resolve().parents[1]
CANON_KEYS=['id','face','lane','qspace','op','out','ctrl','a','b','param','type','basis','regime','assume','error','resource','conf','proof']
COMPACT_KEYS=['id','lane','op','out','ctrl','in','arg','meta']
def records(rel):
    return [json.loads(x) for x in (ROOT/rel).read_text(encoding='utf-8').splitlines()]
def canonical_rows(text):
    return [[s.strip() for s in line[1:-1].split('¦')] for line in text.splitlines() if line.startswith('(') and len(line[1:-1].split('¦'))==18]
def main():
    parser=argparse.ArgumentParser();parser.add_argument('--static',action='store_true',help='skip compiler execution and report limited scope');args=parser.parse_args()
    fail=[];checks=0
    def check(ok,path,reason):
        nonlocal checks
        checks+=1
        if not ok: fail.append({'path':path,'reason':reason})
    try:
        manifest=json.loads((ROOT/'MANIFEST.json').read_text(encoding='utf-8'));seen=set()
        for ent in manifest['files']:
            rel=ent['path'];p=(ROOT/rel).resolve()
            check(rel not in seen and p.is_relative_to(ROOT) and p.is_file(),rel,'duplicate, escaping or missing manifest path');seen.add(rel)
            if p.is_relative_to(ROOT) and p.is_file():
                raw=p.read_bytes();check(hashlib.sha256(raw).hexdigest()==ent['sha256'],rel,'sha256 mismatch');check(len(raw)==ent['bytes'],rel,'byte count mismatch')
        pairs=records('machine/program_pairs.jsonl');rows=records('machine/row_pairs.jsonl');instructions=records('machine/instruction_corpus.jsonl');negatives=records('machine/negative_cases.jsonl')
        check(len(pairs)==53,'program_pairs','expected 53 programs');check(len(rows)==365,'row_pairs','expected 365 rows');check(len(instructions)==1838,'instruction_corpus','expected 1838 records');check(len(negatives)==512,'negative_cases','expected 512 archived records')
        by_example={};row_lookup={};program_rows={}
        for x in pairs:
            example=x['example'];check(example not in by_example,example,'duplicate program');by_example[example]=x
            compact=(ROOT/x['compact_path']).read_text(encoding='utf-8');canonical=(ROOT/x['canonical_path']).read_text(encoding='utf-8')
            check(compact==x['compact_source'],example,'compact dataset/source mismatch');check(canonical==x['canonical_source'],example,'canonical dataset/source mismatch')
            check(len(compact.encode('utf-8'))==x['compact_bytes'] and len(canonical.encode('utf-8'))==x['canonical_bytes'],example,'dataset size mismatch')
            program_rows[example]=canonical_rows(canonical);check(len(program_rows[example])==x['rows'],example,'row count mismatch')
        for x in rows:
            example=x['example'];ordinal=x['ordinal'];c=x['compact'];k=x['canonical'];text=' │ '.join(c[q] for q in COMPACT_KEYS)
            check(list(k)==CANON_KEYS,example,'canonical field order mismatch');check([k[q] for q in CANON_KEYS]==program_rows[example][ordinal-1],example,'canonical row projection mismatch')
            check(x['alias_expansion']==k['op'] and x['source_columns']==8 and x['canonical_columns']==18,example,'alias/shape mismatch')
            row_lookup[(example,text)]=x
        catalog=json.loads((ROOT/'reference/OPERATOR_CATALOG.json').read_text(encoding='utf-8'));catalog_op={x['operation']:x for x in catalog};alias={x['alias']:x['operation'] for x in catalog if x.get('alias')}
        for i,x in enumerate(instructions,1):
            task=x['task'];example=x['source_example'];expected=None
            if task in ('expand_alias','infer_face','lower_row','explain_inference'):
                r=row_lookup[(example,x['input'])];c=r['canonical']
                expected=c['op'] if task=='expand_alias' else c['face'] if task=='infer_face' else ' ¦ '.join(c[k] for k in CANON_KEYS) if task=='lower_row' else '; '.join(k+'='+c[v] for k,v in [('FACE','face'),('QSPACE','qspace'),('TYPE','type'),('BASIS','basis'),('REGIME','regime'),('ASSUME','assume'),('ERROR','error'),('RESOURCE','resource'),('CONF','conf'),('PROOF','proof')])
            elif task=='operator_face': expected=catalog_op[x['input']]['face']
            elif task=='alias_reference': expected=alias[x['input']]
            elif task=='program_identification': expected=by_example[example]['unit_id']
            elif task=='program_lowering': expected=by_example[example]['canonical_source']
            elif task=='program_size':
                p=by_example[example];expected=f"compact_bytes={p['compact_bytes']}; canonical_bytes={p['canonical_bytes']}"
            elif task not in ('face_family','column_definition'): check(False,f'instruction:{i}','unknown task')
            if expected is not None: check(x['output']==expected,f'instruction:{i}','target/source mismatch')
        unique={x['sample']:x for x in negatives};runtime={'executed':False,'positive_programs':0,'negative_unique_cases':0}
        if not args.static:
            java=os.environ.get('JAVA') or shutil.which('java')
            if not java: raise RuntimeError('Java 21 required; set JAVA to its executable path, or use --static for limited checks')
            cmd=[java,'-jar',str(ROOT/'tooling/lctl-hyperfederated.jar')]
            def run(*a): return subprocess.run(cmd+list(map(str,a)),cwd=ROOT,text=True,encoding='utf-8',errors='replace',capture_output=True,timeout=30)
            def positive(cp):
                try: return cp.returncode==0 and json.loads(cp.stdout).get('status')=='PASS'
                except ValueError: return False
            scratch=ROOT/'build/audit-temp';scratch.mkdir(parents=True,exist_ok=True)
            with tempfile.TemporaryDirectory(prefix='lctl-corpus-audit-',dir=scratch) as tmp:
                tmp=Path(tmp);cmd.insert(1,'-Djava.io.tmpdir='+str(tmp))
                for x in pairs:
                    cp=run('column-verify',ROOT/x['compact_path']);check(positive(cp),x['compact_path'],'compiler verify failed: '+cp.stderr[:200])
                    cp=run('column-roundtrip',ROOT/x['canonical_path']);check(positive(cp),x['canonical_path'],'complete-field roundtrip failed: '+cp.stderr[:200])
                    out=tmp/(x['example']+'.lctl');cp=run('column-compile',ROOT/x['compact_path'],out)
                    check(cp.returncode==0 and out.is_file() and out.read_text(encoding='utf-8')==x['canonical_source'],x['compact_path'],'current lowering differs from recorded canonical authority')
                    runtime['positive_programs']+=1
                base='LCTLC/1.0\n@unit id=audit_negative\n@frame id=F0000\nID│LANE│OP│OUT│CTRL│IN│ARG│META\nD1│alloc│Q│q│_│_│2│_\n'
                for n,(sample,x) in enumerate(unique.items()):
                    if x['class'] in ('bad_magic','missing_frame'): fixture=sample+'\n'
                    elif x['class']=='header_outside_frame': fixture='LCTLC/1.0\n@unit id=bad\n'+sample+'\n'
                    else: fixture=base+sample+'\n@end\n'
                    p=tmp/f'negative_{n}.lctlc';p.write_text(fixture,encoding='utf-8');cp=run('column-verify',p)
                    check(cp.returncode!=0 and 'FAIL:' in cp.stderr,x['id'],'expected compiler/semantic rejection');runtime['negative_unique_cases']+=1
            runtime['executed']=True
        result={'status':'FAIL' if fail else 'PASS_WITH_SCOPE' if args.static else 'PASS','schema':'COLUMNED-LCTL-CORPUS-VALIDATION/2.0','checks':checks,'counts':{'programs':len(pairs),'rows':len(rows),'instruction_records':len(instructions),'negative_records':len(negatives),'negative_unique_samples':len(unique)},'runtime':runtime,'scope':'All linked dataset targets, integrity, canonical lowering and semantic roundtrip. Family descriptions and column definitions remain prose; physical execution is not certified.','failures':fail}
    except Exception as e: result={'status':'FAIL','reason':str(e),'failures':fail}
    print(json.dumps(result,indent=2));return 0 if result['status'] in ('PASS','PASS_WITH_SCOPE') else 1
if __name__=='__main__': sys.exit(main())
