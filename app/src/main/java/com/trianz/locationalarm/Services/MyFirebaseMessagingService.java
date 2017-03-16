package com.trianz.locationalarm.Services;

/**
 * Created by AndroidBash on 20-Aug-16.
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Contacts;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trianz.locationalarm.R;
import com.trianz.locationalarm.SetReminderSentByOthers;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    Bitmap bitmap;
    String message_body;
    String message_body2;
    String messageTitle;
    String reply_status;
    int reminderDay;
    int reminderMonth;
    int reminderYear;
    int reminderHour;
    int reminderMinute;
    String reminder_message;
    AlarmManager alarmManager;
    String reply_status_msg;
    int  pendingIntentRequestCode;
    int DISCARD_KEY = 1;
    int notify_ID;

    public  NotificationManager notificationManager;






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
             messageTitle = remoteMessage.getData().get("from_mobile");
            //message will contain the Push Message body
            reply_status = remoteMessage.getData().get("reply_status");
            message_body = remoteMessage.getData().get("date");
            message_body2 = remoteMessage.getData().get("time");
            reminder_message = remoteMessage.getData().get("reminder_type");
         //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
            String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");

            if(reply_status == null){
                sendNotification(reminder_message, messageTitle, TrueOrFlase, message_body,message_body2);
            }else{
                sendResponseNotification(reminder_message,messageTitle,reply_status);
            }


        }catch (Exception e){
            Log.d("error",e.toString());
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */

    public void sendNotification(String reminder_message,String Title, String TrueOrFalse, String message_body,String message_body2) {
        Title = getContactName(Title);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("AnotherActivity", TrueOrFalse);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
               /* .setLargeIcon(image)Notification icon image*/
                .setSmallIcon(R.mipmap.ic_appicon)
                .setContentTitle(Title)
                .setContentText("Date: " + message_body + "\n" + "Time: " + message_body2)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("wants to remind you about " + reminder_message + "\n" + "Date: " + message_body + " " + "Time: " + message_body2))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .addAction(R.mipmap.ic_addlocation, "Save", SaveIntent());
                //.addAction(R.mipmap.ic_addreminder, "Discard", DiscardIntent());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         notify_ID = generateRandomNumber();
        Log.d("notify_id",String.valueOf(notify_ID));
        notificationManager.notify(notify_ID /* ID of notification */, notificationBuilder.build());

    }


    public void sendResponseNotification(String reminder_message,String Title, String reply_status) {
        Title = getContactName(Title);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
               /* .setLargeIcon(image)Notification icon image*/
                .setSmallIcon(R.mipmap.ic_appicon)
                .setContentTitle(Title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Reminder message " + reminder_message  + " has been " + reply_status))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notify_ID = generateRandomNumber();
        notificationManager.notify(notify_ID /* ID of notification */, notificationBuilder.build());

    }
    private int generateRandomNumber(){
        Random rand = new Random();
        int  n = rand.nextInt(50) + 1;
        return n;
    }


    public PendingIntent DiscardIntent(){
        Intent disAgreeIntent = new Intent(MyFirebaseMessagingService.this, SetReminderSentByOthers.class);
        reply_status_msg = "Rejected";
        disAgreeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendSavedExtras(disAgreeIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,disAgreeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return  pendingIntent;
    }

    public PendingIntent SaveIntent(){
        Intent SetReminderSentByOthers = new Intent(MyFirebaseMessagingService.this, SetReminderSentByOthers.class);
        reply_status_msg = "Accepted";
        SetReminderSentByOthers.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendSavedExtras(SetReminderSentByOthers);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,SetReminderSentByOthers,PendingIntent.FLAG_UPDATE_CURRENT);
        return  pendingIntent;
    }



    public void sendSavedExtras(Intent SetReminderSentByOthers){
        reminderDay = Integer.parseInt(message_body.substring(0,2));
        reminderMonth = Integer.parseInt(message_body.substring(3,5))-1;
        reminderYear = Integer.parseInt(message_body.substring(6,10));
        reminderHour = Integer.parseInt(message_body2.substring(0,2));
        reminderMinute = Integer.parseInt(message_body2.substring(3,5));

        SetReminderSentByOthers.putExtra("reminderDay", reminderDay);
        SetReminderSentByOthers.putExtra("reminderMinute", reminderMinute);
        SetReminderSentByOthers.putExtra("reminderHour", reminderHour);
        SetReminderSentByOthers.putExtra("reminderMonth", reminderMonth);
        SetReminderSentByOthers.putExtra("reminderYear", reminderYear);
        SetReminderSentByOthers.putExtra("pendingIntentRequestCode", pendingIntentRequestCode);
        SetReminderSentByOthers.putExtra("reminderEvent", reminder_message);
        SetReminderSentByOthers.putExtra("sender",messageTitle);
        SetReminderSentByOthers.putExtra("fromNotification", true);
        SetReminderSentByOthers.putExtra("DISCARD_KEY",DISCARD_KEY);
        SetReminderSentByOthers.putExtra("Status_msg",reply_status_msg);
        pendingIntentRequestCode = reminderHour + reminderMinute + reminderDay + reminderMonth + reminderYear;
    }


    public String getContactName(final String phoneNumber)
    {
        Uri uri;
        String[] projection;
        Uri mBaseUri = Contacts.Phones.CONTENT_FILTER_URL;
        projection = new String[] { android.provider.Contacts.People.NAME };
        try {
            Class<?> c =Class.forName("android.provider.ContactsContract$PhoneLookup");
            mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
            projection = new String[] { "display_name" };
        }
        catch (Exception e) {
        }


        uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber));
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

        String contactName = "";

        if (cursor.moveToFirst())
        {
            contactName = cursor.getString(0);
        }

        cursor.close();
        cursor = null;

        if(contactName == "") {
            return phoneNumber;
        }
        else {
            return contactName;
        }
    }


}


