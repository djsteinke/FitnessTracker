package com.rn5.libstrava.stream.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class Stream {

    //@SerializedName("type") private String type;
    @SerializedName("distance") private Distance distance;
    @SerializedName("time") private Time time;
    @SerializedName("watts") private Watts watts;
    @SerializedName("heartrate") private Heartrate heartrate;
    @SerializedName("moving") private Moving moving;

    public Stream() {}

    @Data
    public class Distance {
        @SerializedName("data") private List<Double> data;
        @SerializedName("original_size") private Long originalSize;
        public Distance() {}
    }
    @Data
    public class Time {
        @SerializedName("data") private List<Double> data;
        @SerializedName("original_size") private Long originalSize;
        public Time() {}
    }
    @Data
    public class Watts {
        @SerializedName("data") private List<Double> data;
        @SerializedName("original_size") private Long originalSize;
        public Watts() {}
    }
    @Data
    public class Heartrate {
        @SerializedName("data") private List<Double> data;
        @SerializedName("original_size") private Long originalSize;
        public Heartrate() {}
    }
    @Data
    public class Moving {
        @SerializedName("data") private List<Boolean> data;
        @SerializedName("original_size") private Long originalSize;
        public Moving() {}
    }
}
