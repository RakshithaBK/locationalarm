package com.trianz.locationalarm.Fragments;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.trianz.locationalarm.Controllers.HomeController;
import com.trianz.locationalarm.HomeActivity;
import com.trianz.locationalarm.R;
import com.trianz.locationalarm.Utils.MySingleton;
import com.trianz.locationalarm.Utils.NetworkCallModels;
import com.trianz.locationalarm.Utils.SaveSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.trianz.locationalarm.Utils.Constants.SharedPrefs.MY_PREFS_NAME;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_FORPWD_MOBILE;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_FORPWD_NEW_PASSWORD;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_FORPWD_OTP_TOKEN;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_FORPWD_PWD_OTP;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_MOBILE_LOGIN;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_PASSWORD_LOGIN;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_TOKEN;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.FORGOTPWD_OTP_VERIFICATION_URL;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.FORGOTPWD_URL;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.LOGIN_URL;

public class LoginFragment extends Fragment {

    private EditText loginEditTextMobile;
    private EditText loginEditTextPassword;
    private Button loginButtonRegister;
    private TextView forgotPwd_btn;

    /**Dib**/

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_login, container,false);
        loginEditTextMobile = (EditText) rootView.findViewById(R.id.loginMobileTxt);
        loginEditTextPassword = (EditText) rootView.findViewById(R.id.loginPasswordTxt);
        loginButtonRegister= (Button) rootView.findViewById(R.id.signInBtn);
        forgotPwd_btn = (TextView) rootView.findViewById(R.id.forgotpwd);
        forgotPwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callForgotpassword(v);
            }
        });
        loginButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity(v);
            }
        });
        return rootView;
    }

    public void homeActivity(View view){
        String token = FirebaseInstanceId.getInstance().getToken();
        //Network call
        final String mobile = loginEditTextMobile.getText().toString().trim();
        final String password = loginEditTextPassword.getText().toString().trim();
        final String fcmRegistrationId = token;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(KEY_MOBILE_LOGIN,mobile);
        params.put(KEY_PASSWORD_LOGIN,password);
        params.put(KEY_TOKEN,fcmRegistrationId);

        JSONObject jsonBody = new JSONObject(params);
        JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL ,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            NetworkCallModels models = new NetworkCallModels();
                            models.setJson(response);
                            models.status = Boolean.parseBoolean(models.getJson().getString("status"));
                            models.message = models.getJson().getString("message");

                            if(models.status){
                                String data_Token = models.getJson().getString("data");
                                JSONObject obj_token = new JSONObject(data_Token);
                                String Username = obj_token.getString("username");
                                String Email = obj_token.getString("email");
                                String access_Token = obj_token.getString("accessToken");

                                Toast.makeText(getActivity(),models.message , Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor =  getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("AccessToken", access_Token);
                                editor.putString("UserName", Username);
                                editor.putString("Email",Email);
                                SaveSharedPreferences.setUserName(getContext(),"True");
                                editor.commit();
                                startActivity(new Intent(getContext(),HomeActivity.class));
                            }else{
                                Toast.makeText(getActivity(),models.message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
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

    public void callForgotpassword(View view){
        final String mobile = loginEditTextMobile.getText().toString().trim();
        if(mobile.equals("")){
            Toast.makeText(getActivity(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
        }else{
            final Dialog regOTPDialog = new Dialog(getContext());
            regOTPDialog.setContentView(R.layout.dialog_inputforpwdotp);
            regOTPDialog.show();

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(KEY_FORPWD_MOBILE,mobile);
            JSONObject jsonBody = new JSONObject(params);
            JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, FORGOTPWD_URL ,jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                NetworkCallModels models = new NetworkCallModels();
                                models.setJson(response);
                                models.status = Boolean.parseBoolean(models.getJson().getString("status"));
                                models.message  = models.getJson().getString("message");

                                if(models.status){
                                    String data_Token = models.getJson().getString("data");
                                    JSONObject obj_token = new JSONObject(data_Token);
                                    final String password_otp_token = obj_token.getString("password_otp_token");
                                    String password_otp = obj_token.getString("password_otp");
                                    final String mobile = obj_token.getString("mobile");

                                    Intent sendOTPIntent = new Intent(getContext(), LoginFragment.class);
                                    PendingIntent sendOTPPendingIntent = PendingIntent.getActivity(getContext(),0,sendOTPIntent,0);
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(mobile , null, "OTP RECEIVED for Location Alarm is: " + password_otp , sendOTPPendingIntent, null);
                                    Log.d("Msg send","sent");

                                    final Dialog regOTPDialog = new Dialog(getContext());
                                    regOTPDialog.setContentView(R.layout.dialog_inputforpwdotp);
                                    regOTPDialog.setCanceledOnTouchOutside(false);
                                    regOTPDialog.show();

                                    Toast.makeText(getActivity(),models.message, Toast.LENGTH_SHORT).show();
                                    final EditText enter_otp = (EditText) regOTPDialog.findViewById(R.id.enterOTPforpwd);
                                    final EditText enter_new_password = (EditText) regOTPDialog.findViewById(R.id.new_pwd);
                                    Button verify_otp = (Button) regOTPDialog.findViewById(R.id.OTPverifyBtn);

                                    verify_otp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final String Verify_otp = enter_otp.getText().toString().trim();
                                            final String Verify_otp_token = password_otp_token;
                                            final String Verify_otp_sender = mobile;

                                            HashMap<String, String> params = new HashMap<String, String>();
                                            params.put(KEY_FORPWD_OTP_TOKEN, Verify_otp_token);
                                            params.put(KEY_FORPWD_PWD_OTP,Verify_otp);
                                            params.put(KEY_FORPWD_MOBILE,Verify_otp_sender);
                                            params.put(KEY_FORPWD_NEW_PASSWORD,enter_new_password.getText().toString().trim());


                                            JSONObject jsonBody = new JSONObject(params);
                                            JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, FORGOTPWD_OTP_VERIFICATION_URL ,jsonBody,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            try{
                                                                NetworkCallModels models = new NetworkCallModels();
                                                                models.setJson(response);
                                                                models.message = models.getJson().getString("message");
                                                                models.status = Boolean.parseBoolean(models.getJson().getString("status"));

                                                                if(models.status==true){
                                                                    Toast.makeText(getActivity(),models.message, Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(getContext(), com.trianz.locationalarm.Authentication.AuthenticationActivity.class));
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

                            } catch (JSONException e){
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

    }
}