package com.trianz.locationalarm.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.trianz.locationalarm.R;

/**
 * Created by Rakshitha.Krishnayya on 02-03-2017.
 */

public class ReminderReceiver extends WakefulBroadcastReceiver {

    private NotificationManager alarmNotificationManager;
    int pendingIntentRequestCode;
    Ringtone ringtone;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,100,1000,300,200,100,500,200,100};
        vibrator.vibrate(pattern,-1);

        Bundle bundle = intent.getExtras();
        String reminderEvent = bundle.getString("reminderEvent");
        pendingIntentRequestCode = bundle.getInt("pendingIntentRequestCode");


//        Intent alarmringingIntent = new Intent(context, AlarmRingingForOthers.class);
//           alarmringingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//        alarmringingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        alarmringingIntent.putExtra("reminderEvent", reminderEvent);
//        alarmringingIntent.putExtra("pendingIntentRequestCode", pendingIntentRequestCode);
//        context.startActivity(alarmringingIntent);

        sendNotification(reminderEvent, context);
        Handler h2 = new Handler();
        long alarmDuration = 1000 * 10 ;
        h2.postDelayed(new Runnable() {
            public void run() {
                ringtone.stop();
            }
        }, alarmDuration);
    }



    private void sendNotification(String msg, Context context) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);

        alarmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MyFirebaseMessagingService.class), 0);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Location Alarm Application").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);


        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(pendingIntentRequestCode, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");

        Handler h = new Handler();
        long delayInMilliseconds = 1000 * 60 * 60;
        h.postDelayed(new Runnable() {
            public void run() {
                alarmNotificationManager.cancel(pendingIntentRequestCode);
            }
        }, delayInMilliseconds);

    }
}

