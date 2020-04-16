package com.bcil.endlessservicejava;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

public class NotificationUtils {
    private static final String TAG = "NotificationUtils";
    private NotificationCompat.Builder notificationBuilder;
    private Bitmap icon;
    private static int currentNotificationID = 0;
//    private static int currentNotificationID = 234;

    private NotificationManager notificationManager;
    private Intent notificationIntent;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private List<String> refListStr = new ArrayList<>();
    private PreferenceManager preferenceManager;
    private String CHANNEL_ID;

    public void setDataForSimpleNotification(Context context, String msg) {
        CHANNEL_ID = "my_channel_01";
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.newnotification);
        notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon(), 2)
                .setLargeIcon(icon)
                .setContentTitle("AmulAssetTracking")
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true);
        notificationBuilder.setSound(sound);
        notificationBuilder.setTicker(msg);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        notificationBuilder.setContentText(msg).setColor(Color.parseColor("#00cc00"));
        sendNotification(context,sound,notificationBuilder);
    }

    private void sendNotification(Context context, Uri sound, NotificationCompat.Builder notificationBuilder) {
        currentNotificationID++;
        int notificationId = currentNotificationID;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra(Utils.NAVPAGE,TAG);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setOngoing(false);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX); // for under android 26 compatibility
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        if (notificationId == Integer.MAX_VALUE - 1)
            notificationId = 0;
        notificationManager.notify(notificationId, notification);
        /*PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(contentIntent);
        notificationBuilder.setSound(sound);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        if (notificationId == Integer.MAX_VALUE - 1)
            notificationId = 0;

        notificationManager.notify(notificationId, notification);
*/
    }

    private int getNotificationIcon() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return R.drawable.ic_trans_logo;
        } else {
            return R.mipmap.ic_launcher;
        }

    }
}

