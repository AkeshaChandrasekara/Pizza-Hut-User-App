package com.myapp.pizzahut;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.pizzahut.model.CartItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MAP = 1;
    private static final int REQUEST_CODE_PAYMENT = 11001;
    private RecyclerView rvSelectedItems;
    private TextView tvTotalPrice, tvFinalAmount;
    private EditText etAddress;
    private Button btnPlaceOrder, btnGetCurrentLocation, btnSelectLocation;
    private double totalPrice = 0.00;
    private final double DELIVERY_CHARGE = 400.00;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore db;
    private String userEmail = SignInActivity.loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(this);


        rvSelectedItems = findViewById(R.id.rvSelectedItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        etAddress = findViewById(R.id.etAddress);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);

        setupRecyclerView();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchSavedAddress();
            }
        });

        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, MapsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MAP);
                fetchCurrentLocation();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double finalAmount = totalPrice + DELIVERY_CHARGE;
                openPaymentActivity(finalAmount);
            }
        });

    }


    private void openPaymentActivity(double amount) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("address", etAddress.getText().toString());
        intent.putExtra("selected_items", getIntent().getSerializableExtra("selected_items"));
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }


    private void fetchSavedAddress() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedAddress = sharedPreferences.getString("user_address", "");

        if (!savedAddress.isEmpty()) {
            etAddress.setText(savedAddress);
        } else {
            Toast.makeText(this, "No saved address found", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupRecyclerView() {
        ArrayList<CartItem> selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selected_items");
        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(this, "No items selected for checkout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvSelectedItems.setLayoutManager(new LinearLayoutManager(this));
        SelectedItemsAdapter selectedItemsAdapter = new SelectedItemsAdapter(selectedItems);
        rvSelectedItems.setAdapter(selectedItemsAdapter);

        calculateTotal(selectedItems);
    }

    private void calculateTotal(ArrayList<CartItem> selectedItems) {
        totalPrice = 0;
        for (CartItem item : selectedItems) {
            try {
                double price = Double.parseDouble(item.getProductPrice());
                totalPrice += price * item.getQuantity();
            } catch (NumberFormatException e) {
                Log.e("CheckoutActivity", "Invalid price format for: " + item.getProductName(), e);
            }
        }

        double finalAmount = totalPrice + DELIVERY_CHARGE;
        tvTotalPrice.setText("Total: Rs. " + totalPrice);
        tvFinalAmount.setText("Final Amount (incl. delivery): Rs. " + finalAmount);
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                etAddress.setText(getAddressFromLatLng(location.getLatitude(), location.getLongitude()));
            } else {
                Toast.makeText(this, "Unable to get location!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);

                String addressNumber = address.getSubThoroughfare();
                String street = address.getThoroughfare();
                String city = address.getLocality();
                String district = address.getSubAdminArea();
                String country = address.getCountryName();

                if (city == null || city.isEmpty()) {
                    city = district;
                }

                if (city != null && city.equals("Kegalle")) {
                    city = "Eheliyagoda";
                }

                StringBuilder fullAddress = new StringBuilder();

                if (addressNumber != null && !addressNumber.isEmpty()) {
                    fullAddress.append(addressNumber).append(", ");
                }
                if (street != null && !street.isEmpty()) {
                    fullAddress.append(street).append(", ");
                }
                if (city != null && !city.isEmpty()) {
                    fullAddress.append(city).append(", ");
                }
                if (country != null && !country.isEmpty()) {
                    fullAddress.append(country);
                }

                return fullAddress.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String address = data.getStringExtra("address");

            etAddress.setText(address);
        }

        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == RESULT_OK) {
            //  Toast.makeText(this, "Payment Successful! Order Placed.", Toast.LENGTH_SHORT).show();
            placeOrder("Paid");
            finish();
        }
    }

    private void placeOrder(String paymentStatus) {
        String address = etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Please provide a delivery address", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<CartItem> selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selected_items");
        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(this, "No items selected for order", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> order = new HashMap<>();
        order.put("orderId", UUID.randomUUID().toString());
        order.put("userEmail", userEmail);
        order.put("items", selectedItems);
        order.put("totalPrice", totalPrice);
        order.put("finalAmount", totalPrice + DELIVERY_CHARGE);
        order.put("address", address);
        order.put("status", paymentStatus);
        db.collection("orders").document().set(order)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show());
    }
}