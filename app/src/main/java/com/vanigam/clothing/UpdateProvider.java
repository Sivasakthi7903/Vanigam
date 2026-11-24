package com.vanigam.clothing;
import android.content.*;import android.database.*;import android.net.Uri;import android.os.ParcelFileDescriptor;import android.provider.OpenableColumns;import java.io.*;
/** Only exposes the single verified APK through an explicit read URI grant. */
public final class UpdateProvider extends ContentProvider {
 public boolean onCreate(){return true;}
 static File file(Context c){return new File(c.getCacheDir(),"vanigam-update.apk");}
 private void check(Uri uri){if(!"/update.apk".equals(uri.getPath()))throw new IllegalArgumentException("Unknown update file");}
 public String getType(Uri u){check(u);return "application/vnd.android.package-archive";}
 public ParcelFileDescriptor openFile(Uri u,String mode)throws FileNotFoundException{check(u);if(!"r".equals(mode))throw new FileNotFoundException("Read only");return ParcelFileDescriptor.open(file(getContext()),ParcelFileDescriptor.MODE_READ_ONLY);}
 public Cursor query(Uri u,String[] projection,String selection,String[] args,String sort){check(u);String[] cols=projection==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:projection;MatrixCursor c=new MatrixCursor(cols);Object[] values=new Object[cols.length];for(int i=0;i<cols.length;i++){if(cols[i].equals(OpenableColumns.DISPLAY_NAME))values[i]="Vanigam-Clothing-update.apk";if(cols[i].equals(OpenableColumns.SIZE))values[i]=file(getContext()).length();}c.addRow(values);return c;}
 public Uri insert(Uri u,ContentValues v){throw new UnsupportedOperationException();}public int update(Uri u,ContentValues v,String s,String[]a){throw new UnsupportedOperationException();}public int delete(Uri u,String s,String[]a){throw new UnsupportedOperationException();}
}
