package com.tave.letsgohiking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private recordFragment recordFragment = new recordFragment();
    private badgeFragment badgeFragment = new badgeFragment();
    private SettingFragment settingFragment = new SettingFragment();
    private MapFragment mapFragment = new MapFragment();

    static public Button startBtn;

    private Location lastLocation;
    private long minTime;

    PolylineOverlay path = new PolylineOverlay();

    public List<LatLng> placeList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, mapFragment).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        mapFragment.getMapAsync(this);
        locationSource =  new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        startLocationService(); //위도, 경도 정보 받기
        startBtn = findViewById(R.id.startBtn);
        startBtn.setVisibility(View.VISIBLE);
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MeasureActivity.class);

                startActivity(intent);

                startBtn.setBackgroundResource(R.drawable.runner_image);
                //startBtn.setVisibility(View.INVISIBLE);
//                Drawable finish_image = findViewById(R.drawable.finish_image);
//                startBtn.setBackground(finish_image);

            }
        });

        //위험권한 요청하기
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };

        //checkPermissions(permissions);

    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.homeItem:
                    transaction.replace(R.id.frameLayout, mapFragment).commitAllowingStateLoss();
                    startBtn.setVisibility(View.VISIBLE);
                    break;
                case R.id.recordItem:
                    transaction.replace(R.id.frameLayout, recordFragment).commitAllowingStateLoss();
                    startBtn.setVisibility(View.INVISIBLE);
                    break;
                case R.id.badgeItem:
                    transaction.replace(R.id.frameLayout, badgeFragment).commitAllowingStateLoss();
                    startBtn.setVisibility(View.INVISIBLE);
                    break;
                case R.id.settingItem:
                    transaction.replace(R.id.frameLayout, settingFragment).commitAllowingStateLoss();
                    startBtn.setVisibility(View.INVISIBLE);
                    break;
            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {

        switch(requestCode) {
            case 1000: {
                if (locationSource.onRequestPermissionsResult(
                        requestCode, permissions, grantResults)) {

                    locationSource.isActivated();
                    if (!locationSource.isActivated()) { // 권한 거부됨
                        naverMap.setLocationTrackingMode(LocationTrackingMode.None);
                    }
                    return;
                }
            }
            case 101: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        //현위치로 이동 버튼
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
    }

    //현재 위치에 대한 위도, 경도 정보 받기
    public void startLocationService() {
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("Map", "최근 위치-> Latitude: "+ latitude + "Longitude: " + longitude);
                //lastLocation 시작 위치로 초기화
                if(lastLocation != null) {
                    lastLocation.setLatitude(latitude);
                    lastLocation.setLongitude(longitude);
                }
                //초기 위치를 placeList 배열에 저장
                placeList.add(new LatLng(latitude, longitude));
                placeList.add(new LatLng(latitude, longitude));
            }

            GPSListener gpsListener = new GPSListener();
            minTime = 1000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            Log.d("Map", "내 위치확인 요청함");

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d("Map", "바뀐 위치-> Latitude: "+ latitude + "Longitude: " + longitude);
            //double mySpeed = location.getSpeed()*3600/1000;
            //Log.d("Map", "현재 속도-> "+mySpeed);


            //지도에 경로 표시
            placeList.add(new LatLng(latitude, longitude));
            path.setCoords(placeList);

            path.setWidth(20);
            path.setColor(Color.MAGENTA);
            path.setMap(naverMap);


            //lastLocation과 location 사이 거리 측정과 속도 측정
            double distance;
            if(lastLocation != null) {
                distance = location.distanceTo(lastLocation);
                Log.d("Distance", "거리: "+distance+"m");
                double speed = distance / minTime*1000;
                Log.d("Distance", "현재속도: "+speed+"m/s");
            }
            lastLocation = location;
        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    //GPS 백그라운드 위험권한 요청하기
    public void checkPermissions(String[] permissions) {
        ArrayList<String> targetList = new ArrayList<String>();

        for(int i=0;i<permissions.length;i++) {
            String curPermissions = permissions[i];
            int permissionsCheck = ContextCompat.checkSelfPermission(this, curPermissions);
            if(permissionsCheck == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, curPermissions)) {

                } else {
                    targetList.add(curPermissions);
                }
            }
        }

        String[] targets = new String[targetList.size()];
        targetList.toArray(targets);

        ActivityCompat.requestPermissions(this, targets, 101);
        ActivityCompat.requestPermissions(this, targets, 1000);
    }
}