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
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private EditText mail;
    private FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
    private FirebaseAuth fireAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signup);

        mail = findViewById(R.id.etMail);
        Button submit = findViewById(R.id.btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String sMail;
                sMail = mail.getText().toString();
                DatabaseReference userDb = fireDb.getReference("Users");

                if(!sMail.isEmpty()){

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                               if(sMail.equals(snapshot1.getKey())){
                                   //proceed to location check
                                   Intent intent = new Intent(SignupActivity.this, LogOutActivity.class);
                                   intent.putExtra("Mail", sMail);
                                   startActivity(intent);
                               }
                            }
                            // proceed to user set up
                            setUp(sMail);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    userDb.addValueEventListener(valueEventListener);

                }
                else {
                    Toast.makeText(SignupActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setUp(final String mail) {
        fireAuth = FirebaseAuth.getInstance();
        fireAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (!Objects.requireNonNull(fireAuth.getCurrentUser()).isEmailVerified()) {
                                fireAuth.getCurrentUser().updateEmail(mail);
                                fireAuth.getCurrentUser().sendEmailVerification();
                            }
                            else {
                                Intent intent = new Intent(SignupActivity.this, UserDetailsActivity.class);
                                intent.putExtra("Mail", mail);
                                startActivity(intent);
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
