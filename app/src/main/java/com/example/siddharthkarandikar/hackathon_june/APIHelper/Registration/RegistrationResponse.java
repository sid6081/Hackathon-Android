package com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by siddharth.karandikar on 02/06/18.
 */

public class RegistrationResponse {

    @Expose
    @SerializedName("firstnameEditText")
    public String firstname;

    @Expose
    @SerializedName("lastnameEditText")
    public String lastname;

    @Expose
    @SerializedName("emailidEditText")
    public String emailid;

    @Expose
    @SerializedName("passwordEditText")
    public String password;

    @Expose
    @SerializedName("phoneEditText")
    public String phone;

    @Expose
    @SerializedName("emergencyContactEditText")
    public List<String> emergencyContact;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmailid() {
        return emailid;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getEmergencyContact() {
        return emergencyContact;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmergencyContact(List<String> emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
