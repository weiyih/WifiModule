package com.example.kwei.wifimodule;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.net.NetworkCapabilities.TRANSPORT_WIFI;

public class MainActivity extends Activity {
    // TODO : Add WikiLock to prevent user from accidentally disabling wifi

    WifiManager _manager;
    ConnectivityManager connManager;
    WifiConfiguration _config;
    BroadcastReceiver _receiver;

    private Button btnCheckPermission;
    private Button btnWifiScan;
    private Button btnConnect;

    static final int REQUEST_ACCESS_COARSE_LOCATION = 10;
    static String PG_SSID = "\"DPL-Guest\"";
    static String PG_KEY = "\"Winds0r1947\"";
    List<ScanResult> myDataset = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheckPermission = findViewById(R.id.btnPermission);
        btnWifiScan = findViewById(R.id.btnScan);
        btnConnect = findViewById(R.id.btn_connect);


        btnCheckPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CHK", "Checking Permission");
                checkPermission();
            }
        });

        btnWifiScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }

        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectSoftAp();
            }

        });


        getWifiManager();


        _receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                myDataset.clear();

                Log.d("MAINWIFI","WiFi onReceive");

                if (intent.getParcelableExtra("newState")) {

                }

                //                List<ScanResult> list =_manager.getScanResults();

//                for (ScanResult wifi : list) {
//
//                    if (!myDataset.contains(wifi)) {
//                        myDataset.add(wifi);
//                        StringBuilder sb = new StringBuilder();
//                        sb.append("BSSID: ").append(wifi.BSSID)
//                                .append("   SSID:").append(wifi.SSID+"\n")
//                                .append("   RSSI:").append(wifi.level)
//                                .append("   Capabilities:").append(wifi.capabilities+"\n");
//
//                      Log.d("SCAN", sb.toString());
//
//
//                    }
//                }

            }
        };

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(_receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(_receiver);
    }


    public void startScan() {
        printList();
//        boolean res = _manager.startScan();
//        Toast.makeText(this, "Scanning: " + (res ? "Success" : "Fail"), Toast.LENGTH_SHORT).show();
    }

    public void printList() {
        List<WifiConfiguration> list = _manager.getConfiguredNetworks();

        for (WifiConfiguration wifi:list) {
           Log.d("PG", wifi.SSID.toString());
        }
    }

    public void connectSoftAp() {
        WifiConfiguration ap = createSoftApConfig();
        int netID =_manager.addNetwork(ap);
        _manager.disconnect();
        _manager.enableNetwork(netID, true);
        boolean res = _manager.reconnect();
        Log.d("TEST", String.valueOf(res));


//        NetworkRequest request = new NetworkRequest.Builder().addTransportType(TRANSPORT_WIFI).build();
//        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//
//        };
//        connManager.registerNetworkCallback(request, networkCallback);


IntentFilter intent = new IntentFilter(WifiManager.EXTRA_NEW_STATE);
//        IntentFilter intent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(_receiver, intent);

    }



    public void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Check if permission is disabled
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d("CHK", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)));
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);

                // Utility method to display permission request.
                // Returns false if user has enabled do not ask again or device policy. True if denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) == false) {

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    dialogBuilder.setMessage("Mailbox App needs access to this device's location to connect and configure your mailbox.");
                    dialogBuilder.setTitle("Mailbox App Permission");
                    dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getParent(), new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    dialogBuilder.create();
                    dialogBuilder.show();
                } else { // Usre Denied
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_ACCESS_COARSE_LOCATION);

                }
            }
            else {

            }
        }
    }


    onRequest

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void getWifiManager() {
        //WifiManager should be obtained from application context to avoid memory leaks prior to N
        if (_manager == null) {
            _manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }

        if (connManager == null ) {
            connManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        }
        return;
    }

    // TODO - Static config?
    public WifiConfiguration createSoftApConfig() {

        List<WifiConfiguration> list = _manager.getConfiguredNetworks();

        for (WifiConfiguration wifi:list) {
//            Log.d("PG", wifi.toString());
            if (wifi.SSID.compareTo(PG_SSID) == 0) {
                return wifi;
            }
        }

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = PG_SSID;
        config.hiddenSSID = false;
        config.preSharedKey = PG_KEY; // Pre-shared key for WPA-pSK
        return config;
    }

}