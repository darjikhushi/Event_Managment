package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Calendar;
import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {

    EditText eventTitle, eventDescription, startDate, endDate, venue;
    Button saveEventButton;
    ImageView ivMenu;
    FirebaseUser currentUser;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        venue = findViewById(R.id.venue);
        saveEventButton = findViewById(R.id.saveEventButton);

        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);

        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        auth = FirebaseAuth.getInstance();

        startDate.setOnClickListener(v -> showDatePicker(startDate));
        endDate.setOnClickListener(v -> showDatePicker(endDate));

        saveEventButton.setOnClickListener(v -> saveEvent());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            updateDrawerHeaderUserName(); // Set username dynamically
        } else {
            Toast.makeText(this, "Please login to view your Profile", Toast.LENGTH_SHORT).show();
        }

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
                startActivity(new Intent(AddEventActivity.this, MainActivity.class));
            }
            else if (id == R.id.menu_events) {
                startActivity(new Intent(AddEventActivity.this, MyEventsActivity.class));
            }
            else if (id == R.id.menu_profile) {
                startActivity(new Intent(AddEventActivity.this, ProfileActivity.class));
            }
            else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(AddEventActivity.this, LoginActivity.class);
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
                startActivity(new Intent(AddEventActivity.this, MainActivity.class));
                return true;
            }
            else if (id == R.id.nav_events) {
                startActivity(new Intent(AddEventActivity.this, AddEventActivity.class));
                return true;
            }
            else if (id == R.id.nav_profile) {
                startActivity(new Intent(AddEventActivity.this, ProfileActivity.class));
                return true;
            }

            return true;
        });



    }

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
                if (snapshot.exists()) {
                    String name = snapshot.child("Name").getValue(String.class);
                    if (name != null) {
                        headerName.setText(name);
                    } else {
                        headerName.setText("User");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showDatePicker(EditText field) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) ->
                        field.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void saveEvent() {
        String title = eventTitle.getText().toString().trim();
        String desc = eventDescription.getText().toString().trim();
        String start = startDate.getText().toString().trim();
        String end = endDate.getText().toString().trim();
        String place = venue.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || start.isEmpty() || place.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String ownerId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "UnknownUser";

        String eventId = databaseReference.push().getKey();

        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("Title", title);
        eventData.put("Description", desc);
        eventData.put("StartDate", start);
        eventData.put("EndDate", end);
        eventData.put("Venue", place);
        eventData.put("OwnerId", ownerId); // Organizer replaced

        databaseReference.child(eventId).setValue(eventData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event Added Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
