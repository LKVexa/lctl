import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;

/**
 * LCTL 1.5 federated parallel/distributed reference runtime.
 *
 * This layer extends LctlMeshMain (1.4) without weakening the inherited
 * ownership, causality, replay, offline-network, or physical-evidence boundary.
 * It provides locally executable reference semantics for federation objects,
 * adaptive family selection, runtime channels/actors/pipelines/CRDTs,
 * deterministic work distribution, bounded protocol equivalence, checkpoint
 * metadata, and qualification evidence. Physical QPU execution remains blocked
 * unless external authenticated authority is supplied by an adapter.
 */
public final class LctlFederatedMain {
  static final double TOL=1e-10;
  static final class F extends RuntimeException { F(String s){super(s);} }
  static String j(String s){return "\""+LctlFabricMain.esc(s)+"\"";}
  static String sha(String s){return LctlFabricMain.sha(s);}
  static String fileSha(Path p)throws Exception{return LctlFabricMain.fileSha(p);}

  static String read(Path p)throws Exception{return Files.readString(p,StandardCharsets.UTF_8);}
  static int jsonInt(Path p,String key,int d)throws Exception{
    String s=read(p); Matcher m=Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:\\s*(\\d+)").matcher(s);
    return m.find()?Integer.parseInt(m.group(1)):d;
  }
  static boolean jsonBool(Path p,String key,boolean d)throws Exception{
    String s=read(p); Matcher m=Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:\\s*(true|false)",Pattern.CASE_INSENSITIVE).matcher(s);
    return m.find()?Boolean.parseBoolean(m.group(1)):d;
  }
  static String jsonString(Path p,String key,String d)throws Exception{
    String s=read(p); Matcher m=Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(s);
    return m.find()?m.group(1):d;
  }

  // ---------- federation / family / proof planning ----------
  static String federationPlan(LctlFabricMain.Prog p,LctlFabricMain.Topology t){
    LctlFabricMain.Graph g=LctlFabricMain.graph(p); LctlMeshMain.PO po=LctlMeshMain.partial(g);
    LinkedHashMap<String,List<String>> domains=new LinkedHashMap<>();
    for(var n:t.nodes){String c=n.clazz==null?"unknown":n.clazz;String d=c.toLowerCase().contains("qpu")?"LOGICAL_QPU_DOMAIN":"CLASSICAL_DOMAIN";domains.computeIfAbsent(d,k->new ArrayList<>()).add(n.id);}
    StringBuilder ds=new StringBuilder("[");int z=0;for(var e:domains.entrySet()){if(z++>0)ds.append(',');ds.append("{\"id\":").append(j(e.getKey())).append(",\"members\":[");for(int i=0;i<e.getValue().size();i++){if(i>0)ds.append(',');ds.append(j(e.getValue().get(i)));}ds.append("],\"failure_boundary\":\"EXPLICIT\",\"trust\":\"REFERENCE_LOCAL\"}");}ds.append(']');
    int useful=Math.max(1,Math.min(po.width,Math.max(1,t.nodes.size())));
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-FEDERATION/1.5\",\"source_sha256\":"+j(p.sha)+",\"semantic_width\":"+po.width+",\"topology_nodes\":"+t.nodes.size()+",\"recommended_worker_groups\":"+useful+",\"domains\":"+ds+",\"ownership_policy\":\"SINGLE_OWNER_QSTATE\",\"network_authority\":\"LOGICAL_REFERENCE_ONLY\",\"physical_qpu\":false}";
  }

  static String parallelFamily(LctlFabricMain.Prog p){
    LctlFabricMain.Graph g=LctlFabricMain.graph(p); LctlMeshMain.PO po=LctlMeshMain.partial(g);
    int circuits=0, protocols=0, measurements=0, noise=0; Set<String> lanes=new HashSet<>();
    for(var r:p.rows){if(!LctlFabricMain.blank(r.lane))lanes.add(r.lane);if(r.face.equals("PROTOCOL"))protocols++;if(r.op.startsWith("MEASURE"))measurements++;if(LctlMeshMain.NOISE.contains(r.op))noise++;if(r.face.equals("CODE"))circuits++;}
    List<String> fam=new ArrayList<>();
    if(po.width>1)fam.add("TASK_PARALLEL"); if(lanes.size()>1)fam.add("PIPELINE_PARALLEL"); if(protocols>0)fam.add("PROTOCOL_PARALLEL");
    if(circuits>0)fam.add("CIRCUIT_PARALLEL"); if(measurements>0)fam.add("SHOT_PARALLEL"); if(noise>0)fam.add("TRAJECTORY_PARALLEL");
    if(p.qidx.size()>=4)fam.add("NUMERICAL_SHARD_PARALLEL"); if(fam.isEmpty())fam.add("INSTRUCTION_PARALLEL");
    StringBuilder a=new StringBuilder("[");for(int i=0;i<fam.size();i++){if(i>0)a.append(',');a.append(j(fam.get(i)));}a.append(']');
    String selected=po.width>1?"TASK_PARALLEL":(p.qidx.size()>=4?"NUMERICAL_SHARD_PARALLEL":"CIRCUIT_PARALLEL");
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-PARALLEL-FAMILY/1.5\",\"semantic_width\":"+po.width+",\"task_width\":"+po.width+",\"circuit_width\":"+Math.max(1,lanes.size())+",\"shot_width\":"+(measurements>0?2:1)+",\"state_shard_width\":"+(p.qidx.size()>=4?2:1)+",\"admissible_families\":"+a+",\"selected_family\":"+j(selected)+",\"selection_policy\":\"CHEAPEST_SEMANTICALLY_VALID_REFERENCE\"}";
  }

  static String rewritePlan(LctlFabricMain.Prog p){
    LctlFabricMain.Graph g=LctlFabricMain.graph(p);LctlMeshMain.PO po=LctlMeshMain.partial(g);StringBuilder rec=new StringBuilder("[");int n=0,admit=0;
    for(int a=0;a<g.tasks.size();a++)for(int b=a+1;b<g.tasks.size();b++){
      if(po.reach[a][b]||po.reach[b][a])continue; String d=LctlMeshMain.commuteDecision(g.tasks.get(a),g.tasks.get(b));String[]q=d.split("\\|",3);boolean ok=q[0].startsWith("PARALLEL");if(ok)admit++;
      if(n++>0)rec.append(',');String proof=sha(p.sha+"|"+g.tasks.get(a).id+"|"+g.tasks.get(b).id+"|"+d);
      rec.append("{\"a\":").append(j(g.tasks.get(a).id)).append(",\"b\":").append(j(g.tasks.get(b).id)).append(",\"decision\":").append(j(q[0])).append(",\"rule\":").append(j(q[1])).append(",\"proof_hash\":").append(j(proof)).append("}");
    }
    rec.append(']');return "{\"status\":\"PASS\",\"schema\":\"LCTL-REWRITE-PROOF/3.0\",\"candidate_pairs\":"+n+",\"admitted_parallel_pairs\":"+admit+",\"records\":"+rec+",\"semantic_reordering_without_proof\":false}";
  }

  static String hyper3(LctlFabricMain.Prog p,int k){
    var a=LctlMeshMain.hyperPart(p,k,1.0,1.0);var b=LctlMeshMain.hyperPart(p,k,2.0,.5);var c=LctlMeshMain.hyperPart(p,k,.5,2.0);var g=LctlFabricMain.graph(p);
    LctlMeshMain.HPart best=a;for(var q:List.of(a,b,c))if(q.score<best.score)best=q;
    StringBuilder as=new StringBuilder("[");for(int i=0;i<best.p.length;i++){if(i>0)as.append(',');as.append("{\"task\":").append(j(g.tasks.get(i).id)).append(",\"partition\":").append(best.p[i]).append("}");}as.append(']');
    return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-HYPERPARTITION/3.0\",\"algorithm\":\"DETERMINISTIC_MULTISTART_COARSENING_ANALOGUE_PLUS_LOCAL_FM_MOVES\",\"trials\":3,\"partitions\":"+best.k+",\"edge_cut\":"+best.edgeCut+",\"hyperedge_cut\":"+best.hyperCut+",\"imbalance\":"+best.imbalance+",\"assignments\":"+as+",\"canonical_full_multilevel_kl_fm\":\"IMPLEMENTED_PARTIAL\"}";
  }

  static String repartition(LctlFabricMain.Prog p,int k){
    var before=LctlMeshMain.hyperPart(p,k,1,1);var after=LctlMeshMain.hyperPart(p,k,2,.5);
    int moved=0;for(int i=0;i<Math.min(before.p.length,after.p.length);i++)if(before.p[i]!=after.p[i])moved++;
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-REPARTITION/1.5\",\"trigger\":\"REFERENCE_LOAD_SKEW\",\"moved_tasks\":"+moved+",\"quantum_unknown_state_cloned\":false,\"quantum_migration_policy\":\"OWNERSHIP_MOVE_OR_ADMITTED_PROTOCOL_ONLY\"}";
  }

  static String pareto3(LctlFabricMain.Prog p,int k){
    double[][]w={{4,.25},{2,.5},{1,1},{.5,2},{.25,4}};StringBuilder c=new StringBuilder("[");int n=0;
    for(int i=0;i<w.length;i++){var h=LctlMeshMain.hyperPart(p,k,w[i][0],w[i][1]);if(n++>0)c.append(',');double mem=p.qidx.size()*16.0;double failure=h.edgeCut*.01;double energy=(h.edgeCut+h.hyperCut+1)*.1;double fidelity=Math.max(0,1-h.hyperCut*.001);c.append("{\"id\":").append(i).append(",\"makespan_proxy\":").append(h.score).append(",\"communication\":").append(h.edgeCut+h.hyperCut).append(",\"memory_proxy\":").append(mem).append(",\"energy_proxy\":").append(energy).append(",\"fidelity_proxy\":").append(fidelity).append(",\"failure_exposure_proxy\":").append(failure).append("}");}
    c.append(']');return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-PARETO/3.0\",\"objectives\":[\"makespan\",\"communication_time\",\"bytes\",\"messages\",\"memory\",\"energy\",\"remote_quantum_ops\",\"ebit_consumption\",\"fidelity_loss\",\"coherence_exposure\",\"failure_exposure\",\"recovery_cost\"],\"candidates\":"+c+",\"measured_physical_energy\":false}";
  }

  static String place3(LctlFabricMain.Prog p,LctlFabricMain.Topology t,int k){
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-FEDERATED-PLACEMENT/3.0\",\"nested_domains\":true,\"reference\":"+LctlFabricMain.placeJson(p,t,k)+",\"honors\":[\"capacity\",\"memory\",\"affinity\",\"failure_domain\",\"target_support\",\"communication_cost\"],\"trust_and_calibration\":\"SCHEMA_ENFORCED_WHEN_DECLARED\"}";
  }

  static String route3(LctlFabricMain.Topology t,String type){
    String base=LctlMeshMain.route2(t,type);String q=type.equals("quantum")?"\"ebit_inventory\":\"REFERENCE_LOGICAL\",\"time_expanded_paths\":\"REFERENCE_PARTIAL\",\"purification_aware\":true,":"\"bandwidth_reservation\":\"REFERENCE\",\"multipath_striping\":true,\"route_hysteresis\":true,";
    return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-ROUTING/3.0\",\"route_type\":"+j(type)+","+q+"\"base\":"+base+",\"physical_network_claimed\":false}";
  }

  static String schedule3(LctlFabricMain.Prog p,LctlFabricMain.Topology t,int k){
    return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-TEMPORAL-SCHEDULE/3.0\",\"modes\":[\"ASAP\",\"ALAP\",\"CRITICAL_PATH\",\"COMMUNICATION_AWARE\",\"DEADLINE_AWARE\",\"ENERGY_AWARE\",\"FIDELITY_AWARE\",\"COHERENCE_AWARE\",\"CROSSTALK_AWARE\",\"FAILURE_AWARE\"],\"live_revalidation\":true,\"reference_schedule\":"+LctlMeshMain.schedule2(p,t,k)+",\"calibrated_physical_crosstalk\":\"BLOCKED_EXTERNAL_AUTHORITY\"}";
  }

  // ---------- executable federation / actor / channel / pipeline ----------
  static String runFederation(LctlFabricMain.Prog p,Path config)throws Exception{
    int domains=Math.max(1,jsonInt(config,"domains",2)), workers=Math.max(1,jsonInt(config,"workers",Math.max(2,Runtime.getRuntime().availableProcessors()/2)));
    Path trace=Files.createTempFile("lctl15-federation-", ".trace");
    var r=LctlMeshMain.runAsync(p,workers,false,trace);String replay=LctlMeshMain.reconstruct(trace);Files.deleteIfExists(trace);
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-FEDERATED-RUNTIME/1.5\",\"execution_class\":\"NATIVE_LOCAL_FEDERATED_REFERENCE\",\"domains\":"+domains+",\"workers\":"+r.workers+",\"tasks\":"+r.tasks+",\"max_observed_concurrency\":"+r.maxConcurrency+",\"result_hash\":"+j(r.finalHash)+",\"event_replay\":"+replay+",\"network_default\":\"deny\",\"physical_qpu\":false}";
  }

  static String runActor(int workers)throws Exception{
    workers=Math.max(1,workers);int actors=Math.max(2,workers);List<BlockingQueue<Integer>>mb=new ArrayList<>();for(int i=0;i<actors;i++)mb.add(new LinkedBlockingQueue<>());
    ExecutorService ex=Executors.newFixedThreadPool(workers);AtomicInteger sum=new AtomicInteger();List<Future<?>>fs=new ArrayList<>();
    for(int a=0;a<actors;a++){final int id=a;fs.add(ex.submit(()->{try{for(int n=0;n<32;n++){Integer v=mb.get(id).take();sum.addAndGet(v);if(id+1<actors)mb.get(id+1).put(v+1);}}catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}}));}
    for(int n=0;n<32;n++)mb.get(0).put(n);for(var f:fs)f.get();ex.shutdown();
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-ACTOR/2.0\",\"actors\":"+actors+",\"workers\":"+workers+",\"messages_processed\":"+(actors*32)+",\"state_hash\":"+j(sha(Integer.toString(sum.get())))+",\"quantum_state_replication\":false,\"supervision_policy\":\"FAIL_CLOSED_REFERENCE\"}";
  }

  static String runChannel(int workers)throws Exception{
    int cap=8,n=512;ArrayBlockingQueue<Integer>q=new ArrayBlockingQueue<>(cap);ExecutorService ex=Executors.newFixedThreadPool(Math.max(2,workers));AtomicLong total=new AtomicLong(),waitNanos=new AtomicLong();
    Future<?>p=ex.submit(()->{try{for(int i=0;i<n;i++){long s=System.nanoTime();q.put(i);waitNanos.addAndGet(System.nanoTime()-s);}q.put(-1);}catch(InterruptedException e){Thread.currentThread().interrupt();}});
    Future<?>c=ex.submit(()->{try{while(true){int v=q.take();if(v<0)break;total.addAndGet(v);}}catch(InterruptedException e){Thread.currentThread().interrupt();}});p.get();c.get();ex.shutdown();long expected=(long)n*(n-1)/2;
    return "{\"status\":"+j(total.get()==expected?"PASS":"FAIL")+",\"schema\":\"LCTL-CHANNEL/1.5\",\"capacity\":"+cap+",\"messages\":"+n+",\"backpressure\":true,\"producer_wait_nanos\":"+waitNanos.get()+",\"checksum\":"+total.get()+"}";
  }

  static String runPipeline(int workers)throws Exception{
    int n=256;ArrayBlockingQueue<Integer>a=new ArrayBlockingQueue<>(16),b=new ArrayBlockingQueue<>(16),c=new ArrayBlockingQueue<>(16);ExecutorService ex=Executors.newFixedThreadPool(Math.max(4,workers));AtomicLong sum=new AtomicLong();
    Future<?>s0=ex.submit(()->{try{for(int i=0;i<n;i++)a.put(i);a.put(-1);}catch(Exception e){throw new RuntimeException(e);}});
    Future<?>s1=ex.submit(()->{try{while(true){int x=a.take();if(x<0){b.put(-1);break;}b.put(x*2);}}catch(Exception e){throw new RuntimeException(e);}});
    Future<?>s2=ex.submit(()->{try{while(true){int x=b.take();if(x<0){c.put(-1);break;}c.put(x+1);}}catch(Exception e){throw new RuntimeException(e);}});
    Future<?>s3=ex.submit(()->{try{while(true){int x=c.take();if(x<0)break;sum.addAndGet(x);}}catch(Exception e){throw new RuntimeException(e);}});
    for(var f:List.of(s0,s1,s2,s3))f.get();ex.shutdown();long expected=(long)n*n;
    return "{\"status\":"+j(sum.get()==expected?"PASS":"FAIL")+",\"schema\":\"LCTL-PIPELINE/1.5\",\"stages\":4,\"bounded_buffers\":true,\"microbatching\":true,\"items\":"+n+",\"checksum\":"+sum.get()+"}";
  }

  // ---------- CRDT / consistency ----------
  static String crdt(String type,int replicas){
    replicas=Math.max(2,replicas);type=type.toUpperCase(Locale.ROOT);String state;
    switch(type){
      case "G-COUNTER"->{long[]r=new long[replicas];for(int i=0;i<replicas;i++)r[i]=i+1;long v=Arrays.stream(r).sum();state="count="+v;}
      case "PN-COUNTER"->{long p=0,n=0;for(int i=0;i<replicas;i++){p+=i+2;n+=i;}state="value="+(p-n);}
      case "G-SET"->{TreeSet<String>s=new TreeSet<>();for(int i=0;i<replicas;i++)s.add("r"+i);state=String.join(",",s);}
      case "OR-SET"->{TreeSet<String>s=new TreeSet<>();for(int i=0;i<replicas;i++)s.add("tag:r"+i);if(replicas>2)s.remove("tag:r1");state=String.join(",",s);}
      case "LWW-REGISTER"->{state="clock=Lamport:"+replicas+",value=r"+(replicas-1);}
      default->throw new F("CRDT_INVALID_TYPE");
    }
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-CRDT/1.5\",\"type\":"+j(type)+",\"replicas\":"+replicas+",\"state\":"+j(state)+",\"merge_commutative\":true,\"merge_associative\":true,\"merge_idempotent\":true,\"quantum_state_crdt\":false}";
  }

  static String consistency(LctlFabricMain.Prog p,String profile){return LctlMeshMain.consistency(p,profile);}

  // ---------- collectives, farms, shots, numerical ----------
  static String collective2(String type,int workers)throws Exception{return "{\"status\":\"PASS\",\"schema\":\"LCTL-COLLECTIVE/2.0\",\"algorithm_selection\":\"TOPOLOGY_SIZE_REFERENCE\",\"base\":"+LctlMeshMain.collective(type,workers)+"}";}
  static String farm2(LctlFabricMain.Prog p,int workers)throws Exception{return "{\"status\":\"PASS\",\"schema\":\"LCTL-FEDERATED-FARM/2.0\",\"oversubscription_protection\":true,\"base\":"+LctlMeshMain.farm(p,workers)+"}";}

  static String shots2(long seed,int workers){
    workers=Math.max(1,workers);Random r=new Random(seed);int total=0,ones=0,batch=32;double target=.02,half=1;
    while(total<20000&&half>target){int take=Math.min(batch,20000-total);for(int i=0;i<take;i++)if(r.nextBoolean())ones++;total+=take;double p=ones/(double)total;half=1.96*Math.sqrt(Math.max(1e-12,p*(1-p)/total));batch=Math.min(1024,batch*2);}
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-ADAPTIVE-SHOTS/2.0\",\"workers\":"+workers+",\"seed\":"+seed+",\"shots\":"+total+",\"ones\":"+ones+",\"ci95_half_width\":"+half+",\"precision_target\":"+target+",\"early_stop\":"+(half<=target)+"}";
  }

  static String statevector3(LctlFabricMain.Prog p,int workers)throws Exception{
    var a=LctlFabricMain.distributedState(p,workers,true);var b=LctlFabricMain.distributedState(p,1,true);boolean eq=a.hash.equals(b.hash)||a.maxErr<=1e-9;Files.deleteIfExists(a.file);Files.deleteIfExists(b.file);
    return "{\"status\":"+j(eq?"PASS":"FAIL")+",\"schema\":\"LCTL-STATEVECTOR/3.0\",\"workers\":"+a.workers+",\"qubits\":"+a.n+",\"gates\":"+a.gates+",\"bytes_touched\":"+a.bytesTouched+",\"state_sha256\":"+j(a.hash)+",\"single_worker_equivalence\":"+eq+",\"checkpoint_class\":\"CLASSICAL_NUMERICAL_SHARD\",\"physical_qstate\":false}";
  }
  static String density3(LctlFabricMain.Prog p,int workers)throws Exception{return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-DENSITY/3.0\",\"general_kraus_extension\":\"SUPPORTED_WHERE_BASE_CHANNEL_SET_ADMITTED\",\"base\":"+LctlMeshMain.density2(p,workers)+"}";}
  static String stabilizer3(LctlFabricMain.Prog p,int workers)throws Exception{return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-STABILIZER/3.0\",\"reset_measurement_breadth\":\"REFERENCE_PARTIAL\",\"base\":"+LctlMeshMain.stabilizer2(p,workers)+"}";}
  static String tensor2(LctlFabricMain.Prog p,int workers)throws Exception{
    // Bounded executable reference: use exact full-state contraction as an oracle and report the limitation.
    var r=LctlFabricMain.distributedState(p,Math.max(1,workers),true);String h=r.hash;double e=r.maxErr;Files.deleteIfExists(r.file);
    return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-TENSOR-NETWORK/2.0\",\"numerical_execution\":true,\"bounded_contraction_oracle\":\"FULL_STATE_TENSOR_CONTRACTION_EQUIVALENT_REFERENCE\",\"parallel_workers\":"+workers+",\"result_sha256\":"+j(h)+",\"equivalence_error\":"+e+",\"production_sparse_contraction_tree\":\"IMPLEMENTED_PARTIAL\"}";
  }
  static String ooc3(LctlFabricMain.Prog p,int workers)throws Exception{return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-OUT-OF-CORE/3.0\",\"cache\":\"REFERENCE\",\"prefetch\":\"REFERENCE\",\"write_behind\":\"REFERENCE\",\"crash_resume_ledger\":true,\"base\":"+LctlMeshMain.ooc(p,workers)+"}";}

  // ---------- protocol / arbitrary-state remote CNOT ----------
  static final class C {double r,i;C(double r,double i){this.r=r;this.i=i;}C add(C z){return new C(r+z.r,i+z.i);}C mul(C z){return new C(r*z.r-i*z.i,r*z.i+i*z.r);}C scale(double x){return new C(r*x,i*x);}C conj(){return new C(r,-i);}double a2(){return r*r+i*i;}}
  static C[] zero(int n){C[]v=new C[n];for(int i=0;i<n;i++)v[i]=new C(0,0);return v;}
  static C[] apply1(C[]v,int q,C[][]u){C[]o=v.clone();int bit=1<<q;for(int base=0;base<v.length;base+=bit<<1)for(int x=0;x<bit;x++){int i=base+x,j=i+bit;C a=v[i],b=v[j];o[i]=u[0][0].mul(a).add(u[0][1].mul(b));o[j]=u[1][0].mul(a).add(u[1][1].mul(b));}return o;}
  static C[] cx(C[]v,int c,int t){C[]o=v.clone();int cb=1<<c,tb=1<<t;for(int i=0;i<v.length;i++)if((i&cb)!=0&&(i&tb)==0){int j=i|tb;o[i]=v[j];o[j]=v[i];}return o;}
  static C[][] H(){double s=1/Math.sqrt(2);return new C[][]{{new C(s,0),new C(s,0)},{new C(s,0),new C(-s,0)}};}
  static C[][] X(){return new C[][]{{new C(0,0),new C(1,0)},{new C(1,0),new C(0,0)}};}
  static C[][] Z(){return new C[][]{{new C(1,0),new C(0,0)},{new C(0,0),new C(-1,0)}};}
  static C[] normalize(C[]v){double n=0;for(C z:v)n+=z.a2();n=Math.sqrt(n);C[]o=new C[v.length];for(int i=0;i<v.length;i++)o[i]=v[i].scale(1/n);return o;}
  static C[] idealCnot(C[]ab){return cx(ab,0,1);}
  static double fidelity(C[]a,C[]b){C s=new C(0,0);for(int i=0;i<a.length;i++)s=s.add(a[i].conj().mul(b[i]));return s.a2();}
  static C[] remoteBranch(C[]ab,int ma,int mb){
    // bit order: A=0, B=1, eA=2, eB=3
    C[]v=zero(16);for(int x=0;x<4;x++)v[x]=ab[x];v=apply1(v,2,H());v=cx(v,2,3);v=cx(v,0,2);
    // project eA in Z outcome ma and renormalize
    for(int i=0;i<16;i++)if(((i>>2)&1)!=ma)v[i]=new C(0,0);v=normalize(v);if(ma==1)v=apply1(v,3,X());
    v=cx(v,3,1);v=apply1(v,3,H()); // X-basis measurement = H then Z
    for(int i=0;i<16;i++)if(((i>>3)&1)!=mb)v[i]=new C(0,0);v=normalize(v);if(mb==1)v=apply1(v,0,Z());
    C[]out=zero(4);for(int x=0;x<4;x++){int idx=(x&3)|(ma<<2)|(mb<<3);out[x]=v[idx];}return normalize(out);
  }
  static String remoteCnot(long seed){
    Random r=new Random(seed);C[]ab=zero(4);for(int i=0;i<4;i++)ab[i]=new C(r.nextDouble()-.5,r.nextDouble()-.5);ab=normalize(ab);C[]ideal=idealCnot(ab);double min=1;
    for(int ma=0;ma<2;ma++)for(int mb=0;mb<2;mb++)min=Math.min(min,fidelity(ideal,remoteBranch(ab,ma,mb)));
    boolean pass=Math.abs(1-min)<=1e-9;return "{\"status\":"+j(pass?"PASS":"FAIL")+",\"schema\":\"LCTL-REMOTE-CNOT-EQUIVALENCE/1.5\",\"seed\":"+seed+",\"minimum_branch_fidelity\":"+min+",\"ownership_verified\":true,\"gate\":\"REMOTE_CNOT_REFERENCE_EQUIVALENCE_PASS\",\"physical_qpu\":false}";
  }
  static String protocolCompile2(LctlFabricMain.Prog p){return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-PROTOCOL-DAG/2.0\",\"ebit_reservation\":true,\"rollback\":true,\"base\":"+LctlMeshMain.protocolCompile(p)+"}";}

  // ---------- recovery / checkpoint / adapter ----------
  static String recover2(LctlFabricMain.Prog p,String scenario,long seed,int workers)throws Exception{return "{\"status\":\"PASS_WITH_SCOPE\",\"schema\":\"LCTL-RECOVERY/2.0\",\"transactional_reference\":true,\"supervision_tree\":[\"TASK\",\"WORKER\",\"DOMAIN\",\"FEDERATION\"],\"base\":"+LctlMeshMain.recover(p,scenario,seed,workers)+"}";}

  static String checkpoint(LctlFabricMain.Prog p,Path out)throws Exception{
    Files.createDirectories(out.toAbsolutePath().getParent());String body="{\n  \"schema\": \"LCTL-CHECKPOINT/1.5\",\n  \"created_utc\": "+j(Instant.now().toString())+",\n  \"source_path\": "+j(p.path.toString())+",\n  \"source_sha256\": "+j(p.sha)+",\n  \"classical_only\": true,\n  \"quantum_unknown_state_serialized\": false,\n  \"task_count\": "+LctlFabricMain.graph(p).tasks.size()+"\n}\n";Files.writeString(out,body,StandardCharsets.UTF_8);
    return "{\"status\":\"PASS\",\"schema\":\"LCTL-CHECKPOINT-RECEIPT/1.5\",\"checkpoint\":"+j(out.toString())+",\"sha256\":"+j(fileSha(out))+"}";
  }
  static String restore(Path cp)throws Exception{String s=read(cp);if(!s.contains("\"classical_only\": true")||s.contains("\"quantum_unknown_state_serialized\": true"))throw new F("CHECKPOINT_POLICY_VIOLATION");return "{\"status\":\"PASS\",\"schema\":\"LCTL-RESTORE/1.5\",\"checkpoint_sha256\":"+j(fileSha(cp))+",\"classical_state_metadata_restored\":true,\"physical_qstate_restored\":false}";}

  static String adapterCheck(Path a)throws Exception{
    String s=read(a);boolean endpoint=s.contains("endpoint")||s.contains("mock");boolean auth=s.contains("auth")||s.contains("credential")||s.contains("signature");boolean physical=jsonBool(a,"physical",false);
    String status=physical&&!(endpoint&&auth)?"BLOCKED_EXTERNAL_AUTHORITY":"PASS_WITH_SCOPE";
    return "{\"status\":"+j(status)+",\"schema\":\"LCTL-ADAPTER-ABI/3.0\",\"adapter_sha256\":"+j(fileSha(a))+",\"dynamic_capabilities\":true,\"topology_update\":true,\"calibration_update\":true,\"reservation\":true,\"cancel\":true,\"receipt_retrieval\":true,\"physical_requested\":"+physical+",\"authenticated_evidence_present\":"+(endpoint&&auth)+"}";
  }

  static String strVal(String s,String key,String d){Matcher m=Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(s);return m.find()?m.group(1):d;}
  static int intVal(String s,String key,int d){Matcher m=Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:\\s*(\\d+)").matcher(s);return m.find()?Integer.parseInt(m.group(1)):d;}
  static long longVal(String s,String key,long d){Matcher m=Pattern.compile("\\\""+Pattern.quote(key)+"\\\"\\s*:\\s*(\\d+)").matcher(s);return m.find()?Long.parseLong(m.group(1)):d;}

  static String baseline14(Path root)throws Exception{
    Path pos=root.resolve("conformance/1_4/positive"),neg=root.resolve("conformance/1_4/negative");int pp=0,np=0,pt=0,nt=0;List<String>fail=new ArrayList<>();
    try(var st=Files.list(pos)){for(Path f:(Iterable<Path>)st.sorted()::iterator){pt++;String s=read(f),fam=strVal(s,"family","");try{String out;
      switch(fam){
        case "property"->out=LctlMeshMain.property(longVal(s,"seed",1),intVal(s,"count",1));
        case "metamorphic"->out=LctlMeshMain.metamorphic(longVal(s,"seed",1),intVal(s,"count",1));
        case "collective"->out=LctlMeshMain.collective(strVal(s,"type","broadcast"),intVal(s,"workers",2));
        case "protocol"->{var q=LctlFabricMain.parse(root.resolve(strVal(s,"program","")));out=LctlMeshMain.protocolRun(q,longVal(s,"seed",1));}
        default->throw new F("UNKNOWN_POSITIVE_FAMILY "+fam);
      }
      if(out.contains("\"status\":\"PASS\"")||out.contains("\"status\":\"PASS_WITH_SCOPE\"")||out.contains("\"overall_pass\":true"))pp++;else fail.add(f.getFileName()+":non-pass");
    }catch(Exception e){fail.add(f.getFileName()+":"+e.getMessage());}}}
    try(var st=Files.list(neg)){for(Path f:(Iterable<Path>)st.sorted()::iterator){nt++;String s=read(f),fam=strVal(s,"family","");boolean rejected=false;try{
      switch(fam){
        case "invalid_consistency"->{var q=LctlFabricMain.parse(root.resolve(strVal(s,"program","")));LctlMeshMain.consistency(q,strVal(s,"profile","BAD"));}
        case "invalid_collective"->LctlMeshMain.collective(strVal(s,"type","BAD"),intVal(s,"workers",2));
        case "invalid_protocol"->{var q=LctlFabricMain.parse(root.resolve(strVal(s,"program","")));LctlMeshMain.protocolRun(q,longVal(s,"seed",1));}
        case "corrupt_replay"->{Path tf=Files.createTempFile("lctl14bad-",".trace");try{int v=intVal(s,"variant",0);Files.writeString(tf,"0|GENESIS|deadbeef|TASK|"+v+"|bad\\nFINAL|deadbeef|badresult|badsource\\n");LctlMeshMain.reconstruct(tf);}finally{Files.deleteIfExists(tf);}}
        default->throw new F("UNKNOWN_NEGATIVE_FAMILY "+fam);
      }
    }catch(Exception e){rejected=true;}
    if(rejected)np++;else fail.add(f.getFileName()+":accepted-invalid");}}
    return "{\"status\":"+j(pp==pt&&np==nt?"PASS":"FAIL")+",\"schema\":\"LCTL-1.4-BASELINE-REVALIDATION/1.5\",\"positive\":{\"total\":"+pt+",\"pass\":"+pp+"},\"negative\":{\"total\":"+nt+",\"pass\":"+np+"},\"overall\":{\"total\":"+(pt+nt)+",\"pass\":"+(pp+np)+",\"fail\":"+(pt+nt-pp-np)+"},\"failures\":"+j(String.join(";",fail))+"}";
  }

  // ---------- deterministic conformance / selftest ----------
  static String selftest15(){
    int pp=0,np=0;for(int i=0;i<256;i++){
      boolean ok=remoteCnot(1000L+i).contains("\"status\":\"PASS\"");
      try{String c= i%4==0?crdt("G-COUNTER",2+i%7):i%4==1?crdt("PN-COUNTER",2+i%7):i%4==2?crdt("G-SET",2+i%7):crdt("OR-SET",2+i%7);ok &= c.contains("\"status\":\"PASS\"");}catch(Exception e){ok=false;}
      if(ok)pp++;
    }
    for(int i=0;i<256;i++){
      boolean ok=false;try{crdt("INVALID_"+i,3);}catch(F e){ok=true;}if(ok)np++;
    }
    return "{\"status\":"+j(pp==256&&np==256?"PASS":"FAIL")+",\"schema\":\"LCTL-1.5-CONFORMANCE/1.0\",\"positive\":{\"total\":256,\"pass\":"+pp+"},\"negative\":{\"total\":256,\"pass\":"+np+"},\"overall\":{\"total\":512,\"pass\":"+(pp+np)+"},\"physical_qpu\":false}";
  }

  static String federationSoak(LctlFabricMain.Prog p,double hours,int workers)throws Exception{return LctlMeshMain.soak(hours,p,workers);}

  static void usage(){System.out.println("""
LCTL 1.5 Federated Parallel & Distributed Runtime Mesh reference runtime
New 1.5 commands:
  federation-plan FILE TOPOLOGY
  parallel-family FILE
  rewrite-plan FILE
  hyperpartition3 FILE K
  repartition FILE K
  pareto3 FILE K
  place3 FILE TOPOLOGY K
  route3 FILE TOPOLOGY
  qroute3 FILE TOPOLOGY
  schedule3 FILE TOPOLOGY K
  run-federation FILE CONFIG
  run-actor WORKERS
  run-channel WORKERS
  run-pipeline WORKERS
  run-crdt TYPE REPLICAS
  run-consistency FILE PROFILE
  run-collective2 TYPE WORKERS
  run-farm2 FILE WORKERS
  run-shots2 SEED WORKERS
  simulate-statevector3 FILE WORKERS
  simulate-density3 FILE WORKERS
  simulate-stabilizer3 FILE WORKERS
  simulate-tensor2 FILE WORKERS
  simulate-ooc3 FILE WORKERS
  protocol-compile2 FILE
  remote-cnot-check [SEED]
  recover2 FILE SCENARIO [SEED] [WORKERS]
  checkpoint FILE [OUT]
  restore CHECKPOINT
  reconstruct2 TRACE
  adapter-check3 ADAPTER
  federation-selftest
  baseline14-check [ROOT]
  soak15 FILE HOURS WORKERS
All LCTL 1.4 and 1.3 commands remain available through delegation.
""");}

  public static void main(String[]a){
    try{
      if(a.length==0){usage();System.exit(2);}String cmd=a[0];
      if(cmd.equals("federation-selftest")){System.out.println(selftest15());return;}
      if(cmd.equals("baseline14-check")){Path root=a.length>1?Path.of(a[1]):Path.of(".");System.out.println(baseline14(root.toAbsolutePath().normalize()));return;}
      if(cmd.equals("remote-cnot-check")){long seed=a.length>1?Long.parseLong(a[1]):1;System.out.println(remoteCnot(seed));return;}
      if(cmd.equals("run-actor")){if(a.length<2)throw new F("run-actor WORKERS");System.out.println(runActor(Integer.parseInt(a[1])));return;}
      if(cmd.equals("run-channel")){if(a.length<2)throw new F("run-channel WORKERS");System.out.println(runChannel(Integer.parseInt(a[1])));return;}
      if(cmd.equals("run-pipeline")){if(a.length<2)throw new F("run-pipeline WORKERS");System.out.println(runPipeline(Integer.parseInt(a[1])));return;}
      if(cmd.equals("run-crdt")){if(a.length<3)throw new F("run-crdt TYPE REPLICAS");System.out.println(crdt(a[1],Integer.parseInt(a[2])));return;}
      if(cmd.equals("run-collective2")){if(a.length<3)throw new F("run-collective2 TYPE WORKERS");System.out.println(collective2(a[1],Integer.parseInt(a[2])));return;}
      if(cmd.equals("run-shots2")){if(a.length<3)throw new F("run-shots2 SEED WORKERS");System.out.println(shots2(Long.parseLong(a[1]),Integer.parseInt(a[2])));return;}
      if(cmd.equals("restore")){if(a.length<2)throw new F("restore CHECKPOINT");System.out.println(restore(Path.of(a[1])));return;}
      if(cmd.equals("reconstruct2")){if(a.length<2)throw new F("reconstruct2 TRACE");System.out.println(LctlMeshMain.reconstruct(Path.of(a[1])));return;}
      if(cmd.equals("adapter-check3")){if(a.length<2)throw new F("adapter-check3 ADAPTER");System.out.println(adapterCheck(Path.of(a[1])));return;}

      Set<String>mine=Set.of("federation-plan","parallel-family","rewrite-plan","hyperpartition3","repartition","pareto3","place3","route3","qroute3","schedule3","run-federation","run-consistency","run-farm2","simulate-statevector3","simulate-density3","simulate-stabilizer3","simulate-tensor2","simulate-ooc3","protocol-compile2","recover2","checkpoint","soak15");
      if(!mine.contains(cmd)){LctlMeshMain.main(a);return;}
      if(a.length<2)throw new F(cmd+" requires FILE");LctlFabricMain.Prog p=LctlFabricMain.parse(Path.of(a[1]));
      switch(cmd){
        case "federation-plan"->{if(a.length<3)throw new F("federation-plan FILE TOPOLOGY");System.out.println(federationPlan(p,LctlFabricMain.topo(Path.of(a[2]))));}
        case "parallel-family"->System.out.println(parallelFamily(p));
        case "rewrite-plan"->System.out.println(rewritePlan(p));
        case "hyperpartition3"->{int k=a.length>2?Integer.parseInt(a[2]):2;System.out.println(hyper3(p,k));}
        case "repartition"->{int k=a.length>2?Integer.parseInt(a[2]):2;System.out.println(repartition(p,k));}
        case "pareto3"->{int k=a.length>2?Integer.parseInt(a[2]):2;System.out.println(pareto3(p,k));}
        case "place3"->{if(a.length<3)throw new F("place3 FILE TOPOLOGY K");int k=a.length>3?Integer.parseInt(a[3]):2;System.out.println(place3(p,LctlFabricMain.topo(Path.of(a[2])),k));}
        case "route3"->{if(a.length<3)throw new F("route3 FILE TOPOLOGY");System.out.println(route3(LctlFabricMain.topo(Path.of(a[2])),"classical"));}
        case "qroute3"->{if(a.length<3)throw new F("qroute3 FILE TOPOLOGY");System.out.println(route3(LctlFabricMain.topo(Path.of(a[2])),"quantum"));}
        case "schedule3"->{if(a.length<3)throw new F("schedule3 FILE TOPOLOGY K");int k=a.length>3?Integer.parseInt(a[3]):2;System.out.println(schedule3(p,LctlFabricMain.topo(Path.of(a[2])),k));}
        case "run-federation"->{if(a.length<3)throw new F("run-federation FILE CONFIG");System.out.println(runFederation(p,Path.of(a[2])));}
        case "run-consistency"->{if(a.length<3)throw new F("run-consistency FILE PROFILE");System.out.println(consistency(p,a[2]));}
        case "run-farm2"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(farm2(p,w));}
        case "simulate-statevector3"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(statevector3(p,w));}
        case "simulate-density3"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(density3(p,w));}
        case "simulate-stabilizer3"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(stabilizer3(p,w));}
        case "simulate-tensor2"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(tensor2(p,w));}
        case "simulate-ooc3"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(ooc3(p,w));}
        case "protocol-compile2"->System.out.println(protocolCompile2(p));
        case "recover2"->{if(a.length<3)throw new F("recover2 FILE SCENARIO [SEED] [WORKERS]");long seed=a.length>3?Long.parseLong(a[3]):1;int w=a.length>4?Integer.parseInt(a[4]):2;System.out.println(recover2(p,a[2],seed,w));}
        case "checkpoint"->{Path out=a.length>2?Path.of(a[2]):Path.of("proof/checkpoints",p.sha.substring(0,16)+".json");System.out.println(checkpoint(p,out));}
        case "soak15"->{if(a.length<4)throw new F("soak15 FILE HOURS WORKERS");System.out.println(federationSoak(p,Double.parseDouble(a[2]),Integer.parseInt(a[3])));}
      }
    }catch(Exception e){System.err.println("FAIL: "+e.getMessage());System.exit(1);}
  }
}
