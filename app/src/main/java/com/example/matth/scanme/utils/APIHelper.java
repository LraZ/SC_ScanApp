package com.example.matth.scanme.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class APIHelper extends AsyncTask<Void, Void, Void> {

    private String method = "";

    public List<String> FetchData(String method){
        List<String> temp = null;
        return temp;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //get json data = parse to array (ap, gp)
        //return list

        return null;
    }

    //sendData
}
