package com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks;

import com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol.Packet;

public interface IDataCallback {
    void onResult(Packet data);
}
