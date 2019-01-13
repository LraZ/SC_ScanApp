package com.example.matth.scanme.entities;

import java.util.List;

public class GridPoint {

    private String Id;

    private Integer PosX;

    private Integer PosY;

    private List<AccessPoint> APs;

    public GridPoint(String GP_ID,Integer X,Integer Y){
        this.Id = GP_ID;
        this.PosX = X;
        this.PosY = Y;
    }

    public GridPoint(String idGridPoint, Integer posX, Integer posY, List<AccessPoint> accessPoints) {
        this.Id = idGridPoint;
        this.PosX = posX;
        this.PosY = posY;
        this.APs = accessPoints;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Integer getPosX() {
        return PosX;
    }

    public void setPosX(Integer posX) {
        PosX = posX;
    }

    public Integer getPosY() {
        return PosY;
    }

    public void setPosY(Integer posY) {
        PosY = posY;
    }

    public List<AccessPoint> getAPs() {
        return APs;
    }

    public void setAPs(List<AccessPoint> APs) {
        this.APs = APs;
    }


}
