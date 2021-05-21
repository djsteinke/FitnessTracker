package com.rn5.libstrava.activities.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Activity implements Comparable<Activity> {

    @SerializedName("name") private String name;
    @SerializedName("id") private Long id;
    @SerializedName("upload_id") private Long uploadId;
    @SerializedName("device_watts") private Boolean deviceWatts;
    @SerializedName("has_heartrate") private Boolean hasHeartrate;
    @SerializedName("start_date_local") private String startDate;
    @SerializedName("moving_time") private Integer movingTime;

    public Activity() {}

    @Override
    public int compareTo(Activity o) {
        return this.getId().compareTo(o.getId());
    }
}
