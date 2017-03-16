package com.trianz.locationalarm;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.trianz.locationalarm.Services.ReminderReceiver;
import com.trianz.locationalarm.Utils.GeofenceController;
import com.trianz.locationalarm.Utils.HomeController;
import com.trianz.locationalarm.Utils.MySingleton;
import com.trianz.locationalarm.Utils.NamedGeofence;
import com.trianz.locationalarm.Utils.ReminderSetController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.radius;
import static com.trianz.locationalarm.Utils.Constants.SharedPrefs.MY_PREFS_NAME;
import static com.trianz.locationalarm.Utils.Constants.SharedPrefs.repeatAlarmIntervalValue;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_DATE;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_REMINDER_TYPE;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_REPEAT;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_REPLY_STATUS;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_REPLY_TO;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_RESPONSE_TIME;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.SEND_NOTIFICATION_RESPONSE_;
import static com.trianz.locationalarm.Utils.ReminderSetController.dataIsValid;
import static com.trianz.locationalarm.Utils.ReminderSetController.getMonth;
import static com.trianz.locationalarm.Utils.ReminderSetController.pad;

/**
 * Created by Dibyojyoti.Majumder on 02-03-2017.
 */

public class SetReminderSentByOthers extends AppCompatActivity {
    AlarmManager alarmManager;
    Calendar myCalender = Calendar.getInstance();
    Intent alarmIntentforOthers;
    private String reminder_message, Date_To_remid;

    int reminderDay;
    int reminderMinute;
    int reminderHour;
    int reminderMonth;
    int reminderYear;
    int pendingIntentRequestCode;
    int DISCARD_KEY;

    String reminderEvent;
    String reply_status_msg;
    String response_date;
    String response_time;
    String sender_name;

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
        setContentView(R.layout.activity_home);

        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra("fromNotification")) {
            reminderDay = bundle.getInt("reminderDay");
            reply_status_msg = bundle.getString("Status_msg");
            Log.d("Status_msg",reply_status_msg);
            reminderMinute = bundle.getInt("reminderMinute");
            reminderHour = bundle.getInt("reminderHour");
            reminderMonth = bundle.getInt("reminderMonth");
            reminderYear = bundle.getInt("reminderYear");
            pendingIntentRequestCode = bundle.getInt("pendingIntentRequestCode");
            reminderEvent = bundle.getString("reminderEvent");
            sender_name = bundle.getString("sender");
            DISCARD_KEY = bundle.getInt("DISCARD_KEY");
            sendResponse(reply_status_msg);

            if(reply_status_msg.equals("Accepted")){
                String selectedDateReminder = getMonth(reminderMonth) + " " + String.valueOf(reminderDay) + ", " + String.valueOf(reminderYear) ;
                addReminderToList(selectedDateReminder,reminderEvent);
                setAlarm();
            }
        }

    }
    public void setAlarm(){
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        myCalender.set(Calendar.MINUTE, reminderMinute );
        myCalender.set(Calendar.HOUR_OF_DAY, reminderHour);
        myCalender.set(Calendar.DAY_OF_MONTH, reminderDay);
        myCalender.set(Calendar.MONTH, reminderMonth);
        myCalender.set(Calendar.YEAR, reminderYear);

        alarmIntentforOthers = new Intent(SetReminderSentByOthers.this, ReminderReceiver.class);
        alarmIntentforOthers.putExtra("reminderEvent", reminderEvent);
        alarmIntentforOthers.putExtra("pendingIntentRequestCode", pendingIntentRequestCode);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(SetReminderSentByOthers.this, pendingIntentRequestCode, alarmIntentforOthers, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(),alarmPendingIntent);
    }

    public void addReminderToList(String reminderDate,String reminder_msg){
        reminder_message = reminder_msg;
        Date_To_remid = reminderDate;

        if (reminder_message.equals("")) {
            Snackbar.make(getWindow().getDecorView(), "Set a reminder message.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {

            if (dataIsValid()) {

                NamedGeofence geofence = new NamedGeofence();
                geofence.reminder_msg = reminder_message;
                geofence.reminder_Date_ToAlarm = Date_To_remid;
                geofence.radius = radius * 1.0f;
                GeofenceController.getInstance().addGeofence(geofence, geofenceControllerListener);


            } else {
                ReminderSetController.showValidationErrorToast(this);
            }

        }
    }

    private GeofenceController.GeofenceControllerListener geofenceControllerListener = new GeofenceController.GeofenceControllerListener() {
        @Override
        public void onGeofencesUpdated() {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            SetReminderSentByOthers.this.finish();
        }

        @Override
        public void onError() {
            Toast.makeText(SetReminderSentByOthers.this, SetReminderSentByOthers.this.getString(R.string.Toast_error), Toast.LENGTH_SHORT).show();
        }


    };

    public void cancelAlarmControl(int receivedPendingIntentRequestCode) {

        if (alarmManager != null) {

            PendingIntent sender = PendingIntent.getBroadcast(SetReminderSentByOthers.this, receivedPendingIntentRequestCode,
                    alarmIntentforOthers, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.cancel(sender);
        }
    }


    public void sendResponse(String reply_message){
        response_date = String.valueOf(pad(reminderDay)) + "/" + String.valueOf(pad(reminderMonth)) + "/" + String.valueOf(reminderYear);
        response_time = String.valueOf(pad(reminderHour)) + ":" + String.valueOf(pad(reminderMinute));

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(KEY_RESPONSE_DATE, response_date);
        params.put(KEY_RESPONSE_TIME,response_time);
        params.put(KEY_RESPONSE_REMINDER_TYPE,reminderEvent);
        params.put(KEY_RESPONSE_REPLY_STATUS, reply_message);
        params.put(KEY_RESPONSE_REPLY_TO,sender_name );
        params.put(KEY_RESPONSE_REPEAT, repeatAlarmIntervalValue);

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, SEND_NOTIFICATION_RESPONSE_ ,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        OnResponseValidation(response);
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
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String access_TokenKey_sendResponse = prefs.getString("AccessToken","No Name Defined");
                String auth = access_TokenKey_sendResponse;
                headers.put("Authorization", auth);
                return headers;
            }
        };


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(JsonObjRequest);
    }

    public void OnResponseValidation(JSONObject response){
        try {
            JSONObject json = new JSONObject(response.toString());
            Log.d("RegJson",json.toString());
            Boolean status = Boolean.parseBoolean(json.getString("status"));
            String message = json.getString("message");

            if(status==true){
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

