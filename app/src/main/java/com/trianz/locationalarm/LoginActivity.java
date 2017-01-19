package com.trianz.locationalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends Fragment {
    public static final String REGISTER_URL = "http://10.10.5.202:8080/com.priya.jersey.first/alarm/login/user";

    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_PASSWORD = "password";


    private EditText loginEditTextMobile;
    private EditText loginEditTextPassword;
    private Button loginButtonRegister;

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

        CheckBox checkboxvariable=(CheckBox)rootView.findViewById(R.id.loginCheckBox);

        checkboxvariable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ImageView imgView = (ImageView) rootView.findViewById(R.id.loginFingerImg);
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    imgView.setVisibility(View.VISIBLE);
                }else{
                    imgView.setVisibility(View.INVISIBLE);
                }

            }
        });
                return rootView;


    }

    public void homeActivity(View view){

        Intent homeActivity = new Intent(getContext(),MainActivity.class);
                                startActivity(homeActivity);


//        final String mobile = loginEditTextMobile.getText().toString().trim();
//        final String password = loginEditTextPassword.getText().toString().trim();
//
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put(KEY_MOBILE, mobile);
//        params.put(KEY_PASSWORD,password);
//
//        JSONObject jsonBody = new JSONObject(params);
//
//        JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, REGISTER_URL ,jsonBody,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            // Parsing json object response
//                            // response will be a json object
//                            JSONObject json = new JSONObject(response.toString());
//                            Log.v("Json obj" ,json.toString());
//                            Boolean status = Boolean.parseBoolean(json.getString("status"));
//                            String message = json.getString("message");
//                            if(status==true){
//                                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
//                                Intent homeActivity = new Intent(getContext(),HomeActivity.class);
//                                startActivity(homeActivity);
//                            }else{
//                                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        String message = null;
//                        if (volleyError instanceof NetworkError) {
//                            message = "Cannot connect to Internet...Please check your connection!";
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        } else if (volleyError instanceof ServerError) {
//                            message = "The server could not be found. Please try again after some time!!";
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        } else if (volleyError instanceof AuthFailureError) {
//                            message = "Cannot connect to Internet...Please check your connection!";
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        } else if (volleyError instanceof ParseError) {
//                            message = "Parsing error! Please try again after some time!!";
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        } else if (volleyError instanceof NoConnectionError) {
//                            message = "Cannot connect to Internet...Please check your connection!";
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        } else if (volleyError instanceof TimeoutError) {
//                            message = "Connection TimeOut! Please check your internet connection.";
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//
//                });
//
//        MySingleton.getInstance(getActivity()).addToRequestQueue(JsonObjRequest);
        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //requestQueue.add(JsonObjRequest );

    }
}

