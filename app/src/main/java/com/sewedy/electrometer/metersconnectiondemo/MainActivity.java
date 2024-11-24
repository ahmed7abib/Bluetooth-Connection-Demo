package com.sewedy.electrometer.metersconnectiondemo;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sewedy.electrometer.metersconnectiondemo.bluetooth.BluetoothPair;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.BluetoothReading;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks.IBluetoothConnection;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks.IBluetoothReading;

public class MainActivity extends AppCompatActivity {

    private TextView resultTV;
    private ProgressBar progressBar;
    private Button btnConnect, btnReadData;

    private BluetoothSocket mmSocket;
    private String bluetoothDeviceName;

    private IBluetoothConnection bluetoothConnectionCallback = new IBluetoothConnection() {
        @Override
        public void showProgress() {
            showProgressBar();
        }

        @Override
        public void hideProgress() {
            hideProgressBar();
        }

        @Override
        public void blueToothConnectionResult(boolean isConnected, String deviceName, BluetoothSocket mmSocket) {
            if (isConnected) {
                btnReadData.setEnabled(true);
                MainActivity.this.mmSocket = mmSocket;
                MainActivity.this.bluetoothDeviceName = deviceName;
                writeResult("Paired Successfully, Device name: " + deviceName);
            } else {
                btnReadData.setEnabled(false);
                writeResult("Not paired!");
            }
        }

        @Override
        public void showError(String error) {
            writeResult(error);
        }
    };

    private IBluetoothReading bluetoothReadingCallback = new IBluetoothReading() {
        @Override
        public void readingResult(String result) {
            writeResult(result);
        }

        @Override
        public void showProgress() {
            showProgressBar();
        }

        @Override
        public void hideProgress() {
            hideProgressBar();
        }

        @Override
        public void showError(String error) {
            writeResult(error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        onClick();
    }

    private void init() {
        resultTV = findViewById(R.id.tv_output);
        btnConnect = findViewById(R.id.btn_connect);
        progressBar = findViewById(R.id.progress_bar);

        btnReadData = findViewById(R.id.btn_read_data);
        btnReadData.setEnabled(false);
    }

    private void onClick() {
        btnConnect.setOnClickListener(v -> {
            writeResult("");
            startConnection();
        });

        btnReadData.setOnClickListener(v -> readByBluetooth());
    }

    private void startConnection() {
        new Thread(this::connectToBluetooth).start();
    }

    private void connectToBluetooth() {
        new BluetoothPair(this, bluetoothConnectionCallback).connectToBluetooth();
    }

    private void readByBluetooth() {
        new BluetoothReading(bluetoothDeviceName, mmSocket, bluetoothReadingCallback).startReading();
    }

    private void showProgressBar() {
        runOnUiThread(() -> progressBar.setVisibility(ProgressBar.VISIBLE));
    }

    private void hideProgressBar() {
        runOnUiThread(() -> progressBar.setVisibility(ProgressBar.INVISIBLE));
    }

    private void writeResult(String message) {
        runOnUiThread(() -> resultTV.setText(message));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothReadingCallback = null;
        bluetoothConnectionCallback = null;
    }
}