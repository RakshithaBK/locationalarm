package com.trianz.locationalarm;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.trianz.locationalarm.Utils.Constants.SharedPrefs.MY_PREFS_NAME;
import static com.trianz.locationalarm.Utils.Constants.SharedPrefs.repeatAlarmIntervalValue;

/**
 * Created by Dibyojyoti.Majumder on 12-01-2017.
 */

public class AlarmRingingActivity extends AppCompatActivity {

    int pendingIntentRequestCode;
    MediaPlayer mediaPlayer;
    String notificationTypeValue;
    String audioFilePath;
    Ringtone ringtone;
    Calendar myCalender = Calendar.getInstance();
    ReminderSetActivity inst = ReminderSetActivity.instance();
    private ImageView mPendulum;

    private Animation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmringing);

        Bundle bundle = getIntent().getExtras();
        String reminderEvent = bundle.getString("reminderEvent");


        TextView reminderEventText = (TextView) findViewById(R.id.reminderEvent);
        reminderEventText.setText(reminderEvent);

        mPendulum = (ImageView) findViewById(R.id.pendulum_ringing);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.pendulum);

        repeatAlarmIntervalValue = bundle.getString("repeatAlarmIntervalValue");
        pendingIntentRequestCode = bundle.getInt("pendingIntentRequestCode");
        notificationTypeValue = bundle.getString("notificationTypeValue");

        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);

        rippleBackground.startRippleAnimation();

        findViewById(R.id.alarmvibrate).setOnTouchListener(new MyTouchListener());

        findViewById(R.id.imageView1).setOnDragListener(new MyDragListener());
        findViewById(R.id.imageView3).setOnDragListener(new MyDragListener());

        if (notificationTypeValue.equals("Notification")) {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            ringtone = RingtoneManager.getRingtone(AlarmRingingActivity.this, alarmUri);
            ringtone.play();
        } else {
            audioFilePath = bundle.getString("audioFilePath");

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioFilePath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }

        TextView tv_currentTime = (TextView) findViewById(R.id.tv_currentTime);
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm ");
        String currentTimeOnRinging = currentTimeFormat.format(myCalender.getTime());
        Log.d("currentTime", currentTimeOnRinging);
        tv_currentTime.setText(currentTimeOnRinging);

        SimpleDateFormat currentHourFormat = new SimpleDateFormat("HH");
        int currentHour = Integer.parseInt(currentHourFormat.format(myCalender.getTime()));
        TextView tv_greetings = (TextView) findViewById(R.id.greetings);
        if (currentHour > 5 && currentHour <= 12) {
            tv_greetings.setText("Good Morning");
        } else if (currentHour > 12 && currentHour < 17) {
            tv_greetings.setText("Good Afternoon");
        } else {
            tv_greetings.setText("Good Evening");
        }

        TextView alarm_User_Name = (TextView) findViewById(R.id.alarm_User_Name);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String UserName = prefs.getString("UserName", "No UserName Defined");
        alarm_User_Name.setText(UserName);
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    public class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            View view = (View) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:

                    break;
                case DragEvent.ACTION_DRAG_EXITED:

                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup

                    if (v.equals(findViewById(R.id.imageView1))) {
                        Toast.makeText(AlarmRingingActivity.this, "Snooze", Toast.LENGTH_SHORT).show();
                        if (notificationTypeValue.equals("Notification")) {
                            ringtone.stop();
                        }
                        inst.snoozeAlarmControl(pendingIntentRequestCode);
                        Intent intent = new Intent(AlarmRingingActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else if (v.equals(findViewById(R.id.imageView3))) {
                        Toast.makeText(AlarmRingingActivity.this, "Dismiss", Toast.LENGTH_SHORT).show();
                        if (notificationTypeValue.equals("Notification")) {
                            ringtone.stop();
                        }
                        inst.cancelAlarmControl(pendingIntentRequestCode);
                        Intent intent = new Intent(AlarmRingingActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }

                    view.setVisibility(View.VISIBLE);
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    view.setVisibility(View.VISIBLE);
                default:
                    break;
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mPendulum.startAnimation(mAnimation);
    }

}
