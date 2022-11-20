package com.example.treecheck.Models;

import com.google.gson.annotations.SerializedName;

public class Province_response {


    public Province_response(int id, int country_id, String name, String latitude, String longitude) {
        this.id = id;
        this.country_id = country_id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @SerializedName("id")
    private int id;
    @SerializedName("country")
    private int country_id;
    @SerializedName("name")
    private String name;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        return name;
    }
}
