package com.example.nmd_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private EditText mail;
    private FirebaseAuth fireAuth = FirebaseAuth.getInstance();



    private void setUp(final String mail) {

        fireAuth = FirebaseAuth.getInstance();

        fireAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Objects.requireNonNull(fireAuth.getCurrentUser()).reload()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if(fireAuth.getCurrentUser().isEmailVerified()){
                                                Intent intent = new Intent(SignupActivity.this, UserDetailsActivity.class);
                                                intent.putExtra("Mail", mail);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                            Objects.requireNonNull(fireAuth.getCurrentUser()).updateEmail(mail);
                            fireAuth.getCurrentUser().sendEmailVerification();


                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signup);

        mail = findViewById(R.id.etMail);
        Button submit = findViewById(R.id.btnSubmit);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireAuth.signOut();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String sMail;
                sMail = mail.getText().toString();

                if(!sMail.isEmpty()){

                    setUp(sMail);

                }
                else {
                    Toast.makeText(SignupActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
