// com.example.recyclerequestapp.adapter.UserRequestListAdapter.java (USER SIDE ADAPTER)
package com.example.recyclerequestapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.R;
import com.example.recyclerequestapp.RequestDetailActivity; // User side detail activity
import com.example.recyclerequestapp.model.Request;

import java.util.List;

public class UserRequestListAdapter extends RecyclerView.Adapter<UserRequestListAdapter.UserRequestViewHolder> {

    private Context context;
    private List<Request> requestList;

    public UserRequestListAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public UserRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the same request_item.xml if it suits both admin and user list display,
        // or create a new request_item_user.xml if the user list display is simpler.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new UserRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRequestViewHolder holder, int position) {
        Request request = requestList.get(position);

        // Populate views directly for the list item
        holder.text_request_id.setText("Request ID: #" + request.getRequestId());
        holder.text_status.setText("Status: " + (request.getStatus() != null ? request.getStatus() : "N/A"));
        holder.text_request_date.setText("Date: " + (request.getRequestDate() != null ? request.getRequestDate() : "N/A"));

        // Get username and item name from nested objects, handling nulls
        String username = (request.getUser() != null && request.getUser().getUsername() != null) ? request.getUser().getUsername() : "ID: " + request.getUserId();
        holder.text_user_id.setText("User: " + username);

        String itemName = (request.getItem() != null && request.getItem().getItemName() != null) ? request.getItem().getItemName() : "ID: " + request.getItemId();
        holder.text_item_id.setText("Item: " + itemName);

        // Show/hide or set "N/A" for other fields relevant for the user's list view
        holder.text_address.setText("Address: " + (request.getAddress() != null ? request.getAddress() : "N/A"));
        holder.text_weight.setText("Weight: " + (request.getDisplayWeight() != null ? request.getDisplayWeight() : "N/A"));
        holder.text_total_price.setText("Total Price: " + (request.getDisplayTotalPrice() != null ? request.getDisplayTotalPrice() : "N/A"));
        holder.text_notes.setVisibility(View.GONE); // Example: Hide notes in the list item, only show in detail

        // Set click listener for the item to open RequestDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RequestDetailActivity.class);
            // Pass ALL necessary data for RequestDetailActivity via extras
            intent.putExtra("requestId", request.getRequestId());
            intent.putExtra("userName", username); // Pass username
            intent.putExtra("itemName", itemName); // Pass item name

            intent.putExtra("status", request.getStatus());
            intent.putExtra("totalPrice", request.getTotalPrice() != null ? request.getTotalPrice() : 0.0);
            intent.putExtra("date", request.getRequestDate());
            intent.putExtra("address", request.getAddress());
            intent.putExtra("notes", request.getNotes()); // Pass original notes, not displayNotes if different
            intent.putExtra("weight", request.getWeight() != null ? request.getWeight() : 0.0);
            context.startActivity(intent);
        });
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

    public static class UserRequestViewHolder extends RecyclerView.ViewHolder {
        // Reuse the same IDs from request_item.xml if the list item layout is the same
        TextView text_request_id;
        TextView text_user_id;
        TextView text_item_id;
        TextView text_address;
        TextView text_request_date;
        TextView text_status;
        TextView text_weight;
        TextView text_total_price;
        TextView text_notes;

        public UserRequestViewHolder(@NonNull View itemView) {
            super(itemView);
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
    }
}