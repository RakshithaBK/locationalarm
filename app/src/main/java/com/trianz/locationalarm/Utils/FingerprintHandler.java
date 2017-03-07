package com.trianz.locationalarm.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.trianz.locationalarm.HomeActivity;

import static android.content.Context.MODE_PRIVATE;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends
        FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context appContext;

    public FingerprintHandler(Context context) {
        appContext = context;
    }

    public void startAuth(FingerprintManager manager,
                          FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        Toast.makeText(appContext,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Toast.makeText(appContext,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(appContext,
                "Authentication failed.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        SharedPreferences prefs = appContext.getSharedPreferences(HomeActivity.MY_PREFS_USERNAME, MODE_PRIVATE);
        String UserName = prefs.getString("UserName","No UserName Defined");

        Log.d("Username in ", UserName);
        if(UserName == null || UserName.equals("No UserName Defined")){
            Toast.makeText(appContext, "Please register before using Fingerprint", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(appContext,
                    "Authentication succeeded.",
                    Toast.LENGTH_LONG).show();

            appContext.startActivity(new Intent(appContext,
                    HomeActivity.class));
        }

    }

}