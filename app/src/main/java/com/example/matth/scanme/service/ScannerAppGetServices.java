package com.example.matth.scanme.service;

import android.app.Notification;

import com.example.matth.scanme.entities.AccessPoint;
import com.example.matth.scanme.utils.APIHelper;

import java.util.List;

public class ScannerAppGetServices {

    //APIHelper api = new APIHelper(APIfinished());
    //APIHelper api = new APIHelper(this);

    public List<String> getGridPoints(){
        new APIHelper(this).execute();
        //List<String> temp = api.
        //return temp;
        return null;
    }

    public void APIfinished(List<String> returnlist){

    }

    public List<AccessPoint> getAccessPoints(){
        List<AccessPoint> temp = null;
        //api.FetchData("getAccessPoints");
        return temp;
    }
}
