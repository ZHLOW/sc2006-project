package com.example.meetpoint;

import java.util.ArrayList;

public class User {

    public String fullName, username, email, mobileNumber,user_id;
    public ArrayList<String> locations;
    public String profilePhotoUrl;

    public User(){

    }

    public User(String fullName, String username, String email, String mobileNumber,String user_id, ArrayList<String> locations){

        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.user_id = user_id;
        this.locations = locations;

    }
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }


    public String getFullName() {
        return fullName;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }

    public void setFullName(String fullName) { this.fullName = fullName; }



    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}


