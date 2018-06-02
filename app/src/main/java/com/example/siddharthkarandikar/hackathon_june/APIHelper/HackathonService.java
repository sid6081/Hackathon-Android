package com.example.siddharthkarandikar.hackathon_june.APIHelper;


import com.example.siddharthkarandikar.hackathon_june.APIHelper.Map.MapResponseMain;

import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Created by siddharth.karandikar on 01/06/18.
 */

public interface HackathonService {
    @GET("map")
    io.reactivex.Observable<Response<MapResponseMain>> getMapDatPoints();
}