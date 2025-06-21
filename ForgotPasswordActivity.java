package com.myapp.pizzahut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageView = findViewById(R.id.imageView5);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView.startAnimation(animation);
        firebaseAuth = FirebaseAuth.getInstance();


        EditText emailEditText = findViewById(R.id.EditText10);
        Button resetPasswordButton = findViewById(R.id.button5);

        resetPasswordButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();

            if (email.isEmpty()) {
                showAlert("Error", "Please enter your email", false);
                return;
            }

            AlertDialog loadingDialog = showLoadingDialog();

            db.collection("users").document(email)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String password = documentSnapshot.getString("password");

                            if (password != null && !password.isEmpty()) {
                                loadingDialog.dismiss();
                                sendEmail(email, password);
                            } else {
                                loadingDialog.dismiss();
                                showAlert("Error", "Password not found for this email.", false);
                            }
                        } else {
                            loadingDialog.dismiss();
                            showAlert("Error", "Email not registered.", false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        showAlert("Error", "Error retrieving password: " + e.getMessage(), false);
                    });
        });
    }

    private AlertDialog showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_loading);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void showAlert(String title, String message, boolean isSuccess) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void sendEmail(String email, String password) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Your Password Recovery");
        intent.putExtra(Intent.EXTRA_TEXT, "Your password is: " + password);

        try {
            startActivity(Intent.createChooser(intent, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            showAlert("Error", "No email clients installed.", false);
        }
    }
}
