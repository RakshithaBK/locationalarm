package com.trianz.locationalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import static com.trianz.locationalarm.Utils.Constants.Instances.selfReminderFlag;


/**
 * Created by Dibyojyoti.Majumder on 04-01-2017.
 */

public class RemindMeTask extends AppCompatActivity {

    ListView lv;
    Context context;
    EditText getTask;
    String pickedTask;

    public static String[] remindMeList={"Task","Meet","Hospital","Buy","Call","Event"};
    public static int [] remindMeImages={R.mipmap.ic_task,R.mipmap.ic_meet,R.mipmap.ic_hospital,R.mipmap.ic_buy,R.mipmap.ic_call,R.mipmap.ic_event};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remindme_task);

        getTask = (EditText)findViewById(R.id.et_remindmeto);
        getTask.setHintTextColor(getResources().getColor(R.color.white));

        context = this;

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new CustomAdapter(this, remindMeList,remindMeImages));


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




        ImageView saveTask = (ImageView) findViewById(R.id.saveIcon);

        ImageView closeTask = (ImageView) findViewById(R.id.closeIcon);


        //savetask and proceed to next page
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickedTask = getTask.getText().toString();

                int flag = 1;

                if(selfReminderFlag) {
                    Intent intent = new Intent(RemindMeTask.this, ReminderSetActivity.class);
                    intent.putExtra("reminderEvent", pickedTask);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(RemindMeTask.this, ReminderSetToOthers.class);
                    intent.putExtra("reminderEvent", pickedTask);
                    startActivity(intent);
                }
            }
        });

        //close task and come back to LANDING page
        closeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Home = new Intent(RemindMeTask.this, HomeActivity.class);
                startActivity(Home);
            }
        });
    }

}


