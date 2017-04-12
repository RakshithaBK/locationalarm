package com.trianz.locationalarm.Controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import static com.trianz.locationalarm.Utils.Constants.Geometry.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.trianz.locationalarm.Utils.Constants.Geometry.MY_PERMISSIONS_REQUEST_RECORD;
import static com.trianz.locationalarm.Utils.Constants.Geometry.MY_PERMISSIONS_REQUEST_SENDSMS;

/**
 * Created by Rakshitha.Krishnayya on 12-04-2017.
 */

public class PermissionsCheckController {

    public static boolean checkLocationPermission(AppCompatActivity appCompatActivity) {
        if (ContextCompat.checkSelfPermission(appCompatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat
                .checkSelfPermission(appCompatActivity,
                        Manifest.permission.READ_SMS) + ContextCompat
                .checkSelfPermission(appCompatActivity,
                        Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (appCompatActivity, Manifest.permission.READ_SMS) || ActivityCompat.shouldShowRequestPermissionRationale
                    (appCompatActivity, Manifest.permission.READ_CONTACTS)) {

                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission
                                .ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            //Call whatever you want
            return true;
        }
    }

    public static boolean checkMicroPhonePermission(AppCompatActivity appCompatActivity) {
        if (ContextCompat.checkSelfPermission(appCompatActivity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (appCompatActivity, android.Manifest.permission.RECORD_AUDIO)) {

                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD);
            } else {
                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission
                                .RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD);
            }
            return false;
        } else {
            //Call whatever you want
            return true;
        }
    }

    public static boolean checkSendSMSPermission(AppCompatActivity appCompatActivity){
        if (ContextCompat.checkSelfPermission(appCompatActivity,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (appCompatActivity, android.Manifest.permission.SEND_SMS)) {

                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SENDSMS);
            } else {
                ActivityCompat.requestPermissions(appCompatActivity,
                        new String[]{Manifest.permission
                                .SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SENDSMS);
            }
            return false;
        } else {
            //Call whatever you want
            return true;
        }
    }
}
