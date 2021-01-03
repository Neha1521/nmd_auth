package com.example.nmd_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private double latitude, longitude;
    private String[] userLoc = new String[3];
    private EditText mail;
    private String sMail, uid;
    private FirebaseDatabase fireDb = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_in);

        Log.e("SigninAct","now");

        mail = findViewById(R.id.etMail);
        Button submit = findViewById(R.id.btnSubmit);
        uid = getIntent().getStringExtra("uid");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sMail = mail.getText().toString();
                DatabaseReference userDb = fireDb.getReference("Users");

                if(!sMail.isEmpty()){

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                if(sMail.equals(snapshot1.getValue())){
                                    checkLocation(snapshot1.getKey());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    userDb.addValueEventListener(valueEventListener);
                }
                else {
                    Toast.makeText(SignInActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void checkLocation(final String uid) {
        Log.e("Check loc","XD");

        LocationTrack locationTrack = new LocationTrack(SignInActivity.this);

        if (locationTrack.canGetLocation()) {

            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();

            Log.e("", uid);

            DatabaseReference locDb = fireDb.getReference(uid);
            locDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()) {
                        if(!Objects.equals(snapshot1.getKey(), "Phone")) {
                            System.out.println(snapshot1.getKey());
                            userLoc[Integer.valueOf(Objects.requireNonNull(snapshot1.getKey()))-1] = Objects.requireNonNull(snapshot1.getValue()).toString();
                        }
                    }
                    for(int i=0;i<3;i++) {
                        System.out.println(Arrays.toString(userLoc));
                        String[] loc = userLoc[i].split(" ", 2);
                        float[] dist = new float[1];
                        Location.distanceBetween(latitude, longitude,
                                Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), dist);

                        double rad = 2.0 * 1000.0;
                        System.out.println(rad + " " + dist[0]);
                        if (dist[0] > rad) {
                            Toast.makeText(getBaseContext(),
                                    "Outside, distance from center: " + dist[0] + " radius: " + rad,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(),
                                    "Inside, distance from center: " + dist[0] + " radius: " + rad,
                                    Toast.LENGTH_LONG).show();
                            sendOTP();
                            return;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            locationTrack.showSettingsAlert();
        }
    }


    private void sendOTP() {

        Log.e("SendOTP",":P");

        final StringBuilder generatedToken = new StringBuilder();
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            for (int i = 0; i < 6; i++) {
                generatedToken.append(number.nextInt(9));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        GMailSender sender = new GMailSender("nmdauth@gmail.com",
                                "naimurukudm!");
                        sender.sendMail("Hello", "This is sent from our authentication app for testing purposes. This is your OTP "+ generatedToken.toString(),
                                "nmdauth@gmail.com", sMail);
                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                    }
                }

            }).start();
            startActivity(new Intent(SignInActivity.this, PassCheckActivity.class).putExtra("OTP", generatedToken.toString()));

    }
}
