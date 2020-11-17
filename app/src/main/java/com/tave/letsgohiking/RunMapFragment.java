package com.tave.letsgohiking;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import java.util.ArrayList;
import java.util.List;


//이 fragment에서 현위치 버튼이 활성화되도록 해야 함.

public class RunMapFragment extends Fragment implements OnMapReadyCallback {

    public NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private MapView mapView;
    private LocationButtonView locationButtonView;
    public List<LatLng> placeList=new ArrayList<>();

    private Location lastLocation;
    private long minTime;

    PolylineOverlay path = new PolylineOverlay();


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_runmap, container, false);
        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        locationButtonView = rootView.findViewById(R.id.locationbuttonview);
        //mapView.getMapAsync(this);
        naverMapBasicSettings();

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        //startLocationService();
//        mapView = findViewById(R.id.map_view);
//        mapView.onCreate(savedInstanceState);


        //naverMapBasicSettings();
    }

    public void naverMapBasicSettings() {
        mapView.getMapAsync(this);
        //내위치 버튼
//        locationButtonView = findViewById(R.id.locationbuttonview);
        // 내위치 찾기 위한 source
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        //startLocationService();
    }



    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        locationButtonView.setMap(naverMap);

        // Location Change Listener을 사용하기 위한 FusedLocationSource 설정
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);



//        naverMap.setLocationSource(locationSource);
//        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
//
//        //현위치로 이동 버튼
//        UiSettings uiSettings = naverMap.getUiSettings();
//        uiSettings.setLocationButtonEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
    public void activate​(@NonNull LocationSource.OnLocationChangedListener listener){

    }
//    @Override
//    public void onMapReady(@NonNull NaverMap naverMap) {
//        this.naverMap = naverMap;
//        naverMap.setLocationSource(locationSource);
//        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
//
//        //현위치로 이동 버튼
//        UiSettings uiSettings = naverMap.getUiSettings();
//        uiSettings.setLocationButtonEnabled(true);
//    }

    /*
    //현재 위치에 대한 위도, 경도 정보 받기
    public void startLocationService() {
        LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

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
//            placeList.add(new LatLng(latitude, longitude));
//            path.setCoords(placeList);
//
//            path.setWidth(20);
//            path.setColor(Color.MAGENTA);
//            path.setMap(naverMap);


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
    }*/

    //GPS 백그라운드 위험권한 요청하기
//    public void checkPermissions(String[] permissions) {
//        ArrayList<String> targetList = new ArrayList<String>();
//
//        for(int i=0;i<permissions.length;i++) {
//            String curPermissions = permissions[i];
//            int permissionsCheck = ContextCompat.checkSelfPermission(this, curPermissions);
//            if(permissionsCheck == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, curPermissions)) {
//
//                } else {
//                    targetList.add(curPermissions);
//                }
//            }
//        }
//
//        String[] targets = new String[targetList.size()];
//        targetList.toArray(targets);
//
//        //ActivityCompat.requestPermissions(this, targets, 101);
//        //ActivityCompat.requestPermissions(this, targets, 1000);
//    }

    public void getMapAsync(@androidx.annotation.NonNull com.naver.maps.map.OnMapReadyCallback callback) { /* compiled code */ }



}

