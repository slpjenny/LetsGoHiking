package com.tave.letsgohiking;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentRecord fragmentRecord = new FragmentRecord();
    private FragmentMeasure fragmentMeasure = new FragmentMeasure();
    private FragmentBadge fragmentBadge = new FragmentBadge();
    private FragmentSetting fragmentSetting = new FragmentSetting();
    private MapFragment mapFragment = new MapFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
        transaction.replace(R.id.frameLayout, mapFragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        mapFragment.getMapAsync(this);
        locationSource =  new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {

            locationSource.isActivated();
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
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
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

    /*
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
     */
}


