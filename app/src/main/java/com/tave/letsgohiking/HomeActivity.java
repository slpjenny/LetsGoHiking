package com.tave.letsgohiking;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

public class HomeActivity extends AppCompatActivity {


    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRecord fragmentRecord = new FragmentRecord();
    private FragmentMeasure fragmentMeasure = new FragmentMeasure();
    private FragmentCommunity fragmentCommunity = new FragmentCommunity();
    private FragmentSetting fragmentSetting = new FragmentSetting();
    private MapFragment mapFragment = new MapFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
        transaction.replace(R.id.frameLayout, mapFragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        //UiSettings uiSettings = NaverMap.getUiSettings();
        //uiSettings.isLocationButtonEnabled();

    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.homeItem:
                    //transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    transaction.replace(R.id.frameLayout, mapFragment).commitAllowingStateLoss(); //수정
                    break;
                case R.id.recordItem:
                    transaction.replace(R.id.frameLayout, fragmentRecord).commitAllowingStateLoss();
                    break;
                case R.id.measureItem:
                    transaction.replace(R.id.frameLayout, fragmentMeasure).commitAllowingStateLoss();
                    break;
                case R.id.settingItem:
                    transaction.replace(R.id.frameLayout, fragmentSetting).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    /*
    //NaverMap 객체 얻어오기
    public class MapFragmentActivity extends FragmentActivity
            implements OnMapReadyCallback {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.fragment_map);//수정

            FragmentManager fm = getSupportFragmentManager();
            MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
            if (mapFragment == null) {
                mapFragment = MapFragment.newInstance();
                fm.beginTransaction().add(R.id.map, mapFragment).commit();
            }

            mapFragment.getMapAsync(this);
        }

        @UiThread
        @Override
        public void onMapReady(@NonNull NaverMap naverMap) {
            // ...
        }
    }
    */


    //액티비티에서 FusedLocationSource를 생성하고 NaverMap에 지정하는 예제
    public class LocationTrackingActivity extends AppCompatActivity
            implements OnMapReadyCallback {
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
        private FusedLocationSource locationSource;
        private NaverMap naverMap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // ...
            locationSource =
                    new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,  @NonNull int[] grantResults) {
            if (locationSource.onRequestPermissionsResult(
                    requestCode, permissions, grantResults)) {
                if (!locationSource.isActivated()) { // 권한 거부됨
                    naverMap.setLocationTrackingMode(LocationTrackingMode.None);
                }
                return;
            }
            super.onRequestPermissionsResult(
                    requestCode, permissions, grantResults);
        }

        @Override
        public void onMapReady(@NonNull NaverMap naverMap) {
            this.naverMap = naverMap;
            naverMap.setLocationSource(locationSource);
        }
    }
}


