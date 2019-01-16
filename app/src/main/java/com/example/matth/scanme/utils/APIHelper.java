package com.example.matth.scanme.utils;

import android.app.Notification;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.example.matth.scanme.entities.GridPoint;
import com.example.matth.scanme.service.ScannerAppGetServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class APIHelper extends AsyncTask<Void, Void, List<String>>{

    private GridPoint tempGP = new GridPoint();
    private static final String TAG = APIHelper.class.getSimpleName();
    private static ScannerAppGetServices scannerService;

    public APIHelper(ScannerAppGetServices scannerAppGetServices) {
        scannerService = scannerAppGetServices;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        return getGridPoints();
    }

    @Override
    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);
        scannerService.APIfinished(result);
    }


    public List<String> getGridPoints(){
        List<String> GPoints = null;
        String URL = "http://192.168.0.233:9000/api/getAllGridPoints";
        String jsonStr = makeServiceCall(URL);

        if (jsonStr!=null){
            try{
                JSONObject jsonObj = new JSONObject(jsonStr);
                // Getting JSON Array node
                JSONArray data = jsonObj.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject d = data.getJSONObject(i);
                    String id = d.getString("id");
                    String posX = d.getString("posX");
                    String posY = d.getString("posY");
                    tempGP.setId(d.getString("id"));
                    tempGP.setPosX(d.getInt("posX"));
                    tempGP.setPosY(d.getInt("posY"));

                    // tmp hash map for single contact
                    HashMap<String, String> DataHashMap = new HashMap<>();

                    // adding each child node to HashMap key => value
                    DataHashMap.put("id", id);
                    DataHashMap.put("posX", posX);
                    DataHashMap.put("posY", posY);

                    // adding contact to contact list
                    //resultList.add(DataHashMap);
                    GPoints.add(tempGP.toString());
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }

        }
        if (GPoints != null){
            return GPoints;
        }else {
            return null;
        }
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
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
