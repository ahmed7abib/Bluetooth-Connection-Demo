package com.sewedy.electrometer.metersconnectiondemo.bluetooth;

import android.bluetooth.BluetoothSocket;

import com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks.GetMeterDataCallback;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks.IBluetoothReading;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol.MeterData;


public class BluetoothReading {

    private final BluetoothSocket mmSocket;
    private final String bluetoothDeviceName;
    private final IBluetoothReading iBluetoothReading;

    public BluetoothReading(String bluetoothDeviceName, BluetoothSocket mmSocket, IBluetoothReading iBluetoothReading) {
        this.mmSocket = mmSocket;
        this.iBluetoothReading = iBluetoothReading;
        this.bluetoothDeviceName = bluetoothDeviceName;
    }

    public void startReading() {
        iBluetoothReading.showProgress();

        if (bluetoothDeviceName == null) {
            iBluetoothReading.hideProgress();
            nullDeviceNameError();
            return;
        }

        if (bluetoothDeviceName.toLowerCase().contains("ZPA".toLowerCase())) {
            new Thread(this::getZpaData).start();
        } else if (bluetoothDeviceName.toLowerCase().contains("TesPro".toLowerCase())) {
            new Thread(this::getTesProData).start();
        } else {
            iBluetoothReading.hideProgress();
            notSupportedDevError();
        }
    }

    private void getTesProData() {
        TesProMeterDataRetriever tesProMeterDataRetriever = new TesProMeterDataRetriever(mmSocket);
        tesProMeterDataRetriever.getAllData(new GetMeterDataCallback() {
            @Override
            public void getMeterDataResult(MeterData meterData) {
                try {
                    iBluetoothReading.hideProgress();
                    iBluetoothReading.readingResult("Customer code: " + meterData.getCustomerCode());
                } catch (Exception e) {
                    iBluetoothReading.hideProgress();
                    iBluetoothReading.showError("ERROR. " + e.getMessage());
                }
            }

            @Override
            public void error(String error) {
                iBluetoothReading.showError(error);
            }
        });
    }

    private void getZpaData() {
        ZpaMeterDataRetriever zpaMeterDataRetriever = new ZpaMeterDataRetriever(mmSocket);
        zpaMeterDataRetriever.getAllData(new GetMeterDataCallback() {
            @Override
            public void getMeterDataResult(MeterData meterData) {
                try {
                    iBluetoothReading.hideProgress();
                    iBluetoothReading.readingResult("Customer code: " + meterData.getCustomerCode());
                } catch (Exception e) {
                    iBluetoothReading.hideProgress();
                    iBluetoothReading.showError("ERROR. " + e.getMessage());
                }
            }

            @Override
            public void error(String error) {
                iBluetoothReading.showError(error);
            }
        });
    }

    private void nullDeviceNameError() {
        iBluetoothReading.showError("Error. No pair device found, please try again.");
    }

    private void notSupportedDevError() {
        iBluetoothReading.showError("Not Supported Device Found!");
    }
}