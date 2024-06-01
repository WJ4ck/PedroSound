package com.actividad.pedrosound;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SugerenciaActivity extends AppCompatActivity {

    Button btn_enviar, btn_regresar;
    EditText et_sugerencia;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sugerencia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_enviar = findViewById(R.id.btn_enviar);
        btn_regresar = findViewById(R.id.btn_regresar);
        et_sugerencia = findViewById(R.id.et_sugerencia);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sugerencia = et_sugerencia.getText().toString().trim();
                uploadSugerent(sugerencia);
            }
        });

        btn_regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SugerenciaActivity.this, InformationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void uploadSugerent(String sugerencia) {

        if (sugerencia.isEmpty()) {
            Toast.makeText(SugerenciaActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        Map<String, Object> sugerenciaL = new HashMap<>();
        sugerenciaL.put("uidUser", currentUser.getUid());
        sugerenciaL.put("nombreUser", currentUser.getDisplayName());
        sugerenciaL.put("sugerencia", sugerencia);

        db.collection("SUGERENCIAS")
                .add(sugerenciaL)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SugerenciaActivity.this, "Sugerencia enviada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SugerenciaActivity.this, InformationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SugerenciaActivity.this, "ERROR. Sugerencia no enviada", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}