package com.example.money;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 厂商适配器，用于显示厂商列表。
 */
public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder> {

    private List<String> supplierList;

    public SupplierAdapter(List<String> supplierList) {
        this.supplierList = supplierList;
    }

    @Override
    public SupplierViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SupplierViewHolder holder, int position) {
        String supplierName = supplierList.get(position);
        holder.supplierNameTextView.setText(supplierName);
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public static class SupplierViewHolder extends RecyclerView.ViewHolder {

        TextView supplierNameTextView;

        public SupplierViewHolder(View itemView) {
            super(itemView);
            supplierNameTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
