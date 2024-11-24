package com.sewedy.electrometer.metersconnectiondemo.utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static void showToast(Context context, String error) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (Exception ignored) {
        }
    }
}
