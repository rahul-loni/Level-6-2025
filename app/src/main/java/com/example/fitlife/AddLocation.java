package com.example.fitlife;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitlife.Model.WorkoutLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddLocation extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST = 200;

    private TextInputEditText etLocationName, etAddress;
    private Button btnGetCurrentLocation, btnSaveLocation;
    private MaterialCardView cardLocationStatus, cardCoordinates;
    private TextView tvLocationStatus, tvCoordinates;
    private ProgressBar progressBar;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize views
        etLocationName = findViewById(R.id.et_location_name);
        etAddress = findViewById(R.id.et_address);
        btnGetCurrentLocation = findViewById(R.id.btn_get_current_location);
        btnSaveLocation = findViewById(R.id.btn_save_location);
        cardLocationStatus = findViewById(R.id.card_location_status);
        cardCoordinates = findViewById(R.id.card_coordinates);
        tvLocationStatus = findViewById(R.id.tv_location_status);
        tvCoordinates = findViewById(R.id.tv_coordinates);
        progressBar = findViewById(R.id.progress_bar);

        // Setup listeners
        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());
        btnSaveLocation.setOnClickListener(v -> saveLocation());
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnGetCurrentLocation.setEnabled(false);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    progressBar.setVisibility(View.GONE);
                    btnGetCurrentLocation.setEnabled(true);

                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        // Update UI
                        tvCoordinates.setText(String.format(Locale.getDefault(),
                                "Lat: %.4f, Lng: %.4f", latitude, longitude));
                        cardCoordinates.setVisibility(View.VISIBLE);
                        cardLocationStatus.setVisibility(View.VISIBLE);

                        // Get address from coordinates
                        getAddressFromLocation(latitude, longitude);
                    } else {
                        Toast.makeText(this, "Unable to get location. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnGetCurrentLocation.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void getAddressFromLocation(double lat, double lng) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressText = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressText.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        addressText.append(", ");
                    }
                }

                etAddress.setText(addressText.toString());
            }
        } catch (IOException e) {
            Toast.makeText(this, "Unable to get address", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocation() {
        String name = etLocationName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etLocationName.setError("Location name is required");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Address is required");
            return;
        }

        // Create location object
        WorkoutLocation location = new WorkoutLocation();
        location.setName(name);
        location.setAddress(address);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        // Return result
        Intent resultIntent = new Intent();
        resultIntent.putExtra("location", location);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}