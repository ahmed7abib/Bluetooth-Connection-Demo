package com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks;

public interface IBluetoothReading extends IProgress {

    void showError(String error);

    void readingResult(String result);
}
