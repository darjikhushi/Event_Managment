//package com.example.eventmanagment;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.*;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    EditText emailRegister, passwordRegister, nameRegister, mobileRegister;
//    Button registerButton;
//    FirebaseAuth auth;
//    DatabaseReference usersRef;
//    FirebaseDatabase firebaseDatabase;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        // Added new fields
//        nameRegister = findViewById(R.id.nameRegister);
//        mobileRegister = findViewById(R.id.mobileRegister);
//
//        emailRegister = findViewById(R.id.emailRegister);
//        passwordRegister = findViewById(R.id.passwordRegister);
//        registerButton = findViewById(R.id.registerButton);
//
//        auth = FirebaseAuth.getInstance();
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        usersRef = firebaseDatabase.getReference("Users");
//
//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String name = nameRegister.getText().toString().trim();
//                String mobile = mobileRegister.getText().toString().trim();
//                String email = emailRegister.getText().toString().trim();
//                String password = passwordRegister.getText().toString().trim();
//
//                // Validation
//                if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || password.isEmpty()) {
//                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (mobile.length() != 10) {
//                    Toast.makeText(RegisterActivity.this, "Enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (password.length() < 6) {
//                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                auth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//
//                                    String uid = auth.getCurrentUser().getUid();
//
//                                    // storing new fields also
//                                    Map<String, Object> userData = new HashMap<>();
//                                    userData.put("Name", name);
//                                    userData.put("Mobile", mobile);
//                                    userData.put("email", email);
//                                    userData.put("createdAt", System.currentTimeMillis());
//
//                                    usersRef.child(uid).setValue(userData)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> dbTask) {
//                                                    if (dbTask.isSuccessful()) {
//                                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
//                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//                                                        finish();
//                                                    } else {
//                                                        Toast.makeText(RegisterActivity.this, "Database Error: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                    }
//                                                }
//                                            });
//
//                                } else {
//                                    Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });
//    }
//}


package com.example.eventmanagment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText emailRegister, passwordRegister, nameRegister, mobileRegister;
    Button registerButton;
    FirebaseAuth auth;
    DatabaseReference usersRef;
    FirebaseDatabase firebaseDatabase;
    Button loginRedirectButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Fields
        nameRegister = findViewById(R.id.nameRegister);
        mobileRegister = findViewById(R.id.mobileRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        registerButton = findViewById(R.id.registerButton);
        loginRedirectButton = findViewById(R.id.loginRedirectButton);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");

        loginRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameRegister.getText().toString().trim();
                String mobile = mobileRegister.getText().toString().trim();
                String email = emailRegister.getText().toString().trim();
                String password = passwordRegister.getText().toString().trim();

                // -----------------------
                // 1. NAME VALIDATION
                // -----------------------
                if (name.isEmpty()) {
                    nameRegister.setError("Name is required");
                    nameRegister.requestFocus();
                    return;
                }
                if (name.length() < 3) {
                    nameRegister.setError("Name must be at least 3 characters");
                    nameRegister.requestFocus();
                    return;
                }
                if (!name.matches("^[a-zA-Z ]+$")) {
                    nameRegister.setError("Name should contain only letters");
                    nameRegister.requestFocus();
                    return;
                }

                // -----------------------
                // 2. MOBILE VALIDATION
                // -----------------------
                if (mobile.isEmpty()) {
                    mobileRegister.setError("Mobile number is required");
                    mobileRegister.requestFocus();
                    return;
                }
                if (!mobile.matches("^[0-9]{10}$")) {
                    mobileRegister.setError("Enter a valid 10-digit number");
                    mobileRegister.requestFocus();
                    return;
                }

                // -----------------------
                // 3. EMAIL VALIDATION
                // -----------------------
                if (email.isEmpty()) {
                    emailRegister.setError("Email is required");
                    emailRegister.requestFocus();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailRegister.setError("Invalid email format");
                    emailRegister.requestFocus();
                    return;
                }

                // -----------------------
                // 4. PASSWORD VALIDATION
                // -----------------------
                if (password.isEmpty()) {
                    passwordRegister.setError("Password is required");
                    passwordRegister.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    passwordRegister.setError("Password must be at least 6 characters");
                    passwordRegister.requestFocus();
                    return;
                }
                if (!password.matches(".*[A-Z].*")) {
                    passwordRegister.setError("Password must contain at least 1 uppercase letter");
                    passwordRegister.requestFocus();
                    return;
                }
                if (!password.matches(".*[0-9].*")) {
                    passwordRegister.setError("Password must contain at least 1 number");
                    passwordRegister.requestFocus();
                    return;
                }
                if (!password.matches(".*[!@#$%^&*()._+=-].*")) {
                    passwordRegister.setError("Password must contain 1 special character");
                    passwordRegister.requestFocus();
                    return;
                }

                // -----------------------
                // FIREBASE REGISTRATION
                // -----------------------
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    String uid = auth.getCurrentUser().getUid();

                                    // store new fields also
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("Name", name);
                                    userData.put("Mobile", mobile);
                                    userData.put("email", email);
                                    userData.put("createdAt", System.currentTimeMillis());

                                    usersRef.child(uid).setValue(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> dbTask) {
                                                    if (dbTask.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Database Error: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
}
