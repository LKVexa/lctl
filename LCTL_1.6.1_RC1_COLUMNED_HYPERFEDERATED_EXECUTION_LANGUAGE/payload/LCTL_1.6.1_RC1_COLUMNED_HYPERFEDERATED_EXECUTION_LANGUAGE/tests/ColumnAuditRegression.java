import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Semantic-policy preservation and malformed-source regression campaign. */
public final class ColumnAuditRegression {
  static int checks=0;
  static void check(boolean ok,String what){checks++;if(!ok)throw new AssertionError(what);}
  static Path write(String suffix,String s)throws Exception{Path p=Files.createTempFile("lctl-audit-",suffix);Files.writeString(p,s,StandardCharsets.UTF_8);p.toFile().deleteOnExit();return p;}
  static void rejects(String s,String why)throws Exception{boolean rejected=false;try{LctlColumnMain.parseColumn(write(".lctlc",s));}catch(LctlColumnMain.Cx e){rejected=true;}check(rejected,why);}
  static void changed(LctlMain.Unit u,String[] cells,String why){String before=LctlColumnMain.semanticFingerprint(u);for(int i=0;i<cells.length;i++){String save=cells[i];cells[i]=save+"|mutation";check(!before.equals(LctlColumnMain.semanticFingerprint(u)),why+" cell "+i);cells[i]=save;}}
  public static void main(String[] args)throws Exception{
    Path root=Path.of(args[0]);List<Path> programs;
    try(var paths=Files.walk(root.resolve("examples"))){programs=paths.filter(p->p.getFileName().toString().equals("program.lctl")).sorted().toList();}
    check(programs.size()==53,"all 53 canonical examples present");
    for(Path p:programs){String r=LctlColumnMain.roundtrip(p);check(r.contains("\"status\":\"PASS\""),"complete semantic roundtrip "+p);}
    String sample=Files.readString(root.resolve("examples_column/01_bell_state/program.lctlc"),StandardCharsets.UTF_8);
    LctlColumnMain.CUnit c=LctlColumnMain.parseColumn(write(".lctlc",sample));
    c.unit.put("precision","f32");c.unit.put("replay","deterministic-audit-policy");c.unit.put("state","reviewed");c.unit.put("proof","audit \"quoted\" \\ value=1;it's exact");
    c.frames.get(0).hdr.put("proof","frame proof \"quoted\" \\ path");
    c.frames.get(0).rows.get(0).meta+=";c=1";
    Path policy=write(".lctl",LctlColumnMain.lower(c));
    check(LctlColumnMain.roundtrip(policy).contains("\"status\":\"PASS\""),"retain precision, replay, state, escaping, and exact confidence spelling");
    LctlMain.Unit u=LctlMain.parse(policy,true);
    changed(u,u.b,"bundle fingerprint");changed(u,u.frames.get(0).f,"frame fingerprint");changed(u,u.ordered.get(0).x,"row fingerprint");
    // These cells were previously omitted and could mutate while a roundtrip still passed.
    String[] broken={
      sample.replace("@end\n",""),
      sample.replace("@frame ","@frame nonsense=1 "),
      sample.replace("@defaults ","@defaults unknown=1 "),
      sample.replace("@unit ","@unit network=deny network=deny "),
      sample.replace("@end\n","@unit id=late\n@end\n"),
      sample.replace("@end\n","@frame id=nested\n@end\n"),
      sample.replace("@end\n","@end\n@end\n"),
      sample.replace("@frame id=F0000", "@frame id=F0000 id=F0000"),
      sample.replace("D0002 │", "D0001 │"),
      sample.replace(";res=", ";unknown="),
      sample.replace(";res=", ";r=exact;regime=exact;res="),
      sample.replace("@end\n", "@defaults conf=0.1\n@end\n"),
      sample.replace("error_budget=0.01", "error_budget=\"unterminated"),
      sample.replace("ID│LANE│OP│OUT│CTRL│IN│ARG│META", "ID│LANE│OP│OUT│CTRL│IN│ARG│META\nID│LANE│OP│OUT│CTRL│IN│ARG│META")
    };
    for(int i=0;i<broken.length;i++)rejects(broken[i],"reject malformed structure/key "+i);
    String sm=LctlColumnMain.selftest();check(sm.contains("\"positive\":1")&&sm.contains("\"negative\":1")&&sm.contains("\"total\":2"),"selftest counts executed cases");
    System.out.println("{\"status\":\"PASS\",\"checks\":"+checks+",\"canonical_example_roundtrips\":53,\"negative_source_variants\":"+broken.length+",\"policy_fields_preserved\":true}");
  }
}
