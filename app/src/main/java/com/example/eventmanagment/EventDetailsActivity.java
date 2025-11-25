package com.example.eventmanagment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EventDetailsActivity extends AppCompatActivity {

    ImageView image;

    TextView title, description, venue, startDate, endDate, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        image = findViewById(R.id.detailEventImage);
        title = findViewById(R.id.detailEventTitle);
        description = findViewById(R.id.detailEventDescription);
        venue = findViewById(R.id.detailEventVenue);
        startDate = findViewById(R.id.detailEventStartDate);
        endDate = findViewById(R.id.detailEventEndDate);
        time = findViewById(R.id.detailEventTime);

        // Receive event data
        String eventTitle = getIntent().getStringExtra("title");
        String eventDesc = getIntent().getStringExtra("description");
        String eventVenue = getIntent().getStringExtra("venue");
        String eventStart = getIntent().getStringExtra("startDate");
        String eventEnd = getIntent().getStringExtra("endDate");
        String eventTime = getIntent().getStringExtra("time");
        String eventImage = getIntent().getStringExtra("image");

        title.setText(eventTitle);
        description.setText(eventDesc);
        venue.setText("Venue: " + eventVenue);
        startDate.setText("Start Date: " + eventStart);
        endDate.setText("End Date: " + eventEnd);
        time.setText("Time: " + eventTime);

        if (eventImage != null) {
            Glide.with(this).load(eventImage).into(image);
        }
    }
}
