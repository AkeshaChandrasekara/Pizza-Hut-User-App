package com.myapp.pizzahut;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ResturantsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resturants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.FrameLayout1);
        if (supportMapFragment == null) {
            supportMapFragment = new SupportMapFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.FrameLayout1, supportMapFragment);
            fragmentTransaction.commit();
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                LatLng[] locations = {
                        new LatLng(6.9271, 79.8612),
                        new LatLng(7.2906, 80.6337),
                        new LatLng(6.0535, 80.2210),
                        new LatLng(7.8731, 80.7718),
                        new LatLng(6.9279, 79.8763),
                        new LatLng(6.9344, 79.8585),
                        new LatLng(7.2955, 80.6356),
                        new LatLng(6.9250, 79.8489),
                        new LatLng(6.9393, 79.8584),
                        new LatLng(6.8464, 79.9181),
                        new LatLng(6.682229608805646, 80.41139780329776),
                        new LatLng(6.861452078875132, 80.2541350018462),
                        new LatLng(6.956280367098411, 80.20070771657743),
                        new LatLng(6.83804285318985, 80.00137641646245),
                        new LatLng(7.490303288224124, 80.03903772149977),
                        new LatLng(5.9898824736325675, 80.6522337067657),
                        new LatLng(6.133734203794787, 81.2699361042727),
                        new LatLng(6.700811409146481, 80.04971048757884),
                        new LatLng(8.345481961577997, 80.57841197860989),
                        new LatLng(7.912348377714597, 81.04064324375963),
                        new LatLng(7.721874358399223, 81.71080924613008),
                        new LatLng(9.671149377730892, 79.96398310880383),
                        new LatLng(7.339749193342155, 80.60510567892196),
                        new LatLng(6.94099785510743, 80.7875633630713),
                        new LatLng(6.817177797938363, 80.97233063815922),
                        new LatLng(6.901013147346487, 81.53394686058412),
                        new LatLng(7.319304662058777, 81.73601058668214),
                        new LatLng(8.764552635725007, 80.6234495827297),
                        new LatLng(8.58124617966623, 81.27006623459948),
                        new LatLng(8.063756940525488, 79.97207839665502),
                        new LatLng(6.925249640752466, 79.97263571277054),
                        new LatLng(6.841749831536978, 79.99876575140175),
                        new LatLng(6.727420624374476, 80.0747061761737),
                        new LatLng(9.367264240870325, 80.43515875566986),
                        new LatLng(8.760193448119166, 80.57031649265146),
                        new LatLng(7.1643327406004955, 80.59844148135114),
                      //  new LatLng(7.552455839165385, 79.784979198411),
                        new LatLng(7.060253648334611, 79.78041412953017),
                        new LatLng(6.935754202815708, 80.18704959887035),


                };

                googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(locations[0])
                                .zoom(10)
                                .build())
                );

                for (int i = 0; i < locations.length; i++) {
                    googleMap.addMarker(
                            new MarkerOptions()
                                    .position(locations[i])
                                    .title("Pizza Hut Branch " + (i + 1))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    ).showInfoWindow();
                }

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Log.i("app-map", "Clicked on: " + marker.getTitle());

                        LatLng position = marker.getPosition();
                        String uri = "geo:" + position.latitude + "," + position.longitude + "?q=" + position.latitude + "," + position.longitude + "(" + marker.getTitle() + ")";

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(ResturantsActivity.this, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });
            }
        });
    }
}
