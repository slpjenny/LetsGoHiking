package com.tave.letsgohiking;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyCounterService extends Service {
    public MyCounterService() {
    }

    private int count;//

    @Override
    public void onCreate() {
        super.onCreate();

        Thread counter=new Thread(new Counter());
        counter.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isStop;

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop=true;
    }

    private class Counter implements Runnable {
        private Handler handler=new Handler();

        @Override
        public void run(){
            for (count=0; ; count++){
                if (isStop){
                    break;
                }

                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), count+"", Toast.LENGTH_SHORT).show();
                        Log.d("COUNT", count+"");
                    }
                });

                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            handler.post(new Runnable(){
                @Override
                public void run() {
                    //Toast.makeText(getApplicationContext(), "서비스 종료", Toast.LENGTH_SHORT).show();
                    Log.d("End", "서비스 종료");
                }
            });
        }

    }
}
