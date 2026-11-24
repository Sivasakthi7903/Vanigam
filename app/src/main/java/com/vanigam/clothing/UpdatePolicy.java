package com.vanigam.clothing;
import java.net.URI;
import java.security.MessageDigest;
public final class UpdatePolicy {
 private static final String[] KEYS={"8dbb24a1fa8ff16470454adfad5b0fe96a5f3cf917c99003c7ba9c7fe75b5175","51cbf602e0a3a3e40916123fb1d58778ce39a03de5bd32afa0f05efb92a15965"};
 public static String digest(String key)throws Exception{byte[] hash=MessageDigest.getInstance("SHA-256").digest(key.getBytes("UTF-8"));StringBuilder s=new StringBuilder();for(byte b:hash)s.append(String.format(java.util.Locale.US,"%02x",b));return s.toString();}
 public static boolean acceptedDigest(String hash){for(String key:KEYS)if(key.equals(hash))return true;return false;}
 public static boolean accepts(String key)throws Exception{return key!=null&&key.trim().matches("[0-9]{10}")&&acceptedDigest(digest(key.trim()));}
 public static String repo(String value)throws Exception{value=value.trim();if(value.startsWith("https://github.com/"))value=value.substring(19);if(value.endsWith("/"))value=value.substring(0,value.length()-1);if(value.endsWith(".git"))value=value.substring(0,value.length()-4);if(!value.matches("[A-Za-z0-9][A-Za-z0-9-]{0,38}/[A-Za-z0-9_.-]{1,100}"))throw new Exception("Enter GitHub owner/repository or its HTTPS URL.");return value;}
 public static int compare(String a,String b)throws Exception{String[] x=version(a),y=version(b);for(int i=0;i<3;i++){int c=Integer.compare(Integer.parseInt(x[i]),Integer.parseInt(y[i]));if(c!=0)return c;}return 0;}
 private static String[] version(String v)throws Exception{if(v.startsWith("v"))v=v.substring(1);if(!v.matches("[0-9]{1,5}\\.[0-9]{1,5}\\.[0-9]{1,5}"))throw new Exception("Release tag must use vMAJOR.MINOR.PATCH, for example v1.2.0.");return v.split("\\.");}
 public static boolean trusted(String url){try{URI u=new URI(url);String h=u.getHost();return u.getScheme().equals("https")&&u.getUserInfo()==null&&(u.getPort()==-1||u.getPort()==443)&&("api.github.com".equals(h)||"github.com".equals(h)||"release-assets.githubusercontent.com".equals(h)||"objects.githubusercontent.com".equals(h)||"github-releases.githubusercontent.com".equals(h));}catch(Exception e){return false;}}
}
