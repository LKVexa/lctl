#!/usr/bin/env python3
import json, subprocess, sys, time
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
LEGACY=ROOT/"runtime/bin/lctl-runtime.jar"
FABRIC=ROOT/"runtime/bin/lctl-fabric.jar"
TOPO=ROOT/"topology/reference_logical_topology.json"
JAVA=["java","-jar"]

def run(cmd, expect=0, timeout=30):
    t=time.time()
    p=subprocess.run(cmd, cwd=ROOT, text=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=timeout)
    ok=(p.returncode==expect)
    return {
        "ok":ok,
        "returncode":p.returncode,
        "expect":expect,
        "seconds":round(time.time()-t,6),
        "stdout":p.stdout.strip()[:1200],
        "stderr":p.stderr.strip()[:1200],
        "command":" ".join(str(x) for x in cmd)
    }

cases=[]

def add(name, cmd, expect=0, category="positive", timeout=30):
    r=run(cmd,expect,timeout)
    r.update({"name":name,"category":category})
    cases.append(r)
    if not r["ok"]:
        print("FAIL",name, r["returncode"], r["stderr"], file=sys.stderr)

# Baseline positive conformance: 23 core/parallel + 30 distributed = 53
valid=[]
for d in ["conformance/valid","conformance/valid_distributed"]:
    valid += sorted((ROOT/d).glob("*.lctl"))
for f in valid:
    add("baseline_valid:"+f.name, JAVA+[str(LEGACY),"verify",str(f)])

# Fabric positive tests
examples = [
"01_bell_state","02_ghz_state","05_single_qubit_rotations","06_controlled_operations",
"13_stabilizer","21_parallel_bell_pairs","24_parallel_bell_pair_generation",
"25_parallel_ghz_preparation","34_work_span_analysis","53_native_distributed_bell_fabric"
]
for x in examples:
    f=ROOT/"examples"/x/"program.lctl"
    add("ses:"+x, JAVA+[str(FABRIC),"ses",str(f)])
for x in examples:
    f=ROOT/"examples"/x/"program.lctl"
    add("partition:"+x, JAVA+[str(FABRIC),"partition",str(f),"2"])
for x in examples[:8]:
    f=ROOT/"examples"/x/"program.lctl"
    add("commute:"+x, JAVA+[str(FABRIC),"commute-plan",str(f)])
for x in ["24_parallel_bell_pair_generation","25_parallel_ghz_preparation","34_work_span_analysis","36_communication_aware_scheduling","53_native_distributed_bell_fabric"]:
    f=ROOT/"examples"/x/"program.lctl"
    add("schedule:"+x, JAVA+[str(FABRIC),"schedule",str(f),str(TOPO),"2"])
for x in ["24_parallel_bell_pair_generation","25_parallel_ghz_preparation","53_native_distributed_bell_fabric"]:
    f=ROOT/"examples"/x/"program.lctl"
    add("qcirp2:"+x, JAVA+[str(FABRIC),"qcir-p2",str(f),str(TOPO),"2"])
for x in ["01_bell_state","02_ghz_state","24_parallel_bell_pair_generation","53_native_distributed_bell_fabric"]:
    f=ROOT/"examples"/x/"program.lctl"
    add("dsv:"+x, JAVA+[str(FABRIC),"simulate-distributed",str(f),"2"], timeout=60)
add("density:parallel_bell", JAVA+[str(FABRIC),"simulate-density-distributed",str(ROOT/"examples/24_parallel_bell_pair_generation/program.lctl"),"2"], timeout=60)
add("stabilizer:ghz", JAVA+[str(FABRIC),"simulate-stabilizer",str(ROOT/"examples/13_stabilizer/program.lctl")])
for x in ["41_epr_generation","42_teleportation_across_logical_nodes","43_remote_cnot_decomposition","44_entanglement_swapping","45_classical_feedback_across_nodes"]:
    add("protocol:"+x, JAVA+[str(FABRIC),"protocol-execute",str(ROOT/"examples"/x/"program.lctl"),"1"])
for sc in ["worker_crash","classical_link_timeout","ebit_expiration","coherence_deadline_exceeded"]:
    add("failure:"+sc, JAVA+[str(FABRIC),"failure-inject",str(ROOT/"examples/24_parallel_bell_pair_generation/program.lctl"),sc,"7"])
for x in ["19_hamiltonian_simulation","24_parallel_bell_pair_generation"]:
    add("error:"+x, JAVA+[str(FABRIC),"error-algebra",str(ROOT/"examples"/x/"program.lctl")])
add("benchmark:parallel_bell", JAVA+[str(FABRIC),"benchmark",str(ROOT/"examples/24_parallel_bell_pair_generation/program.lctl"),"2"], timeout=60)
add("replay:reference", JAVA+[str(FABRIC),"replay",str(ROOT/"provenance/lctl13/REFERENCE_EVENT_TRACE.jsonl")])
add("adapter:mock", JAVA+[str(FABRIC),"adapter-check",str(ROOT/"adapters/mocks/two_qpu_mock.json")])

# Baseline negative conformance: 22 core/parallel + 30 distributed = 52
invalid=[]
for d in ["conformance/invalid","conformance/invalid_distributed"]:
    invalid += sorted((ROOT/d).glob("*.lctl"))
for f in invalid:
    add("baseline_reject:"+f.name, JAVA+[str(LEGACY),"verify",str(f)], expect=1, category="negative")

# 28 1.3 fabric-specific mandatory rejection tests
# 8 unsupported distributed statevector kernels
for x in ["03_teleportation","09_density_matrix_noise","10_amplitude_damping","11_depolarizing_channel",
          "12_repetition_error_correction","14_qft","17_vqe","18_qaoa"]:
    add("fabric_reject_dsv:"+x, JAVA+[str(FABRIC),"simulate-distributed",str(ROOT/"examples"/x/"program.lctl"),"2"], expect=1, category="negative", timeout=60)

# 6 non-Clifford/unsupported stabilizer kernels
for x in ["05_single_qubit_rotations","09_density_matrix_noise","10_amplitude_damping","14_qft","17_vqe","18_qaoa"]:
    add("fabric_reject_stabilizer:"+x, JAVA+[str(FABRIC),"simulate-stabilizer",str(ROOT/"examples"/x/"program.lctl")], expect=1, category="negative")

# 6 protocol semantic negatives
for f in ["08_remote_gate_no_route.lctl","09_remote_gate_unsupported_target.lctl","10_teleport_without_entanglement.lctl",
          "11_teleport_without_classical_path.lctl","12_insufficient_ebit_count.lctl","13_expired_entanglement_resource.lctl"]:
    add("fabric_reject_protocol:"+f, JAVA+[str(FABRIC),"protocol-execute",str(ROOT/"conformance/invalid_distributed"/f),"1"], expect=1, category="negative")

# 3 unknown failures
for sc in ["unknown_failure_A","unknown_failure_B","unknown_failure_C"]:
    add("fabric_reject_failure:"+sc, JAVA+[str(FABRIC),"failure-inject",str(ROOT/"examples/24_parallel_bell_pair_generation/program.lctl"),sc,"1"], expect=1, category="negative")

# 2 invalid topologies
for f in ["topology_no_nodes.json","topology_malformed.json"]:
    add("fabric_reject_topology:"+f, JAVA+[str(FABRIC),"place",str(ROOT/"examples/24_parallel_bell_pair_generation/program.lctl"),str(ROOT/"conformance/1_3/invalid"/f),"2"], expect=1, category="negative")

# density bound, missing replay source, and physical distributed blocker
add("fabric_reject_density_bound", JAVA+[str(FABRIC),"simulate-density-distributed",str(ROOT/"conformance/1_3/invalid/density_over_reference_limit.lctl"),"2"], expect=1, category="negative")
add("fabric_reject_missing_replay", JAVA+[str(FABRIC),"replay",str(ROOT/"conformance/1_3/invalid/does_not_exist.trace")], expect=1, category="negative")
add("fabric_reject_physical_distributed", JAVA+[str(FABRIC),"physical-distributed-run",str(ROOT/"examples/24_parallel_bell_pair_generation/program.lctl")], expect=1, category="negative")

summary={}
for cat in ["positive","negative"]:
    q=[x for x in cases if x["category"]==cat]
    summary[cat]={"total":len(q),"pass":sum(x["ok"] for x in q),"fail":sum(not x["ok"] for x in q)}
summary["overall"]={"total":len(cases),"pass":sum(x["ok"] for x in cases),"fail":sum(not x["ok"] for x in cases)}
result={"schema":"LCTL-CONFORMANCE-1.3/1.0","summary":summary,"cases":cases}
proof=ROOT/"proof"
proof.mkdir(exist_ok=True)
(proof/"LCTL_1_3_CONFORMANCE.json").write_text(json.dumps(result,indent=2)+"\n")
lines=[
"LCTL 1.3 RC1 CONFORMANCE",
f"positive {summary['positive']['pass']}/{summary['positive']['total']} pass",
f"negative {summary['negative']['pass']}/{summary['negative']['total']} correctly rejected",
f"overall {summary['overall']['pass']}/{summary['overall']['total']} pass",
]
(proof/"LCTL_1_3_CONFORMANCE.txt").write_text("\n".join(lines)+"\n")
print(json.dumps(summary,indent=2))
sys.exit(0 if summary["overall"]["fail"]==0 and summary["positive"]["total"]>=80 and summary["negative"]["total"]>=80 else 1)
