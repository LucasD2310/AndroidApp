package com.example.autenticacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    // Creación de variables para capturar los elementos visuales
    private EditText editEmail;
    private EditText editPass;
    private Button btnLogin;

    // Variables de los datos que serán mostrados
    private String email = "";
    private String pass = "";

    // Variables para acceder a Authentication y Database
    private FirebaseAuth miAuth;
    private DatabaseReference miDatabase;

    // Acciones que ocurren una vez creada la pestaña
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instancia de Authentication y Database
        miAuth = FirebaseAuth.getInstance();
        miDatabase = FirebaseDatabase.getInstance().getReference();

        // Instancia de los elementos visuales llamados por su Id
        editEmail = findViewById(R.id.editEmail);
        editPass = findViewById(R.id.editPass);
        btnLogin = findViewById(R.id.btnLogin);

        // Acción al clickear botón "Iniciar Sesion"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se capturan los valores contenidos en los campos de texto
                email = editEmail.getText().toString();
                pass = editPass.getText().toString();
                // Si ninguno de los campos ingresados está vacío,
                if(!email.isEmpty() && !pass.isEmpty()) {
                    // Se realiza el inicio de sesión
                    loginUser();
                }
                else {
                    // De lo contrario se indica en un mensaje campos faltantes
                    Toast.makeText(LoginActivity.this, "Debe completar los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para inicio de sesión
    private void loginUser() {
        miAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this, "No se pudo iniciar sesión. Compruebe los datos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
