package com.example.nmd_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LocListActivity extends AppCompatActivity {

    private FirebaseDatabase fireDb;
    private FirebaseAuth fireAuth;
    private ListView lvAddr;
    private List<String> addr;
    private static Integer addrCnt = 0;
    private ArrayAdapter<String> addrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_loc_list);

        Log.e("LocAct","now");

        Button add = findViewById(R.id.btnAdd);
        Button next = findViewById(R.id.btnNext);
        lvAddr = findViewById(R.id.lvAddr);
        fireDb = FirebaseDatabase.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        addr = new ArrayList<>();
        addrAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, addr);
        lvAddr.setAdapter(addrAdapter);

        DatabaseReference locDb = fireDb.getReference(Objects.requireNonNull(fireAuth.getCurrentUser()).getUid());

        locDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double latitude, longitude;
                String location;
                List<Address> address;
                Geocoder geo = new Geocoder(LocListActivity.this);
                for (DataSnapshot snapshot1:snapshot.getChildren()) {
                    if(!Objects.equals(snapshot1.getKey(), "Name")) {
                        location = Objects.requireNonNull(snapshot1.getValue()).toString();
                        String[] loc = location.split(" ",2);
                        latitude = Double.parseDouble(loc[0]);
                        longitude = Double.valueOf(loc[1]);
                        try {
                            address = geo.getFromLocation(latitude, longitude, 1);
                            addr.add(address.get(0).getAddressLine(0));
                            addrAdapter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (addrCnt>=3){
            add.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }




        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addrCnt += 1;
                String pass = getIntent().getStringExtra("Pass");
                Log.e("Pass", pass);
                Intent intent = new Intent(LocListActivity.this, LocationActivity.class);
                intent.putExtra("Key", addrCnt.toString());
                intent.putExtra("Pass", pass);
                startActivity(intent);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass, hashPass, salt;

                pass = getIntent().getStringExtra("Pass");
                salt = genSalt();
                hashPass = genPass(Objects.requireNonNull(pass), salt);
                DatabaseReference passDb = fireDb.getReference("Pass").child(fireAuth.getCurrentUser().getUid());
                passDb.child("Password").setValue(hashPass);
                passDb.child("Salt").setValue(salt);
                startActivity(new Intent(LocListActivity.this, LogOutActivity.class));
            }
        });
    }
    private String genPass(String pass, String salt){
        String saltedPass = pass.concat(salt);
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = Objects.requireNonNull(digest).digest(saltedPass.getBytes(StandardCharsets.UTF_8));

        return toHexString(hash);
    }
    private String genSalt(){

        byte[] array = new byte[4];
        new Random().nextBytes(array);

        return new String(array, Charset.forName("UTF-8"));
    }
    public static String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
