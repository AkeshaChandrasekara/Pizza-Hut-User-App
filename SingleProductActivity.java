package com.myapp.pizzahut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SingleProductActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDescription, productPrice;
    private Button addToCartButton, goToCartButton;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_prodcut);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        addToCartButton = findViewById(R.id.btn_add_to_cart);
        goToCartButton = findViewById(R.id.btn_add_to_cart2);

        String name = getIntent().getStringExtra("productName");
        String description = getIntent().getStringExtra("productDescription");
        String price = getIntent().getStringExtra("productPrice");
        String imagePath = getIntent().getStringExtra("imagePath");

        productName.setText(name);
        productDescription.setText(description);
        productPrice.setText("Rs. " + price);

        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Picasso.get().load(imgFile).into(productImage);
            } else {
                Log.e("SingleProductActivity", "Image file does not exist: " + imagePath);
                productImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
            }
        } else {
            productImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
        }

        //Picasso.get().load(imagePath).into(productImage);

        addToCartButton.setOnClickListener(v -> addToCart(name, price, imagePath));
        goToCartButton.setOnClickListener(view -> startActivity(new Intent(this, CartActivity.class)));
    }

    private void addToCart(String name, String price, String imagePath) {
        if (SignInActivity.loggedInUserEmail == null) {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productName", name);
        cartItem.put("productPrice", price);
       // cartItem.put("imagePath", imagePath);
        cartItem.put("quantity", 1);
        cartItem.put("userEmail", SignInActivity.loggedInUserEmail);

        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                cartItem.put("imagePath", imagePath);
            } else {
                Log.e("addToCart", "Image file does not exist: " + imagePath);
                cartItem.put("imagePath", R.drawable.baseline_add_photo_alternate_24);
            }
        } else {
            cartItem.put("imagePath", R.drawable.baseline_add_photo_alternate_24);
        }

        db.collection("cart").document(UUID.randomUUID().toString())
                .set(cartItem, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding to cart", e));

    }


}