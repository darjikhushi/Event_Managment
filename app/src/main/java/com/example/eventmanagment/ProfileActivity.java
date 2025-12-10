package com.example.eventmanagment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    EditText edtName, edtMobile, edtEmail, edtOldPassword, edtNewPassword;
    ImageView ivMenu;
    Button btnUpdateProfile, btnForgotPassword;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    TextInputLayout nameLayout, mobileLayout, emailLayout;

    FirebaseUser user;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        edtEmail = findViewById(R.id.edtEmail);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);

        // Make sure fields are editable
        edtName.setEnabled(true);
        edtMobile.setEnabled(true);
        edtEmail.setEnabled(true);

        loadUserInfo();

        nameLayout = findViewById(R.id.nameLayout);
        mobileLayout = findViewById(R.id.mobileLayout);
        emailLayout = findViewById(R.id.emailLayout);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        ivMenu = findViewById(R.id.ivMenu);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        // Load user info

        updateDrawerHeaderUserName();

        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) drawerLayout.closeDrawer(Gravity.LEFT);
            else drawerLayout.openDrawer(Gravity.LEFT);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            else if (id == R.id.menu_participation) {
                // Open participation activity
                startActivity(new Intent(this, MyParticipationActivity.class));
            }
            else if (id == R.id.menu_events) startActivity(new Intent(ProfileActivity.this, MyEventsActivity.class));
            else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
            drawerLayout.closeDrawers();
            return true;
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            else if (item.getItemId() == R.id.nav_events) startActivity(new Intent(ProfileActivity.this, AddEventActivity.class));
            return true;
        });

        btnUpdateProfile.setOnClickListener(v -> new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Update Profile")
                .setMessage("Do you want to update your profile?")
                .setPositiveButton("Yes", (dialog, which) -> updateProfile())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());

        btnForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, ForgotPasswordActivity.class)));
    }

    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    edtName.setText(snapshot.child("Name").getValue(String.class));
                    edtMobile.setText(snapshot.child("Mobile").getValue(String.class));
                    edtEmail.setText(snapshot.child("email").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateDrawerHeaderUserName() {
        View header = navigationView.getHeaderView(0);
        TextView headerName = header.findViewById(R.id.headerUserName);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                headerName.setText(snapshot.child("Name").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateProfile() {
        String newName = edtName.getText().toString().trim();
        String newMobile = edtMobile.getText().toString().trim();
        String newEmail = edtEmail.getText().toString().trim();

        // Validations
        if(newName.isEmpty()){ edtName.setError("Name required"); edtName.requestFocus(); return; }
        if(newName.length()<3){ edtName.setError("Min 3 chars"); edtName.requestFocus(); return; }
        if(!newName.matches("^[a-zA-Z ]+$")){ edtName.setError("Only letters allowed"); edtName.requestFocus(); return; }

        if(newMobile.isEmpty()){ edtMobile.setError("Mobile required"); edtMobile.requestFocus(); return; }
        if(!newMobile.matches("^[0-9]{10}$")){ edtMobile.setError("10 digits only"); edtMobile.requestFocus(); return; }

        if(newEmail.isEmpty()){ edtEmail.setError("Email required"); edtEmail.requestFocus(); return; }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()){ edtEmail.setError("Invalid email"); edtEmail.requestFocus(); return; }

        // Update email in Firebase Auth first
        user.updateEmail(newEmail).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                // Update database
                userRef.child("Name").setValue(newName);
                userRef.child("Mobile").setValue(newMobile);
                userRef.child("email").setValue(newEmail);
                updateDrawerHeaderUserName();
            } else {
                edtEmail.setError("Failed to update email");
                edtEmail.requestFocus();
            }
        });

        // Password change
        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        if(!oldPass.isEmpty() && !newPass.isEmpty()){
            if(newPass.length()<6){ edtNewPassword.setError("Min 6 chars"); edtNewPassword.requestFocus(); return; }
            if(!newPass.matches(".*[A-Z].*")){ edtNewPassword.setError("1 uppercase required"); edtNewPassword.requestFocus(); return; }
            if(!newPass.matches(".*[0-9].*")){ edtNewPassword.setError("1 number required"); edtNewPassword.requestFocus(); return; }
            if(!newPass.matches(".*[!@#$%^&*()._+=-].*")){ edtNewPassword.setError("1 special char required"); edtNewPassword.requestFocus(); return; }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    user.updatePassword(newPass).addOnCompleteListener(passTask -> {
                        if(!passTask.isSuccessful()){ edtNewPassword.setError("Failed to update password"); }
                    });
                } else {
                    edtOldPassword.setError("Incorrect old password");
                    edtOldPassword.requestFocus();
                }
            });
        }
    }
}
