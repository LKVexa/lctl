import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.math.BigInteger;
import java.util.*;

public final class QctlRuntime {
  static final String HEADER="QCTL/1.0";
  static final String COLS="ROW¦KIND¦LANE¦OP¦OUT¦A¦B¦CAP¦WIDTH¦MODE¦AUX¦PROOF";
  static final int WORD_BITS=1048576, WORD_BYTES=131072, INSN_HEADER=32, INSN_BYTES=131104, PROGRAM_HEADER=64;
  static final int MEM_BYTES=4*1024*1024, MAX_INSNS=1024;
  static final int F_ZERO=1, F_CARRY=2, F_OVER=4, F_LESS=8, F_GREATER=16;
  static final String[] OPS={"NOP","MOVI","MOV","ADD","SUB","MUL","DIVU","MODU","AND","OR","XOR","NOT","SHL","SHR","CMP","JMP","JZ","JNZ","LOAD","STORE","PUSH","POP","SVC","HALT"};
  static final Map<String,Integer> OPMAP=new LinkedHashMap<>();
  static { for(int i=0;i<OPS.length;i++) OPMAP.put(OPS[i],i); }

  static class Row {
    String row,kind,lane,op,out,a,b,cap,width,mode,aux,proof;
    static Row of(String[] x){ Row r=new Row(); r.row=x[0];r.kind=x[1];r.lane=x[2];r.op=x[3];r.out=x[4];r.a=x[5];r.b=x[6];r.cap=x[7];r.width=x[8];r.mode=x[9];r.aux=x[10];r.proof=x[11]; return r; }
  }
  static class Unit {
    Path path; List<Row> rows=new ArrayList<>(), code=new ArrayList<>();
    Map<String,String> meta=new LinkedHashMap<>(); Map<String,Integer> codeIndex=new LinkedHashMap<>();
    String canonical, declaredSeal, computedSeal;
  }
  static class QctlException extends RuntimeException { QctlException(String m){super(m);} }

  static String norm(String s){ return s.replace("\r\n","\n").replace("\r","\n"); }
  static String sha256Hex(byte[] b) {
    try { MessageDigest md=MessageDigest.getInstance("SHA-256"); return hex(md.digest(b)); }
    catch(Exception e){ throw new RuntimeException(e); }
  }
  static String hex(byte[] b){ StringBuilder s=new StringBuilder(); for(byte x:b)s.append(String.format("%02x",x&255)); return s.toString(); }

  static Unit parse(Path p, boolean requireSeal) throws IOException {
    String all=norm(Files.readString(p,StandardCharsets.UTF_8));
    List<String> lines=new ArrayList<>(Arrays.asList(all.split("\n",-1)));
    if(lines.size()<3 || !HEADER.equals(lines.get(0))) throw new QctlException("PARSE: bad header");
    if(!COLS.equals(lines.get(1))) throw new QctlException("PARSE: bad canonical columns");
    int end=-1; for(int i=2;i<lines.size();i++) if("END".equals(lines.get(i))){end=i;break;}
    if(end<0) throw new QctlException("PARSE: missing END");
    Unit u=new Unit(); u.path=p;
    u.canonical=String.join("\n",lines.subList(0,end+1))+"\n";
    u.computedSeal=sha256Hex(u.canonical.getBytes(StandardCharsets.UTF_8));
    for(int i=end+1;i<lines.size();i++){
      String s=lines.get(i).trim();
      if(s.startsWith("@seal sha256:")) u.declaredSeal=s.substring("@seal sha256:".length()).trim().toLowerCase(Locale.ROOT);
      else if(!s.isEmpty()) throw new QctlException("PARSE: content after END is not a seal");
    }
    if(requireSeal && u.declaredSeal==null) throw new QctlException("SEAL: missing seal");
    if(u.declaredSeal!=null && !u.declaredSeal.equals(u.computedSeal)) throw new QctlException("SEAL: mismatch expected="+u.declaredSeal+" computed="+u.computedSeal);
    Set<String> ids=new HashSet<>();
    for(int i=2;i<end;i++){
      String s=lines.get(i).trim(); if(s.isEmpty()) continue;
      if(!(s.startsWith("(")&&s.endsWith(")"))) throw new QctlException("PARSE: row must be parenthesized at line "+(i+1));
      String[] x=s.substring(1,s.length()-1).split("¦",-1);
      if(x.length!=12) throw new QctlException("PARSE: row must have 12 cells at line "+(i+1));
      for(int j=0;j<x.length;j++) x[j]=x[j].trim();
      Row r=Row.of(x);
      if(r.row.isEmpty() || !ids.add(r.row)) throw new QctlException("ROW: duplicate/empty row id "+r.row);
      u.rows.add(r);
      if("META".equals(r.kind) && "DEFINE".equals(r.op)) u.meta.put(r.out,r.a);
      if("CODE".equals(r.kind)){ u.codeIndex.put(r.row,u.code.size()); u.code.add(r); }
      else if(!Set.of("META","DATA","TEST","EVIDENCE").contains(r.kind)) throw new QctlException("ROW: unknown KIND "+r.kind);
    }
    validate(u); return u;
  }

  static void validate(Unit u){
    String[] req={"module","profile","target","backend","network","word_bits","entry"};
    for(String k:req) if(!u.meta.containsKey(k)) throw new QctlException("META: missing "+k);
    if(!"qctl.backendless.bwec1".equals(u.meta.get("profile"))) throw new QctlException("META: unsupported profile");
    if(!"BWX1".equals(u.meta.get("target"))) throw new QctlException("META: unsupported target");
    if(!"none".equals(u.meta.get("backend"))) throw new QctlException("META: backend must be none");
    if(!"deny".equals(u.meta.get("network"))) throw new QctlException("META: network must be deny");
    if(parseInt(u.meta.get("word_bits"),"word_bits")!=WORD_BITS) throw new QctlException("META: word_bits must be "+WORD_BITS);
    if(u.code.isEmpty()) throw new QctlException("META: no CODE rows");
    if(!u.codeIndex.containsKey(u.meta.get("entry"))) throw new QctlException("META: entry is not CODE");
    if(u.code.size()>MAX_INSNS) throw new QctlException("RESOURCE: too many instructions");
    for(Row r:u.code) validateCode(u,r);
  }
  static void validateCode(Unit u,Row r){
    if(!OPMAP.containsKey(r.op)) throw new QctlException("OPCODE: "+r.op+" at "+r.row);
    int w=width(u,r); if(w<1||w>WORD_BITS) throw new QctlException("OPERAND: width at "+r.row);
    mode(r.mode); regMaybe(r.out);regMaybe(r.a);regMaybe(r.b);capMaybe(r.cap);
    if(Set.of("JMP","JZ","JNZ").contains(r.op)) aux(u,r); else if(!"_".equals(r.aux))parseUnsigned32(r.aux,"AUX "+r.row);
    if("MOVI".equals(r.op)){BigInteger v=parseBig(r.a);if(v.signum()<0||v.bitLength()>w)throw new QctlException("OPERAND: MOVI immediate exceeds WIDTH at "+r.row);}
  }
  static int width(Unit u,Row r){return "W".equals(r.width)?parseInt(u.meta.get("word_bits"),"word_bits"):parseInt(r.width,"WIDTH "+r.row);}
  static int mode(String m){return switch(m){case "wrap"->0;case "checked"->1;case "saturate"->2;case "trap"->3;default->throw new QctlException("OPERAND: bad MODE "+m);};}
  static void regMaybe(String x){if("_".equals(x)||!x.startsWith("R"))return;reg(x);}
  static int reg(String x){if(x==null||!x.matches("R([0-9]|1[0-5])"))throw new QctlException("OPERAND: bad register "+x);return Integer.parseInt(x.substring(1));}
  static void capMaybe(String x){if("_".equals(x))return;cap(x);}
  static int cap(String x){if(x==null||!x.matches("C([0-9]|1[0-5])"))throw new QctlException("OPERAND: bad capability "+x);return Integer.parseInt(x.substring(1));}
  static int parseInt(String s,String what){try{return Integer.parseInt(s);}catch(Exception e){throw new QctlException("OPERAND: bad "+what+"="+s);}}
  static long parseUnsigned32(String s,String what){try{long v=Long.parseUnsignedLong(s);if(v>0xffffffffL)throw new Exception();return v;}catch(Exception e){throw new QctlException("OPERAND: bad "+what+"="+s);}}
  static BigInteger parseBig(String s){try{return (s.startsWith("0x")||s.startsWith("0X"))?new BigInteger(s.substring(2),16):new BigInteger(s,10);}catch(Exception e){throw new QctlException("OPERAND: bad integer "+s);}}
  static int aux(Unit u,Row r){if("_".equals(r.aux))return 0;Integer idx=u.codeIndex.get(r.aux);if(idx!=null)return idx;long x=parseUnsigned32(r.aux,"AUX "+r.row);if(x>=u.code.size())throw new QctlException("BOUNDS: branch target "+x);return(int)x;}
  static int regOr0(String x){return "_".equals(x)?0:reg(x);}
  static int capOr0(String x){return "_".equals(x)?0:cap(x);}

  static byte[] le(BigInteger v,int bytes){byte[] out=new byte[bytes],be=v.toByteArray();int start=(be.length>1&&be[0]==0)?1:0,n=be.length-start;if(n>bytes)throw new QctlException("OPERAND: immediate too large");for(int i=0;i<n;i++)out[i]=be[be.length-1-i];return out;}
  static void put16(byte[] b,int o,int v){b[o]=(byte)v;b[o+1]=(byte)(v>>>8);}
  static void put32(byte[] b,int o,long v){for(int i=0;i<4;i++)b[o+i]=(byte)(v>>>(8*i));}

  static byte[] compile(Unit u){
    int count=u.code.size(),entry=u.codeIndex.get(u.meta.get("entry"));
    byte[] p=new byte[PROGRAM_HEADER+count*INSN_BYTES];
    System.arraycopy("BWX1".getBytes(StandardCharsets.US_ASCII),0,p,0,4);
    put16(p,4,1);put16(p,6,PROGRAM_HEADER);put32(p,8,INSN_BYTES);put32(p,12,count);put32(p,16,entry);
    for(int i=0;i<count;i++){
      Row r=u.code.get(i);int o=PROGRAM_HEADER+i*INSN_BYTES,w=width(u,r);
      System.arraycopy("BWI1".getBytes(StandardCharsets.US_ASCII),0,p,o,4);
      p[o+4]=(byte)(int)OPMAP.get(r.op);p[o+5]=(byte)mode(r.mode);
      int rd=0,ra=0,rb=0,ci=capOr0(r.cap);long ax=0;
      switch(r.op){
        case "MOVI"->rd=reg(r.out);
        case "MOV"->{rd=reg(r.out);ra=reg(r.a);}
        case "ADD","SUB","MUL","DIVU","MODU","AND","OR","XOR"->{rd=reg(r.out);ra=reg(r.a);rb=reg(r.b);}
        case "NOT","SHL","SHR"->{rd=reg(r.out);ra=reg(r.a);}
        case "CMP"->{ra=reg(r.a);rb=reg(r.b);}
        case "LOAD"->rd=reg(r.out);
        case "STORE","PUSH","SVC"->ra=reg(r.a);
        case "POP"->rd=reg(r.out);
      }
      if(Set.of("JMP","JZ","JNZ").contains(r.op))ax=aux(u,r);else if(!"_".equals(r.aux))ax=parseUnsigned32(r.aux,"AUX "+r.row);
      p[o+6]=(byte)rd;p[o+7]=(byte)ra;p[o+8]=(byte)rb;p[o+9]=(byte)ci;put32(p,o+12,w);put32(p,o+16,ax);
      if("MOVI".equals(r.op)){byte[] imm=le(parseBig(r.a),WORD_BYTES);System.arraycopy(imm,0,p,o+INSN_HEADER,WORD_BYTES);}
    }
    byte[] payload=Arrays.copyOfRange(p,PROGRAM_HEADER,p.length),h;
    try{h=MessageDigest.getInstance("SHA-256").digest(payload);}catch(Exception e){throw new RuntimeException(e);}
    System.arraycopy(h,0,p,28,32);return p;
  }

  static class Cap{long base,len;int perms;boolean valid,sealed;Cap(long b,long l,int p,boolean v,boolean s){base=b;len=l;perms=p;valid=v;sealed=s;}}
  static class VM{
    BigInteger[] r=new BigInteger[16];Cap[] c=new Cap[16];byte[] mem=new byte[MEM_BYTES];long sp;int ip,flags,trap,exit;boolean halted;long steps;
    VM(){Arrays.fill(r,BigInteger.ZERO);c[0]=new Cap(0,MEM_BYTES,3,true,false);c[15]=new Cap(3L*1024*1024,1024L*1024,3,true,false);sp=4L*1024*1024;}
  }
  static BigInteger mask(int w){return BigInteger.ONE.shiftLeft(w).subtract(BigInteger.ONE);}
  static BigInteger fit(BigInteger x,int w){return x.and(mask(w));}
  static void flags(VM v,BigInteger x,int w,boolean carry,boolean over){v.flags=0;if(fit(x,w).signum()==0)v.flags|=F_ZERO;if(carry)v.flags|=F_CARRY;if(over)v.flags|=F_OVER;}
  static void trap(VM v,int code){v.trap=code;v.halted=true;v.exit=128+code;}
  static boolean capRange(VM v,int ci,int perm,long off,long len,long[] addr){if(ci<0||ci>=16||v.c[ci]==null)return false;Cap c=v.c[ci];if(!c.valid||c.sealed||(c.perms&perm)!=perm||off<0||len<0||off>c.len||len>c.len-off)return false;long a=c.base+off;if(a<0||a>MEM_BYTES||len>MEM_BYTES-a)return false;addr[0]=a;return true;}
  static BigInteger fromLE(byte[] m,int off,int n){byte[] be=new byte[n+1];for(int i=0;i<n;i++)be[n-i]=m[off+i];return new BigInteger(be);}
  static void toLE(BigInteger x,byte[] m,int off,int n){byte[] a=le(x,n);System.arraycopy(a,0,m,off,n);}
  static void commit(VM v,Row row,BigInteger raw,int width,boolean exceptional,boolean under,int rd){BigInteger out=fit(raw,width);int md=mode(row.mode);if(!exceptional){v.r[rd]=out;flags(v,out,width,under,false);return;}if(md==0){v.r[rd]=out;flags(v,out,width,under,true);return;}if(md==2){out=under?BigInteger.ZERO:mask(width);v.r[rd]=out;flags(v,out,width,under,true);return;}trap(v,5);}

  static VM run(Unit u){
    VM v=new VM();v.ip=u.codeIndex.get(u.meta.get("entry"));long budget=1_000_000;
    while(!v.halted){
      if(v.ip<0||v.ip>=u.code.size()){trap(v,8);break;}if(v.steps++>=budget){trap(v,11);break;}
      Row x=u.code.get(v.ip);int next=v.ip+1,w=width(u,x),rd=0,ra=0,rb=0,ci=capOr0(x.cap);
      switch(x.op){
        case "MOVI" -> rd=reg(x.out);
        case "MOV" -> {rd=reg(x.out);ra=reg(x.a);}
        case "ADD","SUB","MUL","DIVU","MODU","AND","OR","XOR" -> {rd=reg(x.out);ra=reg(x.a);rb=reg(x.b);}
        case "NOT","SHL","SHR" -> {rd=reg(x.out);ra=reg(x.a);}
        case "CMP" -> {ra=reg(x.a);rb=reg(x.b);}
        case "LOAD" -> rd=reg(x.out);
        case "STORE","PUSH","SVC" -> ra=reg(x.a);
        case "POP" -> rd=reg(x.out);
      }
      long ax=Set.of("JMP","JZ","JNZ").contains(x.op)?aux(u,x):("_".equals(x.aux)?0:parseUnsigned32(x.aux,"AUX "+x.row));
      BigInteger a=v.r[ra],b=v.r[rb],raw;boolean ex;
      switch(x.op){
        case "NOP"->{}
        case "MOVI"->{v.r[rd]=fit(parseBig(x.a),w);flags(v,v.r[rd],w,false,false);}
        case "MOV"->{v.r[rd]=fit(a,w);flags(v,v.r[rd],w,false,false);}
        case "ADD"->{raw=a.add(b);ex=raw.bitLength()>w;commit(v,x,raw,w,ex,false,rd);}
        case "SUB"->{boolean under=a.compareTo(b)<0;raw=a.subtract(b);if(under)raw=raw.mod(BigInteger.ONE.shiftLeft(w));commit(v,x,raw,w,under,under,rd);}
        case "MUL"->{raw=a.multiply(b);ex=raw.bitLength()>w;commit(v,x,raw,w,ex,false,rd);}
        case "DIVU"->{if(b.signum()==0){trap(v,6);break;}v.r[rd]=fit(a,w).divide(fit(b,w));flags(v,v.r[rd],w,false,false);}
        case "MODU"->{if(b.signum()==0){trap(v,6);break;}v.r[rd]=fit(a,w).mod(fit(b,w));flags(v,v.r[rd],w,false,false);}
        case "AND"->{v.r[rd]=fit(a.and(b),w);flags(v,v.r[rd],w,false,false);}
        case "OR"->{v.r[rd]=fit(a.or(b),w);flags(v,v.r[rd],w,false,false);}
        case "XOR"->{v.r[rd]=fit(a.xor(b),w);flags(v,v.r[rd],w,false,false);}
        case "NOT"->{v.r[rd]=fit(a.xor(mask(w)),w);flags(v,v.r[rd],w,false,false);}
        case "SHL"->{raw=fit(a,w).shiftLeft((int)ax);ex=raw.bitLength()>w;commit(v,x,raw,w,ex,false,rd);}
        case "SHR"->{v.r[rd]=fit(a,w).shiftRight((int)ax);flags(v,v.r[rd],w,false,false);}
        case "CMP"->{int cmp=fit(a,w).compareTo(fit(b,w));v.flags=cmp==0?F_ZERO:(cmp<0?F_LESS:F_GREATER);}
        case "JMP"->next=(int)ax;
        case "JZ"->{if((v.flags&F_ZERO)!=0)next=(int)ax;}
        case "JNZ"->{if((v.flags&F_ZERO)==0)next=(int)ax;}
        case "LOAD"->{int n=(w+7)/8;long[] ad={0};if(!capRange(v,ci,1,ax,n,ad)){trap(v,7);break;}v.r[rd]=fit(fromLE(v.mem,(int)ad[0],n),w);flags(v,v.r[rd],w,false,false);}
        case "STORE"->{int n=(w+7)/8;long[] ad={0};if(!capRange(v,ci,2,ax,n,ad)){trap(v,7);break;}toLE(fit(a,w),v.mem,(int)ad[0],n);}
        case "PUSH"->{int n=(w+7)/8;if(v.sp<v.c[15].base+n){trap(v,9);break;}v.sp-=n;long[] ad={0};if(!capRange(v,15,2,v.sp-v.c[15].base,n,ad)){trap(v,9);break;}toLE(fit(a,w),v.mem,(int)ad[0],n);}
        case "POP"->{int n=(w+7)/8;if(v.sp>v.c[15].base+v.c[15].len-n){trap(v,9);break;}long[] ad={0};if(!capRange(v,15,1,v.sp-v.c[15].base,n,ad)){trap(v,9);break;}v.r[rd]=fit(fromLE(v.mem,(int)ad[0],n),w);v.sp+=n;flags(v,v.r[rd],w,false,false);}
        case "SVC"->{if(ax==1){BigInteger low=v.r[ra].and(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE));System.out.println("QCTL SVC: "+x.a+".low64="+low);}else if(ax!=3)trap(v,10);}
        case "HALT"->{v.halted=true;v.exit=(int)ax;}
        default->trap(v,2);
      }
      if(!v.halted)v.ip=next;
    }
    return v;
  }

  static void inspect(Unit u){System.out.println("{");System.out.println("  \"status\":\"PASS\",");System.out.println("  \"module\":\""+esc(u.meta.get("module"))+"\",");System.out.println("  \"profile\":\""+esc(u.meta.get("profile"))+"\",");System.out.println("  \"backend\":\""+esc(u.meta.get("backend"))+"\",");System.out.println("  \"network\":\""+esc(u.meta.get("network"))+"\",");System.out.println("  \"rows\":"+u.rows.size()+",");System.out.println("  \"code_rows\":"+u.code.size()+",");System.out.println("  \"seal\":\""+u.computedSeal+"\"");System.out.println("}");}
  static String esc(String s){return s==null?"":s.replace("\\","\\\\").replace("\"","\\\"");}
  static void usage(){System.out.println("QCTL Runtime 1.0.0");System.out.println("verify <file.qctl>");System.out.println("inspect <file.qctl>");System.out.println("run <file.qctl>");System.out.println("compile-bwx <file.qctl> <out.bwx>");System.out.println("seal <input.qctl> <output.qctl>");}

  public static void main(String[] a){
    try{
      if(a.length<2){usage();System.exit(2);}String cmd=a[0];
      if("seal".equals(cmd)){
        if(a.length!=3)throw new QctlException("seal needs input and output");
        String all=norm(Files.readString(Path.of(a[1]),StandardCharsets.UTF_8));List<String> ls=new ArrayList<>(Arrays.asList(all.split("\n",-1)));int end=ls.indexOf("END");if(end<0)throw new QctlException("PARSE: missing END");
        String canon=String.join("\n",ls.subList(0,end+1))+"\n";String out=canon+"@seal sha256:"+sha256Hex(canon.getBytes(StandardCharsets.UTF_8))+"\n";Files.writeString(Path.of(a[2]),out,StandardCharsets.UTF_8);System.out.println("PASS: sealed "+a[2]);return;
      }
      Unit u=parse(Path.of(a[1]),true);
      switch(cmd){
        case "verify"->System.out.println("PASS: verify module="+u.meta.get("module")+" code_rows="+u.code.size()+" seal="+u.computedSeal);
        case "inspect"->inspect(u);
        case "run"->{VM v=run(u);System.out.println("QCTL RESULT: exit="+v.exit+" trap="+v.trap+" steps="+v.steps);if(v.trap!=0||v.exit!=0)System.exit(v.exit==0?1:v.exit);}
        case "compile-bwx"->{if(a.length!=3)throw new QctlException("compile-bwx needs output path");byte[] b=compile(u);Files.write(Path.of(a[2]),b);System.out.println("PASS: compiled "+u.code.size()+" instructions -> "+a[2]+" sha256="+sha256Hex(b));}
        default->{usage();System.exit(2);}
      }
    }catch(Exception e){System.err.println("FAIL: "+e.getMessage());System.exit(1);}
  }
}
