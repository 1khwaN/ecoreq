package com.example.recyclerequestapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.R;
import com.example.recyclerequestapp.RequestDetailActivity;
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.RequestViewHolder> {

    private List<Request> requestList;
    private Context context;
    private String authToken = "Bearer 5a13b9cb-e690-4f09-8437-e0d3594c1784"; // You may want to store this in SharedPreferences instead

    public UserRequestAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.tvDate.setText(request.getRequestDate());
        holder.tvStatus.setText(request.getStatus());

        // Long press to show popup menu
        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(v, request, position);
            return true;
        });
    }

    private void showPopupMenu(View view, Request request, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.request_options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.option_details) {
                Intent intent = new Intent(context, RequestDetailActivity.class);
                intent.putExtra("request_id", request.getRequestId());
                intent.putExtra("date", request.getRequestDate());
                intent.putExtra("status", request.getStatus());
                intent.putExtra("totalPrice", request.getTotalPrice());
                intent.putExtra("address", request.getAddress());
                intent.putExtra("notes", request.getNotes());
                intent.putExtra("weight", request.getWeight());
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
        RequestService RequestService = ApiUtils.getRequestService(authToken);
        Call<Request> call = RequestService.cancelRequest(authToken, request.getRequestId(), "cancelled");

        call.enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if (response.isSuccessful()) {
                    requestList.get(position).setStatus("cancelled");
                    notifyItemChanged(position);
                    Toast.makeText(context, "Request cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to cancel: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("CancelRequest", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CancelRequest", "Failure: ", t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
