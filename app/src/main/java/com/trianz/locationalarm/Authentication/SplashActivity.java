package com.trianz.locationalarm.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.trianz.locationalarm.HomeActivity;
import com.trianz.locationalarm.R;
import com.trianz.locationalarm.Utils.SaveSharedPreferences;


public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String isUserLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
// SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//         isUserLogged = prefs.getString("UserLoggedIn","no user logged");
//        Log.d("isLogged",isUserLogged.toString());
//                if(isUserLogged.equals("true")) {
//                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
//                    startActivity(i);
//
//                } else {

                if(SaveSharedPreferences.getUserName(SplashActivity.this).length() == 0)
                {
                    Intent i = new Intent(SplashActivity.this, AuthenticationActivity.class);
                    startActivity(i);
                }
                else
                {
                    // Stay at the current activity.
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}


