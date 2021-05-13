package com.rn5.libstrava.authentication.model;

public enum AuthenticationType {
    AUTHENTICATE,
    REFRESH_TOKEN,
    DEAUTHORIZE;

    AuthenticationType() {}
}
