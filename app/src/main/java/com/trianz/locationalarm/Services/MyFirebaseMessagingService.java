package com.trianz.locationalarm.Services;

/**
 * Created by AndroidBash on 20-Aug-16.
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Contacts;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trianz.locationalarm.R;
import com.trianz.locationalarm.SetReminderSentByOthers;

import static com.trianz.locationalarm.Utils.Constants.Instances.context;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";

    public static  String messageTitle;
    public static  String message_body;
    public static  String message_body2;
    public static  String reminder_message;
    public static  String reply_status;

    public static int reminderDay;
    public static int reminderMonth;
    public static int reminderYear;
    public static int reminderHour;
    public static int reminderMinute;

    AlarmManager alarmManager;
    public static String reply_status_msg;
    public static int  pendingIntentRequestCode;
    public static int DISCARD_KEY = 1;
    public static int notify_ID;

    public  NotificationManager notificationManager;

    private static final int NOTIFY_REMINDER_ID =  3417;
    private static final int REPLY_REMINDER_ID =  3000;
    private static final int ACTION_IGNORE =  100;
    private static final int ACTION_SAVE =  101;




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


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "From: " + remoteMessage.getFrom());
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

                sendNotification(this);
            }else{
                sendResponseNotification(reminder_message,messageTitle,reply_status);
            }


        }catch (Exception e){
            Log.d("error",e.toString());
        }
    }

    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public  static void sendNotification(Context context) {
       String messageTitleFromContact = getContactName(context,messageTitle);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.color_Purple))
                .setSmallIcon(R.mipmap.ic_appicon)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(messageTitleFromContact)
                .setContentText("On Date: " + message_body + "\n" + "Time: " + message_body2)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("wants to remind you about " + reminder_message + "\n" + "Date: " + message_body + " " + "Time: " + message_body2))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .addAction(SaveNotification(context))
                .addAction(ignoreNotification(context))
                .setContentIntent(contentIntent(context));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_REMINDER_ID /* ID of notification */, notificationBuilder.build());

    }

    private static NotificationCompat.Action ignoreNotification(Context context){
        Intent ignoreIntent = new Intent(context, NotifyReminderIntentService.class);
        ignoreIntent.setAction(SetReminderSentByOthers.ACTION_DISMISS);
        sendSavedExtras(ignoreIntent);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(context,ACTION_IGNORE,ignoreIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_discard,"NO THANKS!",ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    private static NotificationCompat.Action SaveNotification(Context context){
        Intent saveIntent = new Intent(context, NotifyReminderIntentService.class);
        saveIntent.setAction(SetReminderSentByOthers.ACTION_ACCEPT);
        sendSavedExtras(saveIntent);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(context,ACTION_SAVE,saveIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_save_reminder,"SAVE",ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent = new Intent(context, MyFirebaseMessagingService.class);
        return PendingIntent.getActivity(context,NOTIFY_REMINDER_ID,startActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.mipmap.ic_appicon);
        return  largeIcon;
    }

    public void sendResponseNotification(String reminder_message,String Title, String reply_status) {
        Title = getContactName(context,Title);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_appicon)
                .setColor(ContextCompat.getColor(this,R.color.color_Purple))
                .setContentTitle(Title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Reminder message " + reminder_message  + " has been " + reply_status))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(REPLY_REMINDER_ID /* ID of notification */, notificationBuilder.build());

    }
    public static void sendSavedExtras(Intent SetReminderSentByOthers){
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


    public static  String getContactName(Context context,final String phoneNumber)
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
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

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


