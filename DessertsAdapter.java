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

public class DessertsAdapter extends RecyclerView.Adapter<DessertsAdapter.DessertViewHolder> {
    private Context context;
    private List<Product> productList;
   // private FirebaseFirestore db;

    public DessertsAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
       // this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public DessertViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pizza_item, parent, false);
        return new DessertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DessertViewHolder holder, int position) {
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
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context,SingleProductActivity.class);
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

    public static class DessertViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productPrice;
        ImageView productImage;
       // ImageButton addToCartButton;

        public DessertViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }


}
