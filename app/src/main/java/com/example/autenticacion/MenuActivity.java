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
    // Creación de variables para capturar los elementos visuales
    private TextView textName;
    private TextView textEmail2;
    private TextView textLatitud;
    private TextView textLongitud;
    private Button bntLogout;
    private Button bntSaveLocation;

    // Variables de los datos que serán mostrados
    private String latitud = "";
    private String longitud = "";
    private String name = "";
    private String email = "";

    // Variables para acceder a Authentication y Database y Location
    private FirebaseAuth miAuth;
    private DatabaseReference miDatabase;
    LocationManager locationManager;

    // Acciones que ocurren una vez creada la pestaña
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Instancia de Authentication y Database
        miAuth = FirebaseAuth.getInstance();
        miDatabase = FirebaseDatabase.getInstance().getReference();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Instancia de los elementos visuales llamados por su Id
        textName = findViewById(R.id.textName);
        textEmail2 = findViewById(R.id.textEmail2);
        textLatitud = findViewById(R.id.textLatitud);
        textLongitud = findViewById(R.id.textLongitud);
        bntLogout = findViewById(R.id.bntLogout);
        bntSaveLocation = findViewById(R.id.btnSaveLocation);

        // Acción al clickear botón "Cerrar Sesion"
        bntLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se cierra sesión en authentication
                miAuth.signOut();
                // Un Intent lleva de regreso a la página inicial
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                finish();
            }
        });

        // Se llama al método para obtener datos del usuario
        getUserInfo();

        // Obtención de permisos para acceder a ubicación desde el Manifest
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            return;
        }
        // Obtención de la ubicación desde GPS
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // Acción al clickear botón "Guardar Ubicacion"
        bntSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se asignan los valores de latitud y longitud de los campos de texto
                latitud = textLatitud.getText().toString().replace("Latitud: ", "");
                longitud = textLongitud.getText().toString().replace("Longitud: ", "");
                // Se llama al método que almacena la ubicación en la base
                saveUserLocation();
            }
        });


    }

    // Método para guardar ubicacion del usuario
    private void saveUserLocation() {
        // Se crea la variable de ubicación en base a latitud y longitud
        String location = latitud+","+longitud;
        // Se obtiene el Id correspondiente al usuario loggeado
        String id = miAuth.getCurrentUser().getUid();

        // En la base de datos, para el usuario con el id correspondiente,
        // se agrega el campo de ubicación
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

    // Método para obtener información del usuario
    private void getUserInfo() {
        // Se obtiene el Id correspondiente al usuario loggeado
        String id = miAuth.getCurrentUser().getUid();

        // Desde base de datos, se consulta nombre y email del Id correspondiente
        miDatabase.child("User").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Si existen datos, se asignan a las variables
                    name = snapshot.child("name").getValue().toString();
                    email = snapshot.child("email").getValue().toString();

                    // Se asigna el valor de las variables a los campos de texto
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
        // Al actualizarse la ubicación del usuario, se asignan sus valores a los campos de texto
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
