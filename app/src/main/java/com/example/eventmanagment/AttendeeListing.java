package com.example.eventmanagment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AttendeeListing extends AppCompatActivity {

    RecyclerView attendeesRecyclerView;
    AttendeeAdapter attendeeAdapter;
    ArrayList<AttendeeModel> attendeeList;
    DatabaseReference dbRef;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView ivMenu;
    BottomNavigationView bottomNavigationView;

    String eventTitle; // Event title to filter attendees

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_listing);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // -----------------------------
        // Initialize Drawer & Bottom Nav
        // -----------------------------
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ivMenu = findViewById(R.id.ivMenu);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        setupHeader();
        setupNavigation();
        setupBottomNav();

        // -----------------------------
        // RecyclerView Setup
        // -----------------------------
        attendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendeeList = new ArrayList<>();
        attendeeAdapter = new AttendeeAdapter(this, attendeeList);
        attendeesRecyclerView.setAdapter(attendeeAdapter);

        // -----------------------------
        // Firebase Setup
        // -----------------------------
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Get event title from Intent
        String currentEventID = getIntent().getStringExtra("eventId");
        Log.d("Debug","Name of event :"+currentEventID);
        if (currentEventID != null) {
            fetchAttendeesByEventID(currentEventID);
        }

    }

    // -----------------------------
    // Drawer Header Setup (User Name)
    // -----------------------------
    private void setupHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.headerUserName);

        if (FirebaseDatabase.getInstance() == null) return;

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("Name").getValue(String.class);
                        if (name != null) headerName.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    // -----------------------------
    // Drawer Menu Actions
    // -----------------------------
    private void setupNavigation() {
        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_events) startActivity(new Intent(this, MyEventsActivity.class));
            else if (id == R.id.menu_participation) {
                // Open participation activity
                startActivity(new Intent(this, MyParticipationActivity.class));
            } else if (id == R.id.menu_profile) startActivity(new Intent(this, ProfileActivity.class));
            else if (id == R.id.menu_logout) {
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
                sp.edit().clear().apply();
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // -----------------------------
    // Bottom Navigation Actions
    // -----------------------------
    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_events) startActivity(new Intent(this, AddEventActivity.class));
            else if (id == R.id.nav_profile) startActivity(new Intent(this, ProfileActivity.class));

            return true;
        });
    }

    // -----------------------------
    // Fetch Attendees by Event Title
    // -----------------------------

    private void fetchAttendeesByEventID(String eventID) {
        // Step 1: Get event title from "Events" table using eventID
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events");
        eventRef.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String eventTitle = snapshot.child("Title").getValue(String.class);

                    if (eventTitle != null) {
                        // Step 2: Fetch attendees for this event
                        fetchAttendeesByEventTitle(eventTitle);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    // Step 2: Fetch attendees by event title
    private void fetchAttendeesByEventTitle(String eventTitle) {
        DatabaseReference attendeeRef = FirebaseDatabase.getInstance().getReference("Attendee");

        Query query = attendeeRef.orderByChild("eventTitle").equalTo(eventTitle);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendeeList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    AttendeeModel attendee = ds.getValue(AttendeeModel.class);
                    if (attendee != null) attendeeList.add(attendee);
                }

                attendeeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

}


