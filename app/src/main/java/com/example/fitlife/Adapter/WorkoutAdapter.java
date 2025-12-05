package com.example.fitlife.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlife.Model.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder{
    private Context context;
    private List<Workout> workoutList;
    private OnWorkoutClickListener listener;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
        void onEditClick(Workout workout);
        void onDeleteClick(Workout workout);
        void onCompleteClick(Workout workout);
    }
    public WorkoutAdapter(Context context, List<Workout> workoutList) {
        this.context = context;
        this.workoutList = workoutList != null ? workoutList : new ArrayList<>();
    }
    public WorkoutAdapter(Context context, List<Workout> workoutList, OnWorkoutClickListener listener) {
        this.context = context;
        this.workoutList = workoutList != null ? workoutList : new ArrayList<>();
        this.listener = listener;
    }
    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }
}
