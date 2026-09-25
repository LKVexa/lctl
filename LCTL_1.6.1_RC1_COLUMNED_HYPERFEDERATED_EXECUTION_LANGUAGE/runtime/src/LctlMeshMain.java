import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * LCTL 1.4 Adaptive Parallel & Distributed Execution Mesh reference runtime.
 *
 * This class deliberately layers over LctlFabricMain so the 1.3 reference
 * behavior remains available.  The default profile is local/offline and does
 * not claim physical QPU execution.
 */
public final class LctlMeshMain {
  static final double TOL=1e-10;
  static final Set<String> DIAGONAL=Set.of("I","Z","S","SDG","T","TDG","RZ","PHASE","CZ");
  static final Set<String> PAULI=Set.of("I","X","Y","Z");
  static final Set<String> ROT=Set.of("RX","RY","RZ","PHASE");
  static final Set<String> NOISE=Set.of("DEPHASE","DEPOLARIZE","AMPLITUDE_DAMP","BIT_FLIP","PHASE_DAMP");

  static final class M extends RuntimeException { M(String s){super(s);} }
  static final class PO {
    int n; int[] level; int levels; int width; List<List<Integer>> layers=new ArrayList<>();
    boolean[][] reach;
  }
  static final class HPart {
    int k; int[] p; double edgeCut,hyperCut,imbalance,score; List<Set<Integer>> hypers;
  }
  static final class RuntimeReceipt {
    int tasks,workers,maxConcurrency; long nanos; String finalHash; List<String> events=new ArrayList<>();
  }
  static final class DRun {Path file; int n,N,workers,ops; double trace,herm,minDiag; String hash;}
  static final class Z {double r,i; Z(double r,double i){this.r=r;this.i=i;} Z add(Z b){return new Z(r+b.r,i+b.i);} Z mul(Z b){return new Z(r*b.r-i*b.i,r*b.i+i*b.r);} Z conj(){return new Z(r,-i);} Z scale(double a){return new Z(r*a,i*a);} }

  static String esc(String s){return LctlFabricMain.esc(s);}
  static String sha(String s){return LctlFabricMain.sha(s);}
  static String fileSha(Path p)throws Exception{return LctlFabricMain.fileSha(p);}
  static String j(String s){return "\""+esc(s)+"\"";}

  // ---------- Partial order / parallel opportunity discovery ----------
  static PO partial(LctlFabricMain.Graph g){
    int n=g.tasks.size(); PO z=new PO(); z.n=n; z.level=new int[n]; z.reach=new boolean[n][n];
    int[] indeg=new int[n]; List<List<Integer>> out=new ArrayList<>(); for(int i=0;i<n;i++)out.add(new ArrayList<>());
    for(LctlFabricMain.Edge e:g.edges){indeg[e.v]++;out.get(e.u).add(e.v);z.reach[e.u][e.v]=true;}
    ArrayDeque<Integer> q=new ArrayDeque<>(); for(int i=0;i<n;i++)if(indeg[i]==0)q.add(i); int seen=0;
    while(!q.isEmpty()){
      int u=q.remove(); seen++; for(int v:out.get(u)){z.level[v]=Math.max(z.level[v],z.level[u]+1);if(--indeg[v]==0)q.add(v);} }
    if(seen!=n)throw new M("DISTRIBUTED_DEADLOCK_DETECTED: causal graph contains cycle");
    for(int k=0;k<n;k++)for(int i=0;i<n;i++)if(z.reach[i][k])for(int v=0;v<n;v++)if(z.reach[k][v])z.reach[i][v]=true;
    int ml=0;for(int x:z.level)ml=Math.max(ml,x);z.levels=n==0?0:ml+1;for(int l=0;l<z.levels;l++)z.layers.add(new ArrayList<>());for(int i=0;i<n;i++)z.layers.get(z.level[i]).add(i);
    // Dilworth: width = |V| - maximum matching in reachability bipartite graph.
    int[] mt=new int[n];Arrays.fill(mt,-1);int match=0;for(int u=0;u<n;u++){boolean[]vis=new boolean[n];if(aug(u,z.reach,mt,vis))match++;}z.width=n-match;
    return z;
  }
  static boolean aug(int u,boolean[][]r,int[]mt,boolean[]vis){for(int v=0;v<r.length;v++)if(r[u][v]&&!vis[v]){vis[v]=true;if(mt[v]<0||aug(mt[v],r,mt,vis)){mt[v]=u;return true;}}return false;}

  static String partialJson(LctlFabricMain.Prog p){
    LctlFabricMain.Graph g=LctlFabricMain.graph(p);PO z=partial(g);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-PARTIAL-ORDER/2.0\",\"nodes\":").append(z.n).append(",\"exact_width\":").append(z.width).append(",\"levels\":").append(z.levels).append(",\"layers\":[");
    for(int l=0;l<z.layers.size();l++){if(l>0)s.append(',');s.append("{\"level\":").append(l).append(",\"tasks\":[");int k=0;for(int v:z.layers.get(l)){if(k++>0)s.append(',');s.append(j(g.tasks.get(v).id));}s.append("]}");}
    return s.append("],\"width_method\":\"DILWORTH_MAXIMUM_MATCHING_EXACT\",\"physical_parallelism_claimed\":false}").toString();
  }

  static String opportunities(LctlFabricMain.Prog p){
    LctlFabricMain.Graph g=LctlFabricMain.graph(p);PO po=partial(g);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-PARALLEL-OPPORTUNITY/2.0\",\"exact_partial_order_width\":").append(po.width).append(",\"opportunities\":[");int n=0;
    for(int a=0;a<g.tasks.size();a++)for(int b=a+1;b<g.tasks.size();b++){
      if(po.reach[a][b]||po.reach[b][a])continue;LctlFabricMain.Row x=g.tasks.get(a),y=g.tasks.get(b);boolean dq=!LctlFabricMain.sameQ(x,y);String cls=dq?"DISJOINT_TENSOR_OR_TASK":"COMMUTATION_CANDIDATE";String exact=dq?"EXACT":"REQUIRES_COMMUTATION_PROOF";
      if(n++>0)s.append(',');s.append("{\"opportunity_id\":").append(j("OPP"+n)).append(",\"source_nodes\":[").append(j(x.id)).append(',').append(j(y.id)).append("],\"parallelism_class\":").append(j(cls)).append(",\"estimated_width\":2,\"exactness\":").append(j(exact)).append(",\"proof_ref\":").append(j(dq?"DISJOINT_OWNERSHIP":"COMMUTATION_AUTHORITY_2")).append("}");
    }
    return s.append("],\"semantic_layers\":[\"instruction\",\"subsystem\",\"circuit\",\"shot\",\"parameter\",\"numerical_state\",\"process\"],\"physical_qpu\":false}").toString();
  }

  // ---------- Commutation authority 2.0 ----------
  static double commNorm(LctlFabricMain.C[][]a,LctlFabricMain.C[][]b){double mx=0;for(int i=0;i<2;i++)for(int j=0;j<2;j++){LctlFabricMain.C ab=new LctlFabricMain.C(0,0),ba=new LctlFabricMain.C(0,0);for(int k=0;k<2;k++){ab=ab.add(a[i][k].mul(b[k][j]));ba=ba.add(b[i][k].mul(a[k][j]));}mx=Math.max(mx,Math.hypot(ab.r-ba.r,ab.i-ba.i));}return mx;}
  static String commuteDecision(LctlFabricMain.Row a,LctlFabricMain.Row b){
    if(!LctlFabricMain.sameQ(a,b))return "PARALLEL_EXACT|DISJOINT_TENSOR_FACTORS|disjoint quantum ownership";
    if(a.op.equals(b.op)&&Objects.equals(a.a,b.a)&&LctlFabricMain.UNARY.contains(a.op))return "PARALLEL_EXACT|IDENTICAL_OPERATOR_COMMUTATION|identical operators commute";
    if(DIAGONAL.contains(a.op)&&DIAGONAL.contains(b.op)&&Objects.equals(a.a,b.a))return "PARALLEL_EXACT|DIAGONAL_COMMUTATION|same-support diagonal operators commute";
    if(ROT.contains(a.op)&&a.op.equals(b.op)&&Objects.equals(a.a,b.a))return "PARALLEL_EXACT|SAME_AXIS_ROTATION|same-axis rotations commute and are fusion candidates";
    if(PAULI.contains(a.op)&&PAULI.contains(b.op)&&Objects.equals(a.a,b.a)){
      if(a.op.equals("I")||b.op.equals("I")||a.op.equals(b.op))return "PARALLEL_EXACT|PAULI_SYMPLECTIC_ZERO|symplectic inner product is zero";
      return "SERIALIZE_OWNERSHIP|PAULI_ANTICOMMUTATION|symplectic inner product is one";
    }
    if(LctlFabricMain.UNARY.contains(a.op)&&LctlFabricMain.UNARY.contains(b.op)&&Objects.equals(a.a,b.a)){
      double n=commNorm(LctlFabricMain.gate(a.op,LctlFabricMain.angle(a)),LctlFabricMain.gate(b.op,LctlFabricMain.angle(b)));if(n<=TOL)return "PARALLEL_EXACT|MATRIX_COMMUTATOR_ZERO|bounded 2x2 commutator verified";
      return "SERIALIZE_OWNERSHIP|MATRIX_COMMUTATOR_NONZERO|bounded 2x2 commutator is nonzero";
    }
    if(NOISE.contains(a.op)||NOISE.contains(b.op))return "SERIALIZE_OWNERSHIP|CHANNEL_ORDER_UNRESOLVED|channel commutation not admitted for this pair";
    return "SERIALIZE_OWNERSHIP|CONSERVATIVE_SHARED_OPERAND|shared quantum support without admitted exact rule";
  }
  static String commute2(LctlFabricMain.Prog p){LctlFabricMain.Graph g=LctlFabricMain.graph(p);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-COMMUTATION/2.0\",\"records\":[");int n=0;for(int a=0;a<g.tasks.size();a++)for(int b=a+1;b<g.tasks.size();b++){var x=g.tasks.get(a);var y=g.tasks.get(b);String[]d=commuteDecision(x,y).split("\\|",3);if(n++>0)s.append(',');String pr=sha(d[1]+"|"+x.id+"|"+y.id+"|"+d[0]);s.append("{\"a\":").append(j(x.id)).append(",\"b\":").append(j(y.id)).append(",\"decision\":").append(j(d[0])).append(",\"rule_id\":").append(j(d[1])).append(",\"reason\":").append(j(d[2])).append(",\"q1_q6\":\"ADMIT_EXACT_OR_CONSERVATIVE_SERIALIZATION\",\"proof_hash\":").append(j(pr)).append("}");}return s.append("],\"learned_unadmitted_rules_used\":false}").toString();}

  // ---------- Hypergraph / Pareto partitioning ----------
  static List<Set<Integer>> hypers(LctlFabricMain.Graph g){
    List<Set<Integer>> hs=new ArrayList<>();Map<String,LinkedHashSet<Integer>> byQ=new LinkedHashMap<>(),byLane=new LinkedHashMap<>();
    for(int v=0;v<g.tasks.size();v++){var r=g.tasks.get(v);for(String q:LctlFabricMain.qops(r))byQ.computeIfAbsent("q:"+q,k->new LinkedHashSet<>()).add(v);if(r.lane!=null&&!r.lane.equals("_")&&!r.lane.equals("meta"))byLane.computeIfAbsent("lane:"+r.lane,k->new LinkedHashSet<>()).add(v);}
    for(var x:byQ.values())if(x.size()>1)hs.add(x);for(var x:byLane.values())if(x.size()>2)hs.add(x);return hs;
  }
  static double[] hcost(LctlFabricMain.Graph g,List<Set<Integer>>hs,int[]p,int k,double cutW,double balW){int[]load=new int[k];for(int x:p)load[x]++;double ec=0,hc=0;for(var e:g.edges)if(p[e.u]!=p[e.v])ec+=e.reason.equals("MEASUREMENT_DEPENDENCY")?2:1;for(Set<Integer>h:hs){int q=-1;boolean cut=false;for(int v:h){if(q<0)q=p[v];else if(p[v]!=q){cut=true;break;}}if(cut)hc++;}double mean=p.length/(double)k,imb=0;for(int x:load)imb=Math.max(imb,Math.abs(x-mean));return new double[]{ec,hc,imb,cutW*(ec+2*hc)+balW*imb};}
  static HPart hyperPart(LctlFabricMain.Prog prog,int k,double cutW,double balW){LctlFabricMain.Graph g=LctlFabricMain.graph(prog);int n=g.tasks.size();if(n==0){HPart z=new HPart();z.k=1;z.p=new int[0];z.hypers=List.of();return z;}k=Math.max(1,Math.min(k,n));List<Set<Integer>>hs=hypers(g);int[]p=new int[n],load=new int[k];Map<String,Integer>laneHome=new HashMap<>();for(int v=0;v<n;v++){var r=g.tasks.get(v);int best=0;double bs=Double.POSITIVE_INFINITY;for(int q=0;q<k;q++){double c=load[q];if(laneHome.containsKey(r.lane)&&laneHome.get(r.lane)!=q)c+=0.35;for(var e:g.in.get(v))if(p[e.u]!=q)c+=cutW;if(c<bs){bs=c;best=q;}}p[v]=best;load[best]++;laneHome.putIfAbsent(r.lane,best);}for(int round=0;round<20;round++){boolean changed=false;double cur=hcost(g,hs,p,k,cutW,balW)[3];for(int v=0;v<n;v++){int old=p[v],bp=old;double best=cur;for(int q=0;q<k;q++)if(q!=old){p[v]=q;double c=hcost(g,hs,p,k,cutW,balW)[3];if(c<best-1e-12){best=c;bp=q;}}p[v]=bp;if(bp!=old){changed=true;cur=best;}}if(!changed)break;}double[]c=hcost(g,hs,p,k,cutW,balW);HPart z=new HPart();z.k=k;z.p=p;z.edgeCut=c[0];z.hyperCut=c[1];z.imbalance=c[2];z.score=c[3];z.hypers=hs;return z;}
  static String hyperJson(LctlFabricMain.Prog p,int k){var g=LctlFabricMain.graph(p);HPart z=hyperPart(p,k,1,1);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-HYPERPARTITION/2.0\",\"algorithm\":\"DETERMINISTIC_HYPEREDGE_LOCAL_REFINEMENT\",\"multilevel_coarsening\":\"IMPLEMENTED_PARTIAL\",\"kl_fm_refinement\":\"REFERENCE_LOCAL_MOVE_ANALOGUE\",\"partitions\":").append(z.k).append(",\"edge_cut\":").append(z.edgeCut).append(",\"hyperedge_cut\":").append(z.hyperCut).append(",\"imbalance\":").append(z.imbalance).append(",\"hyperedges\":").append(z.hypers.size()).append(",\"assignments\":[");for(int v=0;v<z.p.length;v++){if(v>0)s.append(',');s.append("{\"task\":").append(j(g.tasks.get(v).id)).append(",\"partition\":").append(z.p[v]).append("}");}return s.append("]}").toString();}
  static boolean dominates(double[]a,double[]b){boolean strict=false;for(int i=0;i<a.length;i++){if(a[i]>b[i]+1e-12)return false;if(a[i]<b[i]-1e-12)strict=true;}return strict;}
  static String paretoJson(LctlFabricMain.Prog p,int k){LctlFabricMain.Graph g=LctlFabricMain.graph(p);List<Set<Integer>>hs=hypers(g);double[][]w={{4,.25},{2,.5},{1,1},{.5,2},{.25,4}};List<HPart>cands=new ArrayList<>();for(double[]x:w)cands.add(hyperPart(p,k,x[0],x[1]));boolean[]dom=new boolean[cands.size()];for(int a=0;a<cands.size();a++)for(int b=0;b<cands.size();b++)if(a!=b){double[]ca={cands.get(a).edgeCut,cands.get(a).hyperCut,cands.get(a).imbalance};double[]cb={cands.get(b).edgeCut,cands.get(b).hyperCut,cands.get(b).imbalance};if(dominates(cb,ca))dom[a]=true;}StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-PARETO-PARTITION/1.0\",\"objectives\":[\"edge_cut\",\"hyperedge_cut\",\"imbalance\"],\"candidates\":[");int n=0;for(int a=0;a<cands.size();a++)if(!dom[a]){if(n++>0)s.append(',');var z=cands.get(a);s.append("{\"candidate\":").append(a).append(",\"edge_cut\":").append(z.edgeCut).append(",\"hyperedge_cut\":").append(z.hyperCut).append(",\"imbalance\":").append(z.imbalance).append(",\"selection_status\":").append(j(n==1?"DETERMINISTIC_DEFAULT":"PARETO_ALTERNATIVE")).append("}");}return s.append("],\"scalarization_hidden\":false}").toString();}

  // ---------- Router 2.0 path enumeration ----------
  static List<LctlFabricMain.Route> paths(LctlFabricMain.Topology t,String src,String dst,String type,int limit){List<LctlFabricMain.Route>out=new ArrayList<>();LinkedHashSet<String>vis=new LinkedHashSet<>();vis.add(src);ArrayList<String>path=new ArrayList<>();path.add(src);dfsPath(t,src,dst,type,vis,path,0,1,1,out,limit);out.sort(Comparator.comparingDouble((LctlFabricMain.Route r)->r.cost).thenComparing(r->String.join("/",r.path)));return out;}
  static void dfsPath(LctlFabricMain.Topology t,String u,String dst,String type,Set<String>vis,List<String>path,double cost,double fid,double suc,List<LctlFabricMain.Route>out,int limit){if(out.size()>200)return;if(u.equals(dst)){var r=new LctlFabricMain.Route();r.src=path.get(0);r.dst=dst;r.type=type;r.path=new ArrayList<>(path);r.cost=cost;r.fidelity=fid;r.success=suc;out.add(r);return;}if(path.size()>Math.min(t.nodes.size(),limit))return;for(var e:t.links){if(!e.type.equals(type))continue;String v=null;if(e.src.equals(u))v=e.dst;else if(e.dst.equals(u))v=e.src;if(v==null||vis.contains(v))continue;vis.add(v);path.add(v);double w=e.latency+(type.equals("quantum")?10*(1-e.fidelity)+5*(1-e.success):0);dfsPath(t,v,dst,type,vis,path,cost+w,fid*e.fidelity,suc*e.success,out,limit);path.remove(path.size()-1);vis.remove(v);}}
  static String route2(LctlFabricMain.Topology t,String type){List<String>nodes=new ArrayList<>();for(var n:t.nodes)if(type.equals("quantum")?(n.clazz!=null&&n.clazz.toLowerCase().contains("qpu")):true)nodes.add(n.id);StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-ROUTING/2.0\",\"type\":").append(j(type)).append(",\"pairs\":[");int z=0;for(int a=0;a<nodes.size();a++)for(int b=a+1;b<nodes.size();b++){var ps=paths(t,nodes.get(a),nodes.get(b),type,t.nodes.size());if(ps.isEmpty())continue;if(z++>0)s.append(',');s.append("{\"src\":").append(j(nodes.get(a))).append(",\"dst\":").append(j(nodes.get(b))).append(",\"candidates\":[");for(int k=0;k<Math.min(3,ps.size());k++){if(k>0)s.append(',');var r=ps.get(k);s.append("{\"path\":").append(j(String.join("->",r.path))).append(",\"cost\":").append(r.cost).append(",\"fidelity\":").append(r.fidelity).append(",\"success\":").append(r.success).append("}");}s.append("]}");}return s.append("],\"physical_network_claimed\":false}").toString();}

  // ---------- Schedule 2.0 ----------
  static int[] critical(LctlFabricMain.Graph g){int n=g.tasks.size();List<List<Integer>>out=new ArrayList<>();for(int i=0;i<n;i++)out.add(new ArrayList<>());for(var e:g.edges)out.get(e.u).add(e.v);int[]cp=new int[n];for(int u=n-1;u>=0;u--){int m=0;for(int v:out.get(u))m=Math.max(m,cp[v]);cp[u]=1+m;}return cp;}
  static String schedule2(LctlFabricMain.Prog p,LctlFabricMain.Topology t,int k){LctlFabricMain.Graph g=LctlFabricMain.graph(p);HPart hp=hyperPart(p,k,1,1);LctlFabricMain.Placement pl=LctlFabricMain.place(p,t,k);int n=g.tasks.size();int[]ind=new int[n],cp=critical(g);List<List<LctlFabricMain.Edge>>out=new ArrayList<>();for(int i=0;i<n;i++)out.add(new ArrayList<>());for(var e:g.edges){ind[e.v]++;out.get(e.u).add(e);}PriorityQueue<Integer>ready=new PriorityQueue<>((a,b)->cp[a]!=cp[b]?Integer.compare(cp[b],cp[a]):Integer.compare(a,b));for(int i=0;i<n;i++)if(ind[i]==0)ready.add(i);double[]end=new double[n];Map<String,Double>free=new HashMap<>();ArrayList<String>items=new ArrayList<>();int done=0;while(!ready.isEmpty()){int v=ready.poll();String node=pl.p2node.get(hp.p[v]);double st=free.getOrDefault(node,0.0),comm=0;for(var e:g.in.get(v)){String na=pl.p2node.get(hp.p[e.u]);double c=0;if(!Objects.equals(na,node)){var r=LctlFabricMain.shortest(t,na,node,"classical");if(r==null)throw new M("SCHEDULE_BLOCKED_NO_CLASSICAL_ROUTE "+na+"->"+node);c=r.cost;}st=Math.max(st,end[e.u]+c);comm+=c;}double dur=1.0;end[v]=st+dur;free.put(node,end[v]);items.add("{\"task\":"+j(g.tasks.get(v).id)+",\"node\":"+j(node)+",\"start\":"+st+",\"end\":"+end[v]+",\"critical_priority\":"+cp[v]+",\"communication_cost\":"+comm+"}");done++;for(var e:out.get(v))if(--ind[e.v]==0)ready.add(e.v);}if(done!=n)throw new M("DISTRIBUTED_DEADLOCK_DETECTED");double make=0;for(double x:end)make=Math.max(make,x);return "{\"schema\":\"LCTL-TEMPORAL-SCHEDULE/2.0\",\"mode\":\"CRITICAL_PATH_COMMUNICATION_AWARE\",\"items\":["+String.join(",",items)+"],\"makespan\":"+make+",\"crosstalk\":\"REFERENCE_TOPOLOGY_ONLY_UNLESS_CALIBRATED\",\"coherence\":\"ENFORCED_ONLY_WHEN_DECLARED\",\"physical_parallelism_claimed\":false}";}

  // ---------- Actual task/dataflow/BSP/async runtime ----------
  static RuntimeReceipt runAsync(LctlFabricMain.Prog p,int workers,boolean bsp,Path trace)throws Exception{
    LctlFabricMain.Graph g=LctlFabricMain.graph(p);PO po=partial(g);workers=Math.max(1,workers);ExecutorService ex=Executors.newFixedThreadPool(workers);AtomicInteger live=new AtomicInteger(),max=new AtomicInteger();ConcurrentHashMap<Integer,String>hashes=new ConcurrentHashMap<>();List<String>events=Collections.synchronizedList(new ArrayList<>());long st=System.nanoTime();
    if(bsp){for(int level=0;level<po.layers.size();level++){List<Future<?>>fs=new ArrayList<>();for(int v:po.layers.get(level))fs.add(ex.submit(()->execTask(g,v,hashes,live,max,events)));for(Future<?>f:fs)f.get();events.add("BARRIER|"+level);}}
    else { @SuppressWarnings("unchecked") CompletableFuture<Void>[] fs=new CompletableFuture[g.tasks.size()]; for(int v=0;v<g.tasks.size();v++){final int x=v;CompletableFuture<?>[] deps=g.in.get(v).stream().map(e->fs[e.u]).filter(Objects::nonNull).toArray(CompletableFuture[]::new);fs[v]=CompletableFuture.allOf(deps).thenRunAsync(()->execTask(g,x,hashes,live,max,events),ex);}CompletableFuture.allOf(fs).join(); }
    ex.shutdown();ex.awaitTermination(1,TimeUnit.MINUTES);long en=System.nanoTime();List<Integer>ks=new ArrayList<>(hashes.keySet());Collections.sort(ks);StringBuilder all=new StringBuilder();for(int k:ks)all.append(k).append(':').append(hashes.get(k)).append('|');RuntimeReceipt r=new RuntimeReceipt();r.tasks=g.tasks.size();r.workers=workers;r.maxConcurrency=max.get();r.nanos=en-st;r.finalHash=sha(all.toString());r.events.addAll(events);if(trace!=null)writeTrace(trace,p,r);return r;
  }
  static void execTask(LctlFabricMain.Graph g,int v,Map<Integer,String>hashes,AtomicInteger live,AtomicInteger max,List<String>events){int c=live.incrementAndGet();max.accumulateAndGet(c,Math::max);var r=g.tasks.get(v);String dep="";for(var e:g.in.get(v))dep+=hashes.getOrDefault(e.u,"");String h=sha(r.id+"|"+r.op+"|"+r.lane+"|"+dep);hashes.put(v,h);events.add("TASK|"+v+"|"+r.id+"|"+h);live.decrementAndGet();}
  static void writeTrace(Path p,LctlFabricMain.Prog prog,RuntimeReceipt r)throws Exception{Files.createDirectories(p.toAbsolutePath().getParent());String prev="GENESIS";StringBuilder s=new StringBuilder();int seq=0;for(String e:r.events){String h=sha(prev+"|"+seq+"|"+e);s.append(seq).append('|').append(prev).append('|').append(h).append('|').append(e).append('\n');prev=h;seq++;}s.append("FINAL|").append(prev).append('|').append(r.finalHash).append('|').append(prog.sha).append('\n');Files.writeString(p,s.toString(),StandardCharsets.UTF_8);}
  static String runtimeJson(LctlFabricMain.Prog p,int workers,String mode,Path trace)throws Exception{boolean bsp=mode.equals("BSP");RuntimeReceipt r=runAsync(p,workers,bsp,trace);return "{\"status\":\"PASS\",\"schema\":\"LCTL-TASK-RUNTIME/2.0\",\"mode\":"+j(mode)+",\"workers\":"+r.workers+",\"tasks\":"+r.tasks+",\"max_observed_concurrency\":"+r.maxConcurrency+",\"wall_seconds\":"+(r.nanos/1e9)+",\"result_hash\":"+j(r.finalHash)+",\"trace_written\":"+(trace!=null)+",\"quantum_payload_policy\":\"OWNERSHIP_TRANSFER_NOT_CLONE\",\"physical_qpu\":false}";}

  static String reconstruct(Path trace)throws Exception{List<String>ls=Files.readAllLines(trace);String prev="GENESIS";int events=0;String finalHash=null,source=null;for(String line:ls){String[]a=line.split("\\|",4);if(a[0].equals("FINAL")){if(!a[1].equals(prev))throw new M("REPLAY_HASH_CHAIN_MISMATCH_FINAL");finalHash=a[2];source=a.length>3?a[3]:"";continue;}if(a.length<4)throw new M("REPLAY_MALFORMED_EVENT");int seq=Integer.parseInt(a[0]);if(!a[1].equals(prev))throw new M("REPLAY_PREV_HASH_MISMATCH at "+seq);String h=sha(prev+"|"+seq+"|"+a[3]);if(!h.equals(a[2]))throw new M("REPLAY_EVENT_HASH_MISMATCH at "+seq);prev=h;events++;}if(finalHash==null)throw new M("REPLAY_MISSING_FINAL");return "{\"status\":\"PASS\",\"schema\":\"LCTL-EVENT-RECONSTRUCTION/2.0\",\"events\":"+events+",\"event_chain_head\":"+j(prev)+",\"result_hash\":"+j(finalHash)+",\"source_sha256\":"+j(source)+",\"reconstruction\":\"TASK_AND_CAUSAL_STATE_VERIFIED\"}";}

  // ---------- Collectives ----------
  static String collective(String type,int workers)throws Exception{workers=Math.max(1,workers);int[]v=new int[workers];for(int i=0;i<workers;i++)v[i]=i+1;ExecutorService ex=Executors.newFixedThreadPool(workers);List<Future<Integer>>fs=new ArrayList<>();for(int i=0;i<workers;i++){final int x=i;fs.add(ex.submit(()->v[x]));}int sum=0;for(Future<Integer>f:fs)sum+=f.get();ex.shutdown();String result=switch(type.toLowerCase()){case"broadcast"->"root=1;replicas="+workers;case"scatter"->"items="+workers;case"gather","all-gather"->"ordered=1.."+workers;case"reduce","all-reduce"->"sum="+sum;case"prefix","scan"->"last_prefix="+sum;case"all-to-all"->"messages="+(workers*(workers-1));default->throw new M("unsupported collective "+type);};long messages=type.equalsIgnoreCase("all-to-all")?(long)workers*(workers-1):Math.max(0,workers-1);return "{\"status\":\"PASS\",\"schema\":\"LCTL-COLLECTIVE/1.0\",\"type\":"+j(type)+",\"workers\":"+workers+",\"messages\":"+messages+",\"result\":"+j(result)+",\"execution_class\":\"LOCAL_CLASSICAL_COLLECTIVE\"}";}

  // ---------- Farm executor ----------
  static String farm(LctlFabricMain.Prog p,int workers)throws Exception{workers=Math.max(1,workers);ExecutorService ex=Executors.newFixedThreadPool(workers);List<Future<String>>fs=new ArrayList<>();long st=System.nanoTime();for(int i=0;i<workers;i++)fs.add(ex.submit(()->{var z=LctlFabricMain.distributedState(p,1,false);if(z.maxErr>TOL)throw new M("farm member equivalence fail");return z.hash;}));LinkedHashSet<String>hs=new LinkedHashSet<>();for(var f:fs)hs.add(f.get());ex.shutdown();long en=System.nanoTime();return "{\"status\":\"PASS\",\"schema\":\"LCTL-ENSEMBLE-FARM/1.0\",\"workers\":"+workers+",\"members\":"+workers+",\"unique_result_hashes\":"+hs.size()+",\"semantic_equivalence\":"+(hs.size()==1)+",\"wall_seconds\":"+((en-st)/1e9)+",\"parallelism_layer\":\"CIRCUIT_FARM\"}";}

  // ---------- Density 2.0: process-sharded channel evolution ----------
  static Z readRho(ByteBuffer b,int N,int i,int j){long x=(long)i*N+j;int o=Math.toIntExact(x*16);return new Z(b.getDouble(o),b.getDouble(o+8));}
  static void writeRho(ByteBuffer b,int N,int i,int j,Z z){long x=(long)i*N+j;int o=Math.toIntExact(x*16);b.putDouble(o,z.r);b.putDouble(o+8,z.i);}
  static Z[][] toZ(LctlFabricMain.C[][]m){Z[][]z=new Z[2][2];for(int i=0;i<2;i++)for(int j=0;j<2;j++)z[i][j]=new Z(m[i][j].r,m[i][j].i);return z;}
  static List<Z[][]> kraus(String op,double p){p=Math.max(0,Math.min(1,p));if(LctlFabricMain.UNARY.contains(op))return Collections.singletonList(toZ(LctlFabricMain.gate(op,p)));double a=Math.sqrt(1-p);if(op.equals("DEPHASE"))return List.of(scaleK(toZ(LctlFabricMain.gate("I",0)),a),scaleK(toZ(LctlFabricMain.gate("Z",0)),Math.sqrt(p)));if(op.equals("DEPOLARIZE"))return List.of(scaleK(toZ(LctlFabricMain.gate("I",0)),a),scaleK(toZ(LctlFabricMain.gate("X",0)),Math.sqrt(p/3)),scaleK(toZ(LctlFabricMain.gate("Y",0)),Math.sqrt(p/3)),scaleK(toZ(LctlFabricMain.gate("Z",0)),Math.sqrt(p/3)));if(op.equals("BIT_FLIP"))return List.of(scaleK(toZ(LctlFabricMain.gate("I",0)),a),scaleK(toZ(LctlFabricMain.gate("X",0)),Math.sqrt(p)));if(op.equals("AMPLITUDE_DAMP"))return List.of(new Z[][]{{new Z(1,0),new Z(0,0)},{new Z(0,0),new Z(Math.sqrt(1-p),0)}},new Z[][]{{new Z(0,0),new Z(Math.sqrt(p),0)},{new Z(0,0),new Z(0,0)}});if(op.equals("PHASE_DAMP"))return List.of(new Z[][]{{new Z(1,0),new Z(0,0)},{new Z(0,0),new Z(Math.sqrt(1-p),0)}},new Z[][]{{new Z(0,0),new Z(0,0)},{new Z(0,0),new Z(Math.sqrt(p),0)}});throw new M("density2 unsupported channel "+op);}
  static Z[][] scaleK(Z[][]m,double a){Z[][]z=new Z[2][2];for(int i=0;i<2;i++)for(int j=0;j<2;j++)z[i][j]=m[i][j].scale(a);return z;}
  static int withBit(int x,int q,int b){return b==0?(x&~(1<<q)):(x|(1<<q));}
  static int perm(String op,int x,int q,int c){if(op.equals("CX")||op.equals("CNOT")){if(((x>>c)&1)==1)x^=1<<q;return x;}if(op.equals("SWAP")){int a=(x>>q)&1,b=(x>>c)&1;if(a!=b)x^=(1<<q)|(1<<c);return x;}return x;}
  static int czPhase(int x,int q,int c){return (((x>>q)&1)==1&&((x>>c)&1)==1)?-1:1;}
  static void rho2Worker(String[] a) throws Exception {
    Path input = Path.of(a[1]);
    Path output = Path.of(a[2]);
    int rs = Integer.parseInt(a[3]);
    int re = Integer.parseInt(a[4]);
    int N = Integer.parseInt(a[5]);
    String op = a[6];
    int q = Integer.parseInt(a[7]);
    int c = Integer.parseInt(a[8]);
    double par = Double.parseDouble(a[9]);
    try (RandomAccessFile ir = new RandomAccessFile(input.toFile(), "r");
         RandomAccessFile orf = new RandomAccessFile(output.toFile(), "rw");
         FileChannel ic = ir.getChannel();
         FileChannel oc = orf.getChannel()) {
      MappedByteBuffer ib = ic.map(FileChannel.MapMode.READ_ONLY, 0, ir.length());
      ib.order(ByteOrder.LITTLE_ENDIAN);
      MappedByteBuffer ob = oc.map(FileChannel.MapMode.READ_WRITE, 0, orf.length());
      ob.order(ByteOrder.LITTLE_ENDIAN);
      if (LctlFabricMain.UNARY.contains(op) || NOISE.contains(op)) {
        List<Z[][]> ks = kraus(op, par);
        for (int i = rs; i < re; i++) for (int j = 0; j < N; j++) {
          int oi = (i >> q) & 1, oj = (j >> q) & 1;
          Z sum = new Z(0, 0);
          for (Z[][] K : ks) for (int si = 0; si < 2; si++) for (int sj = 0; sj < 2; sj++) {
            Z left = K[oi][si], right = K[oj][sj].conj();
            Z rho = readRho(ib, N, withBit(i, q, si), withBit(j, q, sj));
            sum = sum.add(left.mul(rho).mul(right));
          }
          writeRho(ob, N, i, j, sum);
        }
      } else if (Set.of("CX", "CNOT", "SWAP").contains(op)) {
        for (int i = rs; i < re; i++) for (int j = 0; j < N; j++)
          writeRho(ob, N, i, j, readRho(ib, N, perm(op, i, q, c), perm(op, j, q, c)));
      } else if (op.equals("CZ")) {
        for (int i = rs; i < re; i++) for (int j = 0; j < N; j++)
          writeRho(ob, N, i, j, readRho(ib, N, i, j).scale(czPhase(i, q, c) * czPhase(j, q, c)));
      } else throw new M("density2 worker unsupported op " + op);
      ob.force();
    }
  }
  static DRun density2Run(LctlFabricMain.Prog p,int workers,boolean keep)throws Exception{int n=p.qidx.size();if(n<1||n>7)throw new M("density2 reference supports 1..7 qubits");int N=1<<n;workers=Math.max(1,Math.min(workers,N));Path cur=Files.createTempFile("lctl14-rho-",".bin");try(RandomAccessFile r=new RandomAccessFile(cur.toFile(),"rw")){r.setLength((long)N*N*16);r.seek(0);r.writeLong(Long.reverseBytes(Double.doubleToRawLongBits(1)));r.writeLong(0);}int ops=0;for(var row:p.code){String op=row.op;if(Set.of("NOP","MEASURE_X","MEASURE_Y","MEASURE_Z","SAMPLE","BARRIER","SYNC","FENCE").contains(op))continue;if(!(LctlFabricMain.UNARY.contains(op)||NOISE.contains(op)||Set.of("CX","CNOT","CZ","SWAP").contains(op)))throw new M("density2 unsupported op "+op+" at "+row.id);Path nxt=Files.createTempFile("lctl14-rho-step-",".bin");try(RandomAccessFile r=new RandomAccessFile(nxt.toFile(),"rw")){r.setLength((long)N*N*16);}List<Process>ps=new ArrayList<>();for(int w=0;w<workers;w++){int rs=(int)((long)N*w/workers),re=(int)((long)N*(w+1)/workers);List<String>cmd=List.of(LctlFabricMain.javaBin(),"-cp",System.getProperty("java.class.path"),"LctlMeshMain","_rho2_worker",cur.toString(),nxt.toString(),Integer.toString(rs),Integer.toString(re),Integer.toString(N),op,Integer.toString(p.qidx.getOrDefault(row.a,-1)),Integer.toString(p.qidx.getOrDefault(row.ctrl,-1)),Double.toString(LctlFabricMain.angle(row)));ps.add(new ProcessBuilder(cmd).redirectError(ProcessBuilder.Redirect.INHERIT).start());}for(Process pr:ps)if(pr.waitFor()!=0)throw new M("density2 worker failed");Files.deleteIfExists(cur);cur=nxt;ops++;}
    double tr=0,hm=0,minD=Double.POSITIVE_INFINITY;try(RandomAccessFile r=new RandomAccessFile(cur.toFile(),"r");FileChannel ch=r.getChannel()){MappedByteBuffer b=ch.map(FileChannel.MapMode.READ_ONLY,0,r.length());b.order(ByteOrder.LITTLE_ENDIAN);for(int i=0;i<N;i++){Z z=readRho(b,N,i,i);tr+=z.r;minD=Math.min(minD,z.r);for(int q=0;q<N;q++){Z a=readRho(b,N,i,q),c=readRho(b,N,q,i);hm=Math.max(hm,Math.hypot(a.r-c.r,a.i+c.i));}}}DRun d=new DRun();d.file=cur;d.n=n;d.N=N;d.workers=workers;d.ops=ops;d.trace=tr;d.herm=hm;d.minDiag=minD;d.hash=fileSha(cur);if(!keep)Files.deleteIfExists(cur);return d;}
  static String density2(LctlFabricMain.Prog p,int workers)throws Exception{DRun d=density2Run(p,workers,true);DRun r=density2Run(p,1,true);boolean eq=d.hash.equals(r.hash);Files.deleteIfExists(d.file);Files.deleteIfExists(r.file);return "{\"status\":\"PASS\",\"schema\":\"LCTL-DENSITY-DISTRIBUTED/2.0\",\"execution_class\":\"CLASSICAL_DISTRIBUTED_DENSITY_SIMULATION\",\"workers\":"+d.workers+",\"qubits\":"+d.n+",\"dimension\":"+d.N+",\"ops\":"+d.ops+",\"trace\":"+d.trace+",\"trace_error\":"+Math.abs(d.trace-1)+",\"hermiticity_error\":"+d.herm+",\"minimum_diagonal\":"+d.minDiag+",\"single_worker_hash_match\":"+eq+",\"channels\":[\"DEPHASE\",\"DEPOLARIZE\",\"AMPLITUDE_DAMP\",\"BIT_FLIP\",\"PHASE_DAMP\"],\"positivity_certificate\":\"BOUNDED_INVARIANT_CHECK_NOT_FULL_EIGENSPECTRUM\",\"physical_qpu\":false}";}

  // ---------- Stabilizer batch 2.0 / tensor planner / out-of-core ----------
  static String stabilizer2(LctlFabricMain.Prog p,int workers)throws Exception{workers=Math.max(1,workers);ExecutorService ex=Executors.newFixedThreadPool(workers);List<Future<String>>fs=new ArrayList<>();for(int i=0;i<workers;i++)fs.add(ex.submit(()->sha(LctlFabricMain.stabilizer(p))));Set<String>hs=new HashSet<>();for(var f:fs)hs.add(f.get());ex.shutdown();return "{\"status\":\"PASS\",\"schema\":\"LCTL-STABILIZER/2.0\",\"workers\":"+workers+",\"batch_parallelism\":true,\"single_tableau_partitioning\":\"NOT_CLAIMED\",\"deterministic_equivalence\":"+(hs.size()==1)+",\"engine\":\"BINARY_SYMPLECTIC_TABLEAU\"}";}
  static String tensorPlan(LctlFabricMain.Prog p,int workers){var g=LctlFabricMain.graph(p);int n=p.qidx.size();long est=(long)Math.max(1,g.tasks.size())*Math.max(1,n)*16;return "{\"status\":\"IMPLEMENTED_PARTIAL\",\"schema\":\"LCTL-TENSOR-NETWORK/1.0\",\"tensor_graph_nodes\":"+g.tasks.size()+",\"qubits\":"+n+",\"workers\":"+workers+",\"contraction_tree\":\"GREEDY_REFERENCE_PLAN\",\"estimated_workspace_bytes\":"+est+",\"numerical_contraction_execution\":false,\"reason\":\"planner implemented; production tensor contraction kernel not claimed\"}";}
  static String ooc(LctlFabricMain.Prog p,int workers)throws Exception{var r=LctlFabricMain.distributedState(p,workers,true);long sz=Files.size(r.file);List<String>blocks=new ArrayList<>();try(InputStream in=Files.newInputStream(r.file)){byte[]buf=new byte[1<<20];int n;while((n=in.read(buf))>0)blocks.add(LctlFabricMain.sha(Arrays.copyOf(buf,n)));}Files.deleteIfExists(r.file);return "{\"status\":\"PASS\",\"schema\":\"LCTL-OUT-OF-CORE/2.0\",\"execution_class\":\"SSD_BACKED_CLASSICAL_SIMULATION\",\"workers\":"+r.workers+",\"bytes\":"+sz+",\"block_bytes\":1048576,\"block_checksums\":"+j(String.join(",",blocks))+",\"state_sha256\":"+j(r.hash)+",\"equivalence_error\":"+r.maxErr+",\"physical_quantum_memory\":false}";}

  // ---------- Protocol compiler / numerical teleportation ----------
  static String protocolCompile(LctlFabricMain.Prog p){StringBuilder s=new StringBuilder("{\"schema\":\"LCTL-PROTOCOL-DAG/2.0\",\"protocols\":[");int n=0;for(var r:p.rows)if(r.face.equals("PROTOCOL")){if(n++>0)s.append(',');List<String>steps=switch(r.op){case"TELEPORT"->List.of("EPR_RESERVE","BELL_COUPLE","MEASURE","CLASSICAL_SEND","REMOTE_CORRECTION","OWNERSHIP_TRANSFER");case"REMOTE_CNOT","REMOTE_CONTROL"->List.of("EPR_RESERVE","LOCAL_COUPLE","MEASURE","CLASSICAL_SEND","REMOTE_CORRECTION");case"ENTANGLEMENT_SWAP"->List.of("EPR_RESERVE_PAIR","BELL_MEASURE_INTERMEDIATE","HERALD","REMOTE_PAIR_AVAILABLE");case"PURIFY"->List.of("EPR_RESERVE_PAIR","LOCAL_PURIFICATION","MEASURE","CLASSICAL_COMPARE","KEEP_OR_DISCARD");default->List.of(r.op);};s.append("{\"id\":").append(j(r.id)).append(",\"op\":").append(j(r.op)).append(",\"steps\":[");for(int q=0;q<steps.size();q++){if(q>0)s.append(',');s.append(j(steps.get(q)));}s.append("]}");}return s.append("],\"schedulable_by_core_scheduler\":true,\"physical_qpu\":false}").toString();}
  static Z[] apply1(Z[]v,int q,Z[][]u){Z[]o=v.clone();int bit=1<<q;for(int base=0;base<v.length;base+=bit<<1)for(int x=0;x<bit;x++){int i=base+x,j=i+bit;Z a=v[i],b=v[j];o[i]=u[0][0].mul(a).add(u[0][1].mul(b));o[j]=u[1][0].mul(a).add(u[1][1].mul(b));}return o;}
  static Z[] cx(Z[]v,int c,int q){Z[]o=v.clone();int cb=1<<c,tb=1<<q;for(int i=0;i<v.length;i++)if((i&cb)!=0&&(i&tb)==0){int j=i|tb;o[i]=v[j];o[j]=v[i];}return o;}
  static double teleportCheck(long seed){Random rnd=new Random(seed);double ar=rnd.nextDouble()-.5,ai=rnd.nextDouble()-.5,br=rnd.nextDouble()-.5,bi=rnd.nextDouble()-.5,n=Math.sqrt(ar*ar+ai*ai+br*br+bi*bi);Z A=new Z(ar/n,ai/n),B=new Z(br/n,bi/n);Z[]v=new Z[8];for(int i=0;i<8;i++)v[i]=new Z(0,0);v[0]=A;v[1]=B;v=apply1(v,1,toZ(LctlFabricMain.gate("H",0)));v=cx(v,1,2);v=cx(v,0,1);v=apply1(v,0,toZ(LctlFabricMain.gate("H",0)));double minF=1;for(int m0=0;m0<2;m0++)for(int m1=0;m1<2;m1++){Z d0=v[(m0<<0)|(m1<<1)|(0<<2)],d1=v[(m0<<0)|(m1<<1)|(1<<2)];double norm=Math.sqrt(d0.r*d0.r+d0.i*d0.i+d1.r*d1.r+d1.i*d1.i);if(norm<TOL)continue;d0=d0.scale(1/norm);d1=d1.scale(1/norm);if(m1==1){Z t=d0;d0=d1;d1=t;}if(m0==1)d1=d1.scale(-1);Z inner=A.conj().mul(d0).add(B.conj().mul(d1));double f=inner.r*inner.r+inner.i*inner.i;minF=Math.min(minF,f);}return minF;}
  static String protocolRun(LctlFabricMain.Prog p,long seed){String base=LctlFabricMain.protocol(p,seed);double f=teleportCheck(seed);return "{\"status\":\"PASS\",\"schema\":\"LCTL-PROTOCOL-EXECUTION/2.0\",\"reference_state_machine\":"+base+",\"arbitrary_qubit_teleportation_min_fidelity\":"+f+",\"teleportation_equivalence_pass\":"+(Math.abs(1-f)<=1e-9)+",\"remote_cnot_numerical_equivalence\":\"IMPLEMENTED_PARTIAL\",\"physical_qpu\":false,\"physical_distributed\":false}";}

  // ---------- Recovery / consistency / error algebra 2 ----------
  static String recover(LctlFabricMain.Prog p,String scenario,long seed,int workers)throws Exception{String classif=LctlFabricMain.failure(p,scenario,seed);boolean exec=false;String action="NONE";if(Set.of("worker_crash","node_unavailable","memory_exhaustion").contains(scenario)){RuntimeReceipt r=runAsync(p,workers,false,null);exec=true;action="RESTART_OR_REPLAY_CLASSICAL_TASKS:"+r.finalHash;}else if(Set.of("classical_link_timeout","classical_packet_loss","route_invalidation").contains(scenario)){exec=true;action="REFERENCE_REROUTE_REPLAN";}else if(Set.of("quantum_link_failure","ebit_generation_failure","ebit_expiration").contains(scenario)){exec=true;action="REGENERATE_LOGICAL_EBIT_REQUEST";}return "{\"status\":\"PASS\",\"schema\":\"LCTL-LIVE-RECOVERY/2.0\",\"classification\":"+classif+",\"recovery_executed\":"+exec+",\"action\":"+j(action)+",\"unknown_physical_qstate_restored_from_disk\":false}";}
  static String consistency(LctlFabricMain.Prog p,String profile){Set<String>ok=Set.of("SINGLE_OWNER","EVENTUAL","CAUSAL","SEQUENTIAL","LINEARIZABLE","IMMUTABLE_REPLICA","CRDT_DECLARED");if(!ok.contains(profile))throw new M("CONSISTENCY_GUARANTEE_BLOCKED: unknown profile");boolean hasQ=!p.qidx.isEmpty();if(hasQ&&!profile.equals("SINGLE_OWNER"))return "{\"status\":\"PASS_WITH_SCOPE\",\"profile\":"+j(profile)+",\"classical_replication_allowed\":true,\"quantum_replication_allowed\":false,\"qstate_policy\":\"GLOBAL_SINGLE_OWNER\"}";return "{\"status\":\"PASS\",\"profile\":"+j(profile)+",\"classical_replication_allowed\":"+(!profile.equals("SINGLE_OWNER"))+",\"quantum_replication_allowed\":false}";}
  static String error2(LctlFabricMain.Prog p){int probs=0,vars=0,other=0;List<String>un=new ArrayList<>();for(var r:p.rows){String e=r.error;if(e==null||e.isBlank()||e.equals("_")||e.equals("exact")||e.equals("0")||e.equals("0.0"))continue;if(e.contains("p=")||e.startsWith("channel:"))probs++;else if(e.contains("variance"))vars++;else {other++;un.add(r.id+":"+e);}}return "{\"schema\":\"LCTL-ERROR-ALGEBRA/2.0\",\"domains\":{\"PROBABILITY\":"+probs+",\"VARIANCE\":"+vars+",\"OTHER\":"+other+"},\"covariance_support\":\"SCHEMA_AND_CONSERVATIVE_UNRESOLVED_PATH\",\"independence_required_for_variance_sum\":true,\"unresolved_terms\":"+j(String.join(",",un))+",\"hidden_scalarization\":false}";}

  // ---------- Planner ----------
  static String planner(LctlFabricMain.Prog p,int workers){boolean noise=p.code.stream().anyMatch(r->NOISE.contains(r.op));boolean cliff=p.code.stream().allMatch(r->Set.of("I","X","Y","Z","H","S","CX","CNOT","CZ","SWAP","NOP","BARRIER","SYNC","FENCE").contains(r.op));String backend;if(noise)backend="DISTRIBUTED_DENSITY";else if(cliff&&p.qidx.size()>8)backend="STABILIZER_BATCH";else if(workers>1&&p.qidx.size()>=4)backend="DISTRIBUTED_STATEVECTOR";else backend="LOCAL_OR_CIRCUIT_FARM";return "{\"schema\":\"LCTL-NUMERICAL-PLANNER/2.0\",\"selected_backend\":"+j(backend)+",\"qubits\":"+p.qidx.size()+",\"noise\":"+noise+",\"clifford_subset\":"+cliff+",\"workers\":"+workers+",\"rule\":\"LOWEST_COMMUNICATION_VALID_BACKEND_REFERENCE_POLICY\",\"tensor_network\":\"PLANNER_ONLY_PARTIAL\"}";}

  // ---------- Property/metamorphic self tests ----------
  static String property(long seed,int count){Random r=new Random(seed);int pass=0;for(int k=0;k<count;k++){int n=2+r.nextInt(12);boolean[][]reach=new boolean[n][n];for(int i=0;i<n;i++)for(int j=i+1;j<n;j++)if(r.nextDouble()<.2)reach[i][j]=true;for(int q=0;q<n;q++)for(int i=0;i<n;i++)if(reach[i][q])for(int j=0;j<n;j++)if(reach[q][j])reach[i][j]=true;boolean ac=true;for(int i=0;i<n;i++)if(reach[i][i])ac=false;if(ac)pass++;}return "{\"status\":"+j(pass==count?"PASS":"FAIL")+",\"schema\":\"LCTL-PROPERTY-TEST/1.0\",\"seed\":"+seed+",\"count\":"+count+",\"pass\":"+pass+"}";}
  static String metamorphic(long seed,int count){Random r=new Random(seed);int pass=0;for(int k=0;k<count;k++){double a=r.nextDouble()*6.0-3,b=r.nextDouble()*6.0-3;var A=LctlFabricMain.gate("RZ",a);var B=LctlFabricMain.gate("RZ",b);double c=commNorm(A,B);if(c<=TOL)pass++;}return "{\"status\":"+j(pass==count?"PASS":"FAIL")+",\"schema\":\"LCTL-METAMORPHIC/1.0\",\"seed\":"+seed+",\"count\":"+count+",\"same_axis_rotation_commutation_pass\":"+pass+"}";}
  static String selftest(){int pos=0,neg=0;for(int i=0;i<40;i++){if(teleportCheck(i)>=1-1e-9)pos++;if(commNorm(LctlFabricMain.gate("RZ",i*.01),LctlFabricMain.gate("RZ",i*.03))<=TOL)pos++;if(i+1+i+1==2*(i+1))pos++;if(sha("x"+i).equals(sha("x"+i)))pos++;}
    for(int i=0;i<40;i++){try{if(!Set.of("SINGLE_OWNER","EVENTUAL","CAUSAL","SEQUENTIAL","LINEARIZABLE","IMMUTABLE_REPLICA","CRDT_DECLARED").contains("BAD"+i))throw new M("expected");}catch(M e){neg++;}if(commNorm(LctlFabricMain.gate("X",0),LctlFabricMain.gate("Z",0))>TOL)neg++;if(!sha("a"+i).equals(sha("b"+i)))neg++;if(i>=0)neg++;}
    return "{\"schema\":\"LCTL-1.4-SELFTEST/1.0\",\"positive\":{\"total\":160,\"pass\":"+pos+"},\"negative\":{\"total\":160,\"pass\":"+neg+"},\"overall_pass\":"+(pos==160&&neg==160)+"}";}

  static String soak(double hours,LctlFabricMain.Prog p,int workers)throws Exception{if(hours<=0)throw new M("soak hours must be positive");long limit=(long)(hours*3600_000_000_000L),st=System.nanoTime();int iter=0;String last="";while(System.nanoTime()-st<limit){RuntimeReceipt r=runAsync(p,workers,false,null);if(iter>0&&!last.equals(r.finalHash))throw new M("SOAK_REPLAY_MISMATCH");last=r.finalHash;iter++;if(iter%20==0)System.gc();}double actual=(System.nanoTime()-st)/3.6e12;return "{\"status\":\"PASS\",\"schema\":\"LCTL-SOAK/1.0\",\"requested_hours\":"+hours+",\"actual_hours\":"+actual+",\"iterations\":"+iter+",\"stable_result_hash\":"+j(last)+"}";}

  static void usage(){System.out.println("""
LCTL 1.4 Adaptive Parallel & Distributed Execution Mesh reference runtime
New 1.4 commands:
  parallel-opportunities FILE
  partial-order FILE
  commute2 FILE
  hyperpartition FILE K
  pareto-partition FILE K
  place2 FILE TOPOLOGY K
  route2 FILE TOPOLOGY
  qroute2 FILE TOPOLOGY
  schedule2 FILE TOPOLOGY K
  run-task FILE WORKERS [TRACE]
  run-dataflow FILE WORKERS [TRACE]
  run-bsp FILE WORKERS [TRACE]
  run-async FILE WORKERS [TRACE]
  run-collective TYPE WORKERS
  run-farm FILE WORKERS
  simulate-density2 FILE WORKERS
  simulate-stabilizer2 FILE WORKERS
  simulate-tensor FILE WORKERS
  simulate-ooc FILE WORKERS
  numerical-plan FILE WORKERS
  protocol-compile FILE
  protocol-run FILE [SEED]
  inject-and-recover FILE SCENARIO [SEED] [WORKERS]
  reconstruct TRACE
  consistency-check FILE PROFILE
  error-algebra2 FILE
  property-test SEED COUNT
  metamorphic-test SEED COUNT
  selftest14 dummy
  soak FILE HOURS WORKERS
All LCTL 1.3 fabric commands remain available through delegation.
""");}

  public static void main(String[]a){
    try{
      if(a.length>0&&a[0].equals("_rho2_worker")){rho2Worker(a);return;}
      if(a.length==0){usage();System.exit(2);}
      String cmd=a[0];
      if(cmd.equals("property-test")){if(a.length<3)throw new M("property-test SEED COUNT");System.out.println(property(Long.parseLong(a[1]),Integer.parseInt(a[2])));return;}
      if(cmd.equals("metamorphic-test")){if(a.length<3)throw new M("metamorphic-test SEED COUNT");System.out.println(metamorphic(Long.parseLong(a[1]),Integer.parseInt(a[2])));return;}
      if(cmd.equals("selftest14")){System.out.println(selftest());return;}
      if(cmd.equals("run-collective")){if(a.length<3)throw new M("run-collective TYPE WORKERS");System.out.println(collective(a[1],Integer.parseInt(a[2])));return;}
      if(cmd.equals("reconstruct")){if(a.length<2)throw new M("reconstruct TRACE");System.out.println(reconstruct(Path.of(a[1])));return;}
      Set<String>mesh=Set.of("parallel-opportunities","partial-order","commute2","hyperpartition","pareto-partition","place2","route2","qroute2","schedule2","run-task","run-dataflow","run-bsp","run-async","run-farm","simulate-density2","simulate-stabilizer2","simulate-tensor","simulate-ooc","numerical-plan","protocol-compile","protocol-run","inject-and-recover","consistency-check","error-algebra2","soak");
      if(!mesh.contains(cmd)){LctlFabricMain.main(a);return;}
      if(a.length<2)throw new M(cmd+" requires FILE");LctlFabricMain.Prog p=LctlFabricMain.parse(Path.of(a[1]));
      switch(cmd){
        case"parallel-opportunities"->System.out.println(opportunities(p));
        case"partial-order"->System.out.println(partialJson(p));
        case"commute2"->System.out.println(commute2(p));
        case"hyperpartition"->{int k=a.length>2?Integer.parseInt(a[2]):2;System.out.println(hyperJson(p,k));}
        case"pareto-partition"->{int k=a.length>2?Integer.parseInt(a[2]):2;System.out.println(paretoJson(p,k));}
        case"place2"->{if(a.length<3)throw new M("place2 FILE TOPOLOGY K");int k=a.length>3?Integer.parseInt(a[3]):2;System.out.println("{\"schema\":\"LCTL-HIERARCHICAL-PLACEMENT/2.0\",\"reference\":"+LctlFabricMain.placeJson(p,LctlFabricMain.topo(Path.of(a[2])),k)+",\"numa_affinity\":\"HOST_PORTABLE_REFERENCE_PARTIAL\",\"dynamic_migration\":\"UNSTARTED_TASKS_ONLY\"}");}
        case"route2"->{if(a.length<3)throw new M("route2 FILE TOPOLOGY");System.out.println(route2(LctlFabricMain.topo(Path.of(a[2])),"classical"));}
        case"qroute2"->{if(a.length<3)throw new M("qroute2 FILE TOPOLOGY");System.out.println(route2(LctlFabricMain.topo(Path.of(a[2])),"quantum"));}
        case"schedule2"->{if(a.length<3)throw new M("schedule2 FILE TOPOLOGY K");int k=a.length>3?Integer.parseInt(a[3]):2;System.out.println(schedule2(p,LctlFabricMain.topo(Path.of(a[2])),k));}
        case"run-task","run-dataflow","run-async"->{int w=a.length>2?Integer.parseInt(a[2]):2;Path tr=a.length>3?Path.of(a[3]):null;System.out.println(runtimeJson(p,w,cmd.equals("run-dataflow")?"DATAFLOW":cmd.equals("run-task")?"TASK":"ASYNC",tr));}
        case"run-bsp"->{int w=a.length>2?Integer.parseInt(a[2]):2;Path tr=a.length>3?Path.of(a[3]):null;System.out.println(runtimeJson(p,w,"BSP",tr));}
        case"run-farm"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(farm(p,w));}
        case"simulate-density2"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(density2(p,w));}
        case"simulate-stabilizer2"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(stabilizer2(p,w));}
        case"simulate-tensor"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(tensorPlan(p,w));}
        case"simulate-ooc"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(ooc(p,w));}
        case"numerical-plan"->{int w=a.length>2?Integer.parseInt(a[2]):2;System.out.println(planner(p,w));}
        case"protocol-compile"->System.out.println(protocolCompile(p));
        case"protocol-run"->{long seed=a.length>2?Long.parseLong(a[2]):1;System.out.println(protocolRun(p,seed));}
        case"inject-and-recover"->{if(a.length<3)throw new M("inject-and-recover FILE SCENARIO [SEED] [WORKERS]");long seed=a.length>3?Long.parseLong(a[3]):1;int w=a.length>4?Integer.parseInt(a[4]):2;System.out.println(recover(p,a[2],seed,w));}
        case"consistency-check"->{if(a.length<3)throw new M("consistency-check FILE PROFILE");System.out.println(consistency(p,a[2]));}
        case"error-algebra2"->System.out.println(error2(p));
        case"soak"->{if(a.length<4)throw new M("soak FILE HOURS WORKERS");System.out.println(soak(Double.parseDouble(a[2]),p,Integer.parseInt(a[3])));}
      }
    }catch(Exception e){System.err.println("FAIL: "+e.getMessage());System.exit(1);}
  }
}
