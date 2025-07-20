// com.example.recyclerequestapp.adapter.RequestAdapter.java (ADMIN SIDE ADAPTER)
package com.example.recyclerequestapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.R;
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.model.User; // Ensure User model is accessible
import com.example.recyclerequestapp.model.RecyclableItem; // Ensure RecyclableItem model is accessible

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<Request> requestList;
    private OnItemClickListener listener; // This listener is for the Admin's click action

    // Interface for item click events (Admin side)
    public interface OnItemClickListener {
        void onItemClick(Request request); // Admin needs the full Request object to fetch details
    }

    public RequestAdapter(Context context,List<Request> requestList, OnItemClickListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.bind(request, listener); // Delegate binding and click handling to ViewHolder
    }

    @Override
    public int getItemCount(){
        return requestList.size();
    }

    public void setRequestList(List<Request> newRequestList) {
        this.requestList.clear();
        this.requestList.addAll(newRequestList);
        notifyDataSetChanged();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        // Declare your TextViews as they are named in request_item.xml
        TextView text_request_id;
        TextView text_user_id; // For username
        TextView text_item_id; // For item name
        TextView text_address;
        TextView text_request_date;
        TextView text_status;
        TextView text_weight;
        TextView text_total_price;
        TextView text_notes;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link your views from request_item.xml
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
            text_request_id.setText("Request ID: #" + request.getRequestId());

            String username = (request.getUser() != null && request.getUser().getUsername() != null) ? request.getUser().getUsername() : "ID: " + request.getUserId();
            text_user_id.setText("User: " + username);

            String itemName = (request.getItem() != null && request.getItem().getItemName() != null) ? request.getItem().getItemName() : "ID: " + request.getItemId();
            text_item_id.setText("Item: " + itemName);

            text_address.setText("Address: " + (request.getAddress() != null ? request.getAddress() : "N/A"));
            text_request_date.setText("Date: " + (request.getRequestDate() != null ? request.getRequestDate() : "N/A"));
            text_status.setText("Status: " + (request.getStatus() != null ? request.getStatus() : "N/A"));

            text_weight.setText("Weight: " + (request.getDisplayWeight() != null ? request.getDisplayWeight() : "N/A"));
            text_total_price.setText("Total Price: " + (request.getDisplayTotalPrice() != null ? request.getDisplayTotalPrice() : "N/A"));
            text_notes.setText("Notes: " + (request.getDisplayNotes() != null ? request.getDisplayNotes() : "N/A"));

            // Set the click listener on the item view using the adapter's listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(request); // This will call the listener in ViewAllRequestsActivity
                }
            });
        }
    }
}