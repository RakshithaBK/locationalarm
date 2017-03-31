package com.trianz.locationalarm;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.trianz.locationalarm.Utils.Constants;
import com.trianz.locationalarm.Utils.GeofenceController;
import com.trianz.locationalarm.Utils.NamedGeofence;

import java.util.ArrayList;

import static com.trianz.locationalarm.Utils.Constants.Geometry.RESULT_SPEECH;
import static com.trianz.locationalarm.Utils.Constants.Instances.speechToText;

public class AddReminderActivity extends AppCompatActivity {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    Place place;
    String location_name,reminder_message;
    double location_latitude, location_longitude;
    float radius;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        place = getIntent().getParcelableExtra("reminder_place");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        this.getSupportActionBar().setTitle(R.string.remind_me_to);

        message = (EditText) findViewById(R.id.reminder_msg);
        message.requestFocus();

        if (message.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        speechToText = (ImageView) findViewById(R.id.speech_to_text);
        speechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);

                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Oops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
                location_name = place.getName().toString();
                location_latitude = place.getLatLng().latitude;
                location_longitude = place.getLatLng().longitude;
                reminder_message = message.getText().toString();
                radius = 1;

                if (reminder_message.equals("")) {
                    Snackbar.make(getWindow().getDecorView(), "Set a reminder message.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    if (dataIsValid()) {

                        NamedGeofence geofence = new NamedGeofence();
                        geofence.reminder_msg = reminder_message;
                        geofence.reminder_place = location_name;
                        geofence.latitude = location_latitude;
                        geofence.longitude = location_longitude;
                        geofence.radius = radius * 50.0f;

                        GeofenceController.getInstance().addGeofence(geofence, geofenceControllerListener);

                    } else {
                        showValidationErrorToast();
                    }
                }
                return true;
            }
            return super.onOptionsItemSelected(item);
        }



    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up

        return false;
    }

    private boolean dataIsValid() {
        boolean validData = true;

        String name = location_name;
        String latitudeString = Double.toString(location_latitude);
        String longitudeString = Double.toString(location_longitude);
        String radiusString = Float.toString(radius);
        String reminderString = reminder_message;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(latitudeString)
                || TextUtils.isEmpty(longitudeString) || TextUtils.isEmpty(radiusString) || TextUtils.isEmpty(reminderString)) {
            validData = false;
        } else {
            double latitude = Double.parseDouble(latitudeString);
            double longitude = Double.parseDouble(longitudeString);
            float radius = Float.parseFloat(radiusString);
            if ((latitude < Constants.Geometry.MinLatitude || latitude > Constants.Geometry.MaxLatitude)
                    || (longitude < Constants.Geometry.MinLongitude || longitude > Constants.Geometry.MaxLongitude)
                    || (radius < Constants.Geometry.MinRadius || radius > Constants.Geometry.MaxRadius)) {
                validData = false;
            }
        }

        return validData;
    }

    private void showValidationErrorToast() {
        Toast.makeText(AddReminderActivity.this, AddReminderActivity.this.getString(R.string.Toast_Validation), Toast.LENGTH_SHORT).show();
    }

    // region GeofenceControllerListener

    private GeofenceController.GeofenceControllerListener geofenceControllerListener = new GeofenceController.GeofenceControllerListener() {
        @Override
        public void onGeofencesUpdated() {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            AddReminderActivity.this.finish();
        }

        @Override
        public void onError() {

            showErrorToast();

        }
    };

    private void showErrorToast() {
        Toast.makeText(AddReminderActivity.this, AddReminderActivity.this.getString(R.string.Toast_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //  message.setText(text.get(0));
                    message.setText("");
                    if (!TextUtils.isEmpty(text.get(0))) {
                        message.append(text.get(0));
                    }
                }
                break;
            }

        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AddReminder Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
