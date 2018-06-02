package com.example.siddharthkarandikar.hackathon_june.APIHelper;


import com.example.siddharthkarandikar.hackathon_june.APIHelper.Login.LoginBody;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Login.LoginResponseMain;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Map.MapResponseMain;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration.RegistrationBody;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration.RegistrationResponseMain;

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
    io.reactivex.Observable<Response<RegistrationResponseMain>> registration(@Body RegistrationBody registrationBody);

    @POST("login")
    io.reactivex.Observable<Response<LoginResponseMain>> login(@Body LoginBody loginBody);
}