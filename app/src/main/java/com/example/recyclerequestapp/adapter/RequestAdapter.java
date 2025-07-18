package com.example.recyclerequestapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclerequestapp.R;
import com.example.recyclerequestapp.model.Request;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private List<Request> requestList;
    private OnItemClickListener listener;

    // Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(Request request);
    }

    // Constructor with listener
    public RequestAdapter(List<Request> requestList, OnItemClickListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.bind(request, listener); // Pass request and listener to ViewHolder's bind method
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void setRequestList(List<Request> newRequestList) {
        this.requestList.clear();
        this.requestList.addAll(newRequestList);
        notifyDataSetChanged();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        // Declare your TextViews as they are named in item_request.xml based on your screenshots
        TextView text_request_id;
        TextView text_user_id; // This will now display username
        TextView text_item_id; // This will now display item name
        TextView text_address;
        TextView text_request_date;
        TextView text_status;
        TextView text_weight;
        TextView text_total_price;
        TextView text_notes;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link your views from item_request.xml
            text_request_id = itemView.findViewById(R.id.text_request_id);
            text_user_id = itemView.findViewById(R.id.text_user_id);
            text_item_id = itemView.findViewById(R.id.text_item_id);
            text_address = itemView.findViewById(R.id.text_address);
            text_request_date = itemView.findViewById(R.id.text_request_date);
            text_status = itemView.findViewById(R.id.text_status);
            text_weight = itemView.findViewById(R.id.text_weight);
            text_total_price = itemView.findViewById(R.id.text_total_price);
            text_notes = itemView.findViewById(R.id.text_notes);
        }

        public void bind(final Request request, final OnItemClickListener listener) {
            // Populate your views with data from the Request object
            text_request_id.setText("Request ID: " + request.getRequestId());

            // Use username and item name from the (now enriched) Request model
            text_user_id.setText("User: " + (request.getUsername() != null ? request.getUsername() : "ID: " + request.getUserId()));
            text_item_id.setText("Item: " + (request.getItemName() != null ? request.getItemName() : "ID: " + request.getItemId()));

            text_address.setText("Address: " + request.getAddress());
            text_request_date.setText("Date: " + request.getRequestDate());
            text_status.setText("Status: " + request.getStatus());
            text_weight.setText("Weight: " + request.getDisplayWeight());
            text_total_price.setText("Total Price: " + request.getDisplayTotalPrice());
            text_notes.setText("Notes: " + request.getDisplayNotes());

            // Set the click listener on the item view
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(request);
                }
            });
        }
    }
}