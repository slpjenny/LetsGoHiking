package com.tave.letsgohiking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class MeasureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        Button mapBtn = findViewById(R.id.mapBtn);
        Button stopBtn = findViewById(R.id.stopBtn);
        //ImageButton pauseBtn = findViewById(R.id.pauseBtn);

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
                Intent intent2 = new Intent(MeasureActivity.this, MyCounterService.class);
                stopService(intent2); //? 값 넘어간거 맞나?

                //내가 만든 커스텀 다이얼로그 클래스를 이용해 다이얼로그 생성하기
                CustomDialog customDialog = new CustomDialog(MeasureActivity.this);

                //커스텀 다이얼로그 호출
                //커스텀 다이얼로그의 결과를 출력할 TEXTVIEW를 매개변수로 같이 넘겨준다 -> 기록 저장 액티비티에서 이루어져야 함.
                customDialog.callFunction();

                MainActivity.startBtn.setBackgroundResource(R.drawable.start_image);
            }
        });

//        pauseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

}
