//package com.example.eventmanagment;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.*;
//import android.widget.Toast;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//
//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ListView eventListView;
//    private FloatingActionButton addEventFab;
//
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReference;
//    FirebaseUser currentUser;
//
//    ArrayList<Event> eventList = new ArrayList<>();
//    EventAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        eventListView = findViewById(R.id.eventListView);
//        addEventFab = findViewById(R.id.addEventFab);
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("Events");
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        adapter = new EventAdapter();
//        eventListView.setAdapter(adapter);
//
//        showEvents();
//
//        addEventFab.setOnClickListener(v -> {
//            if (currentUser != null) {
////                Toast.makeText(MainActivity.this, "Add new event clicked", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
//                startActivity(intent);
//
//                // TODO: Open AddEventActivity
//            } else {
//                Toast.makeText(MainActivity.this, "Please login to add events", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void showEvents() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                eventList.clear();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Event e = data.getValue(Event.class);
//                    if (e != null) e.id = data.getKey();
//                    eventList.add(e);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public static class Event {
//        private String id;  // ✅ Add this line
//        @PropertyName("Title")
//        private String eventTitle;
//        private String eventDescription;
//        private String eventDate;
//        private String ownerId;   // ✅ New field for event creator
//
//
//        // ✅ Required empty constructor for Firebase
//        public Event() {
//        }
//
//        public Event(String Title, String eventDescription, String eventDate) {
//            this.eventTitle = eventTitle;
//            this.eventDescription = eventDescription;
//            this.eventDate = eventDate;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getEventTitle() {
//            return eventTitle;
//        }
//
//        public void setEventTitle(String eventTitle) {
//            this.eventTitle = eventTitle;
//        }
//
//        public String getEventDescription() {
//            return eventDescription;
//        }
//
//        public void setEventDescription(String eventDescription) {
//            this.eventDescription = eventDescription;
//        }
//
//        public String getEventDate() {
//            return eventDate;
//        }
//
//        public void setEventDate(String eventDate) {
//            this.eventDate = eventDate;
//        }
//        public String getOwnerId() { return ownerId; }
//        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
//    }
//
//
//    private class EventAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return eventList.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return eventList.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int position, View convertView, android.view.ViewGroup parent) {
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(R.layout.activity_event_item, parent, false);
//            }
//
//            Event event = eventList.get(position);
//
//            TextView title = convertView.findViewById(R.id.tvEventTitle);
//            TextView date = convertView.findViewById(R.id.tvEventDate);
//            TextView desc = convertView.findViewById(R.id.tvEventDesc);
//            ImageButton edit = convertView.findViewById(R.id.btnEdit);
//            ImageButton delete = convertView.findViewById(R.id.btnDelete);
//
//            title.setText(event.eventTitle != null ? event.eventTitle : "(No Title)");
//            date.setText("Date: " + (event.eventDate != null ? event.eventDate : "-"));
//            desc.setText(event.eventDescription != null ? event.eventDescription : "");
//
//            // Show edit/delete only for creator
//            if (currentUser != null && event.ownerId != null && event.ownerId.equals(currentUser.getUid())) {
//                edit.setVisibility(View.VISIBLE);
//                delete.setVisibility(View.VISIBLE);
//            } else {
//                edit.setVisibility(View.GONE);
//                delete.setVisibility(View.GONE);
//            }
//
//            edit.setOnClickListener(v -> {
//                Toast.makeText(MainActivity.this, "Edit: " + event.eventTitle, Toast.LENGTH_SHORT).show();
//                // TODO: open edit screen
//            });
//
//            delete.setOnClickListener(v -> {
//                if (event.id != null) {
//                    databaseReference.child(event.id).removeValue()
//                            .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Event deleted", Toast.LENGTH_SHORT).show())
//                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Delete failed", Toast.LENGTH_SHORT).show());
//                }
//            });
//
//            return convertView;
//        }
//    }
//}




//
//package com.example.eventmanagment;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.*;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//
//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ListView eventListView;
//    private FloatingActionButton addEventFab;
//
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReference;
//    FirebaseUser currentUser;
//
//    ArrayList<Event> eventList = new ArrayList<>();
//    EventAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        eventListView = findViewById(R.id.eventListView);
//        addEventFab = findViewById(R.id.addEventFab);
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("Events");
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        adapter = new EventAdapter();
//        eventListView.setAdapter(adapter);
//
//        showEvents();
//
//        addEventFab.setOnClickListener(v -> {
//            if (currentUser != null) {
//                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
//                startActivity(intent);
//            } else {
//                Toast.makeText(MainActivity.this, "Please login to add events", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void showEvents() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                eventList.clear();
//
//                if (snapshot.exists()) {
//                    for (DataSnapshot data : snapshot.getChildren()) {
//                        Event e = data.getValue(Event.class);
//                        if (e != null) {
//                            e.setId(data.getKey());
//                            eventList.add(e);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(MainActivity.this, "No events found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Error: " + error.getMessage());
//                Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // ✅ Event model class (corrected)
//    public static class Event {
//        private String id;
//        private String eventTitle;
//        private String eventDescription;
//        private String eventDate;
//        private String ownerId;
//
//        // Required empty constructor for Firebase
//        public Event() {
//        }
//
//        public Event(String eventTitle, String eventDescription, String eventDate, String ownerId) {
//            this.eventTitle = eventTitle;
//            this.eventDescription = eventDescription;
//            this.eventDate = eventDate;
//            this.ownerId = ownerId;
//        }
//
//        public String getId() { return id; }
//        public void setId(String id) { this.id = id; }
//
//        public String getEventTitle() { return eventTitle; }
//        public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
//
//        public String getEventDescription() { return eventDescription; }
//        public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
//
//        public String getEventDate() { return eventDate; }
//        public void setEventDate(String eventDate) { this.eventDate = eventDate; }
//
//        public String getOwnerId() { return ownerId; }
//        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
//    }
//
//    private class EventAdapter extends BaseAdapter {
//        @Override
//        public int getCount() {
//            return eventList.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return eventList.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int position, View convertView, android.view.ViewGroup parent) {
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(R.layout.activity_event_item, parent, false);
//            }
//
//            Event event = eventList.get(position);
//
//            TextView title = convertView.findViewById(R.id.tvEventTitle);
//            TextView date = convertView.findViewById(R.id.tvEventDate);
//            TextView desc = convertView.findViewById(R.id.tvEventDesc);
//            ImageButton edit = convertView.findViewById(R.id.btnEdit);
//            ImageButton delete = convertView.findViewById(R.id.btnDelete);
//
//            title.setText(event.getEventTitle() != null ? event.getEventTitle() : "(No Title)");
//            date.setText("Date: " + (event.getEventDate() != null ? event.getEventDate() : "-"));
//            desc.setText(event.getEventDescription() != null ? event.getEventDescription() : "");
//
//            // Show edit/delete only for creator
//            if (currentUser != null && event.getOwnerId() != null &&
//                    event.getOwnerId().equals(currentUser.getUid())) {
//                edit.setVisibility(View.VISIBLE);
//                delete.setVisibility(View.VISIBLE);
//            } else {
//                edit.setVisibility(View.GONE);
//                delete.setVisibility(View.GONE);
//            }
//
//            edit.setOnClickListener(v -> {
//                Toast.makeText(MainActivity.this, "Edit: " + event.getEventTitle(), Toast.LENGTH_SHORT).show();
//                // TODO: open edit screen
//            });
//
//            delete.setOnClickListener(v -> {
//                if (event.getId() != null) {
//                    databaseReference.child(event.getId()).removeValue()
//                            .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Event deleted", Toast.LENGTH_SHORT).show())
//                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Delete failed", Toast.LENGTH_SHORT).show());
//                }
//            });
//
//            return convertView;
//        }
//    }
//}



package com.example.eventmanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventListView = findViewById(R.id.eventListView);
        addEventFab = findViewById(R.id.addEventFab);
        myevent=findViewById(R.id.myEventsBtn);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new EventAdapter();
        eventListView.setAdapter(adapter);

        showEvents();

        addEventFab.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please login to add events", Toast.LENGTH_SHORT).show();
            }
        });
        myevent.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, MyEventsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please login to see your events", Toast.LENGTH_SHORT).show();
            }
        });

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
                Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
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
        @PropertyName("OwnerID")
        private String ownerId;

        public Event() {
            // Empty constructor for Firebase
        }

        public Event(String title, String description, String startDate, String endDate, String venue, String ownerId) {
            this.title = title;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.venue = venue;
//            this.organizer = organizer;
            this.ownerId=ownerId;
        }

        // Getters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getVenue() { return venue; }
//        public String getOrganizer() { return organizer; }
        public String getOwnerId() { return ownerId; }

        public void setOwnerId(String ownerId) {   this.ownerId = ownerId; }
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
