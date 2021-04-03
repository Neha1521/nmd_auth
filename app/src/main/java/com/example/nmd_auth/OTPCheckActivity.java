package com.example.nmd_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OTPCheckActivity extends AppCompatActivity {

    private EditText otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_otp_check);

        otp = findViewById(R.id.etOtp);
        Button next = findViewById(R.id.btnNext);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actualOtp = getIntent().getStringExtra("OTP");
                String userOtp = otp.getText().toString();
                if(!userOtp.isEmpty() && userOtp.equals(actualOtp)){
                    Toast.makeText(OTPCheckActivity.this, "Success",Toast.LENGTH_SHORT).show();
                    String uid = getIntent().getStringExtra("Uid");
                    startActivity(new Intent(OTPCheckActivity.this, PassCheckActivity.class).putExtra("Uid", uid));
                }
                else {
                    Toast.makeText(OTPCheckActivity.this, "Wrong OTP",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
