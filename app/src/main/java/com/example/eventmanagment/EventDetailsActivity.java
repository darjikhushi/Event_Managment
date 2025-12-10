package com.example.eventmanagment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetailsActivity extends AppCompatActivity {

    ImageView image;
    TextView title, description, venue, startDate, endDate, time;

    // NEW UI components for drawer + bottom nav
    DrawerLayout drawerLayout;
    ImageView ivMenu;
    NavigationView navigationView;
    BottomNavigationView bottom_navigation;

    Button participateButton;

    DatabaseReference databaseReference;
    String eventId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        image = findViewById(R.id.detailEventImage);
        title = findViewById(R.id.detailEventTitle);
        description = findViewById(R.id.detailEventDescription);
        venue = findViewById(R.id.detailEventVenue);
        startDate = findViewById(R.id.detailEventStartDate);
        endDate = findViewById(R.id.detailEventEndDate);
        participateButton = findViewById(R.id.participateButton);

        // -------- Drawer + Bottom Nav INIT ----------------
        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigationView);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        // Menu button click
        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            else drawerLayout.openDrawer(Gravity.LEFT);
        });

        setupHeader();
        setupSideNav();
        setupBottomNav();
        // ---------------------------------------------------

        // Receive event ID
        eventId = getIntent().getStringExtra("id");
        Log.d("DEBUG_INTENT", "Received eventId = " + eventId);

        if (eventId == null || eventId.isEmpty()) {
            Log.e("DEBUG_ERROR", "Event ID NOT RECEIVED!!");
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Events");

        loadEventDetails();

        participateButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, ParticipationActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putExtra("eventTitle", title.getText().toString());
            startActivity(intent);
        });
    }

    // ------------------------------------------
    // LOAD EVENT DETAILS
    // ------------------------------------------
    private void loadEventDetails() {

        databaseReference.child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {

                        if (!data.exists()) {
                            Toast.makeText(EventDetailsActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String eventTitle = data.child("Title").getValue(String.class);
                        String eventDesc = data.child("Description").getValue(String.class);
                        String eventVenue = data.child("Venue").getValue(String.class);
                        String eventStart = data.child("StartDate").getValue(String.class);
                        String eventEnd = data.child("EndDate").getValue(String.class);
                        String eventStartTime = data.child("StartTime").getValue(String.class);
                        String eventEndTime = data.child("EndTime").getValue(String.class);
                        String eventImage = data.child("ImageBase64").getValue(String.class);

                        // Set UI
                        title.setText(eventTitle);
                        description.setText(eventDesc);
                        venue.setText("Venue: " + eventVenue);
                        startDate.setText("Start Date: " + eventStart + " at " + eventStartTime);
                        endDate.setText("End Date: " + eventEnd + " till " + eventEndTime);

                        if (eventImage != null && !eventImage.isEmpty()) {
                            Glide.with(EventDetailsActivity.this)
                                    .load(eventImage)
                                    .into(image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DEBUG_ERROR", "Firebase error: " + error.getMessage());
                    }
                });
    }

    // ---------------------------------------------------
    // SHOW USER NAME IN DRAWER HEADER
    // ---------------------------------------------------
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

    // ---------------------------------------------------
    // SIDE NAVIGATION MENU
    // ---------------------------------------------------
    private void setupSideNav() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home)
                startActivity(new Intent(this, MainActivity.class));

            else if (id == R.id.menu_participation) {
                // Open participation activity
                startActivity(new Intent(this, MyParticipationActivity.class));
            }
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

    // ---------------------------------------------------
    // BOTTOM NAVIGATION
    // ---------------------------------------------------
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
