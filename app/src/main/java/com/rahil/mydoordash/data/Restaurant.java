package com.rahil.mydoordash.data;

import com.google.gson.annotations.SerializedName;

public class Restaurant {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String foodType;

    @SerializedName("address")
    private Address address;

    private boolean fav;

    public Address getAddress() {
        return address;
    }

    public boolean getFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getFoodType() {
        return foodType;
    }

    public String getName() {
        return name;
    }
}
