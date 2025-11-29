package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;

public class ParticipationActivity extends AppCompatActivity {

    EditText participationTitle, participantName, collegeName, age, gender, mobile, email;
    Button submitButton;

    DatabaseReference databaseReference;

    String eventTitleFromIntent = "";
    String eventIdFromIntent = "";

    LinearLayout formCard;
    TextView eventDetailsBox;

    // ---------- NAVIGATION ----------
    DrawerLayout drawerLayout;
    ImageView ivMenu;
    NavigationView navigationView;
    BottomNavigationView bottom_navigation;

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
        databaseReference = FirebaseDatabase.getInstance().getReference("Participation");

        // ---------- INITIALIZE VIEWS ----------
        participationTitle = findViewById(R.id.participationTitle);
        participantName = findViewById(R.id.participantName);
        collegeName = findViewById(R.id.collegeName);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        submitButton = findViewById(R.id.submitButton);

        formCard = findViewById(R.id.formCard);

        // Dynamic event details TextView
        eventDetailsBox = new TextView(this);
        eventDetailsBox.setTextSize(16);
        eventDetailsBox.setPadding(12,12,12,12);
        formCard.addView(eventDetailsBox, 1);

        participationTitle.setText(eventTitleFromIntent);
        participationTitle.setEnabled(false);

        // ---------- NAVIGATION VIEWS ----------
        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigationView);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
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
                            String eventTitle = data.child("Title").getValue(String.class);
                            String eventDesc = data.child("Description").getValue(String.class);
                            String eventVenue = data.child("Venue").getValue(String.class);
                            String eventStart = data.child("StartDate").getValue(String.class);
                            String eventEnd = data.child("EndDate").getValue(String.class);
                            String startTime = data.child("StartTime").getValue(String.class);
                            String endTime = data.child("EndTime").getValue(String.class);

                            // Show event info in dynamic text box

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void saveData() {
        String pName = participantName.getText().toString().trim();
        String cName = collegeName.getText().toString().trim();
        String pAge = age.getText().toString().trim();
        String pGender = gender.getText().toString().trim();
        String pMobile = mobile.getText().toString().trim();
        String pEmail = email.getText().toString().trim();

        if (pName.isEmpty() || cName.isEmpty() || pAge.isEmpty() ||
                pGender.isEmpty() || pMobile.isEmpty() || pEmail.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("eventTitle", eventTitleFromIntent);
        data.put("participantName", pName);
        data.put("collegeName", cName);
        data.put("age", pAge);
        data.put("gender", pGender);
        data.put("mobile", pMobile);
        data.put("email", pEmail);

        String id = databaseReference.push().getKey();

        databaseReference.child(eventTitleFromIntent).child(id)
                .setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ParticipationActivity.this,
                                "Participation Submitted Successfully",
                                Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(ParticipationActivity.this,
                                "Failed to submit. Try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        participantName.setText("");
        collegeName.setText("");
        age.setText("");
        gender.setText("");
        mobile.setText("");
        email.setText("");
    }

    private void setupHeader() {

        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.headerUserName);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) return;

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(auth.getCurrentUser().getUid())
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

    // ------------------------------------------------------------------
    // SIDE NAVIGATION
    // ------------------------------------------------------------------
    private void setupSideNav() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home)
                startActivity(new Intent(this, MainActivity.class));

            else if (id == R.id.menu_events)
                startActivity(new Intent(this, MyEventsActivity.class));

            else if (id == R.id.menu_profile)
                startActivity(new Intent(this, ProfileActivity.class));

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

    // ------------------------------------------------------------------
    // BOTTOM NAVIGATION
    // ------------------------------------------------------------------
    private void setupBottomNav() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)
                startActivity(new Intent(this, MainActivity.class));

            else if (id == R.id.nav_events)
                startActivity(new Intent(this, AddEventActivity.class));

            else if (id == R.id.nav_profile)
                startActivity(new Intent(this, ProfileActivity.class));

            return true;
        });
    }
}
