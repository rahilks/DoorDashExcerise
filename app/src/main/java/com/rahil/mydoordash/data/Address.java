package com.rahil.mydoordash.data;

import com.google.gson.annotations.SerializedName;

public class Address {

    @SerializedName("printable_address")
    private String printableAddress;

    public String getPrintableAddress() {
        return printableAddress;
    }
}
