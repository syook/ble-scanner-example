package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.*;

//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.syook.BLEScanner;

import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    BLEScanner myBLEScanner;
    Button startScanningButton,stopScanningButton;
    TextView peripheralTextView;
    int switchgps = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("App status : ", "Destroyed");
        myBLEScanner.stopScan();
        //unregisterReceiver(m_timeChangedReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         peripheralTextView = (TextView)findViewById(R.id.PeripheralTextView);


        // start service, it has to be foreground service.
        Intent serviceIntent = new Intent(this, BLEScannerService.class);
        serviceIntent.putExtra("inputExtra", "Tab to open App");
        ContextCompat.startForegroundService(this, serviceIntent);


        // BLEScanner object
        myBLEScanner = new BLEScanner(this);

        myBLEScanner.setBLEScannerListener(new BLEScanner.BLEScannerListener() {
            // here we get BLE Scanned Data
            @Override
            public void onScanResults(JSONObject data) {
                Log.e("data from activity : " , data.toString());
                peripheralTextView.append(data.toString()+ "\n");
                // auto scroll for text view
                final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
                if (scrollAmount > 0)
                    peripheralTextView.scrollTo(0, scrollAmount);


            }

            // here we get error messages from library
            @Override
            public void onError(String error) {
                Log.e("Error from activity: ",error);
            }

            @Override
            public void onScanStatusChange(Boolean status) {
                Log.e("Status from activity : ",status.toString());
            }
        });

        // initialize BLEScanner object
        myBLEScanner.Init();


        startScanningButton = (Button) findViewById(R.id.StartScanButton);
        startScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myBLEScanner.startScan();
                startScanningButton.setVisibility(View.INVISIBLE);
                stopScanningButton.setVisibility(View.VISIBLE);
            }
        });


        stopScanningButton = (Button) findViewById(R.id.StopScanButton);
        stopScanningButton.setVisibility(View.INVISIBLE);
        stopScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myBLEScanner.stopScan();
                startScanningButton.setVisibility(View.VISIBLE);
                stopScanningButton.setVisibility(View.INVISIBLE);
            }
        });

    }

    // response from permission request
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permissiom : ","coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


}
