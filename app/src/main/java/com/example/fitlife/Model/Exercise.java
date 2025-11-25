package com.example.fitlife.Model;

import java.io.Serializable;

public class Exercise implements Serializable {

    private long id;
    private long workoutId;
    private String name;
    private int sets;
    private int reps;
    private String instructions;
    private boolean isCompleted;

    // Default constructor
    public Exercise() {
        this.isCompleted = false;
    }

    // Full constructor
    public Exercise(long id, long workoutId, String name, int sets, int reps,
                    String instructions, boolean isCompleted) {
        this.id = id;
        this.workoutId = workoutId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.instructions = instructions;
        this.isCompleted = isCompleted;
    }

    // Constructor without ID (for new exercises)
    public Exercise(long workoutId, String name, int sets, int reps, String instructions) {
        this.workoutId = workoutId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.instructions = instructions;
        this.isCompleted = false;
    }

    // Getters
    public long getId() {
        return id;
    }

    public long getWorkoutId() {
        return workoutId;
    }

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public String getInstructions() {
        return instructions;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setWorkoutId(long workoutId) {
        this.workoutId = workoutId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
