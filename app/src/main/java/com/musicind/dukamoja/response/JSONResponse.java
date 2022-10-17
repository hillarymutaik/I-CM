package com.musicind.dukamoja.response;

import com.musicind.dukamoja.models.MediaItem;
import com.squareup.moshi.Json;

public class JSONResponse {
    @Json(name = "pojo")
    private MediaItem[] android;

    public MediaItem[] getAndroid() {
        return android;
    }
}
