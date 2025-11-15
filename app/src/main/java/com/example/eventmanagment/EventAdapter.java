package com.example.eventmanagment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        MainActivity.Event event = eventList.get(position);

        holder.tvEventTitle.setText(event.getTitle());
        holder.tvEventDate.setText("Date: " + event.getDate());
        holder.tvEventDesc.setText(event.getDescription());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle, tvEventDate, tvEventDesc;
        ImageButton ivEdit, ivDelete;

        @SuppressLint("WrongViewCast")
        EventViewHolder(View itemView) {
            super(itemView);

            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventDesc = itemView.findViewById(R.id.tvEventDescription);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);

            ivEdit.setOnClickListener(v -> listener.onEditClick(getAdapterPosition()));

            ivDelete.setOnClickListener(v -> listener.onDeleteClick(getAdapterPosition()));
        }
    }
}
