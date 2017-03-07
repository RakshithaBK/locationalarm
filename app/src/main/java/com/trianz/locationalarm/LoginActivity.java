package com.trianz.locationalarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.trianz.locationalarm.Utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_MOBILE_LOGIN;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_PASSWORD_LOGIN;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.LOGIN_URL;

public class LoginActivity extends Fragment {

    private EditText loginEditTextMobile;
    private EditText loginEditTextPassword;
    private Button loginButtonRegister;

    /**Dib**/
    public static final String MY_PREFS_NAME = "MyPrefsFile" ;

    public LoginActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_login, container,false);
        loginEditTextMobile = (EditText) rootView.findViewById(R.id.loginMobileTxt);
        loginEditTextPassword = (EditText) rootView.findViewById(R.id.loginPasswordTxt);
         loginButtonRegister= (Button) rootView.findViewById(R.id.signInBtn);

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
        Log.d("token",token);

//        Intent homeActivity = new Intent(getContext(),HomeActivity.class);
//                                startActivity(homeActivity);

        //Network call
        final String mobile = loginEditTextMobile.getText().toString().trim();
        final String password = loginEditTextPassword.getText().toString().trim();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(KEY_MOBILE_LOGIN,mobile);
        params.put(KEY_PASSWORD_LOGIN,password);

        JSONObject jsonBody = new JSONObject(params);
        Log.d("Login_Params",params.toString());

        JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL ,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            JSONObject json = new JSONObject(response.toString());
                            Log.d("Json obj" ,json.toString());
                            Boolean status = Boolean.parseBoolean(json.getString("status"));
                            String message = json.getString("message");
                            String data_Token = json.getString("data");
                            JSONObject obj_token = new JSONObject(data_Token);
                            String access_Token = obj_token.getString("accessToken");
                            Log.d("access_Token",access_Token);


                            if(status==true){
                                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                                Intent homeActivity = new Intent(getContext(),HomeActivity.class);
                                SharedPreferences.Editor editor =  getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("AccessToken", access_Token);
                                SaveSharedPreferences.setUserName(getContext(),"True");
                                editor.commit();
                                startActivity(homeActivity);
                            }else{
                                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
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
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }


                });

        MySingleton.getInstance(getActivity()).addToRequestQueue(JsonObjRequest);
        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //requestQueue.add(JsonObjRequest );

    }



}

