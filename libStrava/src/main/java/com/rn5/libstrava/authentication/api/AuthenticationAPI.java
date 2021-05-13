package com.rn5.libstrava.authentication.api;

import com.rn5.libstrava.authentication.rest.AuthRest;
import com.rn5.libstrava.authentication.request.AuthenticationRequest;
import com.rn5.libstrava.authentication.request.DeauthorizationRequest;
import com.rn5.libstrava.authentication.request.RefreshTokenRequest;

import com.rn5.libstrava.common.api.StravaAPI;
import com.rn5.libstrava.common.api.StravaConfig;

public class AuthenticationAPI extends StravaAPI {

    public AuthenticationAPI(StravaConfig config) {
        super(config);
    }

    public AuthenticationRequest getToken() {
        return new AuthenticationRequest(getAPI(AuthRest.class), this);
    }

    public RefreshTokenRequest refreshToken() {
        return new RefreshTokenRequest(getAPI(AuthRest.class), this);
    }

    public DeauthorizationRequest deauthorize() {
        return new DeauthorizationRequest(getAPI(AuthRest.class), this);
    }
}
