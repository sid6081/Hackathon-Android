package com.example.siddharthkarandikar.hackathon_june.APIHelper.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by siddharth.karandikar on 02/06/18.
 */

public class MapBody {

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("latitude")
    public String latitude;

    @Expose
    @SerializedName("longitude")
    public String longitude;

    @Expose
    @SerializedName("safetyRating")
    public String safetyRating;

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getSafetyRating() {
        return safetyRating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setSafetyRating(String safetyRating) {
        this.safetyRating = safetyRating;
    }
}
