package com.tave.letsgohiking;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naver.maps.geometry.LatLng;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

//import static com.tave.letsgohiking.CustomDialog.SAVE_OR_NOT;
import static com.tave.letsgohiking.recordFragment.adapter;

public class MeasureActivity extends AppCompatActivity {
    TextView timeTextView; // 소요시간 띄울 TextView
    TextView distanceTextView; // 거리 띄울 TextView
    TextView paceTextView; // 순간 페이스 띄울 TextView
    TextView avgPaceTextView; // 평균 페이스 띄울 TextView

    MyService myService;
    boolean isService = false; //서비스 중인 확인용용

    private Handler handler;
    private Timer timer;

    private Location lastLocation;
    private double longitude;
    private double latitude;
    public static String finalDistance;
    private long minTime;
    private double speed;
    private double avgSpeed; // 평균 초속
    //private int pace;
    private int count; // 누적 시간 (초 단위)
    private int min;
    private int sec;
    //private Location location;
    private double pace; // 순간 페이스
    private double avgPace; // 평균 페이스
    private int avgPaceMin;
    private int avgPaceSec;
    private int paceMin;
    private int paceSec;

    private List<LatLng> placeList = new ArrayList<>();

    public static String leadTime;
    public static String totalDistance;

    public static recordFragment recordFragment1 = new recordFragment();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        timeTextView=findViewById(R.id.time);
        distanceTextView=findViewById(R.id.totalDistance);
        paceTextView=findViewById(R.id.pace);
        avgPaceTextView=findViewById(R.id.avgPace);

        Button mapBtn = findViewById(R.id.mapBtn);
        Button stopBtn = findViewById(R.id.stopBtn);
        //ImageButton pauseBtn = findViewById(R.id.pauseBtn);


        Intent intent = new Intent(MeasureActivity.this, MyService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); //MyGPSService와 연결

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.putExtra("Go to mapfragment","go");
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                Bundle bundle = new Bundle();
                bundle.putSerializable("placeList", (Serializable) placeList);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                leadTime = timeTextView.getText().toString();
                totalDistance = distanceTextView.getText().toString();
                //커스텀 다이얼로그 클래스를 이용해 다이얼로그 생성하기
                CustomDialog customDialog = new CustomDialog(MeasureActivity.this);

                //커스텀 다이얼로그 호출
                //커스텀 다이얼로그의 결과를 출력할 TEXTVIEW를 매개변수로 같이 넘겨준다 -> 기록 저장 액티비티에서 이루어져야 함.
                customDialog.callFunction();

                MainActivity.startBtn.setBackgroundResource(R.drawable.start_button);

                //호출 완료되고 dismiss()후에, record fragment로 가도록 해야한다...
                //저장 했을 때만 이동하도록.

            }
        });

        // 일정한 주기마다 MyGpsService에서 정보를 가져온다.
        timer = new Timer(true); //인자가 Daemon 설정인데 true 여야 죽지 않음.
        handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (myService != null) {
                            finalDistance = myService.getTotalDistance();

                            count = myService.getCount();

                            speed = myService.getSpeed();
                            pace = 1/speed;
                            paceMin = (int)pace/60;
                            paceSec = (int)pace%60;

                            if (speed==0.0) {
                                paceTextView.setText("-\'-\"");
                            }

                            else {
                                paceTextView.setText(paceMin+"\'"+paceSec+"\"");
                            }

                            avgSpeed = myService.getkmTotalDistance()/count;
                            avgPace = 1/avgSpeed;
                            avgPaceMin = (int)avgPace/60;
                            avgPaceSec = (int)avgPace%60;

                            if (avgSpeed==0.0){
                                avgPaceTextView.setText("-\'-\"");
                            }

                            else{
                                avgPaceTextView.setText(avgPaceMin+"\'"+avgPaceSec+"\"");
                            }


                            distanceTextView.setText(finalDistance+" KM");

                            lastLocation = myService.getLastLocation();
                            speed = myService.getSpeed();
                            //pace = myService.getPace();
                            //location = myService.getLocation();

                                /*
                                latitude = lastLocation.getLatitude();
                                longitude = lastLocation.getLongitude();
                                placeList.add(new LatLng(latitude, longitude));

                                 */
                            placeList = myService.getList();

                            min = count/60;
                            sec = count%60;

                            if (min < 1) {
                                if (sec < 10)
                                    timeTextView.setText("00:0" + Integer.toString(sec));
                                else
                                    timeTextView.setText("00:" + Integer.toString(sec));
                            } else if (min < 10) {
                                if (sec < 10)
                                    timeTextView.setText("0" + Integer.toString(min) + ":0" + Integer.toString(sec));
                                else
                                    timeTextView.setText("0" + Integer.toString(min) + ":" + Integer.toString(sec));
                            } else if (min < 60) {
                                if (sec < 10)
                                    timeTextView.setText(Integer.toString(min) + ":0" + Integer.toString(sec));
                                else
                                    timeTextView.setText(Integer.toString(min) + ":" + Integer.toString(sec));
                            }
                            //textView.setText(Integer.toString(count));
                        }
                        //Log.d("Measure", "위도: "+latitude+"경도: "+longitude);
                        //Log.d("Measure", "pace: "+pace);
                        //Log.d("Measure", "count: "+count);
                        //Log.d("Measure", "distance"+);
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

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isService) {
            unbindService(conn);
            isService = false;
        }

    }


    public class CustomDialog {
        private Context context;

        public CustomDialog(Context context) {
            this.context = context;
        }

        // 호출할 다이얼로그 함수를 정의한다.
        public void callFunction() {
            //final TextView main_label ()안에 넣어주기 아직 안함.

            // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
            final Dialog mydlg = new Dialog(context);

            // 액티비티의 타이틀바를 숨긴다.
            mydlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // 커스텀 다이얼로그의 레이아웃을 설정.
            mydlg.setContentView(R.layout.custom_dialog);

            // 커스텀 다이얼로그를 노출한다.
            mydlg.show();

            // 커스텀 다이얼로그의 각 위젯들을 정의한다.
            final EditText recordTitle = (EditText) mydlg.findViewById(R.id.recordTitle);
            final Button okButton = (Button) mydlg.findViewById(R.id.okButton);
            final Button cancelButton = (Button) mydlg.findViewById(R.id.cancelButton);


            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //다이얼로그로 저장한 제목이름 string으로 받아오기
                    String recordtitle = recordTitle.getText().toString();

                    //현재 날짜로 기록 저장
                    Date currentDate = Calendar.getInstance().getTime();
                    String date_text = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(currentDate);


                    //record_item 날짜,제목,소요시간,총 거리 초기화 완료
                    recordObject saveRecordItems = new recordObject(date_text, recordtitle, totalDistance, leadTime, avgPaceMin+"\'"+avgPaceSec+"\"");
                    adapter.add(saveRecordItems);

                    Toast.makeText(context, "기록을 저장했습니다.", Toast.LENGTH_SHORT).show();

                    //서비스 종료 구현
                    if(isService) {
                        unbindService(conn);
                        isService = false;
                    }

                    //Measure Activity로 값 전달 (저장)
                    Intent saveIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                    saveIntent.putExtra("SAVE OR NOT", "ok");
                    saveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(saveIntent);
                    finish();



                    // 커스텀 다이얼로그를 종료.
                    mydlg.dismiss();

                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "저장을 취소합니다.", Toast.LENGTH_SHORT).show();

                    //Measure Activity로 값 전달 (취소)
                    Intent saveIntent = new Intent(context.getApplicationContext(), MeasureActivity.class);
                    saveIntent.putExtra("SAVE OR NOT", "no");
//                    saveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(saveIntent);
//                    finish();

                    // 커스텀 다이얼로그를 종료한다.
                    mydlg.dismiss();
                }
            });

        }

    }


}
