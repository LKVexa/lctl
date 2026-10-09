import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.io.*;

public final class MsslbLxRuntime {
  static final String BM="MSSLB-LX/1.0";
  static final String BC="BUNDLE¦ID¦VERSION¦PROFILE¦TARGET¦ENTRY¦FRAMES¦KNOWLEDGE¦BACKEND¦NETWORK¦POLICY¦REPLAY¦ADMISSION¦ERROR¦STATE¦PROOF";
  static final String FC="ID¦PARENT¦SOURCE¦FIELD¦MODULE¦SEQ¦DEPTH¦TICK¦SYMBOL¦EXEC¦ROUTE¦LEARN¦CLASS¦REGIME¦ASSUME¦ERROR¦CONF¦ADMIT¦STATUS¦PROOF";
  static final String RC="ROW¦FACE¦LANE¦OP¦OUT¦A¦B¦CAP¦WIDTH¦MODE¦AUX¦TYPE¦CLASS¦REGIME¦ASSUME¦ERROR¦CONF¦PROOF";
  static final Set<String> FACES=Set.of("CODE","KNOW","LEARN","MODEL","ASSERT","EVIDENCE");
  static final Set<String> REGIMES=Set.of("exact","exact_linear","linearized","approximate","stochastic","structural_analogue","nonlinear","combinatorial","no_faithful_form","impossibility","mixed");
  static class X extends RuntimeException{X(String s){super(s);}}
  static class Row{String[] x;Row(String[] z){x=z;}String get(int i){return x[i];}}
  static class Frame{
    String id,parent,field,module,klass,regime,assume,error,conf,admit,status,proof,seal,computed;
    long seq,depth,tick; List<Row> rows=new ArrayList<>();
  }
  static class Bundle{
    String id,version,profile,target,entry,knowledge,backend,network,policy,replay,admission,error,state,proof,seal,computed;
    int framesDeclared; List<Frame> frames=new ArrayList<>();
  }

  static String norm(String s){return s.replace("\r\n","\n").replace("\r","\n");}
  static String hex(byte[] b){StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format("%02x",x&255));return s.toString();}
  static String sha(String s){try{return hex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new RuntimeException(e);}}
  static String[] tup(String line,int n,String what){
    String s=line.trim(); if(!(s.startsWith("(")&&s.endsWith(")"))) throw new X(what+": tuple expected");
    String[] z=s.substring(1,s.length()-1).split("¦",-1); if(z.length!=n) throw new X(what+": expected "+n+" cells got "+z.length);
    for(int i=0;i<n;i++)z[i]=z[i].trim(); return z;
  }
  static long ui(String s,String what){try{long v=Long.parseLong(s);if(v<0)throw new Exception();return v;}catch(Exception e){throw new X(what+": unsigned integer expected");}}
  static double conf(String s,String what){try{double v=Double.parseDouble(s);if(v<0||v>1)throw new Exception();return v;}catch(Exception e){throw new X(what+": confidence must be 0..1");}}

  static Bundle parse(Path p)throws Exception{
    String all=norm(Files.readString(p,StandardCharsets.UTF_8));
    List<String>L=new ArrayList<>(Arrays.asList(all.split("\n",-1)));
    if(L.size()<6||!BM.equals(L.get(0)))throw new X("BUNDLE_PARSE: bad magic");
    if(!BC.equals(L.get(1)))throw new X("BUNDLE_PARSE: bad columns");
    String[] b=tup(L.get(2),16,"BUNDLE_PARSE");
    if(!"bundle".equals(b[0]))throw new X("BUNDLE_PARSE: first cell");
    Bundle z=new Bundle();
    z.id=b[1];z.version=b[2];z.profile=b[3];z.target=b[4];z.entry=b[5];z.framesDeclared=(int)ui(b[6],"frames");
    z.knowledge=b[7];z.backend=b[8];z.network=b[9];z.policy=b[10];z.replay=b[11];z.admission=b[12];z.error=b[13];z.state=b[14];z.proof=b[15];
    if(!"msslb.lx.bwec1".equals(z.profile))throw new X("BUNDLE_POLICY: profile");
    if(!"BWX1".equals(z.target))throw new X("BUNDLE_POLICY: target");
    if(!"none".equals(z.backend))throw new X("BUNDLE_POLICY: backend must be none");
    if(!"deny".equals(z.network))throw new X("BUNDLE_POLICY: network must be deny");
    if(!"local-only".equals(z.policy))throw new X("BUNDLE_POLICY: local-only required");
    if(!"deterministic".equals(z.replay))throw new X("BUNDLE_POLICY: deterministic replay required");
    if(!"q1-q6".equals(z.admission))throw new X("BUNDLE_POLICY: q1-q6 admission required");
    if(!"fail-closed".equals(z.error))throw new X("BUNDLE_POLICY: fail-closed required");
    Set<String> fids=new HashSet<>(),rids=new HashSet<>(); int i=3;
    while(i<L.size()&&L.get(i).startsWith("MCRT-K/1.0 BEGIN ")){
      int fs=i;String did=L.get(i).substring("MCRT-K/1.0 BEGIN ".length()).trim();i++;
      if(i>=L.size()||!FC.equals(L.get(i)))throw new X("FRAME_PARSE: columns "+did);i++;
      String[] f=tup(L.get(i),20,"FRAME_PARSE "+did);i++;
      Frame q=new Frame();q.id=f[0];q.parent=f[1];q.field=f[3];q.module=f[4];q.seq=ui(f[5],"seq");q.depth=ui(f[6],"depth");q.tick=ui(f[7],"tick");
      q.klass=f[12];q.regime=f[13];q.assume=f[14];q.error=f[15];q.conf=f[16];q.admit=f[17];q.status=f[18];q.proof=f[19];
      if(!did.equals(q.id))throw new X("FRAME_PARSE: begin id mismatch");
      if(!fids.add(q.id))throw new X("FRAME_LINEAGE: duplicate "+q.id);
      if(!REGIMES.contains(q.regime))throw new X("FRAME_PARSE: bad regime "+q.regime);
      conf(q.conf,"FRAME_PARSE "+q.id);
      if(!Set.of("admit","reject","observe").contains(q.admit))throw new X("FRAME_PARSE: bad ADMIT "+q.admit);
      if(i>=L.size()||!"MSSL-S/1.0".equals(L.get(i)))throw new X("FRAME_PARSE: missing MSSL-S");i++;
      if(i>=L.size()||!RC.equals(L.get(i)))throw new X("ROW_PARSE: columns");i++;
      Set<String> evidence=new HashSet<>();boolean hasLearn=false;
      while(i<L.size()&&!"END MSSL-S".equals(L.get(i))){
        if(L.get(i).trim().isEmpty()){i++;continue;}
        String[] r=tup(L.get(i),18,"ROW_PARSE "+q.id);i++;
        if(!rids.add(r[0]))throw new X("ROW_DUPLICATE: "+r[0]);
        if(!FACES.contains(r[1]))throw new X("ROW_PARSE: face "+r[1]);
        if(!REGIMES.contains(r[13]))throw new X("ROW_PARSE: regime "+r[13]+" at "+r[0]);
        conf(r[16],"ROW_PARSE "+r[0]);
        if("CODE".equals(r[1]) && !Set.of("exact","exact_linear").contains(r[13]))throw new X("ROW_PARSE: CODE regime must be exact/exact_linear at "+r[0]);
        if("LEARN".equals(r[1]))hasLearn=true;
        if("EVIDENCE".equals(r[1]) && r[3].matches("Q[1-6]") && "pass".equals(r[5]))evidence.add(r[3]);
        q.rows.add(new Row(r));
      }
      if(i>=L.size()||!"END MSSL-S".equals(L.get(i)))throw new X("FRAME_PARSE: missing END MSSL-S");i++;
      if(i>=L.size()||!"END MCRT-K".equals(L.get(i)))throw new X("FRAME_PARSE: missing END MCRT-K");i++;
      String canon=String.join("\n",L.subList(fs,i))+"\n";q.computed=sha(canon);
      if(i>=L.size()||!L.get(i).startsWith("@frame sha256:"))throw new X("FRAME_SEAL: missing "+q.id);
      q.seal=L.get(i).substring("@frame sha256:".length()).trim().toLowerCase(Locale.ROOT);
      if(!q.seal.equals(q.computed))throw new X("FRAME_SEAL: mismatch "+q.id);
      i++;
      if(hasLearn){
        if(!"admit".equals(q.admit))throw new X("LEARN_GATE: frame "+q.id+" contains LEARN but ADMIT is "+q.admit);
        for(int k=1;k<=6;k++)if(!evidence.contains("Q"+k))throw new X("LEARN_GATE: frame "+q.id+" missing Q"+k+" pass");
      }
      z.frames.add(q);
    }
    if(z.frames.size()!=z.framesDeclared)throw new X("BUNDLE_PARSE: frame count");
    for(Frame q:z.frames)if(!"ROOT".equals(q.parent)&&!fids.contains(q.parent))throw new X("FRAME_LINEAGE: unknown parent "+q.parent);
    if(i>=L.size()||!"END MSSLB-LX".equals(L.get(i)))throw new X("BUNDLE_PARSE: missing END MSSLB-LX");
    String canon=String.join("\n",L.subList(0,i+1))+"\n";z.computed=sha(canon);i++;
    if(i>=L.size()||!L.get(i).startsWith("@bundle sha256:"))throw new X("BUNDLE_SEAL: missing");
    z.seal=L.get(i).substring("@bundle sha256:".length()).trim().toLowerCase(Locale.ROOT);
    if(!z.seal.equals(z.computed))throw new X("BUNDLE_SEAL: mismatch");
    String[] ep=z.entry.split(":",2);if(ep.length!=2)throw new X("BUNDLE_PARSE: entry FRAME:ROW required");
    boolean ok=false;for(Frame q:z.frames)if(q.id.equals(ep[0]))for(Row r:q.rows)if(r.get(0).equals(ep[1])&&"CODE".equals(r.get(1)))ok=true;
    if(!ok)throw new X("BUNDLE_PARSE: entry not CODE");
    return z;
  }

  static Path qctl(Bundle b)throws Exception{
    String entry=b.entry.split(":",2)[1];
    StringBuilder s=new StringBuilder("QCTL/1.0\nROW¦KIND¦LANE¦OP¦OUT¦A¦B¦CAP¦WIDTH¦MODE¦AUX¦PROOF\n");
    String[][] m={
      {"M0001","META","meta","DEFINE","module",b.id,"_","_","_","_","_","msslb-lx"},
      {"M0002","META","meta","DEFINE","profile","qctl.backendless.bwec1","_","_","_","_","_","msslb-lx"},
      {"M0003","META","meta","DEFINE","target","BWX1","_","_","_","_","_","msslb-lx"},
      {"M0004","META","meta","DEFINE","backend","none","_","_","_","_","_","msslb-lx"},
      {"M0005","META","meta","DEFINE","network","deny","_","_","_","_","_","msslb-lx"},
      {"M0006","META","meta","DEFINE","word_bits","1048576","_","_","_","_","_","msslb-lx"},
      {"M0007","META","meta","DEFINE","entry",entry,"_","_","_","_","_","msslb-lx"}
    };
    for(String[] x:m)s.append("(").append(String.join(" ¦ ",x)).append(")\n");
    List<Frame> fs=new ArrayList<>(b.frames);fs.sort(Comparator.comparingLong(x->x.seq));
    for(Frame f:fs)for(Row rr:f.rows)if("CODE".equals(rr.get(1))){
      String[] r=rr.x;
      String[] q={r[0],"CODE",r[2],r[3],r[4],r[5],r[6],r[7],r[8],r[9],r[10],r[17]};
      s.append("(").append(String.join(" ¦ ",q)).append(")\n");
    }
    s.append("END\n");String canon=s.toString();s.append("@seal sha256:").append(sha(canon)).append("\n");
    Path p=Files.createTempFile("msslb-lx-", ".qctl");Files.writeString(p,s.toString(),StandardCharsets.UTF_8);return p;
  }

  static List<String[]> memory()throws Exception{
    InputStream in=MsslbLxRuntime.class.getResourceAsStream("/knowledge/compendium_semantic_memory.tsv");
    if(in==null)throw new X("KNOWLEDGE: embedded memory not found");
    List<String[]> out=new ArrayList<>();
    try(BufferedReader br=new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8))){
      String line;boolean first=true;while((line=br.readLine())!=null){if(first){first=false;continue;}out.add(line.split("\t",-1));}
    }
    return out;
  }
  static Set<String> toks(String s){Set<String>x=new HashSet<>();for(String t:s.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))if(t.length()>=3)x.add(t);return x;}
  static void query(String term)throws Exception{
    Set<String> q=toks(term);List<Object[]> scored=new ArrayList<>();
    for(String[] r:memory()){
      String hay=String.join(" ",r);Set<String> h=toks(hay);int sc=0;for(String t:q)if(h.contains(t))sc++;
      if(sc>0)scored.add(new Object[]{sc,r});
    }
    scored.sort((a,b)->Integer.compare((Integer)b[0],(Integer)a[0]));
    int n=Math.min(10,scored.size());
    System.out.println("LOCAL COMPENDIUM MATCHES: "+n);
    for(int i=0;i<n;i++){String[] r=(String[])scored.get(i)[1];System.out.println((i+1)+". field="+r[0]+" entry="+r[2]+" "+r[3]+" | regime="+r[4]+" | class="+r[5]);System.out.println("   "+r[7]);}
  }
  static void learnedSummary()throws Exception{
    Map<String,Integer> reg=new TreeMap<>(), fld=new TreeMap<>();int n=0;
    for(String[] r:memory()){n++;reg.put(r[4],reg.getOrDefault(r[4],0)+1);fld.put(r[0],fld.getOrDefault(r[0],0)+1);}
    System.out.println("records="+n);System.out.println("regimes="+reg);System.out.println("fields="+fld);
  }

  static void usage(){System.out.println("MSSLB-LX / MCRT-K / MSSL-S 3.0.0");System.out.println("verify FILE");System.out.println("inspect FILE");System.out.println("run FILE");System.out.println("compile-bwx FILE OUT");System.out.println("knowledge TERM");System.out.println("learned-summary");}
  public static void main(String[] a){
    Path tmp=null;
    try{
      if(a.length<1){usage();System.exit(2);}
      if("knowledge".equals(a[0])){if(a.length<2)throw new X("knowledge needs a term");query(String.join(" ",Arrays.copyOfRange(a,1,a.length)));return;}
      if("learned-summary".equals(a[0])){learnedSummary();return;}
      if(a.length<2){usage();System.exit(2);}
      Bundle b=parse(Path.of(a[1]));
      switch(a[0]){
        case "verify"->{tmp=qctl(b);QctlRuntime.Unit u=QctlRuntime.parse(tmp,true);System.out.println("PASS: bundle="+b.id+" frames="+b.frames.size()+" code_rows="+u.code.size()+" knowledge="+b.knowledge+" seal="+b.computed);}
        case "inspect"->{int code=0,learn=0,know=0,model=0;for(Frame f:b.frames)for(Row r:f.rows){switch(r.get(1)){case"CODE"->code++;case"LEARN"->learn++;case"KNOW"->know++;case"MODEL"->model++;}}System.out.println("{\"status\":\"PASS\",\"bundle\":\""+b.id+"\",\"frames\":"+b.frames.size()+",\"code\":"+code+",\"learn\":"+learn+",\"know\":"+know+",\"model\":"+model+",\"backend\":\""+b.backend+"\",\"network\":\""+b.network+"\"}");}
        case "run"->{tmp=qctl(b);QctlRuntime.Unit u=QctlRuntime.parse(tmp,true);QctlRuntime.VM v=QctlRuntime.run(u);System.out.println("MSSLB-LX RESULT: exit="+v.exit+" trap="+v.trap+" steps="+v.steps);if(v.trap!=0||v.exit!=0)System.exit(v.exit==0?1:v.exit);}
        case "compile-bwx"->{if(a.length!=3)throw new X("compile-bwx needs output");tmp=qctl(b);QctlRuntime.Unit u=QctlRuntime.parse(tmp,true);Files.write(Path.of(a[2]),QctlRuntime.compile(u));System.out.println("PASS: BWX1 compiled");}
        default->{usage();System.exit(2);}
      }
    }catch(Exception e){System.err.println("FAIL: "+e.getMessage());System.exit(1);}
    finally{try{if(tmp!=null)Files.deleteIfExists(tmp);}catch(Exception ignored){}}
  }
}
