package com.example.eventmanagment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.Calendar;
import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {

    EditText eventTitle, eventDescription, startDate, endDate, venue;
    Button saveEventButton;
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

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        auth = FirebaseAuth.getInstance();

        startDate.setOnClickListener(v -> showDatePicker(startDate));
        endDate.setOnClickListener(v -> showDatePicker(endDate));

        saveEventButton.setOnClickListener(v -> saveEvent());
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
