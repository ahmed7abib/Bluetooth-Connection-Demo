package com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks;

import com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol.MeterData;

public interface GetMeterDataCallback {
    void getMeterDataResult(MeterData meterData);

    void error(String error);
}
