package com.example.recyclerequestapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.R;
import com.example.recyclerequestapp.model.RecyclableItem;

import java.util.List;

public class RecyclableItemAdapter extends RecyclerView.Adapter<RecyclableItemAdapter.ItemViewHolder> {

    private Context context;
    private List<RecyclableItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RecyclableItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecyclableItemAdapter(Context context, List<RecyclableItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclable_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        RecyclableItem item = itemList.get(position);

        holder.tvItemName.setText("Item Name: " + item.getItemName());
        holder.tvItemPrice.setText(String.format("Price per KG: $%.2f", item.getPricePerKg()));
        holder.tvItemId.setText("ID: " + item.getId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemPrice, tvItemId;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvItemId = itemView.findViewById(R.id.tv_item_id);
        }
    }

    public void setItems(List<RecyclableItem> newItems) {
        this.itemList.clear();
        this.itemList.addAll(newItems);
        notifyDataSetChanged();
    }
}