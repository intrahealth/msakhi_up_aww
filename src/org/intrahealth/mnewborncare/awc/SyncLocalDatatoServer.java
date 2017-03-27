package org.intrahealth.mnewborncare.awc;
import java.util.Random;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class SyncLocalDatatoServer extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Service Created",300);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service Destroy",300);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Toast.makeText(this,"Service LowMemory",300);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(this,"Service start",300);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        Toast.makeText(this,"task perform in service",300);
        ThreadDemo td=new ThreadDemo();
        td.start();
        return super.onStartCommand(intent, flags, startId);
    }
    
    private class ThreadDemo extends Thread{
        @Override
        public void run() {
            super.run();
            try{
            sleep(70*1000);    
            handler.sendEmptyMessage(0);
            }catch(Exception e){
                e.getMessage();
            }
        }
    }
   private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        //showAppNotification();
    }
   };
}