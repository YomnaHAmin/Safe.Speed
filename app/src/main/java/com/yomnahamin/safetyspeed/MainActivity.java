package com.yomnahamin.safetyspeed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_ACCESS_LOCATION = 10;
    private static final float TIME_INTERVAL = 30;

    LocationManager locationManager;
    LocationListener locationListener;

    TextView lat_tv, lng_tv, atit_tv, speed_tv;
    double lat, lng, atit, speed,
            newLat, newLng, newAtit;
    boolean initially;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat_tv = (TextView) findViewById(R.id.lat);
        lng_tv = (TextView) findViewById(R.id.lng);
        atit_tv = (TextView) findViewById(R.id.atit);
        speed_tv = (TextView) findViewById(R.id.speed);

        newLat = 0;
        newLng = 0;
        newAtit = 0;
        speed = 0;

        initially = true;

        handleLocation();
    }

    public void handleLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(initially){
                    newLat = location.getLatitude();
                    newLng = location.getLongitude();
                    newAtit = location.getAltitude();

                    lat = newLat;
                    lng = newLng;
                    atit = newAtit;

                    initially = false;
                }else{
                    lat = newLat;
                    lng = newLng;
                    atit = newAtit;

                    newLat = location.getLatitude();
                    newLng = location.getLongitude();
                    newAtit = location.getAltitude();
                }

                speed = speed(newLat, lat, newLng, lng, TIME_INTERVAL);

                lat_tv.setText(String.valueOf(newLat));
                lng_tv.setText(String.valueOf(newLng));
                atit_tv.setText(String.valueOf(newAtit));
                speed_tv.setText(String.valueOf(speed));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
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
        locationManager.requestLocationUpdates("gps", 30000, 0, locationListener);
    }

    private double speed(double newLat, double oldLat,
                         double newLng, double oldLng,
                         float time_sec){
        double speed = 0;

        float[] dist_meter = new float[3];
        Location.distanceBetween(newLat, newLng, oldLat, oldLng, dist_meter);
        float dist_km = dist_meter[0]/1000;
        float time_hr = time_sec/3600;

        speed = dist_km/time_hr;

        return speed;
    }

}
