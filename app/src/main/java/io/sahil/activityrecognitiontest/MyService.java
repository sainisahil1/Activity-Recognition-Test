package io.sahil.activityrecognitiontest;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

public class MyService extends Service {

    PendingIntent pendingIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.showNotification(this));
        }
        registerActivityRecognition();
    }

    @Override
    public void onDestroy() {
        unregisterActivityRecognition();
        super.onDestroy();
    }

    void registerActivityRecognition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, new Intent(getApplicationContext(), ActivityResult.class), PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, new Intent(getApplicationContext(), ActivityResult.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        ActivityRecognition.getClient(getApplicationContext()).requestActivityUpdates(1000, pendingIntent);
        Log.e("TAG", "registerActivityRecognition: ");
    }

    void unregisterActivityRecognition() {
        if (pendingIntent != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            ActivityRecognition.getClient(getApplicationContext()).removeActivityUpdates(pendingIntent);
            Log.e("TAG", "unregisterActivityRecognition: ");
        }
    }


}

class NotificationHelper {
    public static final int NOTIFICATION_ID = 102;
    private static final int PENDING_INTENT_REQUEST_CODE = 103;
    private static final String ANDROID_CHANNEL_ID = "com.module.react";
    private static final String ANDROID_CHANNEL_NAME = "motion";

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Notification.Builder getAndroidChannelNotification(Context context) {
        String contentTitle = "App is running";
        String contentText = "Click here to open the app";
        return new Notification.Builder(context, ANDROID_CHANNEL_ID)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new Notification.BigTextStyle().bigText(contentText))
                .setAutoCancel(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Notification.Builder nb = getAndroidChannelNotification(context);
        nb.setContentIntent(pendingIntent);
        return nb.build();
    }

    public static void cancelNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(NOTIFICATION_ID);
        }
    }
}