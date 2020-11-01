package com.tave.letsgohiking;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.tave.letsgohiking.recordFragment.adapter;
//import static com.tave.letsgohiking.recordFragment.itemList;

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

                //현재 날짜 출력
                Date currentDate= Calendar.getInstance().getTime();
                String date_text= new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(currentDate);

                recordObject saveRecordItems= new recordObject(date_text,recordtitle,"5km"," 3:55");
                adapter.add(saveRecordItems);

                Toast.makeText(context, "기록을 저장했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료.
                mydlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "저장을 취소합니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                mydlg.dismiss();
            }
        });

    }

}
