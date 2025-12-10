//package com.example.eventmanagment;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.example.eventmanagment.MainActivity.Event;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//public class EditEventActivity extends AppCompatActivity {
//
//    private EditText etTitle, etDescription, etStartDate, etEndDate, etVenue, etStartTime, etEndTime;
//    private ImageView eventImage;
//    private Button btnUpdate, btnChooseImage;
//
//    private String eventId;
//    private FirebaseUser currentUser;
//    private DatabaseReference eventRef;
//    private Uri imageUri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_event);
//
//        eventId = getIntent().getStringExtra("eventId");
//
//        // UI
//        etTitle = findViewById(R.id.editEventName);
//        etDescription = findViewById(R.id.editEventDescription);
//        etStartDate = findViewById(R.id.editEvenstrtDate);
//        etEndDate = findViewById(R.id.editEvenendtDate);
//        etVenue = findViewById(R.id.editVenue);
//
//        etStartTime = findViewById(R.id.editStartTime);
//        etEndTime = findViewById(R.id.editEndTime);
//
//        eventImage = findViewById(R.id.editEventImage);
//        btnChooseImage = findViewById(R.id.btnChooseImage);
//        btnUpdate = findViewById(R.id.btnUpdateEvent);
//
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        eventRef = FirebaseDatabase.getInstance().getReference("Events");
//
//        if (eventId == null) {
//            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        loadEventData();
//
//        btnChooseImage.setOnClickListener(v -> openImagePicker());
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
//                    return;
//                }
//
//                if (!e.getOwnerId().equals(currentUser.getUid())) {
//                    Toast.makeText(EditEventActivity.this, "Not authorized", Toast.LENGTH_SHORT).show();
//                    finish();
//                    return;
//                }
//
//                etTitle.setText(e.getTitle());
//                etDescription.setText(e.getDescription());
//                etStartDate.setText(e.getStartDate());
//                etEndDate.setText(e.getEndDate());
//                etVenue.setText(e.getVenue());
//
//                etStartTime.setText(e.getStartTime());
//                etEndTime.setText(e.getEndTime());
//
//                if (e.getImageBase64() != null) {
//                    Glide.with(EditEventActivity.this)
//                            .load(e.getImageBase64())
//                            .into(eventImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(EditEventActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//
//    private void updateEvent() {
//        String title = etTitle.getText().toString().trim();
//        String desc = etDescription.getText().toString().trim();
//        String startDate = etStartDate.getText().toString().trim();
//        String endDate = etEndDate.getText().toString().trim();
//        String venue = etVenue.getText().toString().trim();
//        String startTime = etStartTime.getText().toString().trim();
//        String endTime = etEndTime.getText().toString().trim();
//
//        if (title.isEmpty() || desc.isEmpty() || startDate.isEmpty() || endDate.isEmpty()
//                || venue.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
//            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        eventRef.child(eventId).child("Title").setValue(title);
//        eventRef.child(eventId).child("Description").setValue(desc);
//        eventRef.child(eventId).child("StartDate").setValue(startDate);
//        eventRef.child(eventId).child("EndDate").setValue(endDate);
//        eventRef.child(eventId).child("Venue").setValue(venue);
//        eventRef.child(eventId).child("StartTime").setValue(startTime);
//        eventRef.child(eventId).child("EndTime").setValue(endTime);
//
//        if (imageUri != null)
//            uploadImage();
//        else {
//            Toast.makeText(this, "Event Updated", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, MyEventsActivity.class));
//        }
//    }
//
//    private void uploadImage() {
//        StorageReference imgRef = FirebaseStorage.getInstance()
//                .getReference("EventImages/" + eventId + ".jpg");
//
//        imgRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
//                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    eventRef.child(eventId).child("ImageUrl").setValue(uri.toString());
//                    Toast.makeText(this, "Event Updated", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(this, MyEventsActivity.class));
//                })
//        );
//    }
//
//    // Image Picker
//    private void openImagePicker() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        imagePicker.launch(intent);
//    }
//
//    private final ActivityResultLauncher<Intent> imagePicker =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                if (result.getData() != null && result.getResultCode() == RESULT_OK) {
//                    imageUri = result.getData().getData();
//                    eventImage.setImageURI(imageUri);
//                }
//            });
//}

package com.example.eventmanagment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.example.eventmanagment.MainActivity.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;

public class EditEventActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etStartDate, etEndDate, etVenue, etStartTime, etEndTime;
    private TextInputLayout layoutTitle, layoutDesc, layoutStartDate, layoutStartTime,
            layoutEndDate, layoutEndTime, layoutVenue;

    private ImageView eventImage, ivMenu;
    private TextView imageError;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottom_navigation;

    private Button btnUpdate, btnChooseImage;

    private String eventId;
    private FirebaseUser currentUser;
    private DatabaseReference eventRef;
    private Uri imageUri;
    private String base64Image = ""; // Base64 of selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventId = getIntent().getStringExtra("eventId");

        // ===============================
        // FIND VIEWS (IDs NOT CHANGED)
        // ===============================
        etTitle = findViewById(R.id.editEventName);
        etDescription = findViewById(R.id.editEventDescription);
        etStartDate = findViewById(R.id.editEvenstrtDate);
        etEndDate = findViewById(R.id.editEvenendtDate);
        etVenue = findViewById(R.id.editVenue);
        etStartTime = findViewById(R.id.editStartTime);
        etEndTime = findViewById(R.id.editEndTime);

        eventImage = findViewById(R.id.editEventImage);
        imageError = findViewById(R.id.imageErrorText);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ivMenu = findViewById(R.id.ivMenu);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUpdate = findViewById(R.id.btnUpdateEvent);

        // Parent TextInputLayouts
        layoutTitle = (TextInputLayout) etTitle.getParent().getParent();
        layoutDesc = (TextInputLayout) etDescription.getParent().getParent();
        layoutStartDate = (TextInputLayout) etStartDate.getParent().getParent();
        layoutStartTime = (TextInputLayout) etStartTime.getParent().getParent();
        layoutEndDate = (TextInputLayout) etEndDate.getParent().getParent();
        layoutEndTime = (TextInputLayout) etEndTime.getParent().getParent();
        layoutVenue = (TextInputLayout) etVenue.getParent().getParent();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        eventRef = FirebaseDatabase.getInstance().getReference("Events");

        if (eventId == null) {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // -------------------------
        // HEADER, SIDEBAR, BOTTOM NAV
        // -------------------------
        setupHeader();
        setupSideNav();
        setupBottomNav();

        // Load event data
        loadEventData();

        // Image picker
        btnChooseImage.setOnClickListener(v -> openImagePicker());

        // Disable past dates
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));

        // Disable past times
        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));

        // Update button
        btnUpdate.setOnClickListener(v -> validateAndUpdate());
    }

    // ===================================================
    // LOAD EXISTING EVENT DATA
    // ===================================================
    private void loadEventData() {
        eventRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event e = snapshot.getValue(Event.class);

                if (e == null) return;

                if (!e.getOwnerId().equals(currentUser.getUid())) {
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

                if (e.getImageBase64() != null && !e.getImageBase64().isEmpty()) {
                    base64Image = e.getImageBase64();
                    Glide.with(EditEventActivity.this)
                            .load(e.getImageBase64())
                            .into(eventImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ===================================================
    // DATE PICKER - BLOCK PAST DATES
    // ===================================================
    private void showDatePicker(EditText field) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, y, m, d) -> field.setText(d + "/" + (m + 1) + "/" + y),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dp.getDatePicker().setMinDate(System.currentTimeMillis());
        dp.show();
    }

    // ===================================================
    // TIME PICKER - BLOCK PAST TIMES for Today
    // ===================================================
    private void showTimePicker(EditText field) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tp = new TimePickerDialog(
                this,
                (view, hour, minute) -> field.setText(String.format("%02d:%02d", hour, minute)),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tp.show();
    }

    // ===================================================
    // VALIDATION
    // ===================================================
    private void validateAndUpdate() {
        clearErrors();
        boolean valid = true;

        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String sDate = etStartDate.getText().toString().trim();
        String sTime = etStartTime.getText().toString().trim();
        String eDate = etEndDate.getText().toString().trim();
        String eTime = etEndTime.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();

        if (title.isEmpty()) { layoutTitle.setError("Title is required"); valid = false; }
        if (desc.isEmpty()) { layoutDesc.setError("Description is required"); valid = false; }
        if (sDate.isEmpty()) { layoutStartDate.setError("Start date required"); valid = false; }
        if (sTime.isEmpty()) { layoutStartTime.setError("Start time required"); valid = false; }
        if (eDate.isEmpty()) { layoutEndDate.setError("End date required"); valid = false; }
        if (eTime.isEmpty()) { layoutEndTime.setError("End time required"); valid = false; }
        if (venue.isEmpty()) { layoutVenue.setError("Venue required"); valid = false; }

        if ((base64Image.trim().isEmpty() && imageUri == null) && imageError != null) {
            imageError.setText("Event image is required");
            imageError.setVisibility(View.VISIBLE);
            valid = false;
        }

        if (!valid) return;

        // Show confirmation dialog
        new AlertDialog.Builder(EditEventActivity.this)
                .setTitle("Update Event")
                .setMessage("Do you want to update this event?")
                .setPositiveButton("Yes", (dialog, which) ->
                        performUpdate(title, desc, sDate, eDate, venue, sTime, eTime))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void clearErrors() {
        layoutTitle.setError(null);
        layoutDesc.setError(null);
        layoutStartDate.setError(null);
        layoutStartTime.setError(null);
        layoutEndDate.setError(null);
        layoutEndTime.setError(null);
        layoutVenue.setError(null);
        if (imageError != null) imageError.setVisibility(View.GONE);
    }

    // ===================================================
    // PERFORM UPDATE
    // ===================================================
    private void performUpdate(String title, String desc, String startDate,
                               String endDate, String venue, String startTime, String endTime) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("Title", title);
        map.put("Description", desc);
        map.put("StartDate", startDate);
        map.put("EndDate", endDate);
        map.put("Venue", venue);
        map.put("StartTime", startTime);
        map.put("EndTime", endTime);

        if (base64Image != null && !base64Image.isEmpty()) {
            map.put("ImageBase64", base64Image);
        }

        eventRef.child(eventId).updateChildren(map)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                    finishUpdate();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void finishUpdate() {
        startActivity(new Intent(this, MyEventsActivity.class));
        finish();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePicker.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    eventImage.setImageURI(imageUri);
                    if (imageError != null) imageError.setVisibility(View.GONE);

                    // Convert image to Base64 immediately
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        base64Image = bitmapToBase64(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // ------------------------- SIDEBAR / NAVIGATION -------------------------
    private void setupSideNav() {
        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                startActivity(new Intent(EditEventActivity.this, MainActivity.class));
            } else if (id == R.id.menu_participation) {
                // Open participation activity
                startActivity(new Intent(this, MyParticipationActivity.class));
            } else if (id == R.id.menu_events) {
                startActivity(new Intent(EditEventActivity.this, MyEventsActivity.class));
            } else if (id == R.id.menu_profile) {
                startActivity(new Intent(EditEventActivity.this, ProfileActivity.class));
            } else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();

                Intent i = new Intent(EditEventActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // ------------------------- NAVIGATION HEADER -------------------------
    private void setupHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.headerUserName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("Name").getValue(String.class);
                    if (name != null && headerName != null) headerName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ------------------------- BOTTOM NAVIGATION -------------------------
    private void setupBottomNav() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(EditEventActivity.this, MainActivity.class));
            } else if (id == R.id.nav_events) {
                startActivity(new Intent(EditEventActivity.this, AddEventActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(EditEventActivity.this, ProfileActivity.class));
            }

            return true;
        });
    }
}
