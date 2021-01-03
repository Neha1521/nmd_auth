package com.example.nmd_auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
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
    private static Integer addrCnt = 0;
    private Button add, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_loc_list);

        Log.e("LocAct","now");

        add = findViewById(R.id.btnAdd);
        next = findViewById(R.id.btnNext);
        lvAddr = findViewById(R.id.lvAddr);
        fireDb = FirebaseDatabase.getInstance();

        if (addrCnt>=3){
            Toast.makeText(LocListActivity.this,"hi", Toast.LENGTH_SHORT).show();
            add.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addrCnt += 1;
                Intent intent = new Intent(LocListActivity.this, LocationActivity.class);
                intent.putExtra("Key", addrCnt.toString());
                startActivity(intent);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocListActivity.this, LogOutActivity.class));
            }
        });
    }
}
