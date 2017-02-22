package com.trianz.locationalarm;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trianz.locationalarm.Utils.ReminderSetController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.trianz.locationalarm.Utils.Constants.Instances.KEY_ALLDAYFLAG;
import static com.trianz.locationalarm.Utils.Constants.Instances.KEY_DAY;
import static com.trianz.locationalarm.Utils.Constants.Instances.KEY_HOUR;
import static com.trianz.locationalarm.Utils.Constants.Instances.KEY_MINUTE;
import static com.trianz.locationalarm.Utils.Constants.Instances.KEY_MONTH;
import static com.trianz.locationalarm.Utils.Constants.Instances.KEY_PHONENUMBER;
import static com.trianz.locationalarm.Utils.Constants.Instances.KEY_REPEATALARMVALUE;
import static com.trianz.locationalarm.Utils.Constants.Instances.KEY_YEAR;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedDayAlarm;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedHourAlarm;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedMinuteAlarm;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedMonthAlarm;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedYearAlarm;


/**
 * Created by Dibyojyoti.Majumder on 15-02-2017.
 */

public class ReminderSetToOthers extends AppCompatActivity {

    Calendar myCalender = Calendar.getInstance();
    String allDayFlag = "false";

    String repeatAlarmIntervalValue = "Does not repeat";

    String reminderEvent;
    TextView selectContactNumber;
    String receiverNumber;

    //for post req
    private static final String REMIND_TO_OTHERS_URL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setreminder_others);

        this.getSupportActionBar().hide();

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

        //some new stuff
        selectContactNumber = (TextView) findViewById(R.id.selectContactNumber);

        selectContactNumber.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);

            }
        });




        //new stuff ends here

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


        //DatePicker
        datePicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DatePickerDialog dialog = new DatePickerDialog(ReminderSetToOthers.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        Toast.makeText(ReminderSetToOthers.this, ""+arg1+"/"+(arg2+1)+"/"+arg3, Toast.LENGTH_SHORT).show();
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
                mTimePicker = new TimePickerDialog(ReminderSetToOthers.this, new TimePickerDialog.OnTimeSetListener() {
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

                Intent intent = new Intent(ReminderSetToOthers.this, RemindMeTask.class);
                startActivity(intent);
            }
        });




        //set the switch for allDay to off and get the status on change
        enableAllDay.setChecked(false);
        enableAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    allDayFlag = "true";
                    timePicked.setVisibility(View.INVISIBLE);

                }else {
                    allDayFlag = "false";
                    timePicked.setVisibility(View.VISIBLE);
                }

            }
        });


        ReminderSetController.repetationSetup(this);
        /*************************/

        //Save the reminder and go back to landing page
        ImageView saveTask = (ImageView) findViewById(R.id.saveIcon1);
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (receiverNumber == null) {
                    Toast.makeText(ReminderSetToOthers.this,"Please enter a number you want to send your reminder",Toast.LENGTH_LONG).show();
                }
                else {

                    Toast.makeText(ReminderSetToOthers.this,String.valueOf(selectedHourAlarm) + ":" +
                                    String.valueOf(pad(selectedMinuteAlarm)) + " On " +
                                    String.valueOf(selectedDayAlarm)+ "/" +
                                    String.valueOf(selectedMonthAlarm + 1)+ "/" +
                                    String.valueOf(selectedYearAlarm)+ "  "+
                                    repeatAlarmIntervalValue + "   "+
                                    allDayFlag + "  "  +
                                    receiverNumber
                            ,Toast.LENGTH_SHORT).show();

                    sendReminderDetailsToBackend();

//                Intent intent = new Intent(ReminderSetToOthers.this, HomeActivity.class);
//
//                startActivity(intent);

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor =  managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                receiverNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                selectContactNumber.setText(receiverNumber);
            }

    }

    private void sendReminderDetailsToBackend() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REMIND_TO_OTHERS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ReminderSetToOthers.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReminderSetToOthers.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_HOUR, String.valueOf(selectedHourAlarm));
                params.put(KEY_MINUTE, String.valueOf(selectedMinuteAlarm));
                params.put(KEY_DAY, String.valueOf(selectedDayAlarm));
                params.put(KEY_MONTH, String.valueOf(selectedMonthAlarm));
                params.put(KEY_YEAR, String.valueOf(selectedYearAlarm));
                params.put(KEY_ALLDAYFLAG, allDayFlag);
                params.put(KEY_PHONENUMBER, receiverNumber);
                params.put(KEY_REPEATALARMVALUE, repeatAlarmIntervalValue);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}
