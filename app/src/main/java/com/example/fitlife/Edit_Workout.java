package com.example.fitlife;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fitlife.Database.DatabaseHelper;
import com.example.fitlife.Model.Workout;
import com.google.android.material.textfield.TextInputEditText;

public class Edit_Workout extends AppCompatActivity {

    private TextInputEditText etEditWorkoutName, etEditWorkoutDescription,
            etEditWorkoutEquipment, etEditWorkoutDuration;
    private Button btnUpdateWorkout, btnCancel;
    private ProgressBar progressBar;

    private DatabaseHelper dbHelper;
    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize views
        etEditWorkoutName = findViewById(R.id.et_edit_workout_name);
        etEditWorkoutDescription = findViewById(R.id.et_edit_workout_description);
        etEditWorkoutEquipment = findViewById(R.id.et_edit_workout_equipment);
        etEditWorkoutDuration = findViewById(R.id.et_edit_workout_duration);
        btnUpdateWorkout = findViewById(R.id.btn_update_workout);
        btnCancel = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_bar);

        // Get workout ID from intent
        long workoutId = getIntent().getLongExtra("workout_id", -1);
        if (workoutId != -1) {
            loadWorkoutData(workoutId);
        } else {
            Toast.makeText(this, "Error loading workout", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup listeners
        btnUpdateWorkout.setOnClickListener(v -> updateWorkout());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadWorkoutData(long workoutId) {
        workout = dbHelper.getWorkout(workoutId);
        if (workout == null) {
            Toast.makeText(this, "Workout not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields
        etEditWorkoutName.setText(workout.getName());
        etEditWorkoutDescription.setText(workout.getDescription());
        etEditWorkoutEquipment.setText(workout.getEquipment());
        etEditWorkoutDuration.setText(String.valueOf(workout.getDuration()));
    }

    private void updateWorkout() {
        String name = etEditWorkoutName.getText().toString().trim();
        String description = etEditWorkoutDescription.getText().toString().trim();
        String equipment = etEditWorkoutEquipment.getText().toString().trim();
        String durationStr = etEditWorkoutDuration.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etEditWorkoutName.setError("Workout name is required");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etEditWorkoutDescription.setError("Description is required");
            return;
        }

        if (TextUtils.isEmpty(equipment)) {
            etEditWorkoutEquipment.setError("Equipment is required");
            return;
        }

        if (TextUtils.isEmpty(durationStr)) {
            etEditWorkoutDuration.setError("Duration is required");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                etEditWorkoutDuration.setError("Duration must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            etEditWorkoutDuration.setError("Invalid duration");
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnUpdateWorkout.setEnabled(false);

        // Update workout object
        workout.setName(name);
        workout.setDescription(description);
        workout.setEquipment(equipment);
        workout.setDuration(duration);

        // Update in database
        int result = dbHelper.updateWorkout(workout);

        progressBar.setVisibility(View.GONE);
        btnUpdateWorkout.setEnabled(true);

        if (result > 0) {
            Toast.makeText(this, "Workout updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update workout", Toast.LENGTH_SHORT).show();
        }
    }
}