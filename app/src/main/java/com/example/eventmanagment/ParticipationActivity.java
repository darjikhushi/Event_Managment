package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;

public class ParticipationActivity extends AppCompatActivity {

    private TextView participationTitle;
    private TextInputEditText participantName, collegeName, age, gender, mobile, email;
    private TextInputLayout participantNameLayout, collegeNameLayout, ageLayout, genderLayout, mobileLayout, emailLayout;
    private TextView eventDetailsBox;
    private LinearLayout formCard;
    private DatabaseReference databaseReference;
    private String eventTitleFromIntent = "";
    private String eventIdFromIntent = "";

    // NAVIGATION
    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private NavigationView navigationView;
    private BottomNavigationView bottom_navigation;
    private View submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participation_form);

        // ---------- INTENT DATA ----------
        eventTitleFromIntent = getIntent().getStringExtra("eventTitle");
        eventIdFromIntent = getIntent().getStringExtra("id");
        Log.d("DEBUG_INTENT", "Received eventTitle = " + eventTitleFromIntent);
        Log.d("DEBUG_INTENT", "Received eventId = " + eventIdFromIntent);

        // ---------- FIREBASE ----------
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // ---------- INITIALIZE VIEWS ----------
        participationTitle = findViewById(R.id.participationTitle);
        participantName = findViewById(R.id.participantName);
        collegeName = findViewById(R.id.collegeName);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        participantNameLayout = findViewById(R.id.participantNameLayout);
        collegeNameLayout = findViewById(R.id.collegeNameLayout);
        ageLayout = findViewById(R.id.ageLayout);
        genderLayout = findViewById(R.id.genderLayout);
        mobileLayout = findViewById(R.id.mobileLayout);
        emailLayout = findViewById(R.id.emailLayout);
        submitButton = findViewById(R.id.submitButton);
        formCard = findViewById(R.id.formCard);

        // Dynamic event details TextView
        eventDetailsBox = new TextView(this);
        eventDetailsBox.setTextSize(16);
        eventDetailsBox.setPadding(12, 12, 12, 12);
        formCard.addView(eventDetailsBox, 1);

        // Set event title in TextView
        participationTitle.setText(eventTitleFromIntent);

        // NAVIGATION
        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigationView);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) drawerLayout.closeDrawer(Gravity.LEFT);
            else drawerLayout.openDrawer(Gravity.LEFT);
        });

        setupHeader();
        setupSideNav();
        setupBottomNav();

        // ---------- LOAD EVENT DETAILS ----------
        loadEventDetails();

        submitButton.setOnClickListener(view -> saveData());
    }

    private void loadEventDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.orderByChild("Title").equalTo(eventTitleFromIntent)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            eventDetailsBox.setText("Event details not found!");
                            return;
                        }
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String eventDesc = data.child("Description").getValue(String.class);
                            String eventVenue = data.child("Venue").getValue(String.class);
                            String eventStart = data.child("StartDate").getValue(String.class);
                            String eventEnd = data.child("EndDate").getValue(String.class);
                            String startTime = data.child("StartTime").getValue(String.class);
                            String endTime = data.child("EndTime").getValue(String.class);

                            String details =
                                    "Description: " + eventDesc + "\n"
                                    + "Venue: " + eventVenue + "\n"
                                    + "Date: " + eventStart + " to " + eventEnd + "\n"
                                    + "Time: " + startTime + " - " + endTime;
                            eventDetailsBox.setText(details);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void saveData() {
        clearErrors();

        String pName = participantName.getText().toString().trim();
        String cName = collegeName.getText().toString().trim();
        String pAge = age.getText().toString().trim();
        String pGender = gender.getText().toString().trim();
        String pMobile = mobile.getText().toString().trim();
        String pEmail = email.getText().toString().trim();

        boolean isValid = true;

        // VALIDATIONS
        if (pName.isEmpty()) {
            participantNameLayout.setError("Participant name is required");
            isValid = false;
        }
        if (cName.isEmpty()) {
            collegeNameLayout.setError("College name is required");
            isValid = false;
        }
        if (pAge.isEmpty()) {
            ageLayout.setError("Age is required");
            isValid = false;
        } else {
            try {
                int ageInt = Integer.parseInt(pAge);
                if (ageInt < 1 || ageInt > 120) {
                    ageLayout.setError("Enter a valid age");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                ageLayout.setError("Enter a numeric age");
                isValid = false;
            }
        }
        if (pGender.isEmpty()) {
            genderLayout.setError("Gender is required");
            isValid = false;
        }
        if (pMobile.isEmpty()) {
            mobileLayout.setError("Mobile number is required");
            isValid = false;
        } else if (!pMobile.matches("\\d{10}")) {
            mobileLayout.setError("Enter a valid 10-digit mobile number");
            isValid = false;
        }
        if (pEmail.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(pEmail).matches()) {
            emailLayout.setError("Enter a valid email address");
            isValid = false;
        }

        if (!isValid) return;

        // GET CURRENT USER UID
        String userId = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // SAVE TO FIREBASE
        HashMap<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("eventTitle", eventTitleFromIntent);
        attendeeData.put("participantName", pName);
        attendeeData.put("collegeName", cName);
        attendeeData.put("age", pAge);
        attendeeData.put("gender", pGender);
        attendeeData.put("mobile", pMobile);
        attendeeData.put("email", pEmail);
        attendeeData.put("userId", userId); // <-- NEW FIELD

        DatabaseReference attendeeRef = FirebaseDatabase.getInstance().getReference("Attendee");
        String attendeeId = attendeeRef.push().getKey();

        attendeeRef.child(attendeeId).setValue(attendeeData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        clearFields();

                        Intent paymentIntent = new Intent(ParticipationActivity.this, PaymentActivity.class);
                        paymentIntent.putExtra("participantName", pName);
                        paymentIntent.putExtra("eventTitle", eventTitleFromIntent);
                        startActivity(paymentIntent);
                    } else {
                        Toast.makeText(this, "Failed to submit. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void clearErrors() {
        participantNameLayout.setError(null);
        collegeNameLayout.setError(null);
        ageLayout.setError(null);
        genderLayout.setError(null);
        mobileLayout.setError(null);
        emailLayout.setError(null);
    }

    private void clearFields() {
        participantName.setText("");
        collegeName.setText("");
        age.setText("");
        gender.setText("");
        mobile.setText("");
        email.setText("");
    }

    // NAVIGATION HEADER
    private void setupHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.headerUserName);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("Name").getValue(String.class);
                            if (name != null) headerName.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void setupSideNav() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.menu_events) startActivity(new Intent(this, MyEventsActivity.class));
            else if (id == R.id.menu_participation) {
                // Open participation activity
                startActivity(new Intent(this, MyParticipationActivity.class));
            }
            else if (id == R.id.menu_profile) startActivity(new Intent(this, ProfileActivity.class));
            else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupBottomNav() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_events) startActivity(new Intent(this, AddEventActivity.class));
            else if (id == R.id.nav_profile) startActivity(new Intent(this, ProfileActivity.class));
            return true;
        });
    }
}
