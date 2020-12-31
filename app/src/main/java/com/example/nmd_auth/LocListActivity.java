package com.example.nmd_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class LocListActivity extends AppCompatActivity {

    private FirebaseDatabase fireDb;
    private ListView lvAddr;
    private List<String> addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_loc_list);

        Button add = findViewById(R.id.btnAdd);
        lvAddr = findViewById(R.id.lvAddr);
        fireDb = FirebaseDatabase.getInstance();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = getIntent().getStringExtra("Key");
                Intent intent = new Intent(LocListActivity.this, LocationActivity.class);
                intent.putExtra("Key", key);
                startActivity(intent);
            }
        });
    }
}
