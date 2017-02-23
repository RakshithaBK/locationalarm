package com.trianz.locationalarm;

import android.content.Intent;
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

import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_EMAIL;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_MOBILE;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_PASSWORD;
import static com.trianz.locationalarm.Utils.Constants.authServiceInstances.KEY_TOKEN;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.REGISTER_URL;

public class RegistrationActivity extends Fragment {

    private EditText editTextMobile;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;

    public RegistrationActivity() {
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
        Button button= (Button) rootView.findViewById(R.id.regBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    confirmRegistration(v);
            }
        });


        return rootView;
    }

    public void confirmRegistration(View view) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("token",token);

        final String mobile = editTextMobile.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String fcmRegistrationId = token;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(KEY_MOBILE, mobile);
        params.put(KEY_PASSWORD,password);
        params.put(KEY_EMAIL, email);
        params.put(KEY_TOKEN,fcmRegistrationId);

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
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
//        requestQueue.add(JsonObjRequest );
    }


    public void OnResponseValidation(JSONObject response){
        try {
            // Parsing json object response
            // response will be a json object
            JSONObject json = new JSONObject(response.toString());
            Boolean status = Boolean.parseBoolean(json.getString("status"));
            String message = json.getString("message");

            if(status==true){
                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                Intent homeActivity = new Intent(getContext(),ConfirmRegisterActivity.class);
                startActivity(homeActivity);
            }else{
                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

