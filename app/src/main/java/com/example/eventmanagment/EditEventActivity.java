//package com.example.eventmanagment;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class EditEventActivity extends AppCompatActivity {
//
//    Intent intent = getIntent();
//
//    private EditText etTitle, etDescription, etStartDate, etEndDate, etVenue;
//    private Button btnUpdate;
//
//    private String eventId;
//    private FirebaseUser currentUser;
//    private DatabaseReference eventRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_event);
//
//        // Initialize UI
////        etTitle = findViewById(R.id.etTitle);
////        etDescription = findViewById(R.id.etDescription);
////        etStartDate = findViewById(R.id.etStartDate);
////        etEndDate = findViewById(R.id.etEndDate);
////        etVenue = findViewById(R.id.etVenue);
////        btnUpdate = findViewById(R.id.btnUpdateEvent);
//        etTitle = findViewById(R.id.editEventName);
//        etDescription = findViewById(R.id.editEventDescription);
//        etStartDate = findViewById(R.id.editEventDate);
//        etEndDate = findViewById(R.id.editEventTime);
//        btnUpdate = findViewById(R.id.btnUpdateEvent);
//
//
//        // Firebase
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        eventRef = db.getReference("Events");
//
//        // Get eventId from intent
//        eventId = getIntent().getStringExtra("eventId");
//
//        if (eventId == null) {
//            Toast.makeText(this, "Invalid Event", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        loadEventData();  // Load existing data
//
//        btnUpdate.setOnClickListener(v -> updateEvent());
//    }
//
//    private void loadEventData() {
//        eventRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Event e = snapshot.getValue(Event.class);
//
//                if (e == null) {
//                    Toast.makeText(EditEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
//                    finish();
//                    return;
//                }
//
//                // Ensure only the owner can edit
//                if (currentUser == null || !e.getOrganizer().equals(currentUser.getUid())) {
//                    Toast.makeText(EditEventActivity.this, "You cannot edit this event", Toast.LENGTH_SHORT).show();
//                    finish();
//                    return;
//                }
//
//                // Populate fields
//                etTitle.setText(e.getTitle());
//                etDescription.setText(e.getDescription());
//                etStartDate.setText(e.getStartDate());
//                etEndDate.setText(e.getEndDate());
//                etVenue.setText(e.getVenue());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(EditEventActivity.this, "Failed to load event", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void updateEvent() {
//        String title = etTitle.getText().toString().trim();
//        String desc = etDescription.getText().toString().trim();
//        String startDate = etStartDate.getText().toString().trim();
//        String endDate = etEndDate.getText().toString().trim();
//        String venue = etVenue.getText().toString().trim();
//
//        if (title.isEmpty() || desc.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || venue.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        eventRef.child(eventId).child("Title").setValue(title);
//        eventRef.child(eventId).child("Description").setValue(desc);
//        eventRef.child(eventId).child("StartDate").setValue(startDate);
//        eventRef.child(eventId).child("EndDate").setValue(endDate);
//        eventRef.child(eventId).child("Venue").setValue(venue)
//                .addOnSuccessListener(aVoid ->
//                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(error ->
//                        Toast.makeText(EditEventActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
//    }
//
//    // Event model used for reading
//    public static class Event {
//        private String Title, Description, StartDate, EndDate, Venue, Organizer;
//
//        public Event() {}
//
//        public String getTitle() { return Title; }
//        public String getDescription() { return Description; }
//        public String getStartDate() { return StartDate; }
//        public String getEndDate() { return EndDate; }
//        public String getVenue() { return Venue; }
//        public String getOrganizer() { return Organizer; }
//    }
//}

package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.eventmanagment.MainActivity.Event;
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

    private String eventId,ownerId;
    private FirebaseUser currentUser;
    private DatabaseReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        ownerId = intent.getStringExtra("OwnerId");
        System.out.println("DEBUG: Event ID = " + eventId);
        System.out.println("DEBUG: Owner ID = " + ownerId);

        // UI
        etTitle = findViewById(R.id.editEventName);
        etDescription = findViewById(R.id.editEventDescription);
        etStartDate = findViewById(R.id.editEventDate);
        etEndDate = findViewById(R.id.editEventTime);
        etVenue = findViewById(R.id.editVenue);
        btnUpdate = findViewById(R.id.btnUpdateEvent);

        // Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        eventRef = FirebaseDatabase.getInstance().getReference("Events");

        // -----------------------------------------
        // ✅ Step 1: Receive eventId using getStringExtra()
        // -----------------------------------------



        if (eventId == null) {
            Toast.makeText(this, "Error: Event ID not received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // -----------------------------------------
        // ✅ Step 2: Load event data using eventId
        // -----------------------------------------
        loadEventData();

        btnUpdate.setOnClickListener(v -> updateEvent());
    }

    // ---------------------------------------------
    // ✅ Fetch event from DB using eventId only
    // ---------------------------------------------
    private void loadEventData() {
        System.out.println("DEBUG:loadevent  " );
        eventRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("DEBUG:Onchange  " );
                Event e = snapshot.getValue(Event.class);
                System.out.println("DEBUG:loadevent  "+e.getTitle() );

                if (e == null) {
                    Toast.makeText(EditEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
//                    finish();
                    return;
                }

                // Only owner can edit
                if (currentUser == null || !e.getOwnerId().equals(currentUser.getUid())) {
                    Toast.makeText(EditEventActivity.this, "You cannot edit this event", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Fill data in UI
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

    // ---------------------------------------------------
    // ✅ Update event
    // ---------------------------------------------------
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
        Intent intent2 = new Intent(EditEventActivity.this,MyEventsActivity.class);

        eventRef.child(eventId).child("Venue").setValue(venue)
                .addOnSuccessListener(aVoid ->
                        startActivity(intent2))
//                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error -> startActivity(intent2));
//                        Toast.makeText(EditEventActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
    }

}
