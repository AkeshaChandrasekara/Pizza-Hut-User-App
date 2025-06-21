package com.myapp.pizzahut;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;
import com.myapp.pizzahut.Notification;
//import com.google.firebase.firestore.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.notificationRecyclerView);

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notificationAdapter);

        db = FirebaseFirestore.getInstance();
        loadNotifications();
    }

    private void loadNotifications() {
        CollectionReference notificationsRef = db.collection("notifications");

        notificationsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null && !snapshot.isEmpty()) {
                    notificationList.clear();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        String title = document.getString("title");
                        String message = document.getString("message");
                        Timestamp timestamp = document.getTimestamp("timestamp");


                        if (title != null && message != null && timestamp != null) {
                            String formattedTimestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                    .format(timestamp.toDate());

                            Notification notification = new Notification(title, message, formattedTimestamp);
                            notificationList.add(notification);
                        } else {
                            Log.e("Firestore", "One or more fields are null in document: " + document.getId());
                        }
                    }
                    notificationAdapter.notifyDataSetChanged();
                } else {
                    Log.e("Firestore", "No notifications found");
                    Toast.makeText(NotificationActivity.this, "No notifications found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Firestore", "Error getting notifications", task.getException());
                Toast.makeText(NotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
