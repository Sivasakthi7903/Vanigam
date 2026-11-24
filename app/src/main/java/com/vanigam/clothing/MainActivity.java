package com.vanigam.clothing;
import android.app.*;
import android.content.*;
import android.os.*;
import android.graphics.*;
import android.net.Uri;
import android.webkit.*;
import android.view.*;
import org.json.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public final class MainActivity extends Activity {
 WebView web,receipt;volatile String uid="";byte[] export;final ExecutorService worker=Executors.newSingleThreadExecutor();long lockedUntil;int failures;boolean restoring;
 SharedPreferences backupPrefs(){return getSharedPreferences("backup",0);}
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.rgb(16,35,63));getWindow().setNavigationBarColor(Color.rgb(16,35,63));getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);web=new WebView(this);web.setBackgroundColor(Color.WHITE);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(false);web.getSettings().setAllowContentAccess(false);web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);web.addJavascriptInterface(new Bridge(),"Native");web.setWebViewClient(new WebViewClient(){@Override public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){String path=r.getUrl().getPath();if(r.getUrl().getHost()!=null&&r.getUrl().getHost().equals("app.vanigam.local")&&path!=null&&path.matches("/(index.html|app.js|style.css)")){try{return new WebResourceResponse(path.endsWith(".js")?"application/javascript":path.endsWith(".css")?"text/css":"text/html","UTF-8",getAssets().open(path.substring(1)));}catch(Exception e){}}return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));}public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return !r.getUrl().toString().startsWith("https://app.vanigam.local/");}});setContentView(web);web.loadUrl("https://app.vanigam.local/index.html");Backup.schedule(this);autoBackup();}
 void autoBackup(){if(!Activation.active(this))return;worker.execute(()->{try{Backup.run(this,false);}catch(Exception ignored){}});}
 void event(String message){runOnUiThread(()->web.evaluateJavascript("window.nativeNotice&&nativeNotice("+JSONObject.quote(message)+")",null));}
 void notifyState(){runOnUiThread(()->web.evaluateJavascript("window.reload&&reload()",null));}
 JSONObject authorized()throws Exception{Activation.require(this);JSONObject s=Store.get(this).read();Core.user(s,uid);return s;}
 void requireAdmin()throws Exception{if(!Core.user(authorized(),uid).getBoolean("admin"))throw new Exception("Administrator only.");}
 public final class Bridge {
  @JavascriptInterface public synchronized String call(String action,String json){try{JSONObject a=new JSONObject(json),result;
   if(restoring)throw new Exception("Restore in progress.");
   if(action.equals("activate")){Activation.activate(MainActivity.this,a.getString("key"));autoBackup();return Core.obj("ok",true,"data",Core.obj("activated",true)).toString();}
   if(action.equals("state")&&!Activation.active(MainActivity.this))return Core.obj("ok",true,"data",Core.obj("activated",false)).toString();
   Activation.require(MainActivity.this);
   if(action.equals("state")){JSONObject state=Core.safe(Store.get(MainActivity.this).read(),uid);state.put("activated",true);state.put("updates",Core.obj("repo",Updater.repo(MainActivity.this),"version",Updater.version(MainActivity.this)));state.put("backup",Core.obj("folder",!backupPrefs().getString("folder","").isEmpty(),"last",backupPrefs().getString("last",""),"error",backupPrefs().getString("error","")));result=state;}
   else if(action.equals("updateConfigure")){requireAdmin();Updater.configure(MainActivity.this,a.getString("repo"));result=Core.obj("ok",true);}
   else if(action.equals("updateCheck")){requireAdmin();Updater.check(MainActivity.this);result=Core.obj("ok",true);}
   else if(action.equals("updateInstall")){requireAdmin();runOnUiThread(()->Updater.install(MainActivity.this));result=Core.obj("ok",true);}
   else if(action.equals("login")){if(System.currentTimeMillis()<lockedUntil)throw new Exception("Too many attempts. Try again in a minute.");try{uid=Core.login(Store.get(MainActivity.this).read(),a.getString("pin"));failures=0;}catch(Exception ex){if(++failures>=5){lockedUntil=System.currentTimeMillis()+60000;failures=0;}throw ex;}result=Core.obj("ok",true);}
   else if(action.equals("logout")){uid="";result=Core.obj("ok",true);}
   else if(action.equals("quote")){JSONObject s=authorized();result=Core.quote(s,a);}
   else if(action.equals("report")){JSONObject s=authorized();String type=a.getString("type");if(Arrays.asList("pl","stockreport","backup").contains(type))requireAdmin();List<Excel.Sheet> sheets=Excel.report(Core.safe(s,uid),type,a.getString("from"),a.getString("to"));JSONArray data=new JSONArray();for(Excel.Sheet sh:sheets){JSONArray rows=new JSONArray();for(Object[] row:sh.rows){JSONArray r=new JSONArray();for(Object v:row)r.put(v);rows.put(r);}data.put(Core.obj("name",sh.name,"rows",rows));}result=Core.obj("sheets",data);}
   else if(action.equals("export")){JSONObject s=authorized();String type=a.getString("type");if(Arrays.asList("pl","stockreport","backup").contains(type))requireAdmin();List<Excel.Sheet> sheets=Excel.report(type.equals("backup")?s:Core.safe(s,uid),type,type.equals("backup")?"0000-01-01":a.getString("from"),type.equals("backup")?"9999-12-31":a.getString("to"));ByteArrayOutputStream out=new ByteArrayOutputStream();Excel.write(out,sheets);export=out.toByteArray();runOnUiThread(()->{Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT).setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").addCategory(Intent.CATEGORY_OPENABLE).putExtra(Intent.EXTRA_TITLE,"Vanigam-"+type+"-"+Core.today()+".xlsx");startActivityForResult(intent,201);});result=Core.obj("ok",true);}
   else if(action.equals("folder")){requireAdmin();runOnUiThread(()->startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION),202));result=Core.obj("ok",true);}
   else if(action.equals("backupNow")){requireAdmin();worker.execute(()->{try{event(Backup.run(MainActivity.this,true));notifyState();}catch(Exception ex){event(ex.getMessage());notifyState();}});result=Core.obj("ok",true);}
   else if(action.equals("restore")){requireAdmin();runOnUiThread(()->new AlertDialog.Builder(MainActivity.this).setTitle("Replace this store from backup?").setMessage("This replaces current local records. Export a backup first. You must sign in again with the restored PIN.").setNegativeButton("Cancel",null).setPositiveButton("Choose backup",(d,w)->startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").addCategory(Intent.CATEGORY_OPENABLE),204)).show());result=Core.obj("ok",true);}
   else if(action.equals("pickLogo")){requireAdmin();runOnUiThread(()->startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE),203));result=Core.obj("ok",true);}
   else if(action.equals("print")){JSONObject s=authorized(),b=Core.find(s.getJSONArray("bills"),a.getString("id"));if(!Core.user(s,uid).getBoolean("admin")&&!b.getString("user").equals(uid))throw new Exception("Only your bills are available.");runOnUiThread(()->{try{receipt=BillPrint.print(MainActivity.this,b);}catch(Exception ex){event(ex.getMessage());}});result=Core.obj("ok",true);}
   else {result=Store.get(MainActivity.this).act(action,a,uid);if(action.equals("setup"))uid=result.getString("uid");}
   return Core.obj("ok",true,"data",result).toString();
  }catch(Exception ex){return "{\"ok\":false,\"error\":"+JSONObject.quote(ex.getMessage()==null?"Operation failed":ex.getMessage())+"}";}}
 }
 @Override protected void onActivityResult(int code,int status,Intent data){super.onActivityResult(code,status,data);if(status!=RESULT_OK||data==null||data.getData()==null)return;Uri uri=data.getData();worker.execute(()->{try{
  if(code==201){byte[] bytes=export;if(bytes==null)throw new Exception("Export expired. Please export again.");try(OutputStream stream=getContentResolver().openOutputStream(uri,"w")){if(stream==null)throw new Exception("Cannot open destination.");stream.write(bytes);}export=null;event("Excel file saved.");}
  else if(code==202){requireAdmin();int flags=data.getFlags()&(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);getContentResolver().takePersistableUriPermission(uri,flags);backupPrefs().edit().putString("folder",uri.toString()).apply();event(Backup.run(this,true));notifyState();}
  else if(code==203){requireAdmin();BitmapFactory.Options options=new BitmapFactory.Options();options.inJustDecodeBounds=true;try(InputStream in=getContentResolver().openInputStream(uri)){BitmapFactory.decodeStream(in,null,options);}options.inSampleSize=1;while(options.outWidth/options.inSampleSize>600||options.outHeight/options.inSampleSize>600)options.inSampleSize*=2;options.inJustDecodeBounds=false;Bitmap bmp;try(InputStream in=getContentResolver().openInputStream(uri)){bmp=BitmapFactory.decodeStream(in,null,options);}if(bmp==null)throw new Exception("Select a valid image.");ByteArrayOutputStream out=new ByteArrayOutputStream();bmp.compress(Bitmap.CompressFormat.PNG,100,out);bmp.recycle();Store.get(this).act("logo",Core.obj("logo",java.util.Base64.getEncoder().encodeToString(out.toByteArray())),uid);notifyState();event("Shop logo saved.");}
  else if(code==204){requireAdmin();restoring=true;JSONObject restored;try(InputStream stream=getContentResolver().openInputStream(uri)){restored=Backup.restoreData(stream);}Store.get(this).restore(restored,uid);uid="";backupPrefs().edit().remove("version").remove("day").apply();restoring=false;notifyState();event("Backup restored. Sign in using the restored PIN.");}
 }catch(Exception ex){restoring=false;event(ex.getMessage());notifyState();}});}
 @Override protected void onResume(){super.onResume();if(web!=null)autoBackup();}
 @Override public void onBackPressed(){web.evaluateJavascript("window.goBack&&goBack()",null);}
 @Override protected void onDestroy(){super.onDestroy();worker.shutdown();if(web!=null){web.removeJavascriptInterface("Native");web.destroy();}if(receipt!=null)receipt.destroy();}
}
