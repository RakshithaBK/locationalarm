package com.trianz.locationalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.trianz.locationalarm.Utils.GeofenceController;
import com.trianz.locationalarm.Utils.NamedGeofence;

import static android.R.attr.radius;

public class AddReminderToDateActivity extends AppCompatActivity {

    private String reminder_message, Date_To_remid;
    String selectedDate = null;
    EditText message;
    String currentDate;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            reminder_message = message.getText().toString();
            Date_To_remid = String.valueOf(currentDate);

            if (reminder_message.equals("")) {
                Snackbar.make(getWindow().getDecorView(), "Set a reminder message.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {

                if (dataIsValid()) {

                    NamedGeofence geofence = new NamedGeofence();
                    geofence.reminder_msg = reminder_message;
                    geofence.reminder_Date = Date_To_remid;
                    geofence.radius = radius * 1.0f;
                    GeofenceController.getInstance().addGeofence(geofence, geofenceControllerListener);


                } else {
                    showValidationErrorToast();
                }

            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean dataIsValid() {
        boolean validData = true;

        String reminderString = reminder_message;

        if (TextUtils.isEmpty(reminderString)) {
            validData = false;
        } else {

            validData = true;
        }

        return validData;
    }

    private void showValidationErrorToast() {
        Toast.makeText(AddReminderToDateActivity.this, AddReminderToDateActivity.this.getString(R.string.Toast_Validation), Toast.LENGTH_SHORT).show();
    }

    private GeofenceController.GeofenceControllerListener geofenceControllerListener = new GeofenceController.GeofenceControllerListener() {
        @Override
        public void onGeofencesUpdated() {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            AddReminderToDateActivity.this.finish();
        }

        @Override
        public void onError() {
            Toast.makeText(AddReminderToDateActivity.this, AddReminderToDateActivity.this.getString(R.string.Toast_error), Toast.LENGTH_SHORT).show();
        }


    };
}
