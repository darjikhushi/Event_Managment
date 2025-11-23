package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.widget.Toast;
import com.example.eventmanagment.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView eventListView;
    private FloatingActionButton addEventFab;
    private FloatingActionButton myevent;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    ArrayList<Event> eventList = new ArrayList<>();
    EventAdapter adapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView ivMenu;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventListView = findViewById(R.id.eventListView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigationView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new EventAdapter();
        eventListView.setAdapter(adapter);
        // Get header view
        View headerView = navigationView.getHeaderView(0);

// Access header items
        TextView headerName = headerView.findViewById(R.id.headerUserName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String name = snapshot.child("Name").getValue(String.class);


                    if (name != null) headerName.setText(name);
                    else headerName.setText("User");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

// Set default (optional)
        headerName.setText("Loading...");


        showEvents();
        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_home) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
            else if (id == R.id.menu_events) {
                startActivity(new Intent(MainActivity.this, MyEventsActivity.class));
            }
            else if (id == R.id.menu_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
            else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();

                // CLEAR SESSION
                SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
                sp.edit().clear().apply();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }


            drawerLayout.closeDrawers();
            return true;
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                return true;
                // action
            } else if (id == R.id.nav_events) {
                startActivity(new Intent(MainActivity.this, AddEventActivity.class));
                return true;
                // action
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
                // action
            }
            return true;




        });

//        addEventFab.setOnClickListener(v -> {
//            if (currentUser != null) {
//                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
//                startActivity(intent);
//            } else {
//                Toast.makeText(MainActivity.this, "Please login to add events", Toast.LENGTH_SHORT).show();
//            }
//        });
//        myevent.setOnClickListener(v -> {
//            if (currentUser != null) {
//                Intent intent = new Intent(MainActivity.this, MyEventsActivity.class);
//                startActivity(intent);
//            } else {
//                Toast.makeText(MainActivity.this, "Please login to see your events", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void showEvents() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Event e = data.getValue(Event.class);
                    if (e != null) e.setId(data.getKey());
                    eventList.add(e);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Corrected Event class matching Firebase fields
    public static class Event {
        private String id;

        @PropertyName("Title")
        private String title;

        @PropertyName("Description")
        private String description;

        @PropertyName("StartDate")
        private String startDate;

        @PropertyName("EndDate")
        private String endDate;

        @PropertyName("Venue")
        private String venue;

//        @PropertyName("Organizer")
//        private String organizer;
        @PropertyName("OwnerId")
        private String OwnerId;

        public Event() {
            // Empty constructor for Firebase
        }

        public Event(String title, String description, String startDate, String endDate, String venue, String OwnerId) {
            this.title = title;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.venue = venue;
//            this.organizer = organizer;
            this.OwnerId=OwnerId;
        }

        // Getters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getVenue() { return venue; }
//        public String getOrganizer() { return organizer; }
        public String getOwnerId() { return OwnerId; }

        public void setOwnerId(String OwnerId) {   this.OwnerId = OwnerId; }
        // Setters
        public void setId(String id) { this.id = id; }
        public void setTitle(String title) { this.title = title; }
        public void setDescription(String description) { this.description = description; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public void setVenue(String venue) { this.venue = venue; }
//        public void setOrganizer(String organizer) { this.organizer = organizer; }

        public String getDate() {
            return null;
        }
    }

    // ✅ Updated Adapter class
    private class EventAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return eventList.size();
        }

        @Override
        public Object getItem(int i) {
            return eventList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_event_item, parent, false);
            }

            Event event = eventList.get(position);

            TextView title = convertView.findViewById(R.id.tvEventTitle);
            TextView date = convertView.findViewById(R.id.tvEventDate);
            TextView desc = convertView.findViewById(R.id.tvEventDescription);
            ImageButton edit = convertView.findViewById(R.id.ivEdit);
            ImageButton delete = convertView.findViewById(R.id.ivDelete);

            title.setText(event.getTitle() != null ? event.getTitle() : "(No Title)");
            date.setText("Date: " + (event.getStartDate() != null ? event.getStartDate() : "-"));
            desc.setText(event.getDescription() != null ? event.getDescription() : "");

            // Show edit/delete only for creator
            if (currentUser != null && event.getOwnerId() != null && event.getOwnerId().equals(currentUser.getUid())) {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }

            edit.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Edit: " + event.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: open edit screen
            });

            delete.setOnClickListener(v -> {
                if (event.getId() != null) {
                    databaseReference.child(event.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Event deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Delete failed", Toast.LENGTH_SHORT).show());
                }
            });

            return convertView;
        }
    }
}
