package com.vanigam.clothing;
import android.content.*;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.app.job.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.json.*;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
public final class Backup {
 public static void schedule(Context c){JobScheduler scheduler=(JobScheduler)c.getSystemService(Context.JOB_SCHEDULER_SERVICE);scheduler.schedule(new JobInfo.Builder(104,new ComponentName(c,BackupJob.class)).setPersisted(true).setPeriodic(6*60*60*1000L).build());}
 public static synchronized String run(Context c,boolean force)throws Exception{if(!Activation.active(c))return "Activate the app first.";SharedPreferences p=c.getSharedPreferences("backup",0);String folder=p.getString("folder","");if(folder.isEmpty())return "Choose a backup folder in Settings.";JSONObject s=Store.get(c).read();if(s.getJSONArray("users").length()==0)return "Create the owner first.";String day=Core.today();if(!force&&day.equals(p.getString("day",""))&&s.getLong("version")==p.getLong("version",-1))return "Backup is up to date.";
  Uri tree=Uri.parse(folder),parent=DocumentsContract.buildDocumentUriUsingTree(tree,DocumentsContract.getTreeDocumentId(tree));String name="Vanigam-Clothing-"+day+"-"+System.currentTimeMillis()+".xlsx";Uri dest=null;
  try{dest=DocumentsContract.createDocument(c.getContentResolver(),parent,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",name);if(dest==null)throw new Exception("Folder cannot create files.");try(OutputStream stream=c.getContentResolver().openOutputStream(dest,"w")){if(stream==null)throw new Exception("Cannot write backup.");Excel.write(stream,Excel.report(s,"backup","0000-01-01","9999-12-31"));}p.edit().putString("day",day).putString("last",Core.now()).putString("error","").putString("file",name).putLong("version",s.getLong("version")).apply();return "Backup saved: "+name;}catch(Exception ex){if(dest!=null)try{DocumentsContract.deleteDocument(c.getContentResolver(),dest);}catch(Exception ignored){}p.edit().putString("error","Backup failed: "+ex.getMessage()).apply();throw ex;}
 }
 public static JSONObject restoreData(InputStream stream)throws Exception{try(ZipInputStream zip=new ZipInputStream(stream)){ZipEntry entry;int count=0,total=0;while((entry=zip.getNextEntry())!=null){if(++count>100)throw new Exception("Invalid workbook.");ByteArrayOutputStream buffer=new ByteArrayOutputStream();byte[] bytes=new byte[8192];int n;while((n=zip.read(bytes))!=-1){total+=n;if(total>80*1024*1024)throw new Exception("Backup is too large.");buffer.write(bytes,0,n);}if(!entry.getName().startsWith("xl/worksheets/"))continue;XmlPullParser parser=Xml.newPullParser();parser.setInput(new ByteArrayInputStream(buffer.toByteArray()),"UTF-8");String cell="";boolean signature=false;StringBuilder encoded=new StringBuilder();for(int event=parser.getEventType();event!=XmlPullParser.END_DOCUMENT;event=parser.next()){if(event==XmlPullParser.START_TAG&&parser.getName().equals("c"))cell=parser.getAttributeValue(null,"r");if(event==XmlPullParser.START_TAG&&parser.getName().equals("t")&&cell!=null&&cell.startsWith("B")){String value=parser.nextText();if(cell.equals("B1"))signature=value.equals("Base64 JSON - do not edit");else if(signature)encoded.append(value);}}if(signature){JSONObject s=new JSONObject(new String(java.util.Base64.getDecoder().decode(encoded.toString()),"UTF-8"));Core.validateBackup(s);return s;}}}throw new Exception("Choose an original Vanigam daily-backup workbook, not a report export.");}
}
