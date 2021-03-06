package com.example.matth.scanme;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matth.scanme.entities.AccessPoint;
import com.example.matth.scanme.entities.DeviceItem;
import com.example.matth.scanme.entities.GridPoint;
import com.example.matth.scanme.service.ScannerAppGetServices;
import com.example.matth.scanme.utils.APIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button SaveMeButton;
    private ListView listView;
    private Spinner spinner;

    private WifiManager wifiManager;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter wifi_adapter;
    private List<AccessPoint> APs = new LinkedList<>();
    private List<AccessPoint> registeredAPs = new LinkedList<>();

    private String JSONString;

    private BluetoothAdapter BTAdapter;
    public static int REQUEST_BLUETOOTH = 1;
    private ArrayList<DeviceItem> deviceItemList;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new getAPData().execute();
        new getGridData().execute();
        SaveMeButton = (Button) findViewById(R.id.saveToDB_button);
        SaveMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scanWifi();
                //bluetoothScanning();

                JSONObject postData = new JSONObject();
                try{
                    //postData.put("name", name.getText().toString());
                    GridPoint selectedGridPoint = (GridPoint) spinner.getSelectedItem();
                    postData.put("destination", selectedGridPoint.getId());
                    JSONArray arr = generateJSONArray(APs);
                    postData.put("ReceivedSignals", arr);
                    JSONString = postData.toString();

                    new SendGPtoAPs().execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //new GetContacts().execute();

        listView = findViewById(R.id.wifi_List);
        spinner = (Spinner) findViewById(R.id.location_spinner);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                APs.clear();
                arrayList.clear();
                scanWifi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true); //
        }

        scanWifi();
        bluetoothScanning();

        wifi_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(wifi_adapter);

    }

    private JSONArray generateJSONArray(List<AccessPoint> ListAP) {
        JSONArray temp = new JSONArray();
        /* test-data
        try{
            JSONObject postData = new JSONObject();
            postData.put("mac", "84:78:ac:b8:bb:b0");
            postData.put("power", "80");
            temp.put(postData);
            JSONObject postData1 = new JSONObject();
            postData1.put("mac", "84:78:ac:b8:d4:80");
            postData1.put("power", "70");
            temp.put(postData1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
        try{
            for (AccessPoint AP : ListAP){
                JSONObject postData = new JSONObject();
                postData.put("mac", AP.getMAC());
                postData.put("power", String.valueOf(AP.getSignal()));
                temp.put(postData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    private void bluetoothScanning() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Add the name and address to an array adapter to show in a ListView
                //arrayList.add(device.getClass() + "\n" + device.getAddress());
                APs.add(new AccessPoint(device.getAddress(), 1, true, device.getName(), 80));
                arrayList.add(new AccessPoint(device.getAddress(), 1, true, device.getName(), 80).toString());
            }
        }

    };

    private void scanWifi() {
        //arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        //Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            for (ScanResult scanResult : results) {
                if(filterAP(scanResult.BSSID)){
                    APs.add(new AccessPoint(scanResult.BSSID, 0, true, scanResult.SSID, scanResult.level));
                    arrayList.add(new AccessPoint(scanResult.BSSID, 0, true, scanResult.SSID, scanResult.level).toString());
                }
                wifi_adapter.notifyDataSetChanged();
            }
            unregisterReceiver(this);
        }
    };

    private boolean filterAP(String mac){
        for(AccessPoint filtered : registeredAPs){
            if((filtered.getMAC().equals(mac))){
                return true;
            }
        }
        return false;
    }

    private class getGridData extends AsyncTask<Void, Void, List<GridPoint>>{

        @Override
        protected List<GridPoint> doInBackground(Void... voids) {
            APIHelper api = new APIHelper();
            return api.getGridPoints();
        }

        protected void onPostExecute(List<GridPoint> result) {
            super.onPostExecute(result);
            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, result);
            //set the spinners adapter to the previously created one.
            spinner.setAdapter(adapter);
        }
    }

    private class getAPData extends AsyncTask<Void, Void, List<AccessPoint>>{

        @Override
        protected List<AccessPoint> doInBackground(Void... voids) {
            APIHelper api = new APIHelper();
            registeredAPs = api.getAccessPoints();
            return registeredAPs;
        }

        protected void onPostExecute(List<AccessPoint> result) {
            super.onPostExecute(result);
        }
    }

    private class SendGPtoAPs extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            APIHelper api = new APIHelper();
            return api.sendData(JSONString);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //arrayList.clear();
            APs.clear();
            Log.e("TAG", result);
            //GridPoint tempGP = (GridPoint) spinner.getSelectedItem();
            //String tempGPString = tempGP.toString() + " assigned";
            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
            //add new adapter to updates object in spinnerlist
        }
    }
}
