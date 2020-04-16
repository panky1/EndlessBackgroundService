package com.bcil.endlessservicejava.network;


import android.content.Context;
import android.util.Log;


import com.bcil.endlessservicejava.PreferenceManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class WebApi {
    private static final String TAG = "WebApi";
    public static final String WSDL_TARGET_NAMESPACE  = "http://tempuri.org/";
    private final PreferenceManager preferenceManager;

    String SERVER_IP                                    = "http://192.168.1.103:6161/Service.asmx";

    //METHODS

    public static final String PLANT_MASTER      = "GetPlantMaster";
    public static final String LOGIN             = /*"ValidateUserLogin"*/"Login";
    public static final String GETNOTIFICATIONDATA         = "GetNotificationData";
    public static final String LOAD_IN_LIFT      = "LoadingInLift";
    public static final String OUTBOUND_LOAD_IN_LIFT      = "OutBoundLoadingInLift";
    public static final String UNLOAD_FROM_LIFT= "UnLoadingFromLift";
    public static final String OUTBOUND_UNLOAD_FROM_LIFT= "OutBoundUnLoadingFromLift";


    public static final String PUTWAY = "PutWay";
    public static final String RELOCATION = "ReLocation";
    public static final String BROKENPCSSCAN = "BrokenPcsScan";
    public static final String PICKINGSCAN = "PickingScan";
    public static final String ASSIGNDOCKSTN = "AssignDockStn";
    public static final String SORTING = "Sorting";
    public static final String PACKINGCOMPLETION = "PackingCompletion";
    public static final String DISPATCH = "Dispatch";

    public static String SPLIT_COLUMN = "~";

    SoapSerializationEnvelope envelope;
    private Context context;

    public WebApi(Context applicationContext) {
        this.context = applicationContext;
        preferenceManager = new PreferenceManager(context);
    }



    private String callServer(SoapObject request, String method){
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        /*envelope.implicitTypes = true;
        envelope.encodingStyle = SoapSerializationEnvelope.XSD;*/
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport;
        String userMasterResponse;
//        String getIp =  "192.168.43.183/JantaWebservice/";
        String getIp =  "192.168.1.198/JantaWebservice/";

        Log.d(TAG, "SERVERIP:"+getIp);
        try {
            httpTransport = new HttpTransportSE("http://"+getIp+"Service.asmx",30000);
            httpTransport.debug = true;
            httpTransport.call(WSDL_TARGET_NAMESPACE+method, envelope);
            userMasterResponse = envelope.getResponse().toString();
        } catch (Exception e) {
            Log.e("WEB_API",e.toString());
            userMasterResponse = e.toString();
        }
        return userMasterResponse;
    }



    public String validateGetNotificationData(String mode, String username) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, GETNOTIFICATIONDATA);
        request.addProperty("strMode",mode);
        request.addProperty("strUserName",username);
        return callServer(request,GETNOTIFICATIONDATA);
    }

}