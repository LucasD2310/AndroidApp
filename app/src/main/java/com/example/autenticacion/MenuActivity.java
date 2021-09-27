package com.example.autenticacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity implements LocationListener {

    private TextView textName;
    private TextView textEmail2;
    private TextView textLatitud;
    private TextView textLongitud;
    private Button bntLogout;
    private Button bntSaveLocation;

    private String latitud = "";
    private String longitud = "";

    private FirebaseAuth miAuth;
    private DatabaseReference miDatabase;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        miAuth = FirebaseAuth.getInstance();
        miDatabase = FirebaseDatabase.getInstance().getReference();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        textName = findViewById(R.id.textName);
        textEmail2 = findViewById(R.id.textEmail2);
        textLatitud = findViewById(R.id.textLatitud);
        textLongitud = findViewById(R.id.textLongitud);
        bntLogout = findViewById(R.id.bntLogout);
        bntSaveLocation = findViewById(R.id.btnSaveLocation);

        bntLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miAuth.signOut();
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                finish();
            }
        });

        getUserInfo();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        bntSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitud = textLatitud.getText().toString().replace("Latitud: ", "");
                longitud = textLongitud.getText().toString().replace("Longitud: ", "");
                saveUserLocation();
            }
        });


    }

    private void saveUserLocation() {
        String location = latitud+","+longitud;
        String id = miAuth.getCurrentUser().getUid();

        miDatabase.child("User").child(id).child("location").setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MenuActivity.this, "Ubicacion almacenada correctamente", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MenuActivity.this, "Ubicacion no pudo ser guardada", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserInfo() {
        String id = miAuth.getCurrentUser().getUid();
        miDatabase.child("User").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();

                    textName.setText(name);
                    textEmail2.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        textLatitud.setText("Latitud: "+location.getLatitude());
        textLongitud.setText("Longitud: "+location.getLongitude());
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}