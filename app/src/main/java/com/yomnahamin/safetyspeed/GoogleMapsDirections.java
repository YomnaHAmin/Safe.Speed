package com.yomnahamin.safetyspeed;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.google.maps.DirectionsApiRequest;
//import com.google.maps.model.LatLng;


import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 17-Aug-18.
 */

public class GoogleMapsDirections {
    private final static String TAG = "#app_GoogleDirections";
    private static final int overview = 0;
    private static final String API_KEY = "AIzaSyCYdqVeYYZ1sbwgiN2Gjn6C5BEBSgSTpMs";

    private Context mContext;

    public GoogleMapsDirections(Context mContext) {
        this.mContext = mContext;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();

        Log.d(TAG, "Geo Api Context");

        return geoApiContext
                .setQueryRateLimit(3)
//                .setApiKey(mContext.getResources().getSystem().getString(R.string.directionsApiKey))
                .setApiKey(API_KEY)
                .setConnectTimeout(2, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }
    private String getEndLocationTitle(DirectionsResult results){
        Log.d(TAG, "Get End Location Title");

        return  "Time :"+ results.routes[overview].legs[overview].duration.humanReadable +
                " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    public void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        Log.d(TAG, "Set Google Maps Screen Setting");
    }

    public DirectionsResult getDirectionsDetails(
            com.google.maps.model.LatLng origin,
            com.google.maps.model.LatLng destination,
            TravelMode mode) {

        DateTime now = new DateTime();
        DateTime freeFallTime = new DateTime(
                now.getYear(),
                now.getMonthOfYear()+1,
                now.getDayOfMonth(),
                3,0,0
        );

        Log.d(TAG, "Get Directions Details (Before Try)");
        try {
            Log.d(TAG, "Get Directions Details (In Try)");
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(freeFallTime)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            Log.d(TAG, "Get Directions Details - Api Exception : " + e);
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "Get Directions Details - Interrupted Exception : " + e);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Get Directions Details - IO Exception : " + e);
            return null;
        }
    }

    public void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());

        Log.d(TAG, "Decoded Path : " + decodedPath);

        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));

        Log.d(TAG, "Add Polyline ");
    }

    public void positionCamera(DirectionsResult results, GoogleMap mMap) {
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(
                                results.routes[overview].legs[overview].startLocation.lat,
                                results.routes[overview].legs[overview].startLocation.lng
                        ), 12
                )
        );

        Log.d(TAG, "Position Camera");
    }

    public void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(
                new MarkerOptions().position(
                        new LatLng(
                                results.routes[overview].legs[overview].startLocation.lat,
                                results.routes[overview].legs[overview].startLocation.lng
                        )
                ).title(
                        results.routes[overview].legs[overview].startAddress
                )
        );

        mMap.addMarker(
                new MarkerOptions().position(
                        new LatLng(
                                results.routes[overview].legs[overview].endLocation.lat,
                                results.routes[overview].legs[overview].endLocation.lng
                        )
                ).title(
                        results.routes[overview].legs[overview].startAddress
                ).snippet(
                        getEndLocationTitle(results)
                )
        );

        Log.d(TAG, "Add Markers To Map");
    }



}
