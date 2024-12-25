package com.example.money;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 库管适配器类，用于显示签署人的姓名列表。
 */
public class SignerAdapter extends RecyclerView.Adapter<SignerAdapter.SignerViewHolder> {

    private List<String> signerNames;

    public SignerAdapter(List<String> signerNames) {
        this.signerNames = signerNames;
    }

    @Override
    public SignerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signer, parent, false);
        return new SignerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SignerViewHolder holder, int position) {
        holder.signerNameTextView.setText(signerNames.get(position));
    }

    @Override
    public int getItemCount() {
        return signerNames.size();
    }

    public static class SignerViewHolder extends RecyclerView.ViewHolder {
        TextView signerNameTextView;

        public SignerViewHolder(View itemView) {
            super(itemView);
            signerNameTextView = itemView.findViewById(R.id.signerNameTextView);
        }
    }
}
