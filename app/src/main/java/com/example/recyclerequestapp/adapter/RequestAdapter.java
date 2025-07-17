package com.example.recyclerequestapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclerequestapp.R; // Make sure this points to your R file
import com.example.recyclerequestapp.model.Request;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private List<Request> requestList;

    public RequestAdapter(List<Request> requestList) {
        this.requestList = requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
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
        holder.requestIdTextView.setText("Request ID: " + request.getRequestId());
        holder.userIdTextView.setText("User ID: " + request.getUserId());
        holder.itemIdTextView.setText("Item ID: " + request.getItemId()); // You might want to display item name instead
        holder.addressTextView.setText("Address: " + request.getAddress());
        holder.requestDateTextView.setText("Date: " + request.getRequestDate());
        holder.statusTextView.setText("Status: " + request.getStatus());
        holder.weightTextView.setText("Weight: " + (request.getWeight() != null ? request.getWeight() + " kg" : "N/A"));
        holder.totalPriceTextView.setText("Total Price: RM " + (request.getTotalPrice() != null ? String.format("%.2f", request.getTotalPrice()) : "N/A"));
        holder.notesTextView.setText("Notes: " + (request.getNotes() != null && !request.getNotes().isEmpty() ? request.getNotes() : "None"));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView requestIdTextView;
        TextView userIdTextView;
        TextView itemIdTextView;
        TextView addressTextView;
        TextView requestDateTextView;
        TextView statusTextView;
        TextView weightTextView;
        TextView totalPriceTextView;
        TextView notesTextView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestIdTextView = itemView.findViewById(R.id.text_request_id);
            userIdTextView = itemView.findViewById(R.id.text_user_id);
            itemIdTextView = itemView.findViewById(R.id.text_item_id);
            addressTextView = itemView.findViewById(R.id.text_address);
            requestDateTextView = itemView.findViewById(R.id.text_request_date);
            statusTextView = itemView.findViewById(R.id.text_status);
            weightTextView = itemView.findViewById(R.id.text_weight);
            totalPriceTextView = itemView.findViewById(R.id.text_total_price);
            notesTextView = itemView.findViewById(R.id.text_notes);
        }
    }
}
