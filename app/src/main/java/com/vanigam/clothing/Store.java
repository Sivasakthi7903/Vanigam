package com.vanigam.clothing;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import org.json.*;
public final class Store extends SQLiteOpenHelper {
 private static Store instance;
 public static synchronized Store get(Context c){if(instance==null)instance=new Store(c.getApplicationContext());return instance;}
 private Store(Context c){super(c,"clothing.db",null,1);}
 public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE store(id INTEGER PRIMARY KEY CHECK(id=1), body TEXT NOT NULL)");try{ContentValues v=new ContentValues();v.put("id",1);v.put("body",Core.seed().toString());db.insertOrThrow("store",null,v);}catch(Exception e){throw new IllegalStateException(e);}}
 public void onUpgrade(SQLiteDatabase db,int a,int b){throw new IllegalStateException("Unsupported database upgrade");}
 public synchronized JSONObject read()throws Exception{try(Cursor c=getReadableDatabase().rawQuery("SELECT body FROM store WHERE id=1",null)){if(!c.moveToFirst())throw new Exception("Store data is missing.");return new JSONObject(c.getString(0));}}
 public synchronized JSONObject act(String action,JSONObject args,String user)throws Exception{SQLiteDatabase db=getWritableDatabase();db.beginTransaction();try{JSONObject state=read();JSONObject result=Core.act(state,action,args,user);state.put("version",state.getLong("version")+1);write(db,state);db.setTransactionSuccessful();return result;}finally{db.endTransaction();}}
 private void write(SQLiteDatabase db,JSONObject state)throws Exception{ContentValues cv=new ContentValues();cv.put("body",state.toString());if(db.update("store",cv,"id=1",null)!=1)throw new Exception("Data save failed.");}
 public synchronized void restore(JSONObject restored,String uid)throws Exception{if(!Core.user(read(),uid).getBoolean("admin"))throw new Exception("Administrator only.");Core.validateBackup(restored);SQLiteDatabase db=getWritableDatabase();db.beginTransaction();try{restored.put("version",Math.max(restored.getLong("version"),read().getLong("version"))+1);write(db,restored);db.setTransactionSuccessful();}finally{db.endTransaction();}}
}
