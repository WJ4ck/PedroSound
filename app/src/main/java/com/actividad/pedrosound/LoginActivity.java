package com.actividad.pedrosound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText et_usuario, et_contrasena;
    Button btn_iniciar_sesion, btn_registrarse;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        et_usuario = findViewById(R.id.et_usuario);
        et_contrasena = findViewById(R.id.et_contrasena);
        btn_iniciar_sesion = findViewById(R.id.btn_iniciar_sesion);
        btn_registrarse = findViewById(R.id.btn_registrarse);

        mAuth = FirebaseAuth.getInstance();

        btn_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailUser = et_usuario.getText().toString().trim();
                String passUser = et_contrasena.getText().toString().trim();

                if(emailUser.isEmpty() && passUser.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Ingresa los datos", Toast.LENGTH_LONG).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailUser).matches()) {
                    et_usuario.setError("Ingresa un correo");
                    et_usuario.setFocusable(true);
                } else if (passUser.length() < 6) {
                    et_contrasena.setError("Contraseña minimo de 6 digitos");
                    et_contrasena.setFocusable(true);
                } else {
                    loginUser(emailUser, passUser);
                }
            }
        });

        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void loginUser(String emailUser, String passUser) {
        mAuth.getInstance().signInWithEmailAndPassword(emailUser, passUser)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Iniciar sesión exitosa
                            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Error al iniciar sesión
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}