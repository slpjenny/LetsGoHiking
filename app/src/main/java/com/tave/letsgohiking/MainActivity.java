package com.tave.letsgohiking;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private recordFragment recordFragment = new recordFragment();
    private badgeFragment badgeFragment = new badgeFragment();
    private SettingFragment settingFragment = new SettingFragment();
    private RunMapFragment mapFragment = new RunMapFragment();

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

        //mapFragment.getMapAsync(this);
        locationSource =  new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        //startLocationService(); //위도, 경도 정보 받기
        startBtn = findViewById(R.id.startBtn);
        startBtn.setVisibility(View.VISIBLE);
        startBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MeasureActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                startBtn.setBackgroundResource(R.drawable.runner_image);
                //startBtn.setVisibility(View.INVISIBLE);
                //               Drawable finish_image = findViewById(R.drawable.finish_image);
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

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
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

    //MeasureActivity에서 보낸 위치 정보 ArrayList 받기
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundle;
        bundle = intent.getBundleExtra("bundle");
        placeList = (ArrayList<LatLng>) bundle.getSerializable("placeList");

        path.setCoords(placeList);

        path.setWidth(20);
        path.setColor(Color.MAGENTA);
        path.setMap(mapFragment.naverMap);
    }
}