import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
/** Batched descriptor execution; no command interpolation and no simulated certification. */
public final class LctlDescriptorConformanceMain {
  public static void main(String[] args)throws Exception {
    if(args.length!=1)throw new IllegalArgumentException("descriptor TSV required");
    int positive=0,negative=0,calls=0;List<String> failures=new ArrayList<>();
    for(String line:Files.readAllLines(Path.of(args[0]),StandardCharsets.UTF_8)){
      String[] z=line.split("\t",-1);String id=z.length>1?z[1]:"missing";
      try{
        if(z.length!=5)throw new IllegalArgumentException("descriptor shape");
        if(z[0].equals("P")){
          long seed=Long.parseLong(z[2]);int replicas=Integer.parseInt(z[3]);
          String first=LctlFederatedMain.remoteCnot(seed),second=LctlFederatedMain.crdt(z[4],replicas);calls+=2;
          if(!first.contains("\"status\":\"PASS\"")||!second.contains("\"status\":\"PASS\""))throw new IllegalArgumentException("reference result failed");positive++;
        }else if(z[0].equals("N")){
          boolean reject=false;calls++;try{LctlFederatedMain.crdt(z[4],Integer.parseInt(z[3]));}catch(LctlFederatedMain.F e){reject=e.getMessage().equals("CRDT_INVALID_TYPE");}
          if(!reject)throw new IllegalArgumentException("invalid type not rejected");negative++;
        }else throw new IllegalArgumentException("polarity");
      }catch(Exception e){failures.add(id+":"+e.getMessage());}
    }
    boolean pass=positive==256&&negative==256&&failures.isEmpty();StringBuilder errors=new StringBuilder("[");for(String f:failures){if(errors.length()>1)errors.append(',');errors.append(LctlFederatedMain.j(f));}errors.append(']');
    System.out.println("{\"status\":\""+(pass?"PASS_WITH_SCOPE":"FAIL")+"\",\"descriptor_execution\":true,\"descriptor_pass\":{\"positive\":"+positive+",\"negative\":"+negative+"},\"runtime_invocations\":"+calls+",\"crdt_merge_laws_verified\":false,\"scope\":\"Reference remote-CNOT branch fidelity, CRDT aggregate commands, and invalid-type rejection; merge algebra remains unimplemented.\",\"failures\":"+errors+"}");
    if(!pass)System.exit(1);
  }
}
