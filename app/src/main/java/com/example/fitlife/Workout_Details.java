package com.example.fitlife;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitlife.Database.DatabaseHelper;
import com.example.fitlife.Model.Workout;
import com.example.fitlife.Model.WorkoutLocation;
import com.google.android.material.card.MaterialCardView;

public class Workout_Details extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST = 300;

    private TextView tvDetailName, tvDetailDescription, tvDetailEquipment,
            tvDetailDuration, tvDetailLocation;
    private Button btnMarkComplete, btnDelegate, btnViewMap;
    private MaterialCardView cardCompletedBadge;
    private LinearLayout layoutLocation;

    private DatabaseHelper dbHelper;
    private Workout workout;
    private WorkoutLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize views
        tvDetailName = findViewById(R.id.tv_detail_name);
        tvDetailDescription = findViewById(R.id.tv_detail_description);
        tvDetailEquipment = findViewById(R.id.tv_detail_equipment);
        tvDetailDuration = findViewById(R.id.tv_detail_duration);
        tvDetailLocation = findViewById(R.id.tv_detail_location);
        btnMarkComplete = findViewById(R.id.btn_mark_complete);
        btnDelegate = findViewById(R.id.btn_delegate);
        btnViewMap = findViewById(R.id.btn_view_map);
        cardCompletedBadge = findViewById(R.id.card_completed_badge);
        layoutLocation = findViewById(R.id.layout_location);

        // Get workout ID from intent
        long workoutId = getIntent().getLongExtra("workout_id", -1);
        if (workoutId != -1) {
            loadWorkoutDetails(workoutId);
        } else {
            Toast.makeText(this, "Error loading workout", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup listeners
        btnMarkComplete.setOnClickListener(v -> toggleCompletion());
        btnDelegate.setOnClickListener(v -> delegateWorkout());
        btnViewMap.setOnClickListener(v -> viewOnMap());
    }

    private void loadWorkoutDetails(long workoutId) {
        workout = dbHelper.getWorkout(workoutId);
        if (workout == null) {
            Toast.makeText(this, "Workout not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display workout details
        tvDetailName.setText(workout.getName());
        tvDetailDescription.setText(workout.getDescription());
        tvDetailEquipment.setText("Equipment: " + workout.getEquipment());
        tvDetailDuration.setText("Duration: " + workout.getDuration() + " minutes");

        // Update completion status
        updateCompletionUI();

        // Load location if exists
        location = dbHelper.getLocationForWorkout(workoutId);
        if (location != null) {
            tvDetailLocation.setText("Location: " + location.getName());
            btnViewMap.setVisibility(View.VISIBLE);
        } else {
            tvDetailLocation.setText("Location: Not set");
            btnViewMap.setVisibility(View.GONE);
        }
    }

    private void toggleCompletion() {
        boolean newStatus = !workout.isCompleted();
        workout.setCompleted(newStatus);
        dbHelper.markWorkoutCompleted(workout.getId(), newStatus);

        updateCompletionUI();

        String message = newStatus ? "Workout marked as completed!" : "Workout marked as incomplete";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateCompletionUI() {
        if (workout.isCompleted()) {
            cardCompletedBadge.setVisibility(View.VISIBLE);
            btnMarkComplete.setText("Mark as Incomplete");
            btnMarkComplete.setIcon(ContextCompat.getDrawable(this, R.drawable.baseline_cancel_24));
        } else {
            cardCompletedBadge.setVisibility(View.GONE);
            btnMarkComplete.setText("Mark as Complete");
            btnMarkComplete.setIcon(ContextCompat.getDrawable(this, R.drawable.baseline_check_24));
        }
    }

    private void delegateWorkout() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST);
            return;
        }

        // Show dialog to enter phone number
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delegate Workout");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Enter phone number");
        input.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String phoneNumber = input.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                sendSMS(phoneNumber);
            } else {
                Toast.makeText(this, "Phone number required", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void sendSMS(String phoneNumber) {
        try {
            String message = String.format("Hi! I'd like to delegate this workout to you:\n\n" +
                            "Workout: %s\nDuration: %d minutes\nEquipment: %s\n\nDescription: %s",
                    workout.getName(), workout.getDuration(),
                    workout.getEquipment(), workout.getDescription());

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void viewOnMap() {
        if (location != null) {
            // Open Google Maps with location
            Uri gmmIntentUri = Uri.parse(String.format(
                    "geo:%f,%f?q=%f,%f(%s)",
                    location.getLatitude(), location.getLongitude(),
                    location.getLatitude(), location.getLongitude(),
                    location.getName()));

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
            }
        }

    }
}