package com.example.siddharthkarandikar.hackathon_june.APIHelper.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by siddharth.karandikar on 02/06/18.
 */

public class LoginBody {

    @Expose
    @SerializedName("emailid")
    public String emailid;

    @Expose
    @SerializedName("password")
    public String password;

    public String getEmailId() {
        return emailid;
    }

    public String getPassword() {
        return password;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
