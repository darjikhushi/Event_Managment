package com.example.eventmanagment;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyParticipationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<MainActivity.Event> participatedEvents = new ArrayList<>();
    EventSliderAdapter sliderAdapter;

    DatabaseReference attendeeRef, eventRef;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_participation);

        recyclerView = findViewById(R.id.recyclerViewParticipation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        attendeeRef = FirebaseDatabase.getInstance().getReference("Attendee");
        eventRef = FirebaseDatabase.getInstance().getReference("Events");

        fetchParticipatedEvents();
    }

    private void fetchParticipatedEvents() {
        if (currentUser == null) return;

        // Step 1: Get all attendees for current user
        Query query = attendeeRef.orderByChild("userId").equalTo(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> eventIds = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String eventTitle = ds.child("eventTitle").getValue(String.class);
                    if (eventTitle != null && !eventIds.contains(eventTitle)) {
                        eventIds.add(eventTitle);
                    }
                }

                // Step 2: Fetch event details for each event title
                fetchEventsByTitles(eventIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchEventsByTitles(ArrayList<String> eventTitles) {
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                participatedEvents.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String title = ds.child("Title").getValue(String.class);
                    if (title != null && eventTitles.contains(title)) {
                        MainActivity.Event event = ds.getValue(MainActivity.Event.class);
                        if (event != null) {
                            event.setId(ds.getKey());
                            participatedEvents.add(event);
                        }
                    }
                }

                // Step 3: Show in RecyclerView / ViewPager
                sliderAdapter = new EventSliderAdapter(MyParticipationActivity.this, participatedEvents, null);
                recyclerView.setAdapter(sliderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
