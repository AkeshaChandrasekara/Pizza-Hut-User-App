package com.myapp.pizzahut;

import static com.myapp.pizzahut.R.id.nav_home;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.myapp.pizzahut.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewPizzas, recyclerViewDesserts, recyclerViewDrinks;
    private PizzaAdapter pizzaAdapter;
    private DessertsAdapter dessertsAdapter;
    private DrinksAdapter drinksAdapter;

    private List<Product> pizzaList, dessertList, drinkList;
    private SearchView searchView;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private long lastUpdateTime;
    private static final int SHAKE_THRESHOLD = 800;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        recyclerViewPizzas = findViewById(R.id.RecyclerView1);
        recyclerViewDesserts = findViewById(R.id.recyclerViewDesserts);
        recyclerViewDrinks = findViewById(R.id.recyclerViewDrinks);
        searchView = findViewById(R.id.searchView);

        pizzaList = new ArrayList<>();
        dessertList = new ArrayList<>();
        drinkList = new ArrayList<>();

        pizzaAdapter = new PizzaAdapter(this, pizzaList);
        dessertsAdapter = new DessertsAdapter(this, dessertList);
        drinksAdapter = new DrinksAdapter(this, drinkList);

        recyclerViewPizzas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPizzas.setAdapter(pizzaAdapter);

        recyclerViewDesserts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDesserts.setAdapter(dessertsAdapter);

        recyclerViewDrinks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDrinks.setAdapter(drinksAdapter);

        loadPizzas();
        loadDesserts();
        loadDrinks();


        SearchHelper.setupSearchView(searchView, pizzaList, dessertList, drinkList, pizzaAdapter, dessertsAdapter, drinksAdapter);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemBackgroundResource(android.R.color.transparent);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            ActivityOptions options;

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_res) {
                intent = new Intent(HomeActivity.this, ResturantsActivity.class);
                options = ActivityOptions.makeCustomAnimation(HomeActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent, options.toBundle());
                return true;
            } else if (itemId == R.id.nav_messages) {
                intent = new Intent(HomeActivity.this, NotificationActivity.class);
                options = ActivityOptions.makeCustomAnimation(HomeActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent, options.toBundle());
                return true;
            } else if (itemId == R.id.nav_cart) {
                intent = new Intent(HomeActivity.this, CartActivity.class);
                options = ActivityOptions.makeCustomAnimation(HomeActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent, options.toBundle());
                return true;
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(HomeActivity.this, ProfileActivity.class);
                options = ActivityOptions.makeCustomAnimation(HomeActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent, options.toBundle());
                return true;
            }

            return false;
        });

        ImageButton imageButtonCall = findViewById(R.id.imageButtonCall);
        imageButtonCall.setOnClickListener(v -> {
            String phoneNumber = "0112222225";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));

            if (callIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(callIntent);
            }
        });
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastUpdateTime) > 100) {
                long diffTime = currentTime - lastUpdateTime;
                lastUpdateTime = currentTime;

                float deltaX = event.values[0] - lastX;
                float deltaY = event.values[1] - lastY;
                float deltaZ = event.values[2] - lastZ;

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];

                float speed = Math.abs(deltaX + deltaY + deltaZ) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    Toast.makeText(this, "Shake detected! Refreshing data...", Toast.LENGTH_SHORT).show();
                    refreshContent();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometer != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void loadPizzas() {
        db.collection("pizzas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pizzaList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Product product = document.toObject(Product.class);
                                product.setImagePath(document.getString("imagePath"));
                                pizzaList.add(product);
                            }
                        }
                        pizzaAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, "Failed to load pizzas.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadDesserts() {
        db.collection("desserts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dessertList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Product product = document.toObject(Product.class);
                                product.setImagePath(document.getString("imagePath"));
                                dessertList.add(product);
                            }
                        }
                        dessertsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, "Failed to load desserts.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadDrinks() {
        db.collection("drinks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        drinkList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Product product = document.toObject(Product.class);
                                product.setImagePath(document.getString("imagePath"));
                                drinkList.add(product);
                            }
                        }
                        drinksAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, "Failed to load drinks.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refreshContent() {
        loadPizzas();
        loadDesserts();
        loadDrinks();
    }

}
