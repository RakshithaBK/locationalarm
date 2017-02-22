package com.trianz.locationalarm.Utils;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.trianz.locationalarm.R;

import static com.trianz.locationalarm.Utils.Constants.SharedPrefs.repeatAlarmIntervalValue;

/**
 * Created by Rakshitha.Krishnayya on 22-02-2017.
 */

public class ReminderSetController {

    public static void repetationSetup(final AppCompatActivity appCompatActivity){
        //Select Repeat interval for alarm
        TextView reminderRepeat = null;
        reminderRepeat = (TextView) appCompatActivity.findViewById(R.id.reminderepeat);

        final TextView finalReminderRepeat = reminderRepeat;
        reminderRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog remindMeBeforeDialog = new Dialog(appCompatActivity);
                remindMeBeforeDialog.setContentView(R.layout.dialog_repeatalarm);

                final TextView doesNotRepeat = (TextView) remindMeBeforeDialog.findViewById(R.id.doesNotRepeat);
                final TextView everyDay = (TextView) remindMeBeforeDialog.findViewById(R.id.everyDay);
                final TextView everyWeek = (TextView) remindMeBeforeDialog.findViewById(R.id.everyWeek);
                final TextView everyMonth = (TextView) remindMeBeforeDialog.findViewById(R.id.everyMonth);
                final TextView everyYear = (TextView) remindMeBeforeDialog.findViewById(R.id.everyYear);

                if(repeatAlarmIntervalValue == "Does not repeat") {
                    doesNotRepeat.setTextColor(Color.parseColor("#9568ff"));
                }
                else if (repeatAlarmIntervalValue == "everyDay") {
                    everyDay.setTextColor(Color.parseColor("#9568ff"));
                }
                else if(repeatAlarmIntervalValue == "everyWeek"){
                    everyWeek.setTextColor(Color.parseColor("#9568ff"));
                }
                else if(repeatAlarmIntervalValue == "everyMonth"){
                    everyMonth.setTextColor(Color.parseColor("#9568ff"));
                }
                else if(repeatAlarmIntervalValue == "everyYear"){
                    everyYear.setTextColor(Color.parseColor("#9568ff"));
                }

                remindMeBeforeDialog.show();

                doesNotRepeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "Does not repeat";
                        remindMeBeforeDialog.dismiss();
                        finalReminderRepeat.setText("Does not repeat");
                    }
                });
                everyDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "everyDay";
                        remindMeBeforeDialog.dismiss();
                        finalReminderRepeat.setText("Every day");

                    }
                });
                everyWeek.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "everyWeek";
                        remindMeBeforeDialog.dismiss();
                        finalReminderRepeat.setText("Every week");

                    }
                });
                everyMonth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "everyMonth";
                        remindMeBeforeDialog.dismiss();
                        finalReminderRepeat.setText("Every month");

                    }
                });
                everyYear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "everyYear";
                        remindMeBeforeDialog.dismiss();
                        finalReminderRepeat.setText("Every year");

                    }
                });


            }
        });
    }

}