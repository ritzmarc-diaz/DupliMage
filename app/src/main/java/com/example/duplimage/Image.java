package com.example.duplimage;

import android.graphics.Bitmap;

public class Image {
    private String path;
    private Bitmap thumbnail;
    private boolean isDuplicate;

    public Image(String path, Bitmap thumbnail) {
        this.path = path;
        this.thumbnail = thumbnail;
        this.isDuplicate = false;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }
}
