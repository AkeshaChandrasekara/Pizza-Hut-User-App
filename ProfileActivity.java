package com.myapp.pizzahut;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private EditText editTextAddress;
    private Button btnSave;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextAddress = findViewById(R.id.editTextTextMultiLine);
        btnSave = findViewById(R.id.button6);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String savedAddress = sharedPreferences.getString("user_address", "");
        editTextAddress.setText(savedAddress);

        btnSave.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        Log.d("ProfileActivity", "Save button clicked");
        try {
            String address = editTextAddress.getText().toString().trim();
            Log.d("ProfileActivity", "Address entered: " + address);

            if (!address.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_address", address);
                editor.apply();
                editTextAddress.setText("");

                Toast.makeText(this, "Address saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter an address!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ProfileActivity", "Error saving address", e);
            Toast.makeText(this, "An error occurred while saving the address.", Toast.LENGTH_SHORT).show();
        }
    }
}