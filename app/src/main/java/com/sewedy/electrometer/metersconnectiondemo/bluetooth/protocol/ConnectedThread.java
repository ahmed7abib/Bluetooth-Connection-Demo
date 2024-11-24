package com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol;

import static com.sewedy.electrometer.metersconnectiondemo.utils.Utils.HEX_ARRAY;

import android.bluetooth.BluetoothSocket;
import android.text.TextUtils;
import android.util.Log;

import com.sewedy.electrometer.metersconnectiondemo.bluetooth.callbacks.IDataCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ConnectedThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final IDataCallback dataCallback;

    private int offset = 0;
    private int packetLength = -1;

    private boolean isRead;
    private byte[] writtenPacket;
    private int expectedLength = 0;
    private boolean isDebuggable = false;
    private boolean isProbPacket = true;

    public ConnectedThread(BluetoothSocket socket, IDataCallback dataCallback, long initialMillis) throws IOException {
        mmSocket = socket;
        this.dataCallback = dataCallback;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public byte[] getWrittenPacket() {
        return writtenPacket;
    }

    public void setWrittenPacket(byte[] writtenPacket) {
        this.writtenPacket = writtenPacket;
    }

    public void setDebuggable(boolean debuggable) {
        isDebuggable = debuggable;
    }

    public boolean isProbPacket() {
        return isProbPacket;
    }

    public void setProbPacket(boolean probPacket) {
        isProbPacket = probPacket;
    }

    public int getExpectedLength() {
        return expectedLength;
    }

    public void setExpectedLength(int expectedLength) {
        this.expectedLength = expectedLength;
    }

    public synchronized void run() {
        if (isRead()) {
            byte[] buffer = new byte[2048];
            byte[] data;
            Packet packetModel = new Packet();
            int len;
            try {
                if (isProbPacket()) {
                    packetLength = 12;
                    offset = 0;
                    while (packetLength > offset) {
                        len = mmSocket.getInputStream().read(buffer);
                        offset += len;
                        data = Arrays.copyOf(buffer, len);
                        String result = getMeterPdu(data);
                        result = (TextUtils.isEmpty(packetModel.getData()) ? "" : packetModel.getData()) + result;
                        packetModel.setData(result);
                    }
                    packetLength = -1;
                    offset = 1;
                } else {
                    if (getExpectedLength() > 0) {
                        packetLength = getExpectedLength();
                    }
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    if (isDebuggable) {
                        while (packetLength > offset || packetLength == -1) {
                            len = mmSocket.getInputStream().read(buffer);
                            offset += len;
                            data = Arrays.copyOf(buffer, len);
                            outputStream.write(data);
                            if (packetLength == -1) {
                                outputStream.toByteArray();
                                if (outputStream.toByteArray().length > 1) {
                                    packetLength = (outputStream.toByteArray()[1] & 0xFF) + ((outputStream.toByteArray()[2] & 0xFF) * (256));
                                    packetLength += 5;
                                }
                            }
                        }
                    } else {
                        while (packetLength > offset || packetLength == -1) {
                            len = mmSocket.getInputStream().read(buffer);
                            offset += len;
                            data = Arrays.copyOf(buffer, len);
                            outputStream.write(data);
                            if (packetLength == -1) {
                                outputStream.toByteArray();
                                if (outputStream.toByteArray().length > 1) {
                                    packetLength = (outputStream.toByteArray()[1] & 0xFF) + ((outputStream.toByteArray()[2] & 0xFF) * (256));
                                    packetLength += 5;
                                }
                            }
                        }
                    }

                    String result = getMeterPdu(outputStream.toByteArray());
                    packetModel.setData(result);
                    packetLength = -1;
                    offset = 0;
                }
                Log.d("TAG", "prob res: " + packetModel.getData());
                dataCallback.onResult(packetModel);
            } catch (Exception e) {
                dataCallback.onResult(null);
            }
        } else {
            if (getWrittenPacket() == null || getWrittenPacket().length == 0) {
                return;
            }

            try {
                mmSocket.getOutputStream().write(getWrittenPacket());
            } catch (IOException ignored) {
            }
        }
    }

    private String getMeterPdu(byte[] bt) {
        char[] hexChars = new char[bt.length * 2];
        for (int j = 0; j < bt.length; j++) {
            int v = bt[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
