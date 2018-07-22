package com.uber.test.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by dell on 7/22/2018.
 */
public class Photos {

    @SerializedName("photos")
    public PhotoData photos;

    public PhotoData getPhotos() {
        return photos;
    }
}
