//// File: EditEventActivity.java
//
//package com.example.eventmanagment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EditEventActivity extends AppCompatActivity {
//
//    RecyclerView recyclerView;
//    List<MainActivity.Event> eventList = new ArrayList<>();
//    EventAdapter adapter;
//
//    DatabaseReference databaseReference;
//    String currentUserId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_edit_event);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        recyclerView = findViewById(R.id.recyclerEvents);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("Events");
//        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//
//        adapter = new EventAdapter(eventList, new EventAdapter.OnItemClickListener() {
//
//            @Override
//            public void onEditClick(int position) {
//                MainActivity.Event event = eventList.get(position);
//                openEditScreen(event);
//            }
//
//            @Override
//            public void onDeleteClick(int position) {
//                MainActivity.Event event = eventList.get(position);
//                deleteEvent(event.getId());
//            }
//        });
//
//        recyclerView.setAdapter(adapter);
//
//        loadMyEvents();
//    }
//
//    // -------------------------------------------------------------------
//    // LOAD ONLY CURRENT USER EVENTS
//    // -------------------------------------------------------------------
//    private void loadMyEvents() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                eventList.clear();
//
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    MainActivity.Event event = data.getValue(MainActivity.Event.class);
//
//                    if (event != null && event.getOwnerId() != null &&
//                            event.getOwnerId().equals(currentUserId)) {
//
//                        event.setId(data.getKey());
//                        eventList.add(event);
//                    }
//                }
//
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(EditEventActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // -------------------------------------------------------------------
//    // OPEN ADD/EDIT SCREEN WITH EVENT DATA
//    // -------------------------------------------------------------------
//    private void openEditScreen(MainActivity.Event event) {
//        Intent intent = new Intent(EditEventActivity.this, AddEventActivity.class);
//
//        intent.putExtra("eventId", event.getId());
//        intent.putExtra("title", event.getTitle());
//        intent.putExtra("description", event.getDescription());
//        intent.putExtra("startDate", event.getStartDate());
//        intent.putExtra("endDate", event.getEndDate());
//        intent.putExtra("venue", event.getVenue());
//        intent.putExtra("ownerId", event.getOwnerId());
//
//        startActivity(intent);
//    }
//
//    // -------------------------------------------------------------------
//    // DELETE EVENT
//    // -------------------------------------------------------------------
//    private void deleteEvent(String eventId) {
//        databaseReference.child(eventId)
//                .removeValue()
//                .addOnSuccessListener(aVoid ->
//                        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show()
//                )
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
//                );
//    }
//}

package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditEventActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etStartDate, etEndDate, etVenue;
    private Button btnUpdate;

    private String eventId;
    private FirebaseUser currentUser;
    private DatabaseReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Initialize UI
//        etTitle = findViewById(R.id.etTitle);
//        etDescription = findViewById(R.id.etDescription);
//        etStartDate = findViewById(R.id.etStartDate);
//        etEndDate = findViewById(R.id.etEndDate);
//        etVenue = findViewById(R.id.etVenue);
//        btnUpdate = findViewById(R.id.btnUpdateEvent);
        etTitle = findViewById(R.id.editEventName);
        etDescription = findViewById(R.id.editEventDescription);
        etStartDate = findViewById(R.id.editEventDate);
        etEndDate = findViewById(R.id.editEventTime);
        btnUpdate = findViewById(R.id.btnUpdateEvent);


        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        eventRef = db.getReference("Events");

        // Get eventId from intent
        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null) {
            Toast.makeText(this, "Invalid Event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEventData();  // Load existing data

        btnUpdate.setOnClickListener(v -> updateEvent());
    }

    private void loadEventData() {
        eventRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event e = snapshot.getValue(Event.class);

                if (e == null) {
                    Toast.makeText(EditEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Ensure only the owner can edit
                if (currentUser == null || !e.getOrganizer().equals(currentUser.getUid())) {
                    Toast.makeText(EditEventActivity.this, "You cannot edit this event", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Populate fields
                etTitle.setText(e.getTitle());
                etDescription.setText(e.getDescription());
                etStartDate.setText(e.getStartDate());
                etEndDate.setText(e.getEndDate());
                etVenue.setText(e.getVenue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditEventActivity.this, "Failed to load event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEvent() {
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || venue.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        eventRef.child(eventId).child("Title").setValue(title);
        eventRef.child(eventId).child("Description").setValue(desc);
        eventRef.child(eventId).child("StartDate").setValue(startDate);
        eventRef.child(eventId).child("EndDate").setValue(endDate);
        eventRef.child(eventId).child("Venue").setValue(venue)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error ->
                        Toast.makeText(EditEventActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    // Event model used for reading
    public static class Event {
        private String Title, Description, StartDate, EndDate, Venue, Organizer;

        public Event() {}

        public String getTitle() { return Title; }
        public String getDescription() { return Description; }
        public String getStartDate() { return StartDate; }
        public String getEndDate() { return EndDate; }
        public String getVenue() { return Venue; }
        public String getOrganizer() { return Organizer; }
    }
}
