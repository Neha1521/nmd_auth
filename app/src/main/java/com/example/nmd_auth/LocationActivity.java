package com.example.nmd_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
    private FirebaseAuth fireAuth = FirebaseAuth.getInstance();
    private double latitude,longitude;
    private List<Address> matchLatlng, matchAddr;
    private EditText address;
    private Geocoder geo;
    private GoogleMap map;
    private MarkerOptions markerOp;
    private Marker marker;
    private TextView chosenLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_location);

        address = findViewById(R.id.etAddress);
        Button submit = findViewById(R.id.btnSubmit);
        final Button next = findViewById(R.id.btnNext);
        chosenLocation = findViewById(R.id.tvLocation);
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        geo = new Geocoder(this);

        if (ContextCompat.checkSelfPermission(LocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(LocationActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(LocationActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        LocationTrack locationTrack = new LocationTrack(LocationActivity.this);


        if (locationTrack.canGetLocation()) {


            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();


            //Toast.makeText(getApplicationContext(), "Longitude:" + longitude + "\nLatitude:" + latitude, Toast.LENGTH_SHORT).show();

        } else {

            locationTrack.showSettingsAlert();
        }

        Objects.requireNonNull(mapFrag).getMapAsync(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    matchLatlng = geo.getFromLocationName(address.getText().toString(),1);
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                if (!matchLatlng.isEmpty()){
                    latitude = matchLatlng.get(0).getLatitude();
                    longitude = matchLatlng.get(0).getLongitude();

                    LatLng curLoc = new LatLng(latitude,longitude);
                    marker.remove();
                    markerOp = new MarkerOptions().position(curLoc).title("Selected Location");
                    marker = map.addMarker(markerOp);

                    try {
                        matchAddr = geo.getFromLocation(latitude, longitude, 1);
                        chosenLocation.setText(String.format("%s", matchAddr.get(0).getAddressLine(0)));



                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 17.5f));

                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference addrDb = fireDb.getReference(Objects.requireNonNull(fireAuth.getCurrentUser()).getUid());
                String key = getIntent().getStringExtra("Key");
                //Toast.makeText(LocationActivity.this, latitude+" "+longitude, Toast.LENGTH_SHORT).show();
                addrDb.child(Objects.requireNonNull(key)).setValue(latitude+" "+longitude);
                String pass = getIntent().getStringExtra("Pass");
                startActivity(new Intent(LocationActivity.this, LocListActivity.class).putExtra("Pass",pass));
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           int[] grantResults){

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(LocationActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng curLoc = new LatLng(latitude,longitude);
        markerOp = new MarkerOptions().position(curLoc).title("Current Location");
        marker = googleMap.addMarker(markerOp);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 17.5f));
        try {
            matchAddr = geo.getFromLocation(latitude, longitude, 1);
            chosenLocation.setText(String.format("%s", matchAddr.get(0).getAddressLine(0)));

            //Toast.makeText(LocationActivity.this, matches.get(0).getAddressLine(0)+"\n"+matches.get(0).getAddressLine(1)
            //+"\n"+matches.get(0).getAdminArea()+"\n"+matches.get(0).getPostalCode(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                latitude = latLng.latitude;
                longitude = latLng.longitude;

                marker.remove();
                markerOp = new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .title("New Marker");
                marker = map.addMarker(markerOp);

                try {
                    matchAddr = geo.getFromLocation(latitude, longitude, 1);
                    chosenLocation.setText(String.format("%s", matchAddr.get(0).getAddressLine(0)));

                    //Toast.makeText(LocationActivity.this, matches.get(0).getAddressLine(0)+"\n"+matches.get(0).getAddressLine(1)
                    //+"\n"+matches.get(0).getAdminArea()+"\n"+matches.get(0).getPostalCode(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }


}
