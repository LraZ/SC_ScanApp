package com.example.matth.scanme.entities;

public class AccessPoint {

    private String MAC;

    private Integer Typ;         //wlan or bluetooth ....

    private Boolean alive;

    private String description;

    private int signal;

    public AccessPoint(String BSSID, Integer type, boolean status, String desc, Integer signal){
        this.MAC = BSSID;
        this.Typ = type;
        this.alive = status;
        this.description = desc;
        this.signal = signal;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public Integer getTyp() {
        return Typ;
    }

    public void setTyp(Integer typ) {
        Typ = typ;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
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
        return (description + " - " + MAC + "    " + signal + " dBm");
    }

}
