package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText emailEdit, passwordEdit;
    Button loginButton, registerRedirectButton;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);
        registerRedirectButton = findViewById(R.id.registerRedirectButton);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Login button
        loginButton.setOnClickListener(v -> {
            String email = emailEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = auth.getCurrentUser().getUid();

                            // Fetch user data
                            databaseReference.child(uid).get()
                                    .addOnCompleteListener(dataTask -> {
                                        if (dataTask.isSuccessful() && dataTask.getResult().exists()) {
                                            String userEmail = dataTask.getResult().child("email").getValue(String.class);
                                            Long createdAt = dataTask.getResult().child("createdAt").getValue(Long.class);

                                            Toast.makeText(LoginActivity.this, "Welcome " + userEmail, Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("userEmail", userEmail);
                                            intent.putExtra("createdAt", createdAt != null ? createdAt : 0);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Exception e = dataTask.getException();
                                            Log.e(TAG, "Database Fetch Error: ", e);
                                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Exception e = task.getException();
                            Log.e(TAG, "Login Error: ", e);
                            Toast.makeText(LoginActivity.this, "Login Failed: You have to register first    " + (e != null ? e.getMessage() : "Unknown"), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Redirect to Register
        registerRedirectButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}
