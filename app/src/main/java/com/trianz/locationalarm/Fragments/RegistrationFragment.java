package com.trianz.locationalarm.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.trianz.locationalarm.Authentication.ConfirmRegisterActivity;
import com.trianz.locationalarm.Controllers.HomeController;
import com.trianz.locationalarm.Controllers.PermissionsCheckController;
import com.trianz.locationalarm.R;
import com.trianz.locationalarm.Utils.MySingleton;
import com.trianz.locationalarm.Utils.NetworkCallModels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.trianz.locationalarm.Utils.Constants.Geometry.MY_PERMISSIONS_REQUEST_SENDSMS;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_EMAIL;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_MOBILE;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_PASSWORD;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_REG_OTP;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_REG_OTP_SENDER;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_REG_OTP_TOKEN;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_USERNAME;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.REGISTER_URL;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.REG_OTP_VERIFICATION_URL;

public class RegistrationFragment extends Fragment {

    private EditText editTextMobile;
    private EditText editTextEmail;
    private EditText editTextPassword;
    public  EditText editTextUserName;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_registration, container,false);
        editTextMobile = (EditText) rootView.findViewById(R.id.regMobileTxt);
        editTextPassword = (EditText) rootView.findViewById(R.id.regPasswordTxt);
        editTextEmail= (EditText) rootView.findViewById(R.id.regEmailTxt);
        editTextUserName= (EditText) rootView.findViewById(R.id.regUserName);
        Button button= (Button) rootView.findViewById(R.id.regBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    confirmRegistration(v);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionsCheckController.checkSendSMSPermission((AppCompatActivity) getContext());
        }
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SENDSMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Toast.makeText(this, "SMS permision granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void confirmRegistration(View view) {
        final String mobile = editTextMobile.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String userName = editTextUserName.getText().toString().trim();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(KEY_MOBILE, mobile);
        params.put(KEY_PASSWORD,password);
        params.put(KEY_USERNAME,userName);
        params.put(KEY_EMAIL, email);

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, REGISTER_URL ,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       OnResponseValidation(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        String message = null;
                        HomeController.errorInResponse(getContext(),volleyError);
                    }

                });

        MySingleton.getInstance(getActivity()).addToRequestQueue(JsonObjRequest);
    }


    public void OnResponseValidation(JSONObject response){
        try {
            NetworkCallModels models = new NetworkCallModels();
            models.setJson(response);
            models.status = Boolean.parseBoolean(models.getJson().getString("status"));
            models.message = models.getJson().getString("message");

            JSONObject otp_data = new JSONObject(models.getJson().getString("data"));
            final String otp_token = otp_data.getString("otp_token");
            String otp = otp_data.getString("otp");
            final String sender_mobile = otp_data.getString("mobile");

            if(models.status){
                Intent sendOTPIntent = new Intent(getContext(), RegistrationFragment.class);
                PendingIntent sendOTPPendingIntent = PendingIntent.getActivity(getContext(),0,sendOTPIntent,0);

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(sender_mobile , null, "OTP RECEIVED for Location Alarm is: " + otp , sendOTPPendingIntent, null);

                final Dialog regOTPDialog = new Dialog(getContext());
                regOTPDialog.setContentView(R.layout.dialog_inputregotp);
                regOTPDialog.show();

                Toast.makeText(getActivity(),models.message, Toast.LENGTH_SHORT).show();
                final EditText enter_otp = (EditText) regOTPDialog.findViewById(R.id.enterOTP);
                Button verify_otp = (Button) regOTPDialog.findViewById(R.id.OTPverifyBtn);

                verify_otp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String Verify_otp = enter_otp.getText().toString().trim();
                        final String Verify_otp_token = otp_token;
                        final String Verify_otp_sender = sender_mobile;

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(KEY_REG_OTP, Verify_otp);
                        params.put(KEY_REG_OTP_TOKEN,Verify_otp_token);
                        params.put(KEY_REG_OTP_SENDER,Verify_otp_sender);

                        JSONObject jsonBody = new JSONObject(params);
                        JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, REG_OTP_VERIFICATION_URL ,jsonBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                    try{
                                        NetworkCallModels models = new NetworkCallModels();
                                        models.setJson(response);
                                        models.status = Boolean.parseBoolean(models.getJson().getString("status"));
                                        models.message = models.getJson().getString("message");

                                        if(models.status){
                                            Toast.makeText(getActivity(),models.message, Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getContext(),ConfirmRegisterActivity.class));
                                        }else{
                                            Toast.makeText(getActivity(),models.message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        HomeController.errorInResponse(getContext(),volleyError);
                                    }

                                });

                        MySingleton.getInstance(getActivity()).addToRequestQueue(JsonObjRequest);

                    }
                });

            }else{
                Toast.makeText(getActivity(),models.message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}