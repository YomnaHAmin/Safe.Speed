package com.yomnahamin.safetyspeed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "#app_MAPS_ACTIVITY";
    private static final int RESULT_ACCESS_LOCATION = 10;
    private static final int TIME_INTERVAL_SECONDS = 30;
    private static final int TIME_INTERVAL_MILLISECONDS = TIME_INTERVAL_SECONDS*1000;

    private GoogleMap googleMap;
    private Safety safety;

    LocationManager locationManager;
    LocationListener locationListener, initialLocationListener;
    MediaPlayer moderateAlert, dangerAlert;

    double initLat, initLng, prevLat, prevLng, currentLat, currentLng;
    boolean is_firstSegment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        moderateAlert = MediaPlayer.create(this, R.raw.moderate_alert);
        dangerAlert = MediaPlayer.create(this, R.raw.danger_alert);

        is_firstSegment = false;

        Log.d(TAG, "On Create");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        safety = new Safety(this, googleMap);
        handleLocation();

        Log.d(TAG, "On Map Ready");

    }

    private void handleLocation() {

        Log.d(TAG, "In Handle Location");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initialLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d(TAG, "Initial Location Listener");

                initLat = location.getLatitude();
                initLng = location.getLongitude();

                is_firstSegment = true;
            }

            @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d(TAG, "Location Changed");

                if(is_firstSegment){
                    prevLat = initLat;
                    prevLng = initLng;

                    is_firstSegment = false;
                }else{
                    prevLat = currentLat;
                    prevLng = currentLng;
                }

                currentLat = location.getLatitude();
                currentLng = location.getLongitude();

                switch (safety.calculateDangerLevel(
                        new LatLng(prevLat, prevLng),
                        new LatLng(currentLat, currentLng),
                        TravelMode.DRIVING)){

                    case -1:
                        Log.e(TAG, "Something Went Wrong on Calculating Danger Level");
                        break;
                    case Safety.SAFE:
                        // Do Nothing
                        break;
                    case Safety.SAFE_DANGER_BOUNDARY:
                        // Moderate Alert !
                        moderateAlert.start();
                        break;
                    case Safety.DANGER:
                        // Danger Alert !!!!
                        dangerAlert.start();
                        break;
                }

            }

            @Override public void onStatusChanged(String s, int i, Bundle bundle){}
            @Override public void onProviderEnabled(String s){}

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET
                }, RESULT_ACCESS_LOCATION);
            }
            return;
        }else{
            getLocation();
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case RESULT_ACCESS_LOCATION:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }
                else{
                    Toast.makeText(this, "Location Permissions not granted",Toast.LENGTH_LONG).show();
                }
                return;
        }
    }


    @SuppressLint("MissingPermission")
    private void getLocation(){
        locationManager.requestSingleUpdate("gps", initialLocationListener, null);
        locationManager.requestLocationUpdates("gps",TIME_INTERVAL_MILLISECONDS, 0, locationListener);
    }

}