package ru.sigmadigital.pantus.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class ItemTextResponse implements Serializable {
    private long id;
    private String imgLink;
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
