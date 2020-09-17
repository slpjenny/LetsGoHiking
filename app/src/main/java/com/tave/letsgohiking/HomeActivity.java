package com.tave.letsgohiking;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//    }

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRecord fragmentRecord = new FragmentRecord();
    private FragmentMeasure fragmentMeasure = new FragmentMeasure();
    private FragmentCommunity fragmentCommunity = new FragmentCommunity();
    private FragmentSetting fragmentSetting = new FragmentSetting();
    private FragmentSearch fragmentSearch = new FragmentSearch();

    private Toolbar toolbar = findViewById(R.id.toolbar);
    private TabLayout tabs = findViewById(R.id.tabs);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        //상단탭
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

       // getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutHome, fragmentHome).commit();
        tabs.addTab(tabs.newTab().setText("홈"));
        tabs.addTab(tabs.newTab().setText("산 검색"));


        tabs.addOnTabSelectedListener(new TabSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.homeItem:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.recordItem:
                    transaction.replace(R.id.frameLayout, fragmentRecord).commitAllowingStateLoss();
                    break;
                case R.id.measureItem:
                    transaction.replace(R.id.frameLayout, fragmentMeasure).commitAllowingStateLoss();
                    break;
                case R.id.communityItem:
                    transaction.replace(R.id.frameLayout, fragmentCommunity).commitAllowingStateLoss();
                    break;
                case R.id.settingItem:
                    transaction.replace(R.id.frameLayout, fragmentSetting).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
    // 상단탭 SelectListener 등록
    class TabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            Fragment selected = null;

            if(position == 0) {
                selected = fragmentHome;
            } else if(position == 1){
                selected = fragmentSearch;
            }
        }
        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            Fragment fragmentHome;
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
    }

}
