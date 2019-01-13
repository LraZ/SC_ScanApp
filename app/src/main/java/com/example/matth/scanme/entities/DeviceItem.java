package com.example.matth.scanme.entities;

public class DeviceItem {
    private String deviceName;
    private String address;
    private boolean connected;


    public DeviceItem(String name, String address, Boolean connected) {
        this.deviceName = name;
        this.address = address;
        this.connected = connected;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
