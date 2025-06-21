package com.myapp.pizzahut;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.pizzahut.model.CartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SelectedItemsAdapter extends RecyclerView.Adapter<SelectedItemsAdapter.ViewHolder> {
    private List<CartItem> selectedItems;

    public SelectedItemsAdapter(List<CartItem> selectedItems) {
        this.selectedItems = selectedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = selectedItems.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText("Rs. " + item.getProductPrice());
        holder.tvQuantity.setText("Qty: " + item.getQuantity());

        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            Picasso.get().load("file://" + item.getImagePath()).into(holder.ivProduct);
        } else {
            holder.ivProduct.setImageResource(R.drawable.baseline_add_photo_alternate_24);
        }

    }

    @Override
    public int getItemCount() {
        return selectedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView ivProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.selected_item_name);
            tvPrice = itemView.findViewById(R.id.selected_item_price);
            tvQuantity = itemView.findViewById(R.id.selected_item_quantity);
            ivProduct = itemView.findViewById(R.id.selected_item_image);
        }
    }
}
