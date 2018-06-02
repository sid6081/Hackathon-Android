package com.example.siddharthkarandikar.hackathon_june.APIHelper.Login;

import com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration.RegistrationResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by siddharth.karandikar on 02/06/18.
 */

public class LoginResponseMain {

    @Expose
    @SerializedName("status")
    public String status;

    @Expose
    @SerializedName("message")
    public String message;

    @Expose
    @SerializedName("response")
    public RegistrationResponse registrationResponse;

    public RegistrationResponse getLoginResponse() {
        return registrationResponse;
    }

    public void setLoginResponse(RegistrationResponse registrationResponse) {
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
