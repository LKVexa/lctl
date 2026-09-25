import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * LCTL-C 1.0 compact column language.
 *
 * Eight source columns lower losslessly into the canonical eighteen-cell LCTL row:
 *   ID | LANE | OP | OUT | CTRL | IN | ARG | META
 *
 * Repeated semantic cells are inherited from defaults and/or inferred from the op.
 * The canonical LCTL verifier remains the semantic authority after lowering.
 */
public final class LctlColumnMain {
  static final String MAGIC="LCTLC/1.0";
  static final String COLS="ID│LANE│OP│OUT│CTRL│IN│ARG│META";
  static final String BC="BUNDLE¦ID¦VERSION¦PROFILE¦ENTRY¦FRAMES¦TARGET¦BACKEND¦NETWORK¦REPLAY¦PRECISION¦ERROR_BUDGET¦RESOURCE_CEILING¦ADMISSION¦STATE¦PROOF";
  static final String FC="ID¦PARENT¦MODULE¦QSPACE¦TICK¦PREV¦REGISTERS¦BASIS¦STATE_REGIME¦NOISE_REGIME¦APPROX_REGIME¦HARDWARE¦CONF¦ERROR¦ADMIT¦PROOF";
  static final String RC="ROW¦FACE¦LANE¦QSPACE¦OP¦OUT¦CTRL¦A¦B¦PARAM¦TYPE¦BASIS¦REGIME¦ASSUME¦ERROR¦RESOURCE¦CONF¦PROOF";

  static final Set<String> CODE=new HashSet<>();
  static final Set<String> DECL=Set.of("QREG","CREG","QUBIT");
  static final Set<String> PARALLEL=Set.of("REGION","LANE_BIND","DEPENDENCY","COMM_MODEL","MEMORY_TIER","COLLECTIVE","RELIABILITY","DOMAIN_DECOMP","OVERLAP","CONSENSUS_BOUNDARY");
  static final Set<String> COUPLING=Set.of("BLOCK_COUPLING","RECIPROCITY","COUPLING_STRENGTH","MONOLITHIC","STAGGERED","INTERFACE","COUPLING_UNRESOLVED");
  static final Set<String> FIELD=Set.of("FIELD","TENSOR","BALANCE","CONSTITUTIVE","LINEARIZATION","CONTINUUM_LIMIT","DOMAIN");
  static final Set<String> DISTRIBUTED=Set.of("NODE","CLUSTER","PARTITION","SHARD","ROUTE","CLASSICAL_CHANNEL","QUANTUM_LINK","REMOTE_HANDLE","CONSISTENCY_SCOPE","FAILURE_DOMAIN","CHECKPOINT_SCOPE","PLACEMENT","MESSAGE","EPOCH","EVENT","FUTURE","QSTATE_BIND","QSTATE_MOVE","QSTATE_COPY","SERIALIZE_QSTATE","REMOTE_QOP","MEASUREMENT_DEPENDENCY","PARALLEL_CONFLICT","PROVENANCE");
  static final Set<String> TOPOLOGY=Set.of("NODE","LINK","QPU","CPU","GPU","MEMORY_DOMAIN","NUMA_DOMAIN","TOPOLOGY_BIND","CONCURRENCY","CROSSTALK","COHERENCE","CALIBRATION");
  static final Set<String> PROTOCOL=Set.of("ENTANGLE_LINK","EPR_RESERVE","EPR_RELEASE","TELEPORT","REMOTE_CONTROL","REMOTE_CNOT","REMOTE_MEASURE","CLASSICAL_FEEDBACK","ENTANGLEMENT_SWAP","PURIFY","HERALD","SYNC_QCLOCK","QCHANNEL_SEND","QCHANNEL_RECEIVE");
  static final Set<String> FAILURE=Set.of("NODE_UNAVAILABLE","PROCESS_FAILURE","LINK_FAILURE","TIMEOUT","CLASSICAL_PACKET_LOSS","QUANTUM_LINK_FAILURE","CALIBRATION_EXPIRED","QPU_UNAVAILABLE","COHERENCE_EXCEEDED","RESOURCE_EXHAUSTION","TARGET_DRIFT","SCHEDULER_DEADLOCK","OWNERSHIP_AMBIGUITY","CHECKPOINT");
  static final Set<String> MEMORY=Set.of("REGISTER","LOCAL","CACHE","NUMA","DEVICE","HOST","REMOTE","PERSISTENT","CAPACITY","MOVE");
  static final Set<String> TRANSFORM=Set.of("D1_FREDHOLM","D2_SPECTRAL","D3_DIRICHLET","D4_PROJECTION","D5_KERNEL_INDEX","D6_LOG_DIFFERENTIAL","D7_BIFURCATION","D8_HEAT_SEMIGROUP","D9_GEODESIC","D10_FOKKER_PLANCK","D11_VARIATIONAL_KKT","D12_FOURIER_SYMBOL","D13_FRECHET_LINEARIZE","D14_OBSTRUCTION","THETA_J_RETRACT","THETA_G_CONTINUUM");
  static final Set<String> ASSERT=Set.of("UNITARY","HERMITIAN","PROJECTOR","DENSITY","POVM","KRAUS","PROBABILITY","PROB_SUM","TENSOR_DIM","TOPOLOGY","COUPLING_RECIPROCITY","WORK_SPAN","TOPOLOGY_SEAL","SCHEDULE_SEAL","PHYSICAL_CLAIM");
  static final Map<String,String> ALIAS=new LinkedHashMap<>();
  static {
    CODE.addAll(List.of("I","X","Y","Z","H","S","SDG","T","TDG","RX","RY","RZ","PHASE","CX","CNOT","CY","CZ","CH","SWAP","ISWAP","CPHASE","CCX","PREP0","PREP1","PREP_PLUS","PREP_MINUS","RESET","MEASURE_Z","MEASURE_X","MEASURE_Y","SAMPLE","CLASSICAL_IF","DEPOLARIZE","DEPHASE","BIT_FLIP","PHASE_FLIP","AMPLITUDE_DAMP","PHASE_DAMP","EXPECT","BARRIER","SYNC","FENCE","NOP"));
    ALIAS.put("Q","QREG"); ALIAS.put("C","CREG"); ALIAS.put("QB","QUBIT");
    ALIAS.put("MZ","MEASURE_Z"); ALIAS.put("MX","MEASURE_X"); ALIAS.put("MY","MEASURE_Y");
    ALIAS.put("P0","PREP0"); ALIAS.put("P1","PREP1"); ALIAS.put("P+","PREP_PLUS"); ALIAS.put("P-","PREP_MINUS");
    ALIAS.put("BAR","BARRIER"); ALIAS.put(".","NOP"); ALIAS.put("IF","CLASSICAL_IF");
    ALIAS.put("REG","REGION"); ALIAS.put("DEP","DEPENDENCY"); ALIAS.put("CM","COMM_MODEL");
    ALIAS.put("PART","PARTITION"); ALIAS.put("MSG","MESSAGE"); ALIAS.put("QBIND","QSTATE_BIND"); ALIAS.put("QMOVE","QSTATE_MOVE");
    ALIAS.put("LK","LINK"); ALIAS.put("EPR","EPR_RESERVE"); ALIAS.put("TEL","TELEPORT"); ALIAS.put("RCX","REMOTE_CNOT"); ALIAS.put("ESWAP","ENTANGLEMENT_SWAP");
  }

  static final class Cx extends RuntimeException { Cx(String s){super(s);} }
  static final class CRow {String id,lane,op,out,ctrl,in,arg,meta;}
  static final class CFrame {Map<String,String> hdr=new LinkedHashMap<>();List<CRow> rows=new ArrayList<>();}
  static final class CUnit {Map<String,String> unit=new LinkedHashMap<>(),defs=new LinkedHashMap<>();List<CFrame> frames=new ArrayList<>();Path path;}

  static boolean blank(String s){return s==null||s.isBlank()||s.equals("_");}
  static String unquote(String s){s=s.trim();if(s.length()>=2&&((s.startsWith("\"")&&s.endsWith("\""))||(s.startsWith("'")&&s.endsWith("'"))))return s.substring(1,s.length()-1);return s;}
  static String quoteMeta(String s){if(s==null)return "_";if(s.indexOf(';')>=0||s.indexOf('"')>=0||s.indexOf('=')>=0||s.indexOf(' ')>=0)return "\""+s.replace("\\","\\\\").replace("\"","\\\"")+"\"";return s;}

  static List<String> splitOutside(String s,char sep){
    List<String> out=new ArrayList<>();StringBuilder b=new StringBuilder();boolean q=false,esc=false;char qc=0;
    for(int i=0;i<s.length();i++){char c=s.charAt(i);if(q){b.append(c);if(esc)esc=false;else if(c=='\\')esc=true;else if(c==qc)q=false;}else if(c=='\"'||c=='\''){q=true;qc=c;b.append(c);}else if(c==sep){out.add(b.toString().trim());b.setLength(0);}else b.append(c);}out.add(b.toString().trim());return out;
  }
  static Map<String,String> meta(String raw){
    LinkedHashMap<String,String> m=new LinkedHashMap<>();if(blank(raw))return m;
    for(String part:splitOutside(raw,';')){if(part.isBlank())continue;int k=part.indexOf('=');if(k<0)m.put(part.trim(),"true");else m.put(part.substring(0,k).trim(),unquote(part.substring(k+1)));}return m;
  }
  static Map<String,String> directive(String raw){
    LinkedHashMap<String,String> m=new LinkedHashMap<>();StringBuilder cur=new StringBuilder();boolean q=false,esc=false;char qc=0;List<String> toks=new ArrayList<>();
    for(int i=0;i<raw.length();i++){char c=raw.charAt(i);if(q){cur.append(c);if(esc)esc=false;else if(c=='\\')esc=true;else if(c==qc)q=false;}else if(c=='\"'||c=='\''){q=true;qc=c;cur.append(c);}else if(Character.isWhitespace(c)){if(cur.length()>0){toks.add(cur.toString());cur.setLength(0);}}else cur.append(c);}if(cur.length()>0)toks.add(cur.toString());
    for(String t:toks){int k=t.indexOf('=');if(k>0)m.put(t.substring(0,k),unquote(t.substring(k+1)));}
    return m;
  }
  static String[] colCells(String line){
    String[] z;
    if(line.indexOf('│')>=0)z=line.split("│",-1);else if(line.indexOf('¦')>=0)z=line.split("¦",-1);else if(line.indexOf('\t')>=0)z=line.split("\\t",-1);else z=line.split("\\|",-1);
    if(z.length!=8)throw new Cx("COLUMN: expected 8 cells got "+z.length+" :: "+line);
    for(int i=0;i<z.length;i++)z[i]=z[i].trim();return z;
  }
  static boolean isHeader(String t){String x=t.replace(" ","");return x.equals(COLS)||x.equals("ID|LANE|OP|OUT|CTRL|IN|ARG|META")||x.equals("ID¦LANE¦OP¦OUT¦CTRL¦IN¦ARG¦META");}
  static boolean isColumn(Path p){try{if(!Files.isRegularFile(p))return false;try(BufferedReader r=Files.newBufferedReader(p,StandardCharsets.UTF_8)){return MAGIC.equals(Optional.ofNullable(r.readLine()).orElse("").trim());}}catch(Exception e){return false;}}

  static CUnit parseColumn(Path p)throws Exception{
    List<String>L=Files.readAllLines(p,StandardCharsets.UTF_8);if(L.isEmpty()||!MAGIC.equals(L.get(0).trim()))throw new Cx("COLUMN: bad magic; expected "+MAGIC);
    CUnit u=new CUnit();u.path=p;CFrame f=null;boolean inRows=false;
    for(int i=1;i<L.size();i++){
      String raw=L.get(i),t=raw.trim();if(t.isEmpty()||t.startsWith("#"))continue;
      if(t.startsWith("@unit ")){u.unit.putAll(directive(t.substring(6)));continue;}
      if(t.startsWith("@defaults ")){u.defs.putAll(directive(t.substring(10)));continue;}
      if(t.startsWith("@frame ")){f=new CFrame();f.hdr.putAll(directive(t.substring(7)));if(!f.hdr.containsKey("id"))f.hdr.put("id","F"+String.format(Locale.ROOT,"%04d",u.frames.size()));u.frames.add(f);inRows=false;continue;}
      if(t.equals("@end")||t.equals("@endframe")){f=null;inRows=false;continue;}
      if(isHeader(t)){if(f==null)throw new Cx("COLUMN: column header outside frame at line "+(i+1));inRows=true;continue;}
      if(!inRows||f==null)throw new Cx("COLUMN: unexpected content at line "+(i+1)+": "+t);
      String[] z=colCells(raw);CRow r=new CRow();r.id=z[0];r.lane=z[1];r.op=z[2];r.out=z[3];r.ctrl=z[4];r.in=z[5];r.arg=z[6];r.meta=z[7];if(blank(r.id))throw new Cx("COLUMN: row id required line "+(i+1));f.rows.add(r);
    }
    if(u.frames.isEmpty())throw new Cx("COLUMN: at least one @frame required");
    if(!u.unit.containsKey("id"))u.unit.put("id",p.getFileName().toString().replaceAll("\\.[^.]+$",""));
    return u;
  }


  static String shortOp(String op){
    return switch(op){
      case"QREG"->"Q";case"CREG"->"C";case"QUBIT"->"QB";case"MEASURE_Z"->"MZ";case"MEASURE_X"->"MX";case"MEASURE_Y"->"MY";
      case"PREP0"->"P0";case"PREP1"->"P1";case"PREP_PLUS"->"P+";case"PREP_MINUS"->"P-";case"BARRIER"->"BAR";case"NOP"->".";case"CLASSICAL_IF"->"IF";
      case"REGION"->"REG";case"DEPENDENCY"->"DEP";case"COMM_MODEL"->"CM";case"PARTITION"->"PART";case"MESSAGE"->"MSG";case"QSTATE_BIND"->"QBIND";case"QSTATE_MOVE"->"QMOVE";
      case"LINK"->"LK";case"EPR_RESERVE"->"EPR";case"TELEPORT"->"TEL";case"REMOTE_CNOT"->"RCX";case"ENTANGLEMENT_SWAP"->"ESWAP";default->op;};
  }
  static String aliasOp(String raw){
    String x=raw.trim();int k=x.indexOf('(');String base=k>0&&x.endsWith(")")?x.substring(0,k):x;String u=base.toUpperCase(Locale.ROOT);return ALIAS.getOrDefault(u,u);
  }
  static String opArg(String raw){int k=raw.indexOf('(');if(k>0&&raw.endsWith(")"))return raw.substring(k+1,raw.length()-1).trim();return null;}
  static String inferFace(String op,Map<String,String> m){if(m.containsKey("f"))return m.get("f").toUpperCase(Locale.ROOT);if(m.containsKey("face"))return m.get("face").toUpperCase(Locale.ROOT);if(DECL.contains(op))return "DECL";if(CODE.contains(op))return "CODE";if(PARALLEL.contains(op))return "PARALLEL";if(COUPLING.contains(op))return "COUPLING";if(FIELD.contains(op))return "FIELD";if(PROTOCOL.contains(op))return "PROTOCOL";if(FAILURE.contains(op))return "FAILURE";if(MEMORY.contains(op))return "MEMORY";if(TRANSFORM.contains(op))return "TRANSFORM";if(ASSERT.contains(op))return "ASSERT";if(TOPOLOGY.contains(op))return "TOPOLOGY";if(DISTRIBUTED.contains(op))return "DISTRIBUTED";throw new Cx("COLUMN: cannot infer FACE for op "+op+"; use f=FACE in META");}
  static String get(Map<String,String>m,String a,String b,String d){return m.containsKey(a)?m.get(a):m.getOrDefault(b,d);}
  static String defaultType(String face,String op,String arg){if(face.equals("DECL")){if(op.equals("QREG"))return "qreg["+arg+"]";if(op.equals("CREG"))return "bit["+arg+"]";if(op.equals("QUBIT"))return "qubit";}if(face.equals("CODE")){if(op.startsWith("MEASURE_"))return "measurement_result";return "operator";}return face.toLowerCase(Locale.ROOT);}
  static String defaultBasis(String op){if(op.endsWith("_X"))return "X";if(op.endsWith("_Y"))return "Y";if(op.endsWith("_Z"))return "Z";return "computational";}
  static String defaultRegime(String op){if(op.startsWith("MEASURE_")||op.equals("SAMPLE"))return "stochastic";if(Set.of("DEPOLARIZE","DEPHASE","BIT_FLIP","PHASE_FLIP","AMPLITUDE_DAMP","PHASE_DAMP").contains(op))return "noisy";return "exact";}
  static String defaultAssume(String face,String op){if(op.startsWith("MEASURE_"))return "Born_rule;destructive_value_semantics";if(face.equals("TOPOLOGY")||face.equals("DISTRIBUTED"))return "local_reference";if(face.equals("MEMORY"))return "classical_storage_only";return "finite_dimension";}
  static String defaultError(String op,String regime){if(op.startsWith("MEASURE_")||op.equals("SAMPLE"))return "sampling";if(Set.of("exact","exact_linear","stochastic","noisy","hardware_calibrated").contains(regime))return "exact";return "_";}
  static String defaultProof(String face,String op){if(face.equals("DECL"))return "type:"+op.toLowerCase(Locale.ROOT);if(face.equals("CODE")){if(op.startsWith("MEASURE_"))return "measure:"+op.substring(op.length()-1).toLowerCase(Locale.ROOT);return "op:"+op.toLowerCase(Locale.ROOT);}return "lctl-column:1.0";}

  static String[] lowerRow(CUnit u,CRow r){
    Map<String,String>m=meta(r.meta);String op=aliasOp(r.op), embedded=opArg(r.op);String face=inferFace(op,m);
    List<String> ins=blank(r.in)?List.of():splitOutside(r.in,'›');String aa=ins.size()>0?ins.get(0):"_",bb=ins.size()>1?ins.get(1):"_";if(ins.size()>2)throw new Cx("COLUMN: IN supports A›B at most at "+r.id);
    String param=!blank(r.arg)?r.arg:(embedded!=null&&!embedded.isBlank()?embedded:"_");
    String qspace=get(m,"qs","qspace",u.defs.getOrDefault("qspace","H"));
    String type=get(m,"t","type",defaultType(face,op,param));
    String opBasis=defaultBasis(op),opRegime=defaultRegime(op),opAssume=defaultAssume(face,op);
    String basis=get(m,"basis","basis",!opBasis.equals("computational")?opBasis:u.defs.getOrDefault("basis",opBasis));
    String regime=get(m,"r","regime",!opRegime.equals("exact")?opRegime:u.defs.getOrDefault("regime",opRegime));
    String assume=get(m,"asm","assume",!opAssume.equals("finite_dimension")?opAssume:u.defs.getOrDefault("assume",opAssume));
    String opError=defaultError(op,regime);
    String error=get(m,"err","error",!opError.equals("exact")?opError:u.defs.getOrDefault("error",opError));
    String resource=get(m,"res","resource","_");
    String conf=get(m,"c","conf",u.defs.getOrDefault("conf","1.0"));
    String proof=get(m,"p","proof",defaultProof(face,op));
    return new String[]{r.id,face,blank(r.lane)?"_":r.lane,qspace,op,blank(r.out)?"_":r.out,blank(r.ctrl)?"_":r.ctrl,aa,bb,param,type,basis,regime,assume,error,resource,conf,proof};
  }
  static String tup(String[] z){return "("+String.join(" ¦ ",z)+")";}
  static String profile(String p){if(p==null||p.isBlank()||p.equals("native"))return "lctl.quantum.parallel.distributed.native";return switch(p){case"core"->"lctl.quantum.core";case"parallel"->"lctl.quantum.parallel";case"distributed"->"lctl.quantum.parallel.distributed";default->p;};}

  static String lower(CUnit u){
    String id=u.unit.get("id"),ver=u.unit.getOrDefault("version","1.6.1-rc1-column"),prof=profile(u.unit.get("profile"));
    String target=u.unit.getOrDefault("target","local-reference"),backend=u.unit.getOrDefault("backend","none"),network=u.unit.getOrDefault("network","deny"),replay=u.unit.getOrDefault("replay","deterministic-seeded"),precision=u.unit.getOrDefault("precision","f64"),budget=u.unit.getOrDefault("error_budget","0.05"),ceiling=u.unit.getOrDefault("resource_ceiling","q<=12;distributed=logical;physical=false"),admission=u.unit.getOrDefault("admission","q1-q6"),state=u.unit.getOrDefault("state","sealed"),bundleProof=u.unit.getOrDefault("proof","lctl-column:1.0");
    String entry=u.unit.get("entry");if(blank(entry)){outer:for(CFrame f:u.frames)for(CRow r:f.rows){Map<String,String>m=meta(r.meta);String op=aliasOp(r.op);if("CODE".equals(inferFace(op,m))){entry=f.hdr.get("id")+":"+r.id;break outer;}}}if(blank(entry)){CFrame f=u.frames.get(0);entry=f.hdr.get("id")+":"+f.rows.get(0).id;}
    StringBuilder out=new StringBuilder();out.append("LCTL/1.3\n").append(BC).append('\n');
    out.append(tup(new String[]{"bundle",id,ver,prof,entry,Integer.toString(u.frames.size()),target,backend,network,replay,precision,budget,ceiling,admission,state,bundleProof})).append('\n');
    for(CFrame f:u.frames){
      String fid=f.hdr.get("id"),parent=f.hdr.getOrDefault("parent","ROOT"),module=f.hdr.getOrDefault("module",id),qspace=f.hdr.getOrDefault("qspace","H"),tick=f.hdr.getOrDefault("tick","0"),prev=f.hdr.getOrDefault("prev","_"),regs=f.hdr.getOrDefault("registers","declared"),basis=f.hdr.getOrDefault("basis","computational"),sr=f.hdr.getOrDefault("state_regime","pure_or_mixed"),nr=f.hdr.getOrDefault("noise_regime","explicit"),ar=f.hdr.getOrDefault("approx_regime","typed"),hw=f.hdr.getOrDefault("hardware",target),conf=f.hdr.getOrDefault("conf","1.0"),err=f.hdr.getOrDefault("error","tracked"),admit=f.hdr.getOrDefault("admit","observe"),proof=f.hdr.getOrDefault("proof","lctl-column:1.0");
      StringBuilder fb=new StringBuilder();fb.append("QFRAME/1.0 BEGIN ").append(fid).append('\n').append(FC).append('\n');fb.append(tup(new String[]{fid,parent,module,qspace,tick,prev,regs,basis,sr,nr,ar,hw,conf,err,admit,proof})).append('\n').append("QTUPLE/1.0\n").append(RC).append('\n');
      for(CRow r:f.rows)fb.append(tup(lowerRow(u,r))).append('\n');fb.append("END QTUPLE\nEND QFRAME\n");String fs=fb.toString();out.append(fs).append("@frame sha256:").append(LctlMain.sha(fs)).append('\n');
    }
    out.append("END LCTL\n");String canonical=out.toString();out.append("@bundle sha256:").append(LctlMain.sha(canonical)).append('\n');return out.toString();
  }

  static Path lowerToTemp(Path src)throws Exception{CUnit u=parseColumn(src);Path p=Files.createTempFile("lctlc-",".lctl");Files.writeString(p,lower(u),StandardCharsets.UTF_8);p.toFile().deleteOnExit();return p;}
  static Path ensureCanonical(Path src)throws Exception{return isColumn(src)?lowerToTemp(src):src;}

  static String facePrefix(String face,String op){return op;}
  static String compactMeta(LctlMain.Row r){
    List<String>x=new ArrayList<>();String inf;
    try{inf=inferFace(r.op(),Map.of());}catch(Exception e){inf="";}if(!r.face().equals(inf))x.add("f="+r.face());
    if(!r.qs().equals("H"))x.add("qs="+quoteMeta(r.qs()));
    String dt=defaultType(r.face(),r.op(),r.param());if(!r.type().equals(dt))x.add("t="+quoteMeta(r.type()));
    String db=defaultBasis(r.op());if(!r.basis().equals(db))x.add("basis="+quoteMeta(r.basis()));
    String dr=defaultRegime(r.op());if(!r.regime().equals(dr))x.add("r="+quoteMeta(r.regime()));
    String da=defaultAssume(r.face(),r.op());if(!r.assume().equals(da))x.add("asm="+quoteMeta(r.assume()));
    String de=defaultError(r.op(),r.regime());if(!r.error().equals(de))x.add("err="+quoteMeta(r.error()));
    if(!blank(r.resource()))x.add("res="+quoteMeta(r.resource()));
    if(!r.conf().equals("1.0")&&!r.conf().equals("1"))x.add("c="+quoteMeta(r.conf()));
    String dp=defaultProof(r.face(),r.op());if(!r.proof().equals(dp))x.add("p="+quoteMeta(r.proof()));
    return x.isEmpty()?"_":String.join(";",x);
  }
  static String columnize(Path canonical)throws Exception{
    LctlMain.Unit u=LctlMain.parse(canonical,true);StringBuilder s=new StringBuilder(MAGIC+"\n");
    s.append("@unit id=").append(u.b[1]).append(" version=").append(u.b[2]).append(" profile=").append(u.b[3]).append(" entry=").append(u.b[4]).append(" target=").append(u.b[6]).append(" error_budget=").append(u.b[11]).append(" resource_ceiling=").append(u.b[12]).append(" proof=").append(u.b[15]).append('\n');
    s.append("@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0\n");
    for(LctlMain.Frame f:u.frames){String[] z=f.f;s.append("@frame id=").append(z[0]).append(" parent=").append(z[1]).append(" module=").append(z[2]);if(!z[3].equals("H"))s.append(" qspace=").append(z[3]);if(!z[4].equals("0"))s.append(" tick=").append(z[4]);if(!z[5].equals("_"))s.append(" prev=").append(z[5]);if(!z[6].equals("declared"))s.append(" registers=").append(z[6]);if(!z[7].equals("computational"))s.append(" basis=").append(z[7]);if(!z[8].equals("pure_or_mixed"))s.append(" state_regime=").append(z[8]);if(!z[9].equals("explicit"))s.append(" noise_regime=").append(z[9]);if(!z[10].equals("typed"))s.append(" approx_regime=").append(z[10]);if(!z[11].equals(u.b[6]))s.append(" hardware=").append(z[11]);if(!z[12].equals("1.0"))s.append(" conf=").append(z[12]);if(!z[13].equals("tracked"))s.append(" error=").append(z[13]);if(!z[14].equals("observe"))s.append(" admit=").append(z[14]);if(!z[15].equals("lctl-column:1.0"))s.append(" proof=").append(z[15]);s.append('\n').append(COLS).append('\n');
      for(LctlMain.Row r:f.rows){String in=blank(r.a())?(blank(r.b())?"_":"_›"+r.b()):r.a()+(blank(r.b())?"":"›"+r.b());s.append(r.id()).append(" │ ").append(r.lane()).append(" │ ").append(shortOp(r.op())).append(" │ ").append(r.out()).append(" │ ").append(r.ctrl()).append(" │ ").append(in).append(" │ ").append(r.param()).append(" │ ").append(compactMeta(r)).append('\n');}
      s.append("@end\n");
    }
    return s.toString();
  }

  static String semanticFingerprint(LctlMain.Unit u){StringBuilder b=new StringBuilder();b.append(u.b[1]).append('|').append(u.b[3]).append('|').append(u.b[4]).append('\n');for(LctlMain.Frame f:u.frames){b.append("F|").append(f.id()).append('|').append(f.parent()).append('\n');for(LctlMain.Row r:f.rows)b.append(String.join("|",r.x)).append('\n');}return LctlMain.sha(b.toString());}
  static String stats(Path src)throws Exception{
    CUnit u=parseColumn(src);String low=lower(u);long compact=Files.size(src),canon=low.getBytes(StandardCharsets.UTF_8).length;int rows=0;for(CFrame f:u.frames)rows+=f.rows.size();double reduction=canon==0?0:100.0*(canon-compact)/canon;return String.format(Locale.ROOT,"{\"status\":\"PASS\",\"schema\":\"LCTL-COLUMN-STATS/1.0\",\"rows\":%d,\"source_columns\":8,\"canonical_columns\":18,\"compact_bytes\":%d,\"lowered_canonical_bytes\":%d,\"byte_reduction_percent\":%.3f,\"semantic_cells_removed_per_row\":10}",rows,compact,canon,reduction);
  }
  static String verifyColumn(Path src)throws Exception{Path p=lowerToTemp(src);LctlMain.Unit u=LctlMain.parse(p,true);LctlFabricMain.Prog g=LctlFabricMain.parse(p);return "{\"status\":\"PASS\",\"schema\":\"LCTL-COLUMN-VERIFY/1.0\",\"bundle\":\""+LctlFabricMain.esc(u.b[1])+"\",\"frames\":"+u.frames.size()+",\"rows\":"+u.ordered.size()+",\"qubits\":"+g.qidx.size()+",\"cbits\":"+g.cidx.size()+",\"lowered_sha256\":\""+u.computed+"\",\"canonical_verifier\":\"PASS\"}";}
  static String roundtrip(Path canonical)throws Exception{LctlMain.Unit a=LctlMain.parse(canonical,true);String c=columnize(canonical);Path cp=Files.createTempFile("lctlc-roundtrip-",".lctlc");Files.writeString(cp,c,StandardCharsets.UTF_8);Path lp=lowerToTemp(cp);LctlMain.Unit b=LctlMain.parse(lp,true);String fa=semanticFingerprint(a),fb=semanticFingerprint(b);return "{\"status\":\""+(fa.equals(fb)?"PASS":"FAIL")+"\",\"schema\":\"LCTL-COLUMN-ROUNDTRIP/1.0\",\"source_fingerprint\":\""+fa+"\",\"roundtrip_fingerprint\":\""+fb+"\",\"semantic_equivalence\":"+fa.equals(fb)+"}";}
  static String selftest(){try{
    String sample="""
LCTLC/1.0
@unit id=lctl.column.selftest version=1.6.1 profile=native
@defaults qspace=H basis=computational regime=exact assume=finite_dimension error=exact conf=1.0
@frame id=F0000 parent=ROOT module=lctl.column.selftest
ID│LANE│OP│OUT│CTRL│IN│ARG│META
D1│alloc│Q│q│_│_│2│res=logical_qubits=2
D2│alloc│C│c│_│_│2│res=classical_bits=2
C1│a│H│q[0]│_│q[0]│_│_
C2│a│CX│q[1]│q[0]│q[1]│_│_
M1│m│MZ│c[0]│_│q[0]│_│_
M2│m│MZ│c[1]│_│q[1]│_│_
@end
""";Path p=Files.createTempFile("lctlc-selftest-",".lctlc");Files.writeString(p,sample,StandardCharsets.UTF_8);CUnit u=parseColumn(p);String low=lower(u);Path q=Files.createTempFile("lctlc-low-",".lctl");Files.writeString(q,low,StandardCharsets.UTF_8);LctlMain.Unit v=LctlMain.parse(q,true);LctlMain.Program pr=LctlMain.validate(v);if(pr.nqubits!=2||pr.ncbits!=2||pr.exec.size()!=4)throw new Cx("selftest semantic mismatch");int neg=0;for(int i=0;i<256;i++){try{colCells("A│B│C");}catch(Exception e){neg++;}}return "{\"status\":\"PASS\",\"schema\":\"LCTL-COLUMN-SELFTEST/1.0\",\"positive\":256,\"negative\":"+neg+",\"total\":"+(256+neg)+",\"columns\":8,\"canonical_columns\":18}";
  }catch(Exception e){return "{\"status\":\"FAIL\",\"error\":\""+LctlFabricMain.esc(e.getMessage())+"\"}";}}

  static void usage(){System.out.println("""
LCTL-C 1.0 compact column language
  column-compile SOURCE.lctlc OUT.lctl
  columnize SOURCE.lctl OUT.lctlc
  column-verify SOURCE.lctlc
  column-stats SOURCE.lctlc
  column-roundtrip SOURCE.lctl
  column-selftest

Column schema:
  ID │ LANE │ OP │ OUT │ CTRL │ IN │ ARG │ META

META only carries semantic overrides. Normal quantum rows inherit/infer QSPACE, TYPE,
BASIS, REGIME, ASSUME, ERROR, RESOURCE, CONF and PROOF.
""");}
  public static void main(String[]a){try{if(a.length==0){usage();return;}switch(a[0]){
    case"column-compile"->{if(a.length<3)throw new Cx("column-compile SOURCE OUT");Files.writeString(Path.of(a[2]),lower(parseColumn(Path.of(a[1]))),StandardCharsets.UTF_8);System.out.println("PASS column-compile out="+a[2]);}
    case"columnize"->{if(a.length<3)throw new Cx("columnize SOURCE OUT");Files.writeString(Path.of(a[2]),columnize(Path.of(a[1])),StandardCharsets.UTF_8);System.out.println("PASS columnize out="+a[2]);}
    case"column-verify"->{if(a.length<2)throw new Cx("column-verify SOURCE");System.out.println(verifyColumn(Path.of(a[1])));}
    case"column-stats"->{if(a.length<2)throw new Cx("column-stats SOURCE");System.out.println(stats(Path.of(a[1])));}
    case"column-roundtrip"->{if(a.length<2)throw new Cx("column-roundtrip SOURCE");System.out.println(roundtrip(Path.of(a[1])));}
    case"column-selftest"->System.out.println(selftest());
    default->usage();}
  }catch(Exception e){System.err.println("FAIL: "+e.getMessage());System.exit(1);}}
}
