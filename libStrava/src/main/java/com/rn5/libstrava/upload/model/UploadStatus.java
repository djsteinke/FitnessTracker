package com.rn5.libstrava.upload.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class UploadStatus {

    @SerializedName("activity_id") Long activityId;
    @SerializedName("external_id") String externalId;
    @SerializedName("id") long id;
    @SerializedName("error") String error;
    @SerializedName("status") String status;

    public UploadStatus() {}
}
