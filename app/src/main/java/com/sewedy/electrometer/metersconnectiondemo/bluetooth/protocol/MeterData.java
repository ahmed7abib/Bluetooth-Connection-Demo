package com.sewedy.electrometer.metersconnectiondemo.bluetooth.protocol;


public class MeterData {

    private static MeterData meterData;
    private String customerCode;

    private MeterData() {
    }

    public static MeterData getInstance() {
        if (meterData != null) {
            return meterData;
        }
        meterData = new MeterData();
        return meterData;
    }


    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
