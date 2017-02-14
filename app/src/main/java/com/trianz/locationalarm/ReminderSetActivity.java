package com.trianz.locationalarm;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;


/**
 * Created by Dibyojyoti.Majumder on 05-01-2017.
 */

public class ReminderSetActivity extends AppCompatActivity {

    Calendar myCalender = Calendar.getInstance();
    Boolean allDayFlag;
    String remindMeBeforeTimeValue = "0minutes";
    String repeatAlarmIntervalValue = "Does not repeat";
    int selectedHourAlarm;
    int selectedMinuteAlarm;
    int selectedYearAlarm;
    int selectedMonthAlarm;
    int selectedDayAlarm;

    String reminderEvent;

    int remindMeBeforeTimeValueInInt = 0;
    int counterMinitueValue;

    //Alarm
    AlarmManager alarmManager;
    PendingIntent alarmPendingIntent;
    Intent alarmIntent;

    int pendingIntentRequestCode;

    private static ReminderSetActivity inst;
    public static ReminderSetActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    //For Audio
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    String audioFilePath;

    boolean isRecording = false;

    Button stopButton;
    Button playButton;
    Button recordButton;

    String notificationTypeValue = "Notification";

    /***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setreminder);

        this.getSupportActionBar().hide();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Bundle bundle = getIntent().getExtras();
        reminderEvent = bundle.getString("reminderEvent");

        TextView datePicked =  (TextView)findViewById(R.id.datePicker);
        final TextView timePicked =  (TextView)findViewById(R.id.timePicker);
        Switch enableAllDay = (Switch) findViewById(R.id.switchIcon);

        //set the alarm text selected from previous layout
        TextView reminderTsk = (TextView) findViewById(R.id.finalTaskSet);
        reminderTsk.setText(reminderEvent);

        //Set current time and date to textView
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        final String currentDate = currentDateFormat.format(myCalender.getTime());

        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm ");
        String currentTime = currentTimeFormat.format(myCalender.getTime());

        datePicked.setText(currentDate);
        timePicked.setText(currentTime);






        //if user does not use datepicker and timepicker
        SimpleDateFormat cHourFormat = new SimpleDateFormat("HH");
        String cHour = cHourFormat.format(myCalender.getTime());
        selectedHourAlarm = Integer.parseInt(cHour);

        SimpleDateFormat cMinuteFormat = new SimpleDateFormat("mm");
        String cMinute = cMinuteFormat.format(myCalender.getTime());
        selectedMinuteAlarm = Integer.parseInt(cMinute);

        SimpleDateFormat cDayFormat = new SimpleDateFormat("d");
        String cDay = cDayFormat.format(myCalender.getTime());
        selectedDayAlarm = Integer.parseInt(cDay);

        SimpleDateFormat cMonthFormat = new SimpleDateFormat("MM");
        String cMonth = cMonthFormat.format(myCalender.getTime());
        selectedMonthAlarm = (Integer.parseInt(cMonth)) - 1;

//        SimpleDateFormat cYearFormat = new SimpleDateFormat("YYYY");
//        String cYear = cYearFormat.format(myCalender.getTime());
//        selectedYearAlarm = Integer.parseInt(cYear);
        selectedYearAlarm = Calendar.getInstance().get(Calendar.YEAR);

        //For Map
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//
//        fragmentTransaction.replace(R.id.mapView, new ContactFragment());
//        fragmentTransaction.commit();

        //DatePicker
        datePicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DatePickerDialog dialog = new DatePickerDialog(ReminderSetActivity.this, new DatePickerDialog.OnDateSetListener() {
                             @Override
                     public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub
                                 Toast.makeText(ReminderSetActivity.this, ""+arg1+"/"+(arg2+1)+"/"+arg3, Toast.LENGTH_SHORT).show();
                         }
                     }, myCalender.YEAR, myCalender.MONTH, myCalender.DAY_OF_MONTH);
                 dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                 dialog.setTitle(null);
                 dialog.show();
            }
        });

        //TimePicker
        timePicked.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ReminderSetActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        TextView timePicked =  (TextView)findViewById(R.id.timePicker);
                        //timePicked.setText("" +  selectedHour + ":" + selectedMinute);
                        timePicked.setText(new StringBuilder().append(pad(selectedHour))
                                .append(":").append(pad(selectedMinute)));

                        selectedHourAlarm = selectedHour;
                        selectedMinuteAlarm = selectedMinute;
                    }
                }, hour, minute, false);//Yes 24 hour time
                // mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        //clicking on cancel button go back to previous page
        ImageView closeTask = (ImageView) findViewById(R.id.closeIcon1);

        closeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ReminderSetActivity.this, RemindMeTask.class);
                startActivity(intent);
            }
        });




        //set the switch for allDay to off and get the status on change
        enableAllDay.setChecked(false);
        enableAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    allDayFlag = true;
                    timePicked.setVisibility(View.INVISIBLE);

                }else {
                    allDayFlag = false;
                    timePicked.setVisibility(View.VISIBLE);
                }

            }
        });


        //Select How much time before the alarm should ring
        final TextView remindMeBefore = (TextView) findViewById(R.id.remindMeBefore);

        remindMeBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog remindMeBeforeDialog = new Dialog(ReminderSetActivity.this);
                remindMeBeforeDialog.setContentView(R.layout.dialog_reminderbeforetime);

                final TextView zeroMinutesBefore = (TextView) remindMeBeforeDialog.findViewById(R.id.zeroMinutesBefore);
                final TextView tenMinutesBefore = (TextView) remindMeBeforeDialog.findViewById(R.id.tenMinutesBefore);
                final TextView twentyMinutesBefore = (TextView) remindMeBeforeDialog.findViewById(R.id.twentyMinutesBefore);
                final TextView thirtyMinutesBefore = (TextView) remindMeBeforeDialog.findViewById(R.id.thirtyMinutesBefore);
                final TextView oneHourBefore = (TextView) remindMeBeforeDialog.findViewById(R.id.oneHourBefore);

                if(remindMeBeforeTimeValue == "10minutes") {
                    tenMinutesBefore.setTextColor(Color.parseColor("#9568ff"));
                }
                else if (remindMeBeforeTimeValue == "0minutes") {
                    zeroMinutesBefore.setTextColor(Color.parseColor("#9568ff"));
                }
                else if (remindMeBeforeTimeValue == "20minutes") {
                    twentyMinutesBefore.setTextColor(Color.parseColor("#9568ff"));
                }
                else if(remindMeBeforeTimeValue == "30minutes"){
                    thirtyMinutesBefore.setTextColor(Color.parseColor("#9568ff"));
                }
                else if(remindMeBeforeTimeValue == "1hour"){
                    oneHourBefore.setTextColor(Color.parseColor("#9568ff"));
                }
                remindMeBeforeDialog.show();

                zeroMinutesBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remindMeBeforeTimeValueInInt = 0;
                        remindMeBeforeTimeValue = "0minutes";
                        remindMeBeforeDialog.dismiss();
                        remindMeBefore.setText("On time");
                    }
                });

                tenMinutesBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remindMeBeforeTimeValueInInt = 10 ;
                        remindMeBeforeTimeValue = "10minutes";
                        remindMeBeforeDialog.dismiss();
                        remindMeBefore.setText("10 minutes before");
                    }
                });

                twentyMinutesBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remindMeBeforeTimeValueInInt = 20 ;
                        remindMeBeforeTimeValue = "20minutes";
                        remindMeBeforeDialog.dismiss();
                        remindMeBefore.setText("20 minutes before");
                    }
                });

                thirtyMinutesBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remindMeBeforeTimeValueInInt = 30;
                        remindMeBeforeTimeValue = "30minutes";
                        remindMeBeforeDialog.dismiss();
                        remindMeBefore.setText("30 minutes before");
                    }
                });

                oneHourBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remindMeBeforeTimeValueInInt = 60;
                        remindMeBeforeTimeValue = "1hour";
                        remindMeBeforeDialog.dismiss();
                        remindMeBefore.setText("1 hour before");
                    }
                });
            }
        });

        //Select Repeat interval for alarm
        final TextView reminderRepeat = (TextView) findViewById(R.id.reminderepeat);

        reminderRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog remindMeBeforeDialog = new Dialog(ReminderSetActivity.this);
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
                        reminderRepeat.setText("Does not repeat");
                    }
                });
                everyDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "everyDay";
                        remindMeBeforeDialog.dismiss();
                        reminderRepeat.setText("Every day");

                    }
                });
                everyWeek.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "everyWeek";
                        remindMeBeforeDialog.dismiss();
                        reminderRepeat.setText("Every week");

                    }
                });
                everyMonth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "everyMonth";
                        remindMeBeforeDialog.dismiss();
                        reminderRepeat.setText("Every month");

                    }
                });
                everyYear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repeatAlarmIntervalValue = "everyYear";
                        remindMeBeforeDialog.dismiss();
                        reminderRepeat.setText("Every year");

                    }
                });


            }
        });


        //Save reminder as audio file
        final TextView tv_reminderType = (TextView) findViewById(R.id.tv_reminderType);


        tv_reminderType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog remindMeType = new Dialog(ReminderSetActivity.this);
                remindMeType.setContentView(R.layout.dialog_remindertype);


                final TextView typeNotification = (TextView) remindMeType.findViewById(R.id.notificationType);
                final TextView typeVoice = (TextView) remindMeType.findViewById(R.id.voiceType);
                final LinearLayout voiceRecordButtons = (LinearLayout) remindMeType.findViewById(R.id.ll_showVoiceRecordButtons);

                recordButton = (Button) remindMeType.findViewById(R.id.btn_record);
                playButton = (Button) remindMeType.findViewById(R.id.btn_play);
                stopButton = (Button) remindMeType.findViewById(R.id.btn_stop);

                recordButton.setText("R");
                playButton.setText("P");
                stopButton.setText("S");

                if(notificationTypeValue == "Notification") {
                    typeNotification.setTextColor(Color.parseColor("#9568ff"));
                }
                else if (notificationTypeValue == "Voice") {
                    typeVoice.setTextColor(Color.parseColor("#9568ff"));
                }
                remindMeType.show();

                typeNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notificationTypeValue = "Notification";
                        remindMeType.dismiss();
                        tv_reminderType.setText("Notification Reminder");
                    }
                });
                typeVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notificationTypeValue = "Voice";
                        typeVoice.setTextColor(Color.parseColor("#9568ff"));
                        typeNotification.setTextColor(Color.parseColor("#000000"));
                       // remindMeType.dismiss();
                        voiceRecordButtons.setVisibility(View.VISIBLE);
                        tv_reminderType.setText("Voice Reminder");

                        if(!hasMicrophone()) {
                            stopButton.setEnabled(false);
                            playButton.setEnabled(false);
                            recordButton.setEnabled(false);
                        }

                        else {
                            playButton.setEnabled(false);
                            stopButton.setEnabled(false);
                        }

//                        audioFilePath =
//                                Environment.getExternalStorageDirectory().getAbsolutePath()
//                                        + "/myaudio.3gp";

                        audioFilePath = getFilesDir()+ "/" + String.valueOf(reminderEvent) + String.valueOf(selectedMinuteAlarm) + String.valueOf(selectedHourAlarm) + "audio.m4a" ;
                        //Log.d("Send audio file path", audioFilePath);


                        recordButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isRecording = true;
                                stopButton.setEnabled(true);
                                playButton.setEnabled(false);
                                recordButton.setEnabled(false);
                                try {
                                    mediaRecorder = new MediaRecorder();
                                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                                    mediaRecorder.setOutputFile(audioFilePath);
                                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                                    mediaRecorder.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ReminderSetActivity.this, "Go to app permission in Locationalarm and allow microphone permission", Toast.LENGTH_LONG).show();
                                }
                                mediaRecorder.start();
                            }
                        });

                        playButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                playButton.setEnabled(false);
                                recordButton.setEnabled(false);
                                stopButton.setEnabled(true);

                                mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(audioFilePath);
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ReminderSetActivity.this, "Go to app permission in Locationalarm and allow microphone permission", Toast.LENGTH_LONG).show();
                                }
                               mediaPlayer.start();
                            }
                        });

                        stopButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stopButton.setEnabled(false);
                                playButton.setEnabled(true);

                                if(isRecording) {
                                    recordButton.setEnabled(false);
                                    mediaRecorder.stop();
                                    mediaRecorder.release();
                                    mediaRecorder = null;
                                    isRecording = false;
                                }
                                else {
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                    recordButton.setEnabled(true);
                                }
                            }
                        });

                    }
                });
            }
        });



        /*************************/

        //Save the reminder and go back to landing page
        ImageView saveTask = (ImageView) findViewById(R.id.saveIcon1);
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((selectedMinuteAlarm - remindMeBeforeTimeValueInInt) < 0 )
                {
                    counterMinitueValue =  remindMeBeforeTimeValueInInt - selectedMinuteAlarm;
                    selectedMinuteAlarm = 60 - counterMinitueValue;
                    myCalender.set(Calendar.MINUTE, selectedMinuteAlarm );

                    if (selectedHourAlarm != 0) {
                        selectedHourAlarm = selectedHourAlarm - 1;
                    }
                    else {
                        selectedHourAlarm = 23;
                        selectedDayAlarm = selectedDayAlarm - 1;
                    }
                    myCalender.set(Calendar.HOUR_OF_DAY, selectedHourAlarm);


                    myCalender.set(Calendar.DAY_OF_MONTH, selectedDayAlarm);
                    myCalender.set(Calendar.MONTH, selectedMonthAlarm);
                    myCalender.set(Calendar.YEAR, selectedYearAlarm);

                }

                else {
                    myCalender.set(Calendar.HOUR_OF_DAY, selectedHourAlarm);
                    selectedMinuteAlarm = selectedMinuteAlarm - remindMeBeforeTimeValueInInt;
                    myCalender.set(Calendar.MINUTE, selectedMinuteAlarm );
                    myCalender.set(Calendar.DAY_OF_MONTH, selectedDayAlarm);
                    myCalender.set(Calendar.MONTH, selectedMonthAlarm);
                    myCalender.set(Calendar.YEAR, selectedYearAlarm);
                }
//                Toast.makeText(ReminderSetActivity.this,"Alarm Set At " + String.valueOf(selectedHourAlarm) + ":" +
//                        String.valueOf(selectedMinuteAlarm) + ":" + String.valueOf(selectedDayAlarm)+ ":" + String.valueOf(selectedMonthAlarm + 1)+ ":" + String.valueOf(selectedYearAlarm),  Toast.LENGTH_LONG).show();

                Toast.makeText(ReminderSetActivity.this,"Alarm Set At " +
                        String.valueOf(selectedHourAlarm) + ":" +
                        String.valueOf(pad(selectedMinuteAlarm)) + " On " +
                        String.valueOf(selectedDayAlarm)+ "/" +
                        String.valueOf(selectedMonthAlarm + 1)+ "/" +
                        String.valueOf(selectedYearAlarm),  Toast.LENGTH_LONG).show();


                pendingIntentRequestCode = selectedHourAlarm + selectedMinuteAlarm + selectedDayAlarm + selectedMonthAlarm + selectedYearAlarm;

                //Toast.makeText(ReminderSetActivity.this,String.valueOf(pendingIntentRequestCode),Toast.LENGTH_LONG).show();

                alarmIntent = new Intent(ReminderSetActivity.this, AlarmReceiver.class);

                alarmIntent.putExtra("reminderEvent", reminderEvent);
                alarmIntent.putExtra("allDayFlag", allDayFlag);
                alarmIntent.putExtra("repeatAlarmIntervalValue", repeatAlarmIntervalValue);
                alarmIntent.putExtra("pendingIntentRequestCode", pendingIntentRequestCode);
                alarmIntent.putExtra("notificationTypeValue", notificationTypeValue);
                if (notificationTypeValue.equals("Voice")) {
                    alarmIntent.putExtra("audioFilePath", audioFilePath);
                }

                alarmPendingIntent = PendingIntent.getBroadcast(ReminderSetActivity.this, pendingIntentRequestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                if(repeatAlarmIntervalValue == "Does not repeat") {
                    //alarmManager.set(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis() , alarmPendingIntent);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), 1000 * 60 * 5, alarmPendingIntent);
                }
                else if (repeatAlarmIntervalValue == "everyDay") {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), AlarmManager.INTERVAL_DAY , alarmPendingIntent);
                }
                else if (repeatAlarmIntervalValue == "everyWeek") {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), 1000 * 60 * 60 * 24 * 7 , alarmPendingIntent);
                }
                else if (repeatAlarmIntervalValue == "everyMonth"){
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), 1000 * 60 * 60 * 24 * 30 , alarmPendingIntent);
                }
                else if (repeatAlarmIntervalValue == "everyYear"){
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), 1000 * 60 * 60 * 24 * 365 , alarmPendingIntent);
                }

                Intent intent = new Intent(ReminderSetActivity.this, HomeActivity.class);

                startActivity(intent);
            }
        });

    }

    //when the alarm started ringing open a view
//    public void openAlarmView() {
//        Intent alarmringingIntent = new Intent(ReminderSetActivity.this, AlarmRingingActivity.class);
//        startActivity(alarmringingIntent);
//    }


    public void snoozeAlarmControl(int receivedPendingIntentRequestCodeSnooze) {
//        alarmManager.set(AlarmManager.RTC, myCalender.getTimeInMillis()+ 600000, alarmPendingIntent );
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis(), 1000 * 60 * 10, alarmPendingIntent);

//        PendingIntent senderSnoozeIntent = PendingIntent.getBroadcast(ReminderSetActivity.this, receivedPendingIntentRequestCodeSnooze,
//                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        alarmManager.set(AlarmManager.RTC, myCalender.getTimeInMillis()+ 1000 * 60 * 10, senderSnoozeIntent );
        Toast.makeText(ReminderSetActivity.this,"Alarm is postponed for 10 minutes",Toast.LENGTH_LONG).show();

    }

    public void cancelAlarmControl(int receivedPendingIntentRequestCode) {

        //Toast.makeText(ReminderSetActivity.this,"Out Block", Toast.LENGTH_LONG).show();

        if (alarmManager != null) {

            //Toast.makeText(ReminderSetActivity.this,String.valueOf(receivedPendingIntentRequestCode),Toast.LENGTH_LONG).show();

            PendingIntent sender = PendingIntent.getBroadcast(ReminderSetActivity.this, receivedPendingIntentRequestCode,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Toast.makeText(ReminderSetActivity.this,"In Block", Toast.LENGTH_LONG).show();

            alarmManager.cancel(sender);

//            Log.d("repeatAlarm-->", getrepeatAlarmIntervalValue);
//
//            if (getrepeatAlarmIntervalValue.equals("everyMonth")) {
//
//                Log.d("repeatAlarm-->after", getrepeatAlarmIntervalValue);
//
//                int currentMonth = myCalender.get(Calendar.MONTH);
//                Log.d("previous month",String.valueOf(currentMonth));
//                currentMonth++;
//
//                if (currentMonth > Calendar.DECEMBER) {
//                    currentMonth = Calendar.JANUARY;
//                    myCalender.set(Calendar.YEAR, myCalender.get(Calendar.YEAR) + 1);
//                }
//                myCalender.set(Calendar.MONTH, currentMonth);
//                Log.d("currentMonth", String.valueOf(currentMonth));
//
//                int maximumDay = myCalender.getActualMaximum(Calendar.DAY_OF_MONTH);
//                Log.d("maximumDay", String.valueOf(maximumDay));
//
//                if( maximumDay < selectedDayAlarm) {
//                    myCalender.set(Calendar.DAY_OF_MONTH, maximumDay);
//                }
//                //Log.d("Then time:", Long.toString(thenTime));
//
//                Toast.makeText(ReminderSetActivity.this,"Alarm Set At " + String.valueOf(selectedHourAlarm) + ":" +
//                        String.valueOf(selectedMinuteAlarm) + ":" + String.valueOf(selectedDayAlarm)+ ":"
//                        + String.valueOf(currentMonth + 1)+ ":" + String.valueOf(selectedYearAlarm),
//                        Toast.LENGTH_LONG).show();
//
//                alarmManager.set(AlarmManager.RTC_WAKEUP, myCalender.getTimeInMillis() , alarmPendingIntent);
//            }
            //alarmManager.cancel(alarmPendingIntent);

        }
    }
    //for Date set
    private void updateLabel() {

        String myFormat = "EEE, MMM d, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        TextView datePicked = (TextView) findViewById(R.id.datePicker);
        datePicked.setText(sdf.format(myCalender.getTime()));
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }


    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}
