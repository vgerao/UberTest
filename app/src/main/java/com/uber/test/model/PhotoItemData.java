package com.uber.test.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dell on 7/22/2018.
 */
public class PhotoItemData {

    @SerializedName("id")
    public String id;

    @SerializedName("owner")
    public String owner;

    @SerializedName("secret")
    public String secret;

    @SerializedName("server")
    public String server;

    @SerializedName("farm")
    public int farm;

    @SerializedName("title")
    public String title;

    @SerializedName("ispublic")
    public int ispublic;

    @SerializedName("isfriend")
    public int isfriend;

    @SerializedName("isfamily")
    public int isfamily;

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public int getFarm() {
        return farm;
    }

    public String getTitle() {
        return title;
    }

    public int getIspublic() {
        return ispublic;
    }

    public int getIsfriend() {
        return isfriend;
    }

    public int getIsfamily() {
        return isfamily;
    }
}
