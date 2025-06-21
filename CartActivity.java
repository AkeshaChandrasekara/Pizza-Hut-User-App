package com.myapp.pizzahut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myapp.pizzahut.model.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        FirebaseApp.initializeApp(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        cartRecyclerView = findViewById(R.id.rv_cart_items);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalTextView = findViewById(R.id.tv_total_price);

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, db);
        cartRecyclerView.setAdapter(cartAdapter);

        loadCartItems();

        Button proceed_Button = findViewById(R.id.btn_proceed_checkout);
        proceed_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CartItem> selectedItems = new ArrayList<>();

                for (CartItem item : cartItemList) {
                    if (item.isSelected()) {
                        selectedItems.add(item);
                    }
                }

                if (selectedItems.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Please select at least one item to proceed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("selected_items", selectedItems);
                startActivity(intent);
            }
        });


    }

    private void loadCartItems() {
        db.collection("cart")
                .whereEqualTo("userEmail", SignInActivity.loggedInUserEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItemList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        CartItem item = document.toObject(CartItem.class);
                        cartItemList.add(item);
                    }

                    if (cartItemList.isEmpty()) {
                        Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                    }

                    cartAdapter.updateCartList(cartItemList);
                    calculateTotal();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartActivity", "Error loading cart", e);
                    Toast.makeText(this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                });
    }

    public void calculateTotal() {
        double total = 0;
        for (CartItem item : cartItemList) {
            try {
                double price = Double.parseDouble(String.valueOf(item.getProductPrice()));
                total += price * item.getQuantity();
            } catch (NumberFormatException e) {
                Log.e("CartActivity", "Invalid price format for item: " + item.getProductName(), e);
            }
        }
        totalTextView.setText("Total: Rs. " + total);
    }


}
