package com.rn5.libstrava.common.api;

import com.rn5.libstrava.common.model.StravaResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import com.rn5.libstrava.exception.StravaAPIException;
import com.rn5.libstrava.exception.StravaUnauthorizedException;

public abstract class StravaAPI {
    private static final int UNAUTHORIZED_CODE = 401;
    private final Config config;

    public StravaAPI(Config config) {
        this.config = config;
    }

    protected <T> T getAPI(Class<T> apiRest) {
        return config.getRetrofit().create(apiRest);
    }

    public <T> T execute(Call<T> call) throws StravaAPIException, StravaUnauthorizedException {
        Response<T> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new StravaAPIException("Error contacting Strava API", e);
        }

        if(response.isSuccessful()) {
            return response.body();
        } else if(response.code() == UNAUTHORIZED_CODE){
            throw new StravaUnauthorizedException();
        } else {
            throw new StravaAPIException("Call was not successful. Code[" + response.code() + "]");
        }
    }

    public <T> StravaResponse<T> executeWithStravaResponse(Call<T> call) throws StravaAPIException, StravaUnauthorizedException {
        Response<T> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new StravaAPIException("Error contacting Strava API", e);
        }

        if(response.isSuccessful()) {
            return new StravaResponse<>(response.body(),response.code());
        } else if(response.code() == UNAUTHORIZED_CODE){
            throw new StravaUnauthorizedException();
        } else {
            throw new StravaAPIException("Call was not successful. Code[" + response.code() + "]");
        }
    }
}
