package com.myapp.pizzahut;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myapp.pizzahut.model.CartItem;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private HashSet<CartItem> selectedItems = new HashSet<>();

    public CartAdapter(List<CartItem> cartItemList, FirebaseFirestore db) {
        this.cartItemList = cartItemList;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        holder.name.setText(item.getProductName());
        holder.price.setText("Rs. " + item.getProductPrice());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            Picasso.get().load("file://" + item.getImagePath()).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.baseline_add_photo_alternate_24);
        }

        holder.checkBox.setChecked(item.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (isChecked) {
                selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }
            ((CartActivity) holder.itemView.getContext()).calculateTotal();
        });

        holder.btnIncrease.setOnClickListener(v -> updateQuantity(holder, item, 1));
        holder.btnDecrease.setOnClickListener(v -> updateQuantity(holder, item, -1));
        holder.btnRemove.setOnClickListener(v -> removeItem(holder, item));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public void updateCartList(List<CartItem> newCartList) {
        this.cartItemList = newCartList;
        notifyDataSetChanged();
    }

    public HashSet<CartItem> getSelectedItems() {
        return selectedItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, quantity;
        Button btnIncrease, btnDecrease;
        ImageButton btnRemove;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cart_product_image);
            name = itemView.findViewById(R.id.cart_product_name);
            price = itemView.findViewById(R.id.cart_product_price);
            quantity = itemView.findViewById(R.id.cart_quantity);
            btnIncrease = itemView.findViewById(R.id.cart_btn_plus);
            btnDecrease = itemView.findViewById(R.id.cart_btn_minus);
            btnRemove = itemView.findViewById(R.id.cart_remove_button);
            checkBox = itemView.findViewById(R.id.cart_checkbox);
        }
    }

    private void updateQuantity(ViewHolder holder, CartItem item, int change) {
        int newQuantity = item.getQuantity() + change;
        if (newQuantity < 1) return;

        db.collection("cart")
                .whereEqualTo("userEmail", item.getUserEmail())
                .whereEqualTo("productName", item.getProductName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();

                            db.collection("cart").document(documentId)
                                    .update("quantity", newQuantity)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("CartAdapter", "Quantity updated successfully");
                                        item.setQuantity(newQuantity);
                                        notifyDataSetChanged();
                                        ((CartActivity) holder.itemView.getContext()).calculateTotal();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error updating quantity", e);
                                        Toast.makeText(holder.itemView.getContext(), "Failed to update quantity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Failed to find item in cart", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding item in cart", e);
                    Toast.makeText(holder.itemView.getContext(), "Failed to find item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void removeItem(ViewHolder holder, CartItem item) {
        db.collection("cart")
                .whereEqualTo("userEmail", item.getUserEmail())
                .whereEqualTo("productName", item.getProductName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();

                            db.collection("cart").document(documentId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        cartItemList.remove(item);
                                        notifyDataSetChanged();
                                        Toast.makeText(holder.itemView.getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                                        ((CartActivity) holder.itemView.getContext()).calculateTotal();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error removing item", e);
                                        Toast.makeText(holder.itemView.getContext(), "Failed to remove item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Item not found in cart", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding item in cart", e);
                    Toast.makeText(holder.itemView.getContext(), "Failed to find item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
