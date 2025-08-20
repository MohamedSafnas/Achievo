package com.s23010675.achievo;

public class LocationModel {
    private String id;   // Firestore document ID
    private String name; // Location name

    public LocationModel() {
    }

    public LocationModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public LocationModel(String locationName, double latitude, double longitude) {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
