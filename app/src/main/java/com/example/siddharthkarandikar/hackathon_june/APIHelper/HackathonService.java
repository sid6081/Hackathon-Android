package com.example.siddharthkarandikar.hackathon_june.APIHelper;


import com.example.siddharthkarandikar.hackathon_june.APIHelper.Map.MapResponseMain;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration.RegistrationBody;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by siddharth.karandikar on 01/06/18.
 */

public interface HackathonService {
    @GET("map")
    io.reactivex.Observable<Response<MapResponseMain>> getMapDatPoints();

    @POST("register")
    io.reactivex.Observable<Response<MapResponseMain>> registration(@Body RegistrationBody registrationBody);
}