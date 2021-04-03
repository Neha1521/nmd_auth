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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class UserDetailsActivity extends AppCompatActivity {

    private EditText phone, password, repass;
    private Button submit;
    private FirebaseAuth fireAuth;
    private FirebaseDatabase firedb = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_user_details);

        Log.e("UserAct", "now");

        phone = findViewById(R.id.etPhone);
        password = findViewById(R.id.etPassword);
        repass = findViewById(R.id.etRepass);
        submit = findViewById(R.id.btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sMail = getIntent().getStringExtra("Mail");
                final String sPhone, sPassword, sRepass;
                sPhone = phone.getText().toString();
                sPassword = password.getText().toString();
                sRepass = repass.getText().toString();
                if (!sPhone.isEmpty() && !sPassword.isEmpty() && !sRepass.isEmpty()) {
                    if (!sPassword.equals(sRepass)) {
                        Toast.makeText(UserDetailsActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    } else {
                        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(sMail), sPassword);
                        fireAuth = FirebaseAuth.getInstance();
                        Objects.requireNonNull(fireAuth.getCurrentUser()).linkWithCredential(credential).addOnCompleteListener(UserDetailsActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(UserDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                DatabaseReference usersDb = firedb.getReference("Users");
                                DatabaseReference detailsDb = firedb.getReference(fireAuth.getCurrentUser().getUid());

                                usersDb.child(fireAuth.getCurrentUser().getUid()).setValue(sMail);
                                detailsDb.child("Name").setValue(sPhone);

                                startActivity(new Intent(UserDetailsActivity.this, LocListActivity.class).putExtra("Pass", sPassword));
                            }
                        });
                    }
                } else {
                    Toast.makeText(UserDetailsActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
