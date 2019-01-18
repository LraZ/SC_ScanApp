package com.example.matth.scanme.utils;

import android.util.Log;

import com.example.matth.scanme.entities.AccessPoint;
import com.example.matth.scanme.entities.GridPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class APIHelper{

    private GridPoint tempGP;
    private AccessPoint tempAP;
    private List<AccessPoint> tempAPList;
    private List<GridPoint> tempGPList;
    private static final String TAG = APIHelper.class.getSimpleName();

    public APIHelper() {
    }

    public List<GridPoint> getGridPoints(){
        tempGPList = new LinkedList<GridPoint>();
        String URL = "http://10.202.233.106:9000/api/getAllGridPoints";
        String jsonStr = makeServiceCall(URL);

        if (jsonStr!=null){
            try{
                JSONObject jsonObj = new JSONObject(jsonStr);
                // Getting JSON Array node
                JSONArray data = jsonObj.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject d = data.getJSONObject(i);
                    String id = d.getString("id");
                    tempGP = new GridPoint(id);
                    //String posX = d.getString("posX");
                    //String posY = d.getString("posY");
                    //tempGP.setId(d.getString("Id"));
                    //tempGP.setPosX(d.getInt("PosX"));
                    //tempGP.setPosY(d.getInt("PosY"));

                    // tmp hash map for single contact
                    HashMap<String, String> DataHashMap = new HashMap<>();

                    // adding each child node to HashMap key => value
                    DataHashMap.put("id", String.valueOf(id));
                    //DataHashMap.put("posX", posX);
                    //DataHashMap.put("posY", posY);

                    // adding contact to contact list
                    //resultList.add(DataHashMap);
                    String temptext = "Grid Point ID: " + id;
                    tempGPList.add(tempGP);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }
        if (tempGPList != null){
            return tempGPList;
        }else {
            return null;
        }
    }

    public List<AccessPoint> getAccessPoints(){
        tempAPList = new LinkedList<AccessPoint>();
        String URL = "http://10.202.233.106:9000/api/getAllAccessPoints";
        String jsonStr = makeServiceCall(URL);

        if (jsonStr!=null){
            try{
                JSONObject jsonObj = new JSONObject(jsonStr);
                // Getting JSON Array node
                JSONArray data = jsonObj.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject d = data.getJSONObject(i);
                    String mac = d.getString("mac");
                    Integer type = d.getInt("type");
                    Boolean activity = d.getBoolean("activity");
                    tempAP = new AccessPoint(mac,type,activity);

                    // tmp hash map for single contact
                    HashMap<String, String> DataHashMap = new HashMap<>();

                    // adding each child node to HashMap key => value
                    DataHashMap.put("mac", mac);
                    DataHashMap.put("type", String.valueOf(type));
                    DataHashMap.put("activity", String.valueOf(activity));

                    // adding contact to contact list
                    //resultList.add(DataHashMap);
                    tempAPList.add(tempAP);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }
        if (tempAPList != null){
            return tempAPList;
        }else {
            return null;
        }
    }

    public String sendData(String JSONstring) {
        String data = "";
        String URL = "http://10.202.233.106:9000/api/getPosition";
        try{
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.connect();


            JSONstring = "{\"destination\": 1, \"ReceivedSignals\":[" +
                    "{\"mac\": \"84:78:ac:b8:bb:b0\",\"power\": \"81\"" +
                    "},{\"mac\": \"84:78:ac:b8:d4:80\",\"power\": \"83\"}," +
                    "{\"mac\": \"84:78:ac:b8:e2:f0\",\"power\": \"20\"" + "}]}";

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(JSONstring);
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            data = convertStreamToString(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
