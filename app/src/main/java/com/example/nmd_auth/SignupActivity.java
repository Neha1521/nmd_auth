package com.example.nmd_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private EditText username, password, repass, mail, phone;
        private FirebaseDatabase fireDb = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        repass = findViewById(R.id.etRepass);
        mail = findViewById(R.id.etMail);
        phone = findViewById(R.id.etPhone);
        Button submit = findViewById(R.id.btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userDb = fireDb.getReference("UserDetails");
                DatabaseReference uListDb = fireDb.getReference("Users");
                String sUsername, sPassword, sRepass, sMail, sPhone;
                sUsername = username.getText().toString();
                sPassword = password.getText().toString();
                sRepass = repass.getText().toString();
                sMail = mail.getText().toString();
                sPhone = phone.getText().toString().trim();

                if(!sUsername.isEmpty() && !sPassword.isEmpty() && !sRepass.isEmpty() && !sMail.isEmpty() && !sPhone.isEmpty()){
                    String key = userDb.push().getKey();
                    User user = new User(sUsername, sPassword, sMail, sPhone);
                    userDb.child(Objects.requireNonNull(key)).setValue(user);
                    uListDb.child(key).setValue("");
                    Intent intent = new Intent(SignupActivity.this, LocListActivity.class);
                    intent.putExtra("Key", key);
                    startActivity(intent);

                }
                else {
                    Toast.makeText(SignupActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
