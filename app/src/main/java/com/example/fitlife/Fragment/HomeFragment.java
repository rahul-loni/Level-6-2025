package com.example.fitlife.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fitlife.Adapter.WorkoutAdapter;
import com.example.fitlife.Create_Workout;
import com.example.fitlife.Database.DatabaseHelper;
import com.example.fitlife.Model.Workout;
import com.example.fitlife.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private TextView tvWelcome, tvNoWorkouts;
    private FloatingActionButton fabAddWorkout;
    private DatabaseHelper dbHelper;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        dbHelper = DatabaseHelper.getInstance(getContext());

        // Initialize views
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvNoWorkouts = view.findViewById(R.id.tv_no_workouts);
        recyclerView = view.findViewById(R.id.recycler_workouts);
        fabAddWorkout = view.findViewById(R.id.fab_add_workout);

        // Set welcome message
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String username = email != null ? email.split("@")[0] : "User";
            tvWelcome.setText("Welcome, " + username + "!");
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkoutAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // Setup swipe to delete and mark complete
        setupSwipeActions();

        // FAB click listener
        fabAddWorkout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Create_Workout.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkouts();
    }

    private void loadWorkouts() {
        List<Workout> workouts = dbHelper.getAllWorkouts();

        if (workouts.isEmpty()) {
            tvNoWorkouts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoWorkouts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setWorkouts(workouts);
        }
    }

    private void setupSwipeActions() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Workout workout = adapter.getWorkoutAt(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Delete workout
                    dbHelper.deleteWorkout(workout.getId());
                    adapter.removeWorkout(position);

                    Snackbar.make(recyclerView, "Workout deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> {
                                long id = dbHelper.insertWorkout(workout);
                                workout.setId(id);
                                adapter.addWorkout(position, workout);
                            }).show();

                    loadWorkouts();

                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Mark as complete/incomplete
                    boolean newStatus = !workout.isCompleted();
                    workout.setCompleted(newStatus);
                    dbHelper.markWorkoutCompleted(workout.getId(), newStatus);
                    adapter.notifyItemChanged(position);

                    String message = newStatus ? "Marked as completed!" : "Marked as incomplete";
                    Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
                }
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }
}