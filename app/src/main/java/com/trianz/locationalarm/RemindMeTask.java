package com.trianz.locationalarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.trianz.locationalarm.Adapters.CustomAdapter;

import static com.trianz.locationalarm.Utils.Constants.Instances.selfReminderFlag;


/**
 * Created by Dibyojyoti.Majumder on 04-01-2017.
 */

public class RemindMeTask extends AppCompatActivity {

    ListView lv;
    Context context;
    EditText getTask;
    String pickedTask;
    ImageView saveTask;
    ImageView closeTask;
    String reminderDate;


    public static String[] remindMeList = {"Task", "Meet", "Hospital", "Buy", "Call", "Event"};
    public static int[] remindMeImages = {R.drawable.ic_task, R.drawable.ic_meet, R.drawable.ic_hospital, R.drawable.ic_buy, R.drawable.ic_call, R.drawable.ic_event};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remindme_task);


        getTask = (EditText) findViewById(R.id.et_remindmeto);
        getTask.setHintTextColor(getResources().getColor(R.color.color_white));


        context = this;

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new CustomAdapter(this, remindMeList, remindMeImages));

        saveTask = (ImageView) findViewById(R.id.saveIcon);
        saveTask.setEnabled(false);
        saveTask.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

        closeTask = (ImageView) findViewById(R.id.closeIcon);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView pickedCustomTask = (TextView) view.findViewById(R.id.textView1);
                String pickedCustomTaskText = pickedCustomTask.getText().toString();

                getTask.setText(pickedCustomTaskText, TextView.BufferType.EDITABLE);
                int pos = getTask.getText().length();
                getTask.setSelection(pos);


            }
        });

        getTask.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                if (getTask.length() > 0) {
                    saveTask.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    saveTask.setEnabled(true);
                } else {
                    saveTask.setEnabled(false);
                    saveTask.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                }

            }

        });
        //savetask and proceed to next page
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                pickedTask = getTask.getText().toString();

                if (selfReminderFlag) {
                    if (getIntent().hasExtra("reminderDate")) {
                        Bundle bundle = getIntent().getExtras();
                        reminderDate = bundle.getString("reminderDate");
                        Log.d("reminderDate", reminderDate);
                    }
                    intent = new Intent(RemindMeTask.this, ReminderSetActivity.class);
                    intent.putExtra("reminderDate", reminderDate);

                } else {
                    intent = new Intent(RemindMeTask.this, ReminderSetToOthers.class);

                }
                intent.putExtra("reminderEvent", pickedTask);

                startActivity(intent);
            }
        });

        //close task and come back to LANDING page
        closeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Home = new Intent(RemindMeTask.this, HomeActivity.class);
                Home.putExtra("Token_Flag", 0);
                startActivity(Home);
            }
        });
    }

}


