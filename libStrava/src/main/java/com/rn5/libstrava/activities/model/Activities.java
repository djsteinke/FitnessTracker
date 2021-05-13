package com.rn5.libstrava.activities.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class Activities {

    private List<Activity> activities;

    public Activities() {}
}
