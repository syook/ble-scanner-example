package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myBLEScanner.stopScan();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peripheralTextView = (TextView)findViewById(R.id.PeripheralTextView);

        // BLEScanner object
        myBLEScanner = new BLEScanner(this);

        myBLEScanner.setBLEScannerListener(new BLEScanner.BLEScannerListener() {
            // here we get BLE Scanned Data
            @Override
            public void onScanResults(JSONObject data) {
                Log.e("data" , data.toString());
                peripheralTextView.append(data.toString()+ "\n");
                // auto scroll for text view
                final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
                if (scrollAmount > 0)
                    peripheralTextView.scrollTo(0, scrollAmount);
            }

            // here we get error messages from library
            @Override
            public void onError(String error) {
                Log.e("Error : ",error);
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
