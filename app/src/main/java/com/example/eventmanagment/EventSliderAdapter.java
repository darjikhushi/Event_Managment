//
//package com.example.eventmanagment;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.util.List;
//
//public class EventSliderAdapter extends RecyclerView.Adapter<EventSliderAdapter.ViewHolder> {
//
//    private Context context;
//    private List<MainActivity.Event> eventList;
//    private OnEventClickListener listener;
//
//    public interface OnEventClickListener {
//        void onEdit(MainActivity.Event event);
//        void onDelete(MainActivity.Event event);
//    }
//
//    public EventSliderAdapter(Context context, List<MainActivity.Event> eventList, OnEventClickListener listener) {
//        this.context = context;
//        this.eventList = eventList;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.activity_event_slider, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        MainActivity.Event event = eventList.get(position);
//
//        // Show event title
//        holder.title.setText(event.getTitle());
//        holder.venue.setText(event.getVenue());
////        holder.image.setImageBitmap(event.getImageBase64());// commented by you
//
//        // Load Base64 image
//        if (event.getImageBase64() != null && !event.getImageBase64().isEmpty()) {
//            Glide.with(context).load(event.getImageBase64()).into(holder.image);
//        }
//
//        // CLICK → OPEN DETAILS PAGE (sending event Firebase ID)
//        holder.title.setOnClickListener(v -> {
//            Intent intent = new Intent(context, EventDetailsActivity.class);
//            intent.putExtra("eventTitle", event.getTitle());   // ✔ send title
//            context.startActivity(intent);
//        });
//
//        holder.venue.setOnClickListener(v -> {
//            Intent intent = new Intent(context, EventDetailsActivity.class);
//            intent.putExtra("eventVanue", event.getVenue());   // ✔ send title
//            context.startActivity(intent);
//        });
//
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, EventDetailsActivity.class);
//            intent.putExtra("id", eventList.get(position).getId()); // << pass ID
//            context.startActivity(intent);
//        });
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return eventList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView image;
//        TextView title, startDate, endDate, time, venue, description;
//        ImageButton edit, delete;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            image = itemView.findViewById(R.id.eventImage);
//            title = itemView.findViewById(R.id.eventTitle);
//            venue = itemView.findViewById(R.id.eventVenue);
//
//        }
//    }
//}
//
//
package com.example.eventmanagment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

        // Set text
        holder.title.setText(event.getTitle());
        holder.venue.setText(event.getVenue());

        // Load Base64 Image
        if (event.getImageBase64() != null && !event.getImageBase64().isEmpty()) {
            Glide.with(context).load(event.getImageBase64()).into(holder.image);
        }

        // Common click function
        View.OnClickListener openDetails = v -> {
            Log.d("SLIDER_DEBUG", "Clicked eventId = " + event.getId());

            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("id", event.getId());               // pass eventId
            intent.putExtra("eventTitle", event.getTitle());   // pass event title
            context.startActivity(intent);
        };

        // Apply same click to title, venue, and card
        holder.title.setOnClickListener(openDetails);
        holder.venue.setOnClickListener(openDetails);
        holder.itemView.setOnClickListener(openDetails);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, venue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.eventImage);
            title = itemView.findViewById(R.id.eventTitle);
            venue = itemView.findViewById(R.id.eventVenue);
        }
    }
}

