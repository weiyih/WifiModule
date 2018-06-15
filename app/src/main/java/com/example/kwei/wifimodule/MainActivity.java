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
import android.widget.TextView;
import android.widget.Toast;

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

    static final int REQUEST_CHANGE_WIFI_STATE = 10;
    static String PG_SSID = "DanbyMailbox";
    static String PG_KEY = "password123";
    List<ScanResult> myDataset;


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
                Toast.makeText(v.getContext(),"Permission Check", Toast.LENGTH_SHORT).show();
                Log.d("CHK","Permission");
                checkPermission();
            }
        });

        btnWifiScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Starting Scan", Toast.LENGTH_SHORT).show();
                Log.d("CHK","WifiScan");
                startScan();
            }
        });

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
//        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);


        getWifiManager();
        _config = createSoftApConfig();
//        startScan();

        _manager.addNetwork(_config);

        _receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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
        Toast.makeText(this,"Starting Scan", Toast.LENGTH_SHORT).show();
        _manager.startScan();
    }


    public void checkPermission() {
        Toast.makeText(this,"Checking WiFi Permissions", Toast.LENGTH_SHORT).show();
        // API 23+ can revoke permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, REQUEST_CHANGE_WIFI_STATE);
        } else {
//            _manager.startScan();
            Toast.makeText(this, "Permission OKAY",Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CHANGE_WIFI_STATE: {
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
            _manager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
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
//
//public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
//    private List<ScanResult> mDataset;
//
//    // Provide a reference to the views for each data item
//    // Complex data items may need more than one view per item, and
//    // you provide access to all the views for a data item in a view holder
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        // each data item is just a string in this case
//        public TextView mTextView;
//        public ViewHolder(TextView v) {
//            super(v);
//            mTextView = v;
//        }
//    }
//
//    // Provide a suitable constructor (depends on the kind of dataset)
//    public MyAdapter(List<ScanResult> myDataset) {
//        this.mDataset = myDataset;
//    }
//
//    // Create new views (invoked by the layout manager)
//    @Override
//    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
//                                                   int viewType) {
//        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.my_text_view, parent, false);
//        ...
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        // - get element from your dataset at this position
//        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset[position]);
//
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return mDataset.size();
//    }
//}