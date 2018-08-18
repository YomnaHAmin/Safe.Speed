package com.yomnahamin.safetyspeed;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by user on 18-Aug-18.
 */

public class SegmentUtils {
    private Segment[] segments;
    private Context mContext;

    public SegmentUtils(Context mContext) {
        this.mContext = mContext;
        JSONArray csvData = new FileReader(mContext).read();
        for(int i = 0; i < csvData.length(); i++) {
            try {
                segments[i] = new Segment(
                        Double.parseDouble(csvData.getJSONObject(i).getString("startLat")),
                        Double.parseDouble(csvData.getJSONObject(i).getString("startLng")),
                        Double.parseDouble(csvData.getJSONObject(i).getString("endLat")),
                        Double.parseDouble(csvData.getJSONObject(i).getString("endLng")),
                        Double.parseDouble(csvData.getJSONObject(i).getString("safeSpeed"))
                );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public double calcSafeSpeed(double currentlat, double currentlng){

        double safeSpeed = 0;

        return safeSpeed;
    }
}
