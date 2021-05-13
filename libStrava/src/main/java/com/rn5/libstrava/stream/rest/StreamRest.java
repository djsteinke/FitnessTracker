package com.rn5.libstrava.stream.rest;

import com.rn5.libstrava.stream.model.Stream;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StreamRest {
    @GET("activities/{id}/streams")
    Call<Stream> getStreams(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Query("keys") String keys,
            @Query("key_by_type") Boolean keyByType);
}
