package com.example.eventmanagment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eventmanagment.MainActivity.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditEventActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etStartDate, etEndDate, etVenue, etStartTime, etEndTime;
    private ImageView eventImage;
    private Button btnUpdate, btnChooseImage;

    private String eventId;
    private FirebaseUser currentUser;
    private DatabaseReference eventRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventId = getIntent().getStringExtra("eventId");

        // UI
        etTitle = findViewById(R.id.editEventName);
        etDescription = findViewById(R.id.editEventDescription);
        etStartDate = findViewById(R.id.editEvenstrtDate);
        etEndDate = findViewById(R.id.editEvenendtDate);
        etVenue = findViewById(R.id.editVenue);

        etStartTime = findViewById(R.id.editStartTime);
        etEndTime = findViewById(R.id.editEndTime);

        eventImage = findViewById(R.id.editEventImage);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUpdate = findViewById(R.id.btnUpdateEvent);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        eventRef = FirebaseDatabase.getInstance().getReference("Events");

        if (eventId == null) {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEventData();

        btnChooseImage.setOnClickListener(v -> openImagePicker());

        btnUpdate.setOnClickListener(v -> updateEvent());
    }

    private void loadEventData() {
        eventRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event e = snapshot.getValue(Event.class);

                if (e == null) {
                    Toast.makeText(EditEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!e.getOwnerId().equals(currentUser.getUid())) {
                    Toast.makeText(EditEventActivity.this, "Not authorized", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                etTitle.setText(e.getTitle());
                etDescription.setText(e.getDescription());
                etStartDate.setText(e.getStartDate());
                etEndDate.setText(e.getEndDate());
                etVenue.setText(e.getVenue());

                etStartTime.setText(e.getStartTime());
                etEndTime.setText(e.getEndTime());

                if (e.getImageBase64() != null) {
                    Glide.with(EditEventActivity.this)
                            .load(e.getImageBase64())
                            .into(eventImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditEventActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateEvent() {
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || startDate.isEmpty() || endDate.isEmpty()
                || venue.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        eventRef.child(eventId).child("Title").setValue(title);
        eventRef.child(eventId).child("Description").setValue(desc);
        eventRef.child(eventId).child("StartDate").setValue(startDate);
        eventRef.child(eventId).child("EndDate").setValue(endDate);
        eventRef.child(eventId).child("Venue").setValue(venue);
        eventRef.child(eventId).child("StartTime").setValue(startTime);
        eventRef.child(eventId).child("EndTime").setValue(endTime);

        if (imageUri != null)
            uploadImage();
        else {
            Toast.makeText(this, "Event Updated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MyEventsActivity.class));
        }
    }

    private void uploadImage() {
        StorageReference imgRef = FirebaseStorage.getInstance()
                .getReference("EventImages/" + eventId + ".jpg");

        imgRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    eventRef.child(eventId).child("ImageUrl").setValue(uri.toString());
                    Toast.makeText(this, "Event Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MyEventsActivity.class));
                })
        );
    }

    // Image Picker
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePicker.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() != null && result.getResultCode() == RESULT_OK) {
                    imageUri = result.getData().getData();
                    eventImage.setImageURI(imageUri);
                }
            });
}
