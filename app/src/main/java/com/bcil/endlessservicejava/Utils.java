package com.bcil.endlessservicejava;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {
    public static final String EMPTY = "";
    private NetworkInfo networkInfo;
    public static final String NAVPAGE = "NAVPAGE";
    public void log(String msg) {
        Log.d("ENDLESS-SERVICE", msg);
    }

    public  boolean isNetworkAvailable(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
