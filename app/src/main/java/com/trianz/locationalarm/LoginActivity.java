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

import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends Fragment {

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

                return rootView;


    }

    public void homeActivity(View view){

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("token",token);

        Intent homeActivity = new Intent(getContext(),HomeActivity.class);
                                startActivity(homeActivity);

        //Network call
//        final String mobile = loginEditTextMobile.getText().toString().trim();
//        final String password = loginEditTextPassword.getText().toString().trim();
//
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put(KEY_MOBILE_LOGIN, mobile);
//        params.put(KEY_PASSWORD_LOGIN,password);
//
//        JSONObject jsonBody = new JSONObject(params);
//
//        JsonObjectRequest JsonObjRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL ,jsonBody,
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

