package com.rn5.libstrava.common.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Athlete {
    @SerializedName("username") private String username;
    @SerializedName("firstname") private String firstName;
    @SerializedName("lastname") private String lastName;

    public Athlete() {}
}
