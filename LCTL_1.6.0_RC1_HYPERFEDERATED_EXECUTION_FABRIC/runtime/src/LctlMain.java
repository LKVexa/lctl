import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

public final class LctlMain {
  static final Set<String> MAGICS=Set.of("LCTL/1.0","LCTL/1.1","LCTL/1.2","LCTL/1.3");
  static final String BC="BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF";
  static final String FC="ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF";
  static final String RC="ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF";
  static final Set<String> FACES=Set.of("DECL","CODE","MODEL","ASSERT","EVIDENCE","LEARN","RESOURCE","TRANSFORM","PARALLEL","COUPLING","FIELD","DISTRIBUTED","TOPOLOGY","PROTOCOL","FAILURE","MEMORY");
  static final Set<String> REGIMES=Set.of("exact","exact_linear","perturbative","linearized","variational","stochastic","approximate","asymptotic","noisy","hardware_calibrated","structural_analogue","combinatorial","impossible","unsupported","no_faithful_form");
  static final Set<String> UNARY=Set.of("I","X","Y","Z","H","S","SDG","T","TDG","RX","RY","RZ","PHASE");
  static final Set<String> BINARY=Set.of("CX","CNOT","CY","CZ","CH","SWAP","ISWAP","CPHASE");
  static final Set<String> NOISE=Set.of("DEPOLARIZE","DEPHASE","BIT_FLIP","PHASE_FLIP","AMPLITUDE_DAMP","PHASE_DAMP");
  static final double TOL=1e-9;

  static class X extends RuntimeException { X(String m){super(m);} }
  static class C { double r,i; C(double r,double i){this.r=r;this.i=i;} C add(C z){return new C(r+z.r,i+z.i);} C sub(C z){return new C(r-z.r,i-z.i);} C mul(C z){return new C(r*z.r-i*z.i,r*z.i+i*z.r);} C scale(double a){return new C(a*r,a*i);} C conj(){return new C(r,-i);} double abs2(){return r*r+i*i;} static C zero(){return new C(0,0);} static C one(){return new C(1,0);} }
  static class Row { String[] x; Row(String[] x){this.x=x;} String id(){return x[0];} String face(){return x[1];} String lane(){return x[2];} String qs(){return x[3];} String op(){return x[4];} String out(){return x[5];} String ctrl(){return x[6];} String a(){return x[7];} String b(){return x[8];} String param(){return x[9];} String type(){return x[10];} String basis(){return x[11];} String regime(){return x[12];} String assume(){return x[13];} String error(){return x[14];} String resource(){return x[15];} String conf(){return x[16];} String proof(){return x[17];} }
  static class Frame { String[] f; List<Row> rows=new ArrayList<>(); String id(){return f[0];} String parent(){return f[1];} String admit(){return f[14];} }
  static class Unit { Path path; String magic; String[] b; List<Frame> frames=new ArrayList<>(); String canonical,declared,computed; Map<String,Row> rows=new LinkedHashMap<>(); List<Row> ordered=new ArrayList<>(); }
  static class Program { LinkedHashMap<String,Integer> qidx=new LinkedHashMap<>(); LinkedHashMap<String,Integer> cidx=new LinkedHashMap<>(); int nqubits=0,ncbits=0; List<Row> exec=new ArrayList<>(); double approxError=0; }
  static class Result { C[] psi; C[][] rho; boolean density; int[] cbits; Map<String,Double> expectations=new LinkedHashMap<>(); long steps; }

  static String norm(String s){return s.replace("\r\n","\n").replace("\r","\n");}
  static String sha(String s){try{MessageDigest md=MessageDigest.getInstance("SHA-256");byte[] h=md.digest(s.getBytes(StandardCharsets.UTF_8));StringBuilder b=new StringBuilder();for(byte x:h)b.append(String.format("%02x",x&255));return b.toString();}catch(Exception e){throw new RuntimeException(e);}}
  static String[] tup(String s,int n,String what){s=s.trim();if(!(s.startsWith("(")&&s.endsWith(")")))throw new X(what+": tuple required");String[] a=s.substring(1,s.length()-1).split("¦",-1);if(a.length!=n)throw new X(what+": expected "+n+" cells got "+a.length);for(int i=0;i<n;i++)a[i]=a[i].trim();return a;}
  static double num(String s,String what){try{return Double.parseDouble(s);}catch(Exception e){throw new X(what+": bad number "+s);}}
  static int uint(String s,String what){try{int v=Integer.parseInt(s);if(v<0)throw new Exception();return v;}catch(Exception e){throw new X(what+": bad nonnegative integer "+s);}}
  static double conf(String s,String what){double v=num(s,what);if(v<0||v>1)throw new X(what+": confidence out of range");return v;}
  static boolean blank(String s){return s.equals("_")||s.isBlank();}

  static Unit parse(Path p, boolean requireSeal) throws IOException {
    List<String> L=new ArrayList<>(Arrays.asList(norm(Files.readString(p,StandardCharsets.UTF_8)).split("\n",-1)));
    if(L.size()<7||!MAGICS.contains(L.get(0)))throw new X("PARSE: bad magic");
    if(!BC.equals(L.get(1)))throw new X("PARSE: bad bundle columns");
    Unit u=new Unit();u.path=p;u.magic=L.get(0);u.b=tup(L.get(2),16,"BUNDLE");
    if(!"bundle".equals(u.b[0]))throw new X("BUNDLE: first cell must be bundle");
    if(!Set.of("lctl.quantum.core","lctl.quantum.parallel","lctl.quantum.parallel.distributed","lctl.quantum.parallel.distributed.native").contains(u.b[3]))throw new X("BUNDLE: unsupported LCTL profile "+u.b[3]);
    if(!"none".equals(u.b[7]))throw new X("BUNDLE_POLICY: backend must be none");
    if(!"deny".equals(u.b[8]))throw new X("BUNDLE_POLICY: network must be deny");
    if(!u.b[9].startsWith("deterministic"))throw new X("BUNDLE_POLICY: deterministic replay required");
    if(!"q1-q6".equals(u.b[13]))throw new X("BUNDLE_POLICY: q1-q6 admission required");
    int declaredFrames=uint(u.b[5],"frames");
    double budget=num(u.b[11],"error budget"); if(budget<0)throw new X("BUNDLE: negative error budget");
    int i=3;Set<String> fids=new HashSet<>(),rids=new HashSet<>();
    while(i<L.size()&&L.get(i).startsWith("QFRAME/1.0 BEGIN ")){
      int fs=i;String did=L.get(i).substring("QFRAME/1.0 BEGIN ".length()).trim();i++;
      if(i>=L.size()||!FC.equals(L.get(i)))throw new X("FRAME: columns "+did);i++;
      Frame f=new Frame();f.f=tup(L.get(i),16,"FRAME "+did);i++;
      if(!did.equals(f.id()))throw new X("FRAME: begin id mismatch");if(!fids.add(f.id()))throw new X("FRAME: duplicate "+f.id());
      conf(f.f[12],"FRAME "+f.id()); if(!Set.of("admit","observe","reject").contains(f.f[14]))throw new X("FRAME: bad ADMIT "+f.f[14]);
      if(i>=L.size()||!"QTUPLE/1.0".equals(L.get(i)))throw new X("FRAME: missing QTUPLE");i++;
      if(i>=L.size()||!RC.equals(L.get(i)))throw new X("ROW: bad columns");i++;
      Set<String> ev=new HashSet<>();boolean hasLearn=false;
      while(i<L.size()&&!"END QTUPLE".equals(L.get(i))){String s=L.get(i).trim();i++;if(s.isEmpty()||s.startsWith("#"))continue;String[] r=tup(s,18,"ROW "+f.id());Row rr=new Row(r);if(!rids.add(rr.id()))throw new X("ROW: duplicate "+rr.id());if(!FACES.contains(rr.face()))throw new X("ROW: bad FACE "+rr.face());if(!REGIMES.contains(rr.regime()))throw new X("ROW: bad REGIME "+rr.regime());conf(rr.conf(),"ROW "+rr.id());if(rr.face().equals("LEARN"))hasLearn=true;if(rr.face().equals("EVIDENCE")&&rr.op().matches("Q[1-6]")&&"pass".equals(rr.a()))ev.add(rr.op());f.rows.add(rr);u.rows.put(rr.id(),rr);u.ordered.add(rr);}
      if(i>=L.size()||!"END QTUPLE".equals(L.get(i)))throw new X("FRAME: missing END QTUPLE");i++;
      if(i>=L.size()||!"END QFRAME".equals(L.get(i)))throw new X("FRAME: missing END QFRAME");i++;
      String canon=String.join("\n",L.subList(fs,i))+"\n";String computed=sha(canon);
      if(i>=L.size()||!L.get(i).startsWith("@frame sha256:"))throw new X("FRAME_SEAL: missing "+f.id());String seal=L.get(i).substring("@frame sha256:".length()).trim().toLowerCase(Locale.ROOT);if(!seal.equals(computed))throw new X("FRAME_SEAL: mismatch "+f.id());i++;
      if(hasLearn){if(!"admit".equals(f.admit()))throw new X("LEARN_GATE: "+f.id()+" contains LEARN but ADMIT="+f.admit());for(int q=1;q<=6;q++)if(!ev.contains("Q"+q))throw new X("LEARN_GATE: "+f.id()+" missing Q"+q);}
      u.frames.add(f);
    }
    if(u.frames.size()!=declaredFrames)throw new X("BUNDLE: frame count mismatch");
    for(Frame f:u.frames)if(!"ROOT".equals(f.parent())&&!fids.contains(f.parent()))throw new X("FRAME: unknown parent "+f.parent());
    if(i>=L.size()||!"END LCTL".equals(L.get(i)))throw new X("BUNDLE: missing END LCTL");
    u.canonical=String.join("\n",L.subList(0,i+1))+"\n";u.computed=sha(u.canonical);i++;
    while(i<L.size()&&L.get(i).isBlank())i++;
    if(i<L.size()&&L.get(i).startsWith("@bundle sha256:")){u.declared=L.get(i).substring("@bundle sha256:".length()).trim().toLowerCase(Locale.ROOT);i++;}
    if(requireSeal&&u.declared==null)throw new X("BUNDLE_SEAL: missing");if(u.declared!=null&&!u.declared.equals(u.computed))throw new X("BUNDLE_SEAL: mismatch");
    while(i<L.size()){if(!L.get(i).isBlank())throw new X("PARSE: content after bundle seal");i++;}
    validate(u);return u;
  }

  static Program validate(Unit u){
    Program p=new Program();Map<String,String> qstate=new HashMap<>();Map<String,String> qowner=new HashMap<>();Set<String> measured=new HashSet<>();
    for(Row r:u.ordered){
      if(r.face().equals("DECL")){
        if(r.op().equals("QREG")){int n=uint(r.param(),"QREG "+r.id());if(n<1||n>12)throw new X("RESOURCE: qreg size 1..12 required");if(p.qidx.containsKey(r.out()))throw new X("OWNERSHIP: duplicate qreg "+r.out());for(int k=0;k<n;k++){String q=r.out()+"["+k+"]";p.qidx.put(q,p.nqubits++);qstate.put(q,"active");qowner.put(q,"local");}}
        else if(r.op().equals("CREG")){int n=uint(r.param(),"CREG "+r.id());for(int k=0;k<n;k++)p.cidx.put(r.out()+"["+k+"]",p.ncbits++);}
        else if(r.op().equals("QUBIT")){if(p.qidx.putIfAbsent(r.out(),p.nqubits)!=null)throw new X("OWNERSHIP: duplicate qubit "+r.out());p.nqubits++;qstate.put(r.out(),"active");qowner.put(r.out(),"local");}
        else throw new X("DECL: unsupported "+r.op());continue;
      }
      if(r.face().equals("ASSERT")){validateAssert(r);continue;}
      if(r.face().equals("TRANSFORM")){validateTransform(r);continue;}
      if(r.face().equals("PARALLEL")){validateParallelMeta(r);continue;}
      if(r.face().equals("COUPLING")){validateCouplingMeta(r);continue;}
      if(r.face().equals("FIELD")){validateFieldMeta(r);continue;}
      if(r.face().equals("DISTRIBUTED")){validateDistributedMeta(r,p,qowner);continue;}
      if(r.face().equals("TOPOLOGY")){validateTopologyMeta(r);continue;}
      if(r.face().equals("PROTOCOL")){validateProtocolMeta(r);continue;}
      if(r.face().equals("FAILURE")){validateFailureMeta(r);continue;}
      if(r.face().equals("MEMORY")){validateMemoryMeta(r);continue;}
      if(r.face().equals("MODEL")||r.face().equals("RESOURCE")||r.face().equals("EVIDENCE")||r.face().equals("LEARN"))continue;
      if(!r.face().equals("CODE"))continue;
      String op=r.op();
      if(op.equals("CLONE")||op.equals("ALIAS_Q"))throw new X("NO_CLONING: "+op+" forbidden at "+r.id());
      if(UNARY.contains(op)){qactive(p,qstate,r.a(),r);if(!r.out().equals(r.a())&&!blank(r.out()))throw new X("OWNERSHIP: unary OUT must equal A at "+r.id());if(Set.of("RX","RY","RZ","PHASE").contains(op))num(r.param(),"angle "+r.id());}
      else if(BINARY.contains(op)){qactive(p,qstate,r.ctrl(),r);qactive(p,qstate,r.a(),r);if(r.ctrl().equals(r.a()))throw new X("OWNERSHIP: control equals target at "+r.id());if(op.equals("CPHASE"))num(r.param(),"cphase "+r.id());}
      else if(op.equals("CCX")){qactive(p,qstate,r.ctrl(),r);qactive(p,qstate,r.a(),r);qactive(p,qstate,r.b(),r);if(Set.of(r.ctrl(),r.a(),r.b()).size()!=3)throw new X("OWNERSHIP: CCX operands must be disjoint");}
      else if(op.startsWith("PREP")||op.equals("RESET")){qexists(p,r.a(),r);qstate.put(r.a(),"active");measured.remove(r.a());}
      else if(op.startsWith("MEASURE_")){qactive(p,qstate,r.a(),r);if(!p.cidx.containsKey(r.out()))throw new X("TYPE: measurement OUT must be classical bit at "+r.id());qstate.put(r.a(),"measured");measured.add(r.a());}
      else if(op.equals("SAMPLE")){for(String q:splitQubits(r.a()))qactive(p,qstate,q,r);int sh=uint(r.param(),"shots "+r.id());if(sh<1)throw new X("SAMPLE: shot count must be positive at "+r.id());}
      else if(NOISE.contains(op)){qactive(p,qstate,r.a(),r);double v=num(r.param(),"noise parameter "+r.id());if(v<0||v>1)throw new X("NOISE: parameter outside [0,1] at "+r.id());}
      else if(op.equals("CLASSICAL_IF")){if(!p.cidx.containsKey(r.ctrl()))throw new X("TYPE: CLASSICAL_IF ctrl must be cbit");if(!Set.of("0","1").contains(r.a()))throw new X("TYPE: CLASSICAL_IF condition 0/1");qactive(p,qstate,r.out(),r);if(!UNARY.contains(r.b()))throw new X("CONTROL: CLASSICAL_IF B must name unary gate");}
      else if(op.equals("EXPECT")){for(String q:pauliQubits(r.param()))qexists(p,q,r);}
      else if(op.equals("BARRIER")||op.equals("SYNC")||op.equals("FENCE")||op.equals("NOP")){}
      else throw new X("OP: unsupported executable operation "+op+" at "+r.id());
      if(Set.of("approximate","linearized","perturbative","asymptotic").contains(r.regime())){double e=errorBound(r.error(),r.id());if(r.face().equals("CODE")&&!r.assume().contains("approx_allowed"))throw new X("ERROR: approximate CODE requires approx_allowed at "+r.id());p.approxError+=e;}
      else if(Set.of("exact","exact_linear").contains(r.regime())&&!Set.of("exact","0","0.0","_").contains(r.error()))throw new X("ERROR: exact row carries nonzero error at "+r.id());
      p.exec.add(r);
    }
    double budget=num(u.b[11],"error budget");if(p.approxError>budget+1e-15)throw new X("ERROR_BUDGET: "+p.approxError+" > "+budget);
    if(p.nqubits<1)throw new X("PROGRAM: no quantum register declared");
    String[] ep=u.b[4].split(":",2);if(ep.length!=2||!u.rows.containsKey(ep[1]))throw new X("BUNDLE: bad ENTRY");
    return p;
  }
  static void qexists(Program p,String q,Row r){if(blank(q)||!p.qidx.containsKey(q))throw new X("TYPE: unknown qubit "+q+" at "+r.id());}
  static void qactive(Program p,Map<String,String> st,String q,Row r){qexists(p,q,r);if("measured".equals(st.get(q)))throw new X("OWNERSHIP: use-after-measure without reset for "+q+" at "+r.id());}
  static double errorBound(String s,String id){if(blank(s))throw new X("ERROR: approximate row missing error at "+id);String t=s;if(t.startsWith("abs<="))t=t.substring(5);else if(t.startsWith("rel<="))t=t.substring(5);else throw new X("ERROR: use abs<= or rel<= at "+id);double v=num(t,"error "+id);if(v<0)throw new X("ERROR: negative bound");return v;}
  static void validateTransform(Row r){if(blank(r.assume())||"none".equals(r.assume()))throw new X("TRANSFORM: assumptions required at "+r.id());if(blank(r.proof()))throw new X("TRANSFORM: proof required at "+r.id());Set<String> ops=Set.of("D1_FREDHOLM","D2_SPECTRAL","D3_DIRICHLET","D4_PROJECTION","D5_KERNEL_INDEX","D6_LOG_DIFFERENTIAL","D7_BIFURCATION","D8_HEAT_SEMIGROUP","D9_GEODESIC","D10_FOKKER_PLANCK","D11_VARIATIONAL_KKT","D12_FOURIER_SYMBOL","D13_FRECHET_LINEARIZE","D14_OBSTRUCTION","THETA_J_RETRACT","THETA_G_CONTINUUM");if(!ops.contains(r.op()))throw new X("TRANSFORM: unknown calculus transform op "+r.op());if(Set.of("approximate","linearized","asymptotic","perturbative").contains(r.regime()))errorBound(r.error(),r.id());if(r.op().equals("D14_OBSTRUCTION")&&!Set.of("no_faithful_form","impossible","unsupported").contains(r.regime()))throw new X("TRANSFORM: D14 must preserve obstruction regime");}

  static void validateParallelMeta(Row r){Set<String> ops=Set.of("REGION","LANE_BIND","DEPENDENCY","COMM_MODEL","MEMORY_TIER","COLLECTIVE","RELIABILITY","DOMAIN_DECOMP","OVERLAP","CONSENSUS_BOUNDARY");if(!ops.contains(r.op()))throw new X("PARALLEL: unsupported metadata op "+r.op());if(blank(r.proof()))throw new X("PARALLEL: proof/provenance required at "+r.id());if(Set.of("approximate","linearized","perturbative","asymptotic").contains(r.regime()))errorBound(r.error(),r.id());}
  static void validateCouplingMeta(Row r){Set<String> ops=Set.of("BLOCK_COUPLING","RECIPROCITY","COUPLING_STRENGTH","MONOLITHIC","STAGGERED","INTERFACE","COUPLING_UNRESOLVED");if(!ops.contains(r.op()))throw new X("COUPLING: unsupported metadata op "+r.op());if(blank(r.proof()))throw new X("COUPLING: proof/provenance required at "+r.id());if(r.op().equals("COUPLING_UNRESOLVED"))throw new X("COUPLING: unresolved coupling at "+r.id());if(Set.of("approximate","linearized","perturbative","asymptotic").contains(r.regime()))errorBound(r.error(),r.id());}
  static void validateFieldMeta(Row r){Set<String> ops=Set.of("FIELD","TENSOR","BALANCE","CONSTITUTIVE","LINEARIZATION","CONTINUUM_LIMIT","DOMAIN");if(!ops.contains(r.op()))throw new X("FIELD: unsupported metadata op "+r.op());if(blank(r.proof()))throw new X("FIELD: proof/provenance required at "+r.id());if(Set.of("approximate","linearized","perturbative","asymptotic").contains(r.regime()))errorBound(r.error(),r.id());}

  static Map<String,String> kv(String raw){Map<String,String>m=new LinkedHashMap<>();if(blank(raw))return m;for(String part:raw.split(";")){String z=part.trim();if(z.isEmpty())continue;int k=z.indexOf('=');if(k<1)m.put(z,"true");else m.put(z.substring(0,k).trim(),z.substring(k+1).trim());}return m;}
  static String req(Map<String,String>m,String k,String ctx){String v=m.get(k);if(v==null||blank(v))throw new X(ctx+": missing "+k);return v;}
  static boolean yes(Map<String,String>m,String k){return "true".equalsIgnoreCase(m.getOrDefault(k,"false"))||"yes".equalsIgnoreCase(m.getOrDefault(k,"false"))||"1".equals(m.getOrDefault(k,"false"));}
  static double dval(Map<String,String>m,String k,double def,String ctx){if(!m.containsKey(k))return def;return num(m.get(k),ctx+" "+k);}
  static int ival(Map<String,String>m,String k,int def,String ctx){if(!m.containsKey(k))return def;return uint(m.get(k),ctx+" "+k);}

  static void validateDistributedMeta(Row r,Program p,Map<String,String> qowner){
    Set<String> ops=Set.of("NODE","CLUSTER","PARTITION","SHARD","ROUTE","CLASSICAL_CHANNEL","QUANTUM_LINK","REMOTE_HANDLE","CONSISTENCY_SCOPE","FAILURE_DOMAIN","CHECKPOINT_SCOPE","PLACEMENT","MESSAGE","EPOCH","EVENT","FUTURE","QSTATE_BIND","QSTATE_MOVE","QSTATE_COPY","SERIALIZE_QSTATE","REMOTE_QOP","MEASUREMENT_DEPENDENCY","PARALLEL_CONFLICT","PROVENANCE");
    if(!ops.contains(r.op()))throw new X("DISTRIBUTED: unsupported metadata op "+r.op());
    if(blank(r.proof()))throw new X("DISTRIBUTED: proof/provenance required at "+r.id());
    Map<String,String> m=kv(r.param());String ctx="DISTRIBUTED "+r.id();
    switch(r.op()){
      case"QSTATE_BIND"->{String q=req(m,"q",ctx),node=req(m,"node",ctx);qexists(p,q,r);String cur=qowner.get(q);if(cur!=null&&!cur.equals("local")&&!cur.equals(node))throw new X("OWNERSHIP: qstate already bound to "+cur+" at "+r.id());qowner.put(q,node);}
      case"QSTATE_MOVE"->{String q=req(m,"q",ctx),from=req(m,"from",ctx),to=req(m,"to",ctx);qexists(p,q,r);String cur=qowner.get(q);if(!from.equals(cur))throw new X("OWNERSHIP: move source mismatch for "+q+" expected="+cur+" got="+from+" at "+r.id());if(from.equals(to))throw new X("OWNERSHIP: qstate move requires distinct nodes at "+r.id());qowner.put(q,to);}
      case"QSTATE_COPY"->throw new X("NO_CLONING: distributed qstate copy forbidden at "+r.id());
      case"SERIALIZE_QSTATE"->{if(yes(m,"unknown")||"physical".equalsIgnoreCase(m.getOrDefault("state","")))throw new X("NO_CLONING: implicit serialization of unknown physical qstate forbidden at "+r.id());}
      case"REMOTE_QOP"->{String q=req(m,"q",ctx),node=req(m,"node",ctx);qexists(p,q,r);String cur=qowner.get(q);if(!node.equals(cur))throw new X("OWNERSHIP: remote qop owner unresolved/mismatch for "+q+" owner="+cur+" requested="+node+" at "+r.id());}
      case"MEASUREMENT_DEPENDENCY"->{if(!yes(m,"declared"))throw new X("CAUSAL: missing measurement dependency at "+r.id());}
      case"PARALLEL_CONFLICT"->{String mode=req(m,"mode",ctx);if(Set.of("write_write","read_write","resource").contains(mode))throw new X("PARALLEL: unresolved "+mode+" conflict at "+r.id());}
      case"MESSAGE"->{double bytes=dval(m,"bytes",0,ctx),budget=dval(m,"budget",Double.POSITIVE_INFINITY,ctx);if(bytes<0)throw new X("COMM: negative bytes at "+r.id());if(bytes>budget)throw new X("COMM: communication budget exceeded at "+r.id());}
      case"CONSISTENCY_SCOPE"->{if(yes(m,"partitioned")&&yes(m,"exact_all")&&yes(m,"cap_forbidden"))throw new X("IMPOSSIBLE: requested exact consistency/availability under declared partition at "+r.id());}
      case"PROVENANCE"->{if(!yes(m,"complete"))throw new X("PROVENANCE: distributed result incomplete at "+r.id());}
      default->{}
    }
    if(Set.of("approximate","linearized","perturbative","asymptotic").contains(r.regime()))errorBound(r.error(),r.id());
  }

  static void validateTopologyMeta(Row r){
    Set<String> ops=Set.of("NODE","LINK","QPU","CPU","GPU","MEMORY_DOMAIN","NUMA_DOMAIN","TOPOLOGY_BIND","CONCURRENCY","CROSSTALK","COHERENCE","CALIBRATION");
    if(!ops.contains(r.op()))throw new X("TOPOLOGY: unsupported metadata op "+r.op());
    if(blank(r.proof()))throw new X("TOPOLOGY: proof/provenance required at "+r.id());
    Map<String,String>m=kv(r.param());String ctx="TOPOLOGY "+r.id();
    switch(r.op()){
      case"LINK"->{String src=req(m,"src",ctx),dst=req(m,"dst",ctx),type=req(m,"type",ctx);if(src.equals(dst))throw new X("TOPOLOGY: self-link forbidden at "+r.id());if(!Set.of("classical","quantum").contains(type))throw new X("TOPOLOGY: link type classical|quantum required at "+r.id());double lat=dval(m,"latency",0,ctx);if(lat<0)throw new X("TOPOLOGY: negative latency at "+r.id());if(type.equals("quantum")){double f=dval(m,"fidelity",-1,ctx);if(f<0||f>1)throw new X("TOPOLOGY: quantum link fidelity [0,1] required at "+r.id());}}
      case"CONCURRENCY"->{int requested=ival(m,"requested",0,ctx),max=ival(m,"max",0,ctx);if(requested>max)throw new X("TOPOLOGY: impossible target concurrency at "+r.id());}
      case"CROSSTALK"->{double use=dval(m,"expected",0,ctx),budget=dval(m,"budget",0,ctx);if(use>budget)throw new X("TOPOLOGY: crosstalk budget exceeded at "+r.id());}
      case"COHERENCE"->{double dur=dval(m,"duration",0,ctx),limit=dval(m,"limit",0,ctx);if(dur>limit)throw new X("TOPOLOGY: coherence window exceeded at "+r.id());}
      case"CALIBRATION"->{double age=dval(m,"age",0,ctx),max=dval(m,"max_age",Double.POSITIVE_INFINITY,ctx);if(age>max)throw new X("TOPOLOGY: target calibration expired at "+r.id());}
      default->{}
    }
  }

  static void validateProtocolMeta(Row r){
    Set<String> ops=Set.of("ENTANGLE_LINK","EPR_RESERVE","EPR_RELEASE","TELEPORT","REMOTE_CONTROL","REMOTE_CNOT","REMOTE_MEASURE","CLASSICAL_FEEDBACK","ENTANGLEMENT_SWAP","PURIFY","HERALD","SYNC_QCLOCK","QCHANNEL_SEND","QCHANNEL_RECEIVE");
    if(!ops.contains(r.op()))throw new X("PROTOCOL: unsupported quantum-network operation "+r.op());
    if(blank(r.proof()))throw new X("PROTOCOL: proof/provenance required at "+r.id());
    Map<String,String>m=kv(r.param());String ctx="PROTOCOL "+r.id();
    switch(r.op()){
      case"EPR_RESERVE"->{int n=ival(m,"count",0,ctx);if(n<1)throw new X("PROTOCOL: insufficient ebit count at "+r.id());if(yes(m,"expired"))throw new X("PROTOCOL: expired entanglement resource at "+r.id());}
      case"TELEPORT"->{req(m,"src",ctx);req(m,"dst",ctx);String e=req(m,"ebit",ctx);String c=req(m,"classical",ctx);if(blank(e))throw new X("PROTOCOL: teleport requires entanglement at "+r.id());if(blank(c))throw new X("PROTOCOL: teleport requires classical message path at "+r.id());int n=ival(m,"ebit_count",1,ctx);if(n<1)throw new X("PROTOCOL: insufficient ebit count at "+r.id());}
      case"REMOTE_CNOT","REMOTE_CONTROL"->{String route=req(m,"route",ctx);if(blank(route))throw new X("PROTOCOL: remote gate has no route at "+r.id());if(m.containsKey("target_supported")&&!yes(m,"target_supported"))throw new X("PROTOCOL: remote gate unsupported by target at "+r.id());}
      case"ENTANGLEMENT_SWAP"->{int n=ival(m,"ebit_count",0,ctx);if(n<2)throw new X("PROTOCOL: entanglement swap requires two ebits at "+r.id());}
      default->{}
    }
    if(Set.of("approximate","linearized","perturbative","asymptotic","noisy","hardware_calibrated").contains(r.regime())&&!Set.of("noisy","hardware_calibrated").contains(r.regime()))errorBound(r.error(),r.id());
  }

  static void validateFailureMeta(Row r){
    Set<String> ops=Set.of("NODE_UNAVAILABLE","PROCESS_FAILURE","LINK_FAILURE","TIMEOUT","CLASSICAL_PACKET_LOSS","QUANTUM_LINK_FAILURE","CALIBRATION_EXPIRED","QPU_UNAVAILABLE","COHERENCE_EXCEEDED","RESOURCE_EXHAUSTION","TARGET_DRIFT","SCHEDULER_DEADLOCK","OWNERSHIP_AMBIGUITY","CHECKPOINT");
    if(!ops.contains(r.op()))throw new X("FAILURE: unsupported failure class "+r.op());
    if(blank(r.proof()))throw new X("FAILURE: proof/evidence required at "+r.id());
    Map<String,String>m=kv(r.param());
    if(r.op().equals("CHECKPOINT")&&"physical_unknown_qstate".equals(m.get("kind"))&&!yes(m,"qualified_protocol"))throw new X("CHECKPOINT: unknown physical qstate cannot be checkpointed at "+r.id());
  }

  static void validateMemoryMeta(Row r){
    Set<String> ops=Set.of("REGISTER","LOCAL","CACHE","NUMA","DEVICE","HOST","REMOTE","PERSISTENT","CAPACITY","MOVE");
    if(!ops.contains(r.op()))throw new X("MEMORY: unsupported memory op "+r.op());
    if(blank(r.proof()))throw new X("MEMORY: proof/provenance required at "+r.id());
    Map<String,String>m=kv(r.param());String ctx="MEMORY "+r.id();
    if(r.op().equals("CAPACITY")){double used=dval(m,"used",0,ctx),cap=dval(m,"capacity",0,ctx);if(used>cap)throw new X("MEMORY: capacity exceeded at "+r.id());}
  }

  static C parseComplex(String s){s=s.trim();if(s.contains(":")){String[] a=s.split(":",-1);if(a.length!=2)throw new X("MATRIX: bad complex cell "+s);return new C(num(a[0],"complex real"),num(a[1],"complex imag"));}return new C(num(s,"complex"),0);}
  static C[][] matrix(String s){String[] rows=s.split(";",-1);int n=rows.length;C[][] m=new C[n][n];for(int i=0;i<n;i++){String[] c=rows[i].split(",",-1);if(c.length!=n)throw new X("MATRIX: square matrix required");for(int j=0;j<n;j++)m[i][j]=parseComplex(c[j]);}return m;}
  static C[][] mul(C[][]a,C[][]b){int n=a.length,m=b[0].length,k=b.length;C[][]o=new C[n][m];for(int i=0;i<n;i++)for(int j=0;j<m;j++){C z=C.zero();for(int t=0;t<k;t++)z=z.add(a[i][t].mul(b[t][j]));o[i][j]=z;}return o;}
  static C[][] dag(C[][]a){int n=a.length,m=a[0].length;C[][]o=new C[m][n];for(int i=0;i<n;i++)for(int j=0;j<m;j++)o[j][i]=a[i][j].conj();return o;}
  static C[][] trans(C[][]a){int n=a.length,m=a[0].length;C[][]o=new C[m][n];for(int i=0;i<n;i++)for(int j=0;j<m;j++)o[j][i]=a[i][j];return o;}
  static C[][] add(C[][]a,C[][]b){C[][]o=new C[a.length][a[0].length];for(int i=0;i<a.length;i++)for(int j=0;j<a[0].length;j++)o[i][j]=a[i][j].add(b[i][j]);return o;}
  static C[][] eye(int n){C[][]o=new C[n][n];for(int i=0;i<n;i++)for(int j=0;j<n;j++)o[i][j]=i==j?C.one():C.zero();return o;}
  static double maxDiff(C[][]a,C[][]b){double d=0;for(int i=0;i<a.length;i++)for(int j=0;j<a[0].length;j++){C z=a[i][j].sub(b[i][j]);d=Math.max(d,Math.sqrt(z.abs2()));}return d;}
  static boolean herm(C[][]a){return maxDiff(a,dag(a))<1e-8;}
  static C det(C[][]a){int n=a.length;if(n==1)return a[0][0];C z=C.zero();for(int j=0;j<n;j++){C[][]m=new C[n-1][n-1];for(int r=1;r<n;r++){int cc=0;for(int c=0;c<n;c++)if(c!=j)m[r-1][cc++]=a[r][c];}C term=a[0][j].mul(det(m));z=(j%2==0)?z.add(term):z.sub(term);}return z;}
  static boolean psd(C[][]a){if(!herm(a))return false;int n=a.length;if(n>8)throw new X("ASSERT: PSD reference check limited to 8x8");for(int mask=1;mask<(1<<n);mask++){int k=Integer.bitCount(mask);C[][]m=new C[k][k];int rr=0;for(int i=0;i<n;i++)if((mask&(1<<i))!=0){int cc=0;for(int j=0;j<n;j++)if((mask&(1<<j))!=0)m[rr][cc++]=a[i][j];rr++;}C d=det(m);if(Math.abs(d.i)>1e-7||d.r<-1e-8)return false;}return true;}
  static void validateAssert(Row r){switch(r.op()){
    case "UNITARY"->{C[][]m=matrix(r.param());if(maxDiff(mul(dag(m),m),eye(m.length))>1e-8)throw new X("ASSERT: invalid unitary at "+r.id());}
    case "HERMITIAN"->{if(!herm(matrix(r.param())))throw new X("ASSERT: invalid Hermitian at "+r.id());}
    case "PROJECTOR"->{C[][]m=matrix(r.param());if(!herm(m)||maxDiff(mul(m,m),m)>1e-8)throw new X("ASSERT: invalid projector at "+r.id());}
    case "DENSITY"->{C[][]m=matrix(r.param());if(!herm(m)||!psd(m))throw new X("ASSERT: density not PSD/Hermitian at "+r.id());C tr=C.zero();for(int i=0;i<m.length;i++)tr=tr.add(m[i][i]);if(Math.abs(tr.r-1)>1e-8||Math.abs(tr.i)>1e-8)throw new X("ASSERT: density trace != 1 at "+r.id());}
    case "POVM"->{String[] ms=r.param().split("\\|\\|",-1);if(ms.length<2)throw new X("ASSERT: POVM needs 2+ effects");C[][]sum=null;for(String s:ms){C[][]m=matrix(s);if(!psd(m))throw new X("ASSERT: POVM effect not PSD at "+r.id());sum=sum==null?m:add(sum,m);}if(maxDiff(sum,eye(sum.length))>1e-8)throw new X("ASSERT: POVM incomplete at "+r.id());}
    case "KRAUS"->{String[] ms=r.param().split("\\|\\|",-1);C[][]sum=null;for(String s:ms){C[][]k=matrix(s);C[][]q=mul(dag(k),k);sum=sum==null?q:add(sum,q);}if(sum==null||maxDiff(sum,eye(sum.length))>1e-8)throw new X("ASSERT: Kraus completeness failed at "+r.id());}
    case "PROBABILITY"->{double v=num(r.param(),"probability");if(v<0||v>1)throw new X("ASSERT: probability outside [0,1] at "+r.id());}
    case "PROB_SUM"->{double s=0;for(String x:r.param().split(",")){double v=num(x,"probability");if(v<0)throw new X("ASSERT: negative probability at "+r.id());s+=v;}if(Math.abs(s-1)>1e-8)throw new X("ASSERT: probability sum != 1 at "+r.id());}
    case "TENSOR_DIM"->{String[] a=r.param().split(",");if(a.length!=3)throw new X("ASSERT: TENSOR_DIM expects a,b,out");int x=uint(a[0],"tensor dim"),y=uint(a[1],"tensor dim"),z=uint(a[2],"tensor dim");if(x*y!=z)throw new X("ASSERT: incompatible tensor dimensions at "+r.id());}
    case "TOPOLOGY"->{for(String e:r.param().split(",")){String[] a=e.trim().split("-");if(a.length!=2||a[0].equals(a[1]))throw new X("ASSERT: invalid hardware topology at "+r.id());}}
    case "COUPLING_RECIPROCITY"->{String[] z=r.param().split("\\|\\|",-1);if(z.length!=2)throw new X("ASSERT: COUPLING_RECIPROCITY expects B||C at "+r.id());C[][]b=matrix(z[0]),c=matrix(z[1]);if(maxDiff(b,trans(c))>1e-8)throw new X("ASSERT: coupling reciprocity B != C^T at "+r.id());}
    case "WORK_SPAN"->{String[] z=r.param().split(",");if(z.length!=2)throw new X("ASSERT: WORK_SPAN expects W,D");double w=num(z[0],"work"),d=num(z[1],"span");if(w<0||d<=0||d>w)throw new X("ASSERT: invalid work/span at "+r.id());}
    case "TOPOLOGY_SEAL"->{Map<String,String>m=kv(r.param());if(!Objects.equals(m.get("declared"),m.get("computed")))throw new X("ASSERT: corrupted topology seal at "+r.id());}
    case "SCHEDULE_SEAL"->{Map<String,String>m=kv(r.param());if(!Objects.equals(m.get("declared"),m.get("computed")))throw new X("ASSERT: corrupted schedule seal at "+r.id());}
    case "PHYSICAL_CLAIM"->{Map<String,String>m=kv(r.param());if("simulator".equals(m.get("source"))&&yes(m,"physical"))throw new X("ASSERT: physical execution claim from simulator at "+r.id());if(yes(m,"distributed")&&ival(m,"qualified_endpoints",0,"physical claim")<2)throw new X("ASSERT: physical distributed claim requires two qualified endpoints at "+r.id());if(yes(m,"distributed")&&!yes(m,"quantum_link_evidence"))throw new X("ASSERT: physical distributed claim lacks quantum-link evidence at "+r.id());}
    default->throw new X("ASSERT: unsupported "+r.op());}}

  static String[] splitQubits(String s){if(blank(s))return new String[0];return Arrays.stream(s.split(",")).map(String::trim).toArray(String[]::new);}
  static List<String> pauliQubits(String p){List<String> q=new ArrayList<>();for(String t:p.split("\\*")){String z=t.trim();int at=z.indexOf('@');if(at>0)q.add(z.substring(at+1));}return q;}

  static C[][] gate(String op,double a){double s=1/Math.sqrt(2);return switch(op){
    case "I"->new C[][]{{C.one(),C.zero()},{C.zero(),C.one()}};
    case "X"->new C[][]{{C.zero(),C.one()},{C.one(),C.zero()}};
    case "Y"->new C[][]{{C.zero(),new C(0,-1)},{new C(0,1),C.zero()}};
    case "Z"->new C[][]{{C.one(),C.zero()},{C.zero(),new C(-1,0)}};
    case "H"->new C[][]{{new C(s,0),new C(s,0)},{new C(s,0),new C(-s,0)}};
    case "S"->new C[][]{{C.one(),C.zero()},{C.zero(),new C(0,1)}};
    case "SDG"->new C[][]{{C.one(),C.zero()},{C.zero(),new C(0,-1)}};
    case "T"->new C[][]{{C.one(),C.zero()},{C.zero(),new C(Math.cos(Math.PI/4),Math.sin(Math.PI/4))}};
    case "TDG"->new C[][]{{C.one(),C.zero()},{C.zero(),new C(Math.cos(-Math.PI/4),Math.sin(-Math.PI/4))}};
    case "RX"->new C[][]{{new C(Math.cos(a/2),0),new C(0,-Math.sin(a/2))},{new C(0,-Math.sin(a/2)),new C(Math.cos(a/2),0)}};
    case "RY"->new C[][]{{new C(Math.cos(a/2),0),new C(-Math.sin(a/2),0)},{new C(Math.sin(a/2),0),new C(Math.cos(a/2),0)}};
    case "RZ"->new C[][]{{new C(Math.cos(-a/2),Math.sin(-a/2)),C.zero()},{C.zero(),new C(Math.cos(a/2),Math.sin(a/2))}};
    case "PHASE"->new C[][]{{C.one(),C.zero()},{C.zero(),new C(Math.cos(a),Math.sin(a))}};
    default->throw new X("SIM: no unary matrix for "+op);};}
  static C[][] controlGate(String op,double a){C[][]m=new C[4][4];for(int i=0;i<4;i++)for(int j=0;j<4;j++)m[i][j]=C.zero();m[0][0]=m[1][1]=C.one();C[][]u=switch(op){case"CX","CNOT"->gate("X",0);case"CY"->gate("Y",0);case"CZ"->gate("Z",0);case"CH"->gate("H",0);case"CPHASE"->gate("PHASE",a);default->null;};if(u==null)throw new X("SIM: bad controlled op");m[2][2]=u[0][0];m[2][3]=u[0][1];m[3][2]=u[1][0];m[3][3]=u[1][1];return m;}
  static C[][] zeros(int n,int m){C[][]o=new C[n][m];for(int i=0;i<n;i++)for(int j=0;j<m;j++)o[i][j]=C.zero();return o;}

  static void apply1(C[]v,int q,C[][]u){int bit=1<<q;for(int base=0;base<v.length;base+=(bit<<1))for(int k=0;k<bit;k++){int i=base+k,j=i+bit;C a=v[i],b=v[j];v[i]=u[0][0].mul(a).add(u[0][1].mul(b));v[j]=u[1][0].mul(a).add(u[1][1].mul(b));}}
  static void applyCU(C[]v,int c,int t,C[][]u){int cb=1<<c,tb=1<<t;for(int i=0;i<v.length;i++)if((i&cb)!=0&&(i&tb)==0){int j=i|tb;C a=v[i],b=v[j];v[i]=u[0][0].mul(a).add(u[0][1].mul(b));v[j]=u[1][0].mul(a).add(u[1][1].mul(b));}}
  static void applySwap(C[]v,int a,int b,boolean iphase){int ba=1<<a,bb=1<<b;for(int i=0;i<v.length;i++)if((i&ba)==0&&(i&bb)!=0){int j=(i|ba)&~bb;C x=v[i],y=v[j];if(iphase){v[i]=new C(-y.i,y.r);v[j]=new C(-x.i,x.r);}else{v[i]=y;v[j]=x;}}}
  static void applyCCX(C[]v,int c1,int c2,int t){int b1=1<<c1,b2=1<<c2,bt=1<<t;for(int i=0;i<v.length;i++)if((i&b1)!=0&&(i&b2)!=0&&(i&bt)==0){int j=i|bt;C z=v[i];v[i]=v[j];v[j]=z;}}
  static C[] cloneVec(C[]v){C[]o=new C[v.length];for(int i=0;i<v.length;i++)o[i]=new C(v[i].r,v[i].i);return o;}
  static C[][] pureDensity(C[]v){C[][]r=zeros(v.length,v.length);for(int i=0;i<v.length;i++)for(int j=0;j<v.length;j++)r[i][j]=v[i].mul(v[j].conj());return r;}
  static C[] basisCol(int n,int j){C[]v=new C[n];for(int i=0;i<n;i++)v[i]=i==j?C.one():C.zero();return v;}
  static void applyExecVec(C[]v,Program p,Row r){String op=r.op();if(UNARY.contains(op)){double a=Set.of("RX","RY","RZ","PHASE").contains(op)?num(r.param(),"angle"):0;apply1(v,p.qidx.get(r.a()),gate(op,a));}
    else if(Set.of("CX","CNOT","CY","CZ","CH","CPHASE").contains(op)){double a=op.equals("CPHASE")?num(r.param(),"angle"):0;applyCU(v,p.qidx.get(r.ctrl()),p.qidx.get(r.a()),switch(op){case"CX","CNOT"->gate("X",0);case"CY"->gate("Y",0);case"CZ"->gate("Z",0);case"CH"->gate("H",0);case"CPHASE"->gate("PHASE",a);default->throw new X("bad");});}
    else if(op.equals("SWAP")||op.equals("ISWAP"))applySwap(v,p.qidx.get(r.ctrl()),p.qidx.get(r.a()),op.equals("ISWAP"));
    else if(op.equals("CCX"))applyCCX(v,p.qidx.get(r.ctrl()),p.qidx.get(r.a()),p.qidx.get(r.b()));
  }
  static C[][] fullU(Program p,Row r){int d=1<<p.nqubits;C[][]u=zeros(d,d);for(int j=0;j<d;j++){C[]v=basisCol(d,j);applyExecVec(v,p,r);for(int i=0;i<d;i++)u[i][j]=v[i];}return u;}
  static C[][] leftRight(C[][]rho,C[][]u){return mul(mul(u,rho),dag(u));}
  static C[][] embed1(int n,int q,C[][]k){int d=1<<n;C[][]u=zeros(d,d);int bit=1<<q;for(int j=0;j<d;j++){int jb=(j&bit)==0?0:1;int base=j&~bit;for(int ob=0;ob<2;ob++){int i=base|(ob==1?bit:0);u[i][j]=k[ob][jb];}}return u;}
  static C[][] channel1(C[][]rho,int n,int q,List<C[][]>ks){C[][]o=zeros(rho.length,rho.length);for(C[][]k:ks){C[][]K=embed1(n,q,k);o=add(o,leftRight(rho,K));}return o;}
  static List<C[][]> kraus(String op,double p){double a=Math.sqrt(Math.max(0,1-p)),b=Math.sqrt(Math.max(0,p));return switch(op){
    case"BIT_FLIP"->List.of(new C[][]{{new C(a,0),C.zero()},{C.zero(),new C(a,0)}},new C[][]{{C.zero(),new C(b,0)},{new C(b,0),C.zero()}});
    case"PHASE_FLIP","DEPHASE"->List.of(new C[][]{{new C(a,0),C.zero()},{C.zero(),new C(a,0)}},new C[][]{{new C(b,0),C.zero()},{C.zero(),new C(-b,0)}});
    case"AMPLITUDE_DAMP"->List.of(new C[][]{{C.one(),C.zero()},{C.zero(),new C(a,0)}},new C[][]{{C.zero(),new C(b,0)},{C.zero(),C.zero()}});
    case"PHASE_DAMP"->List.of(new C[][]{{C.one(),C.zero()},{C.zero(),new C(a,0)}},new C[][]{{C.zero(),C.zero()},{C.zero(),new C(b,0)}});
    case"DEPOLARIZE"->{double q=Math.sqrt(Math.max(0,p/3.0)),i=Math.sqrt(Math.max(0,1-p));yield List.of(gate("I",0).clone(), scaleMat(gate("X",0),q), scaleMat(gate("Y",0),q), scaleMat(gate("Z",0),q), scaleMat(gate("I",0),0));}
    default->throw new X("SIM: noise "+op);};}
  static C[][] scaleMat(C[][]m,double a){C[][]o=zeros(m.length,m[0].length);for(int i=0;i<m.length;i++)for(int j=0;j<m[0].length;j++)o[i][j]=m[i][j].scale(a);return o;}

  static int measureZ(Result s,int q,Random rng){int bit=1<<q;double p1=0;if(!s.density){for(int i=0;i<s.psi.length;i++)if((i&bit)!=0)p1+=s.psi[i].abs2();}else{for(int i=0;i<s.rho.length;i++)if((i&bit)!=0)p1+=s.rho[i][i].r;}int out=rng.nextDouble()<p1?1:0;double prob=out==1?p1:1-p1;if(prob<TOL)out=1-out;prob=out==1?p1:1-p1;if(!s.density){double z=Math.sqrt(Math.max(prob,TOL));for(int i=0;i<s.psi.length;i++){if((((i&bit)!=0)?1:0)!=out)s.psi[i]=C.zero();else s.psi[i]=s.psi[i].scale(1/z);}}else{for(int i=0;i<s.rho.length;i++)for(int j=0;j<s.rho.length;j++){int bi=((i&bit)!=0)?1:0,bj=((j&bit)!=0)?1:0;if(bi!=out||bj!=out)s.rho[i][j]=C.zero();else s.rho[i][j]=s.rho[i][j].scale(1/Math.max(prob,TOL));}}return out;}
  static void applyBasis(Result s,Program p,int q,String op,boolean forward){String g=null;if(op.equals("MEASURE_X"))g="H";if(op.equals("MEASURE_Y")){if(forward){applyGateResult(s,p,q,"SDG",0);applyGateResult(s,p,q,"H",0);}else{applyGateResult(s,p,q,"H",0);applyGateResult(s,p,q,"S",0);}return;}if(g!=null)applyGateResult(s,p,q,g,0);}
  static void applyGateResult(Result s,Program p,int q,String op,double a){C[][]u=gate(op,a);if(!s.density)apply1(s.psi,q,u);else s.rho=leftRight(s.rho,embed1(p.nqubits,q,u));}
  static double expectPauli(Result s,Program p,String expr){C[]v=s.density?null:cloneVec(s.psi);C[][]rho=s.density?s.rho:null;List<String> terms=Arrays.asList(expr.split("\\*"));if(!s.density){for(String t:terms){String[]a=t.trim().split("@");String op=a[0];if(!Set.of("X","Y","Z","I").contains(op))throw new X("EXPECT: Pauli only");if(!op.equals("I"))apply1(v,p.qidx.get(a[1]),gate(op,0));}C z=C.zero();for(int i=0;i<v.length;i++)z=z.add(s.psi[i].conj().mul(v[i]));return z.r;}else{C[][]U=eye(rho.length);for(String t:terms){String[]a=t.trim().split("@");if(!a[0].equals("I")){C[][]e=embed1(p.nqubits,p.qidx.get(a[1]),gate(a[0],0));U=mul(e,U);}}C tr=C.zero();C[][]m=mul(rho,U);for(int i=0;i<m.length;i++)tr=tr.add(m[i][i]);return tr.r;}}

  static Result run(Unit u,long seed){Program p=validate(u);if(p.nqubits>12)throw new X("RESOURCE: statevector cap 12 qubits");Result s=new Result();s.psi=new C[1<<p.nqubits];for(int i=0;i<s.psi.length;i++)s.psi[i]=C.zero();s.psi[0]=C.one();s.cbits=new int[p.ncbits];Random rng=new Random(seed);
    for(Row r:p.exec){s.steps++;String op=r.op();if(UNARY.contains(op)||BINARY.contains(op)||op.equals("CCX")){if(!s.density)applyExecVec(s.psi,p,r);else{s.rho=leftRight(s.rho,fullU(p,r));}}
      else if(op.equals("PREP0")||op.equals("RESET")){int q=p.qidx.get(r.a());int m=measureZ(s,q,rng);if(m==1)applyGateResult(s,p,q,"X",0);}
      else if(op.equals("PREP1")){int q=p.qidx.get(r.a());int m=measureZ(s,q,rng);if(m==0)applyGateResult(s,p,q,"X",0);}
      else if(op.equals("PREP_PLUS")){int q=p.qidx.get(r.a());int m=measureZ(s,q,rng);if(m==1)applyGateResult(s,p,q,"X",0);applyGateResult(s,p,q,"H",0);}
      else if(op.equals("PREP_MINUS")){int q=p.qidx.get(r.a());int m=measureZ(s,q,rng);if(m==1)applyGateResult(s,p,q,"X",0);applyGateResult(s,p,q,"H",0);applyGateResult(s,p,q,"Z",0);}
      else if(op.startsWith("MEASURE_")){int q=p.qidx.get(r.a());applyBasis(s,p,q,op,true);int m=measureZ(s,q,rng);applyBasis(s,p,q,op,false);s.cbits[p.cidx.get(r.out())]=m;}
      else if(NOISE.contains(op)){if(!s.density){s.rho=pureDensity(s.psi);s.psi=null;s.density=true;}double v=num(r.param(),"noise");List<C[][]> ks;if(op.equals("DEPOLARIZE")){double q=Math.sqrt(v/3.0),a=Math.sqrt(Math.max(0,1-v));ks=List.of(scaleMat(gate("I",0),a),scaleMat(gate("X",0),q),scaleMat(gate("Y",0),q),scaleMat(gate("Z",0),q));}else ks=kraus(op,v);s.rho=channel1(s.rho,p.nqubits,p.qidx.get(r.a()),ks);}
      else if(op.equals("CLASSICAL_IF")){int c=s.cbits[p.cidx.get(r.ctrl())],want=Integer.parseInt(r.a());if(c==want)applyGateResult(s,p,p.qidx.get(r.out()),r.b(),0);}
      else if(op.equals("EXPECT")){s.expectations.put(r.id(),expectPauli(s,p,r.param()));}
    }return s;}

  static String bitString(Result r){StringBuilder b=new StringBuilder();for(int i=r.cbits.length-1;i>=0;i--)b.append(r.cbits[i]);return b.toString();}
  static String evidenceMeta(String cls,String realization){return "\"language\":\"LCTL/1.1\",\"execution_class\":\""+cls+"\",\"state_realization\":\""+realization+"\",\"host_authority\":\"CLASSICAL_REFERENCE_HOST\",\"storage_authority\":\"CLASSICAL_STORAGE_AGNOSTIC\",\"physical_qpu\":false,\"quantum_boundary\":\"NOT_CROSSED\"";}
  static String provenance(Unit u,String cls,String realization){return "{"+evidenceMeta(cls,realization)+",\"source_sha256\":\""+u.computed+"\",\"target\":\""+u.b[6]+"\",\"adapter\":\"none-qualified-physical\",\"evidence_status\":\"REFERENCE_ONLY\"}";}
  static Map<String,Integer> sample(Unit u,int shots,long seed){Map<String,Integer>h=new TreeMap<>();for(int i=0;i<shots;i++){Result r=run(u,seed+i*1000003L);h.put(bitString(r),h.getOrDefault(bitString(r),0)+1);}return h;}
  static String stateJson(Result r){StringBuilder s=new StringBuilder();String realization=r.density?"CLASSICAL_NUMERIC_DENSITY":"CLASSICAL_NUMERIC_STATEVECTOR";s.append("{").append(evidenceMeta("CLASSICAL_SIMULATION",realization)).append(",\"representation\":\"").append(r.density?"density":"statevector").append("\",\"steps\":").append(r.steps).append(",\"cbits\":\"").append(bitString(r)).append("\",\"expectations\":{");int k=0;for(var e:r.expectations.entrySet()){if(k++>0)s.append(',');s.append('"').append(e.getKey()).append("\":").append(String.format(Locale.ROOT,"%.12g",e.getValue()));}s.append("},\"probabilities\":[");int d=r.density?r.rho.length:r.psi.length;for(int i=0;i<d;i++){if(i>0)s.append(',');double p=r.density?r.rho[i][i].r:r.psi[i].abs2();s.append(String.format(Locale.ROOT,"%.12g",Math.max(0,p)));}s.append("]}");return s.toString();}

  static Map<String,Object> resources(Unit u){Program p=validate(u);Map<String,Integer> counts=new TreeMap<>();Map<String,Integer> depthByQ=new HashMap<>();int depth=0,meas=0,two=0,nonCliff=0;for(Row r:p.exec){String op=r.op();counts.put(op,counts.getOrDefault(op,0)+1);List<String> qs=new ArrayList<>();if(UNARY.contains(op)||NOISE.contains(op)||op.startsWith("MEASURE_")||op.startsWith("PREP")||op.equals("RESET"))qs.add(r.a());else if(BINARY.contains(op)){qs.add(r.ctrl());qs.add(r.a());two++;}else if(op.equals("CCX")){qs.add(r.ctrl());qs.add(r.a());qs.add(r.b());two++;}else if(op.equals("CLASSICAL_IF"))qs.add(r.out());int d=0;for(String q:qs)d=Math.max(d,depthByQ.getOrDefault(q,0));d++;for(String q:qs)depthByQ.put(q,d);depth=Math.max(depth,d);if(op.startsWith("MEASURE_"))meas++;if(Set.of("T","TDG","RX","RY","RZ","PHASE","CPHASE","CCX").contains(op))nonCliff++;}Map<String,Object>o=new LinkedHashMap<>();o.put("language","LCTL/1.2");o.put("execution_class","HARDWARE_ESTIMATE");o.put("state_realization","SYMBOLIC_QUANTUM_OBJECT");o.put("host_authority","CLASSICAL_REFERENCE_HOST");o.put("storage_authority","CLASSICAL_STORAGE_AGNOSTIC");o.put("physical_qpu",Boolean.FALSE);o.put("quantum_boundary","NOT_CROSSED");o.put("logical_qubits",p.nqubits);o.put("classical_bits",p.ncbits);o.put("gates",p.exec.size());o.put("two_qubit_or_more",two);o.put("non_clifford",nonCliff);o.put("measurements",meas);o.put("depth",depth);o.put("approx_error_bound",p.approxError);o.put("counts",counts);return o;}
  static String jsonObj(Map<String,Object>m){StringBuilder s=new StringBuilder("{");int k=0;for(var e:m.entrySet()){if(k++>0)s.append(',');s.append('"').append(e.getKey()).append("\":");Object v=e.getValue();if(v instanceof Number||v instanceof Boolean)s.append(v);else if(v instanceof Map<?,?> mm){s.append('{');int j=0;for(var z:mm.entrySet()){if(j++>0)s.append(',');s.append('"').append(z.getKey()).append("\":").append(z.getValue());}s.append('}');}else s.append('"').append(v).append('"');}return s.append('}').toString();}

  static String qcir(Unit u){Program p=validate(u);StringBuilder s=new StringBuilder();s.append("{\"qcir\":\"QCIR/1.0\",\"source_language\":\"LCTL/1.2\",\"execution_class\":\"QUANTUM_COMPILATION\",\"physical_qpu\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"source_sha256\":\"").append(u.computed).append("\",\"qubits\":").append(p.nqubits).append(",\"cbits\":").append(p.ncbits).append(",\"ops\":[");int k=0;for(Row r:p.exec){if(k++>0)s.append(',');s.append("{\"id\":\"").append(r.id()).append("\",\"lane\":\"").append(r.lane()).append("\",\"op\":\"").append(r.op()).append("\",\"out\":\"").append(r.out()).append("\",\"ctrl\":\"").append(r.ctrl()).append("\",\"a\":\"").append(r.a()).append("\",\"b\":\"").append(r.b()).append("\",\"param\":\"").append(r.param()).append("\",\"regime\":\"").append(r.regime()).append("\",\"proof\":\"").append(r.proof()).append("\"}");}return s.append("]}").toString();}

  static List<Row> optimized(Program p){List<Row>o=new ArrayList<>();for(Row r:p.exec){if(!o.isEmpty()){Row z=o.get(o.size()-1);if(inversePair(z,r)){o.remove(o.size()-1);continue;}}o.add(r);}return o;}
  static boolean inversePair(Row a,Row b){if(!a.a().equals(b.a())||!a.ctrl().equals(b.ctrl()))return false;String x=a.op()+":"+b.op();return Set.of("H:H","X:X","Y:Y","Z:Z","CX:CX","CNOT:CNOT","CZ:CZ","SWAP:SWAP","S:SDG","SDG:S","T:TDG","TDG:T").contains(x);}
  static double optCheck(Unit u){Program p=validate(u);if(p.exec.stream().anyMatch(r->r.op().startsWith("MEASURE_")||NOISE.contains(r.op())||r.op().equals("CLASSICAL_IF")))throw new X("OPT: equivalence check requires unitary-only source");Result a=run(u,7);List<Row>save=new ArrayList<>(p.exec),opt=optimized(p);C[]v=new C[1<<p.nqubits];for(int i=0;i<v.length;i++)v[i]=C.zero();v[0]=C.one();for(Row r:opt)if(UNARY.contains(r.op())||BINARY.contains(r.op())||r.op().equals("CCX"))applyExecVec(v,p,r);double d=0;for(int i=0;i<v.length;i++){C z=v[i].sub(a.psi[i]);d=Math.max(d,Math.sqrt(z.abs2()));}return d;}

  static String qasm(Unit u){Program p=validate(u);StringBuilder s=new StringBuilder("OPENQASM 3.0;\ninclude \"stdgates.inc\";\nqubit[").append(p.nqubits).append("] q;\nbit[").append(p.ncbits).append("] c;\n");for(Row r:p.exec){String op=r.op();if(UNARY.contains(op)){int q=p.qidx.get(r.a());String g=op.toLowerCase(Locale.ROOT);if(op.equals("SDG"))g="sdg";if(op.equals("TDG"))g="tdg";if(Set.of("RX","RY","RZ").contains(op))s.append(g).append('(').append(r.param()).append(") q[").append(q).append("];\n");else if(op.equals("PHASE"))s.append("p(").append(r.param()).append(") q[").append(q).append("];\n");else s.append(g).append(" q[").append(q).append("];\n");}
      else if(Set.of("CX","CNOT","CZ","SWAP").contains(op)){String g=op.equals("CNOT")?"cx":op.toLowerCase(Locale.ROOT);s.append(g).append(" q[").append(p.qidx.get(r.ctrl())).append("], q[").append(p.qidx.get(r.a())).append("];\n");}
      else if(op.startsWith("MEASURE_Z")){s.append("c[").append(p.cidx.get(r.out())).append("] = measure q[").append(p.qidx.get(r.a())).append("];\n");}
      else if(op.equals("BARRIER")||op.equals("SYNC")||op.equals("FENCE"))s.append("barrier q; // ").append(op).append("\n");
      else if(op.startsWith("PREP")||op.equals("RESET"))s.append("reset q[").append(p.qidx.get(r.a())).append("];\n");
      else if(op.equals("EXPECT")||NOISE.contains(op)||op.equals("CLASSICAL_IF")||op.equals("SAMPLE")||op.equals("CCX")||Set.of("CY","CH","ISWAP","CPHASE").contains(op))s.append("// LCTL target adapter: ").append(op).append(" requires lowering or target-specific handling; proof=").append(r.proof()).append("\n");}
    return s.toString();}


  static Set<String> rowResources(Row r){Set<String>s=new LinkedHashSet<>();String op=r.op();if(UNARY.contains(op)||NOISE.contains(op)||op.startsWith("MEASURE_")||op.startsWith("PREP")||op.equals("RESET")){if(!blank(r.a()))s.add("q:"+r.a());}else if(BINARY.contains(op)){if(!blank(r.ctrl()))s.add("q:"+r.ctrl());if(!blank(r.a()))s.add("q:"+r.a());}else if(op.equals("CCX")){if(!blank(r.ctrl()))s.add("q:"+r.ctrl());if(!blank(r.a()))s.add("q:"+r.a());if(!blank(r.b()))s.add("q:"+r.b());}else if(op.equals("CLASSICAL_IF")){if(!blank(r.ctrl()))s.add("c:"+r.ctrl());if(!blank(r.out()))s.add("q:"+r.out());}else if(op.equals("SAMPLE")){for(String q:splitQubits(r.a()))s.add("q:"+q);}else if(op.equals("EXPECT")){for(String q:pauliQubits(r.param()))s.add("q:"+q);}return s;}
  static boolean intersects(Set<String>a,Set<String>b){for(String x:a)if(b.contains(x))return true;return false;}
  static String parallelPlan(Unit u){Program p=validate(u);int work=0,span=0,barriers=0,parallelMeta=0,coupling=0,fields=0,comm=0,sync=0;Set<String>lanes=new TreeSet<>();List<String> layerDesc=new ArrayList<>();for(Row r:u.ordered){if(r.face().equals("PARALLEL")){parallelMeta++;if(Set.of("COMM_MODEL","COLLECTIVE","OVERLAP").contains(r.op()))comm++;}else if(r.face().equals("COUPLING"))coupling++;else if(r.face().equals("FIELD"))fields++;}
    for(Frame f:u.frames){List<Row> rows=new ArrayList<>();for(Row r:f.rows)if(r.face().equals("CODE"))rows.add(r);List<Row> current=new ArrayList<>();Set<String> used=new LinkedHashSet<>();for(Row r:rows){if(Set.of("BARRIER","SYNC","FENCE").contains(r.op())){if(!current.isEmpty()){span++;layerDesc.add(layerJson(f.id(),span,current));current.clear();used.clear();}barriers++;sync++;continue;}if(r.op().equals("NOP"))continue;if(!blank(r.lane()))lanes.add(r.lane());work++;Set<String>rr=rowResources(r);if(intersects(used,rr)){span++;layerDesc.add(layerJson(f.id(),span,current));current.clear();used.clear();}current.add(r);used.addAll(rr);}if(!current.isEmpty()){span++;layerDesc.add(layerJson(f.id(),span,current));}}
    double par=span==0?0.0:((double)work/span);StringBuilder z=new StringBuilder();z.append("{\"language\":\"LCTL/1.2\",\"profile\":\"").append(u.b[3]).append("\",\"execution_class\":\"QUANTUM_COMPILATION\",\"logical_parallel\":").append(work>1).append(",\"scheduled_parallel\":").append(par>1.0).append(",\"physical_parallel\":false,\"physical_qpu\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"work\":").append(work).append(",\"span\":").append(span).append(",\"max_exploitable_parallelism\":").append(String.format(Locale.ROOT,"%.6f",par)).append(",\"lanes\":").append(lanes.size()).append(",\"barriers\":").append(barriers).append(",\"synchronization_markers\":").append(sync).append(",\"parallel_metadata_rows\":").append(parallelMeta).append(",\"communication_metadata_rows\":").append(comm).append(",\"coupling_rows\":").append(coupling).append(",\"field_rows\":").append(fields).append(",\"layers\":[");for(int i=0;i<layerDesc.size();i++){if(i>0)z.append(',');z.append(layerDesc.get(i));}return z.append("]}").toString();}
  static String layerJson(String frame,int layer,List<Row> rows){StringBuilder s=new StringBuilder("{\"layer\":").append(layer).append(",\"frame\":\"").append(frame).append("\",\"ops\":[");for(int i=0;i<rows.size();i++){Row r=rows.get(i);if(i>0)s.append(',');s.append("{\"id\":\"").append(r.id()).append("\",\"lane\":\"").append(r.lane()).append("\",\"op\":\"").append(r.op()).append("\"}");}return s.append("]}").toString();}


  static List<Row> faceRows(Unit u,String face){List<Row>z=new ArrayList<>();for(Row r:u.ordered)if(r.face().equals(face))z.add(r);return z;}
  static String causalDag(Unit u){
    validate(u);List<String>nodes=new ArrayList<>(),edges=new ArrayList<>();Map<String,String>last=new LinkedHashMap<>();String barrier=null;
    for(Row r:u.ordered)if(r.face().equals("CODE")&&!r.op().equals("NOP")){
      nodes.add("{\"id\":\""+esc(r.id())+"\",\"op\":\""+esc(r.op())+"\",\"lane\":\""+esc(r.lane())+"\",\"resources\":\""+esc(String.join(",",rowResources(r)))+"\"}");
      if(barrier!=null&&!barrier.equals(r.id()))edges.add("{\"from\":\""+esc(barrier)+"\",\"to\":\""+esc(r.id())+"\",\"reason\":\"BARRIER\"}");
      if(Set.of("BARRIER","SYNC","FENCE").contains(r.op())){Set<String>pred=new LinkedHashSet<>(last.values());for(String x:pred)if(!x.equals(r.id()))edges.add("{\"from\":\""+esc(x)+"\",\"to\":\""+esc(r.id())+"\",\"reason\":\"BARRIER\"}");barrier=r.id();continue;}
      for(String res:rowResources(r)){String prev=last.get(res);if(prev!=null&&!prev.equals(r.id()))edges.add("{\"from\":\""+esc(prev)+"\",\"to\":\""+esc(r.id())+"\",\"reason\":\""+(res.startsWith("q:")?"QUANTUM_OWNERSHIP":"DATA_DEPENDENCY")+"\",\"resource\":\""+esc(res)+"\"}");last.put(res,r.id());}
    }
    for(Row r:u.ordered)if(r.face().equals("PARALLEL")&&r.op().equals("DEPENDENCY"))for(String e:r.param().split(";")){String[]a=e.trim().split("->");if(a.length==2)edges.add("{\"from\":\""+esc(a[0].trim())+"\",\"to\":\""+esc(a[1].trim())+"\",\"reason\":\"USER_ORDER\"}");}
    for(Row r:u.ordered)if(r.face().equals("DISTRIBUTED")&&r.op().equals("MEASUREMENT_DEPENDENCY")){Map<String,String>m=kv(r.param());String a=m.get("from"),b=m.get("to");if(a!=null&&b!=null)edges.add("{\"from\":\""+esc(a)+"\",\"to\":\""+esc(b)+"\",\"reason\":\"MEASUREMENT_DEPENDENCY\"}");}
    return "{\"dag\":\"LCTL-CAUSAL-DAG/1.0\",\"source_hash\":\""+u.computed+"\",\"node_count\":"+nodes.size()+",\"edge_count\":"+edges.size()+",\"nodes\":["+String.join(",",nodes)+"],\"edges\":["+String.join(",",edges)+"]}";
  }
  static String esc(String x){return x.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n");}
  static String topologyCanonical(Unit u){List<String>a=new ArrayList<>();for(Row r:u.ordered)if(r.face().equals("TOPOLOGY")||r.face().equals("DISTRIBUTED")&&Set.of("NODE","CLUSTER","ROUTE","CLASSICAL_CHANNEL","QUANTUM_LINK","PLACEMENT","PARTITION").contains(r.op()))a.add(r.face()+"|"+r.op()+"|"+r.param()+"|"+r.proof());Collections.sort(a);return String.join("\n",a)+"\n";}
  static String scheduleCanonical(Unit u){return parallelPlan(u)+"\n";}
  static String topologyCheck(Unit u){validate(u);int nodes=0,links=0,qlinks=0,clinks=0,qpus=0,numa=0;Set<String>ids=new TreeSet<>();for(Row r:u.ordered)if(r.face().equals("TOPOLOGY")){Map<String,String>m=kv(r.param());switch(r.op()){case"NODE"->{nodes++;ids.add(m.getOrDefault("id",r.out()));}case"LINK"->{links++;if("quantum".equals(m.get("type")))qlinks++;else if("classical".equals(m.get("type")))clinks++;}case"QPU"->qpus++;case"NUMA_DOMAIN"->numa++;default->{}}}String c=topologyCanonical(u);return "{\"language\":\"LCTL/1.2\",\"profile\":\""+u.b[3]+"\",\"status\":\"PASS\",\"execution_class\":\"QUANTUM_COMPILATION\",\"physical_qpu\":false,\"physical_distributed\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"nodes\":"+nodes+",\"links\":"+links+",\"classical_links\":"+clinks+",\"quantum_links\":"+qlinks+",\"qpu_descriptors\":"+qpus+",\"numa_domains\":"+numa+",\"topology_hash\":\""+sha(c)+"\",\"node_ids\":\""+esc(String.join(",",ids))+"\"}";}
  static String distributedPlan(Unit u){
    validate(u);int nodes=0,parts=0,placements=0,routes=0,messages=0,qlinks=0,clinks=0,protocols=0,ebits=0,remote=0,failures=0,memory=0,events=0,futures=0,codeWork=0;double bytes=0;Set<String>nodeIds=new TreeSet<>();List<String>protocolIds=new ArrayList<>();
    for(Row r:u.ordered){
      Map<String,String>m=kv(r.param());
      if(r.face().equals("TOPOLOGY")){if(r.op().equals("NODE")){nodes++;nodeIds.add(m.getOrDefault("id",r.out()));}if(r.op().equals("LINK")){if("quantum".equals(m.get("type")))qlinks++;else if("classical".equals(m.get("type")))clinks++;}}
      if(r.face().equals("DISTRIBUTED")){switch(r.op()){case"NODE"->{nodes++;nodeIds.add(m.getOrDefault("id",r.out()));}case"PARTITION","SHARD"->parts++;case"PLACEMENT"->placements++;case"ROUTE"->routes++;case"MESSAGE"->{messages++;bytes+=dval(m,"bytes",0,"distributed plan");}case"QUANTUM_LINK"->qlinks++;case"CLASSICAL_CHANNEL"->clinks++;case"EVENT","EPOCH"->events++;case"FUTURE"->futures++;default->{}}}
      if(r.face().equals("PROTOCOL")){protocols++;protocolIds.add(r.op());if(r.op().equals("EPR_RESERVE"))ebits+=ival(m,"count",0,"distributed plan");if(r.op().equals("REMOTE_CNOT")||r.op().equals("REMOTE_CONTROL"))remote++;}
      if(r.face().equals("FAILURE"))failures++;
      if(r.face().equals("MEMORY"))memory++;
      if(r.face().equals("CODE")&&!r.op().equals("NOP"))codeWork++;
    }
    boolean logical=nodes>1||parts>1||routes>0||protocols>0;String dstate=logical?"DISTRIBUTED_SCHEDULED":"LOCAL_ONLY";String pstate=codeWork>1?"SCHEDULED_PARALLEL":"SERIAL";String topHash=sha(topologyCanonical(u));String schedHash=sha(scheduleCanonical(u));String qc=sha(qcirP(u)+"\n");
    return "{\"language\":\"LCTL/1.2\",\"profile\":\"lctl.quantum.parallel.distributed\",\"execution_class\":\"QUANTUM_COMPILATION\",\"parallel_state\":\""+pstate+"\",\"distributed_state\":\""+dstate+"\",\"quantum_boundary\":\"NOT_CROSSED\",\"physical_qpu\":false,\"physical_parallel\":false,\"physical_distributed\":false,\"source_hash\":\""+u.computed+"\",\"topology_hash\":\""+topHash+"\",\"schedule_hash\":\""+schedHash+"\",\"qcir_p_hash\":\""+qc+"\",\"nodes\":"+nodes+",\"partitions\":"+parts+",\"placements\":"+placements+",\"routes\":"+routes+",\"messages\":"+messages+",\"classical_bytes\":"+String.format(Locale.ROOT,"%.3f",bytes)+",\"classical_links\":"+clinks+",\"quantum_links\":"+qlinks+",\"protocol_ops\":"+protocols+",\"ebits_reserved\":"+ebits+",\"remote_gate_ops\":"+remote+",\"failure_rows\":"+failures+",\"memory_rows\":"+memory+",\"events\":"+events+",\"futures\":"+futures+",\"node_ids\":\""+esc(String.join(",",nodeIds))+"\",\"protocols\":\""+esc(String.join(",",protocolIds))+"\",\"parallel_plan\":"+parallelPlan(u)+"}";
  }
  static String qcirP(Unit u){
    Program p=validate(u);StringBuilder s=new StringBuilder();s.append("{\"qcir\":\"QCIR-P/1.0\",\"source_language\":\"LCTL/1.2\",\"profile\":\"lctl.quantum.parallel.distributed\",\"execution_class\":\"QUANTUM_COMPILATION\",\"physical_qpu\":false,\"physical_distributed\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"source_sha256\":\"").append(u.computed).append("\",\"records\":[");int k=0;
    for(Row r:u.ordered){if(Set.of("DECL","CODE","PARALLEL","COUPLING","FIELD","DISTRIBUTED","TOPOLOGY","PROTOCOL","FAILURE","MEMORY").contains(r.face())){if(k++>0)s.append(',');s.append("{\"id\":\"").append(esc(r.id())).append("\",\"face\":\"").append(r.face()).append("\",\"lane\":\"").append(esc(r.lane())).append("\",\"op\":\"").append(esc(r.op())).append("\",\"out\":\"").append(esc(r.out())).append("\",\"ctrl\":\"").append(esc(r.ctrl())).append("\",\"a\":\"").append(esc(r.a())).append("\",\"b\":\"").append(esc(r.b())).append("\",\"param\":\"").append(esc(r.param())).append("\",\"regime\":\"").append(esc(r.regime())).append("\",\"proof\":\"").append(esc(r.proof())).append("\"}");}}
    return s.append("],\"causal_dag\":").append(causalDag(u)).append(",\"topology_hash\":\"").append(sha(topologyCanonical(u))).append("\",\"schedule_hash\":\"").append(sha(scheduleCanonical(u))).append("\"}").toString();
  }
  static String emulateDistributed(Unit u,long seed){
    validate(u);Random rng=new Random(seed);double alpha=0,beta=0,total=0,qtime=0;int messages=0,linkAttempts=0,ebits=0,protocols=0;for(Row r:u.ordered){Map<String,String>m=kv(r.param());if(r.face().equals("PARALLEL")&&r.op().equals("COMM_MODEL")){alpha=dval(m,"alpha",alpha,"comm model");beta=dval(m,"beta",beta,"comm model");}if(r.face().equals("DISTRIBUTED")&&r.op().equals("MESSAGE")){double n=dval(m,"bytes",0,"message");double lat=dval(m,"latency",alpha+beta*n,"message");total+=lat;messages++;}if(r.face().equals("TOPOLOGY")&&r.op().equals("LINK")&&"quantum".equals(m.get("type"))){double setup=dval(m,"setup",0,"qlink"),herald=dval(m,"herald",0,"qlink"),success=dval(m,"success",1,"qlink");if(success<=0||success>1)throw new X("EMULATOR: qlink success probability must be (0,1]");int attempts=1;while(rng.nextDouble()>success&&attempts<100000)attempts++;linkAttempts+=attempts;qtime+=(setup+herald)*attempts;}if(r.face().equals("PROTOCOL")){protocols++;if(r.op().equals("EPR_RESERVE"))ebits+=ival(m,"count",0,"protocol");}}
    return "{\"language\":\"LCTL/1.2\",\"execution_class\":\"DISTRIBUTED_CLASSICAL_EMULATION\",\"state_realization\":\"CLASSICAL_DISTRIBUTED_MODEL\",\"parallel_state\":\"PARALLEL_EMULATION\",\"distributed_state\":\"DISTRIBUTED_CLASSICAL_EMULATION\",\"physical_qpu\":false,\"physical_parallel\":false,\"physical_distributed\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"seed\":"+seed+",\"messages\":"+messages+",\"classical_communication_time_model\":"+String.format(Locale.ROOT,"%.9f",total)+",\"quantum_link_attempts_emulated\":"+linkAttempts+",\"quantum_link_time_model\":"+String.format(Locale.ROOT,"%.9f",qtime)+",\"ebits_modelled\":"+ebits+",\"protocol_ops_modelled\":"+protocols+",\"warning\":\"This is classical emulation of distributed semantics, not physical distributed quantum execution.\"}";
  }
  static String distributedResources(Unit u){Map<String,Object>base=resources(u);int messages=0,barriers=0,remote=0,ebits=0,qlinks=0,swaps=0,failures=0;double bytes=0;for(Row r:u.ordered){Map<String,String>m=kv(r.param());if(r.face().equals("DISTRIBUTED")&&r.op().equals("MESSAGE")){messages++;bytes+=dval(m,"bytes",0,"resource");}if(r.face().equals("CODE")&&Set.of("BARRIER","SYNC","FENCE").contains(r.op()))barriers++;if(r.face().equals("PROTOCOL")){if(Set.of("REMOTE_CNOT","REMOTE_CONTROL").contains(r.op()))remote++;if(r.op().equals("EPR_RESERVE"))ebits+=ival(m,"count",0,"resource");if(r.op().equals("ENTANGLEMENT_SWAP"))swaps++;}if(r.face().equals("TOPOLOGY")&&r.op().equals("LINK")&&"quantum".equals(m.get("type")))qlinks++;if(r.face().equals("FAILURE"))failures++;}base.put("profile","lctl.quantum.parallel.distributed");base.put("messages",messages);base.put("bytes_moved",bytes);base.put("barriers",barriers);base.put("remote_gate_count",remote);base.put("ebits_reserved",ebits);base.put("entanglement_swaps",swaps);base.put("quantum_links",qlinks);base.put("failure_model_rows",failures);return jsonObj(base);}
  static String protocolCheck(Unit u){validate(u);int epr=0,tp=0,remote=0,swap=0,feedback=0,other=0;for(Row r:u.ordered)if(r.face().equals("PROTOCOL"))switch(r.op()){case"EPR_RESERVE"->epr++;case"TELEPORT"->tp++;case"REMOTE_CNOT","REMOTE_CONTROL"->remote++;case"ENTANGLEMENT_SWAP"->swap++;case"CLASSICAL_FEEDBACK"->feedback++;default->other++;}return "{\"status\":\"PASS\",\"execution_class\":\"SYMBOLIC\",\"physical_qpu\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"epr_reservations\":"+epr+",\"teleports\":"+tp+",\"remote_gates\":"+remote+",\"entanglement_swaps\":"+swap+",\"classical_feedback\":"+feedback+",\"other_protocol_ops\":"+other+"}";}
  static String failureReport(Unit u){validate(u);int n=0;Set<String>kinds=new TreeSet<>();for(Row r:u.ordered)if(r.face().equals("FAILURE")){n++;kinds.add(r.op());}return "{\"status\":\"PASS\",\"execution_class\":\"SYMBOLIC\",\"failure_rows\":"+n+",\"failure_classes\":\""+esc(String.join(",",kinds))+"\",\"quantum_recovery_policy\":\"NO_UNKNOWN_QSTATE_COPY\",\"physical_qpu\":false}";}

  static void usage(){System.out.println("LCTL 1.3 parallel/distributed reference runtime (1.2-compatible)\nverify FILE\ninspect FILE\nsimulate FILE [shots] [seed]\nresources FILE\ndistributed-resources FILE\nqcir FILE [OUT]\nqcir-p FILE [OUT]\nqasm FILE OUT\nopt-check FILE\nprovenance FILE\nparallel-plan FILE\ndistributed-plan FILE\ntopology-check FILE\nprotocol-check FILE\nemulate-distributed FILE [seed]\nfailure-report FILE\ncausal-dag FILE\nphysical-run FILE\nphysical-distributed-run FILE");}
  public static void main(String[]a){try{if(a.length<2){usage();System.exit(2);}Unit u=parse(Path.of(a[1]),true);switch(a[0]){
    case"verify"->System.out.println("PASS LCTL verify execution_class=SYMBOLIC physical_qpu=false quantum_boundary=NOT_CROSSED bundle="+u.b[1]+" frames="+u.frames.size()+" rows="+u.ordered.size()+" sha256="+u.computed);
    case"inspect"->{Program p=validate(u);System.out.println("{"+evidenceMeta("SYMBOLIC","SYMBOLIC_QUANTUM_OBJECT")+",\"status\":\"PASS\",\"bundle\":\""+u.b[1]+"\",\"qubits\":"+p.nqubits+",\"cbits\":"+p.ncbits+",\"exec_rows\":"+p.exec.size()+",\"approx_error\":"+p.approxError+"}");}
    case"simulate"->{int shots=a.length>=3?Integer.parseInt(a[2]):0;long seed=a.length>=4?Long.parseLong(a[3]):1;if(shots>0)System.out.println("{"+evidenceMeta("CLASSICAL_SIMULATION","CLASSICAL_NUMERIC_MEASUREMENT_SAMPLING")+",\"shots\":"+shots+",\"histogram\":"+jsonObj(new LinkedHashMap<>(sample(u,shots,seed)))+"}");else System.out.println(stateJson(run(u,seed)));}
    case"resources"->System.out.println(jsonObj(resources(u)));
    case"qcir"->{String z=qcir(u);if(a.length>=3){Files.writeString(Path.of(a[2]),z+"\n",StandardCharsets.UTF_8);System.out.println("PASS QCIR execution_class=QUANTUM_COMPILATION physical_qpu=false quantum_boundary=NOT_CROSSED sha256="+sha(z+"\n"));}else System.out.println(z);}
    case"qasm"->{if(a.length<3)throw new X("qasm needs OUT");Files.writeString(Path.of(a[2]),qasm(u),StandardCharsets.UTF_8);System.out.println("PASS QASM adapter execution_class=QUANTUM_COMPILATION physical_qpu=false quantum_boundary=NOT_CROSSED");}
    case"opt-check"->{double d=optCheck(u);if(d>1e-8)throw new X("OPT: equivalence failed max_diff="+d);System.out.println("PASS optimizer equivalence execution_class=QUANTUM_COMPILATION physical_qpu=false quantum_boundary=NOT_CROSSED max_diff="+d);}
    case"provenance"->System.out.println(provenance(u,"SYMBOLIC","SYMBOLIC_QUANTUM_OBJECT"));
    case"parallel-plan"->System.out.println(parallelPlan(u));
    case"distributed-plan"->System.out.println(distributedPlan(u));
    case"topology-check"->System.out.println(topologyCheck(u));
    case"protocol-check"->System.out.println(protocolCheck(u));
    case"emulate-distributed"->{long seed=a.length>=3?Long.parseLong(a[2]):1;System.out.println(emulateDistributed(u,seed));}
    case"failure-report"->System.out.println(failureReport(u));
    case"causal-dag"->System.out.println(causalDag(u));
    case"distributed-resources"->System.out.println(distributedResources(u));
    case"qcir-p"->{String z=qcirP(u);if(a.length>=3){Files.writeString(Path.of(a[2]),z+"\n",StandardCharsets.UTF_8);System.out.println("PASS QCIR-P execution_class=QUANTUM_COMPILATION physical_qpu=false physical_distributed=false quantum_boundary=NOT_CROSSED sha256="+sha(z+"\n"));}else System.out.println(z);}
    case"physical-run"->throw new X("PHYSICAL_QPU_EXECUTION_BLOCKED: no qualified physical-QPU adapter; ordinary SSD/CPU execution cannot be labeled physical quantum execution");
    case"physical-distributed-run"->throw new X("PHYSICAL_DISTRIBUTED_EXECUTION_BLOCKED: no authenticated multi-QPU endpoints, qualified quantum interconnect, calibration, timing, or execution receipts are bundled");
    default->{usage();System.exit(2);}}
  }catch(Exception e){System.err.println("FAIL: "+e.getMessage());System.exit(1);}}
}
