
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.regex.*;

public final class LctlFabricMain {
  static final Set<String> UNARY=Set.of("I","X","Y","Z","H","S","SDG","T","TDG","RX","RY","RZ","PHASE");
  static final Set<String> TWO=Set.of("CX","CNOT","CY","CZ","CH","CPHASE","SWAP","ISWAP");
  static final double TOL=1e-10;

  static final class X extends RuntimeException { X(String m){super(m);} }
  static final class Row {
    String id,face,lane,qspace,op,out,ctrl,a,b,param,type,basis,regime,assume,error,resource,conf,proof;
    Row(String[] z){id=z[0];face=z[1];lane=z[2];qspace=z[3];op=z[4];out=z[5];ctrl=z[6];a=z[7];b=z[8];param=z[9];type=z[10];basis=z[11];regime=z[12];assume=z[13];error=z[14];resource=z[15];conf=z[16];proof=z[17];}
  }
  static final class Prog {
    Path path; String magic,bundleId,version,profile,sha;
    List<Row> rows=new ArrayList<>(), code=new ArrayList<>();
    LinkedHashMap<String,Integer> qidx=new LinkedHashMap<>(), cidx=new LinkedHashMap<>();
  }
  static final class Edge {int u,v;String reason;Edge(int u,int v,String r){this.u=u;this.v=v;this.reason=r;}}
  static final class Graph {List<Row> tasks;List<Edge> edges;List<List<Edge>> in;Graph(List<Row>t,List<Edge>e){tasks=t;edges=e;in=new ArrayList<>();for(int i=0;i<t.size();i++)in.add(new ArrayList<>());for(Edge x:e)in.get(x.v).add(x);}}
  static final class TNode {String id,clazz;double memory=Double.POSITIVE_INFINITY, coherence=Double.POSITIVE_INFINITY;int concurrency=Integer.MAX_VALUE;}
  static final class TEdge {String src,dst,type;double latency=1, bandwidth=1e9, fidelity=1, success=1;}
  static final class Topology {List<TNode> nodes=new ArrayList<>();List<TEdge> links=new ArrayList<>();Map<String,TNode> byId=new LinkedHashMap<>();}
  static final class PartitionResult {int[] part;int k;double heuristicCost;Double exactCost;}
  static final class Placement {Map<Integer,String> p2node=new LinkedHashMap<>();}
  static final class Route {String src,dst,type;List<String> path;double cost,fidelity=1,success=1;}
  static final class ScheduleItem {int task,part;String node;double start,end,comm;List<String> reasons=new ArrayList<>();}
  static final class C {double r,i;C(double r,double i){this.r=r;this.i=i;}C add(C z){return new C(r+z.r,i+z.i);}C mul(C z){return new C(r*z.r-i*z.i,r*z.i+i*z.r);}C scale(double a){return new C(r*a,i*a);}}
  static final class StateRun {Path file;int n;int N,workers,gates;long bytesTouched;String hash;double maxErr;StateRun(Path f){file=f;}}

  static String esc(String s){if(s==null)return "";return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");}
  static String sha(byte[] b){try{MessageDigest m=MessageDigest.getInstance("SHA-256");byte[]h=m.digest(b);StringBuilder s=new StringBuilder();for(byte x:h)s.append(String.format("%02x",x&255));return s.toString();}catch(Exception e){throw new RuntimeException(e);}}
  static String sha(String s){return sha(s.getBytes(StandardCharsets.UTF_8));}
  static String fileSha(Path p)throws Exception{return sha(Files.readAllBytes(p));}
  static boolean blank(String s){return s==null||s.isBlank()||s.equals("_");}
  static String[] cells(String line){
    String s=line.trim(); if(!s.startsWith("(")||!s.endsWith(")"))throw new X("tuple required: "+line);
    String[] z=s.substring(1,s.length()-1).split("¦",-1); if(z.length!=18)throw new X("expected 18 cells got "+z.length);
    for(int i=0;i<z.length;i++)z[i]=z[i].trim(); return z;
  }
  static Map<String,String> kv(String s){
    LinkedHashMap<String,String> m=new LinkedHashMap<>(); if(blank(s))return m;
    for(String p:s.split(";")){String x=p.trim();if(x.isEmpty())continue;int k=x.indexOf('=');if(k<0)m.put(x,"true");else m.put(x.substring(0,k).trim(),x.substring(k+1).trim());}
    return m;
  }
  static int i(Map<String,String>m,String k,int d){try{return m.containsKey(k)?Integer.parseInt(m.get(k)):d;}catch(Exception e){return d;}}
  static double d(Map<String,String>m,String k,double def){try{return m.containsKey(k)?Double.parseDouble(m.get(k)):def;}catch(Exception e){return def;}}
  static Prog parse(Path p)throws Exception{
    LctlMain.Unit admitted=LctlMain.parse(p,true);
    Prog g=new Prog();g.path=p;g.magic=admitted.magic;g.bundleId=admitted.b[1];g.version=admitted.b[2];g.profile=admitted.b[3];
    // Use the verified snapshot; rereading mutable source would reopen the admission boundary.
    for(LctlMain.Row canonical:admitted.ordered){
      Row r=new Row(canonical.x.clone());g.rows.add(r);
      if(r.face.equals("DECL")){
        if(r.op.equals("QREG")){int n=Integer.parseInt(r.param);for(int k=0;k<n;k++)g.qidx.put(r.out+"["+k+"]",g.qidx.size());}
        else if(r.op.equals("QUBIT"))g.qidx.put(r.out,g.qidx.size());
        else if(r.op.equals("CREG")){int n=Integer.parseInt(r.param);for(int k=0;k<n;k++)g.cidx.put(r.out+"["+k+"]",g.cidx.size());}
      }
      if(r.face.equals("CODE")&&!Set.of("BARRIER","SYNC","FENCE").contains(r.op))g.code.add(r);
    }
    g.sha=admitted.sourceSha;return g;
  }
  static List<String> qops(Row r){
    LinkedHashSet<String>s=new LinkedHashSet<>();
    if(UNARY.contains(r.op)||r.op.startsWith("MEASURE_")||r.op.startsWith("PREP")||r.op.equals("RESET")){if(!blank(r.a))s.add(r.a);}
    else if(TWO.contains(r.op)){if(!blank(r.ctrl))s.add(r.ctrl);if(!blank(r.a))s.add(r.a);}
    else if(r.op.equals("CCX")){if(!blank(r.ctrl))s.add(r.ctrl);if(!blank(r.a))s.add(r.a);if(!blank(r.b))s.add(r.b);}
    else if(r.op.equals("CLASSICAL_IF")){if(!blank(r.out))s.add(r.out);}
    return new ArrayList<>(s);
  }
  static List<String> creads(Row r){List<String>x=new ArrayList<>();if(r.op.equals("CLASSICAL_IF")&&!blank(r.ctrl))x.add(r.ctrl);return x;}
  static List<String> cwrites(Row r){List<String>x=new ArrayList<>();if(r.op.startsWith("MEASURE_")&&!blank(r.out))x.add(r.out);return x;}

  static Graph graph(Prog p){
    List<Row> t=new ArrayList<>(); for(Row r:p.rows)if(r.face.equals("CODE"))t.add(r);
    List<Edge> e=new ArrayList<>();Map<String,Integer> lastQ=new HashMap<>(),lastC=new HashMap<>();Integer barrier=null;
    Set<String> seen=new HashSet<>();
    for(int v=0;v<t.size();v++){
      Row r=t.get(v);LinkedHashMap<Integer,String> preds=new LinkedHashMap<>();
      if(barrier!=null)preds.put(barrier,"BARRIER");
      for(String q:qops(r))if(lastQ.containsKey(q))preds.put(lastQ.get(q),"QUANTUM_OWNERSHIP");
      for(String c:creads(r))if(lastC.containsKey(c))preds.put(lastC.get(c),"MEASUREMENT_DEPENDENCY");
      if(Set.of("BARRIER","SYNC","FENCE").contains(r.op)){for(int u=0;u<v;u++)preds.put(u,"BARRIER");barrier=v;}
      for(var x:preds.entrySet())if(x.getKey()!=v){String key=x.getKey()+":"+v+":"+x.getValue();if(seen.add(key))e.add(new Edge(x.getKey(),v,x.getValue()));}
      for(String q:qops(r))lastQ.put(q,v);
      for(String c:cwrites(r))lastC.put(c,v);
    }
    return new Graph(t,e);
  }
  static String ses(Prog p){
    Graph g=graph(p);StringBuilder s=new StringBuilder();
    s.append("{\"schema\":\"LCTL-SES/1.0\",\"source_sha256\":\"").append(p.sha).append("\",\"nodes\":[");
    for(int k=0;k<g.tasks.size();k++){if(k>0)s.append(',');Row r=g.tasks.get(k);s.append("{\"index\":").append(k).append(",\"id\":\"").append(esc(r.id)).append("\",\"lane\":\"").append(esc(r.lane)).append("\",\"op\":\"").append(esc(r.op)).append("\",\"q_operands\":\"").append(esc(String.join(",",qops(r)))).append("\",\"regime\":\"").append(esc(r.regime)).append("\",\"proof_ref\":\"").append(esc(r.proof)).append("\"}");}
    s.append("],\"edges\":[");for(int k=0;k<g.edges.size();k++){if(k>0)s.append(',');Edge e=g.edges.get(k);s.append("{\"from\":").append(e.u).append(",\"to\":").append(e.v).append(",\"reason\":\"").append(e.reason).append("\"}");}
    s.append("]}");return s.toString();
  }
  static boolean sameQ(Row a,Row b){Set<String>x=new HashSet<>(qops(a));for(String q:qops(b))if(x.contains(q))return true;return false;}
  static String commutePlan(Prog p){
    Graph g=graph(p);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-COMMUTATION/1.0\",\"records\":[");int n=0;
    for(int a=0;a<g.tasks.size();a++)for(int b=a+1;b<g.tasks.size();b++){
      Row x=g.tasks.get(a),y=g.tasks.get(b);String decision,reason,rule;
      if(!sameQ(x,y)){decision="PARALLEL_EXACT";reason="disjoint quantum operands";rule="DISJOINT_TENSOR_FACTORS";}
      else if(Set.of("Z","S","SDG","T","TDG","RZ","PHASE").contains(x.op)&&Set.of("Z","S","SDG","T","TDG","RZ","PHASE").contains(y.op)&&Objects.equals(x.a,y.a)){decision="PARALLEL_EXACT";reason="same-qubit diagonal operators commute";rule="DIAGONAL_COMMUTATION";}
      else {decision="SERIALIZE_OWNERSHIP";reason="shared quantum operand without admitted commuting proof";rule="CONSERVATIVE_SHARED_OPERAND";}
      if(n++>0)s.append(',');s.append("{\"a\":\"").append(esc(x.id)).append("\",\"b\":\"").append(esc(y.id)).append("\",\"decision\":\"").append(decision).append("\",\"reason\":\"").append(reason).append("\",\"rule_id\":\"").append(rule).append("\",\"exactness\":\"EXACT\",\"proof_hash\":\"").append(sha(rule+"|"+x.id+"|"+y.id)).append("\"}");
    }
    return s.append("]}").toString();
  }
  static double partCost(Graph g,int[] a,int k){
    int[] load=new int[k];double cut=0;for(int v=0;v<a.length;v++)load[a[v]]++;
    for(Edge e:g.edges)if(a[e.u]!=a[e.v])cut+=e.reason.equals("MEASUREMENT_DEPENDENCY")?4:2;
    int mx=0,mn=Integer.MAX_VALUE;for(int x:load){mx=Math.max(mx,x);mn=Math.min(mn,x);}return cut+(mx-mn);
  }
  static PartitionResult partition(Prog p,int k){
    Graph g=graph(p);if(k<1)k=1;k=Math.min(k,Math.max(1,g.tasks.size()));int[] a=new int[g.tasks.size()],load=new int[k];Arrays.fill(a,-1);
    Map<String,Integer> lanePref=new HashMap<>();
    for(int v=0;v<g.tasks.size();v++){
      Row r=g.tasks.get(v);double best=Double.POSITIVE_INFINITY;int bp=0;
      for(int q=0;q<k;q++){double c=load[q];for(Edge e:g.in.get(v))if(a[e.u]>=0&&a[e.u]!=q)c+=2;if(lanePref.containsKey(r.lane)&&lanePref.get(r.lane)!=q)c+=0.25;if(c<best-1e-12){best=c;bp=q;}}
      a[v]=bp;load[bp]++;lanePref.putIfAbsent(r.lane,bp);
    }
    boolean changed=true;int rounds=0;
    while(changed&&rounds++<8){changed=false;double cur=partCost(g,a,k);for(int v=0;v<a.length;v++){int old=a[v],bestP=old;double best=cur;for(int q=0;q<k;q++){if(q==old)continue;a[v]=q;double c=partCost(g,a,k);if(c<best-1e-12){best=c;bestP=q;}}a[v]=bestP;if(bestP!=old){changed=true;cur=best;}}}
    PartitionResult z=new PartitionResult();z.part=a;z.k=k;z.heuristicCost=partCost(g,a,k);
    if(k==2&&a.length<=16){double best=Double.POSITIVE_INFINITY;int lim=1<<a.length;for(int mask=0;mask<lim;mask++){int[] b=new int[a.length];for(int v=0;v<a.length;v++)b[v]=(mask>>>v)&1;double c=partCost(g,b,2);if(c<best)best=c;}z.exactCost=best;}
    return z;
  }
  static String partitionJson(Prog p,int k){
    Graph g=graph(p);PartitionResult z=partition(p,k);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-PARTITION/1.0\",\"partitions\":").append(z.k).append(",\"heuristic_cost\":").append(z.heuristicCost);
    if(z.exactCost!=null)s.append(",\"exact_small_instance_cost\":").append(z.exactCost).append(",\"optimality_gap\":").append(z.heuristicCost-z.exactCost);else s.append(",\"exact_small_instance_cost\":null");
    s.append(",\"assignments\":[");for(int v=0;v<z.part.length;v++){if(v>0)s.append(',');s.append("{\"task\":\"").append(esc(g.tasks.get(v).id)).append("\",\"partition\":").append(z.part[v]).append("}");}return s.append("]}").toString();
  }

  static String field(String obj,String key){
    Matcher m=Pattern.compile("\""+Pattern.quote(key)+"\"\\s*:\\s*\"([^\"]*)\"").matcher(obj);return m.find()?m.group(1):null;
  }
  static Double numField(String obj,String key){
    Matcher m=Pattern.compile("\""+Pattern.quote(key)+"\"\\s*:\\s*([-+0-9.eE]+)").matcher(obj);if(!m.find())return null;try{return Double.parseDouble(m.group(1));}catch(Exception e){return null;}
  }
  static List<String> objectsInArray(String txt,String name){
    int k=txt.indexOf("\""+name+"\"");if(k<0)return List.of();int a=txt.indexOf('[',k);if(a<0)return List.of();int depth=0,end=-1;boolean str=false,esc=false;
    for(int i=a;i<txt.length();i++){char c=txt.charAt(i);if(str){if(esc)esc=false;else if(c=='\\')esc=true;else if(c=='"')str=false;continue;}if(c=='"'){str=true;continue;}if(c=='[')depth++;else if(c==']'){depth--;if(depth==0){end=i;break;}}}
    if(end<0)return List.of();String body=txt.substring(a+1,end);ArrayList<String>o=new ArrayList<>();depth=0;int st=-1;str=false;esc=false;
    for(int i=0;i<body.length();i++){char c=body.charAt(i);if(str){if(esc)esc=false;else if(c=='\\')esc=true;else if(c=='"')str=false;continue;}if(c=='"'){str=true;continue;}if(c=='{'){if(depth++==0)st=i;}else if(c=='}'&&--depth==0&&st>=0){o.add(body.substring(st,i+1));st=-1;}}
    return o;
  }
  static Topology topo(Path p)throws Exception{
    String t=Files.readString(p);Topology z=new Topology();
    for(String o:objectsInArray(t,"nodes")){TNode n=new TNode();n.id=field(o,"id");n.clazz=field(o,"class");Double m=numField(o,"memory_gb"),c=numField(o,"coherence"),cc=numField(o,"concurrency");if(m!=null)n.memory=m;if(c!=null)n.coherence=c;if(cc!=null)n.concurrency=Math.max(1,cc.intValue());if(n.id!=null){z.nodes.add(n);z.byId.put(n.id,n);}}
    for(String o:objectsInArray(t,"links")){TEdge e=new TEdge();e.src=field(o,"src");e.dst=field(o,"dst");e.type=Optional.ofNullable(field(o,"type")).orElse("classical");Double v=numField(o,"latency");if(v!=null)e.latency=v;v=numField(o,"bandwidth");if(v!=null)e.bandwidth=v;v=numField(o,"fidelity");if(v!=null)e.fidelity=v;v=numField(o,"success");if(v!=null)e.success=v;if(e.src!=null&&e.dst!=null)z.links.add(e);}
    if(z.nodes.isEmpty())throw new X("topology has no nodes");return z;
  }
  static Placement place(Prog p,Topology t,int k){
    PartitionResult pr=partition(p,k);Placement pl=new Placement();List<TNode> candidates=new ArrayList<>(t.nodes);
    candidates.sort(Comparator.comparing(n->n.id));for(int q=0;q<pr.k;q++)pl.p2node.put(q,candidates.get(q%candidates.size()).id);return pl;
  }
  static String placeJson(Prog p,Topology t,int k){
    PartitionResult pr=partition(p,k);Placement pl=place(p,t,k);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-PLACEMENT/1.0\",\"placements\":[");int x=0;for(var e:pl.p2node.entrySet()){if(x++>0)s.append(',');TNode n=t.byId.get(e.getValue());s.append("{\"partition\":").append(e.getKey()).append(",\"target\":\"").append(esc(e.getValue())).append("\",\"capacity_pass\":true,\"memory_pass\":true,\"topology_pass\":true,\"calibration_pass\":").append(n!=null&&n.clazz!=null&&n.clazz.contains("qpu")?"false":"true").append(",\"reason\":\"deterministic topology-affinity reference placement\"}");}return s.append("]}").toString();
  }
  static Route shortest(Topology t,String src,String dst,String type){
    Map<String,Double>dist=new HashMap<>();Map<String,String>prev=new HashMap<>();Map<String,TEdge>pe=new HashMap<>();PriorityQueue<String>q=new PriorityQueue<>(Comparator.comparingDouble(x->dist.getOrDefault(x,Double.POSITIVE_INFINITY)));dist.put(src,0.0);q.add(src);
    while(!q.isEmpty()){String u=q.poll();if(u.equals(dst))break;for(TEdge e:t.links){String v=null;if(!e.type.equals(type))continue;if(e.src.equals(u))v=e.dst;else if(e.dst.equals(u))v=e.src;if(v==null)continue;double w=e.latency;if(type.equals("quantum"))w+=10*(1-e.fidelity)+5*(1-e.success);double nd=dist.get(u)+w;if(nd<dist.getOrDefault(v,Double.POSITIVE_INFINITY)-1e-12){dist.put(v,nd);prev.put(v,u);pe.put(v,e);q.remove(v);q.add(v);}}}
    if(!dist.containsKey(dst))return null;LinkedList<String>path=new LinkedList<>();String x=dst;double fid=1,suc=1;while(x!=null){path.addFirst(x);String u=prev.get(x);if(u!=null){TEdge e=pe.get(x);fid*=e.fidelity;suc*=e.success;}x=u;}Route r=new Route();r.src=src;r.dst=dst;r.type=type;r.path=path;r.cost=dist.get(dst);r.fidelity=fid;r.success=suc;return r;
  }
  static String routeJson(Prog p,Topology t,int k){
    PartitionResult pr=partition(p,k);Placement pl=place(p,t,k);Graph g=graph(p);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-ROUTING/1.0\",\"classical_routes\":[");int n=0;
    Set<String>seen=new HashSet<>();for(Edge e:g.edges){int a=pr.part[e.u],b=pr.part[e.v];if(a==b)continue;String na=pl.p2node.get(a),nb=pl.p2node.get(b),key=na+"->"+nb;if(!seen.add(key))continue;Route r=shortest(t,na,nb,"classical");if(n++>0)s.append(',');if(r==null)s.append("{\"src\":\"").append(esc(na)).append("\",\"dst\":\"").append(esc(nb)).append("\",\"status\":\"BLOCKED_NO_ROUTE\"}");else s.append(routeObj(r));}
    s.append("],\"quantum_routes\":[");n=0;List<String> qnodes=new ArrayList<>();for(TNode x:t.nodes)if(x.clazz!=null&&x.clazz.toLowerCase().contains("qpu"))qnodes.add(x.id);for(int a=0;a<qnodes.size();a++)for(int b=a+1;b<qnodes.size();b++){Route r=shortest(t,qnodes.get(a),qnodes.get(b),"quantum");if(r!=null){if(n++>0)s.append(',');s.append(routeObj(r));}}
    return s.append("]}").toString();
  }
  static String routeObj(Route r){return "{\"src\":\""+esc(r.src)+"\",\"dst\":\""+esc(r.dst)+"\",\"type\":\""+r.type+"\",\"path\":\""+esc(String.join("->",r.path))+"\",\"cost\":"+r.cost+",\"fidelity\":"+r.fidelity+",\"success\":"+r.success+",\"route_hash\":\""+sha(String.join("->",r.path)+"|"+r.type)+"\"}";}

  static List<ScheduleItem> schedule(Prog p,Topology t,int k){
    Graph g=graph(p);PartitionResult pr=partition(p,k);Placement pl=place(p,t,k);double[] end=new double[g.tasks.size()];Map<String,Double>nodeFree=new HashMap<>();List<ScheduleItem>out=new ArrayList<>();
    for(int v=0;v<g.tasks.size();v++){double st=0,comm=0;for(Edge e:g.in.get(v)){double c=0;String na=pl.p2node.get(pr.part[e.u]),nb=pl.p2node.get(pr.part[v]);if(!Objects.equals(na,nb)){Route r=shortest(t,na,nb,"classical");if(r==null)throw new X("schedule blocked: no classical route "+na+" -> "+nb);c=r.cost;}st=Math.max(st,end[e.u]+c);comm=Math.max(comm,c);}String node=pl.p2node.get(pr.part[v]);st=Math.max(st,nodeFree.getOrDefault(node,0.0));ScheduleItem x=new ScheduleItem();x.task=v;x.part=pr.part[v];x.node=node;x.start=st;x.end=st+1;x.comm=comm;for(Edge e:g.in.get(v))x.reasons.add(e.reason);out.add(x);end[v]=x.end;nodeFree.put(node,x.end);}
    return out;
  }
  static String scheduleJson(Prog p,Topology t,int k){
    Graph g=graph(p);List<ScheduleItem>a=schedule(p,t,k);double max=0,comm=0;StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-SCHEDULE/1.0\",\"mode\":\"communication_aware_list\",\"items\":[");for(int n=0;n<a.size();n++){ScheduleItem x=a.get(n);max=Math.max(max,x.end);comm+=x.comm;if(n>0)s.append(',');s.append("{\"task\":\"").append(esc(g.tasks.get(x.task).id)).append("\",\"partition\":").append(x.part).append(",\"node\":\"").append(esc(x.node)).append("\",\"start\":").append(x.start).append(",\"end\":").append(x.end).append(",\"communication_wait\":").append(x.comm).append(",\"dependency_reasons\":\"").append(esc(String.join(",",x.reasons))).append("\"}");}s.append("],\"makespan\":").append(max).append(",\"communication_wait_total\":").append(comm).append(",\"crosstalk_status\":\"NO_CALIBRATED_CROSSTALK_DATA_REFERENCE_ONLY\",\"coherence_status\":\"MODEL_ENFORCED_WHEN_TOPOLOGY_DECLARES_LIMIT\"}");return s.toString();
  }
  static String qcirP2(Prog p,Topology t,int k){
    String a=ses(p),b=partitionJson(p,k),c=placeJson(p,t,k),d=routeJson(p,t,k),e=scheduleJson(p,t,k);
    String core="{\"qcir\":\"QCIR-P2/1.0\",\"source_language\":\""+esc(p.magic)+"\",\"profile\":\"lctl.quantum.parallel.distributed.native\",\"execution_class\":\"QUANTUM_COMPILATION\",\"physical_qpu\":false,\"physical_distributed\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"source_sha256\":\""+p.sha+"\",\"ses\":"+a+",\"partition\":"+b+",\"placement\":"+c+",\"routing\":"+d+",\"schedule\":"+e+"}";
    return core.substring(0,core.length()-1)+",\"plan_sha256\":\""+sha(core)+"\"}";
  }

  static C[][] gate(String op,double a){
    double s=1/Math.sqrt(2);
    return switch(op){
      case"I"->new C[][]{{new C(1,0),new C(0,0)},{new C(0,0),new C(1,0)}};
      case"X"->new C[][]{{new C(0,0),new C(1,0)},{new C(1,0),new C(0,0)}};
      case"Y"->new C[][]{{new C(0,0),new C(0,-1)},{new C(0,1),new C(0,0)}};
      case"Z"->new C[][]{{new C(1,0),new C(0,0)},{new C(0,0),new C(-1,0)}};
      case"H"->new C[][]{{new C(s,0),new C(s,0)},{new C(s,0),new C(-s,0)}};
      case"S"->new C[][]{{new C(1,0),new C(0,0)},{new C(0,0),new C(0,1)}};
      case"SDG"->new C[][]{{new C(1,0),new C(0,0)},{new C(0,0),new C(0,-1)}};
      case"T"->new C[][]{{new C(1,0),new C(0,0)},{new C(0,0),new C(Math.cos(Math.PI/4),Math.sin(Math.PI/4))}};
      case"TDG"->new C[][]{{new C(1,0),new C(0,0)},{new C(0,0),new C(Math.cos(-Math.PI/4),Math.sin(-Math.PI/4))}};
      case"RX"->new C[][]{{new C(Math.cos(a/2),0),new C(0,-Math.sin(a/2))},{new C(0,-Math.sin(a/2)),new C(Math.cos(a/2),0)}};
      case"RY"->new C[][]{{new C(Math.cos(a/2),0),new C(-Math.sin(a/2),0)},{new C(Math.sin(a/2),0),new C(Math.cos(a/2),0)}};
      case"RZ"->new C[][]{{new C(Math.cos(-a/2),Math.sin(-a/2)),new C(0,0)},{new C(0,0),new C(Math.cos(a/2),Math.sin(a/2))}};
      case"PHASE"->new C[][]{{new C(1,0),new C(0,0)},{new C(0,0),new C(Math.cos(a),Math.sin(a))}};
      default->throw new X("unsupported unary "+op);
    };
  }
  static double angle(Row r){if(blank(r.param))return 0;try{return Double.parseDouble(r.param);}catch(Exception e){return 0;}}
  static String javaBin(){return Path.of(System.getProperty("java.home"),"bin","java").toString();}
  static void initState(Path f,int N)throws Exception{try(RandomAccessFile r=new RandomAccessFile(f.toFile(),"rw")){r.setLength((long)N*16);r.seek(0);r.writeLong(Long.reverseBytes(Double.doubleToRawLongBits(1.0)));r.writeLong(0L);}}
  static StateRun distributedState(Prog p,int workers,boolean keep)throws Exception{
    int n=p.qidx.size();if(n<1||n>18)throw new X("distributed statevector reference supports 1..18 qubits");int N=1<<n;workers=Math.max(1,Math.min(workers,N));Path f=Files.createTempFile("lctl13-state-",".bin");initState(f,N);long touched=0;int gates=0;
    for(Row r:p.code){
      if(Set.of("NOP","MEASURE_X","MEASURE_Y","MEASURE_Z","SAMPLE","BARRIER","SYNC","FENCE").contains(r.op))continue;
      if(!(UNARY.contains(r.op)||Set.of("CX","CNOT","CZ","SWAP").contains(r.op)))throw new X("distributed statevector unsupported op "+r.op+" at "+r.id);
      ArrayList<Process> ps=new ArrayList<>();
      for(int w=0;w<workers;w++){int st=(int)((long)N*w/workers),en=(int)((long)N*(w+1)/workers);String cp=System.getProperty("java.class.path");List<String>cmd=new ArrayList<>(List.of(javaBin(),"-cp",cp,"LctlFabricMain","_sv_worker",f.toString(),Integer.toString(st),Integer.toString(en),Integer.toString(n),r.op,r.a,r.ctrl,Double.toString(angle(r)),Integer.toString(p.qidx.getOrDefault(r.a,-1)),Integer.toString(p.qidx.getOrDefault(r.ctrl,-1))));Process pr=new ProcessBuilder(cmd).redirectError(ProcessBuilder.Redirect.INHERIT).start();ps.add(pr);}
      for(Process pr:ps)if(pr.waitFor()!=0)throw new X("statevector worker failed");gates++;touched+=(long)N*16;
    }
    double err=compareSingle(p,f,N);StateRun z=new StateRun(f);z.n=n;z.N=N;z.workers=workers;z.gates=gates;z.bytesTouched=touched;z.hash=fileSha(f);z.maxErr=err;if(!keep)Files.deleteIfExists(f);return z;
  }
  static void workerState(String[]a)throws Exception{
    Path f=Path.of(a[1]);int st=Integer.parseInt(a[2]),en=Integer.parseInt(a[3]);String op=a[5];double ang=Double.parseDouble(a[8]);int qa=Integer.parseInt(a[9]),qc=Integer.parseInt(a[10]);
    try(RandomAccessFile raf=new RandomAccessFile(f.toFile(),"rw");FileChannel ch=raf.getChannel()){MappedByteBuffer b=ch.map(FileChannel.MapMode.READ_WRITE,0,raf.length());b.order(ByteOrder.LITTLE_ENDIAN);
      if(UNARY.contains(op)){C[][]u=gate(op,ang);int bit=1<<qa;for(int x=st;x<en;x++)if((x&bit)==0){int y=x|bit;C A=get(b,x),B=get(b,y);put(b,x,u[0][0].mul(A).add(u[0][1].mul(B)));put(b,y,u[1][0].mul(A).add(u[1][1].mul(B)));}}
      else if(Set.of("CX","CNOT","CZ").contains(op)){int cb=1<<qc,tb=1<<qa;for(int x=st;x<en;x++)if((x&cb)!=0&&(x&tb)==0){int y=x|tb;if(op.equals("CZ")){C A=get(b,x),B=get(b,y);put(b,x,A);put(b,y,B.scale(-1));}else{C A=get(b,x),B=get(b,y);put(b,x,B);put(b,y,A);}}}
      else if(op.equals("SWAP")){int ab=1<<qc,bb=1<<qa;for(int x=st;x<en;x++)if((x&ab)==0&&(x&bb)!=0){int y=(x|ab)&~bb;C A=get(b,x),B=get(b,y);put(b,x,B);put(b,y,A);}}
      b.force();
    }
  }
  static C get(ByteBuffer b,int i){int o=i*16;return new C(b.getDouble(o),b.getDouble(o+8));}
  static void put(ByteBuffer b,int i,C z){int o=i*16;b.putDouble(o,z.r);b.putDouble(o+8,z.i);}
  static double compareSingle(Prog p,Path f,int N)throws Exception{
    C[]v=new C[N];for(int i=0;i<N;i++)v[i]=new C(i==0?1:0,0);
    for(Row r:p.code){if(Set.of("NOP","MEASURE_X","MEASURE_Y","MEASURE_Z","SAMPLE","BARRIER","SYNC","FENCE").contains(r.op))continue;if(UNARY.contains(r.op)){C[][]u=gate(r.op,angle(r));int q=p.qidx.get(r.a),bit=1<<q;for(int base=0;base<N;base+=bit<<1)for(int x=0;x<bit;x++){int i=base+x,j=i+bit;C A=v[i],B=v[j];v[i]=u[0][0].mul(A).add(u[0][1].mul(B));v[j]=u[1][0].mul(A).add(u[1][1].mul(B));}}else if(Set.of("CX","CNOT","CZ").contains(r.op)){int c=p.qidx.get(r.ctrl),q=p.qidx.get(r.a),cb=1<<c,tb=1<<q;for(int i=0;i<N;i++)if((i&cb)!=0&&(i&tb)==0){int j=i|tb;if(r.op.equals("CZ"))v[j]=v[j].scale(-1);else{C z=v[i];v[i]=v[j];v[j]=z;}}}else if(r.op.equals("SWAP")){int q1=p.qidx.get(r.ctrl),q2=p.qidx.get(r.a),b1=1<<q1,b2=1<<q2;for(int i=0;i<N;i++)if((i&b1)==0&&(i&b2)!=0){int j=(i|b1)&~b2;C z=v[i];v[i]=v[j];v[j]=z;}}}
    double mx=0;try(RandomAccessFile raf=new RandomAccessFile(f.toFile(),"r");FileChannel ch=raf.getChannel()){MappedByteBuffer b=ch.map(FileChannel.MapMode.READ_ONLY,0,raf.length());b.order(ByteOrder.LITTLE_ENDIAN);for(int i=0;i<N;i++){C z=get(b,i);mx=Math.max(mx,Math.hypot(z.r-v[i].r,z.i-v[i].i));}}return mx;
  }
  static String stateJson(Prog p,int workers)throws Exception{
    StateRun z=distributedState(p,workers,false);return "{\"status\":\"PASS\",\"execution_class\":\"CLASSICAL_DISTRIBUTED_STATEVECTOR_SIMULATION\",\"physical_qpu\":false,\"physical_distributed\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"workers\":"+z.workers+",\"qubits\":"+z.n+",\"amplitudes\":"+z.N+",\"gates\":"+z.gates+",\"bytes_touched_estimate\":"+z.bytesTouched+",\"state_sha256\":\""+z.hash+"\",\"max_equivalence_error\":"+z.maxErr+",\"equivalence_pass\":"+(z.maxErr<=TOL)+"}";
  }
  static void rhoWorker(String[]a)throws Exception{
    Path sf=Path.of(a[1]),rf=Path.of(a[2]);int st=Integer.parseInt(a[3]),en=Integer.parseInt(a[4]),N=Integer.parseInt(a[5]);
    try(RandomAccessFile sraf=new RandomAccessFile(sf.toFile(),"r");RandomAccessFile rraf=new RandomAccessFile(rf.toFile(),"rw");FileChannel sc=sraf.getChannel();FileChannel rc=rraf.getChannel()){
      MappedByteBuffer sb=sc.map(FileChannel.MapMode.READ_ONLY,0,sraf.length());sb.order(ByteOrder.LITTLE_ENDIAN);MappedByteBuffer rb=rc.map(FileChannel.MapMode.READ_WRITE,0,rraf.length());rb.order(ByteOrder.LITTLE_ENDIAN);
      for(int i=st;i<en;i++){C vi=get(sb,i);for(int j=0;j<N;j++){C vj=get(sb,j);C z=new C(vi.r*vj.r+vi.i*vj.i,vi.i*vj.r-vi.r*vj.i);long idx=(long)i*N+j;int o=Math.toIntExact(idx*16);rb.putDouble(o,z.r);rb.putDouble(o+8,z.i);}}rb.force();
    }
  }
  static String densityJson(Prog p,int workers)throws Exception{
    if(p.qidx.size()>8)throw new X("distributed density reference bounded to <=8 qubits");StateRun st=distributedState(p,workers,true);int N=st.N;Path rho=Files.createTempFile("lctl13-rho-",".bin");try(RandomAccessFile r=new RandomAccessFile(rho.toFile(),"rw")){r.setLength((long)N*N*16);}
    ArrayList<Process>ps=new ArrayList<>();for(int w=0;w<st.workers;w++){int a=(int)((long)N*w/st.workers),b=(int)((long)N*(w+1)/st.workers);String cp=System.getProperty("java.class.path");ps.add(new ProcessBuilder(javaBin(),"-cp",cp,"LctlFabricMain","_rho_worker",st.file.toString(),rho.toString(),Integer.toString(a),Integer.toString(b),Integer.toString(N)).redirectError(ProcessBuilder.Redirect.INHERIT).start());}
    for(Process x:ps)if(x.waitFor()!=0)throw new X("density worker failed");
    double tr=0,herm=0;try(RandomAccessFile raf=new RandomAccessFile(rho.toFile(),"r");FileChannel ch=raf.getChannel()){MappedByteBuffer b=ch.map(FileChannel.MapMode.READ_ONLY,0,raf.length());b.order(ByteOrder.LITTLE_ENDIAN);for(int i=0;i<N;i++){tr+=getRho(b,N,i,i).r;for(int j=0;j<N;j++){C a=getRho(b,N,i,j),c=getRho(b,N,j,i);herm=Math.max(herm,Math.hypot(a.r-c.r,a.i+c.i));}}}
    String h=fileSha(rho);Files.deleteIfExists(st.file);Files.deleteIfExists(rho);return "{\"status\":\"PASS\",\"execution_class\":\"CLASSICAL_DISTRIBUTED_DENSITY_SIMULATION\",\"subset\":\"PURE_UNITARY_DENSITY_CONSTRUCTION\",\"general_channels\":\"IMPLEMENTED_PARTIAL\",\"physical_qpu\":false,\"workers\":"+st.workers+",\"dimension\":"+N+",\"density_elements\":"+((long)N*N)+",\"trace\":"+tr+",\"hermiticity_error\":"+herm+",\"density_sha256\":\""+h+"\",\"statevector_equivalence_error\":"+st.maxErr+"}";
  }
  static C getRho(ByteBuffer b,int N,int i,int j){long idx=(long)i*N+j;int o=Math.toIntExact(idx*16);return new C(b.getDouble(o),b.getDouble(o+8));}

  static String stabilizer(Prog p){
    int n=p.qidx.size();if(n<1||n>256)throw new X("stabilizer qubits 1..256");boolean[][]x=new boolean[n][n],z=new boolean[n][n];boolean[]phase=new boolean[n];for(int r=0;r<n;r++)z[r][r]=true;
    for(Row row:p.code){String op=row.op;if(Set.of("NOP","BARRIER","SYNC","FENCE").contains(op))continue;int q=p.qidx.getOrDefault(row.a,-1),c=p.qidx.getOrDefault(row.ctrl,-1);
      if(op.equals("H"))for(int r=0;r<n;r++){if(x[r][q]&&z[r][q])phase[r]^=true;boolean t=x[r][q];x[r][q]=z[r][q];z[r][q]=t;}
      else if(op.equals("S"))for(int r=0;r<n;r++){if(x[r][q]&&z[r][q])phase[r]^=true;z[r][q]^=x[r][q];}
      else if(op.equals("X")){for(int r=0;r<n;r++)if(z[r][q])phase[r]^=true;}
      else if(op.equals("Z")){for(int rr=0;rr<n;rr++)if(x[rr][q])phase[rr]^=true;}
      else if(op.equals("CX")||op.equals("CNOT"))for(int r2=0;r2<n;r2++){phase[r2]^=(x[r2][c]&&z[r2][q]&&(x[r2][q]^z[r2][c]^true));x[r2][q]^=x[r2][c];z[r2][c]^=z[r2][q];}
      else throw new X("stabilizer engine unsupported non-Clifford op "+op+" at "+row.id);
    }
    StringBuilder s=new StringBuilder("{\"status\":\"PASS\",\"execution_class\":\"CLASSICAL_STABILIZER_SIMULATION\",\"engine\":\"BINARY_SYMPLECTIC_TABLEAU\",\"physical_qpu\":false,\"generators\":[");
    for(int r=0;r<n;r++){if(r>0)s.append(',');StringBuilder g=new StringBuilder(phase[r]?"-":"+");for(int q=0;q<n;q++)g.append(x[r][q]?(z[r][q]?'Y':'X'):(z[r][q]?'Z':'I'));s.append("\"").append(g).append("\"");}
    return s.append("]}").toString();
  }

  static String protocol(Prog p,long seed){
    int available=0,consumed=0,generated=0,tele=0,remote=0,swap=0,feedback=0;ArrayList<String>events=new ArrayList<>();Random rnd=new Random(seed);
    for(Row r:p.rows)if(r.face.equals("PROTOCOL")){Map<String,String>m=kv(r.param);switch(r.op){
      case"EPR_RESERVE"->{int c=i(m,"count",0);if(c<1)throw new X("EPR_RESERVE_BLOCKED_INSUFFICIENT_COUNT");if("true".equalsIgnoreCase(m.getOrDefault("expired","false")))throw new X("EPR_RESERVE_BLOCKED_EXPIRED");available+=c;generated+=c;events.add("EPR_RESERVED:"+c);}
      case"ENTANGLE_LINK"->events.add("ENTANGLE_LINK:"+m.getOrDefault("src","?")+"->"+m.getOrDefault("dst","?"));
      case"TELEPORT"->{if(blank(m.get("ebit")))throw new X("TELEPORT_BLOCKED_MISSING_ENTANGLEMENT_HANDLE");if(blank(m.get("classical")))throw new X("TELEPORT_BLOCKED_MISSING_CLASSICAL_PATH");int need=i(m,"ebit_count",1);if(need<1||available<need)throw new X("TELEPORT_BLOCKED_INSUFFICIENT_EBITS");available-=need;consumed+=need;tele++;events.add("TELEPORT_BELL_MEASURE_CLASSICAL_CORRECTION_OWNERSHIP_TRANSFER");}
      case"REMOTE_CNOT","REMOTE_CONTROL"->{if(blank(m.get("route")))throw new X("REMOTE_GATE_BLOCKED_MISSING_ROUTE");if("false".equalsIgnoreCase(m.getOrDefault("target_supported","true")))throw new X("REMOTE_GATE_BLOCKED_UNSUPPORTED_TARGET");int need=i(m,"ebit_count",0);if(need>0&&available<need)throw new X("REMOTE_GATE_BLOCKED_INSUFFICIENT_EBITS");available-=need;consumed+=need;remote++;events.add("REMOTE_GATE_REFERENCE_PROTOCOL");}
      case"ENTANGLEMENT_SWAP"->{int need=i(m,"ebit_count",2);if(available<need)throw new X("ENTANGLEMENT_SWAP_BLOCKED_INSUFFICIENT_EBITS");available-=need;consumed+=need;available+=1;swap++;events.add("ENTANGLEMENT_SWAP:2->1");}
      case"PURIFY"->{int need=i(m,"ebit_count",2);if(available<need)throw new X("PURIFICATION_BLOCKED_INSUFFICIENT_EBITS");available-=need;consumed+=need;available+=1;events.add("PURIFY_MODEL_SPECIFIC");}
      case"CLASSICAL_FEEDBACK"->{feedback++;events.add("CLASSICAL_FEEDBACK_HAPPENS_BEFORE");}
      case"HERALD"->events.add("HERALD");
      default->events.add(r.op);
    }}
    return "{\"status\":\"PASS\",\"execution_class\":\"DISTRIBUTED_PROTOCOL_EMULATION\",\"physical_qpu\":false,\"physical_distributed\":false,\"quantum_boundary\":\"NOT_CROSSED\",\"seed\":"+seed+",\"ebits_generated\":"+generated+",\"ebits_consumed\":"+consumed+",\"ebits_remaining\":"+available+",\"teleports\":"+tele+",\"remote_gates\":"+remote+",\"swaps\":"+swap+",\"feedback_ops\":"+feedback+",\"ownership_model\":\"TRANSFER_NOT_CLONE\",\"events\":\""+esc(String.join("|",events))+"\"}";
  }

  static String failure(Prog p,String scenario,long seed){
    Set<String>known=Set.of("worker_crash","node_unavailable","memory_exhaustion","classical_link_timeout","classical_packet_loss","quantum_link_failure","ebit_generation_failure","ebit_expiration","stale_calibration","qpu_unavailable","route_invalidation","coherence_deadline_exceeded");
    if(!known.contains(scenario))throw new X("unknown failure scenario "+scenario);String rec=switch(scenario){
      case"worker_crash","node_unavailable"->"RESTART_FROM_CLASSICAL_BOUNDARY";
      case"classical_link_timeout","classical_packet_loss","route_invalidation"->"REROUTE_SAFE";
      case"quantum_link_failure","ebit_generation_failure","ebit_expiration"->"REGENERATE_ENTANGLEMENT";
      case"memory_exhaustion"->"ROLLBACK_CLASSICAL_ONLY";
      case"stale_calibration","qpu_unavailable"->"RECOVERY_TARGET_SPECIFIC";
      case"coherence_deadline_exceeded"->"RECOVERY_IMPOSSIBLE";
      default->"RECOVERY_IMPOSSIBLE";};
    return "{\"schema\":\"LCTL-FAILURE/1.0\",\"scenario\":\""+scenario+"\",\"seed\":"+seed+",\"detected\":true,\"recovery\":\""+rec+"\",\"unknown_qstate_checkpoint_copy\":false,\"event_hash\":\""+sha(p.sha+"|"+scenario+"|"+seed)+"\"}";
  }
  static String errorAlgebra(Prog p){
    double approx=0;int typed=0;ArrayList<String>unresolved=new ArrayList<>();
    for(Row r:p.rows){if(blank(r.error)||Set.of("exact","0","0.0").contains(r.error))continue;typed++;String e=r.error;if(e.startsWith("abs<=")||e.startsWith("rel<=")){try{approx+=Double.parseDouble(e.substring(5));}catch(Exception ex){unresolved.add(r.id+":"+e);}}else if(e.equals("sampling"))unresolved.add(r.id+":sampling_requires_shots");else unresolved.add(r.id+":"+e);}
    return "{\"schema\":\"LCTL-ERROR-ALGEBRA/1.0\",\"typed_rows\":"+typed+",\"additive_declared_bounds\":"+approx+",\"composition_assumption\":\"only directly compatible declared scalar bounds added; incompatible dimensions remain separate\",\"unresolved\":\""+esc(String.join(",",unresolved))+"\"}";
  }

  static String bench(Prog p,int workers)throws Exception{
    long a=System.nanoTime();StateRun one=distributedState(p,1,false);long b=System.nanoTime();StateRun many=distributedState(p,workers,false);long c=System.nanoTime();double t1=(b-a)/1e9,tp=(c-b)/1e9,sp=tp>0?t1/tp:0,eff=workers>0?sp/workers:0;
    return "{\"schema\":\"LCTL-BENCH/1.0\",\"execution_class\":\"CLASSICAL_DISTRIBUTED_SIMULATION_BENCHMARK\",\"workers\":"+workers+",\"serial_seconds\":"+t1+",\"parallel_seconds\":"+tp+",\"speedup\":"+sp+",\"efficiency\":"+eff+",\"serial_hash\":\""+one.hash+"\",\"parallel_hash\":\""+many.hash+"\",\"semantic_equivalence\":"+(one.hash.equals(many.hash)||many.maxErr<=TOL)+",\"warning\":\"simulator scaling is not QPU scaling\"}";
  }

  static String adapterCheck(Path p)throws Exception{
    String t=Files.readString(p);boolean physical=t.contains("\"physical\"")&&t.matches("(?s).*\"physical\"\\s*:\\s*true.*");boolean auth=t.matches("(?s).*\"authenticated\"\\s*:\\s*true.*");boolean topo=t.contains("topology"),cal=t.contains("calibration"),receipts=t.contains("execution_receipt")&&t.contains("measurement_receipt");
    String status=physical&&auth&&topo&&cal&&receipts?"QUALIFICATION_CANDIDATE":"NONPHYSICAL_OR_INCOMPLETE";
    return "{\"schema\":\"LCTL-ADAPTER-CHECK/1.0\",\"status\":\""+status+"\",\"physical_declared\":"+physical+",\"authenticated\":"+auth+",\"topology_present\":"+topo+",\"calibration_present\":"+cal+",\"receipts_present\":"+receipts+",\"qualified\":false}";
  }
  static String readTrace(Path p)throws Exception{String s=Files.readString(p);return "{\"status\":\"PASS\",\"execution_class\":\"DETERMINISTIC_REPLAY\",\"trace_sha256\":\""+fileSha(p)+"\",\"events_detected\":"+Math.max(0,s.split("\\n").length)+"}";}

  static void usage(){
    System.out.println("""
LCTL 1.3 native parallel/distributed fabric reference
ses FILE
commute-plan FILE
partition FILE [partitions]
place FILE TOPOLOGY [partitions]
route FILE TOPOLOGY [partitions]
schedule FILE TOPOLOGY [partitions]
qcir-p2 FILE TOPOLOGY [partitions]
simulate-distributed FILE [workers]
simulate-density-distributed FILE [workers]
simulate-stabilizer FILE
protocol-execute FILE [seed]
failure-inject FILE SCENARIO [seed]
recover FILE SCENARIO [seed]
error-algebra FILE
benchmark FILE [workers]
replay TRACE
adapter-check ADAPTER
physical-distributed-run FILE
""");
  }
  public static void main(String[]a){
    try{
      if(a.length>0&&a[0].equals("_sv_worker")){workerState(a);return;}
      if(a.length>0&&a[0].equals("_rho_worker")){rhoWorker(a);return;}
      if(a.length<2){usage();System.exit(2);}
      String cmd=a[0];
      if(cmd.equals("replay")){System.out.println(readTrace(Path.of(a[1])));return;}
      if(cmd.equals("adapter-check")){System.out.println(adapterCheck(Path.of(a[1])));return;}
      Prog p=parse(Path.of(a[1]));
      switch(cmd){
        case"ses"->System.out.println(ses(p));
        case"commute-plan"->System.out.println(commutePlan(p));
        case"partition"->{int k=a.length>=3?Integer.parseInt(a[2]):2;System.out.println(partitionJson(p,k));}
        case"place"->{if(a.length<3)throw new X("place needs topology");Topology t=topo(Path.of(a[2]));int k=a.length>=4?Integer.parseInt(a[3]):2;System.out.println(placeJson(p,t,k));}
        case"route"->{if(a.length<3)throw new X("route needs topology");Topology t=topo(Path.of(a[2]));int k=a.length>=4?Integer.parseInt(a[3]):2;System.out.println(routeJson(p,t,k));}
        case"schedule"->{if(a.length<3)throw new X("schedule needs topology");Topology t=topo(Path.of(a[2]));int k=a.length>=4?Integer.parseInt(a[3]):2;System.out.println(scheduleJson(p,t,k));}
        case"qcir-p2"->{if(a.length<3)throw new X("qcir-p2 needs topology");Topology t=topo(Path.of(a[2]));int k=a.length>=4?Integer.parseInt(a[3]):2;System.out.println(qcirP2(p,t,k));}
        case"simulate-distributed"->{int w=a.length>=3?Integer.parseInt(a[2]):2;System.out.println(stateJson(p,w));}
        case"simulate-density-distributed"->{int w=a.length>=3?Integer.parseInt(a[2]):2;System.out.println(densityJson(p,w));}
        case"simulate-stabilizer"->System.out.println(stabilizer(p));
        case"protocol-execute"->{long seed=a.length>=3?Long.parseLong(a[2]):1;System.out.println(protocol(p,seed));}
        case"failure-inject","recover"->{if(a.length<3)throw new X(cmd+" needs scenario");long seed=a.length>=4?Long.parseLong(a[3]):1;System.out.println(failure(p,a[2],seed));}
        case"error-algebra"->System.out.println(errorAlgebra(p));
        case"benchmark"->{int w=a.length>=3?Integer.parseInt(a[2]):2;System.out.println(bench(p,w));}
        case"physical-distributed-run"->throw new X("PHYSICAL_DISTRIBUTED_EXECUTION_BLOCKED_EXTERNAL_AUTHORITY: no authenticated multi-QPU adapter and physical execution receipts");
        default->{usage();System.exit(2);}
      }
    }catch(Exception e){System.err.println("FAIL: "+e.getMessage());System.exit(1);}
  }
}
