package com.yomnahamin.safetyspeed;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 18-Aug-18.
 */

public class FileReader {
    private final static String TAG = "FileReaderClass";

    private InputStream inputStream;
    private BufferedReader reader;
    private JSONArray data;
    private Context mContext;

    public FileReader(Context mContext){
        this.mContext = mContext;
    }

//    private Segment[] segments;
//
//    public Segment[] getSegments() {
//        return segments;
//    }

    public JSONArray read(){
        data = new JSONArray();
        inputStream = mContext.getResources().openRawResource(R.raw.data);
        reader = new BufferedReader(
                new InputStreamReader(inputStream)
        );
        String line;
        String[] line_parsed;
        int counted = 0;
        try {
            while((line = reader.readLine()) != null){
                if(counted > 2) {
                    line_parsed = line.split(",");

                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("startLat", line_parsed[0]);
                    dataMap.put("startLng", line_parsed[1]);
                    dataMap.put("endLat", line_parsed[2]);
                    dataMap.put("endLng", line_parsed[3]);
                    dataMap.put("safeSpeed", line_parsed[4]);

                    data.put(new JSONObject(dataMap));
                }else{
                    counted++;
                }

            }

            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
