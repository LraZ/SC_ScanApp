package com.example.matth.scanme.entities;

public class AccessPoint {

    private String mac;

    private Integer type;         //wlan or bluetooth ....

    private Boolean activity;

    private String description;

    private int signal;

    public AccessPoint(){

    }

    public AccessPoint(String BSSID, Integer type, boolean status){
        this.type = type;
        this.activity = status;
        this.mac = BSSID;
    }

    public AccessPoint(String BSSID, Integer type, boolean status, String desc, Integer signal){
        this.mac = BSSID;
        this.type = type;
        this.activity = status;
        this.description = desc;
        this.signal = signal;
    }

    public String getMAC() {
        return mac;
    }

    public void setMAC(String MAC) {
        this.mac = MAC;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getActivity() {
        return activity;
    }

    public void setActivity(Boolean activity) {
        this.activity = activity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    @Override
    public String toString(){
        return (description + " - " + mac + "    " + signal + " dBm");
    }

}
