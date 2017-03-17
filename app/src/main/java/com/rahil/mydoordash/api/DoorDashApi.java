package com.rahil.mydoordash.api;

import com.rahil.mydoordash.data.Restaurant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoorDashApi {

    @GET("v2/restaurant")
    Call<List<Restaurant>> getRestaurantListByLocation(@Query("lat") double latitude, @Query("lng") double longitude);
}
