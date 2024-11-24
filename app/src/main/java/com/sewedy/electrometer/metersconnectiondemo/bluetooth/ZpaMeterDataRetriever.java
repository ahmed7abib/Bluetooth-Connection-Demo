package com.sewedy.electrometer.metersconnectiondemo.bluetooth;

import android.bluetooth.BluetoothSocket;

import com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks.GetMeterDataCallback;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol.ConnectionHandler;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol.ConnectionTypes;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol.MeterData;
import com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol.Packet;
import com.sewedy.electrometer.metersconnectiondemo.utils.Utils;

public class ZpaMeterDataRetriever {

    private MeterData meterData;
    private GetMeterDataCallback callback;
    private final ConnectionHandler connectionHandler;

    public ZpaMeterDataRetriever(BluetoothSocket bluetoothSocket) {
        connectionHandler = new ConnectionHandler(bluetoothSocket, this::parseMeterReply);
    }

    public void getAllData(GetMeterDataCallback sho3a3MeterDataCallback) {
        try {
            this.callback = sho3a3MeterDataCallback;
            meterData = MeterData.getInstance();
            writeBaudRate300();
        } catch (Exception e) {
            connectionHandler.closeSocket();
            this.callback.error("Error. " + e.getMessage());
        }
    }

    private void writeBaudRate300() {
        connectionHandler.writingZpa300(ConnectionTypes.baudTran_300_7_ZPA);
        Utils.sleep(30);
        writeInitPacket();
    }

    /**
     * requestPacket = Test Command will send to meter.
     * 16 = is a expected data length will returned from meter.
     */
    private void writeInitPacket() {
        byte[] requestPacket = {0, 0, 0, 0, 0};
        connectionHandler.writing(requestPacket, 16, false);
    }

    /**
     * Parse the meter reply to get customer code.
     */
    private void parseMeterReply(Packet packetModel) {
        byte[] data = Utils.hexStringToByteArray(packetModel.getData());
        StringBuilder customerCode = new StringBuilder();

        for (int i = 5; i < data.length - 2; i++) {
            customerCode.append((char) (data[i] & 0xFF));
        }

        this.connectionHandler.closeSocket();
        this.meterData.setCustomerCode(customerCode.toString());
        this.callback.getMeterDataResult(meterData);
    }
}
