package com.tave.letsgohiking;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MeasureActivity extends AppCompatActivity {

    MyService myService;
    boolean isService = false; //서비스 중인 확인용용

    private Handler handler;
    private Timer timer;

    private Location lastLocation;
    private double longitude;
    private double latitude;
    private double distance;
    private long minTime;
    private double speed;
    private int pace;
    private int count;

    private List<LatLng> placeList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        Button mapBtn = findViewById(R.id.mapBtn);
        Button stopBtn = findViewById(R.id.stopBtn);
        //ImageButton pauseBtn = findViewById(R.id.pauseBtn);

       Intent intent = new Intent(MeasureActivity.this, MyService.class);
       bindService(intent, conn, Context.BIND_AUTO_CREATE); //MyGPSService와 연결

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //Task 내에 이미 활성화된 activity 를 다시 활성화 할때,
                                                                        //새로 생성하지 않고 재사용하는 flag
                startActivity(intent);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //내가 만든 커스텀 다이얼로그 클래스를 이용해 다이얼로그 생성하기
                CustomDialog customDialog = new CustomDialog(MeasureActivity.this);

                //커스텀 다이얼로그 호출
                //커스텀 다이얼로그의 결과를 출력할 TEXTVIEW를 매개변수로 같이 넘겨준다 -> 기록 저장 액티비티에서 이루어져야 함.
                customDialog.callFunction();

                MainActivity.startBtn.setBackgroundResource(R.drawable.start_button);

                //서비스 종료
                if(isService) {
                    unbindService(conn);
                    isService=false;
                }
            }
        });

        // 일정한 주기마다 MyGpsService에서 정보를 가져온다.
        timer = new Timer(true); //인자가 Daemon 설정인데 true 여야 죽지 않음.
        handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable(){
                    public void run(){
                        if(myService !=null) {
                            distance = myService.getDistance();
                            lastLocation = myService.getLastLocation();
                            speed = myService.getSpeed();
                            pace = myService.getPace();
                            if(lastLocation!=null) {
                                latitude = lastLocation.getLatitude();
                                longitude = lastLocation.getLongitude();
                            }
                            count = myService.getCount();
                        }
                        Log.d("Measure", "위도: "+latitude+"경도: "+longitude);
                        Log.d("Measure", "pace: "+pace);
                        Log.d("Measure", "count: "+count);
                    }
                });
            }
        }, 0, 1000); //시작지연시간 0, 주기 1초



//        pauseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            MyService.MyBinder mb = (MyService.MyBinder) service;
            myService = mb.getService(); // 서비스가 제공하는 메소드를 호출하여 서비스쪽 객체를 전달받을수 있음
            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isService) {
            unbindService(conn);
            isService = false;
        }
    }
}