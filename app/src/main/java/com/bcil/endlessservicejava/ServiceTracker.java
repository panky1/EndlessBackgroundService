package com.bcil.endlessservicejava;


import android.content.Context;

public class ServiceTracker {
    /*public enum ServiceState {
        STARTED,
        STOPPED,
    }*/

    private final static String name = "SPYSERVICE_KEY";
    private final static String key = "SPYSERVICE_KEY";

    public void setServiceState(Context context, String value) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.putPreferenceValues(key, value);
    }

    String getServiceState(Context context) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
//         String value = preferenceManager.getPreferenceValues(key);
        return preferenceManager.getPreferenceValues(key);
    }

}


