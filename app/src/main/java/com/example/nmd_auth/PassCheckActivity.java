package com.example.nmd_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;

public class PassCheckActivity extends AppCompatActivity {

    private FirebaseAuth fireAuth;
    private FirebaseDatabase fireDb;
    private EditText password;
    private TextView name;
    private String dbPass, dbSalt, userPass, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pass_check);

        fireAuth = FirebaseAuth.getInstance();
        fireDb = FirebaseDatabase.getInstance();
        password = findViewById(R.id.etPassword);
        name = findViewById(R.id.tvName);
        Button submit = findViewById(R.id.btnSubmit);
        uid = getIntent().getStringExtra("Uid");

        DatabaseReference userDb = fireDb.getReference(Objects.requireNonNull(uid));
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(Objects.equals(snapshot1.getKey(), "Name")){
                        name.setText(Objects.requireNonNull(snapshot1.getValue()).toString());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String sPass = password.getText().toString();
                if(!sPass.isEmpty()){


                    DatabaseReference passDb = fireDb.getReference("Pass").child(Objects.requireNonNull(uid));

                    passDb.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1: snapshot.getChildren()){
                                System.out.println(snapshot1);
                                if (Objects.equals(snapshot1.getKey(), "Password"))
                                    dbPass = Objects.requireNonNull(snapshot1.getValue()).toString();
                                if (Objects.equals(snapshot1.getKey(), "Salt"))
                                    dbSalt = Objects.requireNonNull(snapshot1.getValue()).toString();
                            }
                            System.out.println(dbPass +"  "+dbSalt+"  "+userPass);
                            userPass = genPass(sPass, dbSalt);

                            if (Objects.equals(dbPass, userPass)){
                                startActivity(new Intent(PassCheckActivity.this, LogOutActivity.class));
                            }
                            else {
                                Toast.makeText(PassCheckActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else {
                    Toast.makeText(PassCheckActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
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
