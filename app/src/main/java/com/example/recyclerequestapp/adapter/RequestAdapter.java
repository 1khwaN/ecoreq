// com.example.recyclerequestapp.adapter.RequestAdapter.java (ADMIN SIDE ADAPTER)
package com.example.recyclerequestapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.R;
import com.example.recyclerequestapp.RequestDetailActivity; // Changed from RequestDetailsActivity? Confirm this name
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;
import com.example.recyclerequestapp.SharedPrefManager; // Import SharedPrefManager
import com.example.recyclerequestapp.LoginActivity; // Import LoginActivity for redirect
import com.example.recyclerequestapp.model.User; // Ensure User model is accessible
import com.example.recyclerequestapp.model.RecyclableItem; // Ensure RecyclableItem model is accessible


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<Request> requestList;
    private OnItemClickListener listener;
    private String authToken; // This will store the RAW token (no "Bearer ")

    // Interface for item click events (Admin side)
    public interface OnItemClickListener {
        void onItemClick(Request request); // Admin needs the full Request object to fetch details
    }

    // Corrected Constructor: Get token from SharedPrefManager
    public RequestAdapter(Context context, List<Request> requestList, OnItemClickListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;

        // Fetch token from SharedPrefManager
        SharedPrefManager spm = SharedPrefManager.getInstance(context.getApplicationContext());
        User loggedInUser = spm.getUser();
        if (loggedInUser != null) {
            this.authToken = loggedInUser.getToken();
        } else {
            // Handle case where token is not available (e.g., redirect to login)
            Toast.makeText(context, "Authentication token missing. Please log in again.", Toast.LENGTH_LONG).show();
            // This might need to be handled in the calling Activity, as adapters shouldn't start activities that clear tasks
            // For now, logging, but consider how the main Admin activity handles this
            Log.e("RequestAdapter", "Auth token is null in RequestAdapter constructor!");
            // Optionally, you could broadcast an intent or use a callback to the Activity
            // context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
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
        holder.bind(request, listener); // CALL THE BIND METHOD HERE
    }

    private void showPopupMenu(View view, Request request, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.request_options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.option_details) {
                // The listener.onItemClick(request) (used by RequestViewHolder.bind)
                // is typically for the main click. For the menu, you handle it directly.
                // The Admin's RequestDetailActivity should also fetch details based on ID,
                // and its API calls need to be fixed to not add "Bearer "
                Intent intent = new Intent(context, RequestDetailActivity.class);
                intent.putExtra("request_id", request.getRequestId());
                // Pass individual fields if RequestDetailActivity expects them separately
                // Or simply pass the request object if it's Parcelable/Serializable
                // intent.putExtra("request_object", request); // If Request is Parcelable/Serializable
                context.startActivity(intent);
                return true;
            } else if (itemId == R.id.option_cancel) {
                cancelRequest(request, position);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }


    private void cancelRequest(Request request, int position) {
        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(context, "Authentication token missing for cancellation. Please log in again.", Toast.LENGTH_LONG).show();
            // Consider directing to login screen or informing the activity to do so
            return;
        }

        // CORRECTED: ApiUtils.getRequestService(authToken) already adds "Bearer "
        // And ensure RequestService.cancelRequest also takes just the raw token
        RequestService requestService = ApiUtils.getRequestService(authToken);
        Call<Request> call = requestService.cancelRequest(authToken, request.getRequestId(), "cancelled"); // Check RequestService.cancelRequest method signature.
        // If it has @Header("Authorization") String auth, pass authToken (raw)
        // If it combines the header internally, it might not need the auth token here.

        call.enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if (response.isSuccessful()) {
                    // Update only if backend confirms cancellation
                    if (response.body() != null && "cancelled".equals(response.body().getStatus())) {
                        requestList.get(position).setStatus("cancelled");
                        notifyItemChanged(position);
                        Toast.makeText(context, "Request cancelled successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Request cancelled, but status update from server was unexpected.", Toast.LENGTH_SHORT).show();
                        Log.w("CancelRequest", "Server response for cancel was successful but status not 'cancelled'.");
                        // You might still want to refresh the list or update UI based on your needs
                    }
                } else {
                    String errorMessage = "Failed to cancel request: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Session expired or unauthorized. Please log in again.";
                        // Trigger logout and redirect via the activity context if possible
                        SharedPrefManager.getInstance(context).logout();
                        context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("CancelRequest", "Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("CancelRequest", "Could not read error body", e);
                    }
                    Log.e("CancelRequest", "HTTP Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                Toast.makeText(context, "Network error during cancellation: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CancelRequest", "Network failure: ", t);
            }
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

            // Check for nulls before accessing nested objects to prevent NullPointerExceptions
            String username = (request.getUser() != null && request.getUser().getUsername() != null) ? request.getUser().getUsername() : "ID: " + request.getUserId();
            text_user_id.setText("User: " + username);

            String itemName = (request.getItem() != null && request.getItem().getItemName() != null) ? request.getItem().getItemName() : "ID: " + request.getItemId();
            text_item_id.setText("Item: " + itemName);

            text_address.setText("Address: " + (request.getAddress() != null ? request.getAddress() : "N/A"));
            text_request_date.setText("Date: " + (request.getRequestDate() != null ? request.getRequestDate() : "N/A"));
            text_status.setText("Status: " + (request.getStatus() != null ? request.getStatus() : "N/A"));

            // Use getDisplayWeight() etc. if they format the string for display
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