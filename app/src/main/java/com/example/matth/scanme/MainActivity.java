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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matth.scanme.entities.AccessPoint;
import com.example.matth.scanme.entities.DeviceItem;
import com.example.matth.scanme.entities.GridPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button SaveMeButton;
    private Spinner spinner;
    private WifiManager wifiManager;
    private ListView listView;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter wifi_adapter;
    private ArrayList<String> GridPoints = new ArrayList<>();

    private BluetoothAdapter BTAdapter;
    //set to identify the activity request
    public static int REQUEST_BLUETOOTH = 1;
    private ArrayList <DeviceItem>deviceItemList;
    //private BroadcastReceiver mReceiver;
    //private final BroadcastReceiver mReceiver;
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SaveMeButton = (Button) findViewById(R.id.saveToDB_button);
        SaveMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();
                scanWifi();
                bluetoothScanning();

            }
        });

        listView = findViewById(R.id.wifi_List);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true); //
        }

        wifi_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(wifi_adapter);
        scanWifi();
        bluetoothScanning();
        //new FetchData().execute();


        // CursorAdapter if the choices are available from a database query -> https://developer.android.com/guide/topics/ui/controls/spinner#java .array.location_array
        spinner = (Spinner) findViewById(R.id.location_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        /*
        //Bluetooth adapter to interface with Bluetooth
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        //If phone does not support Bluetooth show an alert dialog to the user and exit the app.
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth.")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        //Log.d("DEVICELIST", "Super called for DeviceListFragment onCreate\n");
        //deviceItemList = new ArrayList<DeviceItem>();
        */


    }

    private void bluetoothScanning(){

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
    }


   /*
    @Override
    public void onScanResult(int callbackType, ScanResult scanResult) {
        super.onScanResult(callbackType, scanResult);

        // Retrieve device name via ScanRecord.
        String deviceName = scanResult.getScanRecord().getDeviceName();
    }*/

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Add the name and address to an array adapter to show in a ListView
                //arrayList.add(device.getClass() + "\n" + device.getAddress());
                arrayList.add(new AccessPoint(device.getAddress(), 1, true, device.getName(),80).toString());
            }
        }

    };



    private void scanWifi(){
        //arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            //https://androidforums.com/threads/wifimanager-getscanresults-always-returns-empty-list.1266068/
            //need to enable permission to access localization service
            for (ScanResult scanResult : results) {
                //if(filterAP(scanResult.BSSID)){
                //arrayList.add(scanResult.SSID + " - " + scanResult.BSSID + "    " + scanResult.level + " dBm");
                arrayList.add(new AccessPoint(scanResult.BSSID, 0, true, scanResult.SSID, scanResult.level).toString());
                //}
                wifi_adapter.notifyDataSetChanged();
            }
            unregisterReceiver(this);
        }
    };


    //APScanner Signavio
    /*
    filter registered APs in database
    private boolean filterAP(String BSSID, List<AccessPoint> registeredAPs){
        //Logic, compare BSSID string to MAC-adresses in our List of registeredAP (get APs from database)
        for(List<AccessPoint> filtered : registeredAPs){
            if(filtered.getMAC().equals(BSSID)){
                return true;
            }
            else {
                return false;
            }
        }
    }
    */


    //hardcoded method for testing
    private boolean filterAP(String BSSID){

        if (BSSID.equals("1c:e6:c7:1d:6e:34")) {
            return true;
        }
        else {
            return false;
        }
    }

    //move to Service package

    private void getGridPoints(){
        //service, get Grid Points from database, JSON
    }

    private void getAccessPoints(){
        //service, get Access Points from database, JSON
    }

    private void assignGPToAPs(GridPoint GP, List<AccessPoint> APs){
        //service, send date to database, JSON
    }
    private class FetchData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //https://www.salzburg.gv.at/ogd/c8711f5c-a49f-446d-ad69-6435bbc5a78e/names-szg.json
                //http://192.168.0.101:9000/getaccespoints
                URL url = new URL("https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:STATIONENOGD&srsName=EPSG:4326&outputFormat=json");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("json", s);
        }
    }

}

