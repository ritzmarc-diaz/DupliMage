package com.example.duplimage;

public class Item {
    String name, date_created, match_result;
    int image;

    public Item(String name, String date_created, String match_result, int image) {
        this.name = name;
        this.date_created = date_created;
        this.match_result = match_result;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getMatch_result() {
        return match_result;
    }

    public void setMatch_result(String match_result) {
        this.match_result = match_result;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
