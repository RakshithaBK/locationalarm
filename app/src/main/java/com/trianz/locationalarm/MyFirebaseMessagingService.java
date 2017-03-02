package com.trianz.locationalarm;

/**
 * Created by AndroidBash on 20-Aug-16.
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

import static com.trianz.locationalarm.Utils.Constants.Instances.DISCARD_KEY;
import static com.trianz.locationalarm.Utils.Constants.Instances.SAVE_KEY;
import static com.trianz.locationalarm.Utils.Constants.Instances.notificationManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    Bitmap bitmap;
    String message_body;
    String message_body2;
    int reminderDay;
    int reminderMonth;
    int reminderYear;
    int reminderHour;
    int reminderMinute;
    String reminder_message;
    Calendar myCalender = Calendar.getInstance();
    AlarmManager alarmManager;
    ReminderSetActivity inst = ReminderSetActivity.instance();
    Intent alarmIntent;



    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
      //  Log.d(TAG, "From: " + remoteMessage.getFrom());
       // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
       // Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
           // Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.
    try{
            //message will contain the Push Message
            String messageTitle = remoteMessage.getData().get("from_mobile");
            //message will contain the Push Message body
             message_body = remoteMessage.getData().get("date");
             message_body2 = remoteMessage.getData().get("time");
              reminder_message = remoteMessage.getData().get("reminder_type");

            Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.play);
            //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
            //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
            String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");
            sendNotification(reminder_message, messageTitle,bm, TrueOrFlase, message_body,message_body2);
        }catch (Exception e){
            Log.d("error",e.toString());
        }


    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */

    public void sendNotification(String reminder_message,String Title, Bitmap image, String TrueOrFalse, String message_body,String message_body2) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("AnotherActivity", TrueOrFalse);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
               /* .setLargeIcon(image)Notification icon image*/
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(Title)
                .setContentText("Date: " + message_body + "\n" + "Time: " + message_body2)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("wants to remind you about " + reminder_message + "\n" + "Date: " + message_body + " " + "Time: " + message_body2))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .addAction(R.mipmap.ic_addlocation, "Save", SaveIntent())
                .addAction(R.mipmap.ic_addreminder, "Discard", DiscardIntent());

         notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


    }
public PendingIntent DiscardIntent(){
    Intent disAgreeIntent = new Intent(this, PushNotificationReceiver.class);
    DISCARD_KEY = 0;
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, disAgreeIntent, 0);
    startService(disAgreeIntent);
    return  pendingIntent;
}
    public PendingIntent SaveIntent(){
        reminderDay = Integer.parseInt(message_body.substring(0,2));
        reminderMonth = Integer.parseInt(message_body.substring(3,5)) - 1;
        Log.d("others month",String.valueOf(reminderMonth));
        reminderYear = Integer.parseInt(message_body.substring(6,10));
        reminderHour = Integer.parseInt(message_body2.substring(0,2));
        reminderMinute = Integer.parseInt(message_body2.substring(3,5));

        myCalender.set(Calendar.MINUTE, reminderMinute );
        myCalender.set(Calendar.HOUR_OF_DAY, reminderHour);
        myCalender.set(Calendar.DAY_OF_MONTH, reminderDay);
        myCalender.set(Calendar.MONTH, reminderMonth);
        myCalender.set(Calendar.YEAR, reminderYear);

            int  pendingIntentRequestCode = reminderHour + reminderMinute + reminderDay + reminderMonth + reminderYear;
             alarmIntent = new Intent(MyFirebaseMessagingService.this, ReminderReceiver.class);
            alarmIntent.putExtra("reminderEvent", reminder_message);
            alarmIntent.putExtra("pendingIntentRequestCode", pendingIntentRequestCode);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(MyFirebaseMessagingService.this, pendingIntentRequestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), 1000 * 60 * 5, alarmPendingIntent);

                //String selectedDateReminder = String.valueOf(reminderMonth) + " " + String.valueOf(reminderDay) + ", " + String.valueOf(reminderYear) ;
              // inst.addReminderToList(selectedDateReminder,reminder_message);

//                Handler h = new Handler();
//                long delayInMilliseconds = 1000 * 1;
//                h.postDelayed(new Runnable() {
//                    public void run() {
//                        Intent intent = new Intent(MyFirebaseMessagingService.this, HomeActivity.class);
//                        startActivity(intent);
//                    }
//                }, delayInMilliseconds);

        Intent agreeIntent = new Intent(this,HomeActivity.class);
        SAVE_KEY = 0;
        PendingIntent resultIntent =PendingIntent.getActivity(this,0,agreeIntent,0);
        return  resultIntent;
    }
}


