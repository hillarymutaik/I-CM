package com.musicind.dukamoja.response;


import com.musicind.dukamoja.models.CommentModel;
import com.musicind.dukamoja.models.ProfileModel;
import com.squareup.moshi.Json;
import java.util.List;

public class ProfileResponse {

    @Json(name = "profile")
    List<ProfileModel> profile;

    public List<ProfileModel> getUserProfile() {
        return profile;
    }
}

