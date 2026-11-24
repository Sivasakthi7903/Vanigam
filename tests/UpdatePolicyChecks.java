import com.vanigam.clothing.UpdatePolicy;
public class UpdatePolicyChecks {
 static int n;static void ok(boolean condition)throws Exception{if(!condition)throw new Exception("Check failed: "+n);n++;}
 public static void main(String[] keys)throws Exception {
  ok(keys.length==2);ok(UpdatePolicy.accepts(keys[0]));ok(UpdatePolicy.accepts(keys[1]));ok(UpdatePolicy.accepts(" "+keys[0]+" "));ok(!UpdatePolicy.accepts("0000000000"));ok(!UpdatePolicy.accepts(""));ok(!UpdatePolicy.accepts(null));ok(!UpdatePolicy.accepts(keys[0]+"1"));ok(!UpdatePolicy.accepts("+91"+keys[0]));
  ok(UpdatePolicy.repo("https://github.com/owner/repository.git").equals("owner/repository"));ok(UpdatePolicy.repo("owner/repository").equals("owner/repository"));boolean rejected=false;try{UpdatePolicy.repo("https://evil.com/repo");}catch(Exception e){rejected=true;}ok(rejected);
  ok(UpdatePolicy.compare("v1.2.0","1.1.0")>0);ok(UpdatePolicy.compare("v1.10.0","1.2.0")>0);ok(UpdatePolicy.compare("v1.1.0","1.1.0")==0);ok(UpdatePolicy.compare("v1.0.0","1.1.0")<0);
  ok(UpdatePolicy.trusted("https://github.com/owner/repo/releases/download/v1.2.0/app.apk"));ok(UpdatePolicy.trusted("https://release-assets.githubusercontent.com/file.apk"));ok(!UpdatePolicy.trusted("http://github.com/file.apk"));ok(!UpdatePolicy.trusted("https://github.com.evil.com/file.apk"));ok(!UpdatePolicy.trusted("https://github.com@evil.com/file.apk"));ok(!UpdatePolicy.trusted("https://github.com:444/file.apk"));
  System.out.println("PASS "+n+" activation, repository, version and trusted-download policy checks");
 }
}
