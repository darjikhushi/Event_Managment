////package com.example.eventmanagment;
////
////import android.app.DatePickerDialog;
////import android.app.TimePickerDialog;
////import android.content.Intent;
////import android.graphics.Bitmap;
////import android.graphics.BitmapFactory;
////import android.net.Uri;
////import android.os.Bundle;
////import android.provider.MediaStore;
////import android.util.Base64;
////import android.view.Gravity;
////import android.view.View;
////import android.widget.*;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.drawerlayout.widget.DrawerLayout;
////
////import com.google.android.material.bottomnavigation.BottomNavigationView;
////import com.google.android.material.navigation.NavigationView;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.auth.FirebaseUser;
////import com.google.firebase.database.*;
////
////import java.io.ByteArrayOutputStream;
////import java.io.InputStream;
////import java.util.Calendar;
////import java.util.HashMap;
////
////public class AddEventActivity extends AppCompatActivity {
////
////    EditText eventTitle, eventDescription, startDate, startTime, endDate, endTime, venue;
////    Button saveEventButton, selectImageButton;
////    ImageView eventImagePreview;
////
////    String base64Image = ""; // Store final Base64 string
////
////    DrawerLayout drawerLayout;
////    ImageView ivMenu;
////    NavigationView navigationView;
////    BottomNavigationView bottom_navigation;
////
////    FirebaseAuth auth;
////    DatabaseReference eventRef;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_add_event);
////
////        // ---------- UI INITIALIZATION ----------
////        eventTitle = findViewById(R.id.eventTitle);
////        eventDescription = findViewById(R.id.eventDescription);
////        startDate = findViewById(R.id.startDate);
////        startTime = findViewById(R.id.startTime);
////        endDate = findViewById(R.id.endDate);
////        endTime = findViewById(R.id.endTime);
////        venue = findViewById(R.id.venue);
////
////        selectImageButton = findViewById(R.id.selectImageButton);
////        saveEventButton = findViewById(R.id.saveEventButton);
////
////        eventImagePreview = findViewById(R.id.eventImagePreview);
////
////        drawerLayout = findViewById(R.id.drawerLayout);
////        ivMenu = findViewById(R.id.ivMenu);
////        navigationView = findViewById(R.id.navigationView);
////        bottom_navigation = findViewById(R.id.bottom_navigation);
////
////        auth = FirebaseAuth.getInstance();
////        eventRef = FirebaseDatabase.getInstance().getReference("Events");
////
////        // ---------- DATE PICKERS ----------
////        startDate.setOnClickListener(v -> showDatePicker(startDate));
////        endDate.setOnClickListener(v -> showDatePicker(endDate));
////
////        // ---------- TIME PICKERS ----------
////        startTime.setOnClickListener(v -> showTimePicker(startTime));
////        endTime.setOnClickListener(v -> showTimePicker(endTime));
////
////        // ---------- IMAGE PICKER ----------
////        selectImageButton.setOnClickListener(v -> chooseImage());
////
////        // ---------- SAVE EVENT ----------
////        saveEventButton.setOnClickListener(v -> saveEventToFirebase());
////
////        // ---------- DRAWER MENU ----------
////        ivMenu.setOnClickListener(v -> {
////            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
////                drawerLayout.closeDrawer(Gravity.LEFT);
////            else drawerLayout.openDrawer(Gravity.LEFT);
////        });
////
////        setupSideNav();
////        setupBottomNav();
////    }
////
////    // ------------------------------------------------------------------
////    // DATE PICKER
////    // ------------------------------------------------------------------
////    private void showDatePicker(EditText target) {
////        Calendar c = Calendar.getInstance();
////        DatePickerDialog picker = new DatePickerDialog(this,
////                (view, y, m, d) -> target.setText(d + "/" + (m + 1) + "/" + y),
////                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
////        picker.show();
////    }
////
////    // ------------------------------------------------------------------
////    // TIME PICKER
////    // ------------------------------------------------------------------
////    private void showTimePicker(EditText target) {
////        Calendar c = Calendar.getInstance();
////        TimePickerDialog picker = new TimePickerDialog(this,
////                (view, h, m) -> target.setText(String.format("%02d:%02d", h, m)),
////                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
////        picker.show();
////    }
////
////    // ------------------------------------------------------------------
////    // IMAGE PICKER INTENT
////    // ------------------------------------------------------------------
////    private void chooseImage() {
////        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////        startActivityForResult(intent, 100);
////    }
////
////    // ------------------------------------------------------------------
////    // ON IMAGE SELECT — Convert to Base64
////    // ------------------------------------------------------------------
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////
////        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
////            Uri imageUri = data.getData();
////            eventImagePreview.setImageURI(imageUri);
////
////            try {
////                InputStream stream = getContentResolver().openInputStream(imageUri);
////                Bitmap bitmap = BitmapFactory.decodeStream(stream);
////                base64Image = bitmapToBase64(bitmap);
////            } catch (Exception e) {
////                Toast.makeText(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
////            }
////        }
////    }
////
////    // ------------------------------------------------------------------
////    // Convert Bitmap to Base64
////    // ------------------------------------------------------------------
////    private String bitmapToBase64(Bitmap bitmap) {
////        ByteArrayOutputStream baos = new ByteArrayOutputStream();
////        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
////        byte[] bytes = baos.toByteArray();
////        return Base64.encodeToString(bytes, Base64.DEFAULT);
////    }
////
////    // ------------------------------------------------------------------
////    // SAVE EVENT DATA + BASE64 IMAGE
////    // ------------------------------------------------------------------
////    private void saveEventToFirebase() {
////
////        String title = eventTitle.getText().toString().trim();
////        String desc = eventDescription.getText().toString().trim();
////        String sDate = startDate.getText().toString().trim();
////        String sTime = startTime.getText().toString().trim();
////        String eDate = endDate.getText().toString().trim();
////        String eTime = endTime.getText().toString().trim();
////        String eventVenue = venue.getText().toString().trim();
////
////        if (title.isEmpty() || desc.isEmpty() || sDate.isEmpty() || sTime.isEmpty()
////                || eDate.isEmpty() || eTime.isEmpty() || eventVenue.isEmpty()) {
////            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        String userId = auth.getCurrentUser().getUid();
////        String eventId = eventRef.push().getKey();
////
////        HashMap<String, Object> map = new HashMap<>();
////        map.put("Title", title);
////        map.put("Description", desc);
////        map.put("StartDate", sDate);
////        map.put("StartTime", sTime);
////        map.put("EndDate", eDate);
////        map.put("EndTime", eTime);
////        map.put("Venue", eventVenue);
////        map.put("OwnerId", userId);
////        map.put("ImageBase64", base64Image);
////
////        eventRef.child(eventId).setValue(map)
////                .addOnSuccessListener(a -> {
////                    Toast.makeText(this, "Event Added Successfully!", Toast.LENGTH_SHORT).show();
////                    finish();
////                })
////                .addOnFailureListener(e -> Toast.makeText(this,
////                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
////    }
////
////    // ------------------------------------------------------------------
////    // SETUP SIDE NAV
////    // ------------------------------------------------------------------
////    private void setupSideNav() {
////        navigationView.setNavigationItemSelectedListener(item -> {
////            int id = item.getItemId();
////
////            if (id == R.id.menu_home)
////                startActivity(new Intent(this, MainActivity.class));
////
////            else if (id == R.id.menu_events)
////                startActivity(new Intent(this, MyEventsActivity.class));
////
////            else if (id == R.id.menu_profile)
////                startActivity(new Intent(this, ProfileActivity.class));
////
////            else if (id == R.id.menu_logout) {
////                FirebaseAuth.getInstance().signOut();
////                Intent i = new Intent(this, LoginActivity.class);
////                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                startActivity(i);
////            }
////
////            drawerLayout.closeDrawers();
////            return true;
////        });
////    }
////
////    // ------------------------------------------------------------------
////    // BOTTOM NAVIGATION
////    // ------------------------------------------------------------------
////    private void setupBottomNav() {
////        bottom_navigation.setOnItemSelectedListener(item -> {
////
////            int id = item.getItemId();
////
////            if (id == R.id.nav_home)
////                startActivity(new Intent(this, MainActivity.class));
////
////            else if (id == R.id.nav_events)
////                startActivity(new Intent(this, AddEventActivity.class));
////
////            else if (id == R.id.nav_profile)
////                startActivity(new Intent(this, ProfileActivity.class));
////
////            return true;
////        });
////    }
////}
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
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.os.Build;
//import androidx.core.app.NotificationCompat;
//
//
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
//    String base64Image = "";
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
//        setupHeader();      //  <<<<<<<<<< ADDED HERE
//        setupSideNav();
//        setupBottomNav();
//    }
//
//    // ------------------------------------------------------------------
//    // SHOW USER NAME IN SIDE MENU HEADER
//    // ------------------------------------------------------------------
//    private void setupHeader() {
//
//        View headerView = navigationView.getHeaderView(0);
//        TextView headerName = headerView.findViewById(R.id.headerUserName);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) return;
//
//        DatabaseReference userRef = FirebaseDatabase.getInstance()
//                .getReference("Users")
//                .child(user.getUid());
//
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String name = snapshot.child("Name").getValue(String.class);
//                    if (name != null) {
//                        headerName.setText(name);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }
//
//    private void showNotification(String eventName) {
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        String channelId = "event_channel";
//        String channelName = "Event Notifications";
//
//        // For Android 8.0+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH
//            );
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Tap intent (opens MainActivity)
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
//        );
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.bell_solid_full) // replace with your drawable icon
//                .setContentTitle("New Event")
//                .setContentText(eventName)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
//
//        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//    }
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
//    private String bitmapToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
//        byte[] bytes = baos.toByteArray();
//        return Base64.encodeToString(bytes, Base64.DEFAULT);
//    }
//
//    // ------------------------------------------------------------------
//    // SAVE EVENT
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
//
//                    Toast.makeText(this, "Event Added Successfully!", Toast.LENGTH_SHORT).show();
//                    showNotification(title);
//                    finish();
//                })
//                .addOnFailureListener(e -> Toast.makeText(this,
//                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    // ------------------------------------------------------------------
//    // SIDE NAVIGATION
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
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

    // NEW: image error view
    TextView imageError;

    String base64Image = "";

    DrawerLayout drawerLayout;
    ImageView ivMenu;
    NavigationView navigationView;
    BottomNavigationView bottom_navigation;

    FirebaseAuth auth;
    DatabaseReference eventRef;

    Calendar now = Calendar.getInstance();
    Calendar startCal = Calendar.getInstance();
    Calendar endCal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // ---------- UI ----------
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

        // NEW: error text for image required
        imageError = findViewById(R.id.imageErrorText);

        drawerLayout = findViewById(R.id.drawerLayout);
        ivMenu = findViewById(R.id.ivMenu);
        navigationView = findViewById(R.id.navigationView);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        auth = FirebaseAuth.getInstance();
        eventRef = FirebaseDatabase.getInstance().getReference("Events");

        // ---------- Date / Time ----------
        startDate.setOnClickListener(v -> showStartDatePicker());
        endDate.setOnClickListener(v -> showEndDatePicker());

        startTime.setOnClickListener(v -> showStartTimePicker());
        endTime.setOnClickListener(v -> showEndTimePicker());

        // ---------- IMAGE PICKER ----------
        selectImageButton.setOnClickListener(v -> chooseImage());

        // ---------- SAVE ----------
        saveEventButton.setOnClickListener(v -> { saveEventToFirebase();
        });

        setupHeader();
        setupSideNav();
        setupBottomNav();
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
                    if (name != null) headerName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }




    // NOTIFICATION
    private void showNotification(String eventName) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "event_channel";
        String channelName = "Event Notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.bell_solid_full)
                .setContentTitle("New Event")
                .setContentText(eventName)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // DATE PICKERS
    private void showStartDatePicker() {
        final Calendar c = Calendar.getInstance();
        int cy = c.get(Calendar.YEAR), cm = c.get(Calendar.MONTH), cd = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    startDate.setText(d + "/" + (m + 1) + "/" + y);
                    startCal.set(y, m, d, 0, 0);
                    endDate.setText("");
                    endTime.setText("");
                }, cy, cm, cd);

        picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        picker.show();
    }

    private void showEndDatePicker() {
        final Calendar min = (Calendar) startCal.clone();
        if (startDate.getText().toString().trim().isEmpty()) {
            min.setTimeInMillis(System.currentTimeMillis());
        }
        int y = min.get(Calendar.YEAR), m = min.get(Calendar.MONTH), d = min.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(this,
                (view, y1, m1, d1) -> {
                    endDate.setText(d1 + "/" + (m1 + 1) + "/" + y1);
                    endCal.set(y1, m1, d1, 0, 0);
                }, y, m, d);

        picker.getDatePicker().setMinDate(min.getTimeInMillis());
        picker.show();
    }

    // TIME PICKERS
    private void showStartTimePicker() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY), minute = c.get(Calendar.MINUTE);

        boolean isToday = isSameDay(startCal, Calendar.getInstance());
        int defH = isToday ? hour : 9;
        int defM = isToday ? minute : 0;

        TimePickerDialog picker = new TimePickerDialog(this, (view, h, m) -> {

            if (isToday) {
                Calendar temp = (Calendar) startCal.clone();
                temp.set(Calendar.HOUR_OF_DAY, h);
                temp.set(Calendar.MINUTE, m);

                if (temp.before(Calendar.getInstance())) {
                    setLayoutError(startTime, "Start time cannot be in the past");
                    return;
                }
            }

            startTime.setText(String.format("%02d:%02d", h, m));
            setLayoutError(startTime, null);

            startCal.set(Calendar.HOUR_OF_DAY, h);
            startCal.set(Calendar.MINUTE, m);

        }, defH, defM, true);

        picker.show();
    }

    private void showEndTimePicker() {

        int defH = 18, defM = 0;

        if (isSameDay(startCal, endCal)) {
            defH = startCal.get(Calendar.HOUR_OF_DAY);
            defM = startCal.get(Calendar.MINUTE) + 30;
            if (defM >= 60) { defM -= 60; defH++; }
        }

        TimePickerDialog picker = new TimePickerDialog(this, (view, h, m) -> {

            Calendar tempEnd = (Calendar) endCal.clone();
            tempEnd.set(Calendar.HOUR_OF_DAY, h);
            tempEnd.set(Calendar.MINUTE, m);

            if (!startDate.getText().toString().trim().isEmpty()
                    && tempEnd.getTimeInMillis() <= startCal.getTimeInMillis()) {
                setLayoutError(endTime, "End must be after start");
                return;
            }

            endTime.setText(String.format("%02d:%02d", h, m));
            setLayoutError(endTime, null);

            endCal.set(Calendar.HOUR_OF_DAY, h);
            endCal.set(Calendar.MINUTE, m);

        }, defH, defM, true);

        picker.show();
    }

    // IMAGE PICKER
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);

        // clear image error once user tries again
        imageError.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

                Uri imageUri = data.getData();
                eventImagePreview.setImageURI(imageUri);

                InputStream stream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(stream);

                base64Image = bitmapToBase64(bitmap);

                // NEW: remove error if image selected
                imageError.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    // SAVE EVENT
    private void saveEventToFirebase() {

        clearAllLayoutErrors();

        String title = eventTitle.getText().toString().trim();
        String desc = eventDescription.getText().toString().trim();
        String sDate = startDate.getText().toString().trim();
        String sTime = startTime.getText().toString().trim();
        String eDate = endDate.getText().toString().trim();
        String eTime = endTime.getText().toString().trim();
        String eventVenue = venue.getText().toString().trim();

        boolean valid = true;

        if (title.isEmpty()) { setLayoutError(eventTitle, "Title is required"); valid = false; }
        if (desc.isEmpty()) { setLayoutError(eventDescription, "Description required"); valid = false; }
        if (sDate.isEmpty()) { setLayoutError(startDate, "Start date required"); valid = false; }
        if (sTime.isEmpty()) { setLayoutError(startTime, "Start time required"); valid = false; }
        if (eDate.isEmpty()) { setLayoutError(endDate, "End date required"); valid = false; }
        if (eTime.isEmpty()) { setLayoutError(endTime, "End time required"); valid = false; }
        if (eventVenue.isEmpty()) { setLayoutError(venue, "Venue required"); valid = false; }

        // NEW: image required
        if (base64Image.trim().isEmpty()) {
            imageError.setVisibility(View.VISIBLE);
            imageError.setText("Event image is required");
            valid = false;
        }

        if (!valid) return;

        // Parse Start Date & Time
        try {
            String[] sd = sDate.split("/");
            String[] st = sTime.split(":");

            startCal.set(Integer.parseInt(sd[2]),
                    Integer.parseInt(sd[1]) - 1,
                    Integer.parseInt(sd[0]),
                    Integer.parseInt(st[0]),
                    Integer.parseInt(st[1]),
                    0);

        } catch (Exception e) {
            setLayoutError(startDate, "Invalid start date/time");
            return;
        }

        // Parse End Date & Time
        try {
            String[] ed = eDate.split("/");
            String[] et = eTime.split(":");

            endCal.set(Integer.parseInt(ed[2]),
                    Integer.parseInt(ed[1]) - 1,
                    Integer.parseInt(ed[0]),
                    Integer.parseInt(et[0]),
                    Integer.parseInt(et[1]),
                    0);

        } catch (Exception e) {
            setLayoutError(endDate, "Invalid end date/time");
            return;
        }

        if (startCal.before(Calendar.getInstance())) {
            setLayoutError(startDate, "Start must be future");
            return;
        }

        if (!endCal.after(startCal)) {
            setLayoutError(endDate, "End must be after start");
            setLayoutError(endTime, "End must be after start");
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = eventRef.push().getKey();
        if (eventId == null) {
            Toast.makeText(this, "Unable to create event id", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("Title", title);
        map.put("Description", desc);
        map.put("StartDate", sDate);
        map.put("StartTime", sTime);
        map.put("EndDate", eDate);
        map.put("EndTime", eTime);
        map.put("Venue", eventVenue);
        map.put("OwnerId", user.getUid());
        map.put("ImageBase64", base64Image);

        eventRef.child(eventId).setValue(map)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Event Added Successfully!", Toast.LENGTH_SHORT).show();
                    showNotification(title);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // ------------------------- SIDEBAR / NAVIGATION -------------------------
    private void setupSideNav() {
        // Handle menu icon click to open/close drawer
        ivMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        });

        // Navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                startActivity(new Intent(AddEventActivity.this, MainActivity.class));
            } else if (id == R.id.menu_participation) {
                // Open participation activity
                startActivity(new Intent(this, MyParticipationActivity.class));
            } else if (id == R.id.menu_events) {
                startActivity(new Intent(AddEventActivity.this, MyEventsActivity.class));
            } else if (id == R.id.menu_profile) {
                startActivity(new Intent(AddEventActivity.this, ProfileActivity.class));
            } else if (id == R.id.menu_logout) {
                FirebaseAuth.getInstance().signOut();

                // Optional: clear session/shared preferences if you use them
                getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();

                Intent i = new Intent(AddEventActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // ------------------------- NAVIGATION HEADER -------------------------


    // ------------------------- BOTTOM NAVIGATION -------------------------
    private void setupBottomNav() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(AddEventActivity.this, MainActivity.class));
            } else if (id == R.id.nav_events) {
                startActivity(new Intent(AddEventActivity.this, AddEventActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(AddEventActivity.this, ProfileActivity.class));
            }

            return true;
        });
    }


    // HELPERS
    private TextInputLayout findParentTextInputLayout(View v) {
        View parent = (View) v.getParent();
        for (int i = 0; i < 4 && parent != null; i++) {
            if (parent instanceof TextInputLayout) return (TextInputLayout) parent;
            parent = (View) parent.getParent();
        }
        return null;
    }

    private void setLayoutError(View v, String message) {
        TextInputLayout layout = findParentTextInputLayout(v);
        if (layout != null) layout.setError(message);
        else if (v instanceof EditText) ((EditText) v).setError(message);
    }

    private void clearAllLayoutErrors() {
        TextInputLayout t;

        t = findParentTextInputLayout(eventTitle); if (t != null) t.setError(null);
        t = findParentTextInputLayout(eventDescription); if (t != null) t.setError(null);
        t = findParentTextInputLayout(startDate); if (t != null) t.setError(null);
        t = findParentTextInputLayout(startTime); if (t != null) t.setError(null);
        t = findParentTextInputLayout(endDate); if (t != null) t.setError(null);
        t = findParentTextInputLayout(endTime); if (t != null) t.setError(null);
        t = findParentTextInputLayout(venue); if (t != null) t.setError(null);
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                && a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH);
    }
}
