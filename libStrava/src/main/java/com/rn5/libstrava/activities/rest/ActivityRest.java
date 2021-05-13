package com.rn5.libstrava.activities.rest;

import com.rn5.libstrava.activities.model.Activities;
import com.rn5.libstrava.activities.model.Activity;
import com.rn5.libstrava.gear.model.Gear;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ActivityRest {
    @GET("athlete/activities")
    Call<List<Activity>> getActivities(
            @Header("Authorization") String token,
            @Query("after") Long after,
            @Query("before") Long before,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage);
}
