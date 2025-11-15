package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.eventmanagment.MainActivity.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyEventsActivity extends AppCompatActivity {

    private LinearLayout eventsContainer;
    private ArrayList<Event> myEventList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        eventsContainer = findViewById(R.id.eventsContainer);
        myEventList = new ArrayList<>();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            fetchMyEvents();
        } else {
            Toast.makeText(this, "Please login to view your events", Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchMyEvents() {

        Toast.makeText(this, "Fetching events...", Toast.LENGTH_SHORT).show();
        System.out.println("DEBUG: fetchMyEvents() called");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println("DEBUG: onDataChange() triggered. Snapshot exists = " + snapshot.exists());
                System.out.println("DEBUG: Total events in database = " + snapshot.getChildrenCount());

                myEventList.clear();
                eventsContainer.removeAllViews();

                if (!snapshot.exists()) {
                    System.out.println("DEBUG: No data inside Events node.");
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {

                    System.out.println("----------------------------------------------------");
                    System.out.println("DEBUG: Raw event snapshot = " + data);

                    Event e = data.getValue(Event.class);

                    System.out.println("DEBUG: Event object mapped = " + e);

                    if (e == null) {
                        System.out.println("DEBUG: Event is NULL — mapping failed! Check Firebase keys.");
                        continue;
                    }

                    System.out.println("DEBUG: Event Title = " + e.getTitle());
                    System.out.println("DEBUG: Event OwnerId = " + e.getOwnerId());
                    System.out.println("DEBUG: CurrentUser = " + currentUser.getUid());

                    // CHECK IF OWNER ID IS NULL
                    if (e.getOwnerId() == null) {
                        System.out.println("DEBUG: ownerId is NULL in this event!");
                        continue;
                    }

                    // FILTER EVENTS BELONGING TO LOGGED-IN USER
                    if (e.getOwnerId().equals(currentUser.getUid())) {

                        System.out.println("DEBUG: This event belongs to current user → ADDING");

                        e.setId(data.getKey());
                        myEventList.add(e);
                        addEventCard(e);

                    } else {
                        System.out.println("DEBUG: This event belongs to ANOTHER USER → SKIPPED");
                    }
                }

                System.out.println("DEBUG: Total events found for this user = " + myEventList.size());

                if (myEventList.isEmpty()) {
                    Toast.makeText(MyEventsActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
                System.out.println("DEBUG: Firebase error: " + error.getMessage());
            }
        });
    }


//    private void fetchMyEvents() {
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                myEventList.clear();
//                eventsContainer.removeAllViews();
//
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Event e = data.getValue(Event.class);
//                    if (e != null && e.getOwnerId() != null &&
//                            e.getOwnerId().equals(currentUser.getUid())) {
//
//                        e.setId(data.getKey());
//                        myEventList.add(e);
//
//                        addEventCard(e);
//                    }
//                }
//
//                if (myEventList.isEmpty()) {
//                    Toast.makeText(MyEventsActivity.this, "No events found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(MyEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void addEventCard(Event event) {
        View card = getLayoutInflater().inflate(R.layout.activity_event_item, eventsContainer, false);

        TextView title = card.findViewById(R.id.tvEventTitle);
        TextView date = card.findViewById(R.id.tvEventDate);
        TextView desc = card.findViewById(R.id.tvEventDescription);
        ImageButton edit = card.findViewById(R.id.ivEdit);
        ImageButton delete = card.findViewById(R.id.ivDelete);

        title.setText(event.getTitle());
        date.setText("Date: " + event.getStartDate());
        desc.setText(event.getDescription());

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventsActivity.this, EditEventActivity.class);
            intent.putExtra("eventId", event.getId());
            intent.putExtra("OwnerId", event.getOwnerId());
//            intent.putExtra("title", event.getTitle());
//            intent.putExtra("desc", event.getDescription());
//            intent.putExtra("startDate", event.getStartDate());
//            intent.putExtra("endDate", event.getEndDate());
//            intent.putExtra("venue", event.getVenue()
//            );
            startActivity(intent);
        });

        delete.setOnClickListener(v -> {
            if (event.getId() != null) {
                databaseReference.child(event.getId()).removeValue()
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(MyEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(MyEventsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show());
            }
        });

        eventsContainer.addView(card);
    }

}
