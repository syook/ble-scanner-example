package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import android.util.Log;

import com.syook.BLEScanner;

import org.json.JSONObject;

public class BLEScannerService extends Service {
    int switch_fuc = 0;
    int counter = 0;

    BLEScanner BeaconScanner;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, "ch1")
                .setContentTitle("Scanning Beacon in Foreground")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();


        BeaconScanner = new BLEScanner(this);
        BeaconScanner.setBLEScannerListener(new BLEScanner.BLEScannerListener() {
            // here we get BLE Scanned Data
            @Override
            public void onScanResults(JSONObject data) {
                Log.e("data from service : " , data.toString());
            }

            // here we get error messages from library
            @Override
            public void onError(String error) {
                Log.e("Error from service : ",error);
            }

            // her we get Scan status change event
            @Override
            public void onScanStatusChange(Boolean status) {
                Log.e("Status from service : ",status.toString());
            }
        });

        BeaconScanner.Init();

        registerReceiver(m_timeChangedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        startForeground(1, notification);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_timeChangedReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel("ch1","Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT  );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

// triggered at every change of Minute
    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_TICK)) {

                counter++;
                if(switch_fuc == 0 && counter == 1)
                {
                    Log.e("scanning :", "started");
                    switch_fuc =1;

                    String[] macadd = new String[]{"AC233F293E24","AC233F293EC0","AC233F24EB00"};

                    // use this function if you want to filter scan result using MAC Addresses
                    // Skip if MAC filtering is not need
                    BeaconScanner.setMACFilter(macadd); // has to be called before every startscan call

                    // Scanning starts
                    BeaconScanner.startScan();

                }
                else if(switch_fuc == 1)
                {
                    Log.e("scanning :", "stopped");
                    switch_fuc =0;

                    // Scan stops
                    BeaconScanner.stopScan();
                }

                // this help to scan for 1 minute at every 5 minute interval.
                if(counter >= 6)
                {
                    counter =0;
                }

            }
        }
    };

}
