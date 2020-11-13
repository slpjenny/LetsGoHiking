package com.tave.letsgohiking;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends Fragment {
    private FirebaseAuth mAuth;
    Bitmap bitmap;
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup)inflater.inflate(R.layout.fragment_setting, container, false);

        mAuth=FirebaseAuth.getInstance();
        final FirebaseUser user=mAuth.getCurrentUser();

        CircleImageView user_profile=rootView.findViewById(R.id.user_profile);
        Thread mThread=new Thread(){
            @Override
            public void run() {
                try{
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException ee) {
                    ee.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };

        mThread.start();

        try{
            mThread.join();
            user_profile.setImageBitmap(bitmap);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        // 로그인 정보 textView에 받아오기
        textView=rootView.findViewById(R.id.textView);

        AccountManager manager = AccountManager.get(getActivity());
        Account[] accounts =  manager.getAccounts();
        final int count = accounts.length;
        Account account = null;

        for(int i=0;i<count;i++) {
            account = accounts[i];
            Log.d("HSGIL", "Account - name: " + account.name + ", type :" + account.type);
            if(account.type.equals("com.google")){		//이러면 구글 계정 구분 가능
                textView.setText(account.name);
            }

        }

        // 버튼 클릭하면 로그아웃
        Button google_logoutBtn=rootView.findViewById(R.id.google_logoutBtn);
        google_logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();

                Intent loginintent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginintent);

            }
        });

        // 버튼 클릭하면 회원 탈퇴
        Button button2=rootView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revokeAccess();
            }
        });

        //인스타그램 아이디에 인스타 링크 걸기
        TextView jyp_Linkfy=rootView.findViewById(R.id.jyp);
        TextView hsl_Linkfy=rootView.findViewById(R.id.hsl);
        TextView wjc_Linkfy=rootView.findViewById(R.id.wjc);
        TextView jwh_Linkfy=rootView.findViewById(R.id.jwh);


        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String url) {
                return "";
            }
        };

        Pattern pattern1 =Pattern.compile("  @jy_s21022");
        Pattern pattern2 =Pattern.compile("  @hoshogiii");
        Pattern pattern3 =Pattern.compile("  @wxx._.zee");
        Pattern pattern4 =Pattern.compile("  @1g1_h");

        Linkify.addLinks(jyp_Linkfy,pattern1,"https://www.instagram.com/jy_s21022/",null,mTransform);
        Linkify.addLinks(hsl_Linkfy,pattern2,"https://www.instagram.com/hoshogiii/",null,mTransform);
        Linkify.addLinks(wjc_Linkfy,pattern3,"https://www.instagram.com/wxx._.zee/",null,mTransform);
        Linkify.addLinks(jwh_Linkfy,pattern4,"https://www.instagram.com/1g1_h/",null,mTransform);

        return rootView;
    }

    // 로그아웃
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

    // 회원 탈퇴
    private void revokeAccess() {
        mAuth.getCurrentUser().delete();
    }



}
