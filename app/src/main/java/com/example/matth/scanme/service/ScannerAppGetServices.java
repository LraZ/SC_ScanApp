package com.example.matth.scanme.service;

import com.example.matth.scanme.entities.AccessPoint;
import com.example.matth.scanme.entities.GridPoint;
import com.example.matth.scanme.utils.APIHelper;

import java.util.List;

public class ScannerAppGetServices {

    APIHelper api = new APIHelper();

    public List<String> getGridPoints(){
        //api.FetchData("getGrid");
        List<String> temp = api.getGridPoints();
        return temp;
    }

    public List<AccessPoint> getAccessPoints(){
        List<AccessPoint> temp = null;
        //api.FetchData("getAccessPoints");
        return temp;
    }
}
