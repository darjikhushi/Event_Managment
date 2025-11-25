//package com.example.eventmanagment;
//
//import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Base64;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.*;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.drawerlayout.widget.DrawerLayout;
//
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.util.Calendar;
//import java.util.HashMap;
//
//public class AddEventActivity extends AppCompatActivity {
//
//    EditText eventTitle, eventDescription, startDate, startTime, endDate, endTime, venue;
//    Button saveEventButton, selectImageButton;
//    ImageView eventImagePreview;
//
//    String base64Image = ""; // Store final Base64 string
//
//    DrawerLayout drawerLayout;
//    ImageView ivMenu;
//    NavigationView navigationView;
//    BottomNavigationView bottom_navigation;
//
//    FirebaseAuth auth;
//    DatabaseReference eventRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_event);
//
//        // ---------- UI INITIALIZATION ----------
//        eventTitle = findViewById(R.id.eventTitle);
//        eventDescription = findViewById(R.id.eventDescription);
//        startDate = findViewById(R.id.startDate);
//        startTime = findViewById(R.id.startTime);
//        endDate = findViewById(R.id.endDate);
//        endTime = findViewById(R.id.endTime);
//        venue = findViewById(R.id.venue);
//
//        selectImageButton = findViewById(R.id.selectImageButton);
//        saveEventButton = findViewById(R.id.saveEventButton);
//
//        eventImagePreview = findViewById(R.id.eventImagePreview);
//
//        drawerLayout = findViewById(R.id.drawerLayout);
//        ivMenu = findViewById(R.id.ivMenu);
//        navigationView = findViewById(R.id.navigationView);
//        bottom_navigation = findViewById(R.id.bottom_navigation);
//
//        auth = FirebaseAuth.getInstance();
//        eventRef = FirebaseDatabase.getInstance().getReference("Events");
//
//        // ---------- DATE PICKERS ----------
//        startDate.setOnClickListener(v -> showDatePicker(startDate));
//        endDate.setOnClickListener(v -> showDatePicker(endDate));
//
//        // ---------- TIME PICKERS ----------
//        startTime.setOnClickListener(v -> showTimePicker(startTime));
//        endTime.setOnClickListener(v -> showTimePicker(endTime));
//
//        // ---------- IMAGE PICKER ----------
//        selectImageButton.setOnClickListener(v -> chooseImage());
//
//        // ---------- SAVE EVENT ----------
//        saveEventButton.setOnClickListener(v -> saveEventToFirebase());
//
//        // ---------- DRAWER MENU ----------
//        ivMenu.setOnClickListener(v -> {
//            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
//                drawerLayout.closeDrawer(Gravity.LEFT);
//            else drawerLayout.openDrawer(Gravity.LEFT);
//        });
//
//        setupSideNav();
//        setupBottomNav();
//    }
//
//    // ------------------------------------------------------------------
//    // DATE PICKER
//    // ------------------------------------------------------------------
//    private void showDatePicker(EditText target) {
//        Calendar c = Calendar.getInstance();
//        DatePickerDialog picker = new DatePickerDialog(this,
//                (view, y, m, d) -> target.setText(d + "/" + (m + 1) + "/" + y),
//                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        picker.show();
//    }
//
//    // ------------------------------------------------------------------
//    // TIME PICKER
//    // ------------------------------------------------------------------
//    private void showTimePicker(EditText target) {
//        Calendar c = Calendar.getInstance();
//        TimePickerDialog picker = new TimePickerDialog(this,
//                (view, h, m) -> target.setText(String.format("%02d:%02d", h, m)),
//                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
//        picker.show();
//    }
//
//    // ------------------------------------------------------------------
//    // IMAGE PICKER INTENT
//    // ------------------------------------------------------------------
//    private void chooseImage() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 100);
//    }
//
//    // ------------------------------------------------------------------
//    // ON IMAGE SELECT — Convert to Base64
//    // ------------------------------------------------------------------
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//            eventImagePreview.setImageURI(imageUri);
//
//            try {
//                InputStream stream = getContentResolver().openInputStream(imageUri);
//                Bitmap bitmap = BitmapFactory.decodeStream(stream);
//                base64Image = bitmapToBase64(bitmap);
//            } catch (Exception e) {
//                Toast.makeText(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // ------------------------------------------------------------------
//    // Convert Bitmap to Base64
//    // ------------------------------------------------------------------
//    private String bitmapToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
//        byte[] bytes = baos.toByteArray();
//        return Base64.encodeToString(bytes, Base64.DEFAULT);
//    }
//
//    // ------------------------------------------------------------------
//    // SAVE EVENT DATA + BASE64 IMAGE
//    // ------------------------------------------------------------------
//    private void saveEventToFirebase() {
//
//        String title = eventTitle.getText().toString().trim();
//        String desc = eventDescription.getText().toString().trim();
//        String sDate = startDate.getText().toString().trim();
//        String sTime = startTime.getText().toString().trim();
//        String eDate = endDate.getText().toString().trim();
//        String eTime = endTime.getText().toString().trim();
//        String eventVenue = venue.getText().toString().trim();
//
//        if (title.isEmpty() || desc.isEmpty() || sDate.isEmpty() || sTime.isEmpty()
//                || eDate.isEmpty() || eTime.isEmpty() || eventVenue.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = auth.getCurrentUser().getUid();
//        String eventId = eventRef.push().getKey();
//
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("Title", title);
//        map.put("Description", desc);
//        map.put("StartDate", sDate);
//        map.put("StartTime", sTime);
//        map.put("EndDate", eDate);
//        map.put("EndTime", eTime);
//        map.put("Venue", eventVenue);
//        map.put("OwnerId", userId);
//        map.put("ImageBase64", base64Image);
//
//        eventRef.child(eventId).setValue(map)
//                .addOnSuccessListener(a -> {
//                    Toast.makeText(this, "Event Added Successfully!", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> Toast.makeText(this,
//                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    // ------------------------------------------------------------------
//    // SETUP SIDE NAV
//    // ------------------------------------------------------------------
//    private void setupSideNav() {
//        navigationView.setNavigationItemSelectedListener(item -> {
//            int id = item.getItemId();
//
//            if (id == R.id.menu_home)
//                startActivity(new Intent(this, MainActivity.class));
//
//            else if (id == R.id.menu_events)
//                startActivity(new Intent(this, MyEventsActivity.class));
//
//            else if (id == R.id.menu_profile)
//                startActivity(new Intent(this, ProfileActivity.class));
//
//            else if (id == R.id.menu_logout) {
//                FirebaseAuth.getInstance().signOut();
//                Intent i = new Intent(this, LoginActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
//            }
//
//            drawerLayout.closeDrawers();
//            return true;
//        });
//    }
//
//    // ------------------------------------------------------------------
//    // BOTTOM NAVIGATION
//    // ------------------------------------------------------------------
//    private void setupBottomNav() {
//        bottom_navigation.setOnItemSelectedListener(item -> {
//
//            int id = item.getItemId();
//
//            if (id == R.id.nav_home)
//                startActivity(new Intent(this, MainActivity.class));
//
//            else if (id == R.id.nav_events)
//                startActivity(new Intent(this, AddEventActivity.class));
//
//            else if (id == R.id.nav_profile)
//                startActivity(new Intent(this, ProfileActivity.class));
//
//            return true;
//        });
//    }
//}
package com.example.eventmanagment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {

    EditText eventTitle, eventDescription, startDate, startTime, endDate, endTime, venue;
    Button saveEventButton, selectImageButton;
    ImageView eventImagePreview;

    String base64Image = "";

    DrawerLayout drawerLayout;
    ImageView ivMenu;
    NavigationView navigationView;
    BottomNavigationView bottom_navigation;

    FirebaseAuth auth;
    DatabaseReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // ---------- UI INITIALIZATION ----------
        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        startDate = findViewById(R.id.startDate);
        startTime = findViewById(R.id.startTime);
        endDate = findViewById(R.id.endDate);
        endTime = findViewById(R.id.endTime);
        venue = findViewById(R.id.venue);

        selectImageButton = findViewById(R.id.selectImageButton);
        saveEventButton = findViewById(R.id.saveEventButton);

        eventImagePreview = findViewById(R.id.eventImagePreview);

        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigationView);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        auth = FirebaseAuth.getInstance();
        eventRef = FirebaseDatabase.getInstance().getReference("Events");

        // ---------- DATE PICKERS ----------
        startDate.setOnClickListener(v -> showDatePicker(startDate));
        endDate.setOnClickListener(v -> showDatePicker(endDate));

        // ---------- TIME PICKERS ----------
        startTime.setOnClickListener(v -> showTimePicker(startTime));
        endTime.setOnClickListener(v -> showTimePicker(endTime));

        // ---------- IMAGE PICKER ----------
        selectImageButton.setOnClickListener(v -> chooseImage());

        // ---------- SAVE EVENT ----------
        saveEventButton.setOnClickListener(v -> saveEventToFirebase());

        // ---------- DRAWER MENU ----------
        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            else drawerLayout.openDrawer(Gravity.LEFT);
        });

        setupHeader();      //  <<<<<<<<<< ADDED HERE
        setupSideNav();
        setupBottomNav();
    }

    // ------------------------------------------------------------------
    // SHOW USER NAME IN SIDE MENU HEADER
    // ------------------------------------------------------------------
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
                    if (name != null) {
                        headerName.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ------------------------------------------------------------------
    // DATE PICKER
    // ------------------------------------------------------------------
    private void showDatePicker(EditText target) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, y, m, d) -> target.setText(d + "/" + (m + 1) + "/" + y),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    // ------------------------------------------------------------------
    // TIME PICKER
    // ------------------------------------------------------------------
    private void showTimePicker(EditText target) {
        Calendar c = Calendar.getInstance();
        TimePickerDialog picker = new TimePickerDialog(this,
                (view, h, m) -> target.setText(String.format("%02d:%02d", h, m)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        picker.show();
    }

    // ------------------------------------------------------------------
    // IMAGE PICKER INTENT
    // ------------------------------------------------------------------
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            eventImagePreview.setImageURI(imageUri);

            try {
                InputStream stream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                base64Image = bitmapToBase64(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // ------------------------------------------------------------------
    // SAVE EVENT
    // ------------------------------------------------------------------
    private void saveEventToFirebase() {

        String title = eventTitle.getText().toString().trim();
        String desc = eventDescription.getText().toString().trim();
        String sDate = startDate.getText().toString().trim();
        String sTime = startTime.getText().toString().trim();
        String eDate = endDate.getText().toString().trim();
        String eTime = endTime.getText().toString().trim();
        String eventVenue = venue.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || sDate.isEmpty() || sTime.isEmpty()
                || eDate.isEmpty() || eTime.isEmpty() || eventVenue.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String eventId = eventRef.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("Title", title);
        map.put("Description", desc);
        map.put("StartDate", sDate);
        map.put("StartTime", sTime);
        map.put("EndDate", eDate);
        map.put("EndTime", eTime);
        map.put("Venue", eventVenue);
        map.put("OwnerId", userId);
        map.put("ImageBase64", base64Image);

        eventRef.child(eventId).setValue(map)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Event Added Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // ------------------------------------------------------------------
    // SIDE NAVIGATION
    // ------------------------------------------------------------------
    private void setupSideNav() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home)
                startActivity(new Intent(this, MainActivity.class));

            else if (id == R.id.menu_events)
                startActivity(new Intent(this, MyEventsActivity.class));

            else if (id == R.id.menu_profile)
                startActivity(new Intent(this, ProfileActivity.class));

            else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // ------------------------------------------------------------------
    // BOTTOM NAVIGATION
    // ------------------------------------------------------------------
    private void setupBottomNav() {
        bottom_navigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home)
                startActivity(new Intent(this, MainActivity.class));

            else if (id == R.id.nav_events)
                startActivity(new Intent(this, AddEventActivity.class));

            else if (id == R.id.nav_profile)
                startActivity(new Intent(this, ProfileActivity.class));

            return true;
        });
    }
}
