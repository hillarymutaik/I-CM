package com.musicind.dukamoja.response;

import com.musicind.dukamoja.models.CommentModel;
import com.squareup.moshi.Json;
import java.util.List;

public class CommentResponse {

    @Json(name = "comments")
    List<CommentModel> data;

    public List<CommentModel> getComments() {
    return data;
    }
}
