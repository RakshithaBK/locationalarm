package com.trianz.locationalarm.Services;

/**
 * Created by Dibyojyoti.Majumder on 04-01-2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.trianz.locationalarm.AlarmRingingActivity;
import com.trianz.locationalarm.R;
import com.trianz.locationalarm.ReminderSetActivity;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private NotificationManager alarmNotificationManager;
    int pendingIntentRequestCode;
    String audioFilePath;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,100,1000,300,200,100,500,200,100};
        vibrator.vibrate(pattern,-1);

        Bundle bundle = intent.getExtras();
        String reminderEvent = bundle.getString("reminderEvent");
        Boolean allDayFlag = bundle.getBoolean("allDayFlag");
        String repeatAlarmIntervalValue = bundle.getString("repeatAlarmIntervalValue");
        String notificationTypeValue = bundle.getString("notificationTypeValue");
        pendingIntentRequestCode = bundle.getInt("pendingIntentRequestCode");

        if (notificationTypeValue.equals("Voice")) {
            audioFilePath = bundle.getString("audioFilePath");
        }

        Log.d("reminderEvent", reminderEvent);

        if (allDayFlag == false) {
            Intent alarmringingIntent = new Intent(context, AlarmRingingActivity.class);
//            alarmringingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            alarmringingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmringingIntent.putExtra("reminderEvent", reminderEvent);
            alarmringingIntent.putExtra("repeatAlarmIntervalValue", repeatAlarmIntervalValue);
            alarmringingIntent.putExtra("pendingIntentRequestCode", pendingIntentRequestCode);
            alarmringingIntent.putExtra("notificationTypeValue", notificationTypeValue);
            if (notificationTypeValue.equals("Voice")) {
                alarmringingIntent.putExtra("audioFilePath", audioFilePath);
            }
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

