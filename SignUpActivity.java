package com.myapp.pizzahut;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageView = findViewById(R.id.imageView3);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView.startAnimation(animation);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);
        EditText editText4 = findViewById(R.id.editText4);
        EditText editText5 = findViewById(R.id.editText5);
        Button button = findViewById(R.id.button2);
        TextView textViewSignIn = findViewById(R.id.textView10);

        button.setOnClickListener(view -> {
            String first_name = editText1.getText().toString().trim();
            String last_name = editText2.getText().toString().trim();
            String mobile = editText3.getText().toString().trim();
            String email = editText4.getText().toString().trim();
            String password = editText5.getText().toString().trim();

            if (first_name.isEmpty()) {
                showAlert("First Name is required.", () -> editText1.requestFocus());
                return;
            }
            if (last_name.isEmpty()) {
                showAlert("Last Name is required.", () -> editText2.requestFocus());
                return;
            }
            if (email.isEmpty()) {
                showAlert("Email is required.", () -> editText4.requestFocus());
                return;
            }
            if (mobile.isEmpty()) {
                showAlert("Mobile number is required.", () -> editText3.requestFocus());
                return;
            }
            if (password.isEmpty()) {
                showAlert("Password is required.", () -> editText5.requestFocus());
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert("Invalid email format.", () -> editText4.requestFocus());
                return;
            }
            if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                showAlert("Password must be at least 8 characters long and contain at least one letter, one number, and one special character.",
                        () -> editText5.requestFocus());
                return;
            }
            if (!mobile.matches("^\\d{10}$")) {
                showAlert("Mobile number must be exactly 10 digits.", () -> editText3.requestFocus());
                return;
            }

            checkIfEmailExists(firebaseFirestore, first_name, last_name, email, mobile, password);
        });

        textViewSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    private void checkIfEmailExists(FirebaseFirestore firebaseFirestore, String first_name, String last_name, String email, String mobile, String password) {
        firebaseFirestore.collection("users").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        showAlert("User already registered with this email.", null);
                    } else {
                        registerUser(firebaseFirestore, first_name, last_name, email, mobile, password);
                    }
                })
                .addOnFailureListener(e -> showAlert("Error checking email. Please try again.", null));
    }

    private void registerUser(FirebaseFirestore firebaseFirestore, String first_name, String last_name, String email, String mobile, String password) {
        String userId = generateRandomUserId();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("user_id", userId);
        userMap.put("first_name", first_name);
        userMap.put("last_name", last_name);
        userMap.put("email", email);
        userMap.put("mobile", mobile);
        userMap.put("password", password);

        firebaseFirestore.collection("users")
                .document(email)
                .set(userMap)
                .addOnSuccessListener(documentReference -> showAlert("Sign up successful! Your User ID: " + userId, this::navigateToSignIn))
                .addOnFailureListener(e -> showAlert("Sign up failed, please try again.", null));
    }

    private String generateRandomUserId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder userId = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            userId.append(characters.charAt(random.nextInt(characters.length())));
        }
        return userId.toString();
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void showAlert(String message, Runnable onOkClick) {
        new AlertDialog.Builder(this)
                .setTitle("Message")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (onOkClick != null) {
                        onOkClick.run();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
