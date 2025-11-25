package com.example.fitlife.Model;

import java.io.Serializable;

public class Workout implements Serializable {

    private long id;
    private String name;
    private String description;
    private String equipment;
    private int duration;
    private String imagePath;
    private boolean isCompleted;
    private long createdAt;

    // Default constructor
    public Workout() {
        this.createdAt = System.currentTimeMillis();
        this.isCompleted = false;
    }

    // Full constructor
    public Workout(long id, String name, String description, String equipment,
                   int duration, String imagePath, boolean isCompleted, long createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.equipment = equipment;
        this.duration = duration;
        this.imagePath = imagePath;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }

    // Constructor without ID (for new workouts)
    public Workout(String name, String description, String equipment, int duration) {
        this.name = name;
        this.description = description;
        this.equipment = equipment;
        this.duration = duration;
        this.isCompleted = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEquipment() {
        return equipment;
    }

    public int getDuration() {
        return duration;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

