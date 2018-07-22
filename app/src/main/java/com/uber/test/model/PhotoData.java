package com.uber.test.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by dell on 7/22/2018.
 */
public class PhotoData {

    @SerializedName("page")
    public int page;

    @SerializedName("pages")
    public int pages;

    @SerializedName("perpage")
    public int perpage;

    @SerializedName("total")
    public int total;

    @SerializedName("photo")
    public ArrayList<PhotoItemData> results;

    public int getPage() {
        return page;
    }

    public int getPages() {
        return pages;
    }

    public int getPerpage() {
        return perpage;
    }

    public int getTotal() {
        return total;
    }

    public ArrayList<PhotoItemData> getResults() {
        return results;
    }
}
