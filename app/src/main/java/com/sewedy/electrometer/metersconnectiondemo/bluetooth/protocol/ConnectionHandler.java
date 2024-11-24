package com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks.IDataCallback;
import com.sewedy.electrometer.metersconnectiondemo.utils.Utils;

import java.io.IOException;

public class ConnectionHandler {

    private final BluetoothSocket mmSocket;
    private ConnectedThread myThread;

    public ConnectionHandler(BluetoothSocket socket, IDataCallback IDataCallback) {
        mmSocket = socket;

        try {
            myThread = new ConnectedThread(mmSocket, IDataCallback, 2000);
        } catch (IOException ignored) {
        }

        myThread.start();
    }

    public void closeSocket() {
        try {
            mmSocket.close();
        } catch (IOException ignored) {
        }
    }

    public synchronized void startRead(boolean isProbPacket, int expectedLength) {
        myThread.setRead(true);
        myThread.setProbPacket(isProbPacket);
        myThread.setWrittenPacket(null);
        myThread.setExpectedLength(expectedLength);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
        }

        myThread.run();
    }

    public synchronized void writing(byte[] data, int expectedLength, boolean isDebuggable) {
        myThread.setRead(false);
        myThread.setProbPacket(false);
        myThread.setWrittenPacket(data);
        myThread.setDebuggable(isDebuggable);

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        myThread.start();
        startRead(false, expectedLength);
    }

    public synchronized void writingZpa300(String strPdu) {
        myThread.setRead(false);
        myThread.setProbPacket(true);
        Log.d("Writing_Prob", strPdu);
        myThread.setWrittenPacket(Utils.hexStringToByteArray(strPdu));
        Utils.sleep(100);
        myThread.start();
    }

    public synchronized void writing(String strPdu, int expectedLength) {
        myThread.setRead(false);
        myThread.setProbPacket(true);
        myThread.setWrittenPacket(Utils.hexStringToByteArray(strPdu));

        Utils.sleep(100);
        myThread.start();
        startRead(true, expectedLength);
    }
}
