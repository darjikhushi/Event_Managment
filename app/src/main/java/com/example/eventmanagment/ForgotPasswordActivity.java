package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEditText;
    Button resetPasswordButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            // ---------------------------
            // 1. EMPTY EMAIL VALIDATION
            // ---------------------------
            if (email.isEmpty()) {
                emailEditText.setError("Please enter your email");
                emailEditText.requestFocus();
                return;
            }

            // ---------------------------
            // 2. EMAIL FORMAT VALIDATION
            // ---------------------------
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email");
                emailEditText.requestFocus();
                return;
            }

            // ---------------------------
            // 3. SEND RESET LINK
            // ---------------------------
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Reset link sent to " + email,
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Error: " + Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
