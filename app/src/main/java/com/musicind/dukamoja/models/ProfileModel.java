package com.musicind.dukamoja.models;


public class ProfileModel {

    String name, email, phone, description, location, profile;

    public ProfileModel(String userName, String userLoc, String userDesc, String userEmail, String userPhone, String userProfile) {
        this.name = userName;
        this.email = userEmail;
        this.phone = userPhone;
        this.description = userDesc;
        this.location = userLoc;
        this.profile = userProfile;
    }

    public String getUserName() {
        return name;
    }

    public void setUserName(String userName) {
        this.email = userName;
    }

    public String getUserLoc() {
        return location;
    }

    public void setUserLoc(String userLoc) {
        this.location = userLoc;
    }

    public String getUserDesc() {
        return description;
    }

    public void setUserDesc(String userDesc) {
        this.description = userDesc;
    }

    public String getUserEmail() {
        return email;
    }

    public void setUserEmail(String userEmail) {
        this.email = userEmail;
    }

    public String getUserPhone() {
        return phone;
    }

    public void setUserPhone(String userPhone) {
        this.phone = userPhone;
    }

    public String getProfile() {
        return profile;
    }
}
