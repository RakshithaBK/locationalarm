package com.trianz.locationalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //close Button in navigation
        final NavigationView  nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        View headerView = getLayoutInflater().inflate(R.layout.nav_header_home, nvDrawer, false);
        nvDrawer.addHeaderView(headerView);
        ImageView img = (ImageView) headerView.findViewById(R.id.nav_close);

        img.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.LEFT);
            }});

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton  wakeupfab = (FloatingActionButton) findViewById(R.id.fab_wakeup_alarm);
        FloatingActionButton  addreminderfab = (FloatingActionButton) findViewById(R.id.fab_add_reminder);
        FloatingActionButton  remindothersfab = (FloatingActionButton) findViewById(R.id.fab_remind_others);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_addreminder:

                        break;
                    case R.id.nav_remindOthers:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;
                    case R.id.nav_wakeUpAlarm:
                        Intent alarmRing = new Intent(HomeActivity.this, AlarmRingingActivity.class);
                        startActivity(alarmRing);
                        break;
                    case R.id.nav_myReminders:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;
                    case R.id.nav_offers:
                        Intent i = new Intent(HomeActivity.this, OffersActivity.class);
                        startActivity(i);
                        break;
                }
                return false;
            }
        });



        //Fab actions
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
       frameLayout.getBackground().setAlpha(240);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);

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
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(null);
            }
        });



        wakeupfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Done", Toast.LENGTH_SHORT).show();
                //Intent to navigate to next page
            }
        });

        addreminderfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent to navigate to next page
            }
        });


        remindothersfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Tapped", Toast.LENGTH_SHORT).show();
               //Intent to navigate to next page
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addreminder) {
            // Handle the camera action
        } else if (id == R.id.nav_remindOthers) {

        } else if (id == R.id.nav_wakeUpAlarm) {

        } else if (id == R.id.nav_myReminders) {

        } else if (id == R.id.nav_offers) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
