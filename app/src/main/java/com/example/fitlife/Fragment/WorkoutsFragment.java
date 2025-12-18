package com.example.fitlife.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.fitlife.Adapter.WorkoutAdapter;
import com.example.fitlife.Database.DatabaseHelper;
import com.example.fitlife.Model.Workout;
import com.example.fitlife.R;

import java.util.List;

public class WorkoutsFragment extends Fragment {

    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private LinearLayout emptyStateLayout;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workouts, container, false);

        dbHelper = DatabaseHelper.getInstance(getContext());

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_all_workouts);
        emptyStateLayout = view.findViewById(R.id.layout_empty_state);

        // Setup RecyclerView with Grid Layout
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new WorkoutAdapter(getContext());
        recyclerView.setAdapter(adapter);

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
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setWorkouts(workouts);
        }
    }
}