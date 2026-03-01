package com.example.cs_360project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.VH> {

    public static class Item {
        public long id;
        public String name;
        public int qty;

        public Item(long id, String name, int qty) {
            this.id = id;
            this.name = name;
            this.qty = qty;
        }
    }

    public interface ItemClickListener {
        void onItemClick(Item item);
        void onItemLongClick(Item item);
    }

    private final ArrayList<Item> items = new ArrayList<>();
    private final ItemClickListener listener;

    public InventoryAdapter(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(ArrayList<Item> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_cell, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Item it = items.get(position);
        holder.tvName.setText(it.name);
        holder.tvQty.setText("Qty: " + it.qty);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(it));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(it);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvQty;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQty = itemView.findViewById(R.id.tvQty);
        }
    }
}