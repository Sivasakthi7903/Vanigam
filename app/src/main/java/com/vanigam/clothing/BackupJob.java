package com.vanigam.clothing;
import android.app.job.*;
public class BackupJob extends JobService {
 public boolean onStartJob(JobParameters params){new Thread(()->{boolean retry=false;try{Backup.run(this,false);}catch(Exception ex){retry=true;}jobFinished(params,retry);},"daily-excel-backup").start();return true;}
 public boolean onStopJob(JobParameters params){return true;}
}
