package com.myapp.pizzahut;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.pizzahut.model.CartItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentActivity extends AppCompatActivity {

    private static final int PAYHERE_REQUEST = 11001;
    private static final String TAG = "PaymentActivity";
    private FirebaseFirestore db;
    private ConnectivityReceiver connectivityReceiver;
    private String userEmail = SignInActivity.loggedInUserEmail;
    private double totalPrice = 0.00;
    private final double DELIVERY_CHARGE = 400.00;
    private ArrayList<CartItem> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);

        db = FirebaseFirestore.getInstance();
        totalPrice = getIntent().getDoubleExtra("amount", 0.00) - DELIVERY_CHARGE;
        selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selected_items");

        if (totalPrice > 0) {
            initiatePayment(totalPrice + DELIVERY_CHARGE);
        } else {
            Toast.makeText(this, "Invalid payment amount", Toast.LENGTH_SHORT).show();
            finish();
        }

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initiatePayment(double amount) {

        InitRequest req = new InitRequest();
        req.setMerchantId("1228153");
        req.setCurrency("LKR");
        req.setAmount(amount);
        req.setOrderId(UUID.randomUUID().toString());
        req.setItemsDescription("Pizza Order");
        req.setCustom1("Additional Data");

        req.getCustomer().setFirstName("John");
        req.getCustomer().setLastName("Doe");
        req.getCustomer().setEmail("akeshanawanjali23@gmail.com");
        req.getCustomer().setPhone("0719258363");
        req.getCustomer().getAddress().setAddress("123, Colombo");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        startActivityForResult(intent, PAYHERE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null) {
                    if (response.isSuccess()) {

                        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                        placeOrder("Paid");
                        removePaidItemsFromCart();
                        sendMultipleSMS("0754027915", new String[]{
                                "Thank you for your order! Your pizza order is confirmed! Your delicious food is being prepared.We’ll notify you when your order is ready for delivery!"

                        });

                        Intent intent = new Intent(this, ConfirmActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "User canceled the request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void placeOrder(String paymentStatus) {
        String address = getIntent().getStringExtra("address");
        ArrayList<CartItem> selectedItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selected_items");


        if (selectedItems == null) {
            selectedItems = new ArrayList<>();
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

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            //Toast.makeText(this, "SMS Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Toast.makeText(this, "SMS failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            openMessagingApp(phoneNumber, message);
        }
    }

    private void sendMultipleSMS(String phoneNumber, String[] messages) {
        for (String message : messages) {
            sendSMS(phoneNumber, message);
        }
    }

    private void openMessagingApp(String phoneNumber, String smsBody) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", smsBody);
        startActivity(intent);
    }

    private void removePaidItemsFromCart() {
        if (selectedItems != null) {
            for (CartItem item : selectedItems) {
                db.collection("cart")
                        .whereEqualTo("userEmail", userEmail)
                        .whereEqualTo("productName", item.getProductName())
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            for (var document : querySnapshot.getDocuments()) {
                                db.collection("cart").document(document.getId()).delete();
                            }
                        });
            }
        }
    }
}