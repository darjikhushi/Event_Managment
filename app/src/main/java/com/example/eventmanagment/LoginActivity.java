//package com.example.eventmanagment;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.*;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class LoginActivity extends AppCompatActivity {
//
//    EditText emailEdit, passwordEdit;
//    Button loginButton, registerRedirectButton;
//    TextView forgotPasswordLink;
//
//    FirebaseAuth auth;
//    DatabaseReference databaseReference;
//
//    private static final String TAG = "LoginActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        // Initialize views
//        emailEdit = findViewById(R.id.emailEdit);
//        passwordEdit = findViewById(R.id.passwordEdit);
//        loginButton = findViewById(R.id.loginButton);
//        registerRedirectButton = findViewById(R.id.registerRedirectButton);
//        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
//
//        auth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//
//        // 🔹 Login button logic
//        loginButton.setOnClickListener(v -> {
//            String email = emailEdit.getText().toString().trim();
//            String password = passwordEdit.getText().toString().trim();
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            String uid = auth.getCurrentUser().getUid();
//
//                            databaseReference.child(uid).get()
//                                    .addOnCompleteListener(dataTask -> {
//                                        if (dataTask.isSuccessful() && dataTask.getResult().exists()) {
//                                            String userEmail = dataTask.getResult().child("email").getValue(String.class);
//                                            Toast.makeText(LoginActivity.this, "Welcome " + userEmail, Toast.LENGTH_SHORT).show();
//
//                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        } else {
//                                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//
//                        } else {
//                            Exception e = task.getException();
//                            Log.e(TAG, "Login Error: ", e);
//                            Toast.makeText(LoginActivity.this, "Login Failed: " +
//                                    (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
//                        }
//                    });
//        });
//
//        // 🔹 Register button → opens RegisterActivity
//        registerRedirectButton.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//            startActivity(intent);
//        });
//
//        // 🔹 Forgot Password link → opens ForgotPasswordActivity
//        forgotPasswordLink.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//            startActivity(intent);
//        });
//    }
//}
package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText emailEdit, passwordEdit;
    Button loginButton, registerRedirectButton;
    TextView forgotPasswordLink;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    private static final String TAG = "LoginActivity";

    private static final long EXPIRY = 24 * 60 * 60 * 1000; // 24 hours

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If already logged in AND not expired → skip login screen
        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        boolean loggedIn = sp.getBoolean("isLoggedIn", false);
        long loginTime = sp.getLong("loginTime", 0);

        if (loggedIn && (System.currentTimeMillis() - loginTime) < EXPIRY) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);
        registerRedirectButton = findViewById(R.id.registerRedirectButton);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

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

                            databaseReference.child(uid).get()
                                    .addOnCompleteListener(dataTask -> {
                                        if (dataTask.isSuccessful() && dataTask.getResult().exists()) {

                                            // SAVE SESSION FOR 24 HOURS
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putBoolean("isLoggedIn", true);
                                            editor.putLong("loginTime", System.currentTimeMillis());
                                            editor.apply();

                                            String userEmail = dataTask.getResult().child("email").getValue(String.class);
                                            Toast.makeText(LoginActivity.this, "Welcome " + userEmail, Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Exception e = task.getException();
                            Log.e(TAG, "Login Error: ", e);
                            Toast.makeText(LoginActivity.this, "Login Failed: " +
                                    (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        registerRedirectButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        forgotPasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }
}
