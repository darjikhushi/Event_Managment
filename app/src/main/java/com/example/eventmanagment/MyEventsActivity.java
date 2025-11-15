//package com.example.eventmanagment;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.*;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//import com.google.firebase.database.PropertyName;
//
//import java.util.ArrayList;
//
//public class MyEventsActivity extends AppCompatActivity {
//
//    private ListView myEventListView;
//    private FloatingActionButton fabAddEvent;
//    private ArrayList<Event> myEventList;
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference databaseReference;
//    private FirebaseUser currentUser;
//    private MyEventAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_events);
//
//        // ✅ FIND VIEWS (this was missing)
//        myEventListView = findViewById(R.id.eventListView);
//        fabAddEvent = findViewById(R.id.addEventFab);
//
//        myEventList = new ArrayList<>();
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("Events");
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        // ✅ Prevent crash if ListView missing in XML
//        if (myEventListView == null) {
//            Toast.makeText(this, "Error: ListView not found in layout", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        adapter = new MyEventAdapter();
//        myEventListView.setAdapter(adapter);
//
//        if (currentUser != null) {
//            fetchMyEvents();
//        } else {
//            Toast.makeText(this, "Please login to view your events", Toast.LENGTH_SHORT).show();
//        }
//
//        fabAddEvent.setOnClickListener(v -> {
//            Intent intent = new Intent(MyEventsActivity.this, AddEventActivity.class);
//            startActivity(intent);
//        });
//    }
//
//    private void fetchMyEvents() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                myEventList.clear();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Event e = data.getValue(Event.class);
//                    if (e != null && currentUser != null &&
//                            e.getOrganizer() != null &&
//                            e.getOrganizer().equals(currentUser.getUid())) {
//
//                        e.setId(data.getKey());
//                        myEventList.add(e);
//                    }
//                }
//                adapter.notifyDataSetChanged();
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
//
//    // ==============================
//    // Event Model
//    // ==============================
//    public static class Event {
//        private String id;
//
//        @PropertyName("Title")
//        private String title;
//
//        @PropertyName("Description")
//        private String description;
//
//        @PropertyName("StartDate")
//        private String startDate;
//
//        @PropertyName("EndDate")
//        private String endDate;
//
//        @PropertyName("Venue")
//        private String venue;
//
//        @PropertyName("Organizer")
//        private String organizer;
//
//        public Event() {}
//
//        public Event(String title, String description, String startDate, String endDate, String venue, String organizer) {
//            this.title = title;
//            this.description = description;
//            this.startDate = startDate;
//            this.endDate = endDate;
//            this.venue = venue;
//            this.organizer = organizer;
//        }
//
//        public String getId() { return id; }
//        public void setId(String id) { this.id = id; }
//
//        public String getTitle() { return title; }
//        public String getDescription() { return description; }
//        public String getStartDate() { return startDate; }
//        public String getEndDate() { return endDate; }
//        public String getVenue() { return venue; }
//        public String getOrganizer() { return organizer; }
//
//        public void setTitle(String title) { this.title = title; }
//        public void setDescription(String description) { this.description = description; }
//        public void setStartDate(String startDate) { this.startDate = startDate; }
//        public void setEndDate(String endDate) { this.endDate = endDate; }
//        public void setVenue(String venue) { this.venue = venue; }
//        public void setOrganizer(String organizer) { this.organizer = organizer; }
//    }
//
//    // ==============================
//    // Adapter
//    // ==============================
//    private class MyEventAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return myEventList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return myEventList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, android.view.ViewGroup parent) {
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(R.layout.activity_event_item, parent, false);
//            }
//
//            Event event = myEventList.get(position);
//
//            TextView title = convertView.findViewById(R.id.tvEventTitle);
//            TextView date = convertView.findViewById(R.id.tvEventDate);
//            TextView desc = convertView.findViewById(R.id.tvEventDesc);
//            ImageButton edit = convertView.findViewById(R.id.btnEdit);
//            ImageButton delete = convertView.findViewById(R.id.btnDelete);
//
//            title.setText(event.getTitle() != null ? event.getTitle() : "(No Title)");
//            date.setText("Date: " + (event.getStartDate() != null ? event.getStartDate() : "-"));
//            desc.setText(event.getDescription() != null ? event.getDescription() : "");
//
//            edit.setVisibility(View.VISIBLE);
//            delete.setVisibility(View.VISIBLE);
//
//            edit.setOnClickListener(v -> {
//                Intent intent = new Intent(MyEventsActivity.this, EditEventActivity.class);
//                intent.putExtra("eventId", event.getId());
//                intent.putExtra("title", event.getTitle());
//                intent.putExtra("desc", event.getDescription());
//                intent.putExtra("startDate", event.getStartDate());
//                intent.putExtra("endDate", event.getEndDate());
//                intent.putExtra("venue", event.getVenue());
//                startActivity(intent);
//            });
//
//            delete.setOnClickListener(v -> {
//                if (event.getId() != null) {
//                    databaseReference.child(event.getId()).removeValue()
//                            .addOnSuccessListener(aVoid ->
//                                    Toast.makeText(MyEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show())
//                            .addOnFailureListener(e ->
//                                    Toast.makeText(MyEventsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show());
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        eventsContainer = findViewById(R.id.eventsContainer);
        myEventList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            fetchMyEvents();
        } else {
            Toast.makeText(this, "Please login to view your events", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMyEvents() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myEventList.clear();
                eventsContainer.removeAllViews();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Event e = data.getValue(Event.class);
                    if (e != null && e.getOrganizer() != null &&
                            e.getOrganizer().equals(currentUser.getUid())) {

                        e.setId(data.getKey());
                        myEventList.add(e);

                        addEventCard(e);
                    }
                }

                if (myEventList.isEmpty()) {
                    Toast.makeText(MyEventsActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
            intent.putExtra("title", event.getTitle());
            intent.putExtra("desc", event.getDescription());
            intent.putExtra("startDate", event.getStartDate());
            intent.putExtra("endDate", event.getEndDate());
            intent.putExtra("venue", event.getVenue());
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

    // Event Model
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

        @PropertyName("Organizer")
        private String organizer;

        public Event() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getVenue() { return venue; }
        public String getOrganizer() { return organizer; }
    }
}
