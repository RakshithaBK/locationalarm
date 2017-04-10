package com.trianz.locationalarm;

import android.app.AlarmManager;
import android.content.ClipData;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Dibyojyoti.Majumder on 12-01-2017.
 */

public class AlarmRingingForOthers extends AppCompatActivity {

    int pendingIntentRequestCode;
    Ringtone ringtone;
    Calendar myCalender = Calendar.getInstance();
    AlarmManager alarmManager;
    SetReminderSentByOthers inst = SetReminderSentByOthers.instance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmringing);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Toast.makeText(AlarmRingingForOthers.this, "inside alarm others", Toast.LENGTH_SHORT).show();

        Bundle bundle = getIntent().getExtras();
        String reminderEvent = bundle.getString("reminderEvent");
        TextView reminderEventText = (TextView) findViewById(R.id.reminderEvent);
        reminderEventText.setText(reminderEvent);

        pendingIntentRequestCode = bundle.getInt("pendingIntentRequestCode");


        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);

        rippleBackground.startRippleAnimation();

        findViewById(R.id.alarmvibrate).setOnTouchListener(new MyTouchListener());

        findViewById(R.id.imageView1).setOnDragListener(new MyDragListener());
        findViewById(R.id.imageView3).setOnDragListener(new MyDragListener());

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(AlarmRingingForOthers.this, alarmUri);
        ringtone.play();


        TextView tv_currentTime = (TextView) findViewById(R.id.tv_currentTime);
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm ");
        String currentTimeOnRinging = currentTimeFormat.format(myCalender.getTime());
        Log.d("currentTime", currentTimeOnRinging);
        tv_currentTime.setText(currentTimeOnRinging);
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


                    if (((ImageView) v).equals(findViewById(R.id.imageView1))) {

                        ringtone.stop();

                        Toast.makeText(AlarmRingingForOthers.this, "Alarm is postponed for 5 minutes", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AlarmRingingForOthers.this, HomeActivity.class);
                        startActivity(intent);
                    } else if (((ImageView) v).equals(findViewById(R.id.imageView3))) {
                        ringtone.stop();
                        inst.cancelAlarmControl(pendingIntentRequestCode);

                        /****NewD***/

                        /********/
                        Intent intent = new Intent(AlarmRingingForOthers.this, HomeActivity.class);
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

}
