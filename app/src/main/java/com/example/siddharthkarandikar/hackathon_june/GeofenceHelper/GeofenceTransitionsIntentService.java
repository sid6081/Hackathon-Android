package com.example.siddharthkarandikar.hackathon_june.GeofenceHelper;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.siddharthkarandikar.hackathon_june.APIHelper.Map.MapActivity;
import com.example.siddharthkarandikar.hackathon_june.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by siddharth.karandikar on 02/04/18.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    private NotificationManager manager;
    private final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private final int NOTIFICATION_ID = 1;

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Toast.makeText(this, geofencingEvent.getErrorCode(), Toast.LENGTH_SHORT).show();
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            Toast.makeText(getApplicationContext(), "Geofence Entered", Toast.LENGTH_SHORT).show();
            vibrate();
//            sendNotification();
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            setNotification();
        } else {
            // Log the error.
            Toast.makeText(getApplicationContext(), "Geofence Transition Mismatch : " + geofenceTransition, Toast.LENGTH_SHORT).show();
        }
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(10 - 00);
        }
    }

    private void sendNotification1() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "Notification")
                .setSmallIcon(R.drawable.baseline_share_black_18dp)
                .setContentTitle("UNSAFE")
                .setContentText("U have entered an unsafe area")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationId is a unique int for each notification that you must define
        mNotificationManager.notify(1111, mBuilder.build());
    }

    private void setNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            manager.createNotificationChannel(notificationChannel);
        }

        Log.d("GEOFENCING_REBOOT", "INSIDE_NOTIFICATION");

        android.support.v4.app.NotificationCompat.Builder notification1 = new android.support.v4.app.NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Map_Hackathon")
                .setContentText("You have entered an unsafe area")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId(NOTIFICATION_CHANNEL_ID);

//        Intent notificationIntent = new Intent(getApplicationContext(), MapActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        notification1.setContentIntent(contentIntent);
        manager.notify(NOTIFICATION_ID, notification1.build());

    }

}
