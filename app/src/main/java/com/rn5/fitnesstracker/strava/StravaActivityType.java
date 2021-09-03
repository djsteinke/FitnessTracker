package com.rn5.fitnesstracker.strava;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

public enum StravaActivityType {
    @SerializedName("run")
    RUN("run"),
    @SerializedName("walk")
    WALK("walk"),
    @SerializedName("ride")
    RIDE("ride");

    private final String stringValue;

    StravaActivityType(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    @NonNull
    public String toString() {
        return this.stringValue;
    }
}
