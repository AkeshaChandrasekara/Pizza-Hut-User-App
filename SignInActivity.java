package com.myapp.pizzahut;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class SignInActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button signInButton;
    private TextView textViewSignUp,textViewforgotPw;
    private FirebaseFirestore db;
    public static String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageView = findViewById(R.id.imageView4);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView.startAnimation(animation);

        editTextEmail = findViewById(R.id.editText6);
        editTextPassword = findViewById(R.id.editText7);
        signInButton = findViewById(R.id.button3);
        textViewforgotPw = findViewById(R.id.textView10);
        textViewSignUp = findViewById(R.id.textView9);

        signInButton.setOnClickListener(view -> loginUser());
        textViewSignUp.setOnClickListener(view -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        textViewforgotPw.setOnClickListener(view -> startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class)));
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            showAlert("Email is required.", () -> editTextEmail.requestFocus());
            return;
        }
        if (password.isEmpty()) {
            showAlert("Password is required.", () -> editTextPassword.requestFocus());
            return;
        }

        db.collection("users").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storedPassword = documentSnapshot.getString("password");
                        if (storedPassword != null && storedPassword.equals(password)) {
                            loggedInUserEmail = email;
                          // showAlert("Sign in successful!", this::navigateToHome);
                            showToastAndNavigate("Sign in successful!");
                        } else {
                            showAlert("Invalid email or password.", null);
                        }
                    } else {
                        showAlert("User not found.", null);
                    }
                })
                .addOnFailureListener(e -> Log.e("SignInActivity", "Error fetching user", e));
    }

    private void navigateToHome() {
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
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

    private void showToastAndNavigate(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }
    
    private void showToastAndFocus(String message, EditText field) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        field.requestFocus();
    }
}
