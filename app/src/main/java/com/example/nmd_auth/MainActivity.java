package com.example.nmd_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private double latitude,longitude;
    private List<Address> matches;
    private EditText address;
    private Button submit;
    private Geocoder geo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = findViewById(R.id.etAddress);
        submit = findViewById(R.id.btnSubmit);
        geo = new Geocoder(this);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    matches = geo.getFromLocationName(address.getText().toString(),1);
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                if (!matches.isEmpty()){
                    latitude = matches.get(0).getLatitude();
                    longitude = matches.get(0).getLongitude();

                    Toast.makeText(MainActivity.this, latitude+" "+longitude, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
