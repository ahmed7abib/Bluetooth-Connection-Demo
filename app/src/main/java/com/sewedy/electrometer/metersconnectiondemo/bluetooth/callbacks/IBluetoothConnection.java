package com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks;

import android.bluetooth.BluetoothSocket;

public interface IBluetoothConnection extends IProgress {
    void showError(String error);

    void blueToothConnectionResult(boolean isConnected, String deviceName, BluetoothSocket mmSocket);
}
