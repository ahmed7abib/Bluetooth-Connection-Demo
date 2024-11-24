package com.sewedy.electrometer.metersconnectiondemo.bluetooth;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;

import androidx.core.app.ActivityCompat;

import com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks.IBluetoothConnection;

import java.io.IOException;
import java.util.Set;

public class BluetoothPair {

    private final Context context;
    private final BluetoothAdapter myBluetoothAdapter;
    private final IBluetoothConnection iBluetoothConnection;

    public BluetoothPair(Context context, IBluetoothConnection iBluetoothConnection) {
        this.context = context;
        this.iBluetoothConnection = iBluetoothConnection;

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        myBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void connectToBluetooth() {
        try {
            iBluetoothConnection.showProgress();

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                iBluetoothConnection.hideProgress();
                permissionError();
                return;
            }

            Set<BluetoothDevice> bondedDevices = myBluetoothAdapter.getBondedDevices();
            BluetoothDevice device = (BluetoothDevice) bondedDevices.toArray()[0];
            String name = device.getName();
            ParcelUuid[] uuids = device.getUuids();

            if (name != null) {
                connect(uuids, device);
            } else {
                notDeviceError();
            }

            iBluetoothConnection.hideProgress();
        } catch (Exception e) {
            iBluetoothConnection.hideProgress();
            notDeviceError();
        }
    }

    @SuppressLint("MissingPermission")
    private void connect(ParcelUuid[] uuids, BluetoothDevice device) {
        BluetoothSocket mmSocket;

        try {
            mmSocket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());

            if (mmSocket != null) {
                mmSocket.connect();
                iBluetoothConnection.blueToothConnectionResult(true, device.getName(), mmSocket);
            } else {
                socketError();
            }
        } catch (IOException e) {
            try {
                final String refSocket = "createRfcommSocket";
                mmSocket = (BluetoothSocket) device.getClass().getMethod(refSocket, int.class).invoke(device, 1);

                if (mmSocket != null) {
                    mmSocket.connect();
                    iBluetoothConnection.blueToothConnectionResult(true, device.getName(), mmSocket);
                } else {
                    socketError();
                }
            } catch (Exception fallbackException) {
                socketError();
            }
        }
    }

    private void socketError() {
        iBluetoothConnection.showError("Socket Error. please unpair and try again.");
    }

    private void permissionError() {
        iBluetoothConnection.showError("Permission Error. Please allow NearBy Devices & bluetooth permissions");
    }

    private void notDeviceError() {
        iBluetoothConnection.showError("Not Devices Error. Cant get paired device, please unpair and try again.");
    }
}