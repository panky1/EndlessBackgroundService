package com.bcil.endlessservicejava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class StartReceiver extends BroadcastReceiver {
    private static final String TAG = "StartReceiver";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_BOOT_COMPLETED)&&new ServiceTracker().getServiceState(context).equals("STARTED")){
            Intent intent1 = new Intent(context,EndlessService.class);
            intent1.setAction(Actions.START.name());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                new Utils().log( "onReceive:Starting the service in >=26 Mode from a BroadcastReceiver");
                context.startForegroundService(intent1);
            }
            new Utils().log( "Starting the service in < 26 Mode from a BroadcastReceiver");
            context.startService(intent1);
        }

    }
}
