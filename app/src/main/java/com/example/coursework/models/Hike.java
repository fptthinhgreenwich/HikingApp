package com.example.coursework.models;

import java.io.Serializable;

/**
 * Model class representing a hiking record
 */
public class Hike implements Serializable {
    private long id;
    private String name;
    private String location;
    private String date;
    private String parkingAvailable;
    private double length;
    private String difficulty;
    private String description;
    private String weatherCondition;
    private String estimatedDuration;
    private String createdAt;

    /**
     * Constructor for creating a new hike
     */
    public Hike() {
    }

    /**
     * Constructor with all fields
     */
    public Hike(long id, String name, String location, String date, String parkingAvailable,
                double length, String difficulty, String description, String weatherCondition,
                String estimatedDuration, String createdAt) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
        this.weatherCondition = weatherCondition;
        this.estimatedDuration = estimatedDuration;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(String parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Hike{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", parkingAvailable='" + parkingAvailable + '\'' +
                ", length=" + length +
                ", difficulty='" + difficulty + '\'' +
                ", description='" + description + '\'' +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", estimatedDuration='" + estimatedDuration + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
