package com.example.fitlife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fitlife.Database.DatabaseHelper;
import com.example.fitlife.Model.Workout;
import com.example.fitlife.Model.WorkoutLocation;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class Create_Workout extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_ADD_LOCATION = 1002;
    private static final int PERMISSION_READ_STORAGE = 100;

    private TextInputEditText etWorkoutName, etWorkoutDescription, etWorkoutEquipment, etWorkoutDuration;
    private Button btnAddImage, btnAddLocation, btnSaveWorkout;
    private MaterialCardView cardImagePreview, cardLocationInfo;
    private ImageView ivWorkoutImage, ivRemoveImage;
    private TextView tvLocationName, tvLocationAddress;

    private DatabaseHelper dbHelper;
    private String imagePath = null;
    private WorkoutLocation workoutLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        dbHelper = DatabaseHelper.getInstance(this);

        // Initialize views
        etWorkoutName = findViewById(R.id.et_workout_name);
        etWorkoutDescription = findViewById(R.id.et_workout_description);
        etWorkoutEquipment = findViewById(R.id.et_workout_equipment);
        etWorkoutDuration = findViewById(R.id.et_workout_duration);
        btnAddImage = findViewById(R.id.btn_add_image);
        btnAddLocation = findViewById(R.id.btn_add_location);
        btnSaveWorkout = findViewById(R.id.btn_save_workout);
        cardImagePreview = findViewById(R.id.card_image_preview);
        cardLocationInfo = findViewById(R.id.card_location_info);
        ivWorkoutImage = findViewById(R.id.iv_workout_image);
        ivRemoveImage = findViewById(R.id.iv_remove_image);
        tvLocationName = findViewById(R.id.tv_location_name);
        tvLocationAddress = findViewById(R.id.tv_location_address);

        // Setup listeners
        btnAddImage.setOnClickListener(v -> pickImage());
        btnAddLocation.setOnClickListener(v -> addLocation());
        btnSaveWorkout.setOnClickListener(v -> saveWorkout());
        ivRemoveImage.setOnClickListener(v -> removeImage());
    }

    private void pickImage() {
        // Check for permission based on Android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_READ_STORAGE);
            } else {
                openImagePicker();
            }
        } else {
            // Android 12 and below use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void addLocation() {
        Intent intent = new Intent(this, AddLocation.class);
        startActivityForResult(intent, REQUEST_ADD_LOCATION);
    }

    private void removeImage() {
        imagePath = null;
        cardImagePreview.setVisibility(View.GONE);
        ivWorkoutImage.setImageDrawable(null);
    }

    private void saveWorkout() {
        String name = etWorkoutName.getText().toString().trim();
        String description = etWorkoutDescription.getText().toString().trim();
        String equipment = etWorkoutEquipment.getText().toString().trim();
        String durationStr = etWorkoutDuration.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etWorkoutName.setError("Workout name is required");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etWorkoutDescription.setError("Description is required");
            return;
        }

        if (TextUtils.isEmpty(equipment)) {
            etWorkoutEquipment.setError("Equipment is required");
            return;
        }

        if (TextUtils.isEmpty(durationStr)) {
            etWorkoutDuration.setError("Duration is required");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                etWorkoutDuration.setError("Duration must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            etWorkoutDuration.setError("Invalid duration");
            return;
        }

        // Create workout object
        Workout workout = new Workout();
        workout.setName(name);
        workout.setDescription(description);
        workout.setEquipment(equipment);
        workout.setDuration(duration);
        workout.setImagePath(imagePath);
        workout.setCreatedAt(System.currentTimeMillis());

        // Insert workout
        long workoutId = dbHelper.insertWorkout(workout);

        if (workoutId != -1) {
            // If location was added, save it
            if (workoutLocation != null) {
                workoutLocation.setWorkoutId(workoutId);
                dbHelper.insertLocation(workoutLocation);
            }

            Toast.makeText(this, "Workout created successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to create workout", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    imagePath = imageUri.toString();
                    ivWorkoutImage.setImageURI(imageUri);
                    cardImagePreview.setVisibility(View.VISIBLE);
                }
            } else if (requestCode == REQUEST_ADD_LOCATION && data != null) {
                workoutLocation = (WorkoutLocation) data.getSerializableExtra("location");
                if (workoutLocation != null) {
                    tvLocationName.setText(workoutLocation.getName());
                    tvLocationAddress.setText(workoutLocation.getAddress());
                    cardLocationInfo.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Storage permission required to select image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}