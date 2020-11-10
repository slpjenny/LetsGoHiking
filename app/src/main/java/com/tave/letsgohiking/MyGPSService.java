package com.tave.letsgohiking;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

public class MyGPSService extends Service {
    private final IBinder mBinder = new MyBinder();

    private Location lastLocation;
    private double distance;
    private long minTime;
    private double speed;
    private int pace;

    class MyBinder extends Binder {
        MyGPSService getService() {
            return MyGPSService.this;
        }
    }

    // 액티비티에서 bindService()를 실행하면 호출됨
    // return한 IBinder 객체는 서비스와 클라이언트 사이의 인터페이스를 정의한다.
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder; // 서비스 객체를 리턴
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service", "서비스 시작");
        startLocationService();
    }

    //onStartCommand: 서비스가 실행될때 마다 실행되는 메서드
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return Service.START_STICKY;
        } else {
            startLocationService();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public MyGPSService() { }

    //현재 위치에 대한 위도, 경도 정보 받기
    public void startLocationService() {
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("Service", "최근 위치-> Latitude: "+ latitude + "Longitude: " + longitude);
                //lastLocation 시작 위치로 초기화
                if(lastLocation != null) {
                    lastLocation.setLatitude(latitude);
                    lastLocation.setLongitude(longitude);
                }
            }

            GPSListener gpsListener = new GPSListener();
            minTime = 1000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            Log.d("Service", "내 위치확인 요청함");

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d("Service", "바뀐 위치-> Latitude: "+ latitude + "Longitude: " + longitude);
            //double mySpeed = location.getSpeed()*3600/1000;
            //Log.d("Map", "현재 속도-> "+mySpeed);

            //lastLocation과 location 사이 거리 측정과 속도 측정
            if(lastLocation != null) {
                distance = location.distanceTo(lastLocation);
                Log.d("Service", "거리: "+distance+"m");
                speed = distance / minTime*1000 / 3600;
                Log.d("Service", "현재속도: "+speed+"km/s");
                if(speed == 0)
                    pace = 0;
                else
                    pace =(int)(distance / speed);
            }
            lastLocation = location;
        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    Location getLastLocation() {
        return lastLocation;
    }

    double getSpeed() {
        return speed;
    }

    double getDistance() {
            return distance;
    }

    int getPace() {
        return pace;
    }
}
