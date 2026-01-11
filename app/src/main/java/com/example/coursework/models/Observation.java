package com.example.coursework.models;

/**
 * Model class representing an observation made during a hike
 */
public class Observation {
    private long id;
    private long hikeId;
    private String observation;
    private String time;
    private String comments;

    /**
     * Default constructor
     */
    public Observation() {
    }

    /**
     * Constructor with all fields
     */
    public Observation(long id, long hikeId, String observation, String time, String comments) {
        this.id = id;
        this.hikeId = hikeId;
        this.observation = observation;
        this.time = time;
        this.comments = comments;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHikeId() {
        return hikeId;
    }

    public void setHikeId(long hikeId) {
        this.hikeId = hikeId;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Observation{" +
                "id=" + id +
                ", hikeId=" + hikeId +
                ", observation='" + observation + '\'' +
                ", time='" + time + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
