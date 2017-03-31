package com.trianz.locationalarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.trianz.locationalarm.Services.MyFirebaseMessagingService;
import com.trianz.locationalarm.Services.ReminderReceiver;
import com.trianz.locationalarm.Utils.HomeController;
import com.trianz.locationalarm.Utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.trianz.locationalarm.Services.MyFirebaseMessagingService.message_body;
import static com.trianz.locationalarm.Services.MyFirebaseMessagingService.message_body2;
import static com.trianz.locationalarm.Utils.Constants.SharedPrefs.MY_PREFS_NAME;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_DATE;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_REMINDER_TYPE;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_REPEAT;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_REPLY_STATUS;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_REPLY_TO;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_TIME;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.SEND_NOTIFICATION_RESPONSE_;

/**
 * Created by Dibyojyoti.Majumder on 02-03-2017.
 */

public class SetReminderSentByOthers extends AppCompatActivity {

    public static AlarmManager alarmManager;
    public static Calendar myCalender = Calendar.getInstance();
    public static Intent alarmIntentforOthers;

    public static final String ACTION_ACCEPT = "ACCEPTED";
    public static final String ACTION_DISMISS = "REJECTED";
    public static int pendingIntentRequestCode;

    private static SetReminderSentByOthers inst;
    public static SetReminderSentByOthers instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder_sent_others_);

    }
    public static void setAlarm(Context context,String response_dateObj,String response_timeObj,String reminderEventObj){
        int reminderMinute = Integer.parseInt(response_timeObj.substring(3,5));
        int reminderHour = Integer.parseInt(message_body2.substring(0,2));
        int reminderDay = Integer.parseInt(response_dateObj.substring(0,2));
        int reminderMonth = Integer.parseInt(message_body.substring(3,5))-1;
        int reminderYear = Integer.parseInt(message_body.substring(6,10));

        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        myCalender.set(Calendar.MINUTE, reminderMinute );
        myCalender.set(Calendar.HOUR_OF_DAY, reminderHour);
        myCalender.set(Calendar.DAY_OF_MONTH, reminderDay);
        myCalender.set(Calendar.MONTH, reminderMonth);
        myCalender.set(Calendar.YEAR, reminderYear);

        alarmIntentforOthers = new Intent(context, ReminderReceiver.class);
        alarmIntentforOthers.putExtra("reminderEvent", reminderEventObj);
        alarmIntentforOthers.putExtra("pendingIntentRequestCode", pendingIntentRequestCode);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, pendingIntentRequestCode, alarmIntentforOthers, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(),alarmPendingIntent);
    }


    //clean this
    public void cancelAlarmControl(int receivedPendingIntentRequestCode) {
        if (alarmManager != null) {
            PendingIntent sender = PendingIntent.getBroadcast(SetReminderSentByOthers.this, receivedPendingIntentRequestCode, alarmIntentforOthers, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(sender);
        }
    }

    public static void sendResponse(final Context context,String response_dateObj,String response_timeObj,String reminderEventObj,String reply_status_msgObj,String sender_nameObj){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(KEY_RESPONSE_DATE, response_dateObj);
        params.put(KEY_RESPONSE_TIME,response_timeObj);
        params.put(KEY_RESPONSE_REMINDER_TYPE,reminderEventObj);
        params.put(KEY_RESPONSE_REPLY_STATUS, reply_status_msgObj);
        params.put(KEY_RESPONSE_REPLY_TO,sender_nameObj );
        params.put(KEY_RESPONSE_REPEAT, "Does Not repeat");

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, SEND_NOTIFICATION_RESPONSE_ ,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        OnResponseValidation(context,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        HomeController.errorInResponse(this,volleyError);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String access_TokenKey_sendResponse = prefs.getString("AccessToken","No Name Defined");
                Log.d("access",access_TokenKey_sendResponse);
                String auth = access_TokenKey_sendResponse;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(JsonObjRequest);
    }

    public static void OnResponseValidation(Context context,JSONObject response){
        try {
            JSONObject json = new JSONObject(response.toString());
            Log.d("RegJson",json.toString());
            Boolean status = Boolean.parseBoolean(json.getString("status"));
            String message = json.getString("message");

            if(status==true){
                Toast.makeText(context.getApplicationContext(),message, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context.getApplicationContext(),message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void executeTask(Context context, String action,String response_dateObj,String response_timeObj,String sender_nameObj,String reply_status_msgObj,String reminderEventObj) {
        if (ACTION_ACCEPT.equals(action)) {
            reply_status_msgObj = ACTION_ACCEPT;
            saveReminder_To_Others(context,response_dateObj,response_timeObj,reminderEventObj,reply_status_msgObj,sender_nameObj);
        }else if(ACTION_DISMISS.equals(action)){
            reply_status_msgObj = ACTION_DISMISS;
            discardReminder_To_Others(context,response_dateObj,response_timeObj,reminderEventObj,reply_status_msgObj,sender_nameObj);
        }
    }

    private static void saveReminder_To_Others(Context context,String response_dateObj,String response_timeObj,String reminderEventObj,String reply_status_msgObj,String sender_nameObj) {
        MyFirebaseMessagingService.clearAllNotifications(context);
        setAlarm(context,response_dateObj,response_timeObj,reminderEventObj);
        sendResponse(context,response_dateObj,response_timeObj,reminderEventObj,reply_status_msgObj,sender_nameObj);
    }

    private static void discardReminder_To_Others(Context context,String response_dateObj,String response_timeObj,String reminderEventObj,String reply_status_msgObj,String sender_nameObj) {
        MyFirebaseMessagingService.clearAllNotifications(context);
        sendResponse(context,response_dateObj,response_timeObj,reminderEventObj,reply_status_msgObj,sender_nameObj);
    }

}


