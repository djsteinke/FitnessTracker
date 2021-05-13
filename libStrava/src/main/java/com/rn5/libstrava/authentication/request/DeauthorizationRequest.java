package com.rn5.libstrava.authentication.request;

import com.rn5.libstrava.authentication.rest.AuthRest;
import com.rn5.libstrava.authentication.api.AuthenticationAPI;
import com.rn5.libstrava.authentication.model.AuthenticationResponse;

import retrofit2.Call;

public class DeauthorizationRequest {

    private final AuthRest restService;
    private final AuthenticationAPI api;



    public DeauthorizationRequest(AuthRest restService, AuthenticationAPI api) {
        this.restService = restService;
        this.api = api;
    }

    public AuthenticationResponse execute() {
        Call<AuthenticationResponse> call = restService.deauthorize();
        return api.execute(call);
    }
}
