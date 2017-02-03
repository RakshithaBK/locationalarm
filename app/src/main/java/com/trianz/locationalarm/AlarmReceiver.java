package com.trianz.locationalarm;

/**
 * Created by Dibyojyoti.Majumder on 04-01-2017.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private NotificationManager alarmNotificationManager;
    int pendingIntentRequestCode;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(5000);

        Bundle bundle = intent.getExtras();
        String reminderEvent = bundle.getString("reminderEvent");
        Boolean allDayFlag = bundle.getBoolean("allDayFlag");
        String repeatAlarmIntervalValue = bundle.getString("repeatAlarmIntervalValue");
        pendingIntentRequestCode = bundle.getInt("pendingIntentRequestCode");

        Log.d("reminderEvent", reminderEvent);

        if (allDayFlag == false) {
            Intent alarmringingIntent = new Intent(context, AlarmRingingActivity.class);
            alarmringingIntent.putExtra("reminderEvent", reminderEvent);
            alarmringingIntent.putExtra("repeatAlarmIntervalValue", repeatAlarmIntervalValue);
            alarmringingIntent.putExtra("pendingIntentRequestCode", pendingIntentRequestCode);
            context.startActivity(alarmringingIntent);
        }

        sendNotification(reminderEvent, context);

    }
    private void sendNotification(String msg, Context context) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);

        alarmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ReminderSetActivity.class), 0);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Location Alarm Application").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);


        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(pendingIntentRequestCode, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");

        Handler h = new Handler();
        long delayInMilliseconds = 1000 * 60 * 10;
        h.postDelayed(new Runnable() {
            public void run() {
                alarmNotificationManager.cancel(pendingIntentRequestCode);
            }
        }, delayInMilliseconds);

    }
}

