package com.trianz.locationalarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

/**
 * Created by Dibyojyoti.Majumder on 02-03-2017.
 */

public class SetReminderSentByOthers extends AppCompatActivity {
    AlarmManager alarmManager;
    Calendar myCalender = Calendar.getInstance();
    Intent alarmIntentforOthers;

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

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Bundle bundle = getIntent().getExtras();
        int reminderDay = bundle.getInt("reminderDay");
        int reminderMinute = bundle.getInt("reminderMinute");
        int reminderHour = bundle.getInt("reminderHour");
        int reminderMonth = bundle.getInt("reminderMonth");
        int reminderYear = bundle.getInt("reminderYear");
        int pendingIntentRequestCode = bundle.getInt("pendingIntentRequestCode");
        String reminderEvent = bundle.getString("reminderEvent");

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

    public void cancelAlarmControl(int receivedPendingIntentRequestCode) {

        if (alarmManager != null) {

            PendingIntent sender = PendingIntent.getBroadcast(SetReminderSentByOthers.this, receivedPendingIntentRequestCode,
                    alarmIntentforOthers, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.cancel(sender);
        }
    }

}

