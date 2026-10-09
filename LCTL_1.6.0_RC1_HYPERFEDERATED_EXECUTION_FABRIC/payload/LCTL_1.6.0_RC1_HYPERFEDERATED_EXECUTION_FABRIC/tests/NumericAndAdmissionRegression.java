import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public final class NumericAndAdmissionRegression {
  static int checks=0;
  static void check(boolean b,String message){checks++;if(!b)throw new AssertionError(message);}
  static String tuple(String[] values){return "("+String.join(" ¦ ",values)+")\n";}
  static String sealed(LctlMain.Unit u){
    StringBuilder s=new StringBuilder("LCTL/1.3\n"+LctlMain.BC+"\n");s.append(tuple(u.b));
    for(LctlMain.Frame f:u.frames){StringBuilder b=new StringBuilder("QFRAME/1.0 BEGIN "+f.id()+"\n"+LctlMain.FC+"\n");b.append(tuple(f.f)).append("QTUPLE/1.0\n"+LctlMain.RC+"\n");for(LctlMain.Row row:f.rows)b.append(tuple(row.x));b.append("END QTUPLE\nEND QFRAME\n");s.append(b).append("@frame sha256:").append(LctlMain.sha(b.toString())).append('\n');}
    s.append("END LCTL\n");String body=s.toString();return body+"@bundle sha256:"+LctlMain.sha(body)+"\n";
  }
  static void rejects(Path root,String text,String why)throws Exception{
    Path p=Files.createTempFile(root.resolve("build/audit-temp"),"admission-",".lctl");try{Files.writeString(p,text,StandardCharsets.UTF_8);
      boolean core=false,fabric=false;try{LctlMain.parse(p,true);}catch(Exception e){core=true;}try{LctlFabricMain.parse(p);}catch(Exception e){fabric=true;}
      check(core,"core rejection "+why);check(fabric,"fabric rejection "+why);
    }finally{Files.deleteIfExists(p);}
  }
  public static void main(String[] args)throws Exception{
    Path root=Path.of(args[0]);Files.createDirectories(root.resolve("build/audit-temp"));Path example=root.resolve("examples/01_bell_state/program.lctl");
    LctlFabricMain.parse(example);check(true,"valid verified source admitted");
    for(String value:new String[]{"NaN","Infinity","-Infinity","1e309"})for(int context=0;context<4;context++){
      LctlMain.Unit u=LctlMain.parse(example,true);
      if(context==0)u.b[11]=value;else if(context==1)u.frames.get(0).f[12]=value;else if(context==2)u.ordered.get(0).x[16]=value;else{LctlMain.Row gate=u.ordered.stream().filter(row->row.op().equals("H")).findFirst().orElseThrow();gate.x[4]="RX";gate.x[9]=value;}
      rejects(root,sealed(u),"nonfinite value in context "+context);
    }
    String original=Files.readString(example,StandardCharsets.UTF_8);rejects(root,original.replaceFirst("@bundle sha256:[0-9a-f]+","@bundle sha256:"+"0".repeat(64)),"tampered bundle seal");
    rejects(root,original.replaceFirst("@frame sha256:[0-9a-f]+","@frame sha256:"+"0".repeat(64)),"tampered frame seal");
    LctlMain.Unit u=LctlMain.parse(example,true);u.b[8]="allow";rejects(root,sealed(u),"resealed unauthorized network policy");
    u=LctlMain.parse(example,true);u.b[7]="physical";rejects(root,sealed(u),"resealed unauthorized backend policy");
    rejects(root,original.replaceFirst("LCTL/[0-9.]+","LCTL/1.5"),"unimplemented magic");
    System.out.println("{\"status\":\"PASS\",\"checks\":"+checks+",\"nonfinite_program_variants\":16,\"admission_boundary_variants\":5}");
  }
}
