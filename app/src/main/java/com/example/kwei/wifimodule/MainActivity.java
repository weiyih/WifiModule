package com.example.kwei.wifimodule;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
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

public class MainActivity extends Activity {

    // NOTE: WifiManager.startScan() deprecated in API28
    // TODO : Add WikiLock to prevent user from accidentally disabling wifi

    WifiManager _manager;
    WifiConfiguration _config;
    BroadcastReceiver _receiver;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnCheckPermission;
    private Button btnWifiScan;

    static final int REQUEST_ACCESS_COARSE_LOCATION = 10;
    static String PG_SSID = "DanbyMailbox";
    static String PG_KEY = "password123";
    List<ScanResult> myDataset = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        btnCheckPermission = findViewById(R.id.btnPermission);
        btnWifiScan = findViewById(R.id.btnScan);

        btnCheckPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CHK", "Permission");
                checkPermission();
            }
        });

        btnWifiScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
//                startList();
            }

        });

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);


        getWifiManager();
        _config = createSoftApConfig();

        _manager.addNetwork(_config);

        _receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                myDataset.clear();

                Log.d("MAINWIFI","WiFi onReceive");
                List<ScanResult> list =_manager.getScanResults();

                for (ScanResult wifi : list) {

//                    TODO map wifi scanresults to remove duplicate ssid

                    if (!myDataset.contains(wifi)) {
                        myDataset.add(wifi);
                        StringBuilder sb = new StringBuilder();
                        sb.append("BSSID: ").append(wifi.BSSID)
                                .append("   SSID:").append(wifi.SSID+"\n")
                                .append("   RSSI:").append(wifi.level)
                                .append("   Capabilities:").append(wifi.capabilities+"\n");

                        Log.d("LIST2", wifi.toString());
                        Log.d("LIST", sb.toString());


                    }
                    mAdapter.notifyDataSetChanged();
                }

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


    public void startList() {
        List<WifiConfiguration> list = _manager.getConfiguredNetworks();
        for (WifiConfiguration item : list) {
            StringBuilder sb = new StringBuilder();
            sb.append("NetId: ").append(item.networkId)
                    .append("   SSID:").append(item.SSID);
            Log.d("LIST", sb.toString());

            sb = new StringBuilder();


        }

    }

    public void startScan() {
//        Toast.makeText(this, "Starting Scan", Toast.LENGTH_SHORT).show();
//        int res = _manager.getWifiState();
//        Toast.makeText(this, "WiFi State: " + String.valueOf(res), Toast.LENGTH_SHORT).show();
        boolean res = _manager.startScan();
        Toast.makeText(this, "Scan: " + (res ? "Success" : "Fail"), Toast.LENGTH_SHORT).show();
    }


    public void checkPermission() {
        Toast.makeText(this, "Checking WiFi Permissions", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Custom permission dialog

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                }
                // API 23+ can revoke permissions
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    _manager.startScan();
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
        return;
    }

    // TODO - Static config?
    public WifiConfiguration createSoftApConfig() {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = PG_SSID;
        config.hiddenSSID = false;
        config.preSharedKey = PG_KEY; // Pre-shared key for WPA-pSK
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<ScanResult> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textSsid;
        public TextView textBssid;

        public ViewHolder(View view) {
            super(view);
            textSsid = view.findViewById(R.id.text_ssid);
            textBssid = view.findViewById(R.id.text_bssid);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<ScanResult> myDataset) {
        this.mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_field, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ScanResult wifi = mDataset.get(position);
        holder.textSsid.setText(wifi.SSID);
        holder.textBssid.setText(wifi.capabilities);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
//        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        return mDataset.size();
    }
}