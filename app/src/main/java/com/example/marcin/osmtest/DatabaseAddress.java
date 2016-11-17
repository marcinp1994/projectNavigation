package com.example.marcin.osmtest;

/**
 * Created by Marcin on 16.11.2016.
 */

public class DatabaseAddress {
    private long id;
    private String address;
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return address;
    }
}