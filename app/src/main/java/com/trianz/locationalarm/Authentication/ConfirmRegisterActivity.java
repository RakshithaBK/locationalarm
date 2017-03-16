package com.trianz.locationalarm.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.trianz.locationalarm.R;

public class ConfirmRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_register);
        Button button= (Button) findViewById(R.id.confirmSignIn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmRegisterActivity.this,AuthenticationActivity.class);
                startActivity(intent);

            }
        });
    }
}
