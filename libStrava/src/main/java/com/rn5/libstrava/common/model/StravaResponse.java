package com.rn5.libstrava.common.model;

import lombok.Data;

@Data
public class StravaResponse<B> {
    private B response;
    private int code;

    public StravaResponse(B response, int code) {
        this.response = response;
        this.code = code;
    }
}
