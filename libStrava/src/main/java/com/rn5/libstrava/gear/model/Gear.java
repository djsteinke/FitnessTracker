package com.rn5.libstrava.gear.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gear {

    @SerializedName("id") private String id;
    @SerializedName("primary") private boolean primary;
    @SerializedName("resource_state") private int resourceState;
    @SerializedName("distance") private int distance;
    @SerializedName("brand_name") private String brandName;
    @SerializedName("model_name") private String modelName;
    @SerializedName("frame_type") private int frameType;
    @SerializedName("description") private String desciption;

    public Gear() {}
}
