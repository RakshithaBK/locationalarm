package com.trianz.locationalarm.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.trianz.locationalarm.AddReminderActivity;
import com.trianz.locationalarm.OffersCoupons.OffersActivity;
import com.trianz.locationalarm.R;
import com.trianz.locationalarm.RemindMeTask;

import static com.trianz.locationalarm.HomeActivity.isLocationEnabled;
import static com.trianz.locationalarm.Utils.Constants.Geometry.SET_REMINDER_REQUEST;
import static com.trianz.locationalarm.Utils.Constants.Instances.frameLayout;
import static com.trianz.locationalarm.Utils.Constants.Instances.isDateSelected;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedDate;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedPlace;
import static com.trianz.locationalarm.Utils.Constants.Instances.selfReminderFlag;
import static com.trianz.locationalarm.Utils.Constants.Instances.toolbar;

/**
 * Created by Rakshitha.Krishnayya on 23-02-2017.
 */

public class HomeController {

    public static void NavigationDrawerSetup(final AppCompatActivity appCompatActivity,DrawerLayout drawer,NavigationView navigationView,Button signOut){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                appCompatActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_addreminder:
                        if(!isLocationEnabled(appCompatActivity)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
                            builder.setMessage(R.string.turn_on_gps)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    }).setCancelable(false)
                                    .create()
                                    .show();
                        }
                        if(selectedPlace == null)
                        {
                            Toast.makeText(appCompatActivity, "Select a reminder Location", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent addReminders = new Intent(appCompatActivity, AddReminderActivity.class);
                            addReminders.putExtra("reminder_place", (Parcelable) selectedPlace);
                            appCompatActivity.startActivity(addReminders);
                        }
                        break;
                    case R.id.nav_remindOthers:
                        selfReminderFlag = false;
                        Intent remindToOthers = new Intent(appCompatActivity, RemindMeTask.class);
                        appCompatActivity.startActivity(remindToOthers);
                        break;
                    case R.id.nav_wakeUpAlarm:
                        selfReminderFlag = true;
                        if(selectedDate == null){
                            Toast.makeText(appCompatActivity, "Select a reminder Date", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent wakeUp = new Intent(appCompatActivity, RemindMeTask.class);
                            appCompatActivity.startActivity(wakeUp);
                        }
                        break;
                    case R.id.nav_offers:
                        Intent offers = new Intent(appCompatActivity, OffersActivity.class);
                        appCompatActivity.startActivity(offers);
                        break;
                }
                return false;
            }
        });
//
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                logoutUser(appCompatActivity);
////                SharedPreferences preferences = appCompatActivity.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
////                SharedPreferences.Editor editor = preferences.edit();
////                editor.remove("AccessToken");
////                editor.remove("UserName");
////                editor.remove("Email");
////                editor.clear();
////                editor.commit();
//            }
//        });
    }



    public static void FloatActionBtnSetup(final AppCompatActivity appCompatActivity, FloatingActionButton wakeupfab, FloatingActionButton addReminderLocationfab,
                                            FloatingActionButton remindothersfab, final FloatingActionsMenu fabMenu){





        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(150);

                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });



        addReminderLocationfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isLocationEnabled(appCompatActivity)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
                    builder.setMessage(R.string.turn_on_gps)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).setCancelable(false)
                            .create()
                            .show();
                }

                if(selectedPlace == null)
                {
                    Snackbar.make(view, "Select a reminder location", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fabMenu.collapse();
                }

                else {
                    Intent addReminderActivity = new Intent(appCompatActivity, AddReminderActivity.class);
                    addReminderActivity.putExtra("reminder_place", (Parcelable) selectedPlace);
                    appCompatActivity.startActivityForResult(addReminderActivity, SET_REMINDER_REQUEST);
                    fabMenu.collapse();
                }
            }
        });



        remindothersfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfReminderFlag = false;
                Intent reminderToOthers = new Intent(appCompatActivity, RemindMeTask.class);
                reminderToOthers.putExtra("reminder_Date", selectedDate);
                appCompatActivity.startActivityForResult(reminderToOthers, SET_REMINDER_REQUEST);
                fabMenu.collapse();
                //Intent to navigate to next page
            }
        });

        wakeupfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedDate == null){
                    Snackbar.make(view, "Select a reminder date", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fabMenu.collapse();
                }else{
                    selfReminderFlag = true;
                    Intent addReminderToDateActivity = new Intent(appCompatActivity, RemindMeTask.class);
                    addReminderToDateActivity.putExtra("reminderDate", selectedDate);
                    appCompatActivity.startActivityForResult(addReminderToDateActivity, SET_REMINDER_REQUEST);
                    fabMenu.collapse();
                }
}
        });

    }


    public static void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d("AndroidBash", "Subscribed");
        String token = FirebaseInstanceId.getInstance().getToken();
        // Log and toast
        //   Log.d("AndroidBash", token);
        //   Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    public  static void switchCalenderToMapSetUp(final AppCompatActivity appCompatActivity, ImageView calenderImg, final FrameLayout search_place, final ViewGroup content_calender){

        calenderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDateSelected){
                    content_calender.setVisibility(View.VISIBLE);
                    search_place.setVisibility(View.INVISIBLE);
                    appCompatActivity.getSupportActionBar().setTitle("");
                    appCompatActivity.getSupportActionBar().setSubtitle("");
                    isDateSelected = true;
                }else{
                    content_calender.setVisibility(View.INVISIBLE);
                    search_place.setVisibility(View.VISIBLE);
                    appCompatActivity.getSupportActionBar().setTitle("");
                    isDateSelected= false;

                }
            }
        });
    }

    public static void errorInResponse(Response.ErrorListener appCompatActivity, VolleyError volleyError){
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        Toast.makeText((Context) appCompatActivity, message, Toast.LENGTH_SHORT).show();
    }



}
