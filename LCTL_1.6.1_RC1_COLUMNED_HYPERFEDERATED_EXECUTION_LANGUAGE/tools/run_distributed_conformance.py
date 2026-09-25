#!/usr/bin/env python3
from pathlib import Path
import subprocess, json, hashlib, sys

ROOT=Path(__file__).resolve().parents[1]
JAR=ROOT/"runtime/bin/lctl-runtime.jar"

def run(*args):
    cp=subprocess.run(["java","-jar",str(JAR),*map(str,args)],capture_output=True,text=True)
    return {"rc":cp.returncode,"stdout":cp.stdout.strip(),"stderr":cp.stderr.strip()}

result={"schema":"LCTL-DISTRIBUTED-CONFORMANCE/1.0","tests":[]}

def test(name, ok, detail=None):
    result["tests"].append({"name":name,"pass":bool(ok),"detail":detail or ""})
    return ok

valid=list(sorted((ROOT/"conformance/valid").glob("*.lctl")))
invalid=list(sorted((ROOT/"conformance/invalid").glob("*.lctl")))
dvalid=list(sorted((ROOT/"conformance/valid_distributed").glob("*.lctl")))
dinvalid=list(sorted((ROOT/"conformance/invalid_distributed").glob("*.lctl")))

test("core_valid_regression", all(run("verify",f)["rc"]==0 for f in valid), f"{len(valid)} programs")
test("core_invalid_regression", all(run("verify",f)["rc"]!=0 for f in invalid), f"{len(invalid)} programs")
test("distributed_valid", all(run("verify",f)["rc"]==0 for f in dvalid), f"{len(dvalid)} programs")
test("distributed_invalid", all(run("verify",f)["rc"]!=0 for f in dinvalid), f"{len(dinvalid)} programs")

flag=ROOT/"examples/42_teleportation_across_logical_nodes/program.lctl"
a=run("qcir-p",flag); b=run("qcir-p",flag)
test("qcir_p_determinism", a["rc"]==0 and a["stdout"]==b["stdout"], hashlib.sha256(a["stdout"].encode()).hexdigest() if a["rc"]==0 else a["stderr"])

a=run("distributed-plan",flag); b=run("distributed-plan",flag)
test("distributed_plan_determinism", a["rc"]==0 and a["stdout"]==b["stdout"])

a=run("causal-dag",ROOT/"examples/23_independent_quantum_lanes/program.lctl")
test("causal_dag_generation", a["rc"]==0 and '"LCTL-CAUSAL-DAG/1.0"' in a["stdout"])

a=run("topology-check",flag)
test("topology_hash", a["rc"]==0 and '"topology_hash"' in a["stdout"])

a=run("protocol-check",flag)
test("teleport_protocol_validation", a["rc"]==0 and '"teleports":1' in a["stdout"])

a=run("emulate-distributed",flag,"7")
test("distributed_emulator_label", a["rc"]==0 and '"DISTRIBUTED_CLASSICAL_EMULATION"' in a["stdout"] and '"physical_distributed":false' in a["stdout"])

a=run("physical-distributed-run",flag)
test("physical_distributed_fail_closed", a["rc"]!=0 and "PHYSICAL_DISTRIBUTED_EXECUTION_BLOCKED" in a["stderr"])

result["summary"]={
    "total":len(result["tests"]),
    "pass":sum(1 for t in result["tests"] if t["pass"]),
    "fail":sum(1 for t in result["tests"] if not t["pass"])
}
print(json.dumps(result,indent=2))
sys.exit(0 if result["summary"]["fail"]==0 else 1)
