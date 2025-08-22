package com.s23010675.achievo;

public class LocationModel {
    private String id;   // Firestore document ID
    private String name; // Location name
    private Double latitude; // Latitude
    private Double longitude; // Longitude

    public LocationModel() {
    }

    public LocationModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public LocationModel(String locationName, Double latitude, Double longitude) {
        this.name = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

     public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
