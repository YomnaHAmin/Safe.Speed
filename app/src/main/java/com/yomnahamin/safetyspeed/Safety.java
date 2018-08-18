package com.yomnahamin.safetyspeed;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

/**
 * Created by user on 18-Aug-18.
 */

public class Safety {
    private final static String TAG = "#app_SafetyClass";
    private static final int overview = 0;
    private static final int TIME_INTERVAL_SECONDS = 30;
    private static final double SAFE_DANGER_BOUNDARY_FRACTION = 1.1;

    public final static int SAFE = 0;
    public final static int SAFE_DANGER_BOUNDARY = 1;
    public final static int DANGER = 2;


    private GoogleMapsDirections mDirections;
    private Context context;
    private GoogleMap googleMap;

    public Safety(Context context, GoogleMap googleMap){
        this.context = context;
        this.googleMap = googleMap;
        mDirections = new GoogleMapsDirections(this.context);
    }

    public int calculateDangerLevel(LatLng prevLocation, LatLng currentLocation, TravelMode travelMode){

        DirectionsResult results = directionsUtils(prevLocation, currentLocation, travelMode);

        Log.d(TAG, "Calculate Danger Level");

        if(results != null){
            Log.d(TAG, "Directions Results Returned");

            double distance = results.routes[overview].legs[overview].distance.inMeters;
            double freeFallTimeInterval = results.routes[overview].legs[overview].duration.inSeconds;

            double actualSpeed = speed_kmphr(distance, TIME_INTERVAL_SECONDS);
            double freeFallSpeed = speed_kmphr(distance, freeFallTimeInterval);

            if(actualSpeed < freeFallSpeed){
                Log.d(TAG,"Safe Speed");
                return SAFE;
            }else{
                if(actualSpeed < freeFallSpeed*SAFE_DANGER_BOUNDARY_FRACTION){
                    Log.d(TAG,"Hardly Safe Speed !");
                    return SAFE_DANGER_BOUNDARY;
                }else{
                    Log.d(TAG,"Danger Speed !!!");
                    return DANGER;
                }
            }
        }else{
            Log.e(TAG, "No Directions Results Returned !!");
            return -1;
        }

    }

    private DirectionsResult directionsUtils(LatLng prevLocation, LatLng currentLocation, TravelMode travelMode){
        DirectionsResult results = mDirections.getDirectionsDetails(prevLocation, currentLocation, travelMode);
        if(results != null) {
            mDirections.addPolyline(results, googleMap);
            mDirections.positionCamera(results, googleMap);
//                    mDirections.addMarkersToMap(results, googleMap);
            Log.d(TAG,"Directions Result Received Successfully ... ");
        }
        return results;
    }

    private double speed_kmphr(double distance_meters, double time_seconds){
        Log.d(TAG,"Calculate Speed");
        return  (distance_meters*0.001) / (time_seconds/3600 );
    }

}
