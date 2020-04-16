package com.bcil.endlessservicejava.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONException;

import static com.bcil.endlessservicejava.network.WebApi.ASSIGNDOCKSTN;
import static com.bcil.endlessservicejava.network.WebApi.BROKENPCSSCAN;
import static com.bcil.endlessservicejava.network.WebApi.DISPATCH;
import static com.bcil.endlessservicejava.network.WebApi.GETNOTIFICATIONDATA;
import static com.bcil.endlessservicejava.network.WebApi.LOAD_IN_LIFT;
import static com.bcil.endlessservicejava.network.WebApi.LOGIN;
import static com.bcil.endlessservicejava.network.WebApi.OUTBOUND_LOAD_IN_LIFT;
import static com.bcil.endlessservicejava.network.WebApi.OUTBOUND_UNLOAD_FROM_LIFT;
import static com.bcil.endlessservicejava.network.WebApi.PACKINGCOMPLETION;
import static com.bcil.endlessservicejava.network.WebApi.PICKINGSCAN;
import static com.bcil.endlessservicejava.network.WebApi.PUTWAY;
import static com.bcil.endlessservicejava.network.WebApi.RELOCATION;
import static com.bcil.endlessservicejava.network.WebApi.SORTING;
import static com.bcil.endlessservicejava.network.WebApi.UNLOAD_FROM_LIFT;


/**
 * Created by ankitdassmathur on 05/15/2019.
 */
public class WebService {

    private WebService service = null;

    private Activity activity = null;
    private Context context = null;
    private WebCall responseListener = null;
    private WebApi api = null;

    private String PREFIX = "";
    private String DATA = "";
    public String METHOD = "PLANT_MASTER";

    public WebService getInstance() {
        if (service == null) {
            service = new WebService();
        }
        return service;
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
        api = new WebApi(activity.getApplicationContext());
    }

    public void setContext(Context context) {
        this.context = context;
        api = new WebApi(context);
    }

    public void setResponseListener(WebCall responseListener) {
        this.responseListener = responseListener;
    }

    public void startToHitService() {
        new CallService().execute();
    }

    public void setPREFIX(String PREFIX) {
        this.PREFIX = PREFIX;
        Log.e("WEB_SERVICES_PREFIX", this.PREFIX);
    }

    public void setMETHOD(String METHOD) {
        this.METHOD = METHOD;
        Log.e("WEB_SERVICES_METHOD", this.METHOD);
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
        Log.e("WEB_SERVICES_DATA", this.DATA);
    }


    @SuppressLint("StaticFieldLeak")
    private class CallService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            final String[] response = {""};
            String[] data = DATA.split("~");
            response[0] = api.validateGetNotificationData(data[0], data[1]);
            return response[0];
        }

        @Override
        protected void onPostExecute(String serviceResponse) {
            super.onPostExecute(serviceResponse);
            Log.d("RESPONSE", serviceResponse);
            if (serviceResponse.startsWith("java.net.ConnectException: failed to connect")) {
                serviceResponse = "Failed to connect,Please try again.";
            } else if (serviceResponse.startsWith("java.net.SocketTimeoutException: failed to connect")) {
                serviceResponse = "Failed to connect,Please try again.";
            } else if (serviceResponse.contains("Connection timed out")) {
                serviceResponse = "Failed to connect,Please try again.";
            }
            if (responseListener != null) {
                try {
                    responseListener.getResponse(serviceResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}