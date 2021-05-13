package com.rn5.libstrava.upload.rest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import com.rn5.libstrava.upload.model.UploadStatus;

public interface UploadRest {

    @POST("uploads") @Multipart
    Call<UploadStatus> upload(
            @Part("activity_type") RequestBody activityType,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("private") Integer isPrivate,
            @Part("trainer") Integer hasTrainer,
            @Part("commute") Integer isCommute,
            @Part("data_type") RequestBody dataType,
            @Part("external_id") RequestBody externalID,
            @Part MultipartBody.Part file);

    @GET("uploads/{id}")
    Call<UploadStatus> checkUploadStatus(@Path("id") long id);
}
