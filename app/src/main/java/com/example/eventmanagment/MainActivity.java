package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager2 eventSlider;
    EventSliderAdapter sliderAdapter;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView ivMenu;
    BottomNavigationView bottomNavigationView;

    FirebaseDatabase firebaseDatabase;
    static DatabaseReference databaseReference;
    static FirebaseUser currentUser;

    ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // -------------------------------------------------
        // 🔐 LOGIN CHECK (VERY IMPORTANT)
        // -------------------------------------------------
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // If NOT logged in → redirect to LoginActivity
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        // -------------------------------------------------
        // INITIALIZATION
        // -------------------------------------------------
        eventSlider = findViewById(R.id.eventSlider);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ivMenu = findViewById(R.id.ivMenu);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        setupHeader();
        setupNavigation();
        setupBottomNav();
        loadEvents();
    }

    // -------------------------------------------------
    // NAVIGATION DRAWER HEADER (User Name)
    // -------------------------------------------------
    private void setupHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.headerUserName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                headerName.setText(snapshot.child("Name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // -------------------------------------------------
    // DRAWER MENU ACTIONS
    // -------------------------------------------------
    private void setupNavigation() {

        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_events) {
                startActivity(new Intent(this, MyEventsActivity.class));
            } else if (id == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();

                SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
                sp.edit().clear().apply();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // -------------------------------------------------
    // BOTTOM NAVIGATION ACTIONS
    // -------------------------------------------------
    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            } else if (id == R.id.nav_events) {
                startActivity(new Intent(MainActivity.this, AddEventActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }

            return true;
        });
    }

    // -------------------------------------------------
    // LOAD EVENTS FROM FIREBASE
    // -------------------------------------------------
    private void loadEvents() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Event event = data.getValue(Event.class);
                    if (event != null) {
                        event.setId(data.getKey());
                        eventList.add(event);
                    }
                }

                sliderAdapter = new EventSliderAdapter(MainActivity.this, eventList,
                        new EventSliderAdapter.OnEventClickListener() {
                            @Override
                            public void onEdit(Event event) {
                                Intent i = new Intent(MainActivity.this, EditEventActivity.class);
                                i.putExtra("eventId", event.getId());
                                startActivity(i);
                            }

                            @Override
                            public void onDelete(Event event) {
                                databaseReference.child(event.getId()).removeValue()
                                        .addOnSuccessListener(a -> Toast.makeText(MainActivity.this, "Event deleted", Toast.LENGTH_SHORT).show());
                            }
                        });

                eventSlider.setAdapter(sliderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // -------------------------------------------------
    // EVENT MODEL (FINAL)
    // -------------------------------------------------
    public static class Event {

        private String id;

        @PropertyName("Title")
        private String title;

        @PropertyName("Description")
        private String description;

        @PropertyName("eventId")
        private String eventId;

        @PropertyName("StartDate")
        private String startDate;

        @PropertyName("EndDate")
        private String endDate;

        @PropertyName("StartTime")
        private String startTime;

        @PropertyName("EndTime")
        private String endTime;

        @PropertyName("Venue")
        private String venue;

        @PropertyName("Image")
        private String imageBase64;

        @PropertyName("OwnerId")
        private String ownerId;

        public Event() {}

        public String getEventId() {return eventId;}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getVenue() { return venue; }
        public String getImageBase64() { return imageBase64; }
        public String getOwnerId() { return ownerId; }

//        public Bitmap getImageBitmap() {
//    if (imageBase64 == null || imageBase64.isEmpty()) {
//        return null;
//    }
//    byte[] bytes = Base64.decode(imageBase64, Base64.DEFAULT);
//    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//}
    }
}

