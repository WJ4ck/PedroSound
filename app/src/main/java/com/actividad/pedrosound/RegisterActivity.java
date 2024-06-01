package com.actividad.pedrosound;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    EditText et_nombre, et_apellido, et_edad, et_correo, et_contrasena, et_verificar_contrasena;
    Button btn_registrarse, btn_regresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        et_nombre = findViewById(R.id.et_nombre);
        et_apellido = findViewById(R.id.et_apellido);
        et_edad = findViewById(R.id.et_edad);
        et_correo = findViewById(R.id.et_correo);
        et_contrasena = findViewById(R.id.et_contrasena);
        et_verificar_contrasena = findViewById(R.id.et_verificar_contrasena);

        btn_registrarse = findViewById(R.id.btn_registrarse);
        btn_regresar = findViewById(R.id.btn_regresar);

        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = et_nombre.getText().toString().trim();
                String apellido = et_apellido.getText().toString().trim();
                String edad = et_edad.getText().toString().trim();
                String correo = et_correo.getText().toString().trim();
                String pass = et_contrasena.getText().toString().trim();
                String veripass = et_verificar_contrasena.getText().toString().trim();

                if (!pass.equals(veripass)) {
                    Toast.makeText(RegisterActivity.this, "¡Las contraseñas son diferentes!", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(nombre, apellido, edad, correo, pass);
            }
        });
    }

    private void registerUser(String nombre, String apellido, String edad, String correo, String password) {
        // Validate user input (optional)
        if (nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || correo.isEmpty() || password.isEmpty() || password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt to create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful, update user profile
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                int age;
                                try {
                                    age = Integer.parseInt(edad);
                                } catch (NumberFormatException e) {
                                    Toast.makeText(RegisterActivity.this, "La edad debe ser un número válido", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                UserProfileChangeRequest profileUpdateRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nombre + " " + apellido)
                                        .build();

                                user.updateProfile(profileUpdateRequest)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    String uid = user.getUid();

                                                    // Create a map of user data (excluding password)
                                                    Map<String, Object> userData = new HashMap<>();
                                                    userData.put("uid", uid);
                                                    userData.put("nombre", nombre);
                                                    userData.put("apellido", apellido);
                                                    userData.put("edad", age);
                                                    userData.put("correo", correo);

                                                    // Save user data to Firestore (secure way)
                                                    db.collection("USUARIOS").document(uid)
                                                            .set(userData)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                    redirectToNext();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error writing document", e);
                                                                }
                                                            });

                                                    // No need to notify about successful profile update (implicit)
                                                } else {
                                                    Log.w(TAG, "Error updating profile", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Registration failed
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void redirectToNext() {
        Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}