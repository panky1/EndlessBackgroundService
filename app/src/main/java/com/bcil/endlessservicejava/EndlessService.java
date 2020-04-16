package com.bcil.endlessservicejava;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.bcil.endlessservicejava.network.WebCall;
import com.bcil.endlessservicejava.network.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class EndlessService extends Service implements WebCall {
    private static final String TAG = "EndlessService";
    private PowerManager.WakeLock wakeLock = null;
    private boolean isServiceStarted = false;
    private Timer timer;
    private WebService service;
    private Context activity;
    private PreferenceManager preferenceManager;
    private String getScanBarcode;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        new Utils().log("Some component want to bind with the service");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Utils().log("The service has been created".toUpperCase());
        activity = getApplicationContext();
        preferenceManager = new PreferenceManager(getApplicationContext());
        service = new WebService().getInstance();
        service.setContext(getApplicationContext());
        Notification notification = createNotification();
        startForeground(111, notification);
    }

    private Notification createNotification() {
        final String notificationChannelId = "ENDLESS SERVICE CHANNEL";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, "Endless Service notifications channel", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("Endless Service channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(Utils.NAVPAGE, TAG);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(this, notificationChannelId);
        } else {
            notificationBuilder = new Notification.Builder(this);
        }
        notificationBuilder.setContentTitle("Endless Service");
        notificationBuilder.setContentText("This is your favorite endless service working");
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setTicker("Ticker text");
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setPriority(Notification.PRIORITY_DEFAULT); // for under android 26 compatibility
        return notificationBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Utils().log("onStartCommand executed with startId:" + startId);
        if (intent != null) {
            String action = intent.getAction();
            new Utils().log("using an intent with action " + action);
            if (action != null) {
                switch (action) {
                    case "START":
                        startService();
                        break;
                    case "STOP":
                        stopService();
                        break;
                    case "":
                        new Utils().log("This should never happen. No action in the received intent");
                        break;
                }
            }
        } else {
            new Utils().log("with a null intent. It has been probably restarted by the system.");
        }
        return START_STICKY;
    }

    private void startService() {
        if (isServiceStarted) return;
        new Utils().log("Starting the foreground service task");
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
        isServiceStarted = true;
        new ServiceTracker().setServiceState(this, "STARTED");
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock");
        }
        wakeLock.acquire();

        timer = new Timer();
        int begin = 0;
        int timeInterval = 2000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                pingFakeServer();
                fetchBrokenPcsData();
            }
        }, begin, timeInterval);
    }

    private void fetchBrokenPcsData() {
        Log.d(TAG, "fetchBrokenPcsData: WebserviceCall");
        if (new Utils().isNetworkAvailable(getApplicationContext())) {
            service.setDATA("NOTIFICATIONDATA" + "~" + "amar");
            service.setResponseListener(EndlessService.this);
            service.startToHitService();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Please check internet connection...", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void pingFakeServer() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ");
        String gmtTime = simpleDateFormat.format(new Date());
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String json = deviceId + "-" + gmtTime;
        new Utils().log(json);

    }

    private void stopService() {
        new Utils().log("Stopping the foreground service");
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();
        try {
            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }
            timer.cancel();
            stopForeground(true);
            stopSelf();
        } catch (Exception e) {
            new Utils().log("Service stopped without being started:" + e.getMessage());
        }
        isServiceStarted = false;
        new ServiceTracker().setServiceState(this, "STOPPED");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Utils().log("The service has been destroyed".toUpperCase());
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getResponse(final String response) throws JSONException {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String getResponse = response != null ? response : "Data found null";
                Log.d(TAG, "getResponse:" + getResponse);
            }
        });
        if (response != null) {
            if (!response.equals("[]")) {
                List<String> stringList = getMessage(response);
                if (!response.contains("Already")) {
                    if (stringList.size() > 0) {
                        if (!stringList.get(0).equals("null")) {
                            String[] str = stringList.get(0).split("~");
                            if (getScanBarcode == null) {
                                getScanBarcode = str[0];
                                NotificationUtils notificationUtils = new NotificationUtils();
                                notificationUtils.setDataForSimpleNotification(getApplicationContext(), str[1]);
                            } else if (!str[0].equals(getScanBarcode)) {
                                getScanBarcode = str[0];
                                NotificationUtils notificationUtils = new NotificationUtils();
                                notificationUtils.setDataForSimpleNotification(getApplicationContext(), str[1]);
                            }
                        }
                    }
                }
            }
        } else {
            Handler handler1 = new Handler(Looper.getMainLooper());
            handler1.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Something went wrong,please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private List<String> getMessage(String response) throws JSONException {
        JSONArray jsonarray = new JSONArray(response);
        List<String> stringList = new ArrayList<>();
        JSONObject jsonobject = jsonarray.getJSONObject(0);
        stringList.add(jsonobject.getString("message"));
        return stringList;
    }
}
