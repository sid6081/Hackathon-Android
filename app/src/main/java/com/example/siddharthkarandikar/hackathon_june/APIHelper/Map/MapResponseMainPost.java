package com.example.siddharthkarandikar.hackathon_june.APIHelper.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by siddharth.karandikar on 02/06/18.
 */

public class MapResponseMainPost {

    @Expose
    @SerializedName("status")
    public String status;

    public String status() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
