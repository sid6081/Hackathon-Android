package com.example.siddharthkarandikar.hackathon_june.APIHelper.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by siddharth.karandikar on 01/06/18.
 */

public class MapResponseMainGet {

    @Expose
    @SerializedName("response")
    public List<MapResponse> mapResponse;

    public List<MapResponse> getEventResponse() {
        return mapResponse;
    }

    public void setEventResponse(List<MapResponse> mapResponse) {
        this.mapResponse = mapResponse;
    }
}
