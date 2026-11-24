package com.vanigam.clothing;
import android.content.Context;
public final class Activation {
 public static boolean active(Context c){return UpdatePolicy.acceptedDigest(c.getSharedPreferences("activation",0).getString("keyDigest",""));}
 public static void activate(Context c,String key)throws Exception{if(!UpdatePolicy.accepts(key))throw new Exception("Invalid activation key. Contact the app owner.");if(!c.getSharedPreferences("activation",0).edit().putString("keyDigest",UpdatePolicy.digest(key.trim())).commit())throw new Exception("Activation could not be saved. Try again.");}
 public static void require(Context c)throws Exception{if(!active(c))throw new Exception("Activate this app before continuing.");}
}
