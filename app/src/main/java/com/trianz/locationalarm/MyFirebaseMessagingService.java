package com.trianz.locationalarm;

/**
 * Created by AndroidBash on 20-Aug-16.
 */

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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.trianz.locationalarm.Utils.Constants.Instances.DISCARD_KEY;
import static com.trianz.locationalarm.Utils.Constants.Instances.SAVE_KEY;
import static com.trianz.locationalarm.Utils.Constants.Instances.notificationManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    Bitmap bitmap;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

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
            String message_body = remoteMessage.getData().get("date");
          String message_body2 = remoteMessage.getData().get("time");

            //imageUri will contain URL of the image to be displayed with Notification
            String imageUri = remoteMessage.getData().get("image");
            Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.play);
            //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
            //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
            String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");
            sendNotification( messageTitle,bm, TrueOrFlase, message_body,message_body2);
        }catch (Exception e){
            Log.d("error",e.toString());
        }


    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */

    public void sendNotification(String Title, Bitmap image, String TrueOrFalse, String message_body,String message_body2) {
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
                .setContentText(message_body)
                .setSubText(message_body2)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message_body))/*Notification with Image*/
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
        Intent agreeIntent = new Intent(this,HomeActivity.class);
        SAVE_KEY = 0;
        PendingIntent resultIntent =PendingIntent.getActivity(this,0,agreeIntent,0);
        return  resultIntent;
    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

}


