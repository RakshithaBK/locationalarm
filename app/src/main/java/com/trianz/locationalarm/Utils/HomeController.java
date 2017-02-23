package com.trianz.locationalarm.Utils;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.trianz.locationalarm.AddReminderActivity;
import com.trianz.locationalarm.AddReminderToDateActivity;
import com.trianz.locationalarm.AuthenticationActivity;
import com.trianz.locationalarm.HomeActivity;
import com.trianz.locationalarm.OffersActivity;
import com.trianz.locationalarm.R;
import com.trianz.locationalarm.RemindMeTask;

import static com.trianz.locationalarm.Utils.Constants.Geometry.SET_REMINDER_REQUEST;
import static com.trianz.locationalarm.Utils.Constants.Instances.frameLayout;
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
                        Intent addReminders = new Intent(appCompatActivity, HomeActivity.class);
                        appCompatActivity.startActivity(addReminders);
                        break;
                    case R.id.nav_remindOthers:
                        selfReminderFlag = false;
                        Intent remindToOthers = new Intent(appCompatActivity, RemindMeTask.class);
                        appCompatActivity.startActivity(remindToOthers);
                        break;
                    case R.id.nav_wakeUpAlarm:
                        selfReminderFlag = true;
                        Intent wakeUp = new Intent(appCompatActivity, RemindMeTask.class);
                        appCompatActivity.startActivity(wakeUp);
                        break;
                    case R.id.nav_myReminders:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;
                    case R.id.nav_offers:
                        Intent offers = new Intent(appCompatActivity, OffersActivity.class);
                        appCompatActivity.startActivity(offers);
                        break;
                }
                return false;
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginAction = new Intent(appCompatActivity,AuthenticationActivity.class);
                appCompatActivity.startActivity(LoginAction);
            }
        });
    }

    public static void FloatActionBtnSetup(final AppCompatActivity appCompatActivity, FloatingActionButton wakeupfab, FloatingActionButton addReminderLocationfab,
                                           FloatingActionButton addReminderDatefab, FloatingActionButton remindothersfab, final FloatingActionsMenu fabMenu){





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

        addReminderDatefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedDate == null){
                    Snackbar.make(view, "Select a reminder date", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fabMenu.collapse();
                }else{

                    Intent addReminderToDateActivity = new Intent(appCompatActivity, AddReminderToDateActivity.class);
                    addReminderToDateActivity.putExtra("reminder_Date", selectedDate);
                    appCompatActivity.startActivityForResult(addReminderToDateActivity, SET_REMINDER_REQUEST);
                    fabMenu.collapse();
                }
            }
        });


        remindothersfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfReminderFlag = false;
                Intent addReminderToDateActivity = new Intent(appCompatActivity, RemindMeTask.class);
                addReminderToDateActivity.putExtra("reminder_Date", selectedDate);
                appCompatActivity.startActivityForResult(addReminderToDateActivity, SET_REMINDER_REQUEST);
                fabMenu.collapse();
                //Intent to navigate to next page
            }
        });

        wakeupfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfReminderFlag = true;
                Intent remindMeTask =  new Intent(appCompatActivity, RemindMeTask.class);
                appCompatActivity.startActivity(remindMeTask);
                fabMenu.collapse();
            }
        });

    }
}
