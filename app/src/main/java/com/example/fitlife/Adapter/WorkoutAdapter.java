package com.example.fitlife.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.Edit_Workout;
import com.example.fitlife.Model.Workout;
import com.example.fitlife.R;
import com.example.fitlife.Workout_Details;

import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private Context context;
    private List<Workout> workouts;

    public WorkoutAdapter(Context context) {
        this.context = context;
        this.workouts = new ArrayList<>();
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    public Workout getWorkoutAt(int position) {
        return workouts.get(position);
    }

    public void removeWorkout(int position) {
        workouts.remove(position);
        notifyItemRemoved(position);
    }

    public void addWorkout(int position, Workout workout) {
        workouts.add(position, workout);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);

        holder.tvWorkoutName.setText(workout.getName());
        holder.tvWorkoutDescription.setText(workout.getDescription());
        holder.tvWorkoutEquipment.setText("Equipment: " + workout.getEquipment());
        holder.tvWorkoutDuration.setText(workout.getDuration() + " min");

        // Show checkmark if completed
        if (workout.isCompleted()) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.GONE);
        }

        // Click listener to view details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Workout_Details.class);
            intent.putExtra("workout_id", workout.getId());
            context.startActivity(intent);
        });

        // Long click listener to show options
        holder.itemView.setOnLongClickListener(v -> {
            showOptionsDialog(workout, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    private void showOptionsDialog(Workout workout, int position) {
        String[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(context)
                .setTitle("Workout Options")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Edit
                        Intent intent = new Intent(context, Edit_Workout.class);
                        intent.putExtra("workout_id", workout.getId());
                        context.startActivity(intent);
                    } else if (which == 1) {
                        // Delete confirmation
                        showDeleteConfirmation(workout, position);
                    }
                })
                .show();
    }

    private void showDeleteConfirmation(Workout workout, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete \"" + workout.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // The actual deletion is handled by swipe in the Fragment
                    // This is just for long-press delete
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkoutName, tvWorkoutDescription, tvWorkoutEquipment, tvWorkoutDuration;
        ImageView ivCheck;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkoutName = itemView.findViewById(R.id.tv_workout_name);
            tvWorkoutDescription = itemView.findViewById(R.id.tv_workout_description);
            tvWorkoutEquipment = itemView.findViewById(R.id.tv_workout_equipment);
            tvWorkoutDuration = itemView.findViewById(R.id.tv_workout_duration);
            ivCheck = itemView.findViewById(R.id.iv_check);
        }
    }
}