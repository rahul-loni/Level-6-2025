package com.example.fitlife.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitlife.Auth.LoginPage;
import com.example.fitlife.Database.DatabaseHelper;
import com.example.fitlife.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvProfileEmail, tvWorkoutCount, tvCompletedCount, tvMemberSince;
    private Button btnEditProfile, btnNotifications, btnLogout;
    private FirebaseAuth mAuth;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        dbHelper = DatabaseHelper.getInstance(getContext());

        // Initialize views
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        tvWorkoutCount = view.findViewById(R.id.tv_workout_count);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvMemberSince = view.findViewById(R.id.tv_member_since);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnNotifications = view.findViewById(R.id.btn_notifications);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Load user data
        loadUserData();
        loadStatistics();

        // Setup button listeners
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit Profile feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnNotifications.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Notification settings coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvProfileEmail.setText(user.getEmail());

            // Get user creation date
            long creationTimestamp = user.getMetadata().getCreationTimestamp();
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            String memberSince = sdf.format(new Date(creationTimestamp));
            tvMemberSince.setText("Member since: " + memberSince);
        }
    }

    private void loadStatistics() {
        int totalWorkouts = dbHelper.getWorkoutCount();
        int completedWorkouts = dbHelper.getCompletedWorkoutCount();

        tvWorkoutCount.setText("Total Workouts: " + totalWorkouts);
        tvCompletedCount.setText("Completed: " + completedWorkouts);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), LoginPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}