package com.trianz.locationalarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class AddReminderToDateActivity extends AppCompatActivity {

    String selectedDate = null;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder_to_date);

        selectedDate = getIntent().getStringExtra("reminder_Date");


        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        this.getSupportActionBar().setTitle(R.string.remind_me_to);

        message = (EditText) findViewById(R.id.reminder_msg_date);
        message.requestFocus();
    }
}
