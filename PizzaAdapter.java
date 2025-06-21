package com.myapp.pizzahut;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.myapp.pizzahut.model.Product;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.PizzaViewHolder> {
    private Context context;
    private List<Product> productList;
    private FirebaseFirestore db;

    public PizzaAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public PizzaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pizza_item, parent, false);
        return new PizzaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PizzaViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText("Rs. " + product.getPrice());


        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Picasso.get()
                        .load(imgFile)
                        .placeholder(R.drawable.baseline_add_photo_alternate_24)
                        .error(R.drawable.baseline_add_photo_alternate_24)
                        .into(holder.productImage);
            } else {
                holder.productImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
            }
        } else {
            holder.productImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
        }

       // holder.addToCartButton.setOnClickListener(v -> addToCart(product));

        holder.addToCartButton.setOnClickListener(v -> {
            addToCart(product);
            holder.addToCartButton.setImageResource(R.drawable.ic_cart1);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleProductActivity.class);
            intent.putExtra("productName", product.getName());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productPrice", product.getPrice());
            intent.putExtra("imagePath", product.getImagePath());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    public static class PizzaViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productPrice;
        ImageView productImage;
        ImageButton addToCartButton;

        public PizzaViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }

    private void addToCart(Product product) {
        if (SignInActivity.loggedInUserEmail == null) {
            Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productName", product.getName());
        cartItem.put("productPrice", product.getPrice());
        cartItem.put("quantity", 1);
        cartItem.put("userEmail", SignInActivity.loggedInUserEmail);

        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            File imgFile = new File(product.getImagePath());
            if (imgFile.exists()) {
                cartItem.put("imagePath", product.getImagePath());
            } else {
                Log.e("addToCart", "Image file does not exist: " + product.getImagePath());
                cartItem.put("imagePath", R.drawable.baseline_add_photo_alternate_24);
            }
        } else {
            cartItem.put("imagePath", R.drawable.baseline_add_photo_alternate_24);
        }

        db.collection("cart").document(UUID.randomUUID().toString())
                .set(cartItem, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error adding to cart", e));
    }
}
