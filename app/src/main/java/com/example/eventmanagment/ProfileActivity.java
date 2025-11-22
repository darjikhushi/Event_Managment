package com.example.eventmanagment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//
//public class ProfileActivity extends AppCompatActivity {
//
//    TextView txtName, txtMobile, txtEmail;
//    EditText edtOldPassword, edtNewPassword;
//    Button btnUpdatePassword, btnForgotPassword;
//
//    FirebaseUser user;
//    DatabaseReference userRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        txtName = findViewById(R.id.txtName);
//        txtMobile = findViewById(R.id.txtMobile);
//        txtEmail = findViewById(R.id.txtEmail);
//
//        edtOldPassword = findViewById(R.id.edtOldPassword);
//        edtNewPassword = findViewById(R.id.edtNewPassword);
//
//        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
//        btnForgotPassword = findViewById(R.id.btnForgotPassword);
//
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
//
//        loadUserInfo();
//
//        btnUpdatePassword.setOnClickListener(v -> updatePassword());
//
//        btnForgotPassword.setOnClickListener(v ->
//                startActivity(new Intent(ProfileActivity.this, ForgotPasswordActivity.class))
//        );
//    }
//
//    private void loadUserInfo() {
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                txtName.setText("Name: " + snapshot.child("Name").getValue(String.class));
//                txtMobile.setText("Mobile: " + snapshot.child("Mobile").getValue());
//                txtEmail.setText("Email: " + snapshot.child("email").getValue(String.class));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//
//    private void updatePassword() {
//        String oldPass = edtOldPassword.getText().toString().trim();
//        String newPass = edtNewPassword.getText().toString().trim();
//
//        AuthCredential credential = EmailAuthProvider
//                .getCredential(user.getEmail(), oldPass);
//
//        user.reauthenticate(credential).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
//                    if (updateTask.isSuccessful()) {
//                        Toast.makeText(this, "Password Updated!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                Toast.makeText(this, "Old password incorrect", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
public class ProfileActivity extends AppCompatActivity {

    EditText edtName, edtMobile, edtEmail, edtOldPassword, edtNewPassword;
    ImageView btnEditName, btnEditMobile, btnEditEmail;
    Button btnUpdateProfile, btnForgotPassword;

    FirebaseUser user;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        edtEmail = findViewById(R.id.edtEmail);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);

        btnEditName = findViewById(R.id.btnEditName);
        btnEditMobile = findViewById(R.id.btnEditMobile);
        btnEditEmail = findViewById(R.id.btnEditEmail);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        loadUserInfo();

        btnEditName.setOnClickListener(v -> edtName.setEnabled(true));
        btnEditMobile.setOnClickListener(v -> edtMobile.setEnabled(true));
        btnEditEmail.setOnClickListener(v -> edtEmail.setEnabled(true));

        btnUpdateProfile.setOnClickListener(v -> updateProfile());

        btnForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, ForgotPasswordActivity.class)));
    }

    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Object nameObj = snapshot.child("Name").getValue();
                Object mobileObj = snapshot.child("Mobile").getValue();
                Object emailObj = snapshot.child("email").getValue();

                edtName.setText(nameObj != null ? String.valueOf(nameObj) : "User");
                edtMobile.setText(mobileObj != null ? String.valueOf(mobileObj) : "");
                edtEmail.setText(emailObj != null ? String.valueOf(emailObj) : "");

                edtName.setEnabled(false);
                edtMobile.setEnabled(false);
                edtEmail.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateProfile() {

        String newName = edtName.getText().toString().trim();
        String newMobile = edtMobile.getText().toString().trim();
        String newEmail = edtEmail.getText().toString().trim();

        // Update firebase
        userRef.child("Name").setValue(newName);
        userRef.child("Mobile").setValue(newMobile);
        userRef.child("email").setValue(newEmail);

        // Update firebase authentication email
        user.updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        // Update password if provided
        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();

        if (!oldPass.isEmpty() && !newPass.isEmpty()) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass);
                    Toast.makeText(this, "Old Password is Updated", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Old Password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        }

        edtName.setEnabled(false);
        edtMobile.setEnabled(false);
        edtEmail.setEnabled(false);
    }
}
