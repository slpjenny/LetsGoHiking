package com.tave.letsgohiking;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
                textView.setText(account.toString());
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
