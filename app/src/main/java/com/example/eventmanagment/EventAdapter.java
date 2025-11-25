package com.example.eventmanagment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.*;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<MainActivity.Event> eventList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public EventAdapter(List<MainActivity.Event> eventList, OnItemClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_event_item, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder h, int pos) {

        MainActivity.Event e = eventList.get(pos);

        h.title.setText(e.getTitle());
        h.desc.setText(e.getDescription());
        h.startDate.setText("Start: " + e.getStartDate());
        h.endDate.setText("End: " + e.getEndDate());
        h.startTime.setText("Start Time: " + e.getStartTime());
        h.endTime.setText("End Time: " + e.getEndTime());
        h.venue.setText("Venue: " + e.getVenue());

        if (e.getImageBase64() != null && !e.getImageBase64().isEmpty()) {
            byte[] bytes = Base64.decode(e.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            h.image.setImageBitmap(bitmap);
        }

        h.edit.setOnClickListener(v -> listener.onEditClick(pos));
        h.delete.setOnClickListener(v -> listener.onDeleteClick(pos));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, startDate, endDate, startTime, endTime, venue;
        ImageView image;
        ImageButton edit, delete;

        public EventViewHolder(View v) {
            super(v);

            title = v.findViewById(R.id.eventTitle);
            desc = v.findViewById(R.id.eventDescription);
            startDate = v.findViewById(R.id.eventStartDate);
            endDate = v.findViewById(R.id.eventEndDate);
            startTime = v.findViewById(R.id.eventstrtTime);
            endTime = v.findViewById(R.id.eventendtTime);
            venue = v.findViewById(R.id.eventVenue);

            image = v.findViewById(R.id.eventImage);
            edit = v.findViewById(R.id.btnEdit);
            delete = v.findViewById(R.id.btnDelete);
        }
    }
}
