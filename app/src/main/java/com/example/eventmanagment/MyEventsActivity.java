package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmanagment.MainActivity.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyEventsActivity extends AppCompatActivity {

    private LinearLayout eventsContainer;
    private ArrayList<Event> myEventList;

    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        // ----------------------------- UI INIT -----------------------------
        eventsContainer = findViewById(R.id.eventsContainer);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        myEventList = new ArrayList<>();

        // ----------------------------- FIREBASE INIT -----------------------------
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Events");

        if (currentUser != null) {
            updateDrawerHeaderUserName();
            fetchMyEvents();
        } else {
            Toast.makeText(this, "Please login to view your events", Toast.LENGTH_SHORT).show();
        }

        // ----------------------------- HAMBURGER MENU -----------------------------
        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // ----------------------------- SIDE NAVIGATION -----------------------------
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_home) {
                startActivity(new Intent(MyEventsActivity.this, MainActivity.class));
            }
            else if (id == R.id.menu_events) {
                startActivity(new Intent(MyEventsActivity.this, MyEventsActivity.class));
            }
            else if (id == R.id.menu_profile) {
                startActivity(new Intent(MyEventsActivity.this, ProfileActivity.class));
            }
            else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MyEventsActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            drawerLayout.closeDrawers();
            return true;
        });

        // ----------------------------- BOTTOM NAVIGATION -----------------------------
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(MyEventsActivity.this, MainActivity.class));
                return true;
            }
            else if (id == R.id.menu_participation) {
                // Open participation activity
                startActivity(new Intent(this, MyParticipationActivity.class));
            }
            else if (id == R.id.nav_events) {
                startActivity(new Intent(MyEventsActivity.this, AddEventActivity.class));
                return true;
            }
            else if (id == R.id.nav_profile) {
                startActivity(new Intent(MyEventsActivity.this, ProfileActivity.class));
                return true;
            }

            return true;
        });

    }

    // ----------------------------- UPDATE DRAWER HEADER -----------------------------
    private void updateDrawerHeaderUserName() {

        View header = navigationView.getHeaderView(0);
        TextView headerName = header.findViewById(R.id.headerUserName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            headerName.setText("User");
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Name").getValue(String.class);
                headerName.setText(name != null ? name : "User");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    // ----------------------------- FETCH USER EVENTS -----------------------------
    private void fetchMyEvents() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myEventList.clear();

                if (eventsContainer.getChildCount() > 1) {
                    eventsContainer.removeViews(1, eventsContainer.getChildCount() - 1);
                }

                for (DataSnapshot data : snapshot.getChildren()) {

                    Event e = data.getValue(Event.class);
                    if (e == null) continue;

                    if (e.getOwnerId() != null &&
                            e.getOwnerId().equals(currentUser.getUid())) {

                        e.setId(data.getKey());
                        myEventList.add(e);
                        addEventCard(e);
                    }
                }

                if (myEventList.isEmpty()) {
                    Toast.makeText(MyEventsActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ----------------------------- ADD EVENT CARD -----------------------------
    private void addEventCard(Event event) {

        View card = getLayoutInflater().inflate(R.layout.activity_event_item, eventsContainer, false);

        TextView title = card.findViewById(R.id.eventTitle);
        TextView desc = card.findViewById(R.id.eventDescription);
        TextView venue = card.findViewById(R.id.eventVenue);
        TextView startDate = card.findViewById(R.id.eventStartDate);
        TextView endDate = card.findViewById(R.id.eventEndDate);
        TextView starttime = card.findViewById(R.id.eventstrtTime);
        TextView endtime = card.findViewById(R.id.eventendtTime);

        ImageView edit = card.findViewById(R.id.btnEdit);
        ImageView delete = card.findViewById(R.id.btnDelete);

        Button viewAttendee = card.findViewById(R.id.btnViewAttendee);

        // set data
        title.setText(event.getTitle());
        desc.setText(event.getDescription());
        venue.setText("Venue: " + event.getVenue());
        startDate.setText("Start Date: " + event.getStartDate());
        endDate.setText("End Date: " + event.getEndDate());
        starttime.setText("Time: " + event.getStartTime());
        endtime.setText("Time: " + event.getEndTime());

        // VIEW ATTENDEE BUTTON CLICK
        viewAttendee.setOnClickListener(v -> {
            Intent i = new Intent(MyEventsActivity.this, AttendeeListing.class);
            i.putExtra("eventId", event.getId());
            startActivity(i);
        });

        // Edit event
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventsActivity.this, EditEventActivity.class);
            intent.putExtra("eventId", event.getId());
            startActivity(intent);
        });

        // Delete event
        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(MyEventsActivity.this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (event.getId() != null) {
                            databaseReference.child(event.getId()).removeValue()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(MyEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(MyEventsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        eventsContainer.addView(card);
    }
}
