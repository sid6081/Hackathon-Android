package com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration;

import com.example.siddharthkarandikar.hackathon_june.APIHelper.Map.MapResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by siddharth.karandikar on 02/06/18.
 */

public class RegistrationResponseMain {

    @Expose
    @SerializedName("status")
    public String status;

    @Expose
    @SerializedName("message")
    public String message;

    @Expose
    @SerializedName("response")
    public RegistrationResponse registrationResponse;

    public RegistrationResponse getRegistrationResponse() {
        return registrationResponse;
    }

    public void setRegistrationResponse(RegistrationResponse registrationResponse) {
        this.registrationResponse = registrationResponse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
