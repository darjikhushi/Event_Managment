package com.example.eventmanagment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {

    private Context context;
    private List<AttendeeModel> attendeeList;

    public AttendeeAdapter(Context context, List<AttendeeModel> attendeeList) {
        this.context = context;
        this.attendeeList = attendeeList;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.attendee_item, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        AttendeeModel attendee = attendeeList.get(position);

        holder.attendeeName.setText(attendee.getParticipantName());
        holder.attendeePhone.setText(attendee.getMobile());
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView attendeeName, attendeePhone;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);

            attendeeName = itemView.findViewById(R.id.attendeeName);
            attendeePhone = itemView.findViewById(R.id.attendeePhone);
        }
    }
}

