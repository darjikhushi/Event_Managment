package com.example.eventmanagment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EventSliderAdapter extends RecyclerView.Adapter<EventSliderAdapter.ViewHolder> {

    private Context context;
    private List<MainActivity.Event> eventList;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEdit(MainActivity.Event event);
        void onDelete(MainActivity.Event event);
    }

    public EventSliderAdapter(Context context, List<MainActivity.Event> eventList, OnEventClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_event_slider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainActivity.Event event = eventList.get(position);

        holder.title.setText(event.getTitle());
//        holder.startDate.setText("Start Date: " + event.getStartDate());
//        holder.endDate.setText("End Date: " + event.getEndDate());
//        holder.time.setText("Time: " + event.getStartTime());
//        holder.time.setText("Time: " + event.getEndTime());
//        holder.venue.setText("Venue: " + event.getVenue());
        holder.description.setText(event.getDescription());

        if (event.getImageBase64() != null) {
            Glide.with(context).load(event.getImageBase64()).into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("title", event.getTitle());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("venue", event.getVenue());
            intent.putExtra("startDate", event.getStartDate());
            intent.putExtra("endDate", event.getEndDate());
            intent.putExtra("time", event.getStartTime());
            intent.putExtra("image", event.getImageBase64());
            context.startActivity(intent);
        });


//        holder.edit.setOnClickListener(v -> listener.onEdit(event));
//        holder.delete.setOnClickListener(v -> listener.onDelete(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, startDate, endDate, time, venue, description;
        ImageButton edit, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.eventImage);
            title = itemView.findViewById(R.id.eventTitle);
//            startDate = itemView.findViewById(R.id.eventStartDate);
//            endDate = itemView.findViewById(R.id.eventEndDate);
//            time = itemView.findViewById(R.id.eventstrtTime);
//            venue = itemView.findViewById(R.id.eventVenue);
            description = itemView.findViewById(R.id.eventDescription);
//            edit = itemView.findViewById(R.id.btnEdit);
//            delete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
