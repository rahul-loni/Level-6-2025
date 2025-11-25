package com.example.fitlife.Model;

import java.io.Serializable;

public class WorkoutLocation implements Serializable {

    private long id;
    private long workoutId;
    private String name;
    private double latitude;
    private double longitude;
    private String address;

    // Default constructor
    public WorkoutLocation() {
    }

    // Full constructor
    public WorkoutLocation(long id, long workoutId, String name,
                           double latitude, double longitude, String address) {
        this.id = id;
        this.workoutId = workoutId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    // Constructor without ID (for new locations)
    public WorkoutLocation(long workoutId, String name, double latitude,
                           double longitude, String address) {
        this.workoutId = workoutId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    // Constructor with coordinates only
    public WorkoutLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
