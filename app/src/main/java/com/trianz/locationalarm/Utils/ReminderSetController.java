package com.trianz.locationalarm.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.trianz.locationalarm.R;

import java.text.DateFormatSymbols;

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

    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public static boolean dataIsValid(String reminder_message) {
        boolean validData = true;

        String reminderString = reminder_message;

        if (TextUtils.isEmpty(reminderString)) {
            validData = false;
        } else {
            validData = true;
        }

        return validData;
    }

    public static void showValidationErrorToast(Context context) {
        Toast.makeText(context, context.getString(R.string.Toast_Validation), Toast.LENGTH_SHORT).show();
    }

    public static String getMonth(int month) {
        String getMonthText = new DateFormatSymbols().getMonths()[month];
        getMonthText = getMonthText.substring(0,3);
        Log.d("month",getMonthText);
        return getMonthText;
    }



}
